import unittest
from unittest import mock

from content_generator import generate_blog_content


class ContentGeneratorTests(unittest.TestCase):
    @mock.patch("content_generator.body_generator.generate_body")
    @mock.patch("content_generator.title_generator.generate_title")
    def test_generate_blog_content(self, mock_title, mock_body):
        mock_title.return_value = "겨울 슬리퍼 추천"
        mock_body.return_value = ("<p>본문</p>", "h2: 목차")

        content = generate_blog_content(
            product_info="상품명: 쿠팡 슬리퍼",
            product_url="https://example.com/detail",
            product_image_url="https://example.com/image.jpg",
            api_key="dummy",
        )
        self.assertIsNotNone(content)
        self.assertEqual(content["title"], "겨울 슬리퍼 추천")
        self.assertIn("<p>본문</p>", content["body"])
        self.assertEqual(content["outline"], "h2: 목차")


if __name__ == "__main__":
    unittest.main()
