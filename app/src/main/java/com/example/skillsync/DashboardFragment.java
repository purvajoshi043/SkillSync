package com.example.skillsync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class DashboardFragment extends Fragment {

    private AnalysisViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AnalysisViewModel.class);

        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        TextView tvTrack = view.findViewById(R.id.tvTrack);
        LinearLayout layoutRecentReports = view.findViewById(R.id.layoutRecentReports);
        LinearLayout layoutRecommendedJobs = view.findViewById(R.id.layoutRecommendedJobs);

        // Observe role from ViewModel → personalize dashboard
        viewModel.getSelectedRole().observe(getViewLifecycleOwner(), role -> {
            tvTrack.setText(role + " Track");
            populateJobRecommendations(layoutRecommendedJobs, role);
        });

        // Observe ATS score for greeting update
        viewModel.getAtsScore().observe(getViewLifecycleOwner(), score -> {
            if (score != null && score > 0) {
                tvGreeting.setText("Welcome back!");
            }
        });

        // Observe Room DB history
        viewModel.getAllHistory().observe(getViewLifecycleOwner(), historyList -> {
            layoutRecentReports.removeAllViews();
            if (historyList == null || historyList.isEmpty()) {
                TextView empty = new TextView(requireContext());
                empty.setText("No analyses yet. Upload your resume to get started!");
                empty.setTextColor(getResources().getColor(R.color.text_secondary, null));
                empty.setPadding(0, 8, 0, 8);
                layoutRecentReports.addView(empty);
            } else {
                for (com.example.skillsync.data.AnalysisHistory item : historyList) {
                    View card = LayoutInflater.from(requireContext())
                            .inflate(R.layout.item_report_card, layoutRecentReports, false);
                    ((TextView) card.findViewById(R.id.tvReportRole)).setText(item.role);
                    ((TextView) card.findViewById(R.id.tvReportDate)).setText(item.date);
                    ((TextView) card.findViewById(R.id.tvReportScore)).setText(String.valueOf(item.score));
                    card.setOnClickListener(v ->
                            Navigation.findNavController(view).navigate(R.id.action_dashboard_to_results));
                    layoutRecentReports.addView(card);
                }
            }
        });

        // FAB
        ExtendedFloatingActionButton fabUpload = view.findViewById(R.id.fabUpload);
        fabUpload.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_dashboard_to_upload));
    }

    private void populateJobRecommendations(LinearLayout container, String role) {
        container.removeAllViews();
        String[][] jobs;
        switch (role) {
            case "Business Analyst":
                jobs = new String[][]{
                        {"McKinsey", "Associate Analyst", "92%"},
                        {"Deloitte", "Senior Business Consultant", "85%"},
                        {"KPMG", "Process Improvement Analyst", "78%"}
                };
                break;
            case "Graphic Designer":
                jobs = new String[][]{
                        {"Adobe", "Creative Lead", "90%"},
                        {"Canva", "Visual Designer", "84%"},
                        {"Meta", "UI Designer", "76%"}
                };
                break;
            case "Marketing Manager":
                jobs = new String[][]{
                        {"Nike", "Brand Marketing Manager", "88%"},
                        {"HubSpot", "Inbound Marketing Strategist", "82%"},
                        {"Google", "Growth Marketing Lead", "74%"}
                };
                break;
            case "HR Specialist":
                jobs = new String[][]{
                        {"LinkedIn", "HR Business Partner", "91%"},
                        {"Amazon", "Employee Experience Specialist", "83%"},
                        {"Microsoft", "Talent Acquisition Manager", "75%"}
                };
                break;
            default:
                jobs = new String[][]{
                        {"Indeed", "General Consultant", "80%"},
                        {"Glassdoor", "Operations Lead", "75%"},
                        {"Monster", "Strategy Associate", "70%"}
                };
        }
        for (String[] job : jobs) {
            View card = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_job_card, container, false);
            ((TextView) card.findViewById(R.id.tvJobCompany)).setText(job[0]);
            ((TextView) card.findViewById(R.id.tvJobTitle)).setText(job[1]);
            ((TextView) card.findViewById(R.id.tvJobMatch)).setText(job[2] + " Match");
            container.addView(card);
        }
    }
}
