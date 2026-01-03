import unittest
from unittest import mock

from keyword_crawler.pipeline import crawl_keywords


class DummyDriver:
    def __init__(self):
        self.quit_called = False

    def quit(self):
        self.quit_called = True


class KeywordCrawlerTests(unittest.TestCase):
    @mock.patch("keyword_crawler.pipeline.CategoryService")
    @mock.patch("keyword_crawler.pipeline.WebDriverWait")
    @mock.patch("keyword_crawler.pipeline.DriverFactory")
    def test_crawl_keywords_returns_list(
        self, mock_factory, mock_wait, mock_service
    ):
        dummy_driver = DummyDriver()
        mock_factory.return_value.create.return_value = dummy_driver
        mock_service.return_value.crawl.return_value = ["테스트 키워드"]

        keywords = crawl_keywords(
            "패션의류",
            persist_csv=False,
        )
        self.assertEqual(keywords, ["테스트 키워드"])
        self.assertTrue(dummy_driver.quit_called is True)
        mock_service.assert_called()


if __name__ == "__main__":
    unittest.main()
