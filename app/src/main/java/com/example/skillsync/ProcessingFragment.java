package com.example.skillsync;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.skillsync.api.AnalysisResponse;
import com.example.skillsync.api.ResumeApi;
import com.example.skillsync.data.AnalysisHistory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProcessingFragment extends Fragment {

    private final String[] messages = {
            "Uploading resume to server...",
            "Extracting text from document...",
            "Running NLP analysis...",
            "Calculating ATS score..."
    };
    private int currentMessageIndex = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private AnalysisViewModel viewModel;
    private TextView tvProcessingStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_processing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AnalysisViewModel.class);
        tvProcessingStatus = view.findViewById(R.id.tvProcessingStatus);

        // Start the UI animation
        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentMessageIndex < messages.length) {
                    tvProcessingStatus.setText(messages[currentMessageIndex]);
                    currentMessageIndex++;
                    handler.postDelayed(this, 1500);
                }
            }
        };
        handler.post(runnable);

        // Start the API call
        startAnalysis(view);
    }

    private void startAnalysis(View view) {
        Uri fileUri = viewModel.getSelectedFileUri().getValue();
        if (fileUri == null) {
            showError(view, "No file selected.");
            return;
        }

        java.util.List<String> selectedRoles = viewModel.getSelectedRoles().getValue();
        String roleStr = (selectedRoles != null && !selectedRoles.isEmpty()) ? selectedRoles.get(0) : "Software Engineer";

        try {
            // Read file into byte array
            InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] fileBytes = buffer.toByteArray();
            inputStream.close();

            // Create RequestBody
            String fileName = viewModel.getSelectedFileName().getValue();
            if (fileName == null) fileName = "resume.pdf";
            
            // Determine mediatype based on extension
            String mediaTypeStr = fileName.toLowerCase().endsWith(".docx") ? "application/vnd.openxmlformats-officedocument.wordprocessingml.document" : "application/pdf";
            RequestBody requestFile = RequestBody.create(MediaType.parse(mediaTypeStr), fileBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("resume", fileName, requestFile);
            RequestBody roleBody = RequestBody.create(MediaType.parse("text/plain"), roleStr);

            // Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.7.26.105:8080/") // Your computer's actual IP address!
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ResumeApi api = retrofit.create(ResumeApi.class);
            Call<AnalysisResponse> call = api.analyzeResume(body, roleBody);
            
            call.enqueue(new Callback<AnalysisResponse>() {
                @Override
                public void onResponse(Call<AnalysisResponse> call, Response<AnalysisResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        AnalysisResponse apiResponse = response.body();
                        viewModel.setApiResponse(apiResponse);
                        viewModel.setAtsScore(apiResponse.getScore());

                        // Save to Room DB
                        String rolesText = String.join(", ", selectedRoles != null ? selectedRoles : java.util.Collections.singletonList("General"));
                        String date = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
                        viewModel.insertHistory(new AnalysisHistory(rolesText, apiResponse.getScore(), date));

                        // Navigate
                        if (getView() != null) {
                            handler.removeCallbacks(runnable);
                            Navigation.findNavController(getView()).navigate(R.id.action_processing_to_results);
                        }
                    } else {
                        showError(view, "Server error: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<AnalysisResponse> call, Throwable t) {
                    showError(view, "Network error: Make sure the Python API is running on localhost:8080.\n" + t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showError(view, "Failed to read file: " + e.getMessage());
        }
    }

    private void showError(View view, String errorMsg) {
        handler.removeCallbacks(runnable);
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                view.findViewById(R.id.layoutProcessing).setVisibility(View.GONE);
                View warning = view.findViewById(R.id.layoutWarning);
                warning.setVisibility(View.VISIBLE);
                warning.findViewById(R.id.btnTryAgain).setOnClickListener(v ->
                        Navigation.findNavController(v).navigateUp());
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }
}
