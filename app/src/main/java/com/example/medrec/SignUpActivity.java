package com.example.medrec;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.NonNull;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.util.HashMap;

public class SignUpActivity extends BaseNoNavActivity {
    private EditText editUsername, editEmail, editPassword, editConfirmPassword;
    private Spinner spinnerAge, spinnerEthnicity, spinnerLocation;
    private CheckBox checkPersonalization;
    private Button btnSignUp;
    private ProgressDialog progressDialog;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private final String[] ageGroups = {"Prefer not to say", "Under 12", "12–17", "18–25", "26–35", "36–45", "46+"};
    private final String[] ethnicities = {"Prefer not to say", "South Asian", "Arab", "African", "White", "Other"};
    private final String[] locations = {"Prefer not to say", "Oman", "UAE", "India", "Pakistan", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setPageTitle("Sign Up");

        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        spinnerAge = findViewById(R.id.spinnerAge);
        spinnerEthnicity = findViewById(R.id.spinnerEthnicity);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        checkPersonalization = findViewById(R.id.checkPersonalization);
        btnSignUp = findViewById(R.id.btnSignUp);
        progressDialog = new ProgressDialog(this);

        spinnerAge.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ageGroups));
        spinnerEthnicity.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ethnicities));
        spinnerLocation.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations));
        checkPersonalization.setChecked(true);

        // Only last field needs action listener for submit
        editConfirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                btnSignUp.performClick();
                return true;
            }
            return false;
        });

        // Password visibility toggles
        editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
        editConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);

        editPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editPassword.getRight() - editPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    isPasswordVisible = !isPasswordVisible;
                    if (isPasswordVisible) {
                        editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                    } else {
                        editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
                    }
                    editPassword.setSelection(editPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

        editConfirmPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editConfirmPassword.getRight() - editConfirmPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    isConfirmPasswordVisible = !isConfirmPasswordVisible;
                    if (isConfirmPasswordVisible) {
                        editConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                    } else {
                        editConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
                    }
                    editConfirmPassword.setSelection(editConfirmPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

        btnSignUp.setOnClickListener(v -> attemptSignUp());

        // Clickable link to Login
        TextView textLogin = findViewById(R.id.textLogin);
        textLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void attemptSignUp() {
        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(username) || username.contains(" ")) {
            editUsername.setError("Enter a valid username (no spaces)");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Enter your email");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            editPassword.setError("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            editConfirmPassword.setError("Passwords do not match");
            return;
        }

        progressDialog.setMessage("Checking username...");
        progressDialog.show();

        DatabaseReference usernamesRef = FirebaseDatabase.getInstance().getReference("Usernames");
        usernamesRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressDialog.dismiss();
                    editUsername.setError("Username already taken");
                } else {
                    registerUser(username, email, password);
                }
            }
            public void onCancelled(@NonNull DatabaseError error) { progressDialog.dismiss(); }
        });
    }

    private void registerUser(String username, String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = task.getResult().getUser().getUid();
                        saveUserToDatabase(userId, username, email);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Sign Up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToDatabase(String userId, String username, String email) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String ageGroup = spinnerAge.getSelectedItem().toString();
        String ethnicity = spinnerEthnicity.getSelectedItem().toString();
        String location = spinnerLocation.getSelectedItem().toString();
        boolean personalization = checkPersonalization.isChecked();

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("email", email);
        userMap.put("ageGroup", ageGroup);
        userMap.put("ethnicity", ethnicity);
        userMap.put("location", location);
        userMap.put("personalizationAllowed", personalization);

        db.child("Users").child(userId).setValue(userMap);
        db.child("Usernames").child(username).setValue(userId)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to save user info.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}





