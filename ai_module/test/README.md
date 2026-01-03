# AI Module Tests

이 디렉터리에는 `ai_module` 하위 서비스들을 빠르게 검증하기 위한
간단한 단위 테스트 스위트가 들어 있습니다. 모든 테스트는 외부 API 호출이나
셀레늄 드라이버 실행을 피하기 위해 `unittest.mock` 을 사용합니다.

## 사전 준비

1. `ai_module` 최상위 경로를 `PYTHONPATH`에 포함해야 합니다.
2. 테스트는 파이썬 표준 라이브러리만 사용하므로 별도의 설치는 필요 없습니다.

## 실행 방법

프로젝트 루트에서 아래 명령을 실행하면 됩니다.

```bash
cd ai_module
PYTHONPATH=.. python -m unittest discover -s test
```

또는 개별 테스트만 실행하려면 예를 들어:

```bash
cd ai_module
PYTHONPATH=.. python -m unittest test.test_keyword_selector
```

각 테스트 파일은 특정 서비스에 대한 샘플 입력/모킹을 통해
주요 로직이 예상대로 동작하는지 확인합니다.
