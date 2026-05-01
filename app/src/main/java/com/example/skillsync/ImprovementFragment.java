package com.example.skillsync;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.skillsync.data.AnalysisHistory;

public class ImprovementFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_improvement, container, false);
    }

    private AnalysisViewModel viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new androidx.lifecycle.ViewModelProvider(requireActivity()).get(AnalysisViewModel.class);

        TextView tvOldScore = view.findViewById(R.id.tvOldScore);
        TextView tvUpdatedScore = view.findViewById(R.id.tvUpdatedScore);
        View containerUpdated = view.findViewById(R.id.containerUpdatedVersion);
        com.google.android.material.chip.ChipGroup chipGroupStrengths = view.findViewById(R.id.chipGroupStrengths);

        com.google.android.material.chip.ChipGroup chipGroupSuggestedSkills = view.findViewById(R.id.chipGroupSuggestedSkills);
        LinearLayout layoutRoadmap = view.findViewById(R.id.layoutRoadmap);

        // Back button
        view.findViewById(R.id.btnBack).setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Observe history to compare versions
        viewModel.getAllHistory().observe(getViewLifecycleOwner(), historyList -> {
            if (historyList != null && !historyList.isEmpty()) {
                // Latest result is at index 0
                com.example.skillsync.data.AnalysisHistory latest = historyList.get(0);
                tvOldScore.setText("Score: " + latest.score + "%");
                
                int projectedScore = Math.min(latest.score + 14, 99);
                tvUpdatedScore.setText("Score: " + projectedScore + "%");

                String fileName = viewModel.getSelectedFileName().getValue();
                String mockContent = (fileName != null && fileName.toLowerCase().contains("resume")) 
                        ? "Experience Education Skills Projects Python" : "Basic Content";

                java.util.List<String> selectedRoles = viewModel.getSelectedRoles().getValue();

                // Update Key Strengths
                chipGroupStrengths.removeAllViews();
                java.util.List<String> strengths = ResumeValidator.getStrengths(selectedRoles, mockContent);
                for (String strength : strengths) {
                    com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(requireContext());
                    chip.setText(strength);
                    chip.setChipBackgroundColorResource(R.color.success_green_light);
                    chip.setTextColor(getResources().getColor(R.color.success_green, null));
                    chipGroupStrengths.addView(chip);
                }

                // Update Suggested Skills (Limited to 2 chips)
                chipGroupSuggestedSkills.removeAllViews();
                java.util.List<String> gaps = ResumeValidator.getSkillGaps(selectedRoles, mockContent);
                for (String gap : gaps) {
                    com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(requireContext());
                    chip.setText(gap);
                    chip.setChipBackgroundColorResource(R.color.primary_light);
                    chip.setTextColor(getResources().getColor(R.color.primary, null));
                    chip.setOnClickListener(v -> {
                        SkillGapBottomSheet sheet = SkillGapBottomSheet.newInstance(gap);
                        sheet.show(getChildFragmentManager(), "SkillGapBottomSheet");
                    });
                    chipGroupSuggestedSkills.addView(chip);
                }

                // Update Roadmap
                layoutRoadmap.removeAllViews();
                java.util.List<String> steps = ResumeValidator.getRoadmap(selectedRoles);
                int stepNum = 1;
                for (String stepData : steps) {
                    String[] parts = stepData.split("\\|");
                    String title = parts[0];
                    String desc = parts.length > 1 ? parts[1] : "";

                    View stepView = LayoutInflater.from(requireContext()).inflate(R.layout.item_roadmap_step, layoutRoadmap, false);
                    ((TextView) stepView.findViewById(R.id.tvStepNumber)).setText(String.valueOf(stepNum++));
                    ((TextView) stepView.findViewById(R.id.tvStepTitle)).setText(title);
                    ((TextView) stepView.findViewById(R.id.tvStepDescription)).setText(desc);
                    layoutRoadmap.addView(stepView);
                }
            }
        });

        // Click for Tips
        containerUpdated.setOnClickListener(v -> {
            String[] tips = {
                "💡 Quantify achievements with numbers (e.g., 'Increased revenue by 20%').",
                "💡 Use industry-standard keywords from the job description.",
                "💡 Keep your resume to 1-2 pages for maximum impact.",
                "💡 Include a strong professional summary at the top."
            };
            String randomTip = tips[(int)(Math.random() * tips.length)];
            android.widget.Toast.makeText(getContext(), randomTip, android.widget.Toast.LENGTH_LONG).show();
        });

        view.findViewById(R.id.btnFinish).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.dashboardFragment));
    }


    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}

