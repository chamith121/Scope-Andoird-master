package lk.archmage.scopecinemas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import butterknife.ButterKnife;
import lk.archmage.scopecinemas.Models.ReservationsData;
import lk.archmage.scopecinemas.ProcessPayment.CollectCardInfoActivity;

public class MyTickets extends AppCompatActivity {

    ImageView thumbnailMovieMt;

    TextView mainName, theaterNameTvMt, movieNameTvMt, movieDateTvMt,
            movieShowTimeTvMt, seatNumbersTvMt, txidTvMt, totalTvMt, ticketsCountTv,
            textProTv, productTvMt;


    private static final String TAG = "QRCODE";
    private ImageView imageView;

     ReservationsData reservationsData;

    private FirebaseAnalytics mFirebaseAnalytics;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        Toolbar toolbar = findViewById(R.id.toolBarMyTickets);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mainName = findViewById(R.id.mainName);
        theaterNameTvMt = findViewById(R.id.theaterNameTvMt);
        movieNameTvMt = findViewById(R.id.movieNameTvMt);
        movieDateTvMt = findViewById(R.id.movieDateTvMt);
        movieShowTimeTvMt = findViewById(R.id.movieShowTimeTvMt);
        seatNumbersTvMt = findViewById(R.id.seatNumbersTvMt);
        txidTvMt = findViewById(R.id.txidTvMt);
        totalTvMt = findViewById(R.id.totalTvMt);
        thumbnailMovieMt = findViewById(R.id.thumbnailMovieMt);
        ticketsCountTv = findViewById(R.id.ticketsCountTv);
        textProTv = findViewById(R.id.textProTv);
        productTvMt = findViewById(R.id.productTvMt);

        ButterKnife.bind(this);

        reservationsData = (ReservationsData) getIntent().getSerializableExtra("reservationsOBJ");

        if (reservationsData != null) {

            mainName.setText(reservationsData.getStringBookingId());

            imageView = findViewById(R.id.QR_Image);
            qrCodeGenarater(reservationsData.getStringBookingId());

            theaterNameTvMt.setText(reservationsData.getLocationName() + " - " + reservationsData.getExperience());
            movieNameTvMt.setText(reservationsData.getFilmName());

            try {

                String movieShowState = reservationsData.getDate();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf1 = new SimpleDateFormat("MMMM dd, yyyy");


                Date d = sdf.parse(movieShowState);

                String setDate = sdf1.format(d);

                movieDateTvMt.setText(setDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            movieShowTimeTvMt.setText(reservationsData.getShowTime());

            seatNumbersTvMt.setText(reservationsData.getSelectedSeats());

            txidTvMt.setText(reservationsData.getReservationId());
            double amount = Double.parseDouble(reservationsData.getAmount());
            totalTvMt.setText("Rs. " + String.format("%.2f", amount));

            if(!reservationsData.getMobileImage().equals("") || !reservationsData.getMobileImage().equals(null)) {
                Glide.with(MyTickets.this).load(reservationsData.getMobileImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(thumbnailMovieMt);
            }else {
                Glide.with(MyTickets.this).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(thumbnailMovieMt);

            }
            ticketsCountTv.setText(reservationsData.getTotalTickets() + "");

            ArrayList<String> foodItem = reservationsData.getFoodItem();

            if(foodItem.size() != 0){

                try {
                    textProTv.setVisibility(View.VISIBLE);
                    productTvMt.setVisibility(View.VISIBLE);

                    String productName = "";

                    for (int i = 0; i < foodItem.size(); i++) {

                        JSONObject getFoodOBJ = new JSONObject(foodItem.get(i));

                        productName += getFoodOBJ.getString("name") +" ("+ getFoodOBJ.getString("count") +") , ";

                    }

                    if (productName != null && productName.length() > 0 && productName.charAt(productName.length() - 2) == ',') {
                        productName = productName.substring(0, productName.length() - 2);
                    }

                    productTvMt.setText(productName);

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }else {

                textProTv.setVisibility(View.GONE);
                productTvMt.setVisibility(View.GONE);
            }

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SelectAShowTime.getInstance().finish();
        SelectADate.getInstance().finish();
        ReservationSummary.getInstance().finish();
        CollectCardInfoActivity.getInstance().finish();
        PromosAndConfessionaies.getInstance().finish();
        SeatPlan.getInstance().finish();
        finish();

        return super.onOptionsItemSelected(item);
    }

    private void qrCodeGenarater(String qrData) {

        StringBuilder textToSend = new StringBuilder();
        textToSend.append(qrData);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(textToSend.toString(), BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        SelectAShowTime.getInstance().finish();
        SelectADate.getInstance().finish();
        ReservationSummary.getInstance().finish();
        CollectCardInfoActivity.getInstance().finish();
        PromosAndConfessionaies.getInstance().finish();
        SeatPlan.getInstance().finish();
        finish();
    }

}