package edu.uga.cs.ridesharingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    private EditText emailInput; // For password reset
    private Button resetPasswordButton;
    private Button logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize views
        emailInput = view.findViewById(R.id.etEmailForReset);
        resetPasswordButton = view.findViewById(R.id.btnResetPassword);
        logoutButton = view.findViewById(R.id.btnLogOut);

        resetPasswordButton.setOnClickListener(v -> resetPassword(emailInput.getText().toString()));
        logoutButton.setOnClickListener(v -> logoutUser());

        return view;
    }


    public void resetPassword(String emailAddress) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Notify user that reset email was sent
                        // You could update UI here or show a message
                    } else {
                        // Handle errors (e.g., invalid email)
                        // Display error message to the user
                    }
                });
    }


    public void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        // User is now logged out
    }
}

