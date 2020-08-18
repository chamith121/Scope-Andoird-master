package lk.archmage.scopecinemas;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import lk.archmage.scopecinemas.Classes.MovieCastingAdapter;
import lk.archmage.scopecinemas.Models.ComingSoonMovieData;
import lk.archmage.scopecinemas.Models.NowShowingMovieData;


public class MovieTeamAndCast extends Fragment {

    public MovieTeamAndCast() {
    }

    NowShowingMovieData nowShowingMovieData;
    ComingSoonMovieData comingSoonMovieData;

    TextView directerNameTv, producedNameTv, writtenNameTv, musicNameTv, msgTv;

    ListView castListView;
    MovieCastingAdapter adapter;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_team_and_cast, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        castListView = view.findViewById(R.id.castListView);
        directerNameTv = view.findViewById(R.id.directerNameTv);
        producedNameTv = view.findViewById(R.id.producedNameTv);
        writtenNameTv = view.findViewById(R.id.writtenNameTv);
        musicNameTv = view.findViewById(R.id.musicNameTv);

        msgTv = view.findViewById(R.id.msgTv);
        nowShowingMovieData = getArguments().getParcelable("nowShowningMovieOBJ");
        comingSoonMovieData = getArguments().getParcelable("comingSoonMovieDataObj");

        if(nowShowingMovieData != null){


            if(nowShowingMovieData.getDirectors().size() != 0){

                ArrayList<String> directorsArray = nowShowingMovieData.getDirectors();
                String directors = "";

                for(int a = 0; a < directorsArray.size(); a++){

                    directors += directorsArray.get(a)+ ", ";
                }

                if (directors != null && directors.length() > 0 && directors.charAt(directors.length() - 2) == ',') {
                    directors = directors.substring(0, directors.length() - 2);
                }
                directerNameTv.setText(directors);

            }else {
                directerNameTv.setText("");
            }

            if(nowShowingMovieData.getProducers().size() != 0){

                ArrayList<String> producersArray = nowShowingMovieData.getProducers();
                String producers = "";

                for(int a = 0; a < producersArray.size(); a++){

                    producers += producersArray.get(a)+ ", ";
                }

                if (producers != null && producers.length() > 0 && producers.charAt(producers.length() - 2) == ',') {
                    producers = producers.substring(0, producers.length() - 2);
                }
                producedNameTv.setText(producers);

            }else {
                producedNameTv.setText("");
            }

            if(nowShowingMovieData.getWritters().size() != 0){

                ArrayList<String> writersArray = nowShowingMovieData.getWritters();
                String writers = "";

                for(int i = 0; i < writersArray.size(); i++){

                    writers += writersArray.get(i)+ ", ";
                }

                if (writers != null && writers.length() > 0 && writers.charAt(writers.length() - 2) == ',') {
                    writers = writers.substring(0, writers.length() - 2);
                }
                writtenNameTv.setText(writers);
            }else {
                writtenNameTv.setText("");
            }

            if(nowShowingMovieData.getMusicians().size() != 0){

                ArrayList<String> musiciansArray = nowShowingMovieData.getMusicians();
                String musicians = "";

                for(int i = 0; i < musiciansArray.size(); i++){

                    musicians += musiciansArray.get(i)+ ", ";
                }

                if (musicians != null && musicians.length() > 0 && musicians.charAt(musicians.length() - 2) == ',') {
                    musicians = musicians.substring(0, musicians.length() - 2);
                }
                musicNameTv.setText(musicians);
            }else {
                musicNameTv.setText("");
            }

            ArrayList castingArray = nowShowingMovieData.getCastObject();

            if(castingArray.size() != 0) {

                msgTv.setVisibility(View.GONE);
                castListView.setVisibility(View.VISIBLE);
                adapter = new MovieCastingAdapter(getContext(), castingArray);
                adapter.notifyDataSetChanged();
                castListView.setAdapter(adapter);

            }else {
                castListView.setVisibility(View.GONE);
                msgTv.setVisibility(View.VISIBLE);
            }


        }else if(comingSoonMovieData != null){

            if(comingSoonMovieData.getDirectors().size() != 0){

                ArrayList<String> directorsArray = comingSoonMovieData.getDirectors();
                String directors = "";

                for(int a = 0; a < directorsArray.size(); a++){

                    directors += directorsArray.get(a)+ ", ";
                }

                if (directors != null && directors.length() > 0 && directors.charAt(directors.length() - 2) == ',') {
                    directors = directors.substring(0, directors.length() - 2);
                }
                directerNameTv.setText(directors);

            }else {
                directerNameTv.setText("");
            }

            if(comingSoonMovieData.getProducers().size() != 0){

                ArrayList<String> producersArray = comingSoonMovieData.getProducers();
                String producers = "";

                for(int a = 0; a < producersArray.size(); a++){

                    producers += producersArray.get(a)+ ", ";
                }

                if (producers != null && producers.length() > 0 && producers.charAt(producers.length() - 2) == ',') {
                    producers = producers.substring(0, producers.length() - 2);
                }
                producedNameTv.setText(producers);

            }else {
                producedNameTv.setText("");
            }

            if(comingSoonMovieData.getWritters().size() != 0){

                ArrayList<String> writersArray = comingSoonMovieData.getWritters();
                String writers = "";

                for(int i = 0; i < writersArray.size(); i++){

                    writers += writersArray.get(i)+ ", ";
                }

                if (writers != null && writers.length() > 0 && writers.charAt(writers.length() - 2) == ',') {
                    writers = writers.substring(0, writers.length() - 2);
                }
                writtenNameTv.setText(writers);
            }else {
                writtenNameTv.setText("");
            }

            if(comingSoonMovieData.getMusicians().size() != 0){

                ArrayList<String> musiciansArray = comingSoonMovieData.getMusicians();
                String musicians = "";

                for(int i = 0; i < musiciansArray.size(); i++){

                    musicians += musiciansArray.get(i)+ ", ";
                }

                if (musicians != null && musicians.length() > 0 && musicians.charAt(musicians.length() - 2) == ',') {
                    musicians = musicians.substring(0, musicians.length() - 2);
                }
                musicNameTv.setText(musicians);
            }else {
                musicNameTv.setText("");
            }

            ArrayList castingArray = comingSoonMovieData.getCastObject();

            if(castingArray.size() != 0) {

                msgTv.setVisibility(View.GONE);
                castListView.setVisibility(View.VISIBLE);
                adapter = new MovieCastingAdapter(getContext(), castingArray);
                adapter.notifyDataSetChanged();
                castListView.setAdapter(adapter);

            }else {
                castListView.setVisibility(View.GONE);
                msgTv.setVisibility(View.VISIBLE);
            }

        }


        return view;
    }

}
