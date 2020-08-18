package lk.archmage.scopecinemas;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;

import lk.archmage.scopecinemas.Models.OffersData;

public class OffersView extends AppCompatActivity {

    OffersData offerOBJ;
    ImageView imgOneOV, imgTwoOV;
    TextView titleTvOV, subTitletvOV, descriptionTvOv;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_view);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolBarOffers);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        imgOneOV = findViewById(R.id.imgOneOV);
        imgTwoOV = findViewById(R.id.imgTwoOV);
        titleTvOV = findViewById(R.id.titleTvOV);
        subTitletvOV = findViewById(R.id.subTitletvOV);
        descriptionTvOv = findViewById(R.id.descriptionTvOv);

        if(getIntent().getSerializableExtra("offerObj") != null){

            offerOBJ = (OffersData) getIntent().getSerializableExtra("offerObj");

            if(!offerOBJ.getImageOne().equals("") || !offerOBJ.getImageOne().equals(null)) {
                Glide.with(OffersView.this).load(offerOBJ.getImageOne()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(imgOneOV);
            }else {
                Glide.with(OffersView.this).load(R.drawable.ttb_no_image).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(imgOneOV);
            }
            Glide.with(OffersView.this).load(offerOBJ.getImageTwo()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(imgTwoOV);

            titleTvOV.setText(offerOBJ.getName());
            subTitletvOV.setText(offerOBJ.getSubTitle());
            descriptionTvOv.setText(Html.fromHtml(offerOBJ.getDescription()));

        }else {

            finish();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();
        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
