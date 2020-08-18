package lk.archmage.scopecinemas;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.ServerCallBack;

public class ResetPassword extends AppCompatActivity {

    ImageButton backBtn;
    EditText txtEmail;
    Button continueBtn;

    ConstraintLayout root;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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
        window.setStatusBarColor(ContextCompat.getColor(ResetPassword.this,R.color.login_Bg_color));


        root = findViewById(R.id.reset_root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) ResetPassword.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(root.getWindowToken(), 0);

            }
        });


        txtEmail = findViewById(R.id.txtemail);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        continueBtn = findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {

                    JSONObject resetOnj = new JSONObject();

                    try {

                        resetOnj.put("emailAddress", txtEmail.getText().toString());
                        resetOnj.put("user_key", "4c21bdc2-fb12-467b-98e0-612e2cececd8");
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
                                handlePasswordResetCallBack(token, resetOnj);
                            }

                        }
                    }, ResetPassword.this);

                }

            }
        });


    }

    private void handlePasswordResetCallBack(String token, JSONObject resetOnj) {

        CallApiMethods.forgotPassword(new ServerCallBack() {
            @Override
            public void onSuccess(Boolean result, JSONObject jOBJ) {
                try {

                    finish();

                    Toast.makeText(ResetPassword.this, jOBJ.getString("message"), Toast.LENGTH_LONG).show();

                    Functions.dismissLoadingDialog();

                } catch (JSONException e) {
                    Functions.dismissLoadingDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Boolean result, VolleyError error) {
                Toast.makeText(ResetPassword.this, "Invalid Username Or Password", Toast.LENGTH_LONG).show();
                Functions.dismissLoadingDialog();

            }
        }, ResetPassword.this, token, resetOnj);
    }

    private boolean validateFields() {

        boolean isValid = true;

        if (txtEmail.getText().toString().equals("")) {
            isValid = false;
            txtEmail.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
            txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));

            Toast.makeText(ResetPassword.this, "Please Enter Mandatory Fields !", Toast.LENGTH_SHORT).show();

        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText()).matches()) {
                txtEmail.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#1b7399")));
                txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b7399")));

            } else {
                isValid = false;
                txtEmail.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));
                txtEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ed0b6d")));

                Toast.makeText(ResetPassword.this, "Invalid email !", Toast.LENGTH_SHORT).show();

            }
        }

        return isValid;
    }
}
