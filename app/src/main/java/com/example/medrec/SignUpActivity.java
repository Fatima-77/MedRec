package com.example.medrec;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText usernameInput, emailInput, passwordInput;
    private ImageView togglePassword;
    private Button signupButton;
    private TextView loginLink;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        togglePassword = findViewById(R.id.toggle_password_visibility); // optional, if added
        signupButton = findViewById(R.id.signup_button);
        loginLink = findViewById(R.id.login_link);

        // Optional: Toggle password visibility
        if (togglePassword != null) {
            final boolean[] isVisible = {false};
            togglePassword.setOnClickListener(v -> {
                if (isVisible[0]) {
                    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePassword.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    togglePassword.setImageResource(R.drawable.ic_visibility);
                }
                passwordInput.setSelection(passwordInput.getText().length());
                isVisible[0] = !isVisible[0];
            });
        }

        signupButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(user.getUid())
                                        .child("Profile")
                                        .setValue(new UserProfile(username, email));
                            }
                            Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    public static class UserProfile {
        public String username;
        public String email;

        public UserProfile() {
        }

        public UserProfile(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}
