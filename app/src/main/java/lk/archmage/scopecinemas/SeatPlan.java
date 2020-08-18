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
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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

import lk.archmage.scopecinemas.Classes.PricesAdapter;
import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.MethodCallBack;
import lk.archmage.scopecinemas.Common.ServerCallBack;
import lk.archmage.scopecinemas.Interfaces.GetPositionClick;
import lk.archmage.scopecinemas.Interfaces.PriceInterface;
import lk.archmage.scopecinemas.Models.MovieShowTimesTheatersData;
import lk.archmage.scopecinemas.Models.ReservationsData;
import lk.archmage.scopecinemas.Models.SeatPlanData;

public class SeatPlan extends AppCompatActivity implements PriceInterface, GetPositionClick {

    TextView movieShowDate, movieNameTv, movieVenue, movieClass,
            totTicCountTv, textViewTime, moveGenreTv;
    ImageView movieImageIV;
    LinearLayout buttonLayout;

    MovieShowTimesTheatersData theaterObj;
    SeatPlanData seatPlanDataObj;

    static SeatPlan seatPlan;

    public static SeatPlan getInstance() {
        return seatPlan;
    }

    private Button confirmBtn;

    String sharedPrefVersion = "0";

    String selectedShowTimeId = "";

    String selectedShowTime = "";

    ExampleBroadcastReceiver exampleBroadcastReceiver = new ExampleBroadcastReceiver();

    TableLayout tableLayout;

    private static int ROW = 0;
    private static int COL = 0;

    ArrayList<String> selectedSeatListId = new ArrayList<>();
    ArrayList<String> selectedSeatsName = new ArrayList<>();

    JSONArray seatIds;

    private RecyclerView recyclerView;

    JSONArray pricesJArray;
    Double totalPrice = 0.0;

    String cinemaId = "";

    long timeleft=0;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private CountDownTimer countDownTimer;

    private long timeCountInMilliSeconds = 1 * 1000;

    JSONObject userData;

    NestedScrollView nestedScrollView;


    boolean firstTime = true;
    HorizontalScrollView horizontalScrollerView;

    AppBarLayout appBarLayout;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_plan);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        seatPlan = this;
        closeReservationDialog = new AlertDialog.Builder(SeatPlan.this);
        sessionExpiredDialog = new AlertDialog.Builder(SeatPlan.this);

        progress = new ProgressDialog(SeatPlan.this, R.style.MyTheme);

        appBarLayout = findViewById(R.id.appBarLayout);


        /***  Check Version Prefarance***/
        SharedPreferences appSharedPref = this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);

        if (!appSharedPref.getString("version", "").isEmpty()) {

            sharedPrefVersion = appSharedPref.getString("version", "");

        }

        /***--- Check User Sheare prefarances ---***/
        SharedPreferences appSharedPreff = this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);

        if(appSharedPreff.getString("user", "").isEmpty()){

        }else{

            try {

                userData = new JSONObject(appSharedPreff.getString("user", ""));

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        /**--- Start Count Down Timer ---**/
        setTimerValues();

        pricesJArray = new JSONArray();

        Toolbar toolbar = findViewById(R.id.toolBarSelectSeats);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        selectedSeatListId.clear();
        selectedSeatsName.clear();

        tableLayout = findViewById(R.id.TableSeats);
        tableLayout.removeAllViews();

        buttonLayout = findViewById(R.id.buttonLayout);
        confirmBtn = findViewById(R.id.confirmBtn);


        movieImageIV = findViewById(R.id.movieImageIV);
        movieShowDate = findViewById(R.id.movieShowDate);
        movieNameTv = findViewById(R.id.movieNameTv);
        movieVenue = findViewById(R.id.movieVenue);
        movieClass = findViewById(R.id.movieClass);
        totTicCountTv = findViewById(R.id.totTicCountTv);
        textViewTime = findViewById(R.id.textViewTime);
        moveGenreTv = findViewById(R.id.moveGenreTv);

        horizontalScrollerView = findViewById(R.id.horizontalScrollerView);

        nestedScrollView = findViewById(R.id.nestedScroller);

        Bundle bundle = getIntent().getExtras();

        confirmBtn.setEnabled(false);

        String movieImage = bundle.getString("movieImage");

        theaterObj = (MovieShowTimesTheatersData) getIntent().getSerializableExtra("movieShowDataOBJ");

        movieNameTv.setText(theaterObj.getMovieName());


        if (movieImage != null) {

            Glide.with(SeatPlan.this).load(movieImage).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);
        }else {
            Glide.with(SeatPlan.this).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);
        }

        String runTime = bundle.getString("runTime");
        String replaceString2 = "";
        if(!runTime.equals("") || !runTime.isEmpty()) {


            String replaceString1 = runTime.replace("Hour","h");
            replaceString2 = "~" + replaceString1.replace("Minutes","m");
           // movieTimeTv.setText("~ "+ replaceString2);

        }

        selectedShowTime = bundle.getString("selectedMovieShowTime");
        selectedShowTimeId = bundle.getString("movieShowTimeID");
        cinemaId = bundle.getString("cinemaId");
        String experienceName = bundle.getString("experienceName");
        movieVenue.setText(experienceName);
        movieClass.setText(theaterObj.getTheaterName());


        ArrayList<String> genreArray = bundle.getStringArrayList("genre");
        String genre = "";

        for(int a = 0; a < genreArray.size(); a++){

            genre += genreArray.get(a)+ " | ";
        }

        if (genre != null && genre.length() > 0 && genre.charAt(genre.length() - 2) == '|') {
            genre = genre.substring(0, genre.length() - 2);
        }
        moveGenreTv.setText(genre +" "+ replaceString2);

        try {

            String getMovieShowTime = bundle.getString("selectedMovieShowTime");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MMM-dd");

            String getDate = theaterObj.getShowTimeDate();

            Date d = sdf.parse(getDate);

            final String date = sdf2.format(d);

            //yyyy-MMM-dd
            String[] splitDate = date.split("-");

            String getDaySuffix = getDayOfMonthSuffix(Integer.parseInt(splitDate[2]));

            movieShowDate.setText(splitDate[2] + getDaySuffix + " " + splitDate[1] + " " + splitDate[0] + ", " + getMovieShowTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                CallApiMethods.getAccessToken(new ApiServerCallBack() {
                    @Override
                    public void onSuccess(Boolean result) {

                    }

                    @Override
                    public void onSuccess(Boolean result, String token) {

                        if (result) {

                            boolean tag = false;

                            if (tag == false) {

                                /***--- Convert SeatListIDs String Array to JSONArray  ---***/
                                seatIds = new JSONArray();

                                for (int i = 0; i < selectedSeatListId.size(); i++) {

                                    seatIds.put(selectedSeatListId.get(i));

                                }

                                tag = true;
                            }

                            if (tag) {

                                /***--- Call Save Booking Api Method ---***/
                                saveBookingData(v, token, "", false);
                            }
                        } else {
                            Functions.showSnackbar(v);
                        }
                    }
                }, SeatPlan.this);
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(SeatPlan.this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void setPosition(int position) {


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

    AlertDialog.Builder closeReservationDialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        showCloseDialod();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        closeReservationDialog = new AlertDialog.Builder(SeatPlan.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cancel_reservation, null);

        closeReservationDialog.setView(dialogView);
        AlertDialog alertDialog = closeReservationDialog.create();

        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnYes = dialogView.findViewById(R.id.yesBtn);
        Button btnNo = dialogView.findViewById(R.id.noBtn);

        alertDialog.show();
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textViewTime.setText("00:00");
                timerStatus = TimerStatus.STOPPED;


//                SelectADate.getInstance().finish();
//                SelectAShowTime.getInstance().finish();


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

            closeReservationDialog = new AlertDialog.Builder(SeatPlan.this);

            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_cancel_reservation, null);

            closeReservationDialog.setView(dialogView);
            AlertDialog alertDialog = closeReservationDialog.create();

            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            Button btnYes = dialogView.findViewById(R.id.yesBtn);
            Button btnNo = dialogView.findViewById(R.id.noBtn);

            alertDialog.show();
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    textViewTime.setText("00:00");
                    timerStatus = TimerStatus.STOPPED;

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

    String secondTimeAddSeatId = "";
    String getSeatId;
    String getSeatName;
    int selectedSeatCount = 0;

    public void createMyTable() {

        try {

            ROW = seatPlanDataObj.getRowCount();
            COL = seatPlanDataObj.getColCount();

            ArrayList seatsArray = seatPlanDataObj.getSeats();

           // for (int i = ROW; i >= 1; i--) {
                for (int i = 1; i <= ROW; i++) {

                    TableRow tableRow = new TableRow(SeatPlan.this);

                    tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    tableRow.setWeightSum(0.1f);
                    tableRow.setPadding(10, 5, 50, 5);

                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(dpToPxTwo(23), dpToPxTwo(23), 0.0f);
                    layoutParams.setMargins(3, 0, 3, 0);


                    for (int j = 1; j <= COL; j++) {

                        final int FINAL_ROW = i;
                        final int FINAL_COL = j;

                        final TextView text = new TextView(this);

                        text.setLayoutParams(new android.widget.TableRow.LayoutParams(dpToPxTwo(23),
                                dpToPxTwo(23), 0.0f));

                        text.setText("");
                        text.setGravity(Gravity.CENTER);
                        text.setTextColor(Color.parseColor("#000000"));
                        text.setBackgroundResource(R.drawable.rounded_corner_seat_none);
                        text.setVisibility(View.INVISIBLE);
                        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPxTwo(8));
                        // text.setEnabled(false);

                        //   for (int h = seatsArray.size() - 1; h > -1; h--) {
                        for (int h = 0; h < seatsArray.size(); h++) {

                            try {

                                final JSONObject getClassObject = new JSONObject((String) seatsArray.get(h));

                                int row = Integer.parseInt(getClassObject.getString("rowID"));
                                int column = Integer.parseInt(getClassObject.getString("gridID"));

                                int availability = Integer.parseInt(getClassObject.getString("status"));

                                if (FINAL_ROW == row && FINAL_COL == column) {

                                    text.setBackgroundResource(R.drawable.rounded_corner_seat_available);
                                    text.setText(getClassObject.getString("alias"));
                                    text.setGravity(Gravity.CENTER);
                                    text.setTextColor(Color.parseColor("#1d1e32"));
                                    text.setVisibility(View.VISIBLE);
                                    //text.setClickable(true);


                                    if (availability == 0) {

                                        text.setEnabled(true);
                                        text.setBackgroundResource(R.drawable.rounded_corner_seat_available);

                                        if (getClassObject.getString("seatID").equals(secondTimeAddSeatId)) {

                                            text.setTextColor(Color.parseColor("#FFFFFF"));
                                            text.setBackgroundResource(R.drawable.rounded_corner_seat_selected);
                                        }

                                        text.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                try {

                                                    getSeatId = getClassObject.getString("seatID");
                                                    getSeatName = getClassObject.getString("alias");

                                                    if (selectedSeatListId.size() == 0) {

                                                        selectedSeatCount++;
                                                        selectedSeatListId.add(getSeatId);
                                                        selectedSeatsName.add(getSeatName);

                                                        text.setTextColor(Color.parseColor("#FFFFFF"));
                                                        text.setBackgroundResource(R.drawable.rounded_corner_seat_selected);

                                                        createPriceList();

                                                        confirmBtn.setEnabled(true);

                                                    } else {

                                                        if (selectedSeatListId.contains(getSeatId)) {

                                                            selectedSeatCount--;
                                                            selectedSeatListId.remove(getSeatId);
                                                            selectedSeatsName.remove(getSeatName);

                                                            text.setBackgroundResource(R.drawable.rounded_corner_seat_available);
                                                            text.setTextColor(Color.parseColor("#1d1e32"));

                                                            createPriceList();

                                                            if (selectedSeatListId.size() == 0) {
                                                                confirmBtn.setEnabled(false);
                                                            }

                                                        } else {

                                                            selectedSeatCount++;
                                                            selectedSeatListId.add(getSeatId);
                                                            selectedSeatsName.add(getSeatName);

                                                            text.setTextColor(Color.parseColor("#FFFFFF"));
                                                            text.setBackgroundResource(R.drawable.rounded_corner_seat_selected);

                                                            createPriceList();
                                                        }

                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });


                                    } else {

                                        text.setEnabled(false);
                                        text.setTextColor(Color.parseColor("#FFFFFF"));
                                        text.setBackgroundResource(R.drawable.rounded_corner_seat_unavailable);
                                    }
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        tableRow.addView(text, layoutParams);
                    }

                    tableLayout.addView(tableRow);


                    if (firstTime){
                        scrollToCenter(horizontalScrollerView);
                    }
            }

            createPriceList();

            if (progress != null) {

                progress.dismiss();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int dpToPxTwo(int dp) {
        Resources r = getResources();
        return Math.round((float) dp * r.getDisplayMetrics().density);
    }


    PricesAdapter adapter;
    /***--- Create Prices List ----****/
    public void createPriceList(){

        try{

            Double totPrice = 0.0;
            pricesJArray = new JSONArray();

            for(int i = 0; i < seatPlanDataObj.getPrices().size(); i++){

                JSONObject getJOBJ = new JSONObject(seatPlanDataObj.getPrices().get(i));

                JSONObject priceJOBJ = new JSONObject();

                priceJOBJ.put("TType_strDescription", getJOBJ.getString("TType_strDescription"));
                priceJOBJ.put("TType_strCode", getJOBJ.getString("TType_strCode"));
                priceJOBJ.put("Price_curPrice", getJOBJ.getString("Price_curPrice"));
                priceJOBJ.put("Price_strChildTicket", getJOBJ.getString("Price_strChildTicket"));
                priceJOBJ.put("available", getJOBJ.getString("available"));
                priceJOBJ.put("name", getJOBJ.getString("name"));

                if(i == 0) {
                    priceJOBJ.put("count", selectedSeatCount);

                    totPrice += Double.parseDouble(getJOBJ.getString("Price_curPrice"));
                }else {
                    priceJOBJ.put("count", 0);
                }

                pricesJArray.put(priceJOBJ);

            }

            /***--- Set Total Tickets Count TextView ---***/
            totTicCountTv.setText("Total tickets - "+ selectedSeatCount);

            /***---  Set Total Tickets Price Confirm Button Text ---***/
            confirmBtn.setText("CONTINUE RS. "+ String.format("%.2f", totPrice * selectedSeatCount));

            /****---  Call Pricess Adapter For List ---***/
            adapter = new PricesAdapter(SeatPlan.this, pricesJArray, selectedSeatCount, this);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);

            buttonLayout.setVisibility(View.VISIBLE);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void scrollToCenter(final HorizontalScrollView horizontalScrollView) {
        horizontalScrollView.getChildAt(0).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                nestedScrollView.setNestedScrollingEnabled(true);

                if(firstTime) {
                    horizontalScrollView.scrollTo(horizontalScrollView.getWidth() / 4, 0);

                     nestedScrollView.fullScroll(View.FOCUS_UP);

                }
                firstTime = false;

            }
        });
    }


    int status = 0;

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        SeatPlan.this.registerReceiver(exampleBroadcastReceiver, filter);

    }

    @Override
    public void onStop() {
        super.onStop();

        SeatPlan.this.unregisterReceiver(exampleBroadcastReceiver);

    }

    ProgressDialog progress;

    private void showInternetDisconnectedView(boolean disconnected) {

        progress = new ProgressDialog(SeatPlan.this, R.style.MyTheme);
        progress.setCancelable(false);
        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        progress.show();

        boolean isNetwork = Functions.isInternetAvailable(SeatPlan.this);

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

                            String sessionId = getIntent().getStringExtra("movieShowTimeID");
                            String cinemaId = getIntent().getStringExtra("cinemaId");

                            getSeatplanByMovieSelectedShowTime(new MethodCallBack() {
                                @Override
                                public void onSuccess(Boolean result) {

                                    if(result){

                                        ArrayList seatsArray = seatPlanDataObj.getSeats();
                                        if(seatsArray.size() != 0) {

                                            /***---- Call Method Carete Seat Plan ***/
                                            createMyTable();

                                        }else {
                                            SeatPlan.this.finish();
                                        }

                                    }else {

                                        SeatPlan.this.finish();
                                    }
                                }
                            },token, sessionId, cinemaId, false);

                        } else {

                            View view = findViewById(android.R.id.content);
                            Functions.showSnackbar(view);

                        }

                    }
                }, SeatPlan.this);

                status = 1;

            } else {

                if (progress != null) {

                    progress.dismiss();
                }

                finish();

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


    /***---  Calculate Total Tickets Count ---***/
    public void totalTicketsPrice(JSONArray getPricesArray){
        try {

            totalPrice = 0.0;
            for (int i = 0; i < getPricesArray.length(); i++) {

                JSONObject getOBJ = getPricesArray.getJSONObject(i);

                totalPrice += Double.parseDouble(getOBJ.getString("Price_curPrice")) * Integer.parseInt(getOBJ.getString("count"));

            }

            /***---  Set Total Tickets Price Confirm Button Text ---***/
            confirmBtn.setText("CONTINUE RS. "+ String.format("%.2f", totalPrice));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /***---  override method PriceInterface --***/
    @Override
    public void getPriceJSONArray(JSONArray jArray) {

        try {
            pricesJArray = jArray;

            totalTicketsPrice(pricesJArray);

        } catch (Exception e) {
            e.printStackTrace();
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

    public void getSeatplanByMovieSelectedShowTime(MethodCallBack methodCallBack, String accessToken, String sessionId, String cinemaID, final boolean secondAttempt){

        CallApiMethods.getSeatPlan(new ServerCallBack() {
            @Override
            public void onSuccess(Boolean result, JSONObject jOBJ) {

                Functions.dismisSnacbar();

                try {
                    JSONObject getOBJ = jOBJ;

                    if(getOBJ.getBoolean("status")) {

                        JSONObject getVersionJOBJ = getOBJ.getJSONObject("version");

                        String version = getVersionJOBJ.getString("andorid");

                        if (sharedPrefVersion.equals(version)) {


                            boolean tag = false;

                            JSONObject getSeatPlanObj = getOBJ.getJSONObject("seatplan");

                            ArrayList<String> seatArray = new ArrayList();
                            ArrayList<String> rowNumbersArray = new ArrayList<>();
                            ArrayList<String> allSessionsSameday = new ArrayList<>();
                            ArrayList<String> priceArray = new ArrayList<>();

                            if (tag == false) {

                                /******---  get Seat Object ---*****/
                                JSONArray getSeatJSONA = getSeatPlanObj.getJSONArray("seats");

                                for (int i = 0; i < getSeatJSONA.length(); i++) {

                                    JSONObject getSeatOBJ = getSeatJSONA.getJSONObject(i);

                                    seatArray.add(getSeatOBJ.toString());
                                }

                                /****---  get RowNumbers ---*****/
                                JSONArray getRowNumbersJSONA = getSeatPlanObj.getJSONArray("rowNumbers");

                                for (int i = 0; i < getRowNumbersJSONA.length(); i++) {

                                    String getRowNumber = getRowNumbersJSONA.getString(i);
                                    rowNumbersArray.add(getRowNumber);
                                }

                                /****---  get allSessions_sameday  ---***/
                                JSONArray getAllSession = getOBJ.getJSONArray("allSessions_sameday");

                                for (int i = 0; i < getAllSession.length(); i++) {

                                    JSONObject getAllSessionJOBJ = getAllSession.getJSONObject(i);
                                    allSessionsSameday.add(getAllSessionJOBJ.toString());
                                }

                                /****---  get prices  ---***/
                                JSONArray getprices = getOBJ.getJSONArray("prices");

                                for (int i = 0; i < getprices.length(); i++) {

                                    JSONObject getPriceJOBJ = getprices.getJSONObject(i);
                                    priceArray.add(getPriceJOBJ.toString());

                                }

                                tag = true;

                            }

                            if (tag == true) {

                                seatPlanDataObj = new SeatPlanData(
                                        seatArray,
                                        rowNumbersArray,
                                        getSeatPlanObj.getString("status"),
                                        getSeatPlanObj.getString("error"),
                                        getSeatPlanObj.getInt("session_id"),
                                        getSeatPlanObj.getString("transaction_id"),
                                        getSeatPlanObj.getInt("rowCount"),
                                        getSeatPlanObj.getInt("colCount"),
                                        allSessionsSameday,
                                        priceArray

                                );

                                tag = false;

                                methodCallBack.onSuccess(true);
                            }

                        } else {

                            startActivity(new Intent(SeatPlan.this, VersionPage.class));
                            ((Activity) SeatPlan.this).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }
                    }else {

                        finish();
                    }


                }catch (JSONException e){
                    e.printStackTrace();

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

                                        getSeatplanByMovieSelectedShowTime(new MethodCallBack() {
                                            @Override
                                            public void onSuccess(Boolean result) {

                                                if (result) {


                                                } else {

                                                }
                                            }
                                        }, token, sessionId, cinemaID,true);

                                    } else {
                                        Functions.showSnackbar(view);
                                    }
                                } else {
                                    Functions.showSnackbar(view);

                                }
                            }
                        }, SeatPlan.this);


                    }

                } else if (error instanceof ServerError) {

                    Functions.showSnackbar(view);

                } else if (error instanceof NetworkError) {

                    Functions.showSnackbar(view);

                } else if (error instanceof ParseError) {

                    Functions.showSnackbar(view);
                }
            }

        },SeatPlan.this, accessToken, CallApiMethods.userKey, sessionId, cinemaID);

    }


    ProgressDialog progressBooking;
    public void saveBookingData(final View v, String accessToken, final String deviceId, final boolean secondAttempt) {

        progressBooking = new ProgressDialog(SeatPlan.this, R.style.MyTheme);
        progressBooking.setCancelable(false);
        progressBooking.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        progressBooking.show();

        try {

            JSONObject setObj = new JSONObject();

            JSONArray typeJArray = new JSONArray();

            for(int i = 0; i < pricesJArray.length(); i++){

                JSONObject getPriceOBJ = pricesJArray.getJSONObject(i);

                JSONObject type = new JSONObject();

                type.put("type_id", getPriceOBJ.getString("TType_strCode"));
                type.put("count", getPriceOBJ.getString("count"));
                type.put("price", getPriceOBJ.getString("Price_curPrice"));
                type.put("name", getPriceOBJ.getString("name"));

                typeJArray.put(type);

            }

            String userId = userData.getString("id");

            setObj.put("user_key", CallApiMethods.userKey);
            setObj.put("transaction_id", seatPlanDataObj.getTransactionId());
            setObj.put("movie_id", theaterObj.getMovieId());
            setObj.put("cinema_id", cinemaId);
            setObj.put("session_id", seatPlanDataObj.getSessionId());
            setObj.put("totalTickets", selectedSeatCount);
            setObj.put("types", typeJArray);
            setObj.put("selected_seats", seatIds);
            setObj.put("user_id", userId);

            CallApiMethods.saveBookingData(new ServerCallBack() {
                                               @Override
                                               public void onSuccess(Boolean result, JSONObject jOBJ) {

                                                   try {

                                                       Functions.dismisSnacbar();

                                                       JSONObject getOBJ = jOBJ;

                                                       if (getOBJ.getBoolean("status")) {

                                                           JSONObject getVersionJOBJ = getOBJ.getJSONObject("version");

                                                           String version = getVersionJOBJ.getString("andorid");

                                                           if (sharedPrefVersion.equals(version)) {

                                                                boolean tag = false;

                                                                JSONObject getReservationOBJ = getOBJ.getJSONObject("reservation");
                                                               JSONObject getFilmJOBJ = getReservationOBJ.getJSONObject("film");

                                                                ArrayList<String> typesArray = new ArrayList<>();
                                                                typesArray.clear();
                                                                ArrayList<String> foodItemArray = new ArrayList<>();
                                                                foodItemArray.clear();
                                                                ArrayList<String> genreArray = new ArrayList<>();
                                                                genreArray.clear();


                                                                if(tag == false){

                                                                    /***--- Get Type JSON Array ---***/
                                                                    JSONArray getTypesJA = getReservationOBJ.getJSONArray("types");

                                                                    for(int i = 0; i < getTypesJA.length(); i++){

                                                                        JSONObject getTypeOBJ = getTypesJA.getJSONObject(i);
                                                                        typesArray.add(getTypeOBJ.toString());
                                                                    }

                                                                    /***--- Get Food Item Array ---***/
                                                                    JSONArray getFoodIJA = getReservationOBJ.getJSONArray("selectedFoodItems");

                                                                    for(int i = 0; i < getFoodIJA.length(); i++){

                                                                        JSONObject getFoodIOBJ = getFoodIJA.getJSONObject(i);
                                                                        foodItemArray.add(getFoodIOBJ.toString());
                                                                    }

                                                                    /****--- Get Genre Array ---***/
                                                                    JSONArray getGenreJA = getFilmJOBJ.getJSONArray("genre");

                                                                    for(int i = 0; i < getGenreJA.length(); i++){

                                                                        genreArray.add(getGenreJA.get(i).toString());
                                                                    }

                                                                    System.out.println(" =================================="+getFilmJOBJ.getString("runtime"));

                                                                    tag = true;



                                                                }


                                                                JSONObject getLocationJOBJ = getReservationOBJ.getJSONObject("location");


                                                                if(tag == true) {

                                                                    ReservationsData reservationsData = new ReservationsData(
                                                                            getOBJ.getString("reservation_id"),
                                                                            getReservationOBJ.getInt("createdAt"),
                                                                            getReservationOBJ.getInt("updatedAt"),
                                                                            getReservationOBJ.getString("name"),
                                                                            getReservationOBJ.getString("email"),
                                                                            getReservationOBJ.getString("mobile"),
                                                                            getReservationOBJ.getString("description"),
                                                                            getReservationOBJ.getInt("method"),
                                                                            getReservationOBJ.getInt("payment_type"),
                                                                            getReservationOBJ.getString("sales_channel"),
                                                                            getReservationOBJ.getString("status"),
                                                                            getReservationOBJ.getString("transaction_id"),
                                                                            getReservationOBJ.getInt("booking_id"),
                                                                            getReservationOBJ.getString("string_booking_id"),
                                                                            getReservationOBJ.getString("cinema_id"),
                                                                            getReservationOBJ.getInt("session_id"),
                                                                            getReservationOBJ.getInt("totalTickets"),
                                                                            getReservationOBJ.getString("amount"),
                                                                            getReservationOBJ.getString("ticketAmount"),
                                                                            getReservationOBJ.getString("foodAmount"),
                                                                            typesArray,
                                                                            getReservationOBJ.getString("selected_seats"),
                                                                            getFilmJOBJ.getString("id"),
                                                                            getFilmJOBJ.getString("name"),
                                                                            getLocationJOBJ.getString("id"),
                                                                            getLocationJOBJ.getString("name"),
                                                                            getReservationOBJ.getInt("screen_num"),
                                                                            getReservationOBJ.getString("screen_name"),
                                                                            getReservationOBJ.getString("date"),
                                                                            getReservationOBJ.getInt("date_timestamp"),
                                                                            getFilmJOBJ.getString("runtime"),
                                                                            getReservationOBJ.getString("show_time"),
                                                                            getReservationOBJ.getString("successIndicator"),
                                                                            getReservationOBJ.getString("session_version"),
                                                                            getReservationOBJ.getString("experince"),
                                                                            getReservationOBJ.getString("staff"),
                                                                            getReservationOBJ.getString("user"),
                                                                            getFilmJOBJ.getString("image"),
                                                                            getFilmJOBJ.getString("mobile_image"),
                                                                            getFilmJOBJ.getString("banner_image"),
                                                                            genreArray,
                                                                            foodItemArray
                                                                    );

//
//                                                               /**  Insert myTheaterData for SQLite DB ************/
//
//                                                               //   String insert_q = "insert into myTheater_data(theaterId) values('"+theaterObj.getTheaterId()+"')";
//
//                                                               //     LocalStore.insert_Update_Delete(SeatPlan.this, insert_q);
//
//                                                               /**  Insert myTheaterData for SQLite DB End **/
//
                                                                    progressBooking.dismiss();


                                                                    Intent intent = new Intent(SeatPlan.this, PromosAndConfessionaies.class);
                                                                    intent.putExtra("reservationOBJ", reservationsData);
                                                                    intent.putExtra("timerTime", timeleft);
                                                                    startActivity(intent);
                                                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                                                }
                                                           } else {

                                                               progressBooking.dismiss();
                                                               startActivity(new Intent(SeatPlan.this, VersionPage.class));
                                                               overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                                           }

                                                       } else {

                                                           progressBooking.dismiss();

                                                           AlertDialog.Builder alertDialog = new AlertDialog.Builder(SeatPlan.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                                                           alertDialog.setTitle("Scope Cinemas");
                                                           alertDialog.setMessage("Sorry! .\nPlease Tryagain..");
                                                           alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                               @Override
                                                               public void onClick(DialogInterface dialog, int which) {

                                                                   dialog.dismiss();
                                                                   finish();
                                                               }
                                                           });

                                                           alertDialog.show();

                                                       }

                                                   } catch (JSONException e) {
                                                       e.printStackTrace();
                                                   }

                                               }

                                               @Override
                                               public void onFailure(Boolean result, VolleyError error) {

                                                   progressBooking.dismiss();

                                                   if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                                                       Functions.showSnackbar(v);

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

                                                                           saveBookingData(v, token, deviceId, true);

                                                                       } else {
                                                                           Functions.showSnackbar(v);
                                                                       }
                                                                   } else {
                                                                       Functions.showSnackbar(v);
                                                                   }
                                                               }
                                                           }, SeatPlan.this);


                                                       }

                                                   } else if (error instanceof ServerError) {

                                                       Functions.showSnackbar(v);

                                                   } else if (error instanceof NetworkError) {

                                                       Functions.showSnackbar(v);

                                                   } else if (error instanceof ParseError) {

                                                       Functions.showSnackbar(v);
                                                   }
                                               }

                                           }, SeatPlan.this, accessToken, setObj);

        } catch (Exception e) {
            e.printStackTrace();
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

        int time = 5;
        timeCountInMilliSeconds = time * 60 * 1000;

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
                timeleft = millisUntilFinished;

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

    AlertDialog.Builder sessionExpiredDialog;
    AlertDialog alertDialog;
    public void showSessionExpiredDialog() {

        try {

            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_session_expired, null);

            sessionExpiredDialog.setView(dialogView);
            alertDialog = sessionExpiredDialog.create();

            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setCancelable(false);
            Button btnGoHome = dialogView.findViewById(R.id.goHomeBtn);

            alertDialog.show();
            btnGoHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SelectADate.getInstance().finish();
                    SelectAShowTime.getInstance().finish();

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

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if(alertDialog != null) {
//            alertDialog.dismiss();
//        }
//    }
}
