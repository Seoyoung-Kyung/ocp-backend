"""
Content generation pipeline executor.

Migrated from Airflow DAG (trend_pipeline.py) to standalone Python worker.
"""
from __future__ import annotations

import os
import time
import uuid
from datetime import datetime, timezone
from typing import Any, Dict, List, Optional

import requests

from keyword_crawler import crawl_keywords
from keyword_selector import select_best_keyword
from product_selector import select_best_product
from product_finder import ProductFinder
from content_generator import generate_blog_content

from .logger import logger
from .models import ContentGenerateRequest


WEBHOOK_HEADER = "X-WEBHOOK-SECRET"


class PipelineLogger:
    """
    Replaces Airflow's dagId/runId with executionId + structured logs.
    """

    def __init__(self):
        self.logs: List[Dict[str, Any]] = []
        self.execution_id = str(uuid.uuid4())

    def log_step(
        self,
        step: str,
        status: str,
        message: str = "",
        duration: float = 0
    ) -> None:
        """
        Record a pipeline step log.

        Args:
            step: Step name (e.g., "crawler", "keyword_select")
            status: Status ("started", "completed", "failed")
            message: Optional message
            duration: Duration in seconds
        """
        self.logs.append({
            "step": step,
            "status": status,
            "message": message,
            "durationSeconds": duration,
            "timestamp": datetime.now(timezone.utc).isoformat()
        })

    def send_log_webhook(self, request: ContentGenerateRequest) -> None:
        """
        Send logs to backend (replaces Airflow log collection).

        Args:
            request: Original request containing webhook URLs
        """
        if not request.webhook_urls.airflow_log:
            logger.debug("로그 수집 webhook URL이 없음 - 건너뜀")
            return

        payload = {
            "workId": request.work_id,
            "executionId": self.execution_id,
            "logs": self.logs,
            "completedAt": datetime.now(timezone.utc).isoformat()
        }

        try:
            send_webhook(
                request.webhook_urls.airflow_log,
                payload,
                request.webhook_secret
            )
            logger.info("로그 수집 webhook 전송 완료")
        except Exception as e:
            # Log collection failure should not fail the pipeline
            logger.warning(f"로그 수집 webhook 전송 실패: {e}")


def send_webhook(
    url: str,
    payload: Dict[str, Any],
    secret: str,
    timeout: int = 30
) -> None:
    """
    Send webhook with X-WEBHOOK-SECRET header.

    Args:
        url: Webhook URL
        payload: JSON payload
        secret: Webhook secret for X-WEBHOOK-SECRET header
        timeout: Request timeout in seconds

    Raises:
        Exception: If webhook call fails
    """
    headers = {"Content-Type": "application/json"}
    if secret:
        headers[WEBHOOK_HEADER] = secret

    response = requests.post(url, json=payload, headers=headers, timeout=timeout)
    if not response.ok:
        raise Exception(
            f"Webhook failed: {response.status_code} - {response.text}"
        )


def execute_pipeline(request: ContentGenerateRequest) -> None:
    """
    Main pipeline executor (replaces Airflow DAG).

    Pipeline steps:
    1. Crawl keywords
    2. Select keyword (GPT)
    3. Select product OR find product (branch)
    4. Generate content
    5. Send log collection

    Args:
        request: Content generation request from RabbitMQ

    Raises:
        Exception: If any pipeline step fails
    """
    pipeline_logger = PipelineLogger()
    logger.info(
        f"파이프라인 시작 - workId: {request.work_id}, "
        f"executionId: {pipeline_logger.execution_id}"
    )

    try:
        # Step 1: Crawl keywords
        pipeline_logger.log_step("crawler", "started")
        start_time = time.time()

        keywords = run_crawler(request)

        duration = time.time() - start_time
        pipeline_logger.log_step(
            "crawler",
            "completed",
            f"{len(keywords)} keywords crawled",
            duration
        )

        # Step 2: Select keyword
        pipeline_logger.log_step("keyword_select", "started")
        start_time = time.time()

        keyword_result = select_keyword(request, keywords)
        send_keyword_webhook(request, keyword_result)

        duration = time.time() - start_time
        pipeline_logger.log_step(
            "keyword_select",
            "completed",
            f"Selected: {keyword_result['keyword']}",
            duration
        )

        # Step 3: Branch - select product OR find product
        if request.has_crawled_items:
            pipeline_logger.log_step("select_product", "started")
            start_time = time.time()

            product_result = select_product(request, keyword_result)

            duration = time.time() - start_time
            pipeline_logger.log_step(
                "select_product",
                "completed",
                f"Selected: {product_result.get('name')}",
                duration
            )
        else:
            pipeline_logger.log_step("find_product", "started")
            start_time = time.time()

            product_result = run_product_finder(request, keyword_result)

            duration = time.time() - start_time
            pipeline_logger.log_step(
                "find_product",
                "completed",
                f"Found: {product_result.get('name')}",
                duration
            )

        # Send product webhook
        send_product_webhook(request, product_result)

        # Step 4: Generate content
        pipeline_logger.log_step("generate_content", "started")
        start_time = time.time()

        content = generate_content(request, product_result)
        send_content_webhook(request, content)

        duration = time.time() - start_time
        pipeline_logger.log_step(
            "generate_content",
            "completed",
            f"Title: {content.get('title')}",
            duration
        )

        logger.info(f"파이프라인 완료 - workId: {request.work_id}")

    except Exception as e:
        logger.error(f"파이프라인 실패 - workId: {request.work_id}: {e}")
        pipeline_logger.log_step(
            "pipeline",
            "failed",
            str(e)
        )
        raise

    finally:
        # Always send logs (success or failure)
        pipeline_logger.send_log_webhook(request)


def run_crawler(request: ContentGenerateRequest) -> List[str]:
    """
    Crawl trend keywords using keyword_crawler.

    Args:
        request: Content generation request

    Returns:
        List of crawled keywords

    Raises:
        Exception: If crawling fails
    """
    logger.info("트렌드 키워드 크롤링 시작...")

    c1 = request.trend_category.category1
    c2 = request.trend_category.category2
    c3 = request.trend_category.category3

    keywords = crawl_keywords(
        c1,
        c2,
        c3,
        persist_csv=False,  # Don't save to CSV (Airflow-specific)
        headless=True,
    )

    logger.info(f"크롤링 완료: {len(keywords)} keywords")
    return keywords


def select_keyword(
    request: ContentGenerateRequest,
    keywords: List[str]
) -> Dict[str, Any]:
    """
    Select best keyword using GPT.

    Args:
        request: Content generation request
        keywords: Crawled keywords

    Returns:
        Selection result dict with 'keyword' and 'reason'

    Raises:
        Exception: If selection fails
    """
    logger.info("최적 키워드 선택 중...")

    if not keywords:
        raise Exception("키워드 리스트가 비어 있습니다")

    started_at = datetime.now(timezone.utc)

    # Exclude recent keywords
    exclude_keywords = [
        kw for kw in request.recent_trend_keywords
        if isinstance(kw, str)
    ]

    logger.info(f"🔍 제외할 최근 키워드 ({len(exclude_keywords)}개): {exclude_keywords}")

    api_key = os.environ.get("OPENAI_API_KEY")
    result = select_best_keyword(
        keywords,
        api_key=api_key,
        exclude_keywords=exclude_keywords,
        as_dict=True,
    )

    completed_at = datetime.now(timezone.utc)

    # Prepare payload for webhook
    result["startedAt"] = started_at.isoformat()
    result["completedAt"] = completed_at.isoformat()

    logger.info(f"키워드 선택 완료: {result.get('keyword')}")
    return result


def send_keyword_webhook(
    request: ContentGenerateRequest,
    keyword_result: Dict[str, Any]
) -> None:
    """
    Send keyword selection result to backend.

    Args:
        request: Content generation request
        keyword_result: Keyword selection result

    Raises:
        Exception: If webhook fails
    """
    logger.info("키워드 선택 결과 전송 중...")

    webhook_url = request.webhook_urls.keyword_select
    if not webhook_url:
        raise Exception("KeywordSelect 웹훅 URL이 설정되지 않았습니다")

    payload = {
        "workId": request.work_id,
        "keyword": keyword_result.get("keyword"),
        "success": True,
        "message": keyword_result.get("reason"),
        "startedAt": keyword_result.get("startedAt"),
        "completedAt": keyword_result.get("completedAt"),
    }

    send_webhook(webhook_url, payload, request.webhook_secret)
    logger.info("키워드 선택 webhook 전송 완료")


def select_product(
    request: ContentGenerateRequest,
    keyword_result: Dict[str, Any]
) -> Dict[str, Any]:
    """
    Select product from crawled items using GPT.

    Args:
        request: Content generation request
        keyword_result: Selected keyword result

    Returns:
        Selected product dict

    Raises:
        Exception: If selection fails
    """
    logger.info("크롤링된 상품에서 선택 중...")

    if not request.crawled_products:
        raise Exception("crawledProducts 목록이 비어 있습니다")

    keyword = keyword_result.get("keyword")
    if not keyword:
        raise Exception("선택된 키워드를 찾을 수 없습니다")

    # Convert ProductInfo objects to dict for product_selector
    site_name = request.site_url or ""
    enriched_products = []
    for product in request.crawled_products:
        enriched = {
            "product_id": product.product_id,
            "name": product.product_name,
            "code": product.product_code,
            "url": product.product_detail_url,
            "price": product.product_price,
            "image_url": product.product_image_url,
            "siteName": site_name,
        }
        enriched_products.append(enriched)

    api_key = os.environ.get("OPENAI_API_KEY")
    result = select_best_product(
        keyword,
        enriched_products,
        api_key=api_key,
        as_dict=True,
    )

    logger.info(f"상품 선택 완료: {result.get('name')}")
    return result


def run_product_finder(
    request: ContentGenerateRequest,
    keyword_result: Dict[str, Any]
) -> Dict[str, Any]:
    """
    Find product using URL-based search.

    Args:
        request: Content generation request
        keyword_result: Selected keyword result

    Returns:
        Found product dict

    Raises:
        Exception: If product finding fails
    """
    logger.info("URL 기반 상품 검색 중...")

    keyword = keyword_result.get("keyword")
    if not keyword:
        raise Exception("선택된 키워드를 찾을 수 없습니다")

    site_url = request.site_url or ""
    exclude_names = request.recently_used_products or []

    api_key = os.environ.get("OPENAI_API_KEY")
    finder = ProductFinder(api_key=api_key)

    result = finder.find(
        keyword=keyword,
        site_url=site_url,
        exclude_names=exclude_names,
    )

    result_dict = result.to_dict()
    logger.info(f"상품 검색 완료: {result_dict.get('name')}")
    return result_dict


def send_product_webhook(
    request: ContentGenerateRequest,
    product_result: Dict[str, Any]
) -> None:
    """
    Send product selection result to backend.

    Args:
        request: Content generation request
        product_result: Product selection/find result

    Raises:
        Exception: If webhook fails
    """
    logger.info("상품 선택 결과 전송 중...")

    webhook_url = request.webhook_urls.product_select
    if not webhook_url:
        raise Exception("ProductSelect 웹훅 URL이 설정되지 않았습니다")

    payload = {
        "workId": request.work_id,
        "success": True,
        "completedAt": datetime.now(timezone.utc).isoformat(),
        "product": {
            "productCode": (
                product_result.get("code")
                or str(product_result.get("product_id"))
            ),
            "productName": product_result.get("name"),
            "productPrice": product_result.get("price"),
            "productUrl": product_result.get("url"),
            "imageUrl": product_result.get("image_url") or "",
            "mall": product_result.get("site_name") or request.site_url or "",
        },
    }

    send_webhook(webhook_url, payload, request.webhook_secret)
    logger.info("상품 선택 webhook 전송 완료")


def generate_content(
    request: ContentGenerateRequest,
    product_result: Dict[str, Any]
) -> Dict[str, Any]:
    """
    Generate blog content using OpenAI.

    Args:
        request: Content generation request
        product_result: Selected/found product

    Returns:
        Generated content dict

    Raises:
        Exception: If content generation fails
    """
    logger.info("AI 블로그 콘텐츠 생성 중...")

    # Build product info string
    product_info = ""
    parts = []
    if product_result.get("name"):
        parts.append(f"상품명: {product_result['name']}")
    if product_result.get("price"):
        parts.append(f"가격: {product_result['price']}")
    if product_result.get("code"):
        parts.append(f"코드: {product_result['code']}")
    if product_result.get("url"):
        parts.append(f"URL: {product_result['url']}")
    if product_result.get("image_url"):
        parts.append(f"이미지: {product_result['image_url']}")

    product_info = " / ".join(parts)

    if not product_info:
        raise Exception("상품 정보가 비어 있어 콘텐츠를 생성할 수 없습니다")

    product_url = product_result.get("url") or ""
    product_image_url = product_result.get("image_url") or ""

    api_key = os.environ.get("OPENAI_API_KEY")

    content = generate_blog_content(
        product_info=product_info,
        product_url=product_url,
        product_image_url=product_image_url,
        api_key=api_key,
    )

    if not content:
        raise Exception("콘텐츠 생성에 실패했습니다")

    logger.info(f"콘텐츠 생성 완료: {content.get('title')}")
    return content


def send_content_webhook(
    request: ContentGenerateRequest,
    content: Dict[str, Any]
) -> None:
    """
    Send generated content to backend.

    Args:
        request: Content generation request
        content: Generated content

    Raises:
        Exception: If webhook fails
    """
    logger.info("생성된 콘텐츠 전송 중...")

    webhook_url = request.webhook_urls.content_generate
    if not webhook_url:
        raise Exception("ContentGenerate 웹훅 URL이 설정되지 않았습니다")

    payload = {
        "workId": request.work_id,
        "success": True,
        "completedAt": datetime.now(timezone.utc).isoformat(),
        "title": content.get("title"),
        "content": content.get("html"),
        "summary": content.get("summary") or content.get("outline"),
    }

    send_webhook(webhook_url, payload, request.webhook_secret)
    logger.info("콘텐츠 생성 webhook 전송 완료")
