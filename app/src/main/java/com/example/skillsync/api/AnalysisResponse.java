package com.example.skillsync.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AnalysisResponse {
    @SerializedName("score")
    private int score;

    @SerializedName("sections_found")
    private List<String> sectionsFound;

    @SerializedName("missing_sections")
    private List<String> missingSections;

    @SerializedName("keywords_matched")
    private List<String> keywordsMatched;

    @SerializedName("keywords_missing")
    private List<String> keywordsMissing;

    @SerializedName("suggestions")
    private List<String> suggestions;

    @SerializedName("skill_gap")
    private List<String> skillGap;

    public int getScore() {
        return score;
    }

    public List<String> getSectionsFound() {
        return sectionsFound;
    }

    public List<String> getMissingSections() {
        return missingSections;
    }

    public List<String> getKeywordsMatched() {
        return keywordsMatched;
    }

    public List<String> getKeywordsMissing() {
        return keywordsMissing;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public List<String> getSkillGap() {
        return skillGap;
    }
}
