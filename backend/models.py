from pydantic import BaseModel
from typing import List

class AnalyzeResponse(BaseModel):
    score: int
    sections_found: List[str]
    missing_sections: List[str]
    keywords_matched: List[str]
    keywords_missing: List[str]
    suggestions: List[str]
    skill_gap: List[str]
