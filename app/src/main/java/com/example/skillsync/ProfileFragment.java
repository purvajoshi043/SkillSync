package com.example.skillsync;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private AnalysisViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AnalysisViewModel.class);

        TextInputEditText etName = view.findViewById(R.id.etName);
        TextInputEditText etAge = view.findViewById(R.id.etAge);
        TextInputEditText etLocation = view.findViewById(R.id.etLocation);
        TextInputEditText etEducation = view.findViewById(R.id.etEducation);
        TextInputEditText etBio = view.findViewById(R.id.etBio);

        view.findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String age = etAge.getText() != null ? etAge.getText().toString().trim() : "";
            String loc = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";
            String education = etEducation.getText() != null ? etEducation.getText().toString().trim() : "";
            String bio = etBio.getText() != null ? etBio.getText().toString().trim() : "";

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.setUserName(name);
            viewModel.setAge(age);
            viewModel.setLocation(loc);
            viewModel.setEducation(education);
            viewModel.setBio(bio);

            Navigation.findNavController(v).navigate(R.id.action_profile_to_onboarding);
        });
    }
}
