from __future__ import annotations

from pathlib import Path

from selenium.webdriver.support.ui import WebDriverWait

from keyword_crawler.infra.driver_factory import DriverFactory
from keyword_crawler.infra.extractors import KeywordExtractor
from keyword_crawler.infra.selenium_actions import Actions
from keyword_crawler.repositories.csv_repository import CSVKeywordRepository
from keyword_crawler.services.category_service import CategoryService


PROJECT_ROOT = Path(__file__).resolve().parents[1]
DEFAULT_OUTPUT_PATH = PROJECT_ROOT / "keywords.csv"


def crawl_keywords(
    c1: str,
    c2: str | None = None,
    c3: str | None = None,
    *,
    output_path: Path | None = None,
    headless: bool = False,
    wait_timeout: int = 15,
    delay: float = 0.3,
    persist_csv: bool = True,
) -> list[str]:
    """
    Itemscout 카테고리 페이지에서 키워드를 추출해 리스트로 반환한다.

    Airflow 등에서 CSV 저장이 필요 없을 경우 `persist_csv=False`로 호출하면
    메모리 내 리스트만 사용 가능하다.
    """

    if not c1:
        raise ValueError("최소한 1차 카테고리는 지정해야 합니다.")

    repository = None
    target_output = output_path or DEFAULT_OUTPUT_PATH
    if persist_csv:
        repository = CSVKeywordRepository(default_path=target_output)

    driver = None
    try:
        factory = DriverFactory(headless=headless)
        driver = factory.create()

        wait = WebDriverWait(driver, wait_timeout)
        actions = Actions(driver, wait)
        extractor = KeywordExtractor(driver, wait)
        service = CategoryService(
            driver,
            wait,
            actions,
            extractor,
            repository=repository,
            delay=delay,
        )
        categories = (c1, c2, c3)
        output_arg = repository.default_path if repository else None
        return service.crawl(categories, output_path=output_arg)
    finally:
        if driver:
            driver.quit()
