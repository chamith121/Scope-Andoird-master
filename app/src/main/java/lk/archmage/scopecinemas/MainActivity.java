package lk.archmage.scopecinemas;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import lk.archmage.scopecinemas.Common.MethodCallBack;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView categoryTv, virsionTv;

    LinearLayout nav_moviesLayout, nav_theatersLayout, nav_DealsLayout, navTermsAndConditions,
            nav_about, nav_signOut, nav_my_loyaltyProgram, nav_upcomingLayout, nav_myAccountLayout;

    JSONObject userData;
    SharedPreferences appSharedPref;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        categoryTv = toolbar.findViewById(R.id.categoryTv);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        /*  ******************* Build Version  ****************/

        String getBuildVersion = BuildConfig.VERSION_NAME;

        virsionTv = findViewById(R.id.virsionTv);
        virsionTv.setText("Version " + getBuildVersion);

        /***--- Check User Sheare prefarances ---***/
        appSharedPref = this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
        if (appSharedPref.getString("user", "").isEmpty()) {
            MainActivity.this.finish();
            startActivity(new Intent(MainActivity.this, Login.class));
        } else {
            try {
                loadFragment(new Cinema());
                userData = new JSONObject(appSharedPref.getString("user", ""));
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        nav_moviesLayout = findViewById(R.id.nav_moviesLayout);
        nav_moviesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                categoryTv.setText("MOVIES");
                loadFragment(new Cinema());
                drawerOpenClose();
            }
        });

        nav_theatersLayout = findViewById(R.id.nav_theatersLayout);
        nav_theatersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                categoryTv.setText("THEATERS");
                loadFragment(new Theaters());
                drawerOpenClose();
            }
        });

        nav_upcomingLayout = findViewById(R.id.nav_upcomingLayout);
        nav_upcomingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                categoryTv.setText("MY ACCOUNTN");
                loadFragment(new MyAccount());
                drawerOpenClose();
            }
        });

        nav_myAccountLayout = findViewById(R.id.nav_myAccountLayout);
        nav_myAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                categoryTv.setText("MY ACCOUNT");
                loadFragment(new MyAccount());
                drawerOpenClose();
            }
        });

        nav_DealsLayout = findViewById(R.id.nav_DealsLayout);
        nav_DealsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                categoryTv.setText("OFFERS");
                loadFragment(new Offers());
                drawerOpenClose();
            }
        });

        navTermsAndConditions = findViewById(R.id.navTermsAndConditions);
        navTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                categoryTv.setText("TERMS &amp; CONDITIONS");
                loadFragment(new TermsAndConditions());
                drawerOpenClose();
            }
        });

        nav_about = findViewById(R.id.nav_about);
        nav_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                categoryTv.setText("ABOUT");
                loadFragment(new About());
                drawerOpenClose();
            }
        });

        nav_signOut = findViewById(R.id.nav_signOut);
        nav_signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLogOutDialog();
            }
        });

        nav_my_loyaltyProgram = findViewById(R.id.nav_my_loyaltyProgram);
        nav_my_loyaltyProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoyaltyProgramDialog();
            }
        });


    }

    public void showLoyaltyProgramDialog() {

        Intent loyaltyIntent = null;

        try {

            if (userData.getString("membership_id").equals("")) {

                loyaltyIntent = new Intent(MainActivity.this, ActivateLoyalty.class);
                startActivity(loyaltyIntent);

            } else {

                loyaltyIntent = new Intent(MainActivity.this, MyLoyalty.class);
                loyaltyIntent.putExtra("user_id", userData.getString("id"));
                startActivity(loyaltyIntent);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (doubleBackToExitPressedOnce) {

                System.exit(0);

                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void drawerOpenClose() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void showLogOutDialog() {
        Dialog logOutDialog = new Dialog(MainActivity.this);
        logOutDialog.setContentView(R.layout.dialog_log_out);

        logOutDialog.setCanceledOnTouchOutside(false);
        logOutDialog.show();

        Button btnYes = logOutDialog.findViewById(R.id.yesBtn);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // added by amal
                SharedPreferences appPreferences = MainActivity.this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
                // remove user in SharedPreferences

                String loginType = appPreferences.getString("login_type", "");

                if (loginType.equals("fb")) {
                    LoginManager.getInstance().logOut();
                }

                SharedPreferences.Editor prEditor = appPreferences.edit();
                prEditor.remove("user");
                prEditor.remove("login_type");
                prEditor.commit();

                logOutDialog.dismiss();

//                Intent intentLogin = new Intent(MainActivity.this, Login.class);
//                startActivity(intentLogin);
                MainActivity.this.finish();


            }
        });

        Button btnCancel = logOutDialog.findViewById(R.id.noBtn);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logOutDialog.dismiss();
            }
        });

        logOutDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                logOutDialog.dismiss();
            }
        });

        logOutDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (appSharedPref != null) {
                userData = new JSONObject(appSharedPref.getString("user", ""));
            } else {
                appSharedPref = this.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);
                userData = new JSONObject(appSharedPref.getString("user", ""));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
