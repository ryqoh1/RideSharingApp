package edu.uga.cs.ridesharingapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RideRequestsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText editTextRequestDate = view.findViewById(R.id.editTextRequestDate);
        EditText editTextRequestDestination = view.findViewById(R.id.editTextRequestDestination);
        Button buttonSubmitRequest = view.findViewById(R.id.buttonSubmitRideRequest);
        ListView listViewRideRequests = view.findViewById(R.id.listViewRideRequests);
        buttonSubmitRequest.setOnClickListener(v -> {
            String date = editTextRequestDate.getText().toString();
            String destination = editTextRequestDestination.getText().toString();
            postRideRequest(date, destination);
        });
        fetchAndDisplayRideRequests(listViewRideRequests);
    }

    private void postRideRequest(String date, String destination) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !date.isEmpty() && !destination.isEmpty()) {
            String userId = user.getUid();
            Ride ride = new Ride(userId, destination, date, 50, "open"); // Assuming points are not needed at creation
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rideRequests");
            ref.push().setValue(ride);
        } else {
            Toast.makeText(getContext(), "You must fill all fields.", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchAndDisplayRideRequests(ListView listView) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rideRequests");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Ride> rideRequests = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ride ride = snapshot.getValue(Ride.class);
                    if (ride != null) {
                        rideRequests.add(ride);
                    }
                }
                RideRequestAdapter adapter = new RideRequestAdapter(getContext(), R.layout.ride_offer_item, rideRequests);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load ride requests.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class RideRequestAdapter extends ArrayAdapter<Ride> {
        private Context context;
        private List<Ride> rideRequests;
        private String currentUserId;

        public RideRequestAdapter(@NonNull Context context, int resource, @NonNull List<Ride> objects) {
            super(context, resource, objects);
            this.context = context;
            this.rideRequests = objects;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                this.currentUserId = user.getUid();
            }
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.ride_offer_item, parent, false);
            }

            TextView textViewDate = convertView.findViewById(R.id.textViewDate);
            TextView textViewDestination = convertView.findViewById(R.id.textViewDestination);
            TextView textViewStatus = convertView.findViewById(R.id.textViewStatus);
            Button buttonAccept = convertView.findViewById(R.id.buttonAccept);

            Ride ride = rideRequests.get(position);
            textViewDate.setText(ride.getDate());
            textViewDestination.setText(ride.getDestination());
            textViewStatus.setText(ride.getStatus());

            if (ride.getUserId().equals(currentUserId)) {
                buttonAccept.setEnabled(false);
                buttonAccept.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
            } else {
                buttonAccept.setEnabled(true);
                buttonAccept.setBackgroundColor(context.getResources().getColor(android.R.color.holo_purple));
            }

            return convertView;
        }
    }
}