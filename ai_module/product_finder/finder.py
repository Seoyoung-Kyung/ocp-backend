from __future__ import annotations

import logging
from dataclasses import dataclass, asdict
from pathlib import Path
from typing import Iterable, Sequence

from product_finder.product_scraper.product_history import (
    ProductHistory,
    normalize_url,
)
from product_finder.product_scraper.selenium_product_extractor import (
    SeleniumProductExtractor,
)
from product_finder.product_scraper.selenium_product_selector import (
    SeleniumProductSelector,
)
from product_finder.url_refiner import URLRefiner, UnsupportedShoppingMallError

logger = logging.getLogger(__name__)


@dataclass
class ProductFinderResult:
    product_id: str | None
    name: str
    reason: str
    price: str | None = None
    url: str | None = None
    code: str | None = None
    image_url: str | None = None
    site_name: str | None = None

    def to_dict(self) -> dict:
        return asdict(self)


class ProductFinder:
    """
    Selenium 기반 상품 탐색기
    - URLRefiner로 쇼핑몰 검색 URL을 만들고
    - SeleniumProductSelector로 상품 URL을 고른 뒤
    - SeleniumProductExtractor로 상세 정보를 추출한다.
    """

    def __init__(
        self,
        *,
        api_key: str | None = None,
        headless: bool = True,
        history_path: str | Path | None = None,
    ):
        self.headless = headless
        self.api_key = api_key
        self.history_path = (
            Path(history_path)
            if history_path
            else Path(__file__).resolve().parent / "product_history.csv"
        )
        self.history = ProductHistory(csv_path=self.history_path)
        self.url_refiner = URLRefiner(strict_mode=False)
        self.extractor = SeleniumProductExtractor(headless=self.headless)

    def find(
        self,
        *,
        keyword: str,
        site_url: str | None,
        exclude_names: Sequence[str] | None = None,
        max_retries: int = 3,
    ) -> ProductFinderResult:
        if not site_url:
            raise ValueError("site_url이 설정되지 않아 상품을 찾을 수 없습니다.")
        if not keyword:
            raise ValueError("keyword가 필요합니다.")

        search_url, site_label = self._resolve_site_context(site_url, keyword)
        exclude_name_set = {
            self._normalize_name(name)
            for name in (exclude_names or [])
            if isinstance(name, str) and name.strip()
        }
        exclude_urls = set(self.history.get_all_urls())

        selector = SeleniumProductSelector(headless=self.headless)
        try:
            selector.initialize(start_url=site_url)
            for attempt in range(1, max_retries + 1):
                selected = selector.select_product(
                    search_url=search_url,
                    keyword=keyword,
                    exclude_urls=exclude_urls,
                )
                normalized_url = normalize_url(selected.product_url)
                exclude_urls.add(normalized_url)

                detail = self.extractor.extract_product_info(
                    selected.product_url
                ).to_dict()
                product_name = detail.get("name") or selected.product_name or ""
                normalized_name = self._normalize_name(product_name)
                if normalized_name and normalized_name in exclude_name_set:
                    logger.info(
                        "recentlyUsedProducts 목록과 충돌하여 다른 상품을 찾습니다: %s",
                        product_name,
                    )
                    continue

                self.history.add_product(
                    product_url=selected.product_url,
                    keyword=keyword,
                    product_name=product_name,
                )

                code = detail.get("description") or detail.get("brand")
                if selected.brand:
                    code = selected.brand

                result = ProductFinderResult(
                    product_id=code or normalized_url,
                    name=product_name or "추천 상품",
                    price=detail.get("price"),
                    url=selected.product_url,
                    code=code,
                    image_url=detail.get("image_url"),
                    site_name=site_label,
                    reason=selected.reason or "추천 상품",
                )
                return result
        finally:
            try:
                selector.close()
            except Exception as exc:  # pragma: no cover
                logger.warning("Selenium 종료 중 오류: %s", exc)

        raise RuntimeError("상품 정보를 찾지 못했습니다.")

    def _resolve_site_context(self, site_url: str, keyword: str) -> tuple[str, str]:
        """URLRefiner로 검색 URL을 만들고, 실패 시 기본 정보를 반환한다."""
        try:
            result = self.url_refiner.refine(site_url, keyword)
            return result.refined_url, result.shopping_mall
        except UnsupportedShoppingMallError:
            logger.info("지원되지 않는 쇼핑몰: %s", site_url)
        except Exception as exc:  # pragma: no cover
            logger.warning("URL 정제 중 오류(%s): %s", site_url, exc)

        from urllib.parse import urlparse

        parsed_url = urlparse(site_url)
        base = (
            f"{parsed_url.scheme}://{parsed_url.netloc}"
            if parsed_url.scheme and parsed_url.netloc
            else site_url
        )
        label = parsed_url.netloc or parsed_url.path or site_url
        return base, label

    @staticmethod
    def _normalize_name(name: str | None) -> str:
        return name.strip().lower() if isinstance(name, str) else ""
