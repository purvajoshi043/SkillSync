import re
import json
import os
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from models import AnalyzeResponse

# Try importing spacy, gracefully fallback if model is not downloaded yet
try:
    import spacy
    nlp = spacy.load("en_core_web_sm")
except (ImportError, OSError):
    print("Warning: spacy or en_core_web_sm not found. Proceeding with basic regex fallback where possible.")
    nlp = None

# Load keywords data
KEYWORDS_FILE = os.path.join(os.path.dirname(os.path.dirname(__file__)), "data", "keywords.json")
try:
    with open(KEYWORDS_FILE, 'r') as f:
        ROLE_DATA = json.load(f)
except Exception as e:
    print(f"Error loading keywords.json: {e}")
    ROLE_DATA = {}

EXPECTED_SECTIONS = ["Education", "Skills", "Projects", "Experience", "Certifications"]

def detect_sections(text: str) -> tuple[list[str], list[str]]:
    """Detects presence of standard resume sections."""
    found_sections = []
    missing_sections = []
    
    # Basic regex to find headers (usually capitalized, maybe followed by newline or colon)
    text_lower = text.lower()
    
    for section in EXPECTED_SECTIONS:
        # Looking for section names at the start of a line or standing out
        pattern = r"(?i)\b" + section + r"\b"
        if re.search(pattern, text):
            found_sections.append(section)
        else:
            missing_sections.append(section)
            
    return found_sections, missing_sections

def extract_keywords_and_gap(text: str, role: str) -> tuple[list[str], list[str], list[str]]:
    """Extracts keywords, finds matches and gaps based on the role."""
    matched = []
    missing = []
    gap = []
    
    if role not in ROLE_DATA:
        # Fallback if role is not found
        return [], [], []
        
    core_skills = ROLE_DATA[role]["core_skills"]
    text_lower = text.lower()
    
    for skill in core_skills:
        # Boundary match for exact skill names
        pattern = r"\b" + re.escape(skill) + r"\b"
        if re.search(pattern, text_lower):
            matched.append(skill)
        else:
            missing.append(skill)
            gap.append(skill) # Using missing as gap for now
            
    return matched, missing, gap

def count_action_verbs(text: str, role: str) -> int:
    """Counts the occurrence of strong action verbs."""
    if role not in ROLE_DATA:
        return 0
        
    action_verbs = ROLE_DATA[role]["action_verbs"]
    count = 0
    text_lower = text.lower()
    
    # Simple lemmatization or exact match
    for verb in action_verbs:
        pattern = r"\b" + re.escape(verb) + r"\b"
        count += len(re.findall(pattern, text_lower))
        
    return count

def calculate_ml_similarity(resume_text: str, role: str) -> float:
    """Calculates TF-IDF Cosine Similarity between resume and ideal job description keywords."""
    if role not in ROLE_DATA:
        return 0.0
        
    ideal_text = " ".join(ROLE_DATA[role]["core_skills"]) + " " + " ".join(ROLE_DATA[role]["action_verbs"])
    
    try:
        vectorizer = TfidfVectorizer(stop_words='english')
        tfidf_matrix = vectorizer.fit_transform([resume_text, ideal_text])
        similarity = cosine_similarity(tfidf_matrix[0:1], tfidf_matrix[1:2])[0][0]
        return float(similarity)
    except Exception as e:
        print(f"Error calculating ML similarity: {e}")
        return 0.0

def generate_suggestions(found_sections: list[str], missing_sections: list[str], 
                         matched_keywords: list[str], missing_keywords: list[str],
                         action_verb_count: int) -> list[str]:
    """Generates actionable suggestions."""
    suggestions = []
    
    for section in missing_sections:
        suggestions.append(f"Add a dedicated '{section}' section to improve ATS readability.")
        
    if action_verb_count < 5:
        suggestions.append("Increase the use of strong action verbs (e.g., 'developed', 'managed', 'optimized') to describe your impact.")
        
    if len(matched_keywords) < 5 and len(missing_keywords) > 0:
        suggestions.append(f"Your resume lacks key skills for this role. Consider adding: {', '.join(missing_keywords[:5])}.")
        
    if len(suggestions) == 0:
        suggestions.append("Your resume format and keywords look solid! Keep tailoring it to specific job descriptions.")
        
    return suggestions

def analyze_resume(text: str, role: str) -> AnalyzeResponse:
    """Main function to analyze the resume text and generate the response."""
    
    # 1. Section Detection
    found_sections, missing_sections = detect_sections(text)
    
    # 2. Keyword Matching & Skill Gap
    matched_keywords, missing_keywords, skill_gap = extract_keywords_and_gap(text, role)
    
    # 3. Action Verbs
    action_verb_count = count_action_verbs(text, role)
    
    # 4. ML Similarity
    similarity_score = calculate_ml_similarity(text, role)
    
    # 5. Score Calculation
    # Weights: Sections 30%, Keywords 40%, Action Verbs 20%, Length/Format 10%
    
    section_score = len(found_sections) / len(EXPECTED_SECTIONS) if EXPECTED_SECTIONS else 0
    keyword_score = len(matched_keywords) / (len(matched_keywords) + len(missing_keywords)) if (len(matched_keywords) + len(missing_keywords)) > 0 else 0
    
    # Normalize action verb score (cap at 10 verbs)
    action_verb_score = min(action_verb_count / 10.0, 1.0)
    
    # Base score out of 100
    # Combining heuristic rules with the ML similarity score
    base_score = (section_score * 30) + (keyword_score * 40) + (action_verb_score * 20) + (similarity_score * 10)
    
    # Ensure score is within 0-100
    final_score = max(0, min(100, int(base_score)))
    
    # Generate Suggestions
    suggestions = generate_suggestions(found_sections, missing_sections, matched_keywords, missing_keywords, action_verb_count)
    
    return AnalyzeResponse(
        score=final_score,
        sections_found=found_sections,
        missing_sections=missing_sections,
        keywords_matched=matched_keywords,
        keywords_missing=missing_keywords,
        suggestions=suggestions,
        skill_gap=skill_gap
    )
