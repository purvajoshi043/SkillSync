package com.example.skillsync;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Map;

public class SkillGapBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_SKILL = "skill";

    // Map of skill → list of {title, source, url}
    private static final Map<String, String[][]> RESOURCES = new HashMap<>();

    static {
        RESOURCES.put("Data Visualization", new String[][]{
                {"Data Visualization Specialization", "Coursera · Tableau", "https://www.coursera.org/specializations/data-visualization"},
                {"Storytelling with Data", "Cole Knaflic", "https://www.storytellingwithdata.com/"},
                {"D3.js Gallery", "Observable", "https://observablehq.com/@d3/gallery"}
        });
        RESOURCES.put("UI/UX Design", new String[][]{
                {"Google UX Design Professional Certificate", "Coursera", "https://www.coursera.org/professional-certificates/google-ux-design"},
                {"Laws of UX", "Jon Yablonski", "https://lawsofux.com/"},
                {"Figma Tutorials", "YouTube · Figma", "https://www.youtube.com/c/FigmaDesign"}
        });
        RESOURCES.put("SEO/SEM", new String[][]{
                {"SEO Starter Guide", "Google Search Central", "https://developers.google.com/search/docs/fundamentals/seo-starter-guide"},
                {"Moz Beginner's Guide to SEO", "Moz", "https://moz.com/beginners-guide-to-seo"},
                {"Google Ads Certification", "Skillshop", "https://skillshop.exceedlms.com/student/path/18128-google-ads-search-certification"}
        });
        RESOURCES.put("Talent Acquisition", new String[][]{
                {"Recruiting Foundations", "LinkedIn Learning", "https://www.linkedin.com/learning/recruiting-foundations"},
                {"SHRM Talent Acquisition Resources", "SHRM", "https://www.shrm.org/resourcesandtools/hr-topics/talent-acquisition/pages/default.aspx"},
                {"Modern Talent Acquisition Guide", "Workable", "https://resources.workable.com/talent-acquisition-guide"}
        });
        RESOURCES.put("SQL & Databases", new String[][]{
                {"SQL for Data Science", "Coursera · UC Davis", "https://www.coursera.org/learn/sql-for-data-science"},
                {"SQLZoo Interactive Tutorial", "SQLZoo.net", "https://sqlzoo.net/"},
                {"Mode SQL Tutorial", "Mode Analytics", "https://mode.com/sql-tutorial/"}
        });
    }

    public static SkillGapBottomSheet newInstance(String skill) {
        SkillGapBottomSheet sheet = new SkillGapBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SKILL, skill);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_skill_gap, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String skill = getArguments() != null ? getArguments().getString(ARG_SKILL, "") : "";
        String[][] resources = RESOURCES.get(skill);

        TextView tvTitle = view.findViewById(R.id.tvBottomSheetTitle);
        tvTitle.setText("Learn: " + skill);

        LinearLayout layoutResources = view.findViewById(R.id.layoutResources);
        layoutResources.removeAllViews();

        if (resources == null) {
            // Default generic resources if skill not in map
            resources = new String[][]{
                    {"Search courses on Coursera", "Coursera", "https://www.coursera.org/search?query=" + skill},
                    {"Learn on YouTube", "YouTube", "https://www.youtube.com/results?search_query=how+to+learn+" + skill},
                    {"Search on Google", "Google", "https://www.google.com/search?q=learn+" + skill}
            };
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (String[] resource : resources) {
            View row = inflater.inflate(R.layout.item_resource_row, layoutResources, false);
            ((TextView) row.findViewById(R.id.tvResourceTitle)).setText(resource[0]);
            ((TextView) row.findViewById(R.id.tvResourceSource)).setText(resource[1]);

            String url = resource[2];
            row.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            });
            layoutResources.addView(row);
        }
    }
}
