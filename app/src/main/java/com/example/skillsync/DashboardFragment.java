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
                // If there's no current session score, load the most recent one to the dashboard card
                if (viewModel.getAtsScore().getValue() == null) {
                    viewModel.setAtsScore(historyList.get(0).score);
                }

                for (com.example.skillsync.data.AnalysisHistory item : historyList) {
                    View itemCard = LayoutInflater.from(requireContext())
                            .inflate(R.layout.item_report_card, layoutRecentReports, false);
                    ((TextView) itemCard.findViewById(R.id.tvReportRole)).setText(item.role);
                    ((TextView) itemCard.findViewById(R.id.tvReportDate)).setText(item.date);
                    ((TextView) itemCard.findViewById(R.id.tvReportScore)).setText(item.score + "%");
                    
                    itemCard.setOnClickListener(v -> {
                        viewModel.setAtsScore(item.score);
                        Navigation.findNavController(view).navigate(R.id.action_dashboard_to_results);
                    });
                    layoutRecentReports.addView(itemCard);
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
            String roleLower = role.toLowerCase();
            String[][] jobs;
            
            if (roleLower.contains("android") || roleLower.contains("ios") || roleLower.contains("mobile")) {
                jobs = new String[][]{
                        {"Google", "Senior Mobile Engineer", "88%"},
                        {"Spotify", "iOS/Android Developer", "82%"},
                        {"Uber", "Staff Engineer (Mobile)", "85%"}
                };
            } else if (roleLower.contains("web") || roleLower.contains("frontend") || roleLower.contains("backend") || roleLower.contains("stack")) {
                jobs = new String[][]{
                        {"Meta", "Full Stack Engineer", "85%"},
                        {"Netflix", "Senior Web Developer", "79%"},
                        {"Amazon", "AWS Cloud Engineer", "83%"}
                };
            } else if (roleLower.contains("data") || roleLower.contains("analyst") || roleLower.contains("scientist")) {
                jobs = new String[][]{
                        {"Google", "Data Scientist L5", "85%"},
                        {"Amazon", "Business Intelligence Engineer", "78%"},
                        {"Microsoft", "Data Analyst", "81%"}
                };
            } else if (roleLower.contains("designer") || roleLower.contains("ux") || roleLower.contains("ui") || roleLower.contains("graphic")) {
                jobs = new String[][]{
                        {"Adobe", "Senior Product Designer", "90%"},
                        {"Airbnb", "UX Researcher", "84%"},
                        {"Canva", "Visual Designer", "86%"}
                };
            } else if (roleLower.contains("manager") || roleLower.contains("lead") || roleLower.contains("product")) {
                jobs = new String[][]{
                        {"Apple", "Product Manager", "87%"},
                        {"Uber", "Technical Program Manager", "80%"},
                        {"Salesforce", "Project Lead", "85%"}
                };
            } else if (roleLower.contains("cyber") || roleLower.contains("security") || roleLower.contains("devops")) {
                jobs = new String[][]{
                        {"Cloudflare", "Security Engineer", "88%"},
                        {"Datadog", "Site Reliability Engineer", "82%"},
                        {"Palo Alto", "Cybersecurity Lead", "90%"}
                } ;
            } else {
                jobs = new String[][]{
                        {"Indeed", "Professional Consultant", "80%"},
                        {"LinkedIn", "Career Strategist", "75%"}
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
