"""
Data models for Content Worker.

Handles Java camelCase → Python snake_case conversion for RabbitMQ messages.
"""
from __future__ import annotations

import json
from dataclasses import dataclass
from typing import List, Optional


@dataclass
class ProductInfo:
    product_id: Optional[int]
    product_name: str
    product_code: str
    product_detail_url: str
    product_price: Optional[int]
    product_image_url: str

    @classmethod
    def from_dict(cls, data: dict) -> ProductInfo:
        return cls(
            product_id=data.get("productId"),
            product_name=data["productName"],
            product_code=data["productCode"],
            product_detail_url=data["productDetailUrl"],
            product_price=data.get("productPrice"),
            product_image_url=data["productImageUrl"]
        )


@dataclass
class TrendCategory:
    category1: str
    category2: Optional[str]
    category3: Optional[str]

    @classmethod
    def from_dict(cls, data: dict) -> TrendCategory:
        return cls(
            category1=data["category1"],
            category2=data.get("category2"),
            category3=data.get("category3")
        )


@dataclass
class WebhookUrls:
    keyword_select: str
    product_select: str
    content_generate: str
    airflow_log: Optional[str]

    @classmethod
    def from_dict(cls, data: dict) -> WebhookUrls:
        return cls(
            keyword_select=data["keywordSelect"],
            product_select=data["productSelect"],
            content_generate=data["contentGenerate"],
            airflow_log=data.get("airflowLog")
        )


@dataclass
class ContentGenerateRequest:
    work_id: int
    has_crawled_items: bool
    recent_trend_keywords: List[str]
    crawled_products: Optional[List[ProductInfo]]
    recently_used_products: Optional[List[str]]
    webhook_secret: str
    webhook_urls: WebhookUrls
    site_url: str
    trend_category: TrendCategory
    is_test: bool

    @classmethod
    def from_json(cls, body: bytes) -> ContentGenerateRequest:
        """
        Parse JSON message from RabbitMQ (Java camelCase format).

        Args:
            body: Raw message bytes from RabbitMQ

        Returns:
            Parsed ContentGenerateRequest object

        Raises:
            json.JSONDecodeError: If body is not valid JSON
            KeyError: If required fields are missing
        """
        data = json.loads(body.decode('utf-8'))

        # Parse nested objects
        trend_category = TrendCategory.from_dict(data["trendCategory"])
        webhook_urls = WebhookUrls.from_dict(data["webhookUrls"])

        # Parse crawled products if present
        crawled_products = None
        if data.get("crawledProducts"):
            crawled_products = [
                ProductInfo.from_dict(p)
                for p in data["crawledProducts"]
            ]

        return cls(
            work_id=data["workId"],
            has_crawled_items=data.get("hasCrawledItems", False),
            recent_trend_keywords=data.get("recentTrendKeywords", []),
            crawled_products=crawled_products,
            recently_used_products=data.get("recentlyUsedProducts"),
            webhook_secret=data["webhookSecret"],
            webhook_urls=webhook_urls,
            site_url=data["siteUrl"],
            trend_category=trend_category,
            is_test=data.get("isTest", False)
        )
