package lk.archmage.scopecinemas;


import android.app.Activity;
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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lk.archmage.scopecinemas.Classes.TheaterAdapter;
import lk.archmage.scopecinemas.Common.ApiServerCallBack;
import lk.archmage.scopecinemas.Common.CallApiMethods;
import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.Common.ServerCallBack;
import lk.archmage.scopecinemas.Models.TheaterData;


public class Theaters extends Fragment {

    TextView msgTv;
    private RecyclerView recyclerView;
    TheaterAdapter adapter;
    private List<TheaterData> theaterDataArray;

    public Theaters() {

    }
    ExampleBroadcastReceiver exampleBroadcastReceiver = new ExampleBroadcastReceiver();

    String sharedPrefVersion = "0";

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_theaters, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        /***  Check Version Prefarance***/
        SharedPreferences appSharedPref = getContext().getSharedPreferences("AppVersion", Context.MODE_PRIVATE);

        if(!appSharedPref.getString("version", "").isEmpty()){

            sharedPrefVersion = appSharedPref.getString("version", "");

        }

        msgTv = view.findViewById(R.id.msgTv);
        recyclerView = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());



        return view;
    }

    public void getAllTheaters(String accessToken, final boolean secondAttempt){

        final ProgressDialog progress = new ProgressDialog(getContext(), R.style.MyTheme);
        progress.setCancelable(false);
        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        progress.show();

        if (Functions.isInternetAvailable(getActivity().getBaseContext())) {

            CallApiMethods.getAllTheaters(new ServerCallBack() {
                @Override
                public void onSuccess(Boolean result, JSONObject jOBJ) {

                    Functions.dismisSnacbar();

                    progress.dismiss();

                    try{

                        theaterDataArray = new ArrayList<>();
                        theaterDataArray.clear();

                        JSONObject getTheaterOBJ = jOBJ;

                        String status = getTheaterOBJ.getString("status");

                        if (status.equals("true")) {

                            JSONObject getVersionJOBJ = getTheaterOBJ.getJSONObject("version");

                            String version = getVersionJOBJ.getString("andorid");

                            if (sharedPrefVersion.equals(version)) {

                                JSONArray getTheaterDataArray = getTheaterOBJ.getJSONArray("theaterlist");

                                boolean tag = false;

                                    for (int i = 0; i < getTheaterDataArray.length(); i++) {

                                        JSONObject getTheaterData = getTheaterDataArray.getJSONObject(i);

                                        ArrayList<String> screenArrayList = new ArrayList<>();

                                        if(tag == false){

                                            /**---- screen array ----**/
                                            JSONArray getScreen = getTheaterData.getJSONArray("screens");

                                            for (int a = 0; a < getScreen.length(); a++) {

                                                JSONObject getScreenData = getScreen.getJSONObject(a);
                                                screenArrayList.add(getScreenData.toString());
                                            }

                                            tag = true;

                                        }

                                        if (tag == true) {

                                            TheaterData theaterData = new TheaterData(
                                                    getTheaterData.getString("tid"),
                                                    getTheaterData.getString("t_name"),
                                                    getTheaterData.getString("t_address"),
                                                    getTheaterData.getString("t_desccription"),
                                                    getTheaterData.getString("t_contact_no"),
                                                    getTheaterData.getString("t_img"),
                                                    getTheaterData.getString("mobile_img"),
                                                    1,
                                                    getTheaterData.getString("t_contact_no2"),
                                                    getTheaterData.getString("vista_code"),
                                                    screenArrayList,
                                                    getTheaterData.getString("online_booking"),
                                                    getTheaterData.getString("gallery"),
                                                    getTheaterData.getString("icon_image"),
                                                    getTheaterData.getString("t_experiences")

                                            );

                                            theaterDataArray.add(theaterData);

                                            tag = false;
                                        }

                                    }

                                    if(theaterDataArray.size() != 0) {

                                        recyclerView.setVisibility(View.VISIBLE);
                                        msgTv.setVisibility(View.GONE);
                                        adapter = new TheaterAdapter(getContext(), theaterDataArray);
                                        adapter.notifyDataSetChanged();
                                        recyclerView.setAdapter(adapter);

                                    }else {

                                        msgTv.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    }

                            }else{

                                startActivity(new Intent(getContext(), VersionPage.class));
                                ((Activity)getContext()).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            }

                        }else{

                            progress.dismiss();

                        }



                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Boolean result, VolleyError error) {

                    progress.dismiss();


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
                                    if(result){

                                        if(!secondAttempt) {
                                            getAllTheaters(token, true);

                                        }else{
                                            Functions.showSnackbar(getView());
                                        }

                                    }else{
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

            }, getContext(), accessToken, CallApiMethods.userKey);

        }else{
            Functions.showSnackbar(getView());
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


    private void showInternetDisconnectedView(boolean disconnected){

        boolean isNetwork = Functions.isInternetAvailable(getContext());


        if(isNetwork) {

            Functions.dismisSnacbar();

            /*********************  get All theaters ***************/
            CallApiMethods.getAccessToken(new ApiServerCallBack() {
                @Override
                public void onSuccess(Boolean result) {

                }

                @Override
                public void onSuccess(Boolean result, String token) {

                    if(result){

                        getAllTheaters(token, false);
                    }else{

                        View view = getActivity().findViewById(android.R.id.content);
                        Functions.showSnackbar(view);
                    }
                }
            }, getContext());

        }else {


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


}
