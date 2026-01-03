
import random
import time
from typing import Optional

from selenium import webdriver
from selenium.webdriver.common.action_chains import ActionChains

from keyword_crawler.infra.driver_factory import DriverFactory


class SeleniumDriverFactory:
    """
    Selenium 드라이버 생성기 (keyword_crawler DriverFactory 활용)
    """

    def __init__(self, headless: bool = False, user_data_dir: str | None = None):
        self.headless = headless
        self.user_data_dir = user_data_dir
        self.driver: Optional[webdriver.Chrome] = None
        self._driver_factory = DriverFactory(headless=headless)

    def create_driver(self) -> webdriver.Chrome:
        """Chrome 드라이버 생성"""
        self.driver = self._driver_factory.create()
        return self.driver

    def natural_goto(self, url: str, visit_home_first: bool = None):
        """자연스러운 페이지 방문"""
        if not self.driver:
            raise RuntimeError("드라이버가 초기화되지 않았습니다")

        if visit_home_first is None:
            visit_home_first = random.random() < 0.3

        if visit_home_first:
            base_url = '/'.join(url.split('/')[:3])
            self.driver.get(base_url)
            self._natural_pause(2, 4)
            self._random_mouse_movement()

        self.driver.get(url)
        self._natural_pause(3, 6)
        self._random_mouse_movement()

        self._natural_scroll()

    def _natural_scroll(self):
        """사람처럼 자연스럽게 스크롤"""
        if not self.driver:
            return

        scroll_count = random.randint(2, 4)
        for _ in range(scroll_count):
            scroll_amount = random.randint(300, 700)
            chunks = random.randint(3, 7)

            for i in range(chunks):
                chunk_size = scroll_amount // chunks
                self.driver.execute_script(f'window.scrollBy(0, {chunk_size})')
                time.sleep(random.uniform(0.1, 0.3))

            self._natural_pause(1, 3)

        # 가끔 위로 스크롤
        if random.random() < 0.3:
            self.driver.execute_script(f'window.scrollBy(0, -{random.randint(50, 150)})')
            self._natural_pause(0.5, 1.5)

    def _random_mouse_movement(self):
        """랜덤 마우스 움직임 (사람처럼)"""
        if not self.driver:
            return

        try:
            # 화면 크기 가져오기
            window_size = self.driver.get_window_size()
            width = window_size['width']
            height = window_size['height']

            # 시작점을 화면 중앙으로 옮겨 음수 오프셋을 방지
            center_x, center_y = width // 2, height // 2
            ActionChains(self.driver).move_by_offset(center_x, center_y).perform()
            current_x, current_y = center_x, center_y

            # 랜덤 좌표로 마우스 이동 (여러 번)
            movements = random.randint(2, 4)
            for _ in range(movements):
                target_x = random.randint(100, width - 100)
                target_y = random.randint(100, height - 100)

                ActionChains(self.driver).move_by_offset(
                    target_x - current_x,
                    target_y - current_y,
                ).perform()

                current_x, current_y = target_x, target_y
                time.sleep(random.uniform(0.1, 0.3))
        except Exception:
            # 마우스 이동 실패해도 계속 진행
            pass

    def _natural_pause(self, min_sec: float = 0.5, max_sec: float = 2.0):
        """랜덤 딜레이"""
        time.sleep(random.uniform(min_sec, max_sec))

    def close(self):
        """리소스 정리 (쿠키 저장 포함)"""
        if not self.driver:
            return

        try:
            self.driver.quit()
            print("✅ 드라이버 종료 완료")
        except Exception as e:
            print(f"드라이버 종료 중 오류: {e}")
