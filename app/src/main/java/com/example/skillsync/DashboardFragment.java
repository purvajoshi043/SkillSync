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
        View cardProgress = view.findViewById(R.id.cardProgress);
        TextView tvATSValue = view.findViewById(R.id.tvATSValue);
        android.widget.ProgressBar pbATS = view.findViewById(R.id.pbATS);
        LinearLayout layoutRecentReports = view.findViewById(R.id.layoutRecentReports);
        LinearLayout layoutRecommendedJobs = view.findViewById(R.id.layoutRecommendedJobs);

        // Observe user name and location for personalized greeting
        viewModel.getUserName().observe(getViewLifecycleOwner(), name -> {
            String location = viewModel.getLocation().getValue();
            if (name != null && !name.isEmpty()) {
                String greeting = "Hello, " + name + "!";
                if (location != null && !location.isEmpty()) {
                    greeting += " (" + location + ")";
                }
                tvGreeting.setText(greeting);
            }
        });

        // Observe roles from ViewModel → personalize dashboard
        viewModel.getSelectedRoles().observe(getViewLifecycleOwner(), roles -> {
            if (roles != null && !roles.isEmpty()) {
                String rolesText = String.join(", ", roles);
                tvTrack.setText(rolesText + " Track");
                populateJobRecommendations(layoutRecommendedJobs, roles);
            }
        });

        // Observe ATS score for progress visibility
        viewModel.getAtsScore().observe(getViewLifecycleOwner(), score -> {
            if (score != null && score > 0) {
                cardProgress.setVisibility(View.VISIBLE);
                tvATSValue.setText(score + "%");
                pbATS.setProgress(score);
            } else {
                cardProgress.setVisibility(View.GONE);
            }
        });

        // Observe Room DB history
        viewModel.getAllHistory().observe(getViewLifecycleOwner(), historyList -> {
            layoutRecentReports.removeAllViews();
            if (historyList == null || historyList.isEmpty()) {
                View emptyView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.layout_empty_history, layoutRecentReports, false);
                layoutRecentReports.addView(emptyView);
            } else {
                for (com.example.skillsync.data.AnalysisHistory item : historyList) {
                    View card = LayoutInflater.from(requireContext())
                            .inflate(R.layout.item_report_card, layoutRecentReports, false);
                    ((TextView) card.findViewById(R.id.tvReportRole)).setText(item.role);
                    ((TextView) card.findViewById(R.id.tvReportDate)).setText(item.date);
                    ((TextView) card.findViewById(R.id.tvReportScore)).setText(item.score + "%");
                    
                    card.setOnClickListener(v -> {
                        // Navigate to results for this specific history item
                        viewModel.setAtsScore(item.score);
                        Navigation.findNavController(view).navigate(R.id.action_dashboard_to_results);
                    });
                    layoutRecentReports.addView(card);
                }
            }
        });

        // FAB
        ExtendedFloatingActionButton fabUpload = view.findViewById(R.id.fabUpload);
        fabUpload.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_dashboard_to_upload));
    }

    private void populateJobRecommendations(LinearLayout container, java.util.List<String> roles) {
        container.removeAllViews();
        java.util.List<String[]> allJobs = new java.util.ArrayList<>();

        for (String role : roles) {
            String[][] jobs;
            switch (role) {
                case "Android Developer":
                    jobs = new String[][]{
                            {"Google", "Software Engineer (Android)", "88%"},
                            {"Spotify", "Mobile Developer", "82%"}
                    };
                    break;
                case "Web Developer":
                    jobs = new String[][]{
                            {"Meta", "Frontend Engineer", "85%"},
                            {"Netflix", "UI Engineer", "79%"}
                    };
                    break;
                case "Data Scientist":
                    jobs = new String[][]{
                            {"Google", "Data Scientist", "85%"},
                            {"Amazon", "ML Engineer", "78%"}
                    };
                    break;
                case "Software Engineer":
                    jobs = new String[][]{
                            {"Microsoft", "Software Engineer II", "90%"},
                            {"Twitter", "Backend Engineer", "81%"}
                    };
                    break;
                case "Business Analyst":
                    jobs = new String[][]{
                            {"McKinsey", "Associate Analyst", "92%"},
                            {"Deloitte", "Senior Business Consultant", "85%"}
                    };
                    break;
                case "Product Manager":
                    jobs = new String[][]{
                            {"Apple", "Product Manager", "87%"},
                            {"Uber", "Technical PM", "80%"}
                    };
                    break;
                case "Graphic Designer":
                    jobs = new String[][]{
                            {"Adobe", "Creative Lead", "90%"},
                            {"Canva", "Visual Designer", "84%"}
                    };
                    break;
                case "UX Researcher":
                    jobs = new String[][]{
                            {"Airbnb", "UX Researcher", "86%"},
                            {"IBM", "Experience Researcher", "77%"}
                    };
                    break;
                case "Marketing Manager":
                    jobs = new String[][]{
                            {"Nike", "Brand Marketing Manager", "88%"},
                            {"HubSpot", "Inbound Marketing Strategist", "82%"}
                    };
                    break;
                case "HR Specialist":
                    jobs = new String[][]{
                            {"LinkedIn", "HR Business Partner", "91%"},
                            {"Amazon", "Employee Experience Specialist", "83%"}
                    };
                    break;
                case "Project Manager":
                    jobs = new String[][]{
                            {"Cisco", "Project Manager", "85%"},
                            {"Oracle", "Technical Project Lead", "79%"}
                    };
                    break;
                case "Sales Representative":
                    jobs = new String[][]{
                            {"Salesforce", "Account Executive", "89%"},
                            {"Oracle", "Sales Manager", "82%"}
                    };
                    break;
                default:
                    jobs = new String[][]{
                            {"Indeed", "General Consultant", "80%"}
                    };
            }
            for (String[] j : jobs) {
                allJobs.add(j);
            }
        }

        // Display up to 5 recommended jobs total
        int count = 0;
        for (String[] job : allJobs) {
            if (count >= 5) break;
            View card = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_job_card, container, false);
            ((TextView) card.findViewById(R.id.tvJobCompany)).setText(job[0]);
            ((TextView) card.findViewById(R.id.tvJobTitle)).setText(job[1]);
            ((TextView) card.findViewById(R.id.tvJobMatch)).setText(job[2] + " Match");
            container.addView(card);
            count++;
        }
    }
}
