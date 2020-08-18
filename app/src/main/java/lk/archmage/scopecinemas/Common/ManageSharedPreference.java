package lk.archmage.scopecinemas.Common;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class ManageSharedPreference {

    public static void setSharedPreferences(Context context, Object className, String prefName){

        SharedPreferences appPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor prEditor = appPreferences.edit();

        Gson gson = new Gson();
        String objectString = gson.toJson(className);

        prEditor.putString("objectName", objectString);
        prEditor.commit();



        //

    }
}
