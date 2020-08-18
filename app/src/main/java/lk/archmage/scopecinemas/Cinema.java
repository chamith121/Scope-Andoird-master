package lk.archmage.scopecinemas;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.ServerCallBack;
import lk.archmage.scopecinemas.Models.ComingSoonMovieData;
import lk.archmage.scopecinemas.Models.NowShowingMovieData;


public class Cinema extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public Cinema() {

    }

    private ArrayList<NowShowingMovieData> nowShowningMovieList;
    private ArrayList<ComingSoonMovieData> comingSoonMovieList;

    String sharedPrefVersion = "0";

    ExampleBroadcastReceiver exampleBroadcastReceiver = new ExampleBroadcastReceiver();

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cinema, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        viewPager = view.findViewById(R.id.viewpager);

        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        return view;
    }

    ViewPagerAdapter adapter;

    private void setUpViewPager(ViewPager viewPager) {

        adapter = new ViewPagerAdapter(getChildFragmentManager());

        NowShowing nowShowing = new NowShowing();
        Bundle bundleNSMD = new Bundle();
        bundleNSMD.putParcelableArrayList("nowShowningMovieList", nowShowningMovieList);

        nowShowing.setArguments(bundleNSMD);

        ComingSoon comingSoon = new ComingSoon();

        Bundle bundleCSMD = new Bundle();
        bundleCSMD.putParcelableArrayList("comingSoonMovieList", comingSoonMovieList);

        comingSoon.setArguments(bundleCSMD);

        adapter.addFragment(nowShowing, "Now Showing");
        adapter.addFragment(comingSoon, "Coming Soon");

        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        progress.dismiss();


    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);

        }

        @Override
        public Fragment getItem(int i) {
            return mFragmentList.get(i);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }

    }

    ProgressDialog progress;

    public void getNowShowingMovie(String accessToken, final boolean secondAttempt) {

        progress = new ProgressDialog(getContext(), R.style.MyTheme);
        progress.setCancelable(false);
        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        progress.show();

        CallApiMethods.getNowShowingMovies(new ServerCallBack() {

            @Override
            public void onSuccess(Boolean result, JSONObject jOBJ) {

                try {

                    nowShowningMovieList = new ArrayList<>();

                    JSONObject getNowShowingJOBJ = jOBJ;

                    String status = getNowShowingJOBJ.getString("status");

                    if (status.equals("true")) {

                        JSONObject getVersionJOBJ = getNowShowingJOBJ.getJSONObject("version");

                        String version = getVersionJOBJ.getString("andorid");

                        if (CallApiMethods.appVersion.equals(version)) {

                            SharedPreferences appPreferences = getContext().getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
                            SharedPreferences.Editor prEditor = appPreferences.edit();

                            prEditor.putString("version", version);
                            prEditor.commit();


                            boolean tag = false;

                            nowShowningMovieList.clear();

                            JSONArray getMovieData = getNowShowingJOBJ.getJSONArray("movielist");

                            for (int i = 0; i < getMovieData.length(); i++) {

                                JSONObject getData = getMovieData.getJSONObject(i);

                                ArrayList<String> objectCastArray = new ArrayList();
                                ArrayList<String> objectVistaCode = new ArrayList<>();
                                ArrayList<String> objectGenre = new ArrayList<>();
                                ArrayList<String> objectDirectors = new ArrayList<>();
                                ArrayList<String> objectProducers = new ArrayList<>();
                                ArrayList<String> objectMusicians = new ArrayList<>();
                                ArrayList<String> objectWritters = new ArrayList<>();


                                if (tag == false) {

                                    /**---- cast array ----**/
                                    JSONArray getCast = getData.getJSONArray("cast");

                                    for (int a = 0; a < getCast.length(); a++) {

                                        JSONObject getCastData = getCast.getJSONObject(a);
                                        objectCastArray.add(getCastData.toString());

                                    }
                                    /**---- vistacode array ----**/
                                    JSONArray getVistaCode = getData.getJSONArray("vista_code");

                                    for(int a = 0; a < getVistaCode.length(); a++){

                                        String vistaCode = getVistaCode.getString(a);
                                        objectVistaCode.add(vistaCode);
                                    }
                                    /**---- genre array ----**/
                                    JSONArray getGenre = getData.getJSONArray("genre");

                                    for(int a = 0; a < getGenre.length(); a++){

                                        String getGenreData = getGenre.getString(a);
                                        objectGenre.add(getGenreData);
                                    }
                                    /**---- directors array ----**/
                                    JSONArray getDirectors = getData.getJSONArray("directors");

                                    for(int a = 0; a < getDirectors.length(); a++){

                                        String getDirectorData = getDirectors.getString(a);
                                        objectDirectors.add(getDirectorData);
                                    }
                                    /**---- producers array ----**/
                                    JSONArray getProducers = getData.getJSONArray("producers");

                                    for(int a = 0; a < getProducers.length(); a++){

                                        String getProducerseData = getProducers.getString(a);
                                        objectProducers.add(getProducerseData);
                                    }
                                    /**---- musicians array ----**/
                                    JSONArray getMusicians = getData.getJSONArray("musicians");

                                    for(int a = 0; a < getMusicians.length(); a++){

                                        String getMusiciansData = getMusicians.getString(a);
                                        objectMusicians.add(getMusiciansData);
                                    }
                                    /**---- writters array ----**/
                                    JSONArray getWritters = getData.getJSONArray("writters");

                                    for(int a = 0; a < getWritters.length(); a++){

                                        String getWrittersData = getWritters.getString(a);
                                        objectWritters.add(getWrittersData.toString());
                                    }

                                    tag = true;

                                }

                                if (tag == true) {

                                    NowShowingMovieData movieData = new NowShowingMovieData(

                                            getData.getString("mid"),
                                            getData.getString("m_name"),
                                            getData.getString("start_date"),
                                            getData.getString("end_date"),
                                            getData.getString("you_tube_link"),
                                            getData.getString("you_tube_id"),
                                            getData.getString("runtime"),
                                            getData.getString("language"),
                                            getData.getString("is3d_movie"),
                                            getData.getString("synopsis"),
                                            getData.getString("m_img"),
                                            getData.getString("m_large_img"),
                                            getData.getString("mobile_img"),
                                            getData.getString("trailer"),
                                            objectCastArray,
                                            getData.getString("imdb"),
                                            getData.getString("adult"),
                                            objectVistaCode,
                                            objectGenre,
                                            getData.getString("fact"),
                                            getData.getString("featured"),
                                            objectDirectors,
                                            objectProducers,
                                            objectMusicians,
                                            objectWritters

                                    );

                                    tag = false;

                                    nowShowningMovieList.add(movieData);

                                }

                            }

                            if (nowShowningMovieList.size() != 0) {

                                //todo

                                CallApiMethods.getAccessToken(new ApiServerCallBack() {
                                    @Override
                                    public void onSuccess(Boolean result) {
                                        // do nothing
                                    }

                                    @Override
                                    public void onSuccess(Boolean result, String token) {

                                        if (result) {

                                            Functions.dismisSnacbar();

                                            getComingSoonMovie(token, true);

                                        } else {
                                            View view = getActivity().findViewById(android.R.id.content);

                                            Functions.showSnackbar(view);

                                        }
                                    }
                                }, getContext());




                            } else {

                               //todo

                            }


                        } else {

                            startActivity(new Intent(getContext(), VersionPage.class));
                            ((Activity)getContext()).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                        }


                    } else {

                        progress.dismiss();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Boolean result, VolleyError error) {

                progress.dismiss();

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    Functions.showSnackbar(getView());

                } else if (error instanceof AuthFailureError) {

                    if (error.networkResponse.statusCode == 401) {

                        CallApiMethods.getAccessToken(new ApiServerCallBack() {
                            @Override
                            public void onSuccess(Boolean result) {

                            }

                            @Override
                            public void onSuccess(Boolean result, String token) {

                                if (result) {
                                    if (!secondAttempt) {

                                        getNowShowingMovie(token, true);
                                    } else {

                                        Functions.showSnackbar(getView());
                                    }
                                } else {

                                    Functions.showSnackbar(getView());
                                }
                            }
                        }, getContext());

                    }

                } else if (error instanceof ServerError) {

                    Functions.showSnackbar(getView());

                } else if (error instanceof NetworkError) {

                    Functions.showSnackbar(getView());

                } else if (error instanceof ParseError) {

                    Functions.showSnackbar(getView());
                }

            }
        }, getContext(), accessToken, CallApiMethods.userKey);

    }

    public void getComingSoonMovie(String accessToken, final boolean secondAttempt) {

        Log.d("ACCESS_TOKEN", "-----------"+accessToken);

        CallApiMethods.getUpComeingMovies(new ServerCallBack() {
            @Override
            public void onSuccess(Boolean result, JSONObject jOBJUpcomingMovie) {

                comingSoonMovieList = new ArrayList<>();

                try {

                    JSONObject getUpComeingJOBJ = jOBJUpcomingMovie;

                    String status = getUpComeingJOBJ.getString("status");

                    if (status.equals("true")) {

                        JSONObject getVersionJOBJ = getUpComeingJOBJ.getJSONObject("version");

                        String version = getVersionJOBJ.getString("andorid");

                        SharedPreferences appSharedPref = getContext().getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
                        sharedPrefVersion = appSharedPref.getString("version", "");

                        if (sharedPrefVersion.equals(version)) {

                            boolean tag = false;

                            comingSoonMovieList.clear();

                            JSONArray getUpComeingMovieArray = getUpComeingJOBJ.getJSONArray("movielist");

                            for (int i = 0; i < getUpComeingMovieArray.length(); i++) {

                                JSONObject getData = getUpComeingMovieArray.getJSONObject(i);

                                ArrayList<String> objectCastArray = new ArrayList();
                                ArrayList<String> objectVistaCode = new ArrayList<>();
                                ArrayList<String> objectGenre = new ArrayList<>();
                                ArrayList<String> objectDirectors = new ArrayList<>();
                                ArrayList<String> objectProducers = new ArrayList<>();
                                ArrayList<String> objectMusicians = new ArrayList<>();
                                ArrayList<String> objectWritters = new ArrayList<>();


                                if (tag == false) {

                                    /**---- cast array ----**/
                                    JSONArray getCast = getData.getJSONArray("cast");

                                    for (int a = 0; a < getCast.length(); a++) {

                                        JSONObject getCastData = getCast.getJSONObject(a);
                                        objectCastArray.add(getCastData.toString());
                                    }
                                    /**---- vistacode array ----**/
                                    JSONArray getVistaCode = getData.getJSONArray("vista_code");

                                    for(int a = 0; a < getVistaCode.length(); a++){

                                        String vistaCode = getVistaCode.getString(a);
                                        objectVistaCode.add(vistaCode);
                                    }
                                    /**---- genre array ----**/
                                    JSONArray getGenre = getData.getJSONArray("genre");

                                    for(int a = 0; a < getGenre.length(); a++){

                                        String getGenreData = getGenre.getString(a);
                                        objectGenre.add(getGenreData);
                                    }
                                    /**---- directors array ----**/
                                    JSONArray getDirectors = getData.getJSONArray("directors");

                                    for(int a = 0; a < getDirectors.length(); a++){

                                        String getDirectorData = getDirectors.getString(a);
                                        objectDirectors.add(getDirectorData);
                                    }
                                    /**---- producers array ----**/
                                    JSONArray getProducers = getData.getJSONArray("producers");

                                    for(int a = 0; a < getProducers.length(); a++){

                                        String getProducerseData = getProducers.getString(a);
                                        objectProducers.add(getProducerseData);
                                    }
                                    /**---- musicians array ----**/
                                    JSONArray getMusicians = getData.getJSONArray("musicians");

                                    for(int a = 0; a < getMusicians.length(); a++){

                                        String getMusiciansData = getMusicians.getString(a);
                                        objectMusicians.add(getMusiciansData);
                                    }
                                    /**---- writters array ----**/
                                    JSONArray getWritters = getData.getJSONArray("writters");

                                    for(int a = 0; a < getWritters.length(); a++){

                                        String getWrittersData = getWritters.getString(a);
                                        objectWritters.add(getWrittersData.toString());
                                    }

                                    tag = true;


                                }

                                if (tag == true) {

                                    ComingSoonMovieData movieData = new ComingSoonMovieData(

                                            getData.getString("mid"),
                                            getData.getString("m_name"),
                                            getData.getString("start_date"),
                                            getData.getString("end_date"),
                                            getData.getString("you_tube_link"),
                                            getData.getString("you_tube_id"),
                                            getData.getString("runtime"),
                                            getData.getString("language"),
                                            getData.getString("is3d_movie"),
                                            getData.getString("synopsis"),
                                            getData.getString("m_img"),
                                            getData.getString("m_large_img"),
                                            getData.getString("mobile_img"),
                                            "",
                                            objectCastArray,
                                            getData.getString("imdb"),
                                            getData.getString("adult"),
                                            objectVistaCode,
                                            objectGenre,
                                            getData.getString("fact"),
                                            getData.getString("featured"),
                                            objectDirectors,
                                            objectProducers,
                                            objectMusicians,
                                            objectWritters

                                    );

                                    tag = false;

                                    comingSoonMovieList.add(movieData);

                                }

                            }

                            if (comingSoonMovieList.size() != 0) {

                                setUpViewPager(viewPager);


                            } else {

                               //todo

                                setUpViewPager(viewPager);

                            }
                        }else{

                            startActivity(new Intent(getContext(), VersionPage.class));
                            ((Activity)getContext()).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }

                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Boolean result, VolleyError error) {

                progress.dismiss();

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    Functions.showSnackbar(getView());

                } else if (error instanceof AuthFailureError) {

                    if (error.networkResponse.statusCode == 401) {

                        CallApiMethods.getAccessToken(new ApiServerCallBack() {
                            @Override
                            public void onSuccess(Boolean result) {

                            }

                            @Override
                            public void onSuccess(Boolean result, String token) {

                                if (result) {
                                    if (!secondAttempt) {

                                        getComingSoonMovie(token, true);
                                    } else {

                                        Functions.showSnackbar(getView());
                                    }
                                } else {

                                    Functions.showSnackbar(getView());
                                }
                            }
                        }, getContext());

                    }


                } else if (error instanceof ServerError) {

                    Functions.showSnackbar(getView());

                } else if (error instanceof NetworkError) {

                    Functions.showSnackbar(getView());

                } else if (error instanceof ParseError) {

                    Functions.showSnackbar(getView());
                } else {
                    Functions.showSnackbar(getView());
                }
            }


        }, getContext(), accessToken, CallApiMethods.userKey);


    }



    @Override
    public void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(exampleBroadcastReceiver, filter);

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(exampleBroadcastReceiver);

    }

    private void showInternetDisconnectedView(boolean disconnected) {

        boolean isNetwork = Functions.isInternetAvailable(getContext());


        if (isNetwork) {

                Functions.dismisSnacbar();

                CallApiMethods.getAccessToken(new ApiServerCallBack() {
                    @Override
                    public void onSuccess(Boolean result) {
                        // do nothing
                    }

                    @Override
                    public void onSuccess(Boolean result, String token) {

                        if (result) {

                            getNowShowingMovie(token, false);

                        } else {

                            View view = getActivity().findViewById(android.R.id.content);
                            Functions.showSnackbar(view);
                        }
                    }
                }, getContext());


        } else {

            View view = getActivity().findViewById(android.R.id.content);
            Functions.showSnackbar(view);
        }

    }


    private class ExampleBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

                boolean noConnectivity = intent.getBooleanExtra(
                        ConnectivityManager.EXTRA_NO_CONNECTIVITY, true);
                showInternetDisconnectedView(noConnectivity);

            }
        }

    }





}
