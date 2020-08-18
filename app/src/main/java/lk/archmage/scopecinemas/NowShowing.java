package lk.archmage.scopecinemas;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import lk.archmage.scopecinemas.Models.NowShowingMovieData;


public class NowShowing extends Fragment {

    public ImageView ivArraowUp;

    public NowShowing() {

    }

    static NowShowing nowShowing;


    //arrow btn
    boolean MotionEventMove = false;
    int MotionEventMoveCount = 0;
    boolean MotionEventUp = false;

    public static NowShowing getInstance() {
        return nowShowing;
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_now_showing, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        nowShowing = this;

        final Bundle bundel = getArguments();

        ArrayList<NowShowingMovieData> nowShowingMovieArray = bundel.getParcelableArrayList("nowShowningMovieList");

        MainScrollerPage frag = new MainScrollerPage();
        frag.setArguments(bundel);
        loadFragment(frag);

        ivArraowUp = view.findViewById(R.id.ivArraowUp);
        ivArraowUp.setVisibility(View.VISIBLE);

        Animation connectingAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        ivArraowUp.startAnimation(connectingAnimation);


        ivArraowUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    MotionEventMove = true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    MotionEventUp = true;
                }

                if (MotionEventUp && MotionEventMove) {
                    ivArraowUp.clearAnimation();
                    ivArraowUp.setVisibility(View.GONE);

                    NowShowingMovies fragment = new NowShowingMovies();
                    fragment.setArguments(bundel);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_up_page, 0, R.anim.slide_down, 0);
                    transaction.replace(R.id.nowShowingFrame, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

                return true;
            }
        });


        return view;
    }

    public void loadFragment(Fragment fragment) {

//        Animation connectingAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
//
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nowShowingFrame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
