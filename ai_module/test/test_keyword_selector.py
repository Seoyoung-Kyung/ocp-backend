import unittest
from unittest import mock

from keyword_selector import select_best_keyword


class KeywordSelectorTests(unittest.TestCase):
    @mock.patch("keyword_selector.selector.KeywordSelector._call_gpt")
    def test_select_best_keyword(self, mock_call):
        mock_call.return_value = '{"keyword": "따뜻한 슬리퍼", "reason": "겨울 수요에 맞음"}'
        result = select_best_keyword(
            ["따뜻한 슬리퍼", "여름 샌들"],
            api_key="dummy",
            exclude_keywords=["여름 샌들"],
            as_dict=True,
        )
        self.assertEqual(result["keyword"], "따뜻한 슬리퍼")
        self.assertIn("reason", result)


if __name__ == "__main__":
    unittest.main()
