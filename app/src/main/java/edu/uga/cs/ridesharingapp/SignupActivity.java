package edu.uga.cs.ridesharingapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        EditText emailInput = findViewById(R.id.signup_email);
        EditText passwordInput = findViewById(R.id.signup_password);
        Button signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(view -> createAccount(emailInput.getText().toString(), passwordInput.getText().toString()));
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Write additional user data to Firebase Database
                            String userId = user.getUid();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference usersRef = database.getReference("users").child(userId);

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("email", email);
                            userData.put("ridePoints", 50); // Assuming 50 is the initial points

                            usersRef.setValue(userData)
                                .addOnSuccessListener(aVoid -> {
                                    // Data write successful
                                    updateUI(user);
                                })
                                .addOnFailureListener(e -> {
                                    // Data write failed
                                    Toast.makeText(SignupActivity.this, "Failed to write user data to database.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                });
                        }
                    } else {
                        // If sign up fails, display a message to the user.
                        Toast.makeText(SignupActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Navigate to MainActivity or other activity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Stay on the signup screen
        }
    }
}
