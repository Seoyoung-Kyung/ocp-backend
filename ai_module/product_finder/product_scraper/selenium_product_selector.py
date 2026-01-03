
import time
import random
import re
from typing import List, Set, Optional
from dataclasses import dataclass
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from openai import OpenAI
import os
from pathlib import Path

from product_finder.product_scraper.product_history import normalize_url
from product_finder.product_scraper.selenium_driver_factory import SeleniumDriverFactory
from product_finder.product_scraper.shopping_mall_config import ShoppingMallManager


@dataclass
class SelectedProduct:
    """랜덤 선택된 상품 (호환성 유지)"""
    product_name: str = ""
    product_url: str = ""
    reason: str = "랜덤 선택 (Selenium)"
    brand: Optional[str] = None
    price: Optional[str] = None


class SeleniumProductSelector:
    """
    Selenium 기반 상품 선택기
    PlaywrightProductSelector와 동일한 기능을 Selenium으로 구현
    """

    def __init__(self, openai_api_key: str = None, headless: bool = False, user_data_dir: str = ".selenium_data"):
        self.driver_factory = SeleniumDriverFactory(headless=headless, user_data_dir=user_data_dir)
        self.driver = None
        self.mall_manager = ShoppingMallManager()

        # OpenAI 클라이언트 (필요 시)
        if openai_api_key:
            self.client = OpenAI(api_key=openai_api_key)
        else:
            self.client = None

    def initialize(self, start_url: Optional[str] = None):
        """드라이버 초기화"""
        self.driver = self.driver_factory.create_driver()

        # 입력된 URL을 기준으로 초기 방문 도메인을 결정한다. (기본: 쿠팡)
        target = start_url or "https://www.coupang.com"
        mall = self.mall_manager.get_mall_by_domain(target)
        if mall:
            # 검색 URL을 받더라도 base_url(없으면 도메인)로 이동
            target = mall.base_url or f"https://{mall.domain}"

        print(f"초기 방문: {target}")
        self.driver.get(target)
        print("⏰ 봇 탐지 우회를 위해 15-20초 대기 중...")
        time.sleep(random.uniform(15, 20))

    def select_product(
        self,
        search_url: str,
        keyword: str,
        exclude_urls: Set[str],
        max_retries: int = 3
    ) -> SelectedProduct:
        """
        검색 결과에서 랜덤으로 상품 URL 선택

        Args:
            search_url: 검색 결과 페이지 URL
            keyword: 검색 키워드 (로깅용)
            exclude_urls: 제외할 URL 목록
            max_retries: 재시도 횟수

        Returns:
            SelectedProduct: 선택된 상품
        """
        print(f"상품 URL 랜덤 선택 시작: {keyword}")

        for attempt in range(max_retries):
            try:
                # 1. 페이지 방문 (자연스럽게)
                self.driver_factory.natural_goto(search_url)

                # 2. 상품 링크 로딩 대기 (타임아웃 증가)
                try:
                    WebDriverWait(self.driver, 30).until(
                        EC.presence_of_element_located((By.TAG_NAME, "a"))
                    )
                except TimeoutException:
                    raise TimeoutException("상품 링크 로딩 실패 (30초 타임아웃)")

                # 3. 스크롤 (상품 로딩)
                self._natural_scroll()

                # 4. URL 추출
                product_urls = self._extract_product_urls()
                print(f"총 {len(product_urls)}개 URL 추출")
                if product_urls:
                    print("예시 URL (상위 5개):")
                    for u in product_urls[:5]:
                        print(f"  - {u}")

                # 5. 중복 제거
                filtered_urls = [
                    url for url in product_urls
                    if normalize_url(url) not in exclude_urls
                ]
                print(f"중복 제거 후: {len(filtered_urls)}개")
                if filtered_urls:
                    print("사용 가능한 예시 (상위 5개):")
                    for u in filtered_urls[:5]:
                        print(f"  - {u}")
                else:
                    print("필터 후 남은 URL이 없습니다. 제외 목록과 추출 URL을 확인하세요.")

                if not filtered_urls:
                    raise ValueError("사용 가능한 상품 URL이 없습니다")

                # 6. 랜덤 선택
                selected_url = random.choice(filtered_urls)
                print(f"랜덤 선택된 URL: {selected_url}")

                return SelectedProduct(
                    product_name="",  # 나중에 GPTProductExtractor에서 추출
                    product_url=selected_url,
                    reason="랜덤 선택 (Selenium 스텔스)",
                    brand=None,
                    price=None
                )

            except Exception as e:
                print(f"시도 {attempt + 1}/{max_retries} 실패: {e}")

                # 에러 발생 시 스크린샷 저장
                try:
                    from datetime import datetime
                    os.makedirs("debug_screenshots", exist_ok=True)
                    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
                    screenshot_path = f"debug_screenshots/error_{timestamp}_attempt{attempt+1}.png"
                    self.driver.save_screenshot(screenshot_path)
                    print(f"📸 에러 스크린샷 저장: {screenshot_path}")

                    # 페이지 소스도 저장
                    html_path = f"debug_screenshots/error_{timestamp}_attempt{attempt+1}.html"
                    with open(html_path, 'w', encoding='utf-8') as f:
                        f.write(self.driver.page_source)
                    print(f"📄 페이지 HTML 저장: {html_path}")
                except Exception as screenshot_error:
                    print(f"스크린샷 저장 실패: {screenshot_error}")

                if attempt == max_retries - 1:
                    raise
                time.sleep(2)

        raise RuntimeError("상품 URL 선택 실패")

    def _natural_scroll(self):
        """자연스러운 스크롤 (상품 로딩)"""
        # 아래로 스크롤
        self.driver.execute_script('window.scrollTo(0, 500);')
        time.sleep(random.uniform(0.5, 1.5))

        # 위로 스크롤
        self.driver.execute_script('window.scrollTo(0, 0);')
        time.sleep(random.uniform(0.5, 1))

    def _extract_product_urls(self) -> List[str]:
        """HTML에서 상품 URL만 추출"""
        urls = []

        # 현재 URL로 쇼핑몰 감지
        current_url = self.driver.current_url
        mall = self.mall_manager.get_mall_by_domain(current_url)

        if not mall:
            print(f"알 수 없는 쇼핑몰: {current_url}")
            return urls

        print(f"쇼핑몰 감지: {mall.name} (URL: {current_url})")

        # 모든 링크 가져오기
        all_links = self.driver.find_elements(By.TAG_NAME, 'a')
        print(f"총 <a> 태그: {len(all_links)}개")

        for link in all_links:
            try:
                href = link.get_attribute('href')
                if not href:
                    continue

                # 절대 URL로 변환
                if href.startswith('http'):
                    pass
                elif href.startswith('/'):
                    href = f"{mall.base_url}{href}"
                else:
                    continue

                # 상품 URL인지 확인
                if self._is_product_url_for_mall(href, mall):
                    urls.append(href)

            except:
                continue

        # 중복 제거
        seen = set()
        unique_urls = []
        for url in urls:
            normalized = self._normalize_url(url)
            if normalized not in seen:
                seen.add(normalized)
                unique_urls.append(url)

        print(f"상품 URL 추출 완료: {len(unique_urls)}개")
        return unique_urls

    def _is_product_url_for_mall(self, url: str, mall) -> bool:
        """특정 쇼핑몰의 상품 URL인지 확인"""
        if not url or not isinstance(url, str):
            return False

        # 제외 패턴 (대소문자 무시)
        exclude_patterns = [
            'javascript:', 'mailto:', '#',
            '/cart', '/login', '/signup', '/mypage',
            '/event', '/magazine', '/brand', '/category',
        ]

        url_lower = url.lower()

        for pattern in exclude_patterns:
            if pattern in url_lower:
                return False

        # product_patterns 검사 (쇼핑몰 정의 그대로, 대소문자 유지)
        if mall.product_patterns:
            for pattern in mall.product_patterns:
                if pattern in url:
                    return True

        return False

    def _normalize_url(self, url: str) -> str:
        """URL 정규화"""
        if '#' in url:
            url = url.split('#')[0]
        return url.rstrip('/')

    def close(self):
        """리소스 정리"""
        if self.driver_factory:
            self.driver_factory.close()
