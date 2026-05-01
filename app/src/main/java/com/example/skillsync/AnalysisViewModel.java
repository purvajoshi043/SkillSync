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
    private final MutableLiveData<String> userName = new MutableLiveData<>("");
    private final MutableLiveData<String> education = new MutableLiveData<>("");
    private final MutableLiveData<String> age = new MutableLiveData<>("");
    private final MutableLiveData<String> location = new MutableLiveData<>("");
    private final MutableLiveData<String> bio = new MutableLiveData<>("");
    private final MutableLiveData<String> selectedFileName = new MutableLiveData<>();
    private final MutableLiveData<android.net.Uri> selectedFileUri = new MutableLiveData<>();
    private final MutableLiveData<List<String>> selectedRoles = new MutableLiveData<>();
    private final MutableLiveData<String> selectedExperience = new MutableLiveData<>("Intermediate");
    private final MutableLiveData<Integer> atsScore = new MutableLiveData<>(null);
    private final MutableLiveData<com.example.skillsync.api.AnalysisResponse> apiResponse = new MutableLiveData<>(null);

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
    public LiveData<String> getUserName() { return userName; }
    public LiveData<String> getEducation() { return education; }
    public LiveData<String> getAge() { return age; }
    public LiveData<String> getLocation() { return location; }
    public LiveData<String> getBio() { return bio; }
    public LiveData<String> getSelectedFileName() { return selectedFileName; }
    public LiveData<android.net.Uri> getSelectedFileUri() { return selectedFileUri; }
    public LiveData<List<String>> getSelectedRoles() { return selectedRoles; }
    public LiveData<String> getSelectedExperience() { return selectedExperience; }
    public LiveData<Integer> getAtsScore() { return atsScore; }
    public LiveData<com.example.skillsync.api.AnalysisResponse> getApiResponse() { return apiResponse; }
    public LiveData<List<AnalysisHistory>> getAllHistory() { return allHistory; }

    // --- Setters ---
    public void setUserName(String name) { userName.setValue(name); }
    public void setEducation(String edu) { education.setValue(edu); }
    public void setAge(String val) { age.setValue(val); }
    public void setLocation(String val) { location.setValue(val); }
    public void setBio(String val) { bio.setValue(val); }
    public void setSelectedFileName(String name) { selectedFileName.setValue(name); }
    public void setSelectedFileUri(android.net.Uri uri) { selectedFileUri.setValue(uri); }
    public void setSelectedRoles(List<String> roles) { selectedRoles.setValue(roles); }
    public void setSelectedExperience(String exp) { selectedExperience.setValue(exp); }
    public void setAtsScore(Integer score) { atsScore.setValue(score); }
    public void setApiResponse(com.example.skillsync.api.AnalysisResponse response) { apiResponse.setValue(response); }

    // --- DB Operations ---
    public void insertHistory(AnalysisHistory history) {
        executorService.execute(() -> analysisDao.insert(history));
    }
}
