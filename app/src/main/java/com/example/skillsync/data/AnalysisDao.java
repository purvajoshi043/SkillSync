package com.example.skillsync.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AnalysisDao {
    @Insert
    void insert(AnalysisHistory history);

    @Query("SELECT * FROM analysis_history ORDER BY id DESC")
    LiveData<List<AnalysisHistory>> getAllHistory();
}
