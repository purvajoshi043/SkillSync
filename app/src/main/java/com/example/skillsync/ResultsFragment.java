package com.example.skillsync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultsFragment extends Fragment {

    // Map of role → skill gaps
    private static final Map<String, List<String>> SKILL_GAPS = new HashMap<>();
    static {
        SKILL_GAPS.put("Business Analyst", Arrays.asList("Data Visualization", "Stakeholder Management", "SQL & Databases"));
        SKILL_GAPS.put("Graphic Designer", Arrays.asList("UI/UX Design", "Motion Graphics", "Typography"));
        SKILL_GAPS.put("Marketing Manager", Arrays.asList("SEO/SEM", "Content Strategy", "Social Media Analytics"));
        SKILL_GAPS.put("HR Specialist", Arrays.asList("Talent Acquisition", "Conflict Resolution", "Labor Laws"));
    }

    private AnalysisViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AnalysisViewModel.class);

        TextView tvAtsScore = view.findViewById(R.id.tvAtsScore);
        ProgressBar pbAts = view.findViewById(R.id.pbAts);
        ChipGroup chipGroupGaps = view.findViewById(R.id.chipGroupGaps);

        // Show dynamic ATS score from ViewModel
        viewModel.getAtsScore().observe(getViewLifecycleOwner(), score -> {
            if (score != null && score > 0) {
                tvAtsScore.setText(score + "%");
                pbAts.setProgress(score);
                // Role match is slightly lower
                int roleMatch = Math.max(score - 14, 40);
                ((TextView) view.findViewById(R.id.tvRoleMatch)).setText(roleMatch + "%");
                ((ProgressBar) view.findViewById(R.id.pbRoleMatch)).setProgress(roleMatch);
            }
        });

        // Populate ChipGroup based on selected role
        viewModel.getSelectedRole().observe(getViewLifecycleOwner(), role -> {
            chipGroupGaps.removeAllViews();
            List<String> gaps = SKILL_GAPS.getOrDefault(role, SKILL_GAPS.get("Data Scientist"));
            if (gaps != null) {
                for (String gap : gaps) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(gap);
                    chip.setChipBackgroundColorResource(R.color.warning_amber_light);
                    chip.setTextColor(getResources().getColor(R.color.text_primary_dark, null));
                    chip.setClickable(true);
                    chip.setFocusable(true);
                    chip.setOnClickListener(v -> {
                        SkillGapBottomSheet sheet = SkillGapBottomSheet.newInstance(gap);
                        sheet.show(getChildFragmentManager(), "SkillGapBottomSheet");
                    });
                    chipGroupGaps.addView(chip);
                }
            }
        });

        view.findViewById(R.id.btnImprovementPlan).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_results_to_improvement));
    }
}

