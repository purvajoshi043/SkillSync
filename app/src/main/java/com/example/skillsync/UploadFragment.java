package com.example.skillsync;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class UploadFragment extends Fragment {

    private AnalysisViewModel viewModel;
    private ActivityResultLauncher<String> filePickerLauncher;
    private Button btnAnalyze;
    private View cardFileSelected;
    private TextView tvSelectedFileName;
    private TextView tvSelectedFileSize;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        displaySelectedFile(uri);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AnalysisViewModel.class);

        View viewDropArea = view.findViewById(R.id.viewDropArea);
        cardFileSelected = view.findViewById(R.id.cardFileSelected);
        btnAnalyze = view.findViewById(R.id.btnAnalyze);
        tvSelectedFileName = view.findViewById(R.id.tvSelectedFileName);
        tvSelectedFileSize = view.findViewById(R.id.tvSelectedFileSize);

        viewDropArea.setOnClickListener(v -> filePickerLauncher.launch("application/pdf"));

        btnAnalyze.setOnClickListener(v -> {
            viewModel.setAtsScore(null); // Clear previous score
            Navigation.findNavController(v).navigate(R.id.action_upload_to_processing);
        });
    }

    private void displaySelectedFile(Uri uri) {
        String fileName = "Unknown file";
        String fileSize = "";
        try (Cursor cursor = requireContext().getContentResolver()
                .query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (nameIndex >= 0) fileName = cursor.getString(nameIndex);
                if (sizeIndex >= 0) {
                    long sizeBytes = cursor.getLong(sizeIndex);
                    fileSize = String.format("%.1f KB", sizeBytes / 1024.0);
                }
            }
        }
        viewModel.setSelectedFileName(fileName);
        viewModel.setSelectedFileUri(uri);
        tvSelectedFileName.setText(fileName);
        tvSelectedFileSize.setText(fileSize);
        cardFileSelected.setVisibility(View.VISIBLE);
        btnAnalyze.setEnabled(true);
    }
}

