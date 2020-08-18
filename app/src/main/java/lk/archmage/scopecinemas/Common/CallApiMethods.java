package lk.archmage.scopecinemas.Common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import lk.archmage.scopecinemas.Models.ApiTokenData;

public class CallApiMethods {

    public static String appVersion = "7";

    public static String userKey = "4c21bdc2-fb12-467b-98e0-612e2cececd8";

    public static boolean checkedValidAccessToken(Context context) {

        SharedPreferences appSharedPref = context.getSharedPreferences("apiData", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String gsondata = appSharedPref.getString("objectApi", "");
        ApiTokenData getApiData = gson.fromJson(gsondata, ApiTokenData.class);

        if (appSharedPref.getString("objectApi", "").isEmpty()) {

            return false;

        } else {

            long nowCurrentTimeMilles = System.currentTimeMillis();
            long firstCurrentTimeMilles = getApiData.getFirstRequestTime();

            float difference = (nowCurrentTimeMilles - firstCurrentTimeMilles) / 1000F;

            float expreInTime = Float.parseFloat(getApiData.getExpiresIn());


            if (expreInTime < difference) {
                System.out.println("Checked false");

                return false;

            } else {

                System.out.println("Checked true");
                return true;
            }

        }

    }

    public static ApiTokenData getSharedPrefaranceDataAccessToken(Context context) {

        SharedPreferences appSharedPref = context.getSharedPreferences("apiData", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String gsondata = appSharedPref.getString("objectApi", "");
        ApiTokenData getApiData = gson.fromJson(gsondata, ApiTokenData.class);

        Log.i("TAG","ACCESS_TOKEN:- " + getApiData.getAccessToken());

        return getApiData;

    }


    public static boolean getAccessToken(final ApiServerCallBack apiServerCallBack, final Context context) {

        if (isConnected(context)) {

            if (checkedValidAccessToken(context)) {


                apiServerCallBack.onSuccess(true, getSharedPrefaranceDataAccessToken(context).getAccessToken());

            } else {

                try {

                    RequestQueue queue = Volley.newRequestQueue(context);

                    String url = "https://gateway.api.cloud.wso2.com/token?grant_type=client_credentials";

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {

                                JSONObject responceOBJ = new JSONObject(response);

                                String accessToken = responceOBJ.getString("access_token");
                                String scope = responceOBJ.getString("scope");
                                String tokenType = responceOBJ.getString("token_type");
                                String expiresIn = responceOBJ.getString("expires_in");
                                Long firstTimeRequest = System.currentTimeMillis();

                                SharedPreferences appPreferences = context.getSharedPreferences("apiData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor prEditor = appPreferences.edit();

                                ApiTokenData apiTokenData = new ApiTokenData(accessToken, scope, tokenType, expiresIn, firstTimeRequest);

                                Gson gson = new Gson();
                                String objectString = gson.toJson(apiTokenData);

                                prEditor.putString("objectApi", objectString);
                                prEditor.commit();

                                apiServerCallBack.onSuccess(true, apiTokenData.getAccessToken());

                                Log.i("RESPONCE", ": " + response);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            apiServerCallBack.onSuccess(false, "");

                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {

                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Basic MUJraXl4WW13emhaZWJsVE1PcXNkQklZM3pzYTpwWk9rSTFvODVnazQ5ZG04SXFZaWU5cGpaZkVh");

                            return headers;
                        }
                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            30000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    queue.add(stringRequest);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else {


            return false;
        }

        return false;
    }


    public static void getNowShowingMovies(final ServerCallBack serverCallBack, Context context, final String accessToken, String userKey) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.getShowingMoviesUrl + userKey;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject getResponceOBJ = new JSONObject(response);

                        serverCallBack.onSuccess(true, getResponceOBJ);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    serverCallBack.onFailure(true, error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);

        } catch (Exception e) {

            //serverCallBack.onFailure(true);

            e.printStackTrace();
        }


    }



    public static void getUpComeingMovies(final ServerCallBack serverCallBack, Context context, final String accessToken, String userKey) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.getUpComeingMoviesUrl + userKey;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try {

                        JSONObject getResponceOBJ = new JSONObject(response);

                        Log.i("***UPCOMING***", response);

                        serverCallBack.onSuccess(true, getResponceOBJ);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);

        } catch (Exception e) {

            //  serverCallBack.onFailure(true);

            e.printStackTrace();
        }

    }

    public static void getAllTheaters(final ServerCallBack serverCallBack, Context context, final String accessToken, String userKey) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.getAllTheatersUrl + userKey;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try {

                        JSONObject getResponceOBJ = new JSONObject(response);

                        serverCallBack.onSuccess(true, getResponceOBJ);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);


        } catch (Exception e) {

            //  serverCallBack.onFailure(true);
            e.printStackTrace();
        }

    }

    public static void getMovieShowTime(final ServerCallBack serverCallBack, Context context, final String accessToken, String userKey, String movieDate, String movieId) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.getMovieShowTimeUrl + "movie_id=" + movieId + "&movie_date=" + movieDate + "&user_key=" + userKey;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try {

                        JSONObject getResponceOBJ = new JSONObject(response);

                        serverCallBack.onSuccess(true, getResponceOBJ);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);


        } catch (Exception e) {

            //   serverCallBack.onFailure(true, error);

            //  e.printStackTrace();
        }
    }

    public static void getSeatPlan(final ServerCallBack serverCallBack, Context context, final String accessToken, String userKey, String sessionId, String cinemaId){

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.getSeatPlanUrl + "user_key=" + userKey +"&session_id="+ sessionId +"&cinema_id="+cinemaId;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try {

                        JSONObject getResponceOBJ = new JSONObject(response);

                        serverCallBack.onSuccess(true, getResponceOBJ);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);


        } catch (Exception e) {

            //   serverCallBack.onFailure(true, error);

            //  e.printStackTrace();
        }
    }

    public static void getConcerssion(final ServerCallBack serverCallBack, Context context, final String accessToken, String userKey, String cinemaId){

        try{

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.getConcessionUrl + "user_key=" + userKey +"&cinema_id="+cinemaId;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try {

                        JSONObject getResponceOBJ = new JSONObject(response);

                        serverCallBack.onSuccess(true, getResponceOBJ);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);


        }catch (Exception e){
           // e.printStackTrace();
        }
    }

    public static void getAllOffers(final ServerCallBack serverCallBack, Context context, final String accessToken, String userKey){

        try{

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.getAllOffersUrl + "user_key=" + userKey;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try {

                        JSONObject getResponceOBJ = new JSONObject(response);

                        serverCallBack.onSuccess(true, getResponceOBJ);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);

        }catch (Exception e){
            //
        }
    }

    public static void saveBookingData(final ServerCallBack serverCallBack, Context context, final String accessToken,
                                       JSONObject getObj) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, MethodsURLs.saveBookingDataUrl, getObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        serverCallBack.onSuccess(true, response);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);

        } catch (Exception e) {

            serverCallBack.onFailure(true, null);

            e.printStackTrace();
        }

    }

    public static void updateFoodandCustomerData(final ServerCallBack serverCallBack, Context context, final String accessToken,
                                                 JSONObject getFoodObj) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, MethodsURLs.updateFoodandCustomerDataUrl, getFoodObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        serverCallBack.onSuccess(true, response);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);

        } catch (Exception e) {

            serverCallBack.onFailure(true, null);

            e.printStackTrace();
        }

    }




    /** SIGN IN **/

    public static void signIn(final ServerCallBack serverCallBack, Context context, final String accessToken, JSONObject signInObject) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, MethodsURLs.signInUrl, signInObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        serverCallBack.onSuccess(true, response);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);

        } catch (Exception e) {

            serverCallBack.onFailure(true, null);

            e.printStackTrace();
        }

    }

    /** REGISTER **/

    public static void registerUser(final ServerCallBack serverCallBack, Context context, final String accessToken, JSONObject signInObject) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, MethodsURLs.registerUrl, signInObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        serverCallBack.onSuccess(true, response);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);

        } catch (Exception e) {

            serverCallBack.onFailure(true, null);

            e.printStackTrace();
        }

    }

    public static void updatePaymentSuccess(final ServerCallBack serverCallBack, Context context, final String accessToken,
                                                        String userKey, String reservationId) {
        try {

            Log.e("ERROR_TWO_1", "UPDATE BOOKING PAYMENT ENTER");

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.paymentSuccess + "user_key=" + userKey + "&reservation_id=" + reservationId;


            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        Log.e("REPOSNE ERROR", "GET THE RESPONSE");

                        JSONObject getResponceOBJ = new JSONObject(response);

                        serverCallBack.onSuccess(true, getResponceOBJ);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            queue.add(stringRequest);

        } catch (Exception e) {

            // serverCallBack.onFailure(true);
            e.printStackTrace();
        }

    }

    public static void getMyBookingInformation(final ServerCallBack serverCallBack, Context context, final String accessToken,
                                             String userKey, String userId) {
        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.getMyBookingDataUrl + "user_key=" + userKey + "&user_id=" + userId;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject getResponceOBJ = new JSONObject(response);

                        serverCallBack.onSuccess(true, getResponceOBJ);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            queue.add(stringRequest);

        } catch (Exception e) {

            //serverCallBack.onFailure(true);
            e.printStackTrace();
        }

    }

    public static void paymentCancel(final ServerCallBack serverCallBack, Context context, final String accessToken,
                                               String userKey, String reservationId) {
        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.paymentCancelUrl + "user_key=" + userKey + "&reservation_id=" + reservationId;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject getResponceOBJ = new JSONObject(response);

                        serverCallBack.onSuccess(true, getResponceOBJ);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            queue.add(stringRequest);

        } catch (Exception e) {

            //serverCallBack.onFailure(true);
            e.printStackTrace();
        }

    }


    public static void getNowshowingInTheater(final ServerCallBack serverCallBack, Context context, final String accessToken,
                                              String userKey, String theaterId, String date) {
        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.getNowshowingInTheaterUrl + "user_key=" + userKey + "&theater_id=" + theaterId + "&date=" + date;

            System.out.println("URL: " + url);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject getResponceOBJ = new JSONObject(response);

                        serverCallBack.onSuccess(true, getResponceOBJ);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            queue.add(stringRequest);

        } catch (Exception e) {

            //serverCallBack.onFailure(true);
            e.printStackTrace();
        }

    }


    public static boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mobile != null && mobile.isConnectedOrConnecting() || (wifi != null && wifi.isConnectedOrConnecting())) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }


    public static void updateUser(final ServerCallBack serverCallBack, Context context, final String accessToken, JSONObject signInObject) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, MethodsURLs.updateUserUrl, signInObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        serverCallBack.onSuccess(true, response);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);

        } catch (Exception e) {

            serverCallBack.onFailure(true, null);

            e.printStackTrace();
        }

    }


    public static void facebookSignin(final ServerCallBack serverCallBack, Context context, final String accessToken, JSONObject signInObject) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, MethodsURLs.facebookSignInUrl, signInObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        serverCallBack.onSuccess(true, response);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);

        } catch (Exception e) {

            serverCallBack.onFailure(true, null);

            e.printStackTrace();
        }

    }


    public static void forgotPassword(final ServerCallBack serverCallBack, Context context, final String accessToken, JSONObject signInObject) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, MethodsURLs.forgotPasswordUrl, signInObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        serverCallBack.onSuccess(true, response);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);

        } catch (Exception e) {

            serverCallBack.onFailure(true, null);

            e.printStackTrace();
        }

    }


    public static void getUserInfo(final ServerCallBack serverCallBack, Context context, final String accessToken, String user_key, String user_id) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.getUserUrl + "user_key=" + user_key + "&user_id=" + user_id;


            System.out.println(url);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        serverCallBack.onSuccess(true, response);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);

        } catch (Exception e) {

            serverCallBack.onFailure(true, null);

            e.printStackTrace();
        }

    }



    public static void activateLoyalty(final ServerCallBack serverCallBack, Context context, final String accessToken, JSONObject signInObject) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, MethodsURLs.activateLoyaltyUrl, signInObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        serverCallBack.onSuccess(true, response);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);

        } catch (Exception e) {

            serverCallBack.onFailure(true, null);

            e.printStackTrace();
        }

    }



    public static void getMyLoyaltyHistory(final ServerCallBack serverCallBack, Context context, final String accessToken, String user_key, String user_id) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = MethodsURLs.getMyLoyaltyHistoryUrl + "user_key=" + user_key + "&user_id=" + user_id;


            System.out.println(url);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        serverCallBack.onSuccess(true, response);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    serverCallBack.onFailure(true, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization:", "Bearer " + accessToken);

                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);

        } catch (Exception e) {

            serverCallBack.onFailure(true, null);

            e.printStackTrace();
        }

    }



}
