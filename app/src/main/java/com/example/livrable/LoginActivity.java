package com.example.livrable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView createAccountTextView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progress_bar);
        createAccountTextView = findViewById(R.id.create_account_text_view_btn);

        // Set click listeners
        loginButton.setOnClickListener(this::loginUser);
        createAccountTextView.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class))
        );
    }

    private void loginUser(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (validateData(email, password)) {
            loginToFirebase(email, password);
        }
    }

    private void loginToFirebase(String email, String password) {
        toggleProgress(true);

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            toggleProgress(false);
            if (task.isSuccessful()) {
                if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                    // Go to main activity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Utility.showToast(LoginActivity.this, "Email not verified. Please verify your email.");
                }
            } else {
                // Show error message
                Utility.showToast(LoginActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
            }
        });
    }

    private void toggleProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateData(String email, String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters long");
            return false;
        }
        return true;
    }
}
