from __future__ import annotations

"""
Airflow/Python 워크플로에서 GPT 키워드 선정을 간단히 호출하기 위한 헬퍼.

`keyword_crawler.pipeline.crawl_keywords`가 반환한 키워드 리스트를 그대로 받아
`select_best_keyword`로 넘기는 것이 권장 사용 패턴이다.
"""

from typing import Sequence

from .selector import KeywordSelector, SelectionResult


def select_best_keyword(
    keywords: Sequence[str],
    *,
    api_key: str | None = None,
    model: str = "gpt-4o-mini",
    temperature: float = 0.3,
    system_prompt: str | None = None,
    max_retries: int = 2,
    as_dict: bool = True,
    exclude_keywords: Sequence[str] | None = None,
) -> SelectionResult | dict:
    """
    KeywordSelector를 사용해 키워드 리스트에서 하나를 선택한다.

    Airflow PythonOperator 등의 환경에서는 반환값(dict)을 그대로 XCom에 저장해
    downstream 태스크가 선택 결과를 공유할 수 있다.
    """

    if not keywords:
        raise ValueError("최소 1개 이상의 키워드가 필요합니다.")

    selector_kwargs = {
        "api_key": api_key,
        "model": model,
        "temperature": temperature,
    }
    if system_prompt:
        selector_kwargs["system_prompt"] = system_prompt

    selector = KeywordSelector(**selector_kwargs)
    result = selector.select(
        keywords,
        exclude_keywords=exclude_keywords,
        max_retries=max_retries,
    )

    if as_dict:
        return result.to_dict()
    return result
