package lk.archmage.scopecinemas.Common;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface ServerCallBack {

    void onSuccess(Boolean result, JSONObject jOBJ);

    void onFailure(Boolean result, VolleyError error);



}
