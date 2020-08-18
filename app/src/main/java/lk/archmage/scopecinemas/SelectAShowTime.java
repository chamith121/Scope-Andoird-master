package lk.archmage.scopecinemas;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import lk.archmage.scopecinemas.Classes.VenueByTheatersAdapter;
import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.ServerCallBack;
import lk.archmage.scopecinemas.Models.ComingSoonMovieData;
import lk.archmage.scopecinemas.Models.MovieShowTimesTheatersData;
import lk.archmage.scopecinemas.Models.NowShowingMovieData;
import lk.archmage.scopecinemas.Models.TheaterByMovieData;


public class SelectAShowTime extends AppCompatActivity {

    RecyclerView recyclerView;
    VenueByTheatersAdapter adapter;

    TextView movieShowDate, moveGenreTv, movieNameTv, msgTv;
    NowShowingMovieData nowShowingMovieData;
    ImageView movieImageIV;

//
//    TheaterByMovieData theaterByMovieData;
    ArrayList<MovieShowTimesTheatersData> movieShowTimeArray;

    String sharedPrefVersion = "0";

    ExampleBroadcastReceiver exampleBroadcastReceiver = new ExampleBroadcastReceiver();

    String sendMovieDate = "";
    String movieId = "";

    static SelectAShowTime selectAShowTime;
    ComingSoonMovieData comingSoonMovieData;

    public static SelectAShowTime getInstance(){
        return selectAShowTime;
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ashow_time);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        selectAShowTime = this;

        Toolbar toolbar = findViewById(R.id.toolBarSelectAShowTime);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        /***  Check Version Prefarance***/
        SharedPreferences appSharedPref = this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);

        if (!appSharedPref.getString("version", "").isEmpty()) {

            sharedPrefVersion = appSharedPref.getString("version", "");

        }

        msgTv = findViewById(R.id.msgTv);

        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                recyclerView.dispatchNestedFling(velocityX, velocityY, false);
                return false;
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(SelectAShowTime.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());



        movieShowDate = findViewById(R.id.movieShowDate);
        movieNameTv = findViewById(R.id.movieNameTv);
        movieImageIV = findViewById(R.id.movieImageIV);
        moveGenreTv = findViewById(R.id.moveGenreTv);

        Bundle bundle = getIntent().getExtras();

        nowShowingMovieData = getIntent().getParcelableExtra("nowShowingMovieObj");
        comingSoonMovieData = getIntent().getParcelableExtra("comingSoonMovieOBJ");

        if (bundle != null) {

            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MMM-dd");

                String getDate = bundle.getString("selectDate");

                /** SEND API MOVEI SELECTED DATE ***/
                sendMovieDate = getDate;

                Date d = sdf.parse(getDate);

                final String date = sdf2.format(d);

                //yyyy-MMM-dd
                String[] splitDate = date.split("-");

                String getDaySuffix = getDayOfMonthSuffix(Integer.parseInt(splitDate[2]));

                movieShowDate.setText(splitDate[2] + getDaySuffix + " " + splitDate[1] + " " + splitDate[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(nowShowingMovieData != null){

            movieId = nowShowingMovieData.getMovieId();

            if(!nowShowingMovieData.getMovieImage().equals("") || !nowShowingMovieData.getMovieImage().equals(null)) {
                Glide.with(SelectAShowTime.this).load(nowShowingMovieData.getMovieImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);
            }else {
                Glide.with(SelectAShowTime.this).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);

            }

            movieNameTv.setText(nowShowingMovieData.getMovieName());

            String replaceString2 = "";
            if(!nowShowingMovieData.getRunTime().equals("") || !nowShowingMovieData.getRunTime().isEmpty()) {

                String movieTime = nowShowingMovieData.getRunTime();
                String replaceString1 = movieTime.replace("Hour","h");
                replaceString2 = "~" +replaceString1.replace("Minutes","m");
               // movieTimeTv.setText("~ "+ replaceString2);

            }

            ArrayList<String> genreArray = nowShowingMovieData.getGenre();
            String genre = "";

            for(int a = 0; a < genreArray.size(); a++){

                genre += genreArray.get(a)+ " | ";
            }

            if (genre != null && genre.length() > 0 && genre.charAt(genre.length() - 2) == '|') {
                genre = genre.substring(0, genre.length() - 2);
            }
            moveGenreTv.setText(genre +" "+ replaceString2);

        }else if(comingSoonMovieData != null){


            movieId = comingSoonMovieData.getMovieId();

            if(!comingSoonMovieData.getMovieImage().equals("") || !comingSoonMovieData.getMovieImage().equals(null)) {

                Glide.with(SelectAShowTime.this).load(comingSoonMovieData.getMovieImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);
            }else {
                Glide.with(SelectAShowTime.this).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);

            }

            movieNameTv.setText(comingSoonMovieData.getMovieName());

            String replaceString2 = "";
            if(!comingSoonMovieData.getRunTime().equals("") || !comingSoonMovieData.getRunTime().isEmpty()) {

                String movieTime = comingSoonMovieData.getRunTime();
                String replaceString1 = movieTime.replace("Hour","h");
                replaceString2 = "~" +replaceString1.replace("Minutes","m");
                // movieTimeTv.setText("~ "+ replaceString2);

            }

            ArrayList<String> genreArray = comingSoonMovieData.getGenre();
            String genre = "";

            for(int a = 0; a < genreArray.size(); a++){

                genre += genreArray.get(a)+ " | ";
            }

            if (genre != null && genre.length() > 0 && genre.charAt(genre.length() - 2) == '|') {
                genre = genre.substring(0, genre.length() - 2);
            }
            moveGenreTv.setText(genre +" "+ replaceString2);

        }


    }


    String getDayOfMonthSuffix(final int n) {
        // checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();
        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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

                outRect.left = spacing ; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = spacing; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom

            } else {

                outRect.left = spacing; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing ; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
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


    public void getTheaterShowTimeByMovie(final String sendDate, final String movieId, String accessToken, final boolean secondAttempt) {

        CallApiMethods.getMovieShowTime(new ServerCallBack() {
            @Override
            public void onSuccess(Boolean result, JSONObject jOBJ) {

                Functions.dismisSnacbar();

                movieShowTimeArray = new ArrayList<>();
                movieShowTimeArray.clear();

                try {
                    JSONObject getOBJ = jOBJ;

                    if (getOBJ.getBoolean("status")) {

                        JSONObject getVersionJOBJ = getOBJ.getJSONObject("version");

                        String version = getVersionJOBJ.getString("andorid");

                        if (sharedPrefVersion.equals(version)) {

                            boolean tag = false;

                            JSONObject getMovieListObj = getOBJ.getJSONObject("movieshowtimes");

                            if (getMovieListObj.has("theaters")) {

                                recyclerView.setVisibility(View.VISIBLE);
                                msgTv.setVisibility(View.GONE);

                                JSONArray getTheatersMovieArray = getMovieListObj.getJSONArray("theaters");

                                for (int i = 0; i < getTheatersMovieArray.length(); i++) {

                                    JSONObject getMovieSData = getTheatersMovieArray.getJSONObject(i);

                                        ArrayList<String> objectExperincesArray = new ArrayList();

                                        if (tag == false) {

                                            JSONArray getExperincesArray = getMovieSData.getJSONArray("experinces");

                                            for (int a = 0; a < getExperincesArray.length(); a++) {

                                                JSONObject getShowTimeData = getExperincesArray.getJSONObject(a);

                                                objectExperincesArray.add(getShowTimeData.toString());
                                            }

                                            tag = true;
                                        }

                                        if (tag = true) {

                                            MovieShowTimesTheatersData movieShowTimeData = new MovieShowTimesTheatersData(

                                                    getMovieSData.getString("t_name"),
                                                    getMovieSData.getString("t_city"),
                                                    getMovieSData.getString("mid"),
                                                    getMovieSData.getString("tid"),
                                                    getMovieSData.getString("m_name"),
                                                    getMovieSData.getString("date"),
                                                    getMovieSData.getString("booking_end_date"),
                                                    getMovieSData.getString("t_img"),
                                                    getMovieSData.getString("m_img"),
                                                    objectExperincesArray,
                                                    false,
                                                    getMovieSData.getString("t_vista_code")

                                            );

                                            tag = false;

                                            movieShowTimeArray.add(movieShowTimeData);

                                        }

                                }

                                if(movieShowTimeArray.size() != 0) {

                                    if (nowShowingMovieData != null) {

                                        adapter = new VenueByTheatersAdapter(SelectAShowTime.this, movieShowTimeArray,
                                                nowShowingMovieData.getMovieImage(), nowShowingMovieData.getRunTime(),
                                                nowShowingMovieData.getCheckAdults(), nowShowingMovieData.getGenre());

                                    } else if (comingSoonMovieData != null) {

                                        ArrayList<String> genre = new ArrayList<>();
                                        adapter = new VenueByTheatersAdapter(SelectAShowTime.this, movieShowTimeArray,
                                                comingSoonMovieData.getMovieImage(), comingSoonMovieData.getRunTime(), comingSoonMovieData.getCheckAdults(), genre);

                                    }
                                    adapter.notifyDataSetChanged();
                                    recyclerView.setAdapter(adapter);

                                }else{

                                    recyclerView.setVisibility(View.INVISIBLE);
                                    String message = getMovieListObj.getString("message");
                                    msgTv.setVisibility(View.VISIBLE);
                                    msgTv.setText(message);
                                }


                            } else {

                                recyclerView.setVisibility(View.INVISIBLE);
                                String message = getMovieListObj.getString("message");
                                msgTv.setVisibility(View.VISIBLE);
                                msgTv.setText(message);

                            }



                        } else {

                            startActivity(new Intent(SelectAShowTime.this, VersionPage.class));
                            ((Activity) SelectAShowTime.this).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }


                    } else {

                        Log.i("TAG_STATUS", "---------- API STATUS FALSE -----------");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Boolean result, VolleyError error) {

                final View getView = findViewById(android.R.id.content);

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    Functions.showSnackbar(getView);

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

                                        getTheaterShowTimeByMovie(sendDate, movieId, token, true);

                                    } else {
                                        Functions.showSnackbar(getView);
                                    }
                                } else {
                                    Functions.showSnackbar(getView);

                                }
                            }
                        }, SelectAShowTime.this);


                    }

                } else if (error instanceof ServerError) {

                    Functions.showSnackbar(getView);

                } else if (error instanceof NetworkError) {

                    Functions.showSnackbar(getView);

                } else if (error instanceof ParseError) {

                    Functions.showSnackbar(getView);
                }
            }

        }, SelectAShowTime.this, accessToken, CallApiMethods.userKey, sendDate, movieId);

    }

    @Override
    public void onStart() {
        super.onStart();

        msgTv.setVisibility(View.VISIBLE);
        msgTv.setText("Loading...");

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(exampleBroadcastReceiver, filter);

    }

    @Override
    public void onStop() {
        super.onStop();
        this.unregisterReceiver(exampleBroadcastReceiver);

    }

    private void showInternetDisconnectedView(boolean disconnected) {

        boolean isNetwork = Functions.isInternetAvailable(SelectAShowTime.this);


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

                        getTheaterShowTimeByMovie(sendMovieDate , movieId, token, false);

                    } else {

                        View view = findViewById(android.R.id.content);
                        Functions.showSnackbar(view);
                    }
                }
            }, SelectAShowTime.this);

        } else {

            msgTv.setVisibility(View.VISIBLE);
            msgTv.setText("Loading...");

            View view = findViewById(android.R.id.content);
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
