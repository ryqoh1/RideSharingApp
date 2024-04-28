package edu.uga.cs.ridesharingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RideOffersFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_offers, container, false);
    }

    public void fetchRides() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("rides");
        ref.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle fetched rides
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    public void postRideOffer(Ride ride) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("rideOffers");
        ref.push().setValue(ride);
    }
    
    public void acceptRideOffer(String rideId, String riderId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("rideOffers").child(rideId);
        ref.child("acceptedBy").setValue(riderId);
        // Move to accepted rides and adjust points
    }

    public void confirmRide(String rideId, String driverId, String riderId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("confirmedRides");
        ref.child(rideId).setValue(new ConfirmedRide(driverId, riderId));
        // Adjust points: increment for driver, decrement for rider
    }
}