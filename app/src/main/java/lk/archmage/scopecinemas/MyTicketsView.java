package lk.archmage.scopecinemas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
import lk.archmage.scopecinemas.Models.MyBookingData;

public class MyTicketsView extends AppCompatActivity {

    MyBookingData bookingData;
    ImageView backArrowIv, thumbnailMovieMt;

    TextView mainName, theaterNameTvMt, movieNameTvMt, movieDateTvMt,
            movieShowTimeTvMt, seatNumbersTvMt, txidTvMt, totalTvMt, ticketsCountTv,
            textProTv, productTvMt;


    private static final String TAG = "QRCODE";
    private ImageView imageView;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets_view);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolBarMyTickets);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

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

       bookingData = (MyBookingData) getIntent().getSerializableExtra("bookingData");

       if(bookingData != null){

           mainName.setText(bookingData.getStringBookingId());

                   imageView = findViewById(R.id.QR_Image);
           qrCodeGenarater(bookingData.getStringBookingId());

           theaterNameTvMt.setText(bookingData.getLocationName() + " - " + bookingData.getExperince());
           movieNameTvMt.setText(bookingData.getFilmName());

           try {

               String movieShowState = bookingData.getDate();

               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               SimpleDateFormat sdf1 = new SimpleDateFormat("MMMM dd, yyyy");


               Date d = sdf.parse(movieShowState);

               String setDate = sdf1.format(d);

               movieDateTvMt.setText(setDate);

           } catch (ParseException e) {
               e.printStackTrace();
           }

           movieShowTimeTvMt.setText(bookingData.getShowTime());

           seatNumbersTvMt.setText(bookingData.getSelectedSeats());

           txidTvMt.setText(bookingData.getStringBookingId());
           double amount = Double.parseDouble(bookingData.getAmount());
           totalTvMt.setText("Rs. " + String.format("%.2f", amount));

           if(!bookingData.getMobileImage().equals("") || !bookingData.getMobileImage().equals(null)) {
               Glide.with(MyTicketsView.this).load(bookingData.getMobileImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(thumbnailMovieMt);
           }else {
               Glide.with(MyTicketsView.this).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(thumbnailMovieMt);

           }
           ticketsCountTv.setText(bookingData.getTotalTickets() + "");

           ArrayList<String> foodItem = bookingData.getFilmVistaCode();

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

        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();

        return super.onOptionsItemSelected(item);
    }

}
