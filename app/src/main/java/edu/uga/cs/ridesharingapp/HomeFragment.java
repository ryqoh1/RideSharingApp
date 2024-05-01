package edu.uga.cs.ridesharingapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.Nullable;


public class HomeFragment extends Fragment {
    private ListView listViewAcceptedRides;
    private ArrayAdapter<Ride> adapter;
    private List<Ride> acceptedRidesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        listViewAcceptedRides = view.findViewById(R.id.listViewAcceptedRides);

        adapter = new RideAdapter(getContext(), acceptedRidesList);
        listViewAcceptedRides.setAdapter(adapter);

        fetchAcceptedRides();

        return view;
    }

    private void fetchAcceptedRides() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference refOffers = FirebaseDatabase.getInstance().getReference("rideOffers");
            DatabaseReference refRequests = FirebaseDatabase.getInstance().getReference("rideRequests");

            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    acceptedRidesList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Ride ride = snapshot.getValue(Ride.class);
                        if (ride != null) {
                            // Check if the ride is accepted by the user or if it's the user's ride with status 'accepted'
                            if (ride.getAcceptedBy() != null && ride.getAcceptedBy().equals(userId) ||
                                (ride.getUserId().equals(userId) && ride.getStatus().equals("accepted"))) {
                                acceptedRidesList.add(ride);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged(); // Refresh the list
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                }
            };

            // Add the listeners to both references
            refOffers.addValueEventListener(listener);
            refRequests.addValueEventListener(listener);
        }
    }

    class RideAdapter extends ArrayAdapter<Ride> {
        private Context context;
        private List<Ride> rides;

        public RideAdapter(@NonNull Context context, @NonNull List<Ride> rides) {
            super(context, 0, rides);
            this.context = context;
            this.rides = rides;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.ride_item, parent, false);
            }

            TextView textViewRideDetails = convertView.findViewById(R.id.textViewRideDetails);
            Button buttonConfirm = convertView.findViewById(R.id.buttonConfirm);

            Ride ride = getItem(position);
            if (ride != null) {
                textViewRideDetails.setText(ride.toString()); // Assuming Ride.toString() method gives all necessary details
            }

            // Set up the button (functionality to be added later)
            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Placeholder for confirmation functionality
                    Toast.makeText(getContext(), "Confirm clicked for " + ride.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }
    }
}