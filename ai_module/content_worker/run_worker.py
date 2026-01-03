"""
Content Worker entry point.

Run this module to start the RabbitMQ consumer for content generation.
"""
from .rabbit_consumer import run_consumer

if __name__ == "__main__":
    run_consumer()
