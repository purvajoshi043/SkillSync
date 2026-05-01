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

                com.example.skillsync.api.AnalysisResponse apiResponse = viewModel.getApiResponse().getValue();
                if (apiResponse != null) {
                    // Update Key Strengths
                    chipGroupStrengths.removeAllViews();
                    java.util.List<String> strengths = new java.util.ArrayList<>();
                    if (apiResponse.getKeywordsMatched() != null) {
                        for (int i = 0; i < Math.min(3, apiResponse.getKeywordsMatched().size()); i++) {
                            strengths.add("Strong Skill: " + apiResponse.getKeywordsMatched().get(i));
                        }
                    }
                    if (strengths.isEmpty()) strengths.add("Solid Foundation");
                    
                    for (String strength : strengths) {
                        com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(requireContext());
                        chip.setText(strength);
                        chip.setChipBackgroundColorResource(R.color.success_green_light);
                        chip.setTextColor(getResources().getColor(R.color.success_green, null));
                        chipGroupStrengths.addView(chip);
                    }

                    // Update Suggested Skills
                    chipGroupSuggestedSkills.removeAllViews();
                    java.util.List<String> gaps = apiResponse.getSkillGap();
                    if (gaps == null || gaps.isEmpty()) gaps = apiResponse.getKeywordsMissing();
                    if (gaps != null) {
                        for (int i = 0; i < Math.min(2, gaps.size()); i++) {
                            String gap = gaps.get(i);
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
                    }

                    // Update Roadmap (Using API suggestions)
                    layoutRoadmap.removeAllViews();
                    java.util.List<String> suggestions = apiResponse.getSuggestions();
                    int stepNum = 1;
                    if (suggestions != null && !suggestions.isEmpty()) {
                        for (String suggestion : suggestions) {
                            View stepView = LayoutInflater.from(requireContext()).inflate(R.layout.item_roadmap_step, layoutRoadmap, false);
                            ((TextView) stepView.findViewById(R.id.tvStepNumber)).setText(String.valueOf(stepNum++));
                            ((TextView) stepView.findViewById(R.id.tvStepTitle)).setText("Improvement Area");
                            ((TextView) stepView.findViewById(R.id.tvStepDescription)).setText(suggestion);
                            layoutRoadmap.addView(stepView);
                        }
                    } else {
                        View stepView = LayoutInflater.from(requireContext()).inflate(R.layout.item_roadmap_step, layoutRoadmap, false);
                        ((TextView) stepView.findViewById(R.id.tvStepNumber)).setText("1");
                        ((TextView) stepView.findViewById(R.id.tvStepTitle)).setText("Keep it up!");
                        ((TextView) stepView.findViewById(R.id.tvStepDescription)).setText("Your resume is looking great.");
                        layoutRoadmap.addView(stepView);
                    }
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

