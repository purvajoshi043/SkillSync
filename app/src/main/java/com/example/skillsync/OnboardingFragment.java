package com.example.skillsync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class OnboardingFragment extends Fragment {

    private AnalysisViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    private static final String[] ALL_ROLES = {
        "Android Developer", "Web Developer", "Data Scientist", "Software Engineer", 
        "Business Analyst", "Product Manager", "Graphic Designer", "HR Specialist",
        "iOS Developer", "Backend Engineer", "Frontend Developer", "Full Stack Developer",
        "UI/UX Designer", "DevOps Engineer", "Cloud Architect", "QA Tester",
        "Digital Marketer", "Content Writer", "Sales Representative", "Financial Analyst",
        "Project Manager", "Network Engineer", "Cybersecurity Analyst", "Database Administrator"
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AnalysisViewModel.class);

        android.widget.AutoCompleteTextView atvSearch = view.findViewById(R.id.atvRoleSearch);
        ChipGroup chipGroupRoles = view.findViewById(R.id.chipGroupRoles);
        RadioGroup rgExperience = view.findViewById(R.id.rgExperience);

        // Setup Search
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, ALL_ROLES);
        atvSearch.setAdapter(adapter);

        java.util.Set<String> selectedRolesSet = new java.util.HashSet<>();

        atvSearch.setOnItemClickListener((parent, v, position, id) -> {
            String role = (String) parent.getItemAtPosition(position);
            if (!selectedRolesSet.contains(role)) {
                selectedRolesSet.add(role);
                addRoleChip(role, chipGroupRoles, selectedRolesSet);
            }
            atvSearch.setText(""); // Clear search
        });

        view.findViewById(R.id.btnContinue).setOnClickListener(v -> {
            if (selectedRolesSet.isEmpty()) {
                Toast.makeText(getContext(), "Please select at least one role", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.setSelectedRoles(new java.util.ArrayList<>(selectedRolesSet));

            // Get selected experience
            int expId = rgExperience.getCheckedRadioButtonId();
            if (expId != -1) {
                String exp = ((RadioButton) view.findViewById(expId)).getText().toString();
                viewModel.setSelectedExperience(exp);
            }

            Navigation.findNavController(v).navigate(R.id.action_onboarding_to_dashboard);
        });
    }

    private void addRoleChip(String role, ChipGroup group, java.util.Set<String> set) {
        Chip chip = new Chip(requireContext());
        chip.setText(role);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setChipBackgroundColorResource(R.color.background_drop_area);
        chip.setTextColor(getResources().getColor(R.color.primary, null));
        chip.setOnCloseIconClickListener(v -> {
            group.removeView(chip);
            set.remove(role);
        });
        group.addView(chip);
    }
}
