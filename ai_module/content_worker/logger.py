"""
Logger configuration for the content worker.
"""
from __future__ import annotations

import logging
import os
import sys
from pathlib import Path


def setup_logger() -> logging.Logger:
    """
    Configure and return the content worker logger.
    """
    logger = logging.getLogger("content_worker")

    # Set log level from environment variable
    log_level = os.getenv("WORKER_LOG_LEVEL", "INFO").upper()
    logger.setLevel(log_level)

    # Prevent propagation to avoid duplicate logs
    logger.propagate = False

    # Remove existing handlers
    logger.handlers.clear()

    # Console handler with formatted output
    console_handler = logging.StreamHandler(sys.stdout)
    console_handler.setLevel(log_level)

    # Formatter
    formatter = logging.Formatter(
        "[%(asctime)s] %(levelname)s - %(name)s - %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S"
    )
    console_handler.setFormatter(formatter)
    logger.addHandler(console_handler)

    # Optional file handler
    log_dir = os.getenv("WORKER_LOG_DIR")
    if log_dir:
        log_path = Path(log_dir)
        log_path.mkdir(parents=True, exist_ok=True)

        file_handler = logging.FileHandler(
            log_path / "content_worker.log",
            encoding="utf-8"
        )
        file_handler.setLevel(log_level)
        file_handler.setFormatter(formatter)
        logger.addHandler(file_handler)

    return logger


# Create module-level logger instance
logger = setup_logger()
