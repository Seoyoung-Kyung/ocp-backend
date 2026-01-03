"""
키워드 크롤러 모듈 진입점.

외부에서는 crawl_keywords 함수를 import해 사용한다.
"""

from .pipeline import DEFAULT_OUTPUT_PATH, crawl_keywords

__all__ = ["crawl_keywords", "DEFAULT_OUTPUT_PATH"]
