package lk.archmage.scopecinemas;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.ServerCallBack;

public class MyLoyalty extends AppCompatActivity {

    private String userId = "";
    private ImageButton closeBtn;

    TextView txtMembershipId, txtBalance, txtTotalEarned;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_loyalty);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        userId = getIntent().getStringExtra("user_id");

        closeBtn = findViewById(R.id.closeBtn);

        txtMembershipId = findViewById(R.id.txt_membership_id);
        txtBalance = findViewById(R.id.txt_balance);
        txtTotalEarned = findViewById(R.id.txt_totalEarned);


        // notofication bar tranceparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        loadUserData();

    }


    private void loadUserData() {

        Functions.showLoadingDialog(MyLoyalty.this, "Please Wait...");


        CallApiMethods.getAccessToken(new ApiServerCallBack() {
            @Override
            public void onSuccess(Boolean result) {
            }

            @Override
            public void onSuccess(Boolean result, String token) {

                if (result) {
                    handlMyLoyaltyHistoryCallBack(token);
                }
            }
        }, MyLoyalty.this);

    }

    private void handlMyLoyaltyHistoryCallBack(String token) {

        CallApiMethods.getMyLoyaltyHistory(new ServerCallBack() {
            @Override
            public void onSuccess(Boolean result, JSONObject jOBJ) {

                System.out.println("===== getMyLoyaltyHistory ====="+ jOBJ);

                try {

                    if (jOBJ.getBoolean("status")) {

                        try {

                            txtMembershipId.setText(jOBJ.getString("membership_id"));
                            txtBalance.setText(jOBJ.getString("available_balance"));
                            txtTotalEarned.setText(jOBJ.getString("totalEarned"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
//                        Toast.makeText(MyLoyalty.this, jOBJ.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
//                        finish();
                    }

                    Functions.dismissLoadingDialog();


                } catch (JSONException e) {
                    Functions.dismissLoadingDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Boolean result, VolleyError error) {
                Toast.makeText(MyLoyalty.this, "Invalid Username Or Password", Toast.LENGTH_LONG).show();
            }
        }, MyLoyalty.this, token, "4c21bdc2-fb12-467b-98e0-612e2cececd8", userId);

    }
}
