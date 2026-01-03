# content_generator/openai_client.py

import os
from openai import OpenAI
from common.utils import load_env_from_default_locations


class OpenAIClient:
    def __init__(self, api_key: str | None = None):
        if not api_key:
            load_env_from_default_locations()
        key = api_key or os.getenv("OPENAI_API_KEY")
        if not key:
            raise ValueError("OPENAI_API_KEY가 설정되지 않았습니다.")
        self.client = OpenAI(api_key=key)

    def generate_text(
        self, system_prompt, user_prompt, model="gpt-4o-mini", temperature=0.7
    ):
        response = self.client.chat.completions.create(
            model=model,
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt},
            ],
            temperature=temperature,
        )
        return response.choices[0].message.content
