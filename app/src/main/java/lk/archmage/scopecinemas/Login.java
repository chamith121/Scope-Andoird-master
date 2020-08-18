package lk.archmage.scopecinemas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.MethodCallBack;
import lk.archmage.scopecinemas.Common.ServerCallBack;

public class Login extends AppCompatActivity implements View.OnClickListener {

    TextView btnRegister, forgotBtn, passwordMsg, emaildMsg;
    EditText txtEmail, txtPassword;
    Button loginBtn, fbLoginBtn;

    private CallbackManager mCallbackManager;

    private final String FACEBOOK_TAG = "FACEBOOK_TAG";

    ConstraintLayout root;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //get firebase device id
        getDeviceToken(new MethodCallBack() {
            @Override
            public void onSuccess(Boolean result) {
                //todo
            }
        });

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "cinema.com.scope",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {
        }
        catch (NoSuchAlgorithmException e) {
        }






        // notofication bar tranceparent
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(Login.this,R.color.login_Bg_color));


        root = findViewById(R.id.login_root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) Login.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(root.getWindowToken(), 0);

            }
        });


//        Initialize fb
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();


        passwordMsg = findViewById(R.id.passwordMsg);
        emaildMsg = findViewById(R.id.emailMsg);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(Login.this);

        fbLoginBtn = findViewById(R.id.fbLoginBtn);
        fbLoginBtn.setOnClickListener(this);


        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setText(Html.fromHtml("<u>REGISTER</u>"));
        btnRegister.setOnClickListener(Login.this);


        forgotBtn = findViewById(R.id.forgotBtn);
        forgotBtn.setOnClickListener(Login.this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.forgotBtn:
                Intent intent = new Intent(Login.this, ResetPassword.class);
                startActivity(intent);
                break;
            case R.id.loginBtn:
                callLogin();
                break;

            case R.id.fbLoginBtn:
                loginFacebook();
                break;
            case R.id.btnRegister:
                Intent intentRegister = new Intent(Login.this, Register.class);
                startActivity(intentRegister);
                break;
        }

    }


    private void callLogin() {
        if (passwordMsg.getVisibility() == View.VISIBLE) {
            passwordMsg.setVisibility(View.GONE);
        }

        if (emaildMsg.getVisibility() == View.VISIBLE) {
            emaildMsg.setVisibility(View.GONE);
        }

        boolean isValid = true;

        if (txtEmail.getText().toString().equals("")) {
            isValid = false;
            emaildMsg.setText("Email Empty");
            emaildMsg.setVisibility(View.VISIBLE);
            txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
        } else {

            if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText()).matches()) {
                isValid = false;
                emaildMsg.setText("Email Invalid");
                emaildMsg.setVisibility(View.VISIBLE);
                txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            } else {
                emaildMsg.setVisibility(View.GONE);
                txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));
            }

        }

        if (txtPassword.getText().toString().equals("")) {
            isValid = false;
            passwordMsg.setText("Password Empty");
            passwordMsg.setVisibility(View.VISIBLE);
            txtPassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
        } else {
            passwordMsg.setVisibility(View.GONE);
            txtPassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));

//                    if (txtPassword.getText().length() < 6) {
//                        isValid = false;
//                        passwordMsg.setText("Password Invalid");
//                        passwordMsg.setVisibility(View.VISIBLE);
//                        txtPassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
//                    } else {
//                        isValid = true;
//                        passwordMsg.setVisibility(View.GONE);
//                        txtPassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));
//                    }
        }


        if (isValid) {
            Functions.showLoadingDialog(Login.this, "Please Wait...");

            JSONObject loginObj = new JSONObject();

            try {

                loginObj.put("deviceId", token);
                loginObj.put("emailAddress", txtEmail.getText().toString());
                loginObj.put("password", txtPassword.getText().toString());
                loginObj.put("user_key", "4c21bdc2-fb12-467b-98e0-612e2cececd8");

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
                        handleLoginCallBack(token, loginObj);
                    }

                }
            }, Login.this);
        } else {
            Toast.makeText(Login.this, "Please Enter Mandatory Fields !", Toast.LENGTH_SHORT).show();
        }
//
//


    }

    private void handleLoginCallBack(String token, JSONObject loginObj) {

        CallApiMethods.signIn(new ServerCallBack() {
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
                        SharedPreferences appPreferences = Login.this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
                        SharedPreferences.Editor prEditor = appPreferences.edit();
                        prEditor.putString("user", userObj.toString());
                        //  prEditor.putBoolean("is_loggedin", true);
                        prEditor.commit();



                        Login.this.finish();
                        Intent homeIntent = new Intent(Login.this, MainActivity.class);
                        startActivity(homeIntent);

                    } else {

                        passwordMsg.setVisibility(View.VISIBLE);
                        passwordMsg.setText(jOBJ.getString("message"));

                    }

                    Functions.dismissLoadingDialog();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Boolean result, VolleyError error) {

                passwordMsg.setVisibility(View.VISIBLE);
                passwordMsg.setText("Invalid Username Or Password");

                Functions.dismissLoadingDialog();

            }
        }, Login.this, token, loginObj);
    }

    private void loginFacebook() {

        LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                if (isLoggedIn) {
                    getUserDataFromFB(accessToken);
                }
            }

            @Override
            public void onCancel() {
                Log.d(FACEBOOK_TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(FACEBOOK_TAG, "facebook:onError", error);
                // ...
            }
        });
    }


    private void getUserDataFromFB(AccessToken accessToken) {

        Functions.showLoadingDialog(Login.this, "Please wait...");

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject json, GraphResponse response) {

//                // Application code
                if (json != null) {

                    JSONObject loginObj = new JSONObject();

                    try {
                        loginObj.put("deviceId", token);
                        loginObj.put("firstName", json.getString("first_name"));
                        loginObj.put("lastName", json.getString("last_name"));
                        loginObj.put("emailAddress", json.getString("email"));
                        loginObj.put("facebookId", json.getString("id"));
                        loginObj.put("user_key", "4c21bdc2-fb12-467b-98e0-612e2cececd8");

                    } catch (JSONException e) {

                        if(e.getMessage().equals("No value for email")){

                            showAlertDialog(Login.this, "", "Sharing Email is mandatory for sign in to this app", "Ok", "");
                        }
                        e.printStackTrace();
                    }

                    CallApiMethods.getAccessToken(new ApiServerCallBack() {
                        @Override
                        public void onSuccess(Boolean result) {
                        }

                        @Override
                        public void onSuccess(Boolean result, String token) {
                            if (result) {
                                handleFacebookSignInCallBack(token, loginObj);
                            }
                        }
                    }, Login.this);

                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void handleFacebookSignInCallBack(String token, JSONObject loginObj) {

        CallApiMethods.facebookSignin(new ServerCallBack() {
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
                        SharedPreferences appPreferences = Login.this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
                        SharedPreferences.Editor prEditor = appPreferences.edit();
                        prEditor.putString("user", userObj.toString());
                        prEditor.putString("login_type", "fb");
                        prEditor.commit();


                        Functions.dismissLoadingDialog();


                        Intent homeIntent = new Intent(Login.this, MainActivity.class);
                        startActivity(homeIntent);

                    } else {
                        passwordMsg.setVisibility(View.VISIBLE);
                        passwordMsg.setText(jOBJ.getString("message"));
                    }

                    Functions.dismissLoadingDialog();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Boolean result, VolleyError error) {

                Functions.dismissLoadingDialog();

                passwordMsg.setVisibility(View.VISIBLE);
                passwordMsg.setText("Invalid Username Or Password");

            }
        }, Login.this, token, loginObj);
    }



    public void showAlertDialog(final Context context, String title, String message, String postBtnMsg, String negBtnMsg) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(postBtnMsg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.show();
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtPassword.getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        hideKeyboard();
        return super.dispatchTouchEvent(ev);


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



