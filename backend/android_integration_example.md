# Android Integration (Retrofit Example)

This file demonstrates how to connect your Android App to the new Python API using Retrofit.

## 1. Add Retrofit Dependencies (`build.gradle.kts` app level)

Ensure you have these in your `dependencies` block:
```kotlin
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
```

## 2. Create the Data Models (Java)

Create a class called `AnalysisResponse.java`:

```java
package com.example.skillsync.models; // Adjust package name

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

    // TODO: Generate Getters and Setters for all fields
    public int getScore() { return score; }
    // ...
}
```

## 3. Create the API Interface

Create `ResumeApi.java`:

```java
package com.example.skillsync.api;

import com.example.skillsync.models.AnalysisResponse;

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
```

## 4. Make the Network Call

Here is how you execute the call from an Activity or Fragment:

```java
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// ... Inside your method:

// 1. Setup Retrofit (Use 10.0.2.2 if testing from Android Emulator to localhost)
Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000") // Use your actual IP if on physical device
        .addConverterFactory(GsonConverterFactory.create())
        .build();

ResumeApi api = retrofit.create(ResumeApi.class);

// 2. Prepare the File part
File file = new File(pdfFilePath); // The path to the file on the device
RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
MultipartBody.Part body = MultipartBody.Part.createFormData("resume", file.getName(), requestFile);

// 3. Prepare the Role part
RequestBody role = RequestBody.create(MediaType.parse("text/plain"), "Software Engineer");

// 4. Execute Request
Call<AnalysisResponse> call = api.analyzeResume(body, role);
call.enqueue(new Callback<AnalysisResponse>() {
    @Override
    public void onResponse(Call<AnalysisResponse> call, Response<AnalysisResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            AnalysisResponse result = response.body();
            int score = result.getScore();
            // Update UI with score, suggestions, etc.
        } else {
            // Handle server error
        }
    }

    @Override
    public void onFailure(Call<AnalysisResponse> call, Throwable t) {
        // Handle network failure
    }
});
```
