import argparse
import logging
from pathlib import Path

from keyword_crawler.pipeline import DEFAULT_OUTPUT_PATH, crawl_keywords


def parse_args(args=None):
    parser = argparse.ArgumentParser(
        description="Itemscout 인기 키워드 크롤러",
    )
    parser.add_argument("--c1", required=True, help="1차 카테고리")
    parser.add_argument("--c2", help="2차 카테고리")
    parser.add_argument("--c3", help="3차 카테고리")
    parser.add_argument(
        "-o",
        "--output",
        type=Path,
        default=DEFAULT_OUTPUT_PATH,
        help=f"저장할 CSV 파일 경로 (기본: {DEFAULT_OUTPUT_PATH})",
    )
    parser.add_argument(
        "--headless",
        action="store_true",
        help="브라우저를 headless 모드로 실행",
    )
    parser.add_argument(
        "--wait-timeout",
        type=int,
        default=15,
        help="명시적 대기 타임아웃 (초)",
    )
    parser.add_argument(
        "--delay",
        type=float,
        default=0.3,
        help="카테고리 선택 사이의 지연 시간 (초)",
    )
    parser.add_argument(
        "--log-level",
        default="INFO",
        choices=["DEBUG", "INFO", "WARNING", "ERROR", "CRITICAL"],
        help="로그 레벨",
    )
    parser.add_argument(
        "--no-save",
        action="store_true",
        help="CSV 저장을 생략하고 키워드 리스트만 출력",
    )

    return parser.parse_args(args=args)


def main_cli(args=None):
    cli_args = parse_args(args)
    logging.basicConfig(
        level=getattr(logging, cli_args.log_level),
        format="%(asctime)s [%(levelname)s] %(message)s",
    )

    keywords = crawl_keywords(
        cli_args.c1,
        cli_args.c2,
        cli_args.c3,
        output_path=cli_args.output,
        headless=cli_args.headless,
        wait_timeout=cli_args.wait_timeout,
        delay=cli_args.delay,
        persist_csv=not cli_args.no_save,
    )
    print("키워드:", keywords)
    return 0
