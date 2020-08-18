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
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.MethodCallBack;
import lk.archmage.scopecinemas.Common.ServerCallBack;

public class Register extends AppCompatActivity {

    TextView btnsignIn;
    EditText txtFName, txtLName, txtEmail, txtMobile, txtDob, txtAddress, txtPassword, txtRePassword;
    Button registerBtn;
    ImageButton backBtn;
    RadioGroup radioGroupGender;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar calendar;
   // private DatePickerDialog dpl;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    int gender = 1;

    ConstraintLayout root;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //get firebase device id
        getDeviceToken(new MethodCallBack() {
            @Override
            public void onSuccess(Boolean result) {
                //todo
            }
        });

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        calendar = Calendar.getInstance();

        // notofication bar tranceparent
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

        root = findViewById(R.id.register_root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) Register.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(root.getWindowToken(), 0);

            }
        });


        txtFName = findViewById(R.id.txtFName);
        txtLName = findViewById(R.id.txtLName);
        txtEmail = findViewById(R.id.txtEmail);
        txtMobile = findViewById(R.id.txtMobile);
        txtDob = findViewById(R.id.txtDob);
        txtAddress = findViewById(R.id.txtAddress);
        txtPassword = findViewById(R.id.txtPassword);
        txtRePassword = findViewById(R.id.txtRePassword);

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


        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnsignIn = findViewById(R.id.btnSignIn);
        btnsignIn.setText(Html.fromHtml("<u>SIGN IN</u>"));
        btnsignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
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

        registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {

                    if (isValidEmail(txtEmail.getText())) {

                        txtEmail.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
                        txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));

                   if(checkMobileNumber()){

                    if (checkPasswordField()) {

                        if (checkSamePassword()) {

                        String mobileNumber[] = txtMobile.getText().toString().split("[)]");

                        Functions.showLoadingDialog(Register.this, "Please Wait...");

                        JSONObject registerObject = new JSONObject();

                        try {
                            registerObject.put("deviceId", token);
                            registerObject.put("firstName", txtFName.getText());
                            registerObject.put("lastName", txtLName.getText());
                            registerObject.put("email", txtEmail.getText());
                            registerObject.put("mobile", mobileNumber[1].trim());
                            registerObject.put("gender", gender);
                            registerObject.put("nic", "");
                            registerObject.put("dateOfBirth", txtDob.getText());
                            registerObject.put("address", txtAddress.getText());
                            registerObject.put("password", txtPassword.getText());
                            registerObject.put("user_key", "4c21bdc2-fb12-467b-98e0-612e2cececd8");
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
                                    handleLoginCallBack(token, registerObject);
                                }

                            }
                        }, Register.this);

                    }else {

                        Toast.makeText(Register.this, "Password does not match. Please enter reenter.", Toast.LENGTH_SHORT).show();
                    }

                    }else {

                        Toast.makeText(Register.this, "Please enter minimum 6 characters.", Toast.LENGTH_SHORT).show();
                    }

                    }else {

                        Toast.makeText(Register.this, "Phone number must be 8 digits.", Toast.LENGTH_SHORT).show();
                    }

                    }else {

                        txtEmail.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
                        txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
                        Toast.makeText(Register.this, "Please enter valid Email.", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(Register.this, "Please Enter Mandatory Fields !", Toast.LENGTH_SHORT).show();
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

                DatePickerDialog dialog = new DatePickerDialog(Register.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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

    }

    private void handleLoginCallBack(String token, JSONObject registerObject) {

        CallApiMethods.registerUser(new ServerCallBack() {
            @Override
            public void onSuccess(Boolean result, JSONObject jOBJ) {
                try {

                    if (jOBJ.getBoolean("status")) {

                        JSONObject userObj = jOBJ.getJSONObject("newUserRecord");

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
                        SharedPreferences appPreferences = Register.this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
                        SharedPreferences.Editor prEditor = appPreferences.edit();
                        prEditor.putString("user", userObj.toString());
                        prEditor.commit();

                        InputMethodManager imm = (InputMethodManager) Register.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(root.getWindowToken(), 0);

                        Intent homeIntent = new Intent(Register.this, MainActivity.class);
                        startActivity(homeIntent);

                        finish();

                    } else {
                        Toast.makeText(Register.this, jOBJ.getString("message"), Toast.LENGTH_LONG).show();
                    }


                    Functions.dismissLoadingDialog();


                } catch (JSONException e) {
                    Functions.dismissLoadingDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Boolean result, VolleyError error) {

                Toast.makeText(Register.this, "Invalid Username Or Password", Toast.LENGTH_LONG).show();
                Functions.dismissLoadingDialog();
            }
        }, Register.this, token, registerObject);
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


        if (txtEmail.getText().toString().equals("")) {
            isValid = false;
            txtEmail.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
        } else {
           // if (Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText()).matches()) {
                txtEmail.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
                txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));
//            } else {
//                isValid = false;
//                txtEmail.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
//                txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
//            }
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


        if (txtPassword.getText().toString().equals("")) {
            isValid = false;
            txtPassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            txtPassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
        } else {

//            if (txtPassword.getText().length() < 6) {
//                isValid = false;
//                txtPassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
//                txtPassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
//
//               // Toast.makeText(Register.this, "Please enter minimum 6 characters.", Toast.LENGTH_SHORT).show();
//            } else {
                txtPassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
                txtPassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));
           // }

        }

        if (txtRePassword.getText().toString().equals("")) {
            isValid = false;
            txtRePassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            txtRePassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
        } else {
//            if (txtRePassword.getText().length() < 6) {
//                isValid = false;
//                txtRePassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
//                txtRePassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
//            } else {
               // if (txtRePassword.getText().toString().equals(txtPassword.getText().toString())) {
                    txtRePassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
                    txtRePassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));
//
//                } else {
//                    isValid = false;
//                    txtRePassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
//                    txtRePassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
//                }
           // }
        }



        return isValid;
    }


    public boolean checkPasswordField(){

        boolean check = false;
        if(txtRePassword.getText().length() < 6 && txtPassword.getText().length() < 6){

            txtRePassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            txtRePassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));

            txtPassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            txtPassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            check = false;

          //  Toast.makeText(Register.this, "Please enter minimum 6 characters.", Toast.LENGTH_SHORT).show();
        }else {

            txtRePassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
            txtRePassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));

            txtPassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
            txtPassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));

            check = true;
        }

        return check;

    }

    public boolean checkSamePassword(){

        boolean check = false;

        if (txtRePassword.getText().toString().equals(txtPassword.getText().toString())) {
        txtRePassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
        txtRePassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));

            check = true;
                } else {
            check = false;
                    txtRePassword.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
                    txtRePassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
                }

        return check;

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

//    private void selectDate() {
//
//        final int date = calendar.get(Calendar.DATE);
//        int month = calendar.get(Calendar.MONTH);
//
//        int year = calendar.get(Calendar.YEAR);
//
//        dpl = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                txtDob.setText(year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth));
//            }
//        }, year, month, date);
//
//        dpl.show();
//    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    public static final String TAG = "FCM_SCOPE_CINEMA";
    private String token = "";

    public void getDeviceToken(final MethodCallBack methodCallBack) {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();
//
//                        SharedPreferences appPreferences = MainActivity.this.getSharedPreferences("DeviceId", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor prEditor = appPreferences.edit();
//
//                        prEditor.putString("deviceId", token);
//                        prEditor.commit();

                        // Log and toast
                        //  String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, token);
                        // Toast.makeText(Loggin.this, token, Toast.LENGTH_SHORT).show();

                        methodCallBack.onSuccess(true);
                    }
                });
    }


}

