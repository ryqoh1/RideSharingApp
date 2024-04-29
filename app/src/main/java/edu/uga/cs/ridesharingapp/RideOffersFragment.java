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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RideOffersFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_offers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText editTextDate = view.findViewById(R.id.editTextDate);
        EditText editTextDestination = view.findViewById(R.id.editTextDestination);
        Button buttonSubmit = view.findViewById(R.id.buttonSubmitRideOffer);
        ListView listViewRideOffers = view.findViewById(R.id.listViewRideOffers);
    
        buttonSubmit.setOnClickListener(v -> {
            String date = editTextDate.getText().toString();
            String destination = editTextDestination.getText().toString();
            postRideOffer(date, destination);
        });

        fetchAndDisplayRideOffers(listViewRideOffers);
    }

    private void postRideOffer(String date, String destination) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !date.isEmpty() && !destination.isEmpty()) {
            String userId = user.getUid();
            Ride ride = new Ride(userId, destination, date, 50, "open"); // Assuming points are not needed at creation
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rideOffers");
            ref.push().setValue(ride);
        } else {
            Toast.makeText(getContext(), "You must fill all fields.", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchAndDisplayRideOffers(ListView listView) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rideOffers");
        ref.orderByChild("status").equalTo("open").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Ride> rideOffers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ride ride = snapshot.getValue(Ride.class);
                    if (ride != null) {
                        rideOffers.add(ride);
                    }
                }
                RideOfferAdapter adapter = new RideOfferAdapter(getContext(), R.layout.ride_offer_item, rideOffers);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load ride offers.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void acceptRideOffer(String rideId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rideOffers").child(rideId);
            ref.child("status").setValue("accepted");
            ref.child("acceptedBy").setValue(userId);
        }
    }

    public void confirmRide(String rideId, String driverId, String riderId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("confirmedRides");
        ref.child(rideId).setValue(new ConfirmedRide(driverId, riderId, "", 0, ""));
        // Adjust points: increment for driver, decrement for rider
    }

    class RideOfferAdapter extends ArrayAdapter<Ride> {
        private Context context;
        private List<Ride> rideOffers;

        public RideOfferAdapter(@NonNull Context context, int resource, @NonNull List<Ride> objects) {
            super(context, resource, objects);
            this.context = context;
            this.rideOffers = objects;
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

            Ride ride = rideOffers.get(position);
            textViewDate.setText(ride.getDate());
            textViewDestination.setText(ride.getDestination());
            textViewStatus.setText(ride.getStatus());

            buttonAccept.setOnClickListener(v -> {
                acceptRideOffer(ride.getRideId());
            });

            return convertView;
        }
    }
}