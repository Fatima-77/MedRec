package com.example.medrec;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.widget.ImageView;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginButton;
    TextView signupLink, resetPasswordLink;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        // Check Firebase Connection
        if (firebaseAuth != null) {
            Toast.makeText(this, "Firebase is connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Firebase is NOT connected", Toast.LENGTH_LONG).show();
        }

        // Find Views
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        signupLink = findViewById(R.id.signup_link);
        resetPasswordLink = findViewById(R.id.reset_password);

        EditText passwordInput = findViewById(R.id.password_input);
        ImageView toggleVisibility = findViewById(R.id.toggle_password_visibility);

        // Track visibility state
        final boolean[] isPasswordVisible = {false};

        toggleVisibility.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                // Hide password
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleVisibility.setImageResource(R.drawable.ic_visibility_off);
            } else {
                // Show password
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleVisibility.setImageResource(R.drawable.ic_visibility);
            }
            // Move cursor to the end after toggling
            passwordInput.setSelection(passwordInput.getText().length());
            isPasswordVisible[0] = !isPasswordVisible[0];
        });


        // Login Button Click
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Sign-Up Link Click
        signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Password Reset Click
        resetPasswordLink.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}




