package edu.uga.cs.ridesharingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;


public class AccountFragment extends Fragment {

    private EditText emailInput;
    private Button resetPasswordButton, logoutButton;
    private TextView ridePointsTextView;

    /**
     * Inflates the layout for this fragment, initializes UI components, and sets up event listeners.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the inflated fragment's layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        emailInput = view.findViewById(R.id.etEmailForReset);
        resetPasswordButton = view.findViewById(R.id.btnResetPassword);
        logoutButton = view.findViewById(R.id.btnLogOut);
        ridePointsTextView = view.findViewById(R.id.tvRidePoints);

        resetPasswordButton.setOnClickListener(v -> resetPassword(emailInput.getText().toString()));
        logoutButton.setOnClickListener(v -> logoutUser());

        loadRidePoints();
        return view;
    }

    /**
     * Displays a toast message if the fragment is currently added to its activity.
     *
     * @param message The message to display in the toast.
     */
    private void showToastMessage(String message) {
        if (isAdded() && getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads and displays the user's ride points from Firebase.
     */
    private void loadRidePoints() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid(); // Correctly declared and initialized
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId).child("ridePoints");
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && isAdded()) {
                        Integer points = dataSnapshot.getValue(Integer.class);
                        ridePointsTextView.setText("Ride Points: " + (points != null ? points.toString() : "0"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if (isAdded()) {
                        showToastMessage("Failed to load ride points.");
                    }
                }
                });
            } else {
                showToastMessage("User is not logged in.");
            }
        }

    /**
     * Sends a password reset email to the user.
     *
     * @param emailAddress The email address to which the reset password email will be sent.
     */
    public void resetPassword(String emailAddress) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showToastMessage("Reset password email sent.");
            } else {
                showToastMessage("Failed to send reset email.");
            }
        });
    }

    /**
     * Logs out the user and redirects to the login activity.
     */
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        if (isAdded()) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
