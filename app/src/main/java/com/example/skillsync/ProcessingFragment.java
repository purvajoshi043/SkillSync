package com.example.skillsync;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.skillsync.data.AnalysisHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ProcessingFragment extends Fragment {

    private final String[] messages = {
            "Scanning skills...",
            "Matching industry requirements...",
            "Calculating ATS score...",
            "Generating insights..."
    };
    private int currentMessageIndex = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private AnalysisViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_processing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AnalysisViewModel.class);
        TextView tvProcessingStatus = view.findViewById(R.id.tvProcessingStatus);

        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentMessageIndex < messages.length) {
                    tvProcessingStatus.setText(messages[currentMessageIndex]);
                    currentMessageIndex++;
                    handler.postDelayed(this, 1500);
                } else {
                    // Generate realistic ATS score using ResumeValidator
                    String fileName = viewModel.getSelectedFileName().getValue();
                    // Mock content based on filename for demonstration
                    String mockContent = (fileName != null && fileName.toLowerCase().contains("resume")) 
                            ? "Experience Education Skills Projects" : "Assignment Homework Task";
                    
                    if (!ResumeValidator.isLikelyResume(fileName, mockContent)) {
                        // Show warning and stop
                        view.findViewById(R.id.layoutProcessing).setVisibility(View.GONE);
                        View warning = view.findViewById(R.id.layoutWarning);
                        warning.setVisibility(View.VISIBLE);
                        warning.findViewById(R.id.btnTryAgain).setOnClickListener(v -> 
                            Navigation.findNavController(v).navigateUp());
                        return;
                    }

                    java.util.List<String> selectedRoles = viewModel.getSelectedRoles().getValue();
                    int realScore = ResumeValidator.calculateScore(fileName, mockContent, selectedRoles);
                    viewModel.setAtsScore(realScore);

                    // Save to Room DB
                    String rolesText = (selectedRoles != null && !selectedRoles.isEmpty()) ? String.join(", ", selectedRoles) : "General";
                    String date = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
                    viewModel.insertHistory(new AnalysisHistory(rolesText, realScore, date));

                    if (getView() != null) {
                        Navigation.findNavController(getView()).navigate(R.id.action_processing_to_results);
                    }
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }
}

