package lk.archmage.scopecinemas.ProcessPayment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mastercard.gateway.android.sdk.Gateway;
import com.mastercard.gateway.android.sdk.Gateway3DSecureCallback;
import com.mastercard.gateway.android.sdk.GatewayCallback;
import com.mastercard.gateway.android.sdk.GatewayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.ServerCallBack;
import lk.archmage.scopecinemas.Models.ReservationsData;
import lk.archmage.scopecinemas.MyTickets;
import lk.archmage.scopecinemas.PromosAndConfessionaies;
import lk.archmage.scopecinemas.R;
import lk.archmage.scopecinemas.ReservationSummary;
import lk.archmage.scopecinemas.SeatPlan;


public class CollectCardInfoActivity extends AppCompatActivity {

    Dialog processCardDialog;
    Dialog errorDialog;

    Button continueBtn;
    EditText cardNameTv, cardNumberTv, cvvTv;
    TextView monthyearTv;

    static final int REQUEST_CARD_INFO = 100;

    // static for demo
    static String AMOUNT = "";
    static final String CURRENCY = "LKR";

    Gateway gateway;
    String sessionId, apiVersion, threeDSecureId, orderId, transactionId;
    boolean isGooglePay = false;
    ApiController apiController = ApiController.getInstance();

    ReservationsData reservationsData;

    int yearSelected;
    int monthSelected;

    String yearSubstring = "";
    String month = "";


    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircle;

    private TextView textViewTime, totalAmountTv;
    private CountDownTimer countDownTimer;

    private long timeCountInMilliSeconds = 1 * 1000;

    ExampleBroadcastReceiver exampleBroadcastReceiver = new ExampleBroadcastReceiver();

    static CollectCardInfoActivity collectCardInfoActivity;

    public static CollectCardInfoActivity getInstance() {
        return collectCardInfoActivity;
    }

    private FirebaseAnalytics mFirebaseAnalytics;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_card_info);

        collectCardInfoActivity = this;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolBarCollectCard);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        errorDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);

        reservationsData = (ReservationsData) getIntent().getSerializableExtra("reservationsOBJ");

        Double total = Double.parseDouble(reservationsData.getAmount());

        String amo = String.format("%.2f", total);

        totalAmountTv = findViewById(R.id.totalAmountTv);
        totalAmountTv.setText("Rs. "+ amo);

        AMOUNT = amo;

        Log.i("AMOUNT", "=========*****===" + total);

        /**********   Open Full screen dialog *********/

        View view = getLayoutInflater().inflate(R.layout.dialog_processing_layout, null);
        processCardDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        processCardDialog.setContentView(view);
        processCardDialog.setCanceledOnTouchOutside(false);

        processCardDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });


        /************    Hide Soft Keyboard Activity startup **********/

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        continueBtn = findViewById(R.id.continueBtn);
        cardNameTv = findViewById(R.id.cardNameTv);
        cardNumberTv = findViewById(R.id.cardNumberTv);
        monthyearTv = findViewById(R.id.monthTv);
        cvvTv = findViewById(R.id.cvvTv);

        // init api controller
        apiController.setMerchantServerUrl(Config.MERCHANT_URL.getValue(this));

        // init gateway
        gateway = new Gateway();
        gateway.setMerchantId(Config.MERCHANT_ID.getValue(this));
        try {
            Gateway.Region region = Gateway.Region.valueOf(Config.REGION.getValue(this));
            gateway.setRegion(region);
        } catch (Exception e) {
            Log.e(CollectCardInfoActivity.class.getSimpleName(), "Invalid Gateway region value provided", e);
        }

        // random order/txn IDs for example purposes
        orderId = UUID.randomUUID().toString();
        orderId = orderId.substring(0, orderId.indexOf('-'));
        transactionId = UUID.randomUUID().toString();
        transactionId = transactionId.substring(0, transactionId.indexOf('-'));


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Functions.isInternetAvailable(CollectCardInfoActivity.this)) {

                    startStop();
                    createSession();

                } else {

                    Functions.showSnackbar(getCurrentFocus());

                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        yearSelected = calendar.get(Calendar.YEAR);
        monthSelected = calendar.get(Calendar.MONTH) + 1;

        String getYear = "" + yearSelected;
        yearSubstring = getYear.substring(2);
        month = "" + monthSelected;

       // monthyearTv.setText(month + " / " + yearSubstring);
       // monthyearTv.setText("mm / yyyyy");

        monthyearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(monthyearTv.getWindowToken(), 0);


                MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment
                        .getInstance(monthSelected, yearSelected);

                dialogFragment.show(getSupportFragmentManager(), null);

                dialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int year, int monthOfYear) {

                        String getYear = "" + year;
                        yearSubstring = getYear.substring(2);

                        monthSelected = monthOfYear + 1;
                        month = "" + monthSelected;

                        monthyearTv.setText(month + " / " + yearSubstring);

                        // Toast.makeText(CollectCardInfoActivity.this, "=========MonthOfYear : "+monthOfYear, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        progressBarCircle = findViewById(R.id.progressBarCircle);
        textViewTime = findViewById(R.id.textViewTime);

        startStop();

    }

    AlertDialog.Builder closeReservationDialog;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //finish();
        showCloseDialod();
        return super.onOptionsItemSelected(item);
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
    private void startStop() {

        if (timerStatus == TimerStatus.STOPPED) {

            setTimerValues();
            setProgressBarValues();
            timerStatus = TimerStatus.STARTED;
            startCountDownTimer();

        } else {

            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();
        }
    }

    /**
     * set time
     */
    private void setTimerValues() {

        int time = 5;
        timeCountInMilliSeconds = time * 60 * 1000;
    }

    /**
     * method timer
     */
    private void startCountDownTimer() {

        setProgressBarValues();

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                textViewTime.setText("00:00");
                timerStatus = TimerStatus.STOPPED;

                showErrorDialog();
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
    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


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


    void createSession() {

        if (checkEditTextValue()) {


            processCardDialog.show();

            //  continueBtn.setEnabled(false);

            apiController.createSession(new ApiController.CreateSessionCallback() {
                @Override
                public void onSuccess(String sessionId, String apiVersion) {

                    Log.i("CreateSessionTask", "Session established");

                    Log.i("sessionId:apiVersion", "=== " + sessionId + " : " + apiVersion);

                    CollectCardInfoActivity.this.sessionId = sessionId;
                    CollectCardInfoActivity.this.apiVersion = apiVersion;

                    processCard();

                }

                @Override
                public void onError(Throwable throwable) {

                    //todo

                    Log.e("ERROR", "createSession_ERROR");
                    showErrorDialog();

                    Log.e(CollectCardInfoActivity.class.getSimpleName(), throwable.getMessage(), throwable);

                }
            });

        } else {
            showAlertDialog(CollectCardInfoActivity.this, "Warning!", "Please Check Your Required Fields", "OK", "NO");
        }
    }

    public void processCard() {

        try {

            String cardName = cardNameTv.getText().toString().trim();
            String cardNumber = cardNumberTv.getText().toString().trim();
            String expiryMM = month;
            String expiryYY = yearSubstring;
            String cvv = cvvTv.getText().toString().trim();

            updateSession(cardName, cardNumber, expiryMM, expiryYY, cvv);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean value = true;

    public boolean checkEditTextValue() {

        if (isEmpty(cardNameTv)) {
            value = false;
        } else {
            value = true;
        }

        if (isEmpty(cardNumberTv)) {
            value = false;
        } else {
            value = true;
        }

        if (isEmpty(cvvTv)) {
            value = false;
        } else {
            value = true;
        }


        return value;
    }


    void updateSession(String name, String number, String expiryMonth, String expiryYear, String cvv) {

        // build the gateway request
        GatewayMap request = new GatewayMap()
                .set("sourceOfFunds.provided.card.nameOnCard", name)
                .set("sourceOfFunds.provided.card.number", number)
                .set("sourceOfFunds.provided.card.securityCode", cvv)
                .set("sourceOfFunds.provided.card.expiry.month", expiryMonth)
                .set("sourceOfFunds.provided.card.expiry.year", expiryYear);

        gateway.updateSession(sessionId, apiVersion, request, new GatewayCallback() {
            @Override
            public void onSuccess(GatewayMap response) {

                Log.e(CollectCardInfoActivity.class.getSimpleName(), "Successfully updated session");

                check3dsEnrollment();

            }

            @Override
            public void onError(Throwable throwable) {

                //    Log.e(CollectCardInfoActivity.class.getSimpleName(), throwable.getMessage(), throwable);

                //todo
                Log.e("ERROR", "updateSession Error");
                if(processCardDialog != null) {
                    processCardDialog.dismiss();
                }
                showErrorDialog();

            }
        });
    }

    void check3dsEnrollment() {

        // generate a random 3DSecureId for testing
        String threeDSId = UUID.randomUUID().toString();
        threeDSId = threeDSId.substring(0, threeDSId.indexOf('-'));

        apiController.check3DSecureEnrollment(sessionId, AMOUNT, CURRENCY, threeDSId, new ApiController.Check3DSecureEnrollmentCallback() {
            @Override
            public void onSuccess(GatewayMap response) {

                int apiVersionInt = Integer.valueOf(apiVersion);

                if(processCardDialog != null) {
                    processCardDialog.dismiss();
                }

                String threeDSecureId = (String) response.get("gatewayResponse.3DSecureId");

                String html = null;

                if (response.containsKey("gatewayResponse.3DSecure.authenticationRedirect.simple.htmlBodyContent")) {
                    html = (String) response.get("gatewayResponse.3DSecure.authenticationRedirect.simple.htmlBodyContent");
                }

                // for API versions <= 46, you must use the summary status field to determine next steps for 3DS
                if (apiVersionInt <= 46) {

                    String summaryStatus = (String) response.get("gatewayResponse.3DSecure.summaryStatus");

                    if ("CARD_ENROLLED".equalsIgnoreCase(summaryStatus)) {
                        Gateway.start3DSecureActivity(CollectCardInfoActivity.this, html);
                        return;
                    }

                    CollectCardInfoActivity.this.threeDSecureId = null;

                    // for these 2 cases, you still provide the 3DSecureId with the pay operation
                    if ("CARD_NOT_ENROLLED".equalsIgnoreCase(summaryStatus) || "AUTHENTICATION_NOT_AVAILABLE".equalsIgnoreCase(summaryStatus)) {
                        CollectCardInfoActivity.this.threeDSecureId = threeDSecureId;
                    }

                    // processPayment();
                }

                // for API versions >= 47, you must look to the gateway recommendation and the presence of 3DS info in the payload
                else {

                    String gatewayRecommendation = (String) response.get("gatewayResponse.response.gatewayRecommendation");

                    // if DO_NOT_PROCEED returned in recommendation, should stop transaction
                    if ("DO_NOT_PROCEED".equalsIgnoreCase(gatewayRecommendation)) {

                        // showResult(R.drawable.failed, R.string.pay_error_3ds_authentication_failed);
                        return;
                    }

                    // if PROCEED in recommendation, and we have HTML for 3ds, perform 3DS
                    if (html != null) {
                        Gateway.start3DSecureActivity(CollectCardInfoActivity.this, html);
                        return;
                    }

                    CollectCardInfoActivity.this.threeDSecureId = threeDSecureId;

                    //  processPayment();
                }
            }

            @Override
            public void onError(Throwable throwable) {

                Log.e("RESULTFAIL", "resultfail111");
                //todo
                if(processCardDialog != null) {
                    processCardDialog.dismiss();
                }
                showErrorDialog();

            }
        });
    }

    ProgressDialog progress1;

    void processPayment() {

        progress1 = new ProgressDialog(CollectCardInfoActivity.this, R.style.MyTheme);
        progress1.setCancelable(false);
        progress1.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        progress1.show();

        apiController.completeSession(sessionId, orderId, transactionId, AMOUNT, CURRENCY, threeDSecureId, isGooglePay, new ApiController.CompleteSessionCallback() {
            @Override
            public void onSuccess(String result) {

                Log.e("YourPayment", "Your Payment Successfull..!");
                Log.e("RESULT", result);

                if(result.equals("SUCCESS")) {

                    //After payment success

                    CallApiMethods.getAccessToken(new ApiServerCallBack() {
                        @Override
                        public void onSuccess(Boolean result) {
                            // do nothing
                        }

                        @Override
                        public void onSuccess(Boolean result, String token) {

                            if (result) {

                                updateBookingPayment(token, false);

                            } else {

                                if (progress1 != null) {
                                    progress1.dismiss();
                                }
                                Functions.showSnackbar(getCurrentFocus());
                            }

                        }
                    }, CollectCardInfoActivity.this);

                }else {

                    if (progress1 != null) {
                        progress1.dismiss();
                    }
                    if(processCardDialog != null) {
                        processCardDialog.dismiss();
                    }
                    showErrorDialog();

                    Log.e("PAYMENT RESULT", "processPayment NOT SUCCESS");
                }

            }

            @Override
            public void onError(Throwable throwable) {

                //todo
                if (progress1 != null) {
                    progress1.dismiss();
                }
                if(processCardDialog != null) {
                    processCardDialog.dismiss();
                }
                showErrorDialog();

                Log.e("ERROR", "processPayment ERROR");

                Log.e(CollectCardInfoActivity.class.getSimpleName(), throwable.getMessage(), throwable);

            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////
    public void updateBookingPayment(String accessToken, boolean secondAttempt) {

        CallApiMethods.updatePaymentSuccess(new ServerCallBack() {
            @Override
            public void onSuccess(Boolean result, JSONObject jOBJ) {

                try {

                    JSONObject getOBJ = jOBJ;

                    Log.e("UPDATE_B_P_R", ""+ getOBJ);

                    if (getOBJ.getBoolean("status")) {


                        ArrayList<String> foodItemArray = new ArrayList<>();
                        foodItemArray.clear();

                       JSONObject getReservationOBJ = getOBJ.getJSONObject("reservation");

                        reservationsData.setPaymentType(Integer.parseInt(getReservationOBJ.getString("payment_type")));
                        reservationsData.setStatus(getReservationOBJ.getString("status"));
                        reservationsData.setStringBookingId(getReservationOBJ.getString("string_booking_id"));
                        reservationsData.setAmount(getReservationOBJ.getString("amount"));

                        JSONArray getFoodItemArray = getReservationOBJ.getJSONArray("selectedFoodItems");

                        for(int a = 0; a < getFoodItemArray.length(); a++){

                            JSONObject getFoodOBJ = new JSONObject(getFoodItemArray.get(a).toString());

                            foodItemArray.add(getFoodOBJ.toString());
                        }

                        reservationsData.setFoodItem(foodItemArray);

                        Intent intent = new Intent(CollectCardInfoActivity.this, MyTickets.class);
                        intent.putExtra("reservationsOBJ", reservationsData);
                        startActivity(intent);

                        if(progress1 != null) {
                            progress1.dismiss();

                        }

                        startStop();
                        SeatPlan.getInstance().finish();
                        PromosAndConfessionaies.getInstance().finish();
                        ReservationSummary.getInstance().finish();
                        finish();

                    } else {


                        if(progress1 != null) {
                            progress1.dismiss();

                        }


                        showErrorDialog();


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
                                //do nothing
                            }

                            @Override
                            public void onSuccess(Boolean result, String token) {
                                if (result) {
                                    if (!secondAttempt) {

                                        updateBookingPayment(token, true);
                                    } else {
                                        Functions.showSnackbar(getCurrentFocus());
                                    }
                                } else {
                                    Functions.showSnackbar(getCurrentFocus());
                                }
                            }
                        }, CollectCardInfoActivity.this);


                    }

                } else if (error instanceof ServerError) {

                    Functions.showSnackbar(getCurrentFocus());

                } else if (error instanceof NetworkError) {

                    Functions.showSnackbar(getCurrentFocus());

                } else if (error instanceof ParseError) {

                    Functions.showSnackbar(getCurrentFocus());
                }
                if (errorDialog != null) {
                    errorDialog.dismiss();
                }
                finish();

            }

        }, CollectCardInfoActivity.this, accessToken, CallApiMethods.userKey, reservationsData.getReservationId());

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

                } else {
                    if(progress1 != null) {
                        progress1.dismiss();
                    }
                   // Functions.showSnackbar(getCurrentFocus());
                }


            }
        }, CollectCardInfoActivity.this);

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
                        }, CollectCardInfoActivity.this);


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
        }, CollectCardInfoActivity.this, accessToken, CallApiMethods.userKey, reservationsData.getReservationId());
    }

    //////////////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (Gateway.handle3DSecureResult(requestCode, resultCode, data, new Gateway3DSecureCallback() {
            @Override
            public void on3DSecureComplete(GatewayMap result) {


                Log.e("ONACTIVITYRESULKT", "=="+result);

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                int apiVersionInt = Integer.valueOf(apiVersion);

                if (apiVersionInt <= 46) {
                    if ("AUTHENTICATION_FAILED".equalsIgnoreCase((String) result.get("3DSecure.summaryStatus"))) {

                        /***--- chamith OTP Press Cancel button ---**/
                        if(progress1 != null) {
                            progress1.dismiss();
                        }
                        if(processCardDialog != null) {
                            processCardDialog.dismiss();
                        }
                        showErrorDialog();


                        return;
                    }
                } else { // version >= 47
                    if ("DO_NOT_PROCEED".equalsIgnoreCase((String) result.get("response.gatewayRecommendation"))) {

                        Log.e("ONACTIVITYRESULKT", "=DO NOT PROCEED 47===");

                        /***--- chamith OTP Press Cancel button ---**/
                        if(progress1 != null) {
                            progress1.dismiss();
                        }
                        if(processCardDialog != null) {
                            processCardDialog.dismiss();
                        }
                        showErrorDialog();

                        return;
                    }
                }

                /***--- Chamith Updated  ThreeDSecureId equal response id --**/

                String threeDSecureId = (String) result.get("3DSecureId");

                /***--  Chamith Updated --***/

                CollectCardInfoActivity.this.threeDSecureId = threeDSecureId;

                processPayment();


            }

            @Override
            public void on3DSecureCancel() {


                Log.e("ERROR", "on3DSecureCancel");
                /***--- chamith OTP Press Cancel button ---**/
                if(progress1 != null) {
                    progress1.dismiss();
                }
                if(processCardDialog != null) {
                    processCardDialog.dismiss();
                }
                showErrorDialog();
            }


        })) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }


    public void showAlertDialog(final Context context, String title, String message, String postBtnMsg, String negBtnMsg) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CollectCardInfoActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(postBtnMsg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cardNameTv.getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        hideKeyboard();
        return super.dispatchTouchEvent(ev);
    }

    public void showErrorDialog() {

        try {
            /***********   Open full screen error dialog *********/

            View view1 = getLayoutInflater().inflate(R.layout.dialog_choose_processing_card, null);
         //   errorDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
            errorDialog.setContentView(view1);
            errorDialog.setCanceledOnTouchOutside(false);
            errorDialog.show();

            Button btnCancel = view1.findViewById(R.id.cancelBtn);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getAccessTokenTocancelPayment();

                    SeatPlan.getInstance().finish();
                    PromosAndConfessionaies.getInstance().finish();
                    ReservationSummary.getInstance().finish();


                    if (errorDialog != null) {
                        errorDialog.dismiss();
                    }

                     finish();


                }
            });


            errorDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });
            errorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    errorDialog.dismiss();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }



//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if(errorDialog != null){
//            errorDialog.dismiss();
//        }
//    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        CollectCardInfoActivity.this.registerReceiver(exampleBroadcastReceiver, filter);

    }

    @Override
    public void onStop() {
        super.onStop();

        CollectCardInfoActivity.this.unregisterReceiver(exampleBroadcastReceiver);

    }

    private void showInternetDisconnectedView(boolean disconnected) {

        boolean isNetwork = Functions.isInternetAvailable(CollectCardInfoActivity.this);


        if (isNetwork) {

            Functions.dismisSnacbar();

        } else {

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

    public void showCloseDialod() {

        try {

            closeReservationDialog = new AlertDialog.Builder(CollectCardInfoActivity.this);

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

//                    textViewTime.setText("00:00");
//                    timerStatus = PromosAndConfessionaies.TimerStatus.STOPPED;

                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }

                    SeatPlan.getInstance().finish();
                    PromosAndConfessionaies.getInstance().finish();
                    ReservationSummary.getInstance().finish();
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


}
