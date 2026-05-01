# Resume Analysis API

This is the backend API for the SkillSync app. It parses resumes (PDF/DOCX), analyzes the text using NLP, compares it against job roles, and generates a score and suggestions.

## Prerequisites
- Python 3.9+
- pip

## Setup Instructions

1. **Navigate to the backend directory:**
   ```bash
   cd backend
   ```

2. **Create a virtual environment (Optional but recommended):**
   ```bash
   python -m venv venv
   # Windows
   venv\Scripts\activate
   # Mac/Linux
   source venv/bin/activate
   ```

3. **Install Dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

4. **Download the spaCy NLP model:**
   ```bash
   python -m spacy download en_core_web_sm
   ```

## Running the Server

Start the FastAPI server locally:
```bash
uvicorn app:app --reload
```
The API will be available at `http://localhost:8000`.

## API Endpoints

### `POST /analyze`
Analyzes a resume against a specific job role.

**Request Form Data:**
- `resume`: The file to upload (PDF or DOCX).
- `job_role`: The string role (e.g., "Software Engineer", "Data Analyst", "Web Developer").

**Response (JSON):**
```json
{
  "score": 85,
  "sections_found": ["Education", "Skills", "Experience"],
  "missing_sections": ["Projects", "Certifications"],
  "keywords_matched": ["python", "sql", "git"],
  "keywords_missing": ["docker", "kubernetes"],
  "suggestions": [
    "Add a dedicated 'Projects' section to improve ATS readability."
  ],
  "skill_gap": ["docker", "kubernetes"]
}
```

## Testing Locally
You can test the API by going to `http://localhost:8000/docs` in your browser. This will open the automatically generated Swagger UI where you can upload a file and test the endpoint directly.
