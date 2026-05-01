package com.example.skillsync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout;

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
        // Tech
        SKILL_GAPS.put("Android Developer", Arrays.asList("System Design", "Jetpack Compose", "CI/CD Pipelines"));
        SKILL_GAPS.put("Web Developer", Arrays.asList("TypeScript", "React/Next.js", "System Design"));
        SKILL_GAPS.put("Data Scientist", Arrays.asList("Deep Learning", "Cloud Deployment (AWS)", "SQL & Databases"));
        SKILL_GAPS.put("Software Engineer", Arrays.asList("Distributed Systems", "Kubernetes", "Low-Level Optimization"));
        
        // Business & Design
        SKILL_GAPS.put("Business Analyst", Arrays.asList("Data Visualization", "Stakeholder Management", "SQL & Databases"));
        SKILL_GAPS.put("Product Manager", Arrays.asList("Product Roadmap", "User Research", "Agile Leadership"));
        SKILL_GAPS.put("Graphic Designer", Arrays.asList("UI/UX Design", "Motion Graphics", "Typography"));
        SKILL_GAPS.put("UX Researcher", Arrays.asList("Usability Testing", "Quantitative Analysis", "User Interviews"));
        
        // Operations & Others
        SKILL_GAPS.put("Marketing Manager", Arrays.asList("SEO/SEM", "Content Strategy", "Social Media Analytics"));
        SKILL_GAPS.put("HR Specialist", Arrays.asList("Talent Acquisition", "Conflict Resolution", "Labor Laws"));
        SKILL_GAPS.put("Project Manager", Arrays.asList("Risk Management", "Budgeting", "Scrum Master"));
        SKILL_GAPS.put("Sales Representative", Arrays.asList("CRM Mastery", "Lead Generation", "Closing Techniques"));
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

        // Populate Strengths and Gaps dynamically
        viewModel.getSelectedFileName().observe(getViewLifecycleOwner(), fileName -> {
            LinearLayout layoutStrengths = view.findViewById(R.id.layoutStrengths);
            if (layoutStrengths != null) {
                layoutStrengths.removeAllViews();
                
                // Simulation content
                String mockContent = (fileName != null && fileName.toLowerCase().contains("resume")) 
                        ? "Experience Education Skills Projects Python" : "Basic Content";
                
                java.util.List<String> selectedRoles = viewModel.getSelectedRoles().getValue();
            
                // Strengths
                java.util.List<String> strengths = ResumeValidator.getStrengths(selectedRoles, mockContent);
                for (String s : strengths) {
                    TextView tv = new TextView(requireContext());
                    tv.setText("✓  " + s);
                    tv.setTextSize(15);
                    tv.setTextColor(getResources().getColor(R.color.success_green, null));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 12);
                    tv.setLayoutParams(lp);
                    layoutStrengths.addView(tv);
                }

                // Gaps (Limited to 2 chips as requested)
                chipGroupGaps.removeAllViews();
                java.util.List<String> gaps = ResumeValidator.getSkillGaps(selectedRoles, mockContent);
                for (String gap : gaps) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(gap);
                    chip.setChipBackgroundColorResource(R.color.warning_amber_light);
                    chip.setTextColor(getResources().getColor(R.color.text_primary_dark, null));
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

