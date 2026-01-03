"""
콘텐츠 생성 전체 파이프라인 엔트리포인트
"""

from typing import Dict, Optional

import logging
from content_generator.title_generator import generate_title
from content_generator.body_generator import generate_body
from content_generator.html_formatter import format_content_to_html

logger = logging.getLogger("content_pipeline")


def generate_blog_content(
    product_info: str,
    product_url: str = "",
    product_image_url: str = "",
    api_key: str | None = None,
) -> Optional[Dict]:
    """제목→아웃라인→본문→HTML 생성까지 수행하는 진입점."""
    info_preview = product_info[:80] + ("..." if len(product_info) > 80 else "")
    logger.info("[Workflow] 콘텐츠 생성 시작 - 상품 정보: %s", info_preview or "미입력")

    title = generate_title(product_info, api_key=api_key)
    if not title:
        logger.error("[Workflow] 제목 생성 실패 - 프로세스 중단")
        return None

    body, outline_text = generate_body(
        product_info,
        title,
        api_key=api_key,
    )
    if not body:
        logger.error("[Workflow] 본문 생성 실패 - 프로세스 중단")
        return None
    outline_text = outline_text or ""

    body = _append_product_block(body, product_url, product_image_url)

    try:
        html = format_content_to_html(
            {
                "title": title,
                "body": body,
            }
        )
    except Exception as exc:
        logger.error("[Workflow] HTML 포맷팅 실패: %s", exc)
        return None

    logger.info("[Workflow] 콘텐츠 생성 완료")

    return {
        "product_info": product_info,
        "title": title,
        "outline": outline_text,
        "summary": outline_text,
        "body": body,
        "html": html,
        "product_url": product_url,
        "product_image_url": product_image_url,
    }


def _append_product_block(body: str, product_url: str, product_image_url: str) -> str:
    top_block = ""
    spacer = ""
    bottom_block = ""

    if product_image_url:
        top_block = (
            '<div class="product-image" style="margin-bottom:32px;text-align:center;">'
            f'<img src="{product_image_url}" alt="상품 이미지" '
            'style="max-width:100%;height:auto;border-radius:16px;box-shadow:0 10px 30px rgba(0,0,0,0.08);" />'
            "</div>"
        )

    if product_url:
        spacer = "<br><br><br>"
        bottom_block = (
            '<div class="product-highlight" '
            'style="margin-top:20px;padding:28px;border-radius:18px;background:linear-gradient(120deg, #fdf2f8, #f0f9ff);'
            'box-shadow:0 16px 40px rgba(0,0,0,0.08);text-align:center;">'
            '<p style="font-size:1.1rem;font-weight:600;color:#1a1a1a;margin-bottom:12px;">이 무드, 우리 같이 즐겨요</p>'
            f'<p style="color:#333;line-height:1.7;margin-bottom:18px;">궁금한 점이 있거나 실제 사용감을 보고 싶다면 '
            f'<a href="{product_url}" target="_blank" rel="noopener noreferrer" '
            'style="color:#0a66c2;font-weight:600;text-decoration:none;">상품 상세 페이지</a>에서 천천히 살펴보세요. '
            "저는 이미 루틴 속에 완전히 녹였답니다 :)</p>"
            f'<a href="{product_url}" target="_blank" rel="noopener noreferrer" '
            'style="display:inline-flex;align-items:center;gap:8px;padding:12px 24px;border-radius:999px;'
            'background:#0a66c2;color:#fff;font-weight:600;text-decoration:none;box-shadow:0 10px 25px rgba(10,102,194,0.4);">'
            "자세히 보기 →</a>"
            "</div>"
        )

    return f"{top_block}{body}{spacer}{bottom_block}"
