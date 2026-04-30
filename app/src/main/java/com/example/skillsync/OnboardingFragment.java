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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AnalysisViewModel.class);

        ChipGroup chipGroupRoles = view.findViewById(R.id.chipGroupRoles);
        RadioGroup rgExperience = view.findViewById(R.id.rgExperience);

        view.findViewById(R.id.btnContinue).setOnClickListener(v -> {
            // Get selected roles from ChipGroup
            List<String> selectedRoles = new ArrayList<>();
            for (int i = 0; i < chipGroupRoles.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupRoles.getChildAt(i);
                if (chip.isChecked()) {
                    selectedRoles.add(chip.getText().toString());
                }
            }

            if (selectedRoles.isEmpty()) {
                Toast.makeText(getContext(), "Please select at least one role", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.setSelectedRoles(selectedRoles);

            // Get selected experience
            int expId = rgExperience.getCheckedRadioButtonId();
            if (expId != -1) {
                String exp = ((RadioButton) view.findViewById(expId)).getText().toString();
                viewModel.setSelectedExperience(exp);
            }

            Navigation.findNavController(v).navigate(R.id.action_onboarding_to_dashboard);
        });
    }
}
