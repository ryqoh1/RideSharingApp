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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;


public class HomeFragment extends Fragment {
    private ListView listViewAcceptedRides;
    private ArrayAdapter<Ride> adapter;
    private List<Ride> acceptedRidesList = new ArrayList<>();

    /**
     * Inflates the layout for this fragment and initializes UI components.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the inflated fragment's layout.
     */
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

    /**
     * Fetches accepted rides from Firebase and updates the ListView.
     */
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

    /**
     * Confirms a ride offer or request by updating its status in Firebase and potentially adjusting user points.
     *
     * @param rideId The unique identifier of the ride.
     * @param confirmButton The button used to trigger the confirmation, which will be disabled after confirmation.
     * @param isRideOffer Indicates whether the ride is an offer or a request.
     */
    public void confirmRideOffer(String rideId, Button confirmButton, boolean isRideOffer) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(isRideOffer ? "rideOffers" : "rideRequests").child(rideId);
            
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Ride ride = dataSnapshot.getValue(Ride.class);
                    if (ride != null) {
                        Map<String, Object> updates = new HashMap<>();
                        final boolean[] updatePoints = new boolean[1]; // Use an array to hold the boolean
                        if (ride.getConfirmedBy_1() == null) {
                            updates.put("confirmedBy_1", userId);
                        } else if (ride.getConfirmedBy_2() == null) {
                            updates.put("confirmedBy_2", userId);
                            updatePoints[0] = true; // Points should be updated only after the second confirmation
                        } else {
                            Toast.makeText(getContext(), "Ride already fully confirmed.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        ref.updateChildren(updates).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Ride offer confirmed.", Toast.LENGTH_SHORT).show();
                                // Disable the confirm button
                                confirmButton.setEnabled(false);
                                confirmButton.setBackgroundColor(getContext().getResources().getColor(android.R.color.darker_gray));
                                if (updatePoints[0]) {
                                    adjustUserPoints(ride, isRideOffer);
                                }
                            } else {
                                Toast.makeText(getContext(), "Failed to confirm ride offer.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to read ride data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    /**
     * Adjusts user points based on the confirmation of a ride offer or request.
     *
     * @param ride The Ride object containing details about the ride.
     * @param isRideOffer Indicates whether the ride is an offer or a request.
     */
    private void adjustUserPoints(Ride ride, boolean isRideOffer) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        String acceptedBy = ride.getAcceptedBy();
        String rideOwner = ride.getUserId();
        
        // Adjust points for ride offers
        if (isRideOffer) {
            usersRef.child(acceptedBy).child("ridePoints").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Integer currentPoints = mutableData.getValue(Integer.class);
                    if (currentPoints == null) {
                        return Transaction.success(mutableData);
                    }
                    mutableData.setValue(currentPoints - 50);
                    return Transaction.success(mutableData);
                }
                
                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    // Handle completion
                }
            });
            usersRef.child(rideOwner).child("ridePoints").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Integer currentPoints = mutableData.getValue(Integer.class);
                    if (currentPoints == null) {
                        return Transaction.success(mutableData);
                    }
                    mutableData.setValue(currentPoints + 50);
                    return Transaction.success(mutableData);
                }
                
                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    // Handle completion
                }
            });
        } else { // Adjust points for ride requests
            usersRef.child(acceptedBy).child("ridePoints").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Integer currentPoints = mutableData.getValue(Integer.class);
                    if (currentPoints == null) {
                        return Transaction.success(mutableData);
                    }
                    mutableData.setValue(currentPoints + 50); // Increment for the user who accepted the ride
                    return Transaction.success(mutableData);
                }
                
                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    // Handle completion
                }
            });
            usersRef.child(rideOwner).child("ridePoints").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Integer currentPoints = mutableData.getValue(Integer.class);
                    if (currentPoints == null) {
                        return Transaction.success(mutableData);
                    }
                    mutableData.setValue(currentPoints - 50); // Decrement for the ride owner
                    return Transaction.success(mutableData);
                }
                
                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    // Handle completion
                }
            });
        }
    }

    /**
     * Custom ArrayAdapter for displaying rides in a ListView.
     */
    class RideAdapter extends ArrayAdapter<Ride> {
        private Context context;
        private List<Ride> rides;

        public RideAdapter(@NonNull Context context, @NonNull List<Ride> rides) {
            super(context, 0, rides);
            this.context = context;
            this.rides = rides;
        }

        /**
         * Provides a view for an AdapterView (ListView, GridView, etc.)
         *
         * @param position The position in the list of data that should be displayed in the list item view.
         * @param convertView The old view to reuse, if possible.
         * @param parent The parent that this view will eventually be attached to.
         * @return A View corresponding to the data at the specified position.
         */
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