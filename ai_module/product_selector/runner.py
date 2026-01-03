from __future__ import annotations

from typing import Sequence

from .selector import ProductSelectionResult, ProductSelector


def select_best_product(
    keyword: str,
    products: Sequence[dict],
    *,
    api_key: str | None = None,
    model: str = "gpt-4o-mini",
    temperature: float = 0.3,
    system_prompt: str | None = None,
    max_retries: int = 2,
    as_dict: bool = True,
) -> ProductSelectionResult | dict:
    if not keyword:
        raise ValueError("키워드가 비어 있습니다.")
    if not products:
        raise ValueError("선택할 상품 목록이 없습니다.")

    selector_kwargs = {
        "api_key": api_key,
        "model": model,
        "temperature": temperature,
    }
    if system_prompt:
        selector_kwargs["system_prompt"] = system_prompt

    selector = ProductSelector(**selector_kwargs)
    result = selector.select(keyword, products, max_retries=max_retries)

    if as_dict:
        return result.to_dict()
    return result
