from __future__ import annotations

import json
import logging
import os
from dataclasses import dataclass, asdict
from pathlib import Path
from typing import Any, Sequence

from openai import OpenAI

from common.utils import load_env_from_default_locations

logger = logging.getLogger(__name__)

DEFAULT_SYSTEM_PROMPT = (
    "You are a senior commerce curator. "
    "Given a target keyword and a list of products, pick the single product that best fits the shopper intent. "
    "Always respond in Korean and provide a short reason."
)


@dataclass
class ProductSelectionResult:
    product_id: Any
    name: str
    reason: str
    raw_response: str
    price: str | None = None
    url: str | None = None
    code: str | None = None
    image_url: str | None = None
    site_name: str | None = None

    def to_dict(self) -> dict:
        return asdict(self)


class ProductSelector:
    def __init__(
        self,
        api_key: str | None = None,
        model: str = "gpt-4o-mini",
        temperature: float = 0.3,
        system_prompt: str = DEFAULT_SYSTEM_PROMPT,
    ):
        self._ensure_env(api_key)
        self.api_key = api_key or os.getenv("OPENAI_API_KEY")
        if not self.api_key:
            raise RuntimeError("OPENAI_API_KEY가 설정되지 않았습니다.")

        self.client = OpenAI(api_key=self.api_key)
        self.model = model
        self.temperature = temperature
        self.system_prompt = system_prompt

    @staticmethod
    def _ensure_env(api_key: str | None) -> None:
        if api_key:
            return
        if os.getenv("OPENAI_API_KEY"):
            return
        load_env_from_default_locations(Path(__file__).resolve())

    def select(
        self,
        keyword: str,
        products: Sequence[dict],
        max_retries: int = 2,
    ) -> ProductSelectionResult:
        normalized = self._normalize_products(products)
        if not normalized:
            raise ValueError("선택할 상품 목록이 비어 있습니다.")

        prompt = self.build_user_prompt(keyword, normalized)
        last_error: Exception | None = None
        for attempt in range(1, max_retries + 1):
            try:
                raw = self._call_gpt(prompt)
                parsed = self._parse_response(raw, normalized)
                return ProductSelectionResult(
                    product_id=parsed["product_id"],
                    name=parsed["name"],
                    price=parsed.get("price"),
                    url=parsed.get("url"),
                    code=parsed.get("code"),
                    image_url=parsed.get("image_url"),
                    site_name=parsed.get("site_name"),
                    reason=parsed["reason"],
                    raw_response=raw,
                )
            except Exception as exc:  # pragma: no cover
                last_error = exc
                logger.warning(
                    "GPT 상품 선택 실패 (%s/%s): %s",
                    attempt,
                    max_retries,
                    exc,
                    exc_info=False,
                )
        raise RuntimeError("GPT 상품 선택에 실패했습니다.") from last_error

    def build_user_prompt(self, keyword: str, products: list[dict]) -> str:
        instructions = (
            f"선정된 블로그 키워드: {keyword}\n\n"
            "위 키워드와 가장 궁합이 좋고 구매 전환 가능성이 높은 상품 1개를 골라주세요.\n"
            "- 키워드와의 직접적인 연관성, 계절성, 가격 경쟁력, 차별화 포인트를 고려하세요.\n"
            "선택 이유는 한 문장으로 요약하세요."
        )
        product_lines = []
        for idx, product in enumerate(products, 1):
            name = product["name"]
            price = product.get("price") or "가격 정보 없음"
            url = product.get("url") or "URL 없음"
            code = product.get("code") or "코드 정보 없음"
            image = product.get("image_url") or ""
            site_name = product.get("site_name") or ""
            block = [
                f"{idx}. {name}",
                f"   코드: {code}",
                f"   가격: {price}",
                f"   링크: {url}",
            ]
            if site_name:
                block.append(f"   쇼핑몰: {site_name}")
            if image:
                block.append(f"   이미지: {image}")
            product_lines.append("\n".join(block))

        format_hint = (
            '\n\n반드시 다음 JSON 형식으로만 답해주세요:\n'
            '{\n'
            '  "product_id": "...",\n'
            '  "name": "...",\n'
            '  "product_code": "...",\n'
            '  "reason": "...",\n'
            '  "price": "...",\n'
            '  "url": "...",\n'
            '  "image_url": "..."\n'
            "}"
        )
        return f"{instructions}\n\n상품 목록:\n" + "\n".join(product_lines) + format_hint

    def _call_gpt(self, user_prompt: str) -> str:
        response = self.client.chat.completions.create(
            model=self.model,
            messages=[
                {"role": "system", "content": self.system_prompt},
                {"role": "user", "content": user_prompt},
            ],
            temperature=self.temperature,
        )
        return response.choices[0].message.content.strip()

    @staticmethod
    def _normalize_products(products: Sequence[dict]) -> list[dict]:
        normalized: list[dict] = []
        for product in products:
            if not isinstance(product, dict):
                continue
            name = (
                str(
                    product.get("productName")
                    or product.get("name")
                    or product.get("title")
                    or ""
                ).strip()
            )
            if not name:
                continue
            code = (
                str(product.get("productCode") or product.get("code") or "").strip()
                or None
            )
            raw_price = product.get("productPrice") or product.get("price")
            if isinstance(raw_price, (int, float)):
                price_str = f"{int(raw_price):,}원"
            else:
                price_str = str(raw_price).strip() if raw_price else None
            url = (
                product.get("productDetailUrl")
                or product.get("productUrl")
                or product.get("url")
            )
            image_url = product.get("productImageUrl") or product.get("imageUrl")
            normalized.append(
                {
                    "product_id": product.get("productId")
                    or product.get("product_id")
                    or product.get("id"),
                    "name": name,
                    "price": price_str,
                    "url": url,
                    "code": code,
                    "image_url": image_url,
                    "site_name": product.get("siteName") or product.get("site_name"),
                }
            )
        return normalized

    @staticmethod
    def _parse_response(raw: str, candidates: list[dict]) -> dict:
        data = ProductSelector._extract_json(raw)
        logger.debug("GPT raw response: %s", raw)
        name = str(data.get("name", "")).strip()
        product_id = data.get("product_id")

        matched = None
        if product_id:
            matched = next(
                (item for item in candidates if str(item.get("product_id")) == str(product_id)),
                None,
            )
        if matched is None and data.get("product_code"):
            code = str(data.get("product_code") or data.get("code")).strip()
            matched = next(
                (item for item in candidates if str(item.get("code") or "") == code),
                None,
            )
        if matched is None and name:
            matched = next((item for item in candidates if item["name"] == name), None)

        if not matched:
            logger.warning("GPT가 반환한 상품을 목록에서 찾지 못했습니다. 첫 번째 상품을 사용합니다.")
            matched = candidates[0]

        reason = str(data.get("reason", "")).strip() or "GPT 응답을 확인하세요."
        code = str(data.get("product_code") or data.get("code") or "").strip()
        image_url = data.get("image_url")
        return {
            "product_id": matched.get("product_id"),
            "name": matched["name"],
            "price": matched.get("price"),
            "url": matched.get("url"),
            "code": code or matched.get("code"),
            "image_url": image_url or matched.get("image_url"),
            "site_name": matched.get("site_name"),
            "reason": reason,
        }

    @staticmethod
    def _extract_json(raw: str) -> dict:
        try:
            return json.loads(raw)
        except json.JSONDecodeError:
            pass

        start = raw.find("{")
        end = raw.rfind("}")
        if start != -1 and end != -1:
            snippet = raw[start : end + 1]
            try:
                return json.loads(snippet)
            except json.JSONDecodeError:
                pass

        return {}
