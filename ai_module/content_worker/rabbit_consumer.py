"""
RabbitMQ consumer for content generation pipeline.
"""

from __future__ import annotations

import json
import ssl
import threading
import time

import pika
from pika import exceptions as pika_exceptions
from pika.adapters.blocking_connection import BlockingChannel

from .config import load_rabbit_settings
from .logger import logger
from .models import ContentGenerateRequest
from .pipeline_executor import execute_pipeline


class ContentWorkerConsumer:
    """
    RabbitMQ consumer for content generation requests.
    """

    def __init__(self) -> None:
        self.settings = load_rabbit_settings()
        self._connection: pika.BlockingConnection | None = None
        self._channel: BlockingChannel | None = None
        self._lock = threading.Lock()

    def connect(self) -> None:
        """Establish RabbitMQ connection."""
        with self._lock:
            if self._connection and self._connection.is_open:
                return

            credentials = pika.PlainCredentials(
                self.settings.username,
                self.settings.password,
            )

            # SSL 설정
            ssl_options = None
            if self.settings.use_ssl:
                context = ssl.create_default_context()
                ssl_options = pika.SSLOptions(context)

            parameters = pika.ConnectionParameters(
                host=self.settings.host,
                port=self.settings.port,
                credentials=credentials,
                heartbeat=600,  # ✅ 긴 작업 대응
                blocked_connection_timeout=300,
                ssl_options=ssl_options,
            )

            logger.info(
                "RabbitMQ 연결 시도 host=%s port=%s queue=%s",
                self.settings.host,
                self.settings.port,
                self.settings.queue,
            )

            self._connection = pika.BlockingConnection(parameters)
            self._channel = self._connection.channel()
            self._channel.basic_qos(prefetch_count=self.settings.prefetch)
            self._channel.queue_declare(queue=self.settings.queue, durable=True)

            logger.info("RabbitMQ 연결 성공")

    def start(self) -> None:
        """Start consuming messages."""
        self.connect()
        assert self._channel is not None

        self._channel.basic_consume(
            queue=self.settings.queue,
            on_message_callback=self._on_message,
        )

        logger.info("RabbitMQ 소비 시작 (queue=%s)", self.settings.queue)

        try:
            self._channel.start_consuming()
        finally:
            self.stop()

    def stop(self) -> None:
        """Stop consuming and close connections."""
        if self._channel and self._channel.is_open:
            self._channel.close()
        if self._connection and self._connection.is_open:
            self._connection.close()

        logger.info("RabbitMQ 연결 종료")

    def _on_message(
            self,
            ch: BlockingChannel,
            method,
            properties,
            body: bytes,
    ) -> None:
        """Message callback handler."""
        logger.info("메시지 수신: delivery_tag=%s", method.delivery_tag)

        try:
            # 1️⃣ 메시지 파싱
            request = ContentGenerateRequest.from_json(body)
            logger.info(
                "콘텐츠 생성 요청 파싱 완료 - workId=%s",
                request.work_id,
            )

            # 2️⃣ 파이프라인 실행
            execute_pipeline(request)

            # 3️⃣ 성공 시 ACK (채널 살아있을 때만)
            if ch.is_open:
                ch.basic_ack(delivery_tag=method.delivery_tag)

            logger.info(
                "메시지 처리 완료 - workId=%s",
                request.work_id,
            )

        except json.JSONDecodeError as exc:
            # ❌ 잘못된 메시지 → 재처리 의미 없음
            logger.error("메시지 JSON 파싱 실패: %s", exc)

            if ch.is_open:
                ch.basic_nack(
                    delivery_tag=method.delivery_tag,
                    requeue=False,
                )

        except (
                pika_exceptions.StreamLostError,
                pika_exceptions.AMQPConnectionError,
        ):
            # ❗ 연결이 끊긴 경우
            # → ACK/NACK 하면 안 됨
            logger.error(
                "RabbitMQ 연결 손실 감지 - ACK/NACK 없이 consumer 재시작"
            )
            raise

        except Exception as exc:
            # ❌ 처리 중 오류
            logger.exception("콘텐츠 생성 처리 중 오류: %s", exc)

            if ch.is_open:
                ch.basic_nack(
                    delivery_tag=method.delivery_tag,
                    requeue=False,
                )


def run_consumer() -> None:
    """
    Run content worker consumer with auto-reconnect.
    """
    consumer = ContentWorkerConsumer()
    retry_delay = 5

    logger.info("Content Worker 시작")

    while True:
        try:
            consumer.start()

        except KeyboardInterrupt:
            logger.info("워커 종료 요청을 감지했습니다. 종료합니다.")
            break

        except (
                pika_exceptions.StreamLostError,
                pika_exceptions.AMQPConnectionError,
                pika_exceptions.ChannelClosedByBroker,
                pika_exceptions.ConnectionClosedByBroker,
        ) as exc:
            logger.warning(
                "RabbitMQ 연결이 끊어졌습니다. %s초 후 재연결 시도합니다. detail=%s",
                retry_delay,
                exc,
            )
            time.sleep(retry_delay)

        except Exception:
            logger.exception(
                "예상치 못한 오류로 워커가 중단되었습니다. %s초 후 재시작합니다.",
                retry_delay,
            )
            time.sleep(retry_delay)

        else:
            # 정상 종료 시 루프 탈출
            break

    logger.info("Content Worker 종료")
