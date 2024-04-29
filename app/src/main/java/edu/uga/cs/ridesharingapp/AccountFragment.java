package edu.uga.cs.ridesharingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Toast;
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
                    Toast.makeText(getActivity(), "Reset password email sent.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                }
            });
    }


    public void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        // Redirect to LoginActivity or another appropriate activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
        startActivity(intent);
    }
}

