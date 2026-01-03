"""
본문+아웃라인 생성 모듈.
"""

import json
import re
import time
from typing import Dict, List, Optional, Tuple

from content_generator.gpt_utils import generate_with_prompt
from content_generator.prompt_builder import format_outline_for_prompt
import logging
logger = logging.getLogger("content_pipeline")


def generate_body(
    product_info: str,
    title: str,
    max_retries: int = 3,
    api_key: Optional[str] = None,
) -> Tuple[Optional[str], Optional[str]]:
    """GPT로 아웃라인과 본문을 한 번에 생성한다."""
    info_preview = product_info[:80] + ("..." if len(product_info) > 80 else "")
    logger.info("본문+아웃라인 생성 시작 - 상품 정보: %s", info_preview or "미입력")

    outline = _generate_outline(product_info, title, max_retries, api_key)
    if not outline:
        return None, None

    outline_text = format_outline_for_prompt(outline)

    for attempt in range(max_retries):
        try:
            response = generate_with_prompt(
                prompt_name="content_prompt",
                user_data={
                    "제목": title,
                    "아웃라인": outline_text,
                    "상품 정보": product_info or "없음",
                },
                temperature=0.7,
                max_retries=2,
                api_key=api_key,
            )

            if not response:
                logger.warning("본문 응답 없음. 재시도 (%s/%s)", attempt + 1, max_retries)
                time.sleep(1)
                continue

            response = _sanitize_markdown_blocks(response)
            
            if validate_body(response):
                return _add_section_spacing(_format_headings(response)), outline_text

            text_length = len(re.sub(r"<[^>]+>", "", response).strip())
            if text_length > 1800:
                summarized = summarize_body(response, api_key=api_key)
                if summarized and _is_valid_summary(summarized):
                    return _add_section_spacing(_format_headings(summarized)), outline_text

            if attempt == max_retries - 1:
                logger.warning(
                    "본문 검증 실패(최종 시도). 조건을 일부 만족하지 않지만 결과를 사용합니다."
                )
                return _add_section_spacing(_format_headings(response)), outline_text

            logger.warning("본문 검증 실패. 재시도 (%s/%s)", attempt + 1, max_retries)
            time.sleep(1)
        except Exception as exc:
            logger.error(
                "본문 생성 중 예외: %s. 재시도 (%s/%s)",
                exc,
                attempt + 1,
                max_retries,
            )
            time.sleep(1)

    logger.error("본문 생성 실패: 최대 재시도 초과")
    return None, None


def summarize_body(content: str, max_retries: int = 2, api_key: Optional[str] = None) -> Optional[str]:
    logger.info("본문 요약 시작")
    for attempt in range(max_retries):
        try:
            response = generate_with_prompt(
                prompt_name="summarize_prompt",
                user_data={"원본 본문": content},
                temperature=0.7,
                max_retries=2,
                api_key=api_key,
            )
            if not response:
                time.sleep(1)
                continue
            if _is_valid_summary(response):
                return response
        except Exception as exc:
            logger.error("요약 중 예외: %s", exc)
            time.sleep(1)
    return None


def validate_body(body: str) -> bool:
    if not body.strip():
        return False
    if "<h2" not in body:
        return False

    text_only = re.sub(r"<[^>]+>", "", body)
    text_length = len(text_only.strip())
    if text_length < 1200:
        logger.debug("본문 길이 부족(%s자) - 재시도", text_length)
        return False
    if text_length > 2100:
        logger.debug("본문 길이 초과(%s자) - 재시도", text_length)
        return False
    return True


def _is_valid_summary(body: str) -> bool:
    text_only = re.sub(r"<[^>]+>", "", body)
    text_length = len(text_only.strip())
    return 1500 <= text_length <= 2000


def _generate_outline(
    product_info: str,
    title: str,
    max_retries: int,
    api_key: Optional[str],
) -> Optional[Dict]:
    for attempt in range(max_retries):
        try:
            response = generate_with_prompt(
                prompt_name="outline_prompt",
                user_data={
                    "제목": title,
                    "상품 정보": product_info or "없음",
                },
                temperature=0.7,
                max_retries=2,
                api_key=api_key,
            )
            if not response:
                time.sleep(1)
                continue
            outline = _parse_outline(response)
            if _validate_outline(outline):
                return outline
        except Exception as exc:
            logger.error("아웃라인 생성 중 예외: %s", exc)
            time.sleep(1)
    logger.warning("아웃라인 생성 실패, 폴백 사용")
    return _fallback_outline(product_info, title)


def _parse_outline(response: str) -> Dict:
    try:
        parsed = json.loads(response)
        if isinstance(parsed, dict) and "h2" in parsed:
            return parsed
    except json.JSONDecodeError:
        pass
    return _parse_markdown_outline(response)


def _parse_markdown_outline(markdown_text: str) -> Dict:
    h2_list: List[Dict] = []
    current = None
    for line in markdown_text.splitlines():
        stripped = line.strip()
        if not stripped:
            continue
        if stripped.startswith("###"):
            if current is None:
                current = {"title": "", "h3": []}
            title = stripped[3:].strip(" #")
            if title:
                current["h3"].append(title)
        elif stripped.startswith("##"):
            if current is not None:
                h2_list.append(current)
            title = stripped[2:].strip(" #")
            current = {"title": title, "h3": []} if title else None
    if current is not None:
        h2_list.append(current)
    return {"h2": h2_list} if h2_list else {}


def _validate_outline(outline: Dict) -> bool:
    if not outline or "h2" not in outline:
        return False
    return bool(outline["h2"])


def _fallback_outline(product_info: str, title: str) -> Dict:
    base = ""
    if product_info:
        first_segment = product_info.split("/")[0]
        base = (
            first_segment.replace("상품명:", "")
            .replace("제품명:", "")
            .strip()
        )
    focus = base or title or "프리미엄 제품"
    return {
        "h2": [
            {"title": f"{focus} 개요", "h3": ["핵심 특징", "관련 트렌드"]},
            {"title": f"{focus} 분석", "h3": ["품목별 차별점", "사용자 시나리오"]},
            {"title": f"{focus} 활용", "h3": ["실전 활용 팁", "주의할 점"]},
        ]
    }


def _sanitize_markdown_blocks(text: str) -> str:
    cleaned = (
        text.replace("```html", "")
        .replace("```HTML", "")
        .replace("```", "")
        .strip()
    )
    return cleaned


def _add_section_spacing(html: str) -> str:
    if not html:
        return html
    result = re.sub(
        r"(</h2>)\s*",
        r"\1<br>",
        html,
        flags=re.IGNORECASE,
    )
    result = re.sub(
        r"</p>\s*(?=<h2)",
        "</p><br><br>",
        result,
        flags=re.IGNORECASE,
    )
    result = re.sub(
        r"</p>\s*(?=<h3)",
        "</p><br>",
        result,
        flags=re.IGNORECASE,
    )
    return result


def _format_headings(html: str) -> str:
    if not html:
        return html

    def _format_h2(match: re.Match) -> str:
        content = match.group(1).strip()
        style = (
            "text-align:center;"
            "font-size:1.35rem;"
            "font-weight:600;"
            "color:#111;"
            "letter-spacing:0.08em;"
            "padding:0.5rem 0;"
            "border-top:1px solid #eee;"
            "border-bottom:1px solid #eee;"
        )
        return f'<h2 style="{style}">[{content}]</h2>'

    def _format_h3(match: re.Match) -> str:
        content = re.sub(r'^"+|"+$', '', match.group(1).strip())
        style = (
            "display:block;"
            "width:fit-content;"
            "margin:0.4rem auto 0;"
            "text-align:center;"
            "font-size:1.05rem;"
            "font-weight:500;"
            "color:#444;"
            "font-style:italic;"
        )
        return f'<h3 style="{style}">"{content}"</h3>'

    formatted = re.sub(
        r"<h2[^>]*>(.*?)</h2>",
        _format_h2,
        html,
        flags=re.DOTALL | re.IGNORECASE,
    )
    formatted = re.sub(
        r"<h3[^>]*>(.*?)</h3>",
        _format_h3,
        formatted,
        flags=re.DOTALL | re.IGNORECASE,
    )
    formatted = re.sub(
        r"(</h3>)\s*(?=<p)",
        r"\1<br>",
        formatted,
        flags=re.IGNORECASE,
    )
    return formatted
