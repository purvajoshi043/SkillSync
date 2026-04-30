package com.example.skillsync;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View branding = view.findViewById(R.id.layoutBranding);
        View loginCard = view.findViewById(R.id.layoutLoginCard);
        TextInputLayout tilEmail = view.findViewById(R.id.tilEmail);
        TextInputLayout tilPassword = view.findViewById(R.id.tilPassword);
        TextInputEditText etEmail = view.findViewById(R.id.etEmail);
        TextInputEditText etPassword = view.findViewById(R.id.etPassword);
        MaterialButton btnLogin = view.findViewById(R.id.btnLogin);
        MaterialButton btnGoogle = view.findViewById(R.id.btnGoogle);
        TextView tvSignUp = view.findViewById(R.id.tvSignUp);
        TextView tvForgotPassword = view.findViewById(R.id.tvForgotPassword);

        // ── Entry animations ──────────────────────────────────────────────
        // Branding fades + slides down from above
        branding.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(100)
                .setDuration(600)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Card slides up from below with a slight overshoot
        loginCard.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(250)
                .setDuration(700)
                .setInterpolator(new OvershootInterpolator(0.8f))
                .start();

        // ── Login ─────────────────────────────────────────────────────────
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            boolean valid = true;

            if (TextUtils.isEmpty(email)) {
                tilEmail.setError("Email is required");
                valid = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.setError("Enter a valid email");
                valid = false;
            } else {
                tilEmail.setError(null);
            }

            if (TextUtils.isEmpty(password)) {
                tilPassword.setError("Password is required");
                valid = false;
            } else if (password.length() < 6) {
                tilPassword.setError("Minimum 6 characters");
                valid = false;
            } else {
                tilPassword.setError(null);
            }

            if (valid) {
                // Animate button briefly, then navigate
                btnLogin.setText("Signing in…");
                btnLogin.setEnabled(false);
                btnLogin.postDelayed(() -> {
                    if (getView() != null) {
                        Navigation.findNavController(getView())
                                .navigate(R.id.action_login_to_onboarding);
                    }
                }, 800);
            }
        });

        // ── Google (mock) ─────────────────────────────────────────────────
        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(requireContext(),
                    "Google Sign-In coming soon!", Toast.LENGTH_SHORT).show();
        });

        // ── Create Account → go to Onboarding ────────────────────────────
        tvSignUp.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_login_to_onboarding));

        // ── Forgot Password (mock) ─────────────────────────────────────────
        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(requireContext(),
                        "Password reset email sent!", Toast.LENGTH_SHORT).show());
    }
}
