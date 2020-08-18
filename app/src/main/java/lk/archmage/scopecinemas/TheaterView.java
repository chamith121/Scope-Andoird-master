package lk.archmage.scopecinemas;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.ServerCallBack;
import lk.archmage.scopecinemas.Models.TheaterByMovieData;
import lk.archmage.scopecinemas.Models.TheaterData;

public class TheaterView extends AppCompatActivity {

    TheaterData theaterData;

    TextView titleTV, locationTv, phoneTv, emailTv, synopsisTv, theaterNameTv;
    LinearLayout actionCallLayout, moviesLayout, actionEmail;
    ImageView theaterImageIV;

    String sharedPrefVersion = "0";

    String sendMovieDate = "";

    ExampleBroadcastReceiver exampleBroadcastReceiver = new ExampleBroadcastReceiver();

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater_view);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolBarTheaterDetails);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        titleTV = findViewById(R.id.titleTV);
        locationTv = findViewById(R.id.locationTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        actionCallLayout = findViewById(R.id.actionCallLayout);
        actionEmail = findViewById(R.id.actionEmail);
        theaterImageIV = findViewById(R.id.theaterImageIV);
        synopsisTv = findViewById(R.id.synopsisTv);
        theaterNameTv = findViewById(R.id.theaterNameTv);

        /***  Check Version Prefarance***/
        SharedPreferences appSharedPref = this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);

        if (!appSharedPref.getString("version", "").isEmpty()) {

            sharedPrefVersion = appSharedPref.getString("version", "");

        }


        theaterData = getIntent().getParcelableExtra("theaterOBJ");

        if(theaterData != null){

            if(!theaterData.getTheaterImage().equals("") || !theaterData.getTheaterImage().equals(null)) {

                Glide.with(TheaterView.this).load(theaterData.getTheaterImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(theaterImageIV);
            }else {
                Glide.with(TheaterView.this).load(R.drawable.ttb_no_image).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(theaterImageIV);
            }
            titleTV.setText(theaterData.getTheaterName().toUpperCase());

            theaterNameTv.setText("ABOUT "+ theaterData.getTheaterName().toUpperCase());

            locationTv.setText(theaterData.getTheaterAddress());

            phoneTv.setText(theaterData.getTheaterContactNo());

            synopsisTv.setText(Html.fromHtml(theaterData.getTheaterDescrip()));

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String dateToday = sdf.format(date);

            sendMovieDate = dateToday;





        /*** Action Call ***/
            actionCallLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String contactNo = theaterData.getTheaterContactNo();

                    boolean result = checkAppPermission();

                    if (result) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel: " + contactNo));
                        if (ActivityCompat.checkSelfPermission(TheaterView.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        startActivity(callIntent);
                    }
                }
            });

            /*****-----  Action Send Email -----*****/
            actionEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String theaterEmail = "helpdesk@scopecinemas.com";
                    emailFunction(theaterEmail);
                }
            });

        }


    }

    /*  ******************* Email click function ****************/

    public void emailFunction(String email) {

        if (Functions.isInternetAvailable(TheaterView.this)) {

            final Intent emailIntent = new Intent(Intent.ACTION_SEND);

            String[] TO = {email};
          //  String[] CC = {"scheduling@simplyinterpreting.com"};
            /* Fill it with Data */
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Scope App");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, TO);
          //  emailIntent.putExtra(Intent.EXTRA_CC);

            /* Send it off to the Activity-Chooser */
            startActivity(Intent.createChooser(emailIntent, "Send E-Mail..."));

        } else {

            showAlertDialog(TheaterView.this, "Waring!", " No Internet Connection. Please Check your \n Wifi or Mobile Data.", "Ok", "");
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

//    public void getMoviesByTheater(String theaterId, final String sendDate, String accessToken, final boolean secondAttempt) {
//
//        final ProgressDialog progress = new ProgressDialog(this, R.style.MyTheme);
//        progress.setCancelable(false);
//        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
//        progress.show();
//
//        CallApiMethods.getNowshowingInTheater(new ServerCallBack() {
//            @Override
//            public void onSuccess(Boolean result, JSONObject jOBJ) {
//
//                Functions.dismisSnacbar();
//
//                movieData = new ArrayList<>();
//                movieData.clear();
//
//                try {
//                    JSONObject getOBJ = jOBJ;
//
//                    String status = getOBJ.getString("status");
//
//                    if (status.equals("true")) {
//
//                        String version = getOBJ.getString("version");
//
//                        if (sharedPrefVersion.equals(version)) {
//
//                            boolean tag = false;
//
//                            JSONArray getMoviesListArray = getOBJ.getJSONArray("movielist");
//
//                            if(getMoviesListArray.length() != 0) {
//
//                                for (int i = 0; i < getMoviesListArray.length(); i++) {
//
//                                    JSONObject getMovieOBJ = getMoviesListArray.getJSONObject(i);
//
//                                    ArrayList<String> objectShowTimeArray = new ArrayList();
//
//                                    if (tag == false) {
//
//                                        JSONArray getShowTimeArray = getMovieOBJ.getJSONArray("showtimes");
//
//                                        for (int a = 0; a < getShowTimeArray.length(); a++) {
//
//                                            JSONObject getShowTimeData = getShowTimeArray.getJSONObject(a);
//
//                                            objectShowTimeArray.add(getShowTimeData.toString());
//                                        }
//
//                                        tag = true;
//                                    }
//
//                                    if (tag = true) {
//
//                                        TheaterByMovieData theaterByMovieData = new TheaterByMovieData(
//                                                theaterData.getTheaterId(),
//                                                theaterData.getTheaterName(),
//                                                theaterData.getTheaterAddress(),
//                                                getMovieOBJ.getString("mid"),
//                                                getMovieOBJ.getString("m_name"),
//                                                getMovieOBJ.getString("synopsis"),
//                                                getMovieOBJ.getString("runtime"),
//                                                getMovieOBJ.getString("imdb"),
//                                                getMovieOBJ.getString("date"),
//                                                getMovieOBJ.getString("m_img"),
//                                                objectShowTimeArray,
//                                                getMovieOBJ.getString("adult")
//                                        );
//
//                                        tag = false;
//                                        movieData.add(theaterByMovieData);
//                                    }
//                                }
//
//                                loadHorizontalScrollerData();
//
//                                progress.dismiss();
//
//
//
//
//
//                            }else {
//
//                            }
//
//                        } else {
//
//                            progress.dismiss();
//
//                            startActivity(new Intent(TheaterView.this, VersionPage.class));
//                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//                        }
//
//                    } else {
//
//                        progress.dismiss();
//
//                        //todo
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Boolean result, VolleyError error) {
//
//                progress.dismiss();
//
//                final View view = findViewById(android.R.id.content);
//
//                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//
//                    Functions.showSnackbar(view);
//
//                } else if (error instanceof AuthFailureError) {
//
//                    if (error.networkResponse.statusCode == 401) {
//
//                        CallApiMethods.getAccessToken(new ApiServerCallBack() {
//                            @Override
//                            public void onSuccess(Boolean result) {
//
//                            }
//
//                            @Override
//                            public void onSuccess(Boolean result, String token) {
//                                if (result) {
//                                    if (!secondAttempt) {
//
//                                               getMoviesByTheater(theaterData.getTheaterId(), sendDate, token, true);
//
//                                    } else {
//                                        Functions.showSnackbar(view);
//                                    }
//                                } else {
//                                    Functions.showSnackbar(view);
//
//                                }
//                            }
//                        }, TheaterView.this);
//
//
//                    }
//
//                } else if (error instanceof ServerError) {
//
//                    Functions.showSnackbar(view);
//
//                } else if (error instanceof NetworkError) {
//
//                    Functions.showSnackbar(view);
//
//                } else if (error instanceof ParseError) {
//
//                    Functions.showSnackbar(view);
//                }
//
//            }
//        }, TheaterView.this, accessToken, CallApiMethods.userKey, theaterId, sendDate);
//    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(exampleBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.unregisterReceiver(exampleBroadcastReceiver);
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

    private void showInternetDisconnectedView(boolean disconnected) {

        boolean isNetwork = Functions.isInternetAvailable(TheaterView.this);

        if (isNetwork) {

            Functions.dismisSnacbar();

            try {

                CallApiMethods.getAccessToken(new ApiServerCallBack() {
                    @Override
                    public void onSuccess(Boolean result) {

                    }

                    @Override
                    public void onSuccess(Boolean result, String token) {

                        if (result) {



                        } else {
                            View view = findViewById(android.R.id.content);
                            Functions.errorShowSnacBar(view);
                        }
                    }

                }, TheaterView.this);


            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            View view = findViewById(android.R.id.content);
            Functions.showSnackbar(view);
        }

    }

    private boolean checkAppPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {


                ActivityCompat.requestPermissions(TheaterView.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else {

            return true;
        }
    }

//
//    public void loadHorizontalScrollerData(){
//
//        /******   Looping movies horizontal scroller ******/
//
//        moviesLayout = findViewById(R.id.moviesLayout);
//        moviesLayout.removeAllViews();
//        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
//
//        for(int s = 0; s < movieData.size(); s++){
//
//            final TheaterByMovieData theaterByMovieData  = movieData.get(s);
//
//            View view1 = inflater.inflate(R.layout.item_movie, moviesLayout, false);
//            moviesLayout.removeView(view1);
//
//            ImageView imageMovieIV = view1.findViewById(R.id.imageMovieIV);
//            Glide.with(TheaterView.this).load(theaterByMovieData.getMovieImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(imageMovieIV);
//
//            TextView nameMovie = view1.findViewById(R.id.nameMovie);
//            nameMovie.setText(theaterByMovieData.getMovieName());
//
//            Button btnTickets = view1.findViewById(R.id.btnTickets);
//
//            btnTickets.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent intent = new Intent(TheaterView.this, SelectADate.class);
//
//                    intent.putExtra("theaterByMovieDataOBJ", theaterByMovieData);
//                    startActivity(intent);
//
//                }
//            });
//
//
//            moviesLayout.addView(view1);
//
//        }
//    }

    public void showAlertDialog(final Context context, String title, String message, String postBtnMsg, String negBtnMsg) {

        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        // alertDialog.setIcon(R.drawable.delete);
        alertDialog.setPositiveButton(postBtnMsg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        alertDialog.show();
    }


}
