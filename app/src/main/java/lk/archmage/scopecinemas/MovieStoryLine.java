package lk.archmage.scopecinemas;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import lk.archmage.scopecinemas.Models.ComingSoonMovieData;
import lk.archmage.scopecinemas.Models.NowShowingMovieData;


public class MovieStoryLine extends Fragment {

    TextView synopsisTv;

    NowShowingMovieData nowShowingMovieData;
    ComingSoonMovieData comingSoonMovieData;

    public MovieStoryLine() {
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_story_line, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        synopsisTv = view.findViewById(R.id.synopsisTv);

        nowShowingMovieData = getArguments().getParcelable("nowShowningMovieOBJ");
        comingSoonMovieData = getArguments().getParcelable("comingSoonMovieDataObj");
        if(nowShowingMovieData != null){

            synopsisTv.setText(nowShowingMovieData.getMovieSynopsis());
            synopsisTv.setLineSpacing(0,1.3f);

        }else if(comingSoonMovieData != null){

            synopsisTv.setText(comingSoonMovieData.getMovieSynopsis());
            synopsisTv.setLineSpacing(0,1.3f);
        }

        return view;
    }

}
