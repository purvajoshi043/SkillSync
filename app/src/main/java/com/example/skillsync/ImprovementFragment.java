package com.example.skillsync;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ImprovementFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_improvement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Clickable skill tags open browser to official resource
        view.findViewById(R.id.tagTensorFlow).setOnClickListener(v ->
                openUrl("https://www.tensorflow.org/tutorials"));

        view.findViewById(R.id.tagPyTorch).setOnClickListener(v ->
                openUrl("https://pytorch.org/tutorials/"));

        view.findViewById(R.id.tagAWS).setOnClickListener(v ->
                openUrl("https://aws.amazon.com/training/"));
    }

    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}

