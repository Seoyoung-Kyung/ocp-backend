"""
제목 생성 모듈
AI2 담당
"""
import logging
import re
import time
from typing import List, Optional

from content_generator.gpt_utils import generate_with_prompt

logger = logging.getLogger("content_pipeline")


def generate_titles(
    product_info: str = "",
    max_retries: int = 3,
    api_key: Optional[str] = None,
) -> List[str]:
    """
    GPT로 제목 리스트 생성 (재시도 로직 포함)
    
    Args:
        product_info: 상품 정보 텍스트
        max_retries: 최대 재시도 횟수 (기본값: 3)
        
    Returns:
        생성된 제목 리스트 (최소 2개 이상)
    """
    info_preview = product_info[:80] + ("..." if len(product_info) > 80 else "")
    logger.info("제목 생성 시작 - 상품 정보: %s", info_preview or "미입력")
    
    for attempt in range(max_retries):
        try:
            # GPT 호출
            response = generate_with_prompt(
                prompt_name="title_prompt",
                user_data={
                    "상품 정보": product_info if product_info else "없음"
                },
                temperature=0.8,  # 다양성을 위해 조금 높게
                max_retries=3,
                api_key=api_key,
            )
            
            if not response:
                logger.warning(f"GPT 응답 없음. 재시도 ({attempt+1}/{max_retries})")
                if attempt < max_retries - 1:
                    time.sleep(1)  # 1초 대기 후 재시도
                continue
            
            # GPT 원본 응답 로그
            logger.debug(f"GPT 원본 응답 (시도 {attempt+1}):\n{response[:500]}...")
            
            # bullet 형태로 파싱
            titles = parse_titles_from_response(response)
            logger.info(f"파싱된 제목 {len(titles)}개: {titles}")
            
            # 제목이 2개 이상이고, 최소 1개는 유효한지 검증
            if len(titles) >= 2:
                logger.info(f"제목 {len(titles)}개 생성 완료")
                return titles
            else:
                logger.warning(
                    f"제목 {len(titles)}개만 생성됨 (최소 2개 필요) - 재시도 ({attempt+1}/{max_retries})"
                )
            
            # 재시도 전 대기
            if attempt < max_retries - 1:
                time.sleep(1)
                
        except Exception as e:
            logger.error(f"제목 생성 중 예외 발생: {e}. 재시도 ({attempt+1}/{max_retries})")
            if attempt < max_retries - 1:
                time.sleep(1)
            continue
    
    logger.error(f"제목 생성 실패: 최대 재시도 횟수({max_retries}) 초과")
    return []


def parse_titles_from_response(response: str) -> List[str]:
    """
    GPT 응답에서 제목 리스트 추출
    
    Args:
        response: GPT 응답 텍스트
        
    Returns:
        제목 리스트
    """
    titles = []
    lines = response.strip().split('\n')
    
    for line in lines:
        line = line.strip()
        
        # 빈 줄 스킵
        if not line:
            continue
        
        # bullet 제거 (-, •, *, 숫자 등)
        line = re.sub(r'^[-•*]\s*', '', line)  # -, •, * 제거
        line = re.sub(r'^\d+[.)]\s*', '', line)  # 1., 2) 등 제거
        line = re.sub(r'^[가-힣]+[.)]\s*', '', line)  # 가), 나) 등 제거
        
        # 최소 길이 체크 (10자 이상)
        if len(line) >= 10:
            titles.append(line)
    
    # 최대 5개만 반환
    return titles[:5]


def select_best_title(titles: List[str]) -> str:
    """
    생성된 제목 중 최적 제목 선택
    """
    if not titles:
        logger.warning("선택할 제목이 없습니다.")
        return ""

    forbidden_words = [
        "대박",
        "역대급",
        "초특가",
        "완판",
        "폭발적",
        "지금 구매",
        "한정",
        "서둘러",
        "파격",
        "할인",
        "쿠폰",
        "에 대해 알아보자",
        "완벽 가이드",
        "모든 것",
        "정리해드립니다",
        "살펴보기",
    ]

    def _score(title: str) -> int:
        score = 0
        length = len(title.strip())
        if 26 <= length <= 34:
            score += 15
        elif 22 <= length <= 38:
            score += 6
        else:
            score -= 12

        if re.search(r"\d", title):
            score += 4

        if "?" in title or "어떻게" in title or "왜" in title:
            score += 3

        if any(word in title for word in forbidden_words):
            score -= 50

        if "  " in title:
            score -= 5

        if title.count(" ") < 3:
            score -= 4

        return score

    scored_titles = [(title, _score(title)) for title in titles]
    scored_titles.sort(key=lambda x: x[1], reverse=True)
    best_title, best_score = scored_titles[0]

    logger.info(
        "최적 제목 선택: %s (점수: %s, 길이: %s자)",
        best_title,
        best_score,
        len(best_title),
    )
    logger.info("제목 점수 평가 결과 (총 %s개):", len(scored_titles))
    for idx, (title, score) in enumerate(scored_titles, start=1):
        logger.info("  %s. [%s점] %s", idx, score, title)

    return best_title


def create_fallback_title(product_info: str = "") -> str:
    """
    모든 시도 실패 시 기본 제목 생성 (폴백 메커니즘)
    """
    base = ""
    if product_info:
        first_segment = product_info.split("/")[0]
        base = (
            first_segment.replace("상품명:", "")
            .replace("제품명:", "")
            .replace("대표 상품:", "")
            .strip()
        )
    if not base:
        base = "프리미엄 제품"

    fallback_title = f"{base} 핵심 포인트 3가지"
    logger.warning("폴백 제목 생성: %s", fallback_title)
    return fallback_title


def _is_valid_title(title: str) -> bool:
    if not title:
        return False
    length = len(title.strip())
    return 24 <= length <= 40


def generate_title(
    product_info: str = "",
    max_retries: int = 3,
    api_key: Optional[str] = None,
) -> Optional[str]:
    """
    제목 생성 통합 함수 (전체 프로세스 재시도 포함)
    """
    for attempt in range(max_retries):
        try:
            titles = generate_titles(
                product_info,
                max_retries=3,
                api_key=api_key,
            )

            if not titles:
                logger.warning(
                    "제목 리스트 생성 실패. 재시도 (%s/%s)", attempt + 1, max_retries
                )
                if attempt < max_retries - 1:
                    time.sleep(2)
                continue

            logger.info("제목 리스트 생성 성공: %s개 제목", len(titles))
            logger.info("최적 제목 선택 시작...")
            best_title = select_best_title(titles)

            if not _is_valid_title(best_title):
                logger.warning(
                    "선택된 제목이 길이/품질 조건을 만족하지 못했습니다. 재시도 (%s/%s)",
                    attempt + 1,
                    max_retries,
                )
                if attempt < max_retries - 1:
                    time.sleep(2)
                continue

            logger.info("제목 생성 성공: %s", best_title)
            return best_title

        except Exception as e:  # noqa: BLE001
            logger.error(
                "제목 생성 프로세스 중 예외 발생: %s. 재시도 (%s/%s)",
                e,
                attempt + 1,
                max_retries,
            )
            if attempt < max_retries - 1:
                time.sleep(2)
            continue

    logger.error("제목 생성 최종 실패: 폴백 제목 사용")
    return create_fallback_title(product_info)

