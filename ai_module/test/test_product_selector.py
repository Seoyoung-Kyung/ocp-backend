import unittest
from unittest import mock

from product_selector import select_best_product


class ProductSelectorTests(unittest.TestCase):
    @mock.patch("product_selector.selector.ProductSelector._call_gpt")
    def test_select_best_product(self, mock_call):
        mock_call.return_value = (
            '{"product_id": 10, "name": "쿠팡 보온 슬리퍼", '
            '"product_code": "CP-10", "reason": "따뜻함", "price": "19,900원", '
            '"url": "https://example.com", "image_url": "https://img"}'
        )
        products = [
            {
                "productId": 10,
                "productName": "쿠팡 보온 슬리퍼",
                "productCode": "CP-10",
                "productDetailUrl": "https://example.com",
                "productPrice": 19900,
                "productImageUrl": "https://img",
            }
        ]
        result = select_best_product(
            "겨울 슬리퍼",
            products,
            api_key="dummy",
            as_dict=True,
        )
        self.assertEqual(result["product_id"], 10)
        self.assertEqual(result["name"], "쿠팡 보온 슬리퍼")


if __name__ == "__main__":
    unittest.main()
