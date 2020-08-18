package lk.archmage.scopecinemas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.concurrent.TimeUnit;

import lk.archmage.scopecinemas.Classes.ConcessionsAdapter;
import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.MethodCallBack;
import lk.archmage.scopecinemas.Common.ServerCallBack;
import lk.archmage.scopecinemas.Interfaces.ConcessionInterface;
import lk.archmage.scopecinemas.Models.ConcessionData;
import lk.archmage.scopecinemas.Models.ReservationsData;

public class PromosAndConfessionaies extends AppCompatActivity implements ConcessionInterface {

    ReservationsData reservationsData;
    ImageView movieImageIV;
    TextView movieNameTv, movieShowDate, movieVenue, movieClass, seatsCountTv, msgTv,
            moveGenreTv;

    Button continueBtn;

    TabLayout tabLayout;

    RecyclerView recyclerView;
    ConcessionsAdapter adapter;


    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private TextView textViewTime;
    private CountDownTimer countDownTimer;

    private long timeCountInMilliSeconds = 0;

    static PromosAndConfessionaies promosAndConfessionaies;

    ExampleBroadcastReceiver exampleBroadcastReceiver = new ExampleBroadcastReceiver();

    public static PromosAndConfessionaies getInstance() {
        return promosAndConfessionaies;
    }

    ArrayList<ConcessionData> itemArrayList = new ArrayList<ConcessionData>();
    ArrayList<String> categoryItemArray = new ArrayList<>();

    ArrayList<ConcessionData> selectedConcessions;

    long timeleft = 0;

    String sharedPrefVersion = "0";

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promos_and_confessionaies);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        promosAndConfessionaies = this;

        Toolbar toolbar = findViewById(R.id.toolBarPromosAndConfe);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        movieImageIV = findViewById(R.id.movieImageIV);
        movieNameTv = findViewById(R.id.movieNameTv);
        movieShowDate = findViewById(R.id.movieShowDate);
        movieVenue = findViewById(R.id.movieVenue);
        movieClass = findViewById(R.id.movieClass);
        seatsCountTv = findViewById(R.id.seatsCountTv);
        moveGenreTv = findViewById(R.id.moveGenreTv);
        textViewTime = findViewById(R.id.textViewTime);

        /***  Check Version Prefarance***/
        SharedPreferences appSharedPref = this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);

        if (!appSharedPref.getString("version", "").isEmpty()) {

            sharedPrefVersion = appSharedPref.getString("version", "");

        }

        selectedConcessions = new ArrayList<>();
        selectedConcessions.clear();

        closeReservationDialog = new AlertDialog.Builder(PromosAndConfessionaies.this);
        sessionExpiredDialog = new AlertDialog.Builder(PromosAndConfessionaies.this);

        reservationsData = (ReservationsData) getIntent().getSerializableExtra("reservationOBJ");

        Bundle bundle = getIntent().getExtras();
        timeleft = bundle.getLong("timerTime");

        setTimerValues();

        if (reservationsData != null) {

            if (reservationsData.getMovieImage() != null) {

                Glide.with(PromosAndConfessionaies.this).load(reservationsData.getMovieImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);

            }else {
                Glide.with(PromosAndConfessionaies.this).load(R.drawable.ttb_no_image).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);

            }

            movieNameTv.setText(reservationsData.getFilmName());

            String runTime = reservationsData.getTime();
            String replaceString2 = "";
            if(!runTime.equals("") || !runTime.isEmpty()) {

                String replaceString1 = runTime.replace("Hour","h");
                replaceString2 = "~" + replaceString1.replace("Minutes","m");
                //movieTimeTv.setText("~ "+ replaceString2);

            }

            ArrayList<String> genreArray = reservationsData.getGenre();
            String genre = "";

            for(int a = 0; a < genreArray.size(); a++){

                genre += genreArray.get(a)+ " | ";
            }

            if (genre != null && genre.length() > 0 && genre.charAt(genre.length() - 2) == '|') {
                genre = genre.substring(0, genre.length() - 2);
            }
            moveGenreTv.setText(genre +" "+ replaceString2);

            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MMM-dd");

                String getDate = reservationsData.getDate();

                Date d = sdf.parse(getDate);

                final String date = sdf2.format(d);

                //yyyy-MMM-dd
                String[] splitDate = date.split("-");

                String getDaySuffix = getDayOfMonthSuffix(Integer.parseInt(splitDate[2]));

                movieShowDate.setText(splitDate[2] + getDaySuffix + " " + splitDate[1] + " " + splitDate[0] + ", " + reservationsData.getShowTime());

            } catch (Exception e) {
                e.printStackTrace();
            }

            movieVenue.setText(reservationsData.getLocationName());
            movieClass.setText(reservationsData.getExperience());
            seatsCountTv.setText(reservationsData.getTotalTickets() + " SEATS");

        }

        continueBtn = findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reservationsData.setSelectedFoodItems(selectedConcessions);

                Intent intent = new Intent(PromosAndConfessionaies.this, ReservationSummary.class);
                intent.putExtra("reservationsOBJ", reservationsData);
                intent.putExtra("timerTime", timeleft);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        msgTv = findViewById(R.id.msgTv);
        recyclerView = findViewById(R.id.recyclerViewConcession);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(PromosAndConfessionaies.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                try {

                    if (itemArrayList.size() != 0) {
                        ArrayList<ConcessionData> items = new ArrayList<>();
                        items.clear();


                        for (int i = 0; i < itemArrayList.size(); i++) {

                            // JSONObject itemOBJ = new JSONObject(itemArrayList.get(i));

                            ConcessionData itemOBJ = itemArrayList.get(i);

                            if (itemOBJ.getCategory().equals(tab.getText())) {

                                ConcessionData concessionData = new ConcessionData(
                                        itemOBJ.getName(),
                                        itemOBJ.getItemId(),
                                        itemOBJ.getPrice(),
                                        itemOBJ.getCategoryId(),
                                        itemOBJ.getCategory(),
                                        0,
                                        itemOBJ.getImage()
                                );

                                for (int j = 0; j < selectedConcessions.size(); j++) {

                                    if(selectedConcessions.get(j).getItemId() == concessionData.getItemId()){

                                        concessionData.setCount(selectedConcessions.get(j).getCount());
                                        concessionData.setStatus(1);
                                    }

                                }

                                items.add(concessionData);

                            }
                        }

                        adapter = new ConcessionsAdapter(getApplicationContext(), items, PromosAndConfessionaies.this);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void setConcessionObject(ConcessionData object) {

        selectedConcessions.add(object);

    }

    @Override
    public void removeConcessionObject(ConcessionData object) {

        for (ConcessionData con: selectedConcessions) {

            if(con.getItemId() == object.getItemId()){

                selectedConcessions.remove(object);
            }

        }
    }


    String getDayOfMonthSuffix(final int n) {
        // checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "ST";
            case 2:
                return "ND";
            case 3:
                return "RD";
            default:
                return "TH";
        }
    }


    /**
     * reset timer
     */
    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }

    /**
     * start and stop timer
     */

    /**
     * set time
     */
    private void setTimerValues() {

        timeCountInMilliSeconds = timeleft;

        startCountDownTimer();
    }

    /**
     * method timer
     */
    private void startCountDownTimer() {

        //  setProgressBarValues();

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                timeleft =  millisUntilFinished;
                // progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                textViewTime.setText("00:00");
                timerStatus = TimerStatus.STOPPED;

                showSessionExpiredDialog();


            }

        }.start();
        countDownTimer.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
//    private void setProgressBarValues() {
//
//        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
//        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
//    }

    /**
     * method to convert millisecond to time format
     * HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        //  String hms = String.format("%02d:%02d:%02d",   TimeUnit.MILLISECONDS.toHours(milliSeconds),
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;

    }


    AlertDialog.Builder closeReservationDialog;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        showCloseDialod();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        closeReservationDialog = new AlertDialog.Builder(PromosAndConfessionaies.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cancel_reservation, null);

        closeReservationDialog.setView(dialogView);
        AlertDialog alertDialog = closeReservationDialog.create();

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCancelable(false);

        Button btnYes = dialogView.findViewById(R.id.yesBtn);
        Button btnNo = dialogView.findViewById(R.id.noBtn);

        alertDialog.show();
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textViewTime.setText("00:00");
                timerStatus = TimerStatus.STOPPED;

                /***---- Call Cancel Payment Method ---***/
                getAccessTokenTocancelPayment();

                SeatPlan.getInstance().finish();

                alertDialog.dismiss();
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (alertDialog != null) {
                    alertDialog.dismiss();
                }

            }
        });

    }


    public void showCloseDialod() {

        try {
            closeReservationDialog = new AlertDialog.Builder(PromosAndConfessionaies.this);

            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_cancel_reservation, null);

            closeReservationDialog.setView(dialogView);
            AlertDialog alertDialog = closeReservationDialog.create();

            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setCancelable(false);

            Button btnYes = dialogView.findViewById(R.id.yesBtn);
            Button btnNo = dialogView.findViewById(R.id.noBtn);

            alertDialog.show();
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /***---- Call Cancel Payment Method ---***/
                    getAccessTokenTocancelPayment();

                    textViewTime.setText("00:00");
                    timerStatus = TimerStatus.STOPPED;

                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }

                    SeatPlan.getInstance().finish();
                    finish();

                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }
            });


            closeReservationDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog.dismiss();
                }
            });

            closeReservationDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });

        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    AlertDialog.Builder sessionExpiredDialog;
    public void showSessionExpiredDialog() {

        try {
            //sessionExpiredDialog = new AlertDialog.Builder(PromosAndConfessionaies.this);

            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_session_expired, null);

            sessionExpiredDialog.setView(dialogView);
            AlertDialog alertDialog = sessionExpiredDialog.create();

            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setCancelable(false);

            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setCancelable(false);
            Button btnGoHome = dialogView.findViewById(R.id.goHomeBtn);

            alertDialog.show();
            btnGoHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /***---- Call Cancel Payment Method ---***/
                    getAccessTokenTocancelPayment();

                    SelectADate.getInstance().finish();
                    SelectAShowTime.getInstance().finish();
                    SeatPlan.getInstance().finish();


                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }

                    finish();
                }
            });

            sessionExpiredDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog.dismiss();
                }
            });

            sessionExpiredDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });

        } catch (Exception e) {
            //e.printStackTrace();
        }

    }


    public void createTabs() {

        if (categoryItemArray.size() != 0) {

            for (int i = 0; i < categoryItemArray.size(); i++) {

                tabLayout.addTab(tabLayout.newTab().setText(categoryItemArray.get(i)));
            }

            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        } else {
            //
        }
    }


    public void getConcerssionMenthod(MethodCallBack methodCallBack, String accessToken, final String cinemaId, boolean secondAttempt) {

        CallApiMethods.getConcerssion(new ServerCallBack() {
            @Override
            public void onSuccess(Boolean result, JSONObject jOBJ) {

                Functions.dismisSnacbar();

                try {
                    JSONObject getOBJ = jOBJ;

                    itemArrayList.clear();
                    categoryItemArray.clear();

                    if (getOBJ.getBoolean("status")) {

                        JSONObject getVersionJOBJ = getOBJ.getJSONObject("version");

                        String version = getVersionJOBJ.getString("andorid");

                        if (sharedPrefVersion.equals(version)) {

                        JSONArray getItemsJOBA = getOBJ.getJSONArray("items");

                        for (int i = 0; i < getItemsJOBA.length(); i++) {

                            JSONObject itemOBJ = getItemsJOBA.getJSONObject(i);

                            ConcessionData concessionObj = new ConcessionData(
                                    itemOBJ.getString("name"),
                                    itemOBJ.getString("item_id"),
                                    itemOBJ.getString("price"),
                                    itemOBJ.getString("category_id"),
                                    itemOBJ.getString("category"),
                                    0,
                                    itemOBJ.getString("image")

                            );

                            if (categoryItemArray.size() == 0) {
                                categoryItemArray.add(itemOBJ.getString("category"));

                            } else {

                                if (!categoryItemArray.contains(itemOBJ.getString("category"))) {

                                    categoryItemArray.add(itemOBJ.getString("category"));

                                }
                            }


                            itemArrayList.add(concessionObj);

                        }

                        methodCallBack.onSuccess(true);
                        } else {

                            startActivity(new Intent(PromosAndConfessionaies.this, VersionPage.class));
                            ((Activity) PromosAndConfessionaies.this).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }

                    } else {
                        //
                    }

                } catch (JSONException e) {

                    methodCallBack.onSuccess(false);
                }

            }

            @Override
            public void onFailure(Boolean result, VolleyError error) {
                methodCallBack.onSuccess(false);

                final View view = findViewById(android.R.id.content);

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    Functions.showSnackbar(view);

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

                                        getConcerssionMenthod(new MethodCallBack() {
                                            @Override
                                            public void onSuccess(Boolean result) {

                                                if (result) {


                                                } else {

                                                }
                                            }
                                        }, token, cinemaId, true);

                                    } else {
                                        Functions.showSnackbar(view);
                                    }
                                } else {
                                    Functions.showSnackbar(view);

                                }
                            }
                        }, PromosAndConfessionaies.this);


                    }

                } else if (error instanceof ServerError) {

                    Functions.showSnackbar(view);

                } else if (error instanceof NetworkError) {

                    Functions.showSnackbar(view);

                } else if (error instanceof ParseError) {

                    Functions.showSnackbar(view);
                }
            }
        }, PromosAndConfessionaies.this, accessToken, CallApiMethods.userKey, cinemaId);

    }

    public void getAccessTokenTocancelPayment(){

        CallApiMethods.getAccessToken(new ApiServerCallBack() {
            @Override
            public void onSuccess(Boolean result) {
                // do nothing
            }

            @Override
            public void onSuccess(Boolean result, String token) {

                if (result) {

                    paymentCancelMethod(token, false);

                }

            }
        }, PromosAndConfessionaies.this);

    }

    public void paymentCancelMethod(String accessToken, boolean secondAttempt){

        CallApiMethods.paymentCancel(new ServerCallBack() {
            @Override
            public void onSuccess(Boolean result, JSONObject jOBJ) {

                //
                Log.i("TAG_CANCEL", "Payment Cancel success.");

            }

            @Override
            public void onFailure(Boolean result, VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    Functions.showSnackbar(getCurrentFocus());

                } else if (error instanceof AuthFailureError) {

                    if (error.networkResponse.statusCode == 401) {

                        CallApiMethods.getAccessToken(new ApiServerCallBack() {
                            @Override
                            public void onSuccess(Boolean result) {
                                //do nothing
                            }

                            @Override
                            public void onSuccess(Boolean result, String token) {
                                if (result) {
                                    if (!secondAttempt) {

                                        paymentCancelMethod(token, true);
                                    } else {
                                        Functions.showSnackbar(getCurrentFocus());
                                    }
                                } else {
                                    Functions.showSnackbar(getCurrentFocus());
                                }
                            }
                        }, PromosAndConfessionaies.this);


                    }

                } else if (error instanceof ServerError) {

                    Functions.showSnackbar(getCurrentFocus());

                } else if (error instanceof NetworkError) {

                    Functions.showSnackbar(getCurrentFocus());

                } else if (error instanceof ParseError) {

                    Functions.showSnackbar(getCurrentFocus());
                }

                finish();

            }
        }, PromosAndConfessionaies.this, accessToken, CallApiMethods.userKey, reservationsData.getReservationId());
    }




    int status = 0;

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        PromosAndConfessionaies.this.registerReceiver(exampleBroadcastReceiver, filter);

    }

    @Override
    public void onStop() {
        super.onStop();

        PromosAndConfessionaies.this.unregisterReceiver(exampleBroadcastReceiver);

    }

    ProgressDialog progress;

    private void showInternetDisconnectedView(boolean disconnected) {

        progress = new ProgressDialog(PromosAndConfessionaies.this, R.style.MyTheme);
        progress.setCancelable(false);
        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        progress.show();

        boolean isNetwork = Functions.isInternetAvailable(PromosAndConfessionaies.this);

        if (isNetwork) {

            if (status == 0) {

                Functions.dismisSnacbar();

                CallApiMethods.getAccessToken(new ApiServerCallBack() {
                    @Override
                    public void onSuccess(Boolean result) {
                        //todo
                    }

                    @Override
                    public void onSuccess(Boolean result, String token) {

                        if (result) {

                            getConcerssionMenthod(new MethodCallBack() {
                                @Override
                                public void onSuccess(Boolean result) {

                                    if (result) {

                                        if (itemArrayList.size() != 0) {

                                            createTabs();

                                            if (progress != null) {

                                                progress.dismiss();
                                            }

                                        }

                                    } else {

                                        finish();
                                    }
                                }
                            }, token, reservationsData.getCinemaId(), false);

                        } else {

                            View view = findViewById(android.R.id.content);
                            Functions.showSnackbar(view);


                        }

                    }
                }, PromosAndConfessionaies.this);

                status = 1;

            } else {

                if (progress != null) {

                    progress.dismiss();
                }

                /***---- Call Cancel Payment Method ---***/
                getAccessTokenTocancelPayment();

                SeatPlan.getInstance().finish();
                this.finish();

            }

        } else {

            status = 0;


            View view = findViewById(android.R.id.content);
            Functions.showSnackbar(view);

            if (progress != null) {

                progress.dismiss();
            }

            finish();
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


}
