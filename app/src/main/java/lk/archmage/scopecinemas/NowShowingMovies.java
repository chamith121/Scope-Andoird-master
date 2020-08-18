package lk.archmage.scopecinemas;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import lk.archmage.scopecinemas.Classes.NowShowingMovieAdapter;
import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.ServerCallBack;
import lk.archmage.scopecinemas.Models.NowShowingMovieData;


public class NowShowingMovies extends Fragment {


    TextView msgTv;
    private RecyclerView recyclerView;
    private NowShowingMovieAdapter adapter;

    ArrayList<NowShowingMovieData> nowShowingMovieArray;

    public NowShowingMovies() {
    }

    SwipeRefreshLayout pullToRefresh;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_now_showing_movies, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        msgTv = view.findViewById(R.id.msgTv);
        recyclerView = view.findViewById(R.id.recycler_viewNSM);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Bundle bundel = getArguments();

        nowShowingMovieArray = bundel.getParcelableArrayList("nowShowningMovieList");

        if(nowShowingMovieArray.size() != 0){

            msgTv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new NowShowingMovieAdapter(getContext(), nowShowingMovieArray);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);

        }else {

            recyclerView.setVisibility(View.GONE);
            msgTv.setVisibility(View.VISIBLE);
        }

        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Functions.isInternetAvailable(getContext())) {

                    System.out.println("network have");

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

                pullToRefresh.setRefreshing(false);
            }
        });


        return view;
    }





    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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

                    nowShowingMovieArray = new ArrayList<>();

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

                            nowShowingMovieArray.clear();

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

                                    nowShowingMovieArray.add(movieData);

                                }

                            }

                            if (nowShowingMovieArray.size() != 0) {

                                msgTv.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                adapter = new NowShowingMovieAdapter(getContext(), nowShowingMovieArray);
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);


                                progress.dismiss();

                            } else {

                                recyclerView.setVisibility(View.GONE);
                                msgTv.setVisibility(View.VISIBLE);
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
                    progress.dismiss();
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

}
