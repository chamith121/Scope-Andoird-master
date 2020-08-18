package lk.archmage.scopecinemas;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
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

public class EditProfile extends AppCompatActivity {

    Button updateBtn;
    EditText txtFName, txtLName, txtMobile, txtDob, txtAddress;
    RadioGroup radioGroupGender;

    Calendar calendar;
    //private DatePickerDialog dpl;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    int gender = 0;
    private String userId = "";

    ConstraintLayout root;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        SharedPreferences appSharedPrefa = this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
        try {
            JSONObject jo = new JSONObject(appSharedPrefa.getString("user", ""));

            userId = jo.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        root = findViewById(R.id.edit_root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) EditProfile.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(root.getWindowToken(), 0);

            }
        });


        calendar = Calendar.getInstance();

        txtFName = findViewById(R.id.txtFName);
        txtLName = findViewById(R.id.txtLName);
        txtMobile = findViewById(R.id.txtMobile);
        txtDob = findViewById(R.id.txtDob);
        txtAddress = findViewById(R.id.txtAddress);

        radioGroupGender = findViewById(R.id.radioGroupGender);


        txtMobile.setText("( 07 ) ");
        Selection.setSelection(txtMobile.getText(), txtMobile.getText().length());


        txtMobile.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
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
                } else {
                    gender = 0;
                }
            }
        });

        updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {

                    if(checkMobileNumber()) {

                    Functions.showLoadingDialog(EditProfile.this, "Please Wait...");

                    String mobileNumber[] = txtMobile.getText().toString().split("[)]");


                    JSONObject userEditObject = new JSONObject();

                    try {

                        userEditObject.put("id", userId);
                        userEditObject.put("firstName", txtFName.getText());
                        userEditObject.put("lastName", txtLName.getText());
                        userEditObject.put("mobile", mobileNumber[1].trim());
                        userEditObject.put("gender", gender);
                        userEditObject.put("dateOfBirth", txtDob.getText());
                        userEditObject.put("address", txtAddress.getText());
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
                                handleUserUpdateCallBack(token, userEditObject);
                            }
                        }
                    }, EditProfile.this);

                    }else {

                        Toast.makeText(EditProfile.this, "Phone number must be 8 digits.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(EditProfile.this, "Please Enter Mandatory Fields !", Toast.LENGTH_SHORT).show();
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

                DatePickerDialog dialog = new DatePickerDialog(EditProfile.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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

    private void handleUserUpdateCallBack(String token, JSONObject registerObject) {

        CallApiMethods.updateUser(new ServerCallBack() {
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
                        SharedPreferences appPreferences = EditProfile.this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
                        SharedPreferences.Editor prEditor = appPreferences.edit();
                        prEditor.putString("user", userObj.toString());
                        prEditor.commit();

                        finish();

                    } else {
                        Toast.makeText(EditProfile.this, jOBJ.getString("message"), Toast.LENGTH_LONG).show();
                    }

                    Functions.dismissLoadingDialog();


                } catch (JSONException e) {
                    Functions.dismissLoadingDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Boolean result, VolleyError error) {
                Toast.makeText(EditProfile.this, "Please Mandatory Fields !", Toast.LENGTH_LONG).show();
                Functions.dismissLoadingDialog();

            }
        }, EditProfile.this, token, registerObject);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


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
            txtDob.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            txtDob.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        }


        if (txtAddress.getText().toString().equals("")) {
            isValid = false;
            txtAddress.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            txtAddress.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
        } else {
            txtAddress.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
            txtAddress.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));
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

        Functions.showLoadingDialog(EditProfile.this, "Please Wait...");


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
        }, EditProfile.this);

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
                        SharedPreferences appPreferences = EditProfile.this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
                        SharedPreferences.Editor prEditor = appPreferences.edit();
                        prEditor.putString("user", userObj.toString());
                        prEditor.commit();

                        try {

                            txtFName.setText(userObj.getString("firstName"));
                            txtLName.setText(userObj.getString("lastName"));

                            txtMobile.setText(txtMobile.getText()+userObj.getString("mobile"));

                            txtDob.setText(userObj.getString("dateOfBirth"));
                            txtAddress.setText(userObj.getString("address"));

                            if (userObj.getString("gender").toLowerCase().equals("male")) {
                                radioGroupGender.check(R.id.redioMale);
                            } else if (userObj.getString("gender").toLowerCase().equals("female")) {
                                radioGroupGender.check(R.id.radiofemale);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        Toast.makeText(EditProfile.this, jOBJ.getString("message"), Toast.LENGTH_LONG).show();
                    }

                    Functions.dismissLoadingDialog();


                } catch (JSONException e) {
                    Functions.dismissLoadingDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Boolean result, VolleyError error) {
                Toast.makeText(EditProfile.this, "Invalid Username Or Password", Toast.LENGTH_LONG).show();
            }
        }, EditProfile.this, token, "4c21bdc2-fb12-467b-98e0-612e2cececd8", userId);

    }


//    private void selectDate() {
//
//        final int date = calendar.get(Calendar.DATE);
//        int month = calendar.get(Calendar.MONTH);
//
//        int year = calendar.get(Calendar.YEAR);
//
//        dpl = new DatePickerDialog(EditProfile.this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                txtDob.setText(year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth));
//            }
//        }, year, month, date);
//
//        dpl.show();
//    }


}
