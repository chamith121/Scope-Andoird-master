package lk.archmage.scopecinemas.Common;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import lk.archmage.scopecinemas.R;


public class Functions {


    private static ProgressDialog progress;

    public static void showLoadingDialog(Context context, String msg) {

        if (progress == null) {
//            progress = new ProgressDialog(context);
//            progress.setCancelable(false);
//            progress.setMessage(msg);
//            progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);

            progress = new ProgressDialog(context, R.style.MyTheme);
            progress.setCancelable(false);
            progress.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
//            progress.show();

        }

        progress.show();
    }

    public static void dismissLoadingDialog() {

        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }


    public static boolean isInternetAvailable(Context context) {

        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null) {
            Log.d("task", "no internet connection");
            return false;
        } else {

            Log.d("task", " internet connection");
            return true;
        }

    }

    public static Snackbar snackbar;

    public static void showSnackbar(View view) {


        snackbar = Snackbar.make(view, "No Network Found !", Snackbar.LENGTH_INDEFINITE);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.RED);

        TextView tv = sbView.findViewById(android.support.design.R.id.snackbar_text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        snackbar.setAction("Action", null).show();

    }

    public static void dismisSnacbar() {

        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    public static void errorShowSnacBar(View view) {

        Snackbar snackbar = Snackbar.make(view, "TimeoutError !", Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.RED);

        TextView tv = sbView.findViewById(android.support.design.R.id.snackbar_text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        snackbar.setAction("Action", null).show();

    }




}
