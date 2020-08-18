package lk.archmage.scopecinemas;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;


public class TermsAndConditions extends Fragment {


    public TermsAndConditions() {
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        return view;
    }

}
