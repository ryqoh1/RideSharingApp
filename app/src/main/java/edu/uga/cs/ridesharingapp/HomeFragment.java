package edu.uga.cs.ridesharingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.Button;

public class HomeFragment extends Fragment {
    private Button btnOfferRide;
    private Button btnRequestRide;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnOfferRide = view.findViewById(R.id.btnOfferRide);
        btnRequestRide = view.findViewById(R.id.btnRequestRide);

        btnOfferRide.setOnClickListener(v -> navigateToOfferRide());
        btnRequestRide.setOnClickListener(v -> navigateToRequestRide());

        return view;
    }
    private void navigateToOfferRide() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, new RideOffersFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToRequestRide() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, new RideRequestsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

