"""
HTML 포맷터 모듈
AI2 담당
"""
from typing import Dict
import logging
logger = logging.getLogger("content_pipeline")


def format_content_to_html(content: Dict) -> str:
    """
    생성된 콘텐츠를 최종 HTML 템플릿으로 변환합니다.

    Args:
        content: 생성된 콘텐츠 딕셔너리
        {
            "title": "...",
            "body": "...",
        }

    Returns:
        최종 HTML 문자열
    """
    logger.info("HTML 포맷팅 시작")

    title = content.get("title", "")
    body = content.get("body", "")

    html = f"""
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>{title}</title>
</head>
<body>
    {body}
</body>
</html>
"""
    return html.strip()

