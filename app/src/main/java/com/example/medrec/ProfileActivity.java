package com.example.medrec;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

public class ProfileActivity extends BaseActivity {
    private TextView textUsername, textEmail;
    private Spinner spinnerAge, spinnerEthnicity, spinnerLocation;
    private CheckBox checkPersonalization;
    private Button btnSave;

    private final List<String> ageGroups = Arrays.asList("Prefer not to say","Under 12","12–17","18–25","26–35","36–45","46+");
    private final List<String> ethnicities = Arrays.asList("Prefer not to say","South Asian","Arab","African","White","Other");
    private final List<String> locations = Arrays.asList("Prefer not to say","Oman","UAE","India","Pakistan","Other");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setPageTitle("Profile");

        // Find views
        textUsername        = findViewById(R.id.textUsername);
        textEmail           = findViewById(R.id.textEmail);
        spinnerAge          = findViewById(R.id.spinnerAge);
        spinnerEthnicity    = findViewById(R.id.spinnerEthnicity);
        spinnerLocation     = findViewById(R.id.spinnerLocation);
        checkPersonalization= findViewById(R.id.checkPersonalization);
        btnSave             = findViewById(R.id.btnSaveProfile);

        // Populate spinners
        spinnerAge.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ageGroups));
        spinnerEthnicity.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ethnicities));
        spinnerLocation.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations));

        // Load current user data
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                if (!snap.exists()) return;
                String username = snap.child("username").getValue(String.class);
                String email    = snap.child("email").getValue(String.class);
                String age      = snap.child("ageGroup").getValue(String.class);
                String eth      = snap.child("ethnicity").getValue(String.class);
                String loc      = snap.child("location").getValue(String.class);
                Boolean per     = snap.child("personalizationAllowed").getValue(Boolean.class);

                textUsername.setText(username);
                textEmail   .setText(email);

                spinnerAge.setSelection(ageGroups.indexOf(age));
                spinnerEthnicity.setSelection(ethnicities.indexOf(eth));
                spinnerLocation.setSelection(locations.indexOf(loc));
                checkPersonalization.setChecked(per != null && per);

            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });

        btnSave.setOnClickListener(v -> {
            String newAge = spinnerAge.getSelectedItem().toString();
            String newEth = spinnerEthnicity.getSelectedItem().toString();
            String newLoc = spinnerLocation.getSelectedItem().toString();
            boolean newPer= checkPersonalization.isChecked();

            HashMap<String, Object> updates = new HashMap<>();
            updates.put("ageGroup", newAge);
            updates.put("ethnicity", newEth);
            updates.put("location", newLoc);
            updates.put("personalizationAllowed", newPer);

            userRef.updateChildren(updates)
                    .addOnSuccessListener(a -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
        });
    }
}


