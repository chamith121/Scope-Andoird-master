package lk.archmage.scopecinemas;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lk.archmage.scopecinemas.Classes.MyBookingAdapter;
import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.ServerCallBack;
import lk.archmage.scopecinemas.Models.MyBookingData;


public class MyAccount extends Fragment {

    RecyclerView recyclerView;
    MyBookingAdapter adapter;
    TextView msgTv;
    Button editProfileBtn, myLoyaltyPointBtn;
    TextView username;

    JSONObject userData;

    ProgressDialog progressBooking;

    SharedPreferences appSharedPref;

    public MyAccount() {
    }

    ExampleBroadcastReceiver exampleBroadcastReceiver = new ExampleBroadcastReceiver();

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        msgTv = view.findViewById(R.id.msgTv);
        recyclerView = view.findViewById(R.id.recycler_viewPurchaseHis);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        /***--- Check User Sheare prefarances ---***/
        appSharedPref = getContext().getSharedPreferences("AppVersion", Context.MODE_PRIVATE);

        if (appSharedPref.getString("user", "").isEmpty()) {
            Toast.makeText(getContext(), "User Not Found", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            Intent loginIntent = new Intent(getContext(), Login.class);
            startActivity(loginIntent);
        } else {

            try {

                userData = new JSONObject(appSharedPref.getString("user", ""));

                System.out.println("==== USER DATA ====="+ userData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            username = view.findViewById(R.id.username);
            username.setText(userData.getString("firstName") + " " + userData.getString("lastName"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfileIntent = new Intent(getContext(), EditProfile.class);
                startActivity(editProfileIntent);
            }
        });

        myLoyaltyPointBtn = view.findViewById(R.id.myLoyaltyPointBtn);
        myLoyaltyPointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoyaltyProgramDialog();
            }
        });


        return view;
    }

    public void getMyBookingData(String accessToken, boolean secondAttempt) {

        try {

            progressBooking = new ProgressDialog(getContext(), R.style.MyTheme);
            progressBooking.setCancelable(false);
            progressBooking.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            progressBooking.show();

            String userId = userData.getString("id");

            System.out.println("+++++++++ USER ID +++++" + userId);

            CallApiMethods.getMyBookingInformation(new ServerCallBack() {
                @Override
                public void onSuccess(Boolean result, JSONObject jOBJ) {

                    try {

                        Functions.dismisSnacbar();

                        JSONObject getOBJ = jOBJ;

                        ArrayList<MyBookingData> myBookingArray = new ArrayList<>();
                        myBookingArray.clear();

                        if (getOBJ.getBoolean("status")) {

                            JSONArray getBookingsJArray = getOBJ.getJSONArray("bookings");

                            for (int i = 0; i < getBookingsJArray.length(); i++) {

                                JSONObject getOBJData = getBookingsJArray.getJSONObject(i);

                                ArrayList<String> typesArray = new ArrayList<>();
                                typesArray.clear();
                                ArrayList<String> foodItemArray = new ArrayList<>();
                                foodItemArray.clear();

                                /***--- Get Type JSON Array ---***/
                                JSONArray getTypesJA = getOBJData.getJSONArray("types");

                                for (int a = 0; a < getTypesJA.length(); a++) {

                                    JSONObject getTypeOBJ = getTypesJA.getJSONObject(a);
                                    typesArray.add(getTypeOBJ.toString());
                                }

                                JSONObject getFilmOBJ = getOBJData.getJSONObject("film");
                                String filmName = getFilmOBJ.getString("name");
                                String movieImage = getFilmOBJ.getString("image");
                                String mobileImage = getFilmOBJ.getString("mobile_image");
                                String bannerImage = getFilmOBJ.getString("banner_image");

                                JSONArray getFoodItemArray = getOBJData.getJSONArray("selectedFoodItems");

                                for (int a = 0; a < getFoodItemArray.length(); a++) {

                                    JSONObject getFoodOBJ = new JSONObject(getFoodItemArray.get(a).toString());

                                    foodItemArray.add(getFoodOBJ.toString());
                                }

                                JSONObject getLocationOBJ = getOBJData.getJSONObject("location");
                                String locationName = getLocationOBJ.getString("name");
                                String locationViCode = getLocationOBJ.getString("vista_code");


                                MyBookingData bookingData = new MyBookingData(
                                        getOBJData.getInt("createdAt"),
                                        getOBJData.getInt("updatedAt"),
                                        getOBJData.getString("id"),
                                        getOBJData.getString("name"),
                                        getOBJData.getString("email"),
                                        getOBJData.getString("mobile"),
                                        getOBJData.getString("description"),
                                        getOBJData.getInt("method"),
                                        getOBJData.getInt("payment_type"),
                                        getOBJData.getString("sales_channel"),
                                        getOBJData.getInt("status"),
                                        getOBJData.getString("transaction_id"),
                                        getOBJData.getInt("booking_id"),
                                        getOBJData.getString("string_booking_id"),
                                        getOBJData.getString("cinema_id"),
                                        getOBJData.getInt("session_id"),
                                        getOBJData.getInt("totalTickets"),
                                        getOBJData.getString("amount"),
                                        getOBJData.getString("ticketAmount"),
                                        getOBJData.getString("foodAmount"),
                                        typesArray,
                                        getOBJData.getString("selected_seats"),
                                        filmName,
                                        foodItemArray,
                                        locationName,
                                        locationViCode,
                                        getOBJData.getString("screen_num"),
                                        getOBJData.getString("screen_name"),
                                        getOBJData.getString("date"),
                                        getOBJData.getString("date_timestamp"),
                                        getOBJData.getString("time"),
                                        getOBJData.getString("show_time"),
                                        getOBJData.getString("successIndicator"),
                                        getOBJData.getString("session_version"),
                                        getOBJData.getString("experince"),
                                        getOBJData.getString("staff"),
                                        getOBJData.getString("user"),
                                        movieImage,
                                        mobileImage,
                                        bannerImage,
                                        getOBJData.getBoolean("active")
                                );

                                myBookingArray.add(bookingData);

                            }

                            if (myBookingArray.size() != 0) {

                                msgTv.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                adapter = new MyBookingAdapter(getContext(), myBookingArray);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            } else {

                                msgTv.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }

                            if (progressBooking != null) {
                                progressBooking.dismiss();
                            }

                            System.out.println("=======TRUE");


                        } else {

                            msgTv.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            if (progressBooking != null) {
                                progressBooking.dismiss();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Boolean result, VolleyError error) {

                    if (progressBooking != null) {
                        progressBooking.dismiss();
                    }

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                        Functions.showSnackbar(getView());

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

                                            getMyBookingData(token, true);
                                        } else {

                                            Functions.showSnackbar(getView());
                                        }
                                    } else {

                                        Functions.showSnackbar(getView());
                                    }
                                }
                            }, getContext());

                        }

                    } else if (error instanceof ServerError) {

                        Functions.showSnackbar(getView());

                    } else if (error instanceof NetworkError) {

                        Functions.showSnackbar(getView());

                    } else if (error instanceof ParseError) {

                        Functions.showSnackbar(getView());
                    }
                }
            }, getContext(), accessToken, CallApiMethods.userKey, userId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(exampleBroadcastReceiver, filter);

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(exampleBroadcastReceiver);

    }

    private void showInternetDisconnectedView(boolean disconnected) {

        boolean isNetwork = Functions.isInternetAvailable(getContext());


        if (isNetwork) {

            Functions.dismisSnacbar();

            CallApiMethods.getAccessToken(new ApiServerCallBack() {
                @Override
                public void onSuccess(Boolean result) {
                    // do nothing
                }

                @Override
                public void onSuccess(Boolean result, String token) {

                    if (result) {

                        getMyBookingData(token, false);

                    } else {

                        View view = getActivity().findViewById(android.R.id.content);
                        Functions.showSnackbar(view);
                    }
                }
            }, getContext());


        } else {

            View view = getActivity().findViewById(android.R.id.content);
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


    @Override
    public void onResume() {


        try {
            userData = new JSONObject(appSharedPref.getString("user", ""));
            username.setText(userData.getString("firstName") + " " + userData.getString("lastName"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.onResume();
    }

    public void showLoyaltyProgramDialog() {

        Intent loyaltyIntent = null;

        try {

            if (userData.getString("membership_id").equals("")) {

                loyaltyIntent = new Intent(getContext(), ActivateLoyalty.class);
                startActivity(loyaltyIntent);

            } else {

                loyaltyIntent = new Intent(getContext(), MyLoyalty.class);
                loyaltyIntent.putExtra("user_id", userData.getString("id"));
                startActivity(loyaltyIntent);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
