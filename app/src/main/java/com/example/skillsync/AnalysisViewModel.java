package com.example.skillsync;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.skillsync.data.AnalysisDao;
import com.example.skillsync.data.AnalysisHistory;
import com.example.skillsync.data.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalysisViewModel extends AndroidViewModel {

    // Shared state for the current analysis session
    private final MutableLiveData<String> selectedFileName = new MutableLiveData<>();
    private final MutableLiveData<String> selectedRole = new MutableLiveData<>("Data Scientist");
    private final MutableLiveData<String> selectedExperience = new MutableLiveData<>("Intermediate");
    private final MutableLiveData<Integer> atsScore = new MutableLiveData<>(0);

    // Room DB
    private final AnalysisDao analysisDao;
    private final LiveData<List<AnalysisHistory>> allHistory;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public AnalysisViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        analysisDao = db.analysisDao();
        allHistory = analysisDao.getAllHistory();
    }

    // --- Getters ---
    public LiveData<String> getSelectedFileName() { return selectedFileName; }
    public LiveData<String> getSelectedRole() { return selectedRole; }
    public LiveData<String> getSelectedExperience() { return selectedExperience; }
    public LiveData<Integer> getAtsScore() { return atsScore; }
    public LiveData<List<AnalysisHistory>> getAllHistory() { return allHistory; }

    // --- Setters ---
    public void setSelectedFileName(String name) { selectedFileName.setValue(name); }
    public void setSelectedRole(String role) { selectedRole.setValue(role); }
    public void setSelectedExperience(String exp) { selectedExperience.setValue(exp); }
    public void setAtsScore(int score) { atsScore.setValue(score); }

    // --- DB Operations ---
    public void insertHistory(AnalysisHistory history) {
        executorService.execute(() -> analysisDao.insert(history));
    }
}
