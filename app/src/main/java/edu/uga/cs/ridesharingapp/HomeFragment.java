package edu.uga.cs.ridesharingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.widget.ListView;
import android.widget.ArrayAdapter;
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

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, acceptedRidesList);
        listViewAcceptedRides.setAdapter(adapter);

        fetchAcceptedRides();

        return view;
    }

    private void fetchAcceptedRides() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference refOffers = FirebaseDatabase.getInstance().getReference("rideOffers");
            DatabaseReference refRequests = FirebaseDatabase.getInstance().getReference("rideRequests");

            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    acceptedRidesList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Ride ride = snapshot.getValue(Ride.class);
                        if (ride != null && user.getUid().equals(ride.getAcceptedBy())) {
                            acceptedRidesList.add(ride);
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
            refOffers.orderByChild("acceptedBy").equalTo(user.getUid()).addValueEventListener(listener);
            refRequests.orderByChild("acceptedBy").equalTo(user.getUid()).addValueEventListener(listener);
        }
    }
}

