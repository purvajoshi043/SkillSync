package com.example.skillsync.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ResumeApi {
    @Multipart
    @POST("/analyze")
    Call<AnalysisResponse> analyzeResume(
            @Part MultipartBody.Part resume,
            @Part("job_role") RequestBody jobRole
    );
}
