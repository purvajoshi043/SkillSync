package com.example.skillsync;

import java.util.Locale;

public class ResumeValidator {

    public static int calculateScore(String fileName, String content, java.util.List<String> roles) {
        if (content == null || content.isEmpty())
            return 10;

        String lowerContent = content.toLowerCase(Locale.getDefault());
        int score = 45; // Base score

        // Section checks
        if (lowerContent.contains("experience"))
            score += 15;
        if (lowerContent.contains("education"))
            score += 10;
        if (lowerContent.contains("skills"))
            score += 10;

        // Role-specific keyword matching
        if (roles != null) {
            for (String role : roles) {
                if (role.contains("Android") && (lowerContent.contains("java") || lowerContent.contains("kotlin")))
                    score += 10;
                if (role.contains("Web") && (lowerContent.contains("javascript") || lowerContent.contains("react")))
                    score += 10;
                if (role.contains("Data") && (lowerContent.contains("python") || lowerContent.contains("sql")))
                    score += 10;
                if (role.contains("Designer") && (lowerContent.contains("figma") || lowerContent.contains("adobe")))
                    score += 10;
            }
        }

        score += (int) (Math.random() * 8 - 4);
        return Math.min(score, 99);
    }

    public static java.util.List<String> getStrengths(java.util.List<String> roles, String content) {
        java.util.List<String> strengths = new java.util.ArrayList<>();
        if (roles == null || roles.isEmpty()) {
            strengths.add("Professional Layout");
            strengths.add("Clear Contact Info");
            return strengths;
        }

        for (String role : roles) {
            if (role.contains("Android"))
                strengths.add("Mobile App Proficiency");
            if (role.contains("Web"))
                strengths.add("Frontend Knowledge");
            if (role.contains("Data"))
                strengths.add("Analytical Thinking");
            if (role.contains("Product"))
                strengths.add("Strategic Planning");
        }

        if (strengths.size() < 3) {
            strengths.add("Concise Summary");
            strengths.add("Action-Oriented Verbs");
        }
        return strengths;
    }

    public static java.util.List<String> getSkillGaps(java.util.List<String> roles, String content) {
        java.util.List<String> gaps = new java.util.ArrayList<>();
        if (roles == null)
            return gaps;

        for (String role : roles) {
            if (role.contains("Android"))
                gaps.add("Jetpack Compose");
            if (role.contains("Web"))
                gaps.add("Next.js");
            if (role.contains("Data"))
                gaps.add("Cloud ML");
            if (role.contains("Designer"))
                gaps.add("3D Design");
        }

        if (gaps.isEmpty())
            gaps.add("System Design");

        // Limit to 2 chips as requested before
        if (gaps.size() > 2)
            return gaps.subList(0, 2);
        return gaps;
    }

    public static java.util.List<String> getRoadmap(java.util.List<String> roles) {
        java.util.List<String> steps = new java.util.ArrayList<>();
        if (roles == null || roles.isEmpty()) {
            steps.add("Update Skills Section|Highlight your latest technical achievements.");
            steps.add("Optimize for ATS|Use standard section headings and fonts.");
            return steps;
        }

        String mainRole = roles.get(0);
        if (mainRole.contains("Android")) {
            steps.add("Master Jetpack Compose|Modern UI toolkit for Android.");
            steps.add("Build a Kotlin Project|Showcase advanced language features.");
        } else if (mainRole.contains("Web")) {
            steps.add("Learn Next.js 14|Modern full-stack web framework.");
            steps.add("Optimize Performance|Focus on Core Web Vitals.");
        } else {
            steps.add("Earn a Certification|Validate your expertise in this domain.");
            steps.add("Network with Peers|Join professional communities.");
        }
        return steps;
    }

    public static boolean isLikelyResume(String fileName, String content) {
        if (content == null)
            return false;
        String lowerContent = content.toLowerCase();
        return lowerContent.contains("experience") || lowerContent.contains("education") ||
                lowerContent.contains("skills") || (fileName != null && fileName.toLowerCase().contains("resume"));
    }
}
