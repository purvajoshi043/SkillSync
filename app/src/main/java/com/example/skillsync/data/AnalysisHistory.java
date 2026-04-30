package com.example.skillsync.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "analysis_history")
public class AnalysisHistory {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String role;
    public int score;
    public String date;

    public AnalysisHistory(String role, int score, String date) {
        this.role = role;
        this.score = score;
        this.date = date;
    }
}
