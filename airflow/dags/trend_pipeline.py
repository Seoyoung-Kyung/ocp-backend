from __future__ import annotations

import json
from datetime import datetime, timedelta, timezone
from typing import Any

import pika
import requests
import logging

log = logging.getLogger(__name__)

from airflow import DAG
from airflow.exceptions import AirflowFailException
from airflow.models import Variable
from airflow.operators.empty import EmptyOperator
from airflow.operators.python import BranchPythonOperator, PythonOperator

from keyword_crawler import crawl_keywords
from keyword_selector import select_best_keyword
from product_selector import select_best_product
from content_generator import generate_blog_content
from product_finder import ProductFinder


RABBITMQ_URL_VAR = "rabbitmq_url"
RABBITMQ_QUEUE_VAR = "rabbitmq_trend_queue"
OPENAI_KEY_VAR = "openai_api_key"
WEBHOOK_HEADER = "X-WEBHOOK-SECRET"


def consume_request(**context) -> None:
    """RabbitMQ에서 ContentGenerateRequest 메시지를 한 건 소비하고 XCom에 저장."""
    log.info("요청 수신 중...")
    url = Variable.get(RABBITMQ_URL_VAR)
    queue_name = Variable.get(RABBITMQ_QUEUE_VAR, default_var="content-generate-queue")

    connection = pika.BlockingConnection(pika.URLParameters(url))
    channel = connection.channel()
    method_frame, header_frame, body = channel.basic_get(queue=queue_name, auto_ack=False)

    if not method_frame or body is None:
        connection.close()
        raise AirflowFailException("대기열에 메시지가 없습니다.")

    payload = json.loads(body)
    channel.basic_ack(delivery_tag=method_frame.delivery_tag)
    connection.close()

    context["ti"].xcom_push(key="content_request", value=payload)


def run_crawler(**context) -> list[str]:
    """키워드 크롤러를 실행해 리스트를 XCom에 적재."""
    log.info("트렌드 키워드 크롤링 시작...")
    ti = context["ti"]
    request: dict[str, Any] | None = ti.xcom_pull(key="content_request", task_ids="consume_request")
    if not request:
        raise AirflowFailException("ContentGenerateRequest 정보를 찾을 수 없습니다.")

    categories = request.get("trendCategory") or {}
    c1 = categories.get("category1")
    c2 = categories.get("category2")
    c3 = categories.get("category3")

    keywords = crawl_keywords(
        c1,
        c2,
        c3,
        persist_csv=False,
        headless=True,
    )
    ti.xcom_push(key="keywords", value=keywords)
    return keywords


def select_keyword(**context) -> dict:
    """GPT를 이용해 크롤링된 키워드 리스트에서 하나를 선택."""
    log.info("최적 키워드 선택 중...")
    ti = context["ti"]
    keywords: list[str] | None = ti.xcom_pull(key="keywords", task_ids="run_crawler")
    if not keywords:
        raise AirflowFailException("키워드 리스트를 찾을 수 없습니다.")
    request: dict[str, Any] | None = ti.xcom_pull(key="content_request", task_ids="consume_request")
    if not request:
        raise AirflowFailException("ContentGenerateRequest 정보를 찾을 수 없습니다.")

    started_at = datetime.now(timezone.utc)
    exclude_keywords = []
    recent_trends = request.get("recentTrendKeywords") or []
    if isinstance(recent_trends, list):
        exclude_keywords = [kw for kw in recent_trends if isinstance(kw, str)]

    result = select_best_keyword(
        keywords,
        api_key=Variable.get(OPENAI_KEY_VAR, default_var=None),
        exclude_keywords=exclude_keywords,
        as_dict=True,
    )
    completed_at = datetime.now(timezone.utc)

    ti.xcom_push(key="selected_keyword", value=result)
    ti.xcom_push(
        key="keyword_selection_payload",
        value={
            "workId": request.get("workId"),
            "keyword": result.get("keyword"),
            "success": True,
            "message": result.get("reason"),
            "startedAt": started_at.isoformat(),
            "completedAt": completed_at.isoformat(),
        },
    )
    return result


def send_keyword_webhook(**context) -> None:
    """백엔드가 제공한 키워드 선택 웹훅으로 결과를 전달한다."""
    log.info("키워드 선택 결과 전송 중...")
    ti = context["ti"]
    request: dict[str, Any] | None = ti.xcom_pull(key="content_request", task_ids="consume_request")
    payload: dict[str, Any] | None = ti.xcom_pull(
        key="keyword_selection_payload", task_ids="select_keyword"
    )
    if not request or not payload:
        raise AirflowFailException("웹훅에 전달할 정보가 부족합니다.")

    webhook_urls = request.get("webhookUrls") or {}
    webhook_url = webhook_urls.get("keywordSelect")
    if not webhook_url:
        raise AirflowFailException("KeywordSelect 웹훅 URL이 설정되지 않았습니다.")

    secret = request.get("webhookSecret")

    headers = {"Content-Type": "application/json"}
    if secret:
        headers[WEBHOOK_HEADER] = secret

    response = requests.post(webhook_url, json=payload, headers=headers, timeout=30)
    if not response.ok:
        raise AirflowFailException(
            f"웹훅 호출 실패 (status={response.status_code}, body={response.text})"
        )


def select_product(**context) -> dict:
    log.info("크롤링된 상품에서 선택 중...")
    ti = context["ti"]
    request: dict[str, Any] | None = ti.xcom_pull(key="content_request", task_ids="consume_request")
    keyword_result: dict[str, Any] | None = ti.xcom_pull(
        key="selected_keyword", task_ids="select_keyword"
    )
    if not request or not keyword_result:
        raise AirflowFailException("상품 선택에 필요한 정보가 부족합니다.")

    products = request.get("crawledProducts") or []
    if not products:
        raise AirflowFailException("crawledProducts 목록이 비어 있습니다.")

    keyword = keyword_result.get("keyword")
    if not keyword:
        raise AirflowFailException("선택된 키워드를 찾을 수 없습니다.")

    started_at = datetime.now(timezone.utc)
    site_name = request.get("siteName") or request.get("siteUrl") or ""
    enriched_products = []
    for product in products:
        enriched = dict(product)
        if site_name and "siteName" not in enriched:
            enriched["siteName"] = site_name
        enriched_products.append(enriched)

    result = select_best_product(
        keyword,
        enriched_products,
        api_key=Variable.get(OPENAI_KEY_VAR, default_var=None),
        as_dict=True,
    )
    completed_at = datetime.now(timezone.utc)

    ti.xcom_push(key="selected_product", value=result)
    ti.xcom_push(
        key="product_selection_payload",
        value={
            "workId": request.get("workId"),
            "success": True,
            "completedAt": completed_at.isoformat(),
            "product": {
                "productCode": result.get("code") or str(result.get("product_id")),
                "productName": result.get("name"),
                "productPrice": result.get("price"),
                "productUrl": result.get("url"),
                "imageUrl": result.get("image_url") or "",
                "mall": result.get("site_name") or request.get("siteUrl") or "",
            },
        },
    )
    return result


def send_product_webhook(**context) -> None:
    log.info("상품 선택 결과 전송 중...")
    ti = context["ti"]
    request: dict[str, Any] | None = ti.xcom_pull(key="content_request", task_ids="consume_request")
    payload: dict[str, Any] | None = ti.xcom_pull(
        key="product_selection_payload", task_ids="select_product"
    )
    if not payload:
        payload = ti.xcom_pull(
            key="product_selection_payload", task_ids="find_product"
        )
    if not request or not payload:
        raise AirflowFailException("상품 웹훅에 전달할 정보가 부족합니다.")

    webhook_urls = request.get("webhookUrls") or {}
    webhook_url = webhook_urls.get("productSelect")
    if not webhook_url:
        raise AirflowFailException("ProductSelect 웹훅 URL이 설정되지 않았습니다.")

    secret = request.get("webhookSecret")
    headers = {"Content-Type": "application/json"}
    if secret:
        headers[WEBHOOK_HEADER] = secret

    response = requests.post(webhook_url, json=payload, headers=headers, timeout=30)
    if not response.ok:
        raise AirflowFailException(
            f"상품 웹훅 호출 실패 (status={response.status_code}, body={response.text})"
        )


def branch_product_selection(**context) -> str:
    log.info("상품 선택 경로 분기 중...")
    ti = context["ti"]
    request: dict[str, Any] | None = ti.xcom_pull(key="content_request", task_ids="consume_request")
    if not request:
        raise AirflowFailException("ContentGenerateRequest 정보를 찾을 수 없습니다.")
    return "select_product" if request.get("hasCrawledItems") else "find_product"


def run_product_finder(**context) -> dict:
    log.info("URL 기반 상품 검색 중...")
    ti = context["ti"]
    request: dict[str, Any] | None = ti.xcom_pull(key="content_request", task_ids="consume_request")
    keyword_result: dict[str, Any] | None = ti.xcom_pull(
        key="selected_keyword", task_ids="select_keyword"
    )
    if not request or not keyword_result:
        raise AirflowFailException("상품 찾기에 필요한 정보가 부족합니다.")

    keyword = keyword_result.get("keyword")
    if not keyword:
        raise AirflowFailException("선택된 키워드를 찾을 수 없습니다.")

    site_url = request.get("siteUrl") or request.get("siteName") or ""
    site_label = request.get("siteName") or site_url
    exclude_names = request.get("recentlyUsedProducts") or []
    api_key = Variable.get(OPENAI_KEY_VAR, default_var=None)
    finder = ProductFinder(api_key=api_key)
    result = finder.find(
        keyword=keyword,
        site_url=site_url,
        exclude_names=exclude_names,
    )
    result_dict = result.to_dict()

    completed_at = datetime.now(timezone.utc).isoformat()

    ti.xcom_push(key="selected_product", value=result_dict)
    ti.xcom_push(
        key="product_selection_payload",
        value={
            "workId": request.get("workId"),
            "success": True,
            "completedAt": completed_at,
            "product": {
                "productCode": result_dict.get("code")
                or result_dict.get("product_id")
                or result_dict.get("name"),
                "productName": result_dict.get("name"),
                "productPrice": result_dict.get("price"),
                "productUrl": result_dict.get("url"),
                "imageUrl": result_dict.get("image_url") or "",
                "mall": result_dict.get("site_name") or site_label or "",
            },
        },
    )
    return result_dict


def generate_content(**context) -> dict:
    log.info("AI 블로그 콘텐츠 생성 중...")
    ti = context["ti"]
    request: dict[str, Any] | None = ti.xcom_pull(key="content_request", task_ids="consume_request")
    product_result: dict[str, Any] | None = ti.xcom_pull(
        key="selected_product", task_ids="select_product"
    )
    if not product_result:
        product_result = ti.xcom_pull(key="selected_product", task_ids="find_product")
    if not request:
        raise AirflowFailException("콘텐츠 생성에 필요한 정보가 부족합니다.")

    product_info = request.get("productDescription") or ""
    product_url = ""
    product_image_url = ""
    if product_result:
        if not product_info:
            parts = []
            if product_result.get("name"):
                parts.append(f"상품명: {product_result['name']}")
            if product_result.get("price"):
                parts.append(f"가격: {product_result['price']}")
            if product_result.get("code"):
                parts.append(f"코드: {product_result['code']}")
            if product_result.get("url"):
                parts.append(f"URL: {product_result['url']}")
                product_url = product_result["url"]
            if product_result.get("image_url"):
                parts.append(f"이미지: {product_result['image_url']}")
                product_image_url = product_result["image_url"]
            product_info = " / ".join(parts)
        else:
            product_url = product_result.get("url") or ""
            product_image_url = product_result.get("image_url") or ""

    api_key = Variable.get(OPENAI_KEY_VAR, default_var=None)

    if not product_info:
        raise AirflowFailException("상품 정보가 비어 있어 콘텐츠를 생성할 수 없습니다.")

    content = generate_blog_content(
        product_info=product_info,
        product_url=product_url,
        product_image_url=product_image_url,
        api_key=api_key,
    )
    if not content:
        raise AirflowFailException("콘텐츠 생성에 실패했습니다.")

    ti.xcom_push(key="generated_content", value=content)
    return content


def send_content_webhook(**context) -> None:
    log.info("생성된 콘텐츠 전송 중...")
    ti = context["ti"]
    request: dict[str, Any] | None = ti.xcom_pull(key="content_request", task_ids="consume_request")
    content: dict[str, Any] | None = ti.xcom_pull(
        key="generated_content", task_ids="generate_content"
    )
    if not request or not content:
        raise AirflowFailException("콘텐츠 웹훅에 전달할 정보가 부족합니다.")

    webhook_urls = request.get("webhookUrls") or {}
    webhook_url = webhook_urls.get("contentGenerate")
    if not webhook_url:
        raise AirflowFailException("ContentGenerate 웹훅 URL이 설정되지 않았습니다.")

    secret = request.get("webhookSecret")
    headers = {"Content-Type": "application/json"}
    if secret:
        headers[WEBHOOK_HEADER] = secret

    payload = {
        "workId": request.get("workId"),
        "success": True,
        "completedAt": datetime.now(timezone.utc).isoformat(),
        "title": content.get("title"),
        "content": content.get("html"),
        "summary": content.get("summary") or content.get("outline"),
    }

    response = requests.post(webhook_url, json=payload, headers=headers, timeout=30)
    if not response.ok:
        raise AirflowFailException(
            f"콘텐츠 웹훅 호출 실패 (status={response.status_code}, body={response.text})"
        )

def send_log_collection_webhook(**context) -> None:
      """DAG 실행 완료 후 백엔드로 로그 수집 요청을 보낸다."""
      log.info("Airflow 로그 수집 시작...")
      ti = context["ti"]
      request: dict[str, Any] | None = ti.xcom_pull(key="content_request", task_ids="consume_request")

      if not request:
          log.warning("ContentGenerateRequest 정보를 찾을 수 없어 로그 수집을 건너뜁니다.")
          return

      # Airflow 실행 정보
      dag_run = context.get("dag_run")
      if not dag_run:
          log.warning("dag_run 정보를 찾을 수 없어 로그 수집을 건너뜁니다.")
          return

      dag_id = dag_run.dag_id
      run_id = dag_run.run_id
      work_id = request.get("workId")

      if not work_id:
          log.warning("workId가 없어 로그 수집을 건너뜁니다.")
          return

      # 웹훅 URL 가져오기
      webhook_urls = request.get("webhookUrls") or {}
      webhook_url = webhook_urls.get("airflowLog")

      if not webhook_url:
          log.warning("AirflowLog 웹훅 URL이 설정되지 않았습니다. 로그 수집을 건너뜁니다.")
          return

      secret = request.get("webhookSecret")

      headers = {"Content-Type": "application/json"}
      if secret:
          headers[WEBHOOK_HEADER] = secret

      payload = {
          "workId": work_id,
          "dagId": dag_id,
          "runId": run_id
      }

      try:
          log.info(f"로그 수집 웹훅 호출 - workId: {work_id}, dagId: {dag_id}, runId: {run_id}")
          response = requests.post(webhook_url, json=payload, headers=headers, timeout=30)

          if response.ok:
              log.info(f"로그 수집 웹훅 호출 성공 - workId: {work_id}")
          else:
              log.warning(
                  f"로그 수집 웹훅 호출 실패 (status={response.status_code}, body={response.text})"
              )
      except Exception as e:
          # 로그 수집 실패는 전체 DAG를 실패시키지 않음
          log.warning(f"로그 수집 웹훅 호출 중 예외 발생: {e}")


default_args = {
    "owner": "trend-bot",
    "retries": 2,
    "retry_delay": timedelta(minutes=5),
}

with DAG(
    dag_id="trend_pipeline",
    description="ContentGenerateRequest 기반 트렌드 → 키워드 추출 파이프라인",
    default_args=default_args,
    start_date=datetime(2024, 1, 1),
    schedule_interval=None,
    catchup=False,
) as dag:
    consume_task = PythonOperator(
        task_id="consume_request",
        python_callable=consume_request,
        provide_context=True,
    )

    crawl_task = PythonOperator(
        task_id="run_crawler",
        python_callable=run_crawler,
        provide_context=True,
    )

    select_task = PythonOperator(
        task_id="select_keyword",
        python_callable=select_keyword,
        provide_context=True,
    )

    webhook_task = PythonOperator(
        task_id="send_keyword_webhook",
        python_callable=send_keyword_webhook,
        provide_context=True,
    )

    branch_task = BranchPythonOperator(
        task_id="branch_product_path",
        python_callable=branch_product_selection,
        provide_context=True,
    )

    product_task = PythonOperator(
        task_id="select_product",
        python_callable=select_product,
        provide_context=True,
    )

    product_finder_task = PythonOperator(
        task_id="find_product",
        python_callable=run_product_finder,
        provide_context=True,
    )

    product_selection_join = EmptyOperator(
        task_id="product_selection_join",
        trigger_rule="none_failed_min_one_success",
    )

    product_webhook_task = PythonOperator(
        task_id="send_product_webhook",
        python_callable=send_product_webhook,
        provide_context=True,
    )

    content_task = PythonOperator(
        task_id="generate_content",
        python_callable=generate_content,
        provide_context=True,
    )

    content_webhook_task = PythonOperator(
        task_id="send_content_webhook",
        python_callable=send_content_webhook,
        provide_context=True,
    )

    # ✅ 새로 추가: 로그 수집 웹훅 Task
    log_collection_task = PythonOperator(
        task_id="send_log_webhook",
        python_callable=send_log_collection_webhook,
        provide_context=True,
        trigger_rule="all_done",  # 이전 task들이 성공/실패 상관없이 무조건 실행
    )

    consume_task >> crawl_task >> select_task >> webhook_task >> branch_task
    branch_task >> product_task >> product_selection_join
    branch_task >> product_finder_task >> product_selection_join
    product_selection_join >> product_webhook_task >> content_task >> content_webhook_task
    content_webhook_task >> log_collection_task