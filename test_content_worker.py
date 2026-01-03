#!/usr/bin/env python3
"""
Test script to send a message to content-generate-queue
"""
import json
import pika

# Test message payload (Java camelCase format)
test_message = {
    "workId": 99999,
    "hasCrawledItems": False,
    "recentTrendKeywords": ["AI", "머신러닝"],
    "crawledProducts": None,
    "recentlyUsedProducts": [],
    "webhookSecret": "test-secret-123",
    "webhookUrls": {
        "keywordSelect": "http://host.docker.internal:8080/api/work/webhook/keyword-select",
        "productSelect": "http://host.docker.internal:8080/api/work/webhook/product-select",
        "contentGenerate": "http://host.docker.internal:8080/api/work/webhook/content-generate",
        "airflowLog": "http://host.docker.internal:8080/api/work/webhook/logs"
    },
    "siteUrl": "https://www.coupang.com",
    "trendCategory": {
        "category1": "패션의류/잡화",
        "category2": "남성패션",
        "category3": "캐주얼상의"
    },
    "isTest": True
}

# Connect to RabbitMQ
connection = pika.BlockingConnection(
    pika.ConnectionParameters(
        host='localhost',
        port=5672,
        credentials=pika.PlainCredentials('guest', 'guest')
    )
)
channel = connection.channel()

# Declare queue (ensure it exists)
channel.queue_declare(queue='content-generate-queue', durable=True)

# Publish message
channel.basic_publish(
    exchange='',
    routing_key='content-generate-queue',
    body=json.dumps(test_message).encode('utf-8'),
    properties=pika.BasicProperties(
        delivery_mode=2,  # make message persistent
    )
)

print(f"✅ Test message sent to content-generate-queue")
print(f"   workId: {test_message['workId']}")
print(f"   category: {test_message['trendCategory']}")
print(f"\n📋 Watch logs with: docker compose logs -f content-worker")

connection.close()