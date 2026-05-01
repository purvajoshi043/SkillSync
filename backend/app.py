from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import os

from models import AnalyzeResponse
from services.parser import extract_text_from_pdf, extract_text_from_docx
from services.analyzer import analyze_resume

app = FastAPI(title="Resume Analysis API", description="API for parsing and scoring resumes")

# Enable CORS for the Android app
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allows all origins, adjust in production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.post("/analyze", response_model=AnalyzeResponse)
async def analyze_endpoint(
    resume: UploadFile = File(...),
    job_role: str = Form(...)
):
    if not resume.filename:
        raise HTTPException(status_code=400, detail="No file uploaded")
        
    # Read file content
    file_bytes = await resume.read()
    
    # Parse text based on extension
    filename = resume.filename.lower()
    extracted_text = ""
    
    if filename.endswith(".pdf"):
        extracted_text = extract_text_from_pdf(file_bytes)
    elif filename.endswith(".docx"):
        extracted_text = extract_text_from_docx(file_bytes)
    else:
        raise HTTPException(status_code=400, detail="Unsupported file format. Please upload PDF or DOCX.")
        
    if not extracted_text.strip():
         raise HTTPException(status_code=400, detail="Could not extract text from the document.")

    # Analyze resume
    result = analyze_resume(extracted_text, job_role)
    
    return result

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
