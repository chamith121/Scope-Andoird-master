package lk.archmage.scopecinemas;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.ServerCallBack;

public class ActivateLoyalty extends AppCompatActivity {

    Button confirm;
    ImageButton closeBtn;
    EditText txtFName, txtLName, txtMobile, txtDob;
    RadioGroup radioGroupGender;
    TextView redioLable;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar calendar;
    //private DatePickerDialog dpl;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private JSONObject result;

    String userId = "";
    int gender = 0;
    private JSONObject userObj;
    SharedPreferences appPreferences;

    ConstraintLayout root;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_loyalty);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // notofication bar tranceparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // get user id
        appPreferences = this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
        try {
            userObj = new JSONObject(appPreferences.getString("user", ""));
            userId = userObj.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        root = findViewById(R.id.loyalty_root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) ActivateLoyalty.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(root.getWindowToken(), 0);

            }
        });


        calendar = Calendar.getInstance();

        redioLable = findViewById(R.id.redioLable);

        confirm = findViewById(R.id.confirmBtn);
        closeBtn = findViewById(R.id.closeBtn);

        txtFName = findViewById(R.id.txtFName);
        txtLName = findViewById(R.id.txtLName);
        txtMobile = findViewById(R.id.txtMobile);
        txtDob = findViewById(R.id.txtDob);
       // txtEmail = findViewById(R.id.txtEmail);

        radioGroupGender = findViewById(R.id.radioGroupGender);


        txtMobile.setText("( 07 ) ");
        Selection.setSelection(txtMobile.getText(), txtMobile.getText().length());

        txtMobile.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("( 07 ) ")) {
                    txtMobile.setText("( 07 ) ");
                    Selection.setSelection(txtMobile.getText(), txtMobile.getText().length());
                }
            }
        });

        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (findViewById(checkedId).getTag().toString().toLowerCase().equals("male")) {
                    gender = 1;
                }
            }
        });


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (validateFields()) {

                    if(checkMobileNumber()) {

                        Functions.showLoadingDialog(ActivateLoyalty.this, "Please Wait...");

                        String mobileNumber[] = txtMobile.getText().toString().split("[)]");

                        JSONObject userEditObject = new JSONObject();

                        try {
                            userEditObject.put("user_id", userId);
                            userEditObject.put("first_name", txtFName.getText());
                            userEditObject.put("last_name", txtLName.getText());
                            userEditObject.put("email", userObj.getString("emailAddress"));
                            userEditObject.put("birth_day", txtDob.getText());
                            userEditObject.put("gender", gender);
                            userEditObject.put("mobile", mobileNumber[1].trim());
                            userEditObject.put("user_key", "4c21bdc2-fb12-467b-98e0-612e2cececd8");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        CallApiMethods.getAccessToken(new ApiServerCallBack() {
                            @Override
                            public void onSuccess(Boolean result) {
                            }

                            @Override
                            public void onSuccess(Boolean result, String token) {

                                if (result) {
                                    handleActivateLoyaltyCallBack(token, userEditObject);
                                }

                            }
                        }, ActivateLoyalty.this);

                    }else {

                        Toast.makeText(ActivateLoyalty.this, "Phone number must be 8 digits.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(ActivateLoyalty.this, "Please Enter Mandatory Fields !", Toast.LENGTH_SHORT).show();
                }
            }
        });


        txtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //selectDate();

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ActivateLoyalty.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // month = month + 1;

                txtDob.setText(year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth));
            }
        };

        loadUserData();

    }

    private void handleActivateLoyaltyCallBack(String token, JSONObject userEditObject) {

        CallApiMethods.activateLoyalty(new ServerCallBack() {
            @Override
            public void onSuccess(Boolean result, JSONObject jOBJ) {
                try {

                    Intent loyaltyIntent = null;

                    System.out.println("******* Activate Loyality  ******"+ jOBJ);

                    if (jOBJ.getBoolean("status")) {

                        userObj.put("membership_id", jOBJ.getString("membership_id"));
                        userObj.put("card_no", jOBJ.getString("card_number"));

                        SharedPreferences.Editor prEditor = appPreferences.edit();
                        prEditor.putString("user", userObj.toString());
                        prEditor.commit();

                        loyaltyIntent = new Intent(ActivateLoyalty.this, MyLoyalty.class);
                        loyaltyIntent.putExtra("user_id", userId);
                        startActivity(loyaltyIntent);
                        finish();

                    } else {
                        Toast.makeText(ActivateLoyalty.this, jOBJ.getString("error"), Toast.LENGTH_LONG).show();

//                        loyaltyIntent = new Intent(ActivateLoyalty.this, ActivateLoyalty.class);
//                        startActivity(loyaltyIntent);
                    }

                    Functions.dismissLoadingDialog();


                } catch (JSONException e) {
                    Functions.dismissLoadingDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Boolean result, VolleyError error) {
                Toast.makeText(ActivateLoyalty.this, "Invalid Username Or Password", Toast.LENGTH_LONG).show();
                Functions.dismissLoadingDialog();

            }
        }, ActivateLoyalty.this, token, userEditObject);

    }

//    private void selectDate() {
//
//        final int date = calendar.get(Calendar.DATE);
//        int month = calendar.get(Calendar.MONTH);
//
//        int year = calendar.get(Calendar.YEAR);
//
//        dpl = new DatePickerDialog(ActivateLoyalty.this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                txtDob.setText(year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth));
//            }
//        }, year, month, date);
//
//        dpl.show();
//    }

    private boolean validateFields() {

        boolean isValid = true;

        if (txtFName.getText().toString().equals("")) {
            isValid = false;
            txtFName.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            txtFName.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
        } else {
            txtFName.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
            txtFName.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));
        }

        if (txtLName.getText().toString().equals("")) {
            isValid = false;
            txtLName.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            txtLName.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
        } else {
            txtLName.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
            txtLName.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));
        }

        if (txtMobile.getText().toString().equals("")) {
            isValid = false;
            txtMobile.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            txtMobile.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
        } else {
            txtMobile.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
            txtMobile.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));
        }


        if (txtDob.getText().toString().equals("")) {
            isValid = false;
            txtDob.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            txtDob.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
        } else {
            txtDob.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
            txtDob.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));
        }

//
//        if (txtEmail.getText().toString().equals("")) {
//            isValid = false;
//            txtEmail.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
//            txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
//        } else {
//            txtEmail.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
//            txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));
//        }

        if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            isValid = false;
            redioLable.setTextColor(Color.parseColor("#ed0b6d"));
        } else {
            redioLable.setTextColor(Color.parseColor("#1b7399"));
        }


        return isValid;
    }

    public boolean checkMobileNumber(){

        boolean check = false;

        if(txtMobile.getText().length() != 15){
            txtMobile.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            txtMobile.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            check = false;
        } else {
            txtMobile.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
            txtMobile.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));

            check = true;
        }
        return check;
    }

    private void loadUserData() {

        Functions.showLoadingDialog(ActivateLoyalty.this, "Please Wait...");


        CallApiMethods.getAccessToken(new ApiServerCallBack() {
            @Override
            public void onSuccess(Boolean result) {
            }

            @Override
            public void onSuccess(Boolean result, String token) {

                if (result) {
                    handleUserDataCallBack(token);
                }
            }
        }, ActivateLoyalty.this);


    }

    private void handleUserDataCallBack(String token) {

        CallApiMethods.getUserInfo(new ServerCallBack() {
            @Override
            public void onSuccess(Boolean result, JSONObject jOBJ) {


                try {

                    if (jOBJ.getBoolean("status")) {

                        JSONObject userObj = jOBJ.getJSONObject("user");

                        // remove unwanted attributs from User object in jOBJ
                        userObj.remove("createdAt");
                        userObj.remove("updatedAt");
                        userObj.remove("emailProofToken");
                        userObj.remove("emailProofTokenExpiresAt");
                        userObj.remove("stripeCustomerId");
                        userObj.remove("hasBillingCard");
                        userObj.remove("billingCardBrand");
                        userObj.remove("billingCardLast4");
                        userObj.remove("billingCardExpMonth");
                        userObj.remove("billingCardExpYear");
                        userObj.remove("tosAcceptedByIp");
                        userObj.remove("lastSeenAt");

                        // save user in SharedPreferences
                        SharedPreferences appPreferences = ActivateLoyalty.this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
                        SharedPreferences.Editor prEditor = appPreferences.edit();
                        prEditor.putString("user", userObj.toString());
                        prEditor.commit();

                        //txtEmail.setText(userObj.getString("emailAddress"));

                        try {

                            txtFName.setText(userObj.getString("firstName"));
                            txtLName.setText(userObj.getString("lastName"));
                            txtMobile.setText(txtMobile.getText() + userObj.getString("mobile"));
                            txtDob.setText(userObj.getString("dateOfBirth"));


                            if (userObj.getString("gender").toLowerCase().equals("male")) {
                                radioGroupGender.check(R.id.redioMale);
                            } else if (userObj.getString("gender").toLowerCase().equals("female")) {
                                radioGroupGender.check(R.id.radiofemale);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        Toast.makeText(ActivateLoyalty.this, jOBJ.getString("message"), Toast.LENGTH_LONG).show();
                    }


                    Functions.dismissLoadingDialog();


                } catch (JSONException e) {
                    Functions.dismissLoadingDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Boolean result, VolleyError error) {
                Toast.makeText(ActivateLoyalty.this, "Invalid Username Or Password", Toast.LENGTH_LONG).show();
            }
        }, ActivateLoyalty.this, token, "4c21bdc2-fb12-467b-98e0-612e2cececd8", userId);


//        if (result != null) {
////            I/System.out: {"id":"5e679b4e0a3d300016aafb56","facebookId":"","emailAddress":"nirosha@archmage.lk","dateOfBirth":"1984-09-02","mobile":772907480,"emailStatus":"confirmed","emailChangeCandidate":"","firstName":"Nirosha","lastName":"Kodituwakku","gender":"male","nic":"123456784V","membership_id":"SCOPEKTW5F4T272","card_no":"772907480","address":"No 1, Flower Road, Colombo","isSuperAdmin":false}
//


    }


}
