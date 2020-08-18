package lk.archmage.scopecinemas;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.concurrent.TimeUnit;

import lk.archmage.scopecinemas.Classes.FoodAdapter;
import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.ServerCallBack;
import lk.archmage.scopecinemas.Interfaces.FoodItemInterface;
import lk.archmage.scopecinemas.Models.ConcessionData;
import lk.archmage.scopecinemas.Models.ReservationsData;
import lk.archmage.scopecinemas.Models.UserData;
import lk.archmage.scopecinemas.ProcessPayment.CollectCardInfoActivity;

public class ReservationSummary extends AppCompatActivity implements FoodItemInterface {

    ImageView movieImageIV;
    TextView movieNameTv, theaterNameTv, theaterCityTv, movieDateTimeTv,
            seatsCountTv, seatsNameTv, totalAmountTv, msgTv, timerTv;

    FoodAdapter adapter;
    RecyclerView recyclerView;
    Button payBtn;

    ReservationsData reservationsData;

    LinearLayout foodItemLayout;

    String sharedPrefVersion = "0";

    ExampleBroadcastReceiver exampleBroadcastReceiver = new ExampleBroadcastReceiver();

    CheckBox chkAndroid;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private CountDownTimer countDownTimer;

    private long timeCountInMilliSeconds = 0;

    long timeleft = 0;

    static ReservationSummary reservationSummary;

    JSONObject userData;

    public static ReservationSummary getInstance() {
        return reservationSummary;
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_summary);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolBarReservationSummery);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        reservationSummary = this;

        /***  Check Version Prefarance***/
        SharedPreferences appSharedPrefa = this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);

        if(!appSharedPrefa.getString("version", "").isEmpty()){

            sharedPrefVersion = appSharedPrefa.getString("version", "");

        }

        /************    Hide Soft Keyboard Activity startup **********/

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sessionExpiredDialog = new AlertDialog.Builder(ReservationSummary.this);


        /***--- Check User Sheare prefarances ---***/
        SharedPreferences appSharedPref = this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);

        if(appSharedPref.getString("user", "").isEmpty()){

        }else{

            try {

                userData = new JSONObject(appSharedPref.getString("user", ""));

            }catch (JSONException e){
                e.printStackTrace();
            }
        }


        movieImageIV = findViewById(R.id.movieImageIV);
        movieNameTv = findViewById(R.id.movieNameTv);
        theaterNameTv = findViewById(R.id.theaterNameTv);
        theaterCityTv = findViewById(R.id.theaterCityTv);
        movieDateTimeTv = findViewById(R.id.movieDateTimeTv);
        seatsCountTv = findViewById(R.id.seatsCountTv);
        seatsNameTv = findViewById(R.id.seatsNameTv);
        totalAmountTv = findViewById(R.id.totalAmountTv);
        payBtn = findViewById(R.id.payBtn);
        foodItemLayout = findViewById(R.id.foodItemLayout);
        timerTv = findViewById(R.id.timerTv);
        chkAndroid = findViewById(R.id.chkAndroid);

        if(chkAndroid.isChecked()){

            payBtn.setEnabled(true);
        }else {
            payBtn.setEnabled(false);
        }

        reservationsData = (ReservationsData) getIntent().getSerializableExtra("reservationsOBJ");


        recyclerView = findViewById(R.id.concessionListRV);
        msgTv = findViewById(R.id.msgTv);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ReservationSummary.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Bundle bundle = getIntent().getExtras();
        timeleft = bundle.getLong("timerTime");

        setTimerValues();

        if(reservationsData != null){

            if (reservationsData.getMovieImage() != null) {

                Glide.with(ReservationSummary.this).load(reservationsData.getMovieImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);

            }else {
                Glide.with(ReservationSummary.this).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);

            }

            movieNameTv.setText(reservationsData.getFilmName());

            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MMM-dd");

                String getDate = reservationsData.getDate();

                Date d = sdf.parse(getDate);

                final String date = sdf2.format(d);

                //yyyy-MMM-dd
                String[] splitDate = date.split("-");

                String getDaySuffix = getDayOfMonthSuffix(Integer.parseInt(splitDate[2]));

                movieDateTimeTv.setText(splitDate[2] + getDaySuffix + " " + splitDate[1] + " " + splitDate[0] + ", " + reservationsData.getShowTime());

            } catch (Exception e) {
                e.printStackTrace();
            }

            theaterNameTv.setText(reservationsData.getExperience());

            theaterCityTv.setText(reservationsData.getLocationName());

            seatsCountTv.setText(reservationsData.getTotalTickets()+"");

            String selectedSeat = reservationsData.getSelectedSeats();

            String[] getSeatsName = selectedSeat.split("-");
            seatsNameTv.setText(getSeatsName[1]);

            /***---  Total Food Price --**/

            Double foodTotal = 0.0;
            for (ConcessionData con: reservationsData.getSelectedFoodItems()) {

                Double getPrice = Double.parseDouble(con.getPrice()) * con.getCount();
                foodTotal += getPrice;

            }

            Double total = Double.parseDouble(reservationsData.getAmount());
            Double subTot = foodTotal + total;

            totalAmountTv.setText("Rs. "+ String.format("%.2f", subTot));

            if(reservationsData.getSelectedFoodItems().size() != 0){

                foodItemLayout.setVisibility(View.VISIBLE);

                adapter = new FoodAdapter(ReservationSummary.this, reservationsData.getSelectedFoodItems(), ReservationSummary.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }else {

                foodItemLayout.setVisibility(View.GONE);
            }

        }else {

        }

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                            CallApiMethods.getAccessToken(new ApiServerCallBack() {
                                @Override
                                public void onSuccess(Boolean result) {

                                }

                                @Override
                                public void onSuccess(Boolean result, String token) {
                                    if(result) {

                                        updateFoodAndCustomer(token, false);

                                    }else {
                                        Functions.showSnackbar(getCurrentFocus());
                                    }
                                }
                            }, ReservationSummary.this);

            }
        });

        chkAndroid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkAndroid.isChecked()){

                    payBtn.setEnabled(true);
                }else {
                    payBtn.setEnabled(false);
                }

            }
        });

    }


    @Override
    public void removeFoodObject(ConcessionData object) {

        ArrayList<ConcessionData> nowCon = new ArrayList<>();

        Double foodTotal = 0.0;
        for(int i =0; i < reservationsData.getSelectedFoodItems().size(); i++){

            ConcessionData con = reservationsData.getSelectedFoodItems().get(i);

            if(con.getItemId().equals(object.getItemId())){

                //
            }else {

                Double getPrice = Double.parseDouble(con.getPrice()) * con.getCount();
                foodTotal += getPrice;
                nowCon.add(con);

            }
        }

        if(nowCon.size() != 0){
            foodItemLayout.setVisibility(View.VISIBLE);
        }else {
            foodItemLayout.setVisibility(View.GONE);
        }

        Double total = Double.parseDouble(reservationsData.getAmount());
        Double subTot = foodTotal + total;

        totalAmountTv.setText("Rs. "+ String.format("%.2f", subTot));

        reservationsData.setSelectedFoodItems(nowCon);

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

    AlertDialog.Builder closeReservationDialog;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       // finish();
        showCloseDialod();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();

        //finish();
        closeReservationDialog = new AlertDialog.Builder(ReservationSummary.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cancel_reservation, null);

        closeReservationDialog.setView(dialogView);

        AlertDialog aa =    closeReservationDialog.create();

        aa.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        aa.setCancelable(false);


        Button btnYes = dialogView.findViewById(R.id.yesBtn);
        Button btnNo = dialogView.findViewById(R.id.noBtn);
        aa.show();
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                timeleft.setText("00:00");
//                timerStatus = PromosAndConfessionaies.TimerStatus.STOPPED;

                /***---- Call Cancel Payment Method ---***/
                getAccessTokenTocancelPayment();

                SeatPlan.getInstance().finish();
                PromosAndConfessionaies.getInstance().finish();


                aa.dismiss();
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (aa != null) {
                    aa.dismiss();
                }

            }
        });
    }


//    public void showAlertDialog(final Context context, String title, String message, String postBtnMsg, String negBtnMsg) {
//
//        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
//
//        alertDialog.setTitle(title);
//        alertDialog.setMessage(message);
//        alertDialog.setPositiveButton(postBtnMsg, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//            }
//        });
//
//        alertDialog.show();
//    }

    public void showCloseDialod() {

        try {
            closeReservationDialog = new AlertDialog.Builder(ReservationSummary.this);

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


                    SeatPlan.getInstance().finish();
                    PromosAndConfessionaies.getInstance().finish();
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }

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


            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });

        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    ProgressDialog progressBooking;
    public void updateFoodAndCustomer(String accessToken, boolean secondAttempt){

        progressBooking = new ProgressDialog(ReservationSummary.this, R.style.MyTheme);
        progressBooking.setCancelable(false);
        progressBooking.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        progressBooking.show();

        try {

            ArrayList<ConcessionData> concessionArray = reservationsData.getSelectedFoodItems();

            JSONArray putJArray = new JSONArray();

            for (int i = 0; i < concessionArray.size(); i++) {

                ConcessionData con = concessionArray.get(i);

                JSONObject addJOBJ = new JSONObject();

                addJOBJ.put("name", con.getName());
                addJOBJ.put("id", con.getItemId());
                addJOBJ.put("price", con.getPrice());
                addJOBJ.put("count", con.getCount());

                putJArray.put(addJOBJ);

            }

            String userName = userData.getString("firstName") +" "+ userData.getString("lastName");
            String mobile = userData.getString("mobile");
            String email = userData.getString("emailAddress");

            JSONObject foodAndCustomeJOBJ = new JSONObject();

            foodAndCustomeJOBJ.put("name", userName);
            foodAndCustomeJOBJ.put("mobile", mobile);
            foodAndCustomeJOBJ.put("email", email);
            foodAndCustomeJOBJ.put("reservation_id", reservationsData.getReservationId());
            foodAndCustomeJOBJ.put("selectedFoodItems", putJArray);
            foodAndCustomeJOBJ.put("user_key", CallApiMethods.userKey);

            CallApiMethods.updateFoodandCustomerData(new ServerCallBack() {
                @Override
                public void onSuccess(Boolean result, JSONObject jOBJ) {

                    try {

                        Functions.dismisSnacbar();

                        JSONObject getOBJ = jOBJ;

                        if (getOBJ.getBoolean("status")) {

                            JSONObject getVersionJOBJ = getOBJ.getJSONObject("version");

                            String version = getVersionJOBJ.getString("andorid");

                       if (sharedPrefVersion.equals(version)) {

                            String amount = getOBJ.getString("amount");

                            reservationsData.setAmount(amount);

                            progressBooking.dismiss();

                            Intent intent = new Intent(ReservationSummary.this, CollectCardInfoActivity.class);
                            intent.putExtra("reservationsOBJ", reservationsData);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                       } else {

                       startActivity(new Intent(ReservationSummary.this, VersionPage.class));
                       overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                     }


                        } else {

                            progressBooking.dismiss();

                            showErrorRecervationDialog();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
                                    // do nothing
                                }

                                @Override
                                public void onSuccess(Boolean result, String token) {
                                    if(result){
                                        if(!secondAttempt){

                                            updateFoodAndCustomer(token, true);
                                        }else {
                                            Functions.showSnackbar(getCurrentFocus());
                                        }
                                    }else {
                                        Functions.showSnackbar(getCurrentFocus());
                                    }
                                }
                            }, ReservationSummary.this);


                        }

                    } else if (error instanceof ServerError) {

                        Functions.showSnackbar(getCurrentFocus());

                    } else if (error instanceof NetworkError) {

                        Functions.showSnackbar(getCurrentFocus());

                    } else if (error instanceof ParseError) {

                        Functions.showSnackbar(getCurrentFocus());
                    }

                   // showAlertDialog(ReservationSummary.this, "Warning!", "Please try again.!", "OK", "NO");
                }
            }, ReservationSummary.this, accessToken, foodAndCustomeJOBJ);


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void showErrorRecervationDialog() {

        try {

            AlertDialog.Builder errorDialog = new AlertDialog.Builder(ReservationSummary.this);

            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_session_expired, null);

            errorDialog.setView(dialogView);
            AlertDialog alertDialog = errorDialog.create();

            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setCancelable(false);

            Button btnGoHome = dialogView.findViewById(R.id.goHomeBtn);

            alertDialog.show();
            btnGoHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SeatPlan.getInstance().finish();
                    PromosAndConfessionaies.getInstance().finish();
                    finish();

                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }

                    finish();
                }
            });

            errorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog.dismiss();
                }
            });

            errorDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });

        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    int status = 0;

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        ReservationSummary.this.registerReceiver(exampleBroadcastReceiver, filter);

    }

    @Override
    public void onStop() {
        super.onStop();

        ReservationSummary.this.unregisterReceiver(exampleBroadcastReceiver);

    }

    private void showInternetDisconnectedView(boolean disconnected) {

        boolean isNetwork = Functions.isInternetAvailable(ReservationSummary.this);


        if (isNetwork) {

            Functions.dismisSnacbar();

            if (status == 0) {

                status = 1;

            }else {

                finish();
            }

        }else{

            View view = findViewById(android.R.id.content);
            Functions.showSnackbar(view);

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

                timerTv.setText(hmsTimeFormatter(millisUntilFinished));
             //   timeleft = millisUntilFinished;

            }

            @Override
            public void onFinish() {

                timerTv.setText("00:00");
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

    AlertDialog.Builder sessionExpiredDialog;
    public void showSessionExpiredDialog() {

        try {
          //  sessionExpiredDialog = new AlertDialog.Builder(ReservationSummary.this);

            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_session_expired, null);

            sessionExpiredDialog.setView(dialogView);
            AlertDialog alertDialog = sessionExpiredDialog.create();

            alertDialog.setCancelable(false);
            Button btnGoHome = dialogView.findViewById(R.id.goHomeBtn);

            alertDialog.show();
            btnGoHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /***--- Call Payment Cancel Method ---***/
                    getAccessTokenTocancelPayment();

                    SelectADate.getInstance().finish();
                    SelectAShowTime.getInstance().finish();
                    SeatPlan.getInstance().finish();
                    PromosAndConfessionaies.getInstance().finish();


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
        }, ReservationSummary.this);

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
                        }, ReservationSummary.this);


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
        }, ReservationSummary.this, accessToken, CallApiMethods.userKey, reservationsData.getReservationId());
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
