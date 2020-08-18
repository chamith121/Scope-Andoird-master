package lk.archmage.scopecinemas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;


public class UpcomingReservations extends Fragment {

    public UpcomingReservations() {

    }

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upcoming_reservations, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        return view;
    }
}
