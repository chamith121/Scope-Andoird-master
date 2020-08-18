package lk.archmage.scopecinemas;

import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import lk.archmage.scopecinemas.Classes.MovieShowDatesAdapter;
import lk.archmage.scopecinemas.Classes.MovieShowDatesByTheaterAdapter;
import lk.archmage.scopecinemas.Classes.MovieShowDatesCSAdapter;
import lk.archmage.scopecinemas.Classes.NowShowingMovieAdapter;
import lk.archmage.scopecinemas.Models.ComingSoonMovieData;
import lk.archmage.scopecinemas.Models.NowShowingMovieData;
import lk.archmage.scopecinemas.Models.TheaterByMovieData;

public class SelectADate extends AppCompatActivity {

    NowShowingMovieData nowShowingMovieData;
    ComingSoonMovieData comingSoonMovieData;


    ImageView movieImageIV;
    TextView movieNameTv,moveGenreTv;

    TextView msgTv;
    private RecyclerView recyclerView;
    MovieShowDatesAdapter adapter;
    MovieShowDatesCSAdapter adapter2;
    MovieShowDatesByTheaterAdapter adapterTwo;

    static SelectADate selectADate;

    public static SelectADate getInstance(){
        return selectADate;
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_adate);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        selectADate = this;

        Toolbar toolbar = findViewById(R.id.toolBarSelectAdate);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        movieImageIV = findViewById(R.id.movieImageIV);
        movieNameTv = findViewById(R.id.movieNameTv);
        moveGenreTv = findViewById(R.id.moveGenreTv);

        msgTv = findViewById(R.id.msgTv);
        recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(SelectADate.this, 4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        nowShowingMovieData = getIntent().getParcelableExtra("nowShowingMovieObj");
        comingSoonMovieData = getIntent().getParcelableExtra("comingSoonMovieDataObj");

        if(nowShowingMovieData != null){

            if(!nowShowingMovieData.getMovieImage().equals("") || !nowShowingMovieData.getMovieImage().equals(null)) {
                Glide.with(SelectADate.this).load(nowShowingMovieData.getMovieImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);
            }else {
                Glide.with(SelectADate.this).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);
            }

            movieNameTv.setText(nowShowingMovieData.getMovieName());

            String replaceString2 = "";
            if(!nowShowingMovieData.getRunTime().equals("") || !nowShowingMovieData.getRunTime().isEmpty()) {

                String movieTime = nowShowingMovieData.getRunTime();
                String replaceString1 = movieTime.replace("Hour","h");
                replaceString2 = "~" + replaceString1.replace("Minutes","m");
                // movieTimeTv.setText("~ "+ replaceString2);

            }

            ArrayList<String> genreArray = nowShowingMovieData.getGenre();
            String genre = "";

            for(int a = 0; a < genreArray.size(); a++){

                genre += genreArray.get(a)+ " | ";
            }

            if (genre != null && genre.length() > 0 && genre.charAt(genre.length() - 2) == '|') {
                genre = genre.substring(0, genre.length() - 2);
            }
            moveGenreTv.setText(genre + " "+ replaceString2);

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                Date currentDate = new Date();

                Date setEetDate;
                if(!nowShowingMovieData.getEndDate().isEmpty() || !nowShowingMovieData.getEndDate().equals("")) {

                    String getEndDate = nowShowingMovieData.getEndDate();
                    Date dd = sdf.parse(getEndDate);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dd);

                    cal.add(Calendar.DATE, 1);

                    String getDate = sdf.format(cal.getTime());
                    setEetDate = sdf.parse(getDate);

                }else {

                    setEetDate = currentDate;
                }

                ArrayList getDates = (ArrayList) getDatesBetween(currentDate, setEetDate);

                //recyclerView.setVisibility(View.VISIBLE);
                adapter = new MovieShowDatesAdapter(SelectADate.this, getDates, nowShowingMovieData);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        if(comingSoonMovieData != null){

            if(!comingSoonMovieData.getMovieImage().equals("") || !comingSoonMovieData.getMovieImage().equals(null)) {

                Glide.with(SelectADate.this).load(comingSoonMovieData.getMovieImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);
            }else {
                Glide.with(SelectADate.this).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);

            }

            movieNameTv.setText(comingSoonMovieData.getMovieName());

            String replaceString2 = "";
            if(!comingSoonMovieData.getRunTime().equals("") || !comingSoonMovieData.getRunTime().isEmpty()) {

                String movieTime = comingSoonMovieData.getRunTime();
                String replaceString1 = movieTime.replace("Hour","h");
                replaceString2 = "~" + replaceString1.replace("Minutes","m");
                // movieTimeTv.setText("~ "+ replaceString2);

            }

            ArrayList<String> genreArray = comingSoonMovieData.getGenre();
            String genre = "";

            for(int a = 0; a < genreArray.size(); a++){

                genre += genreArray.get(a)+ " | ";
            }

            if (genre != null && genre.length() > 0 && genre.charAt(genre.length() - 2) == '|') {
                genre = genre.substring(0, genre.length() - 2);
            }
            moveGenreTv.setText(genre + " "+ replaceString2);

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                Date setEetDate = null;
                Date setStDate = null;
                if(!comingSoonMovieData.getEndDate().isEmpty() || !comingSoonMovieData.getEndDate().equals("")) {

                    String getEndDate = comingSoonMovieData.getEndDate();
                    Date dd = sdf.parse(getEndDate);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dd);

                    cal.add(Calendar.DATE, 0);

                    String getDate = sdf.format(cal.getTime());
                    setEetDate = sdf.parse(getDate);

                }

                if(!comingSoonMovieData.getStartDate().isEmpty() || !comingSoonMovieData.getStartDate().equals("")) {

                    String getStDate = comingSoonMovieData.getStartDate();
                    Date dd = sdf.parse(getStDate);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dd);

                    cal.add(Calendar.DATE, 0);

                    String getDate = sdf.format(cal.getTime());
                    setStDate = sdf.parse(getDate);

                }

                ArrayList getDates = (ArrayList) getDatesBetween(setStDate, setEetDate);

                adapter2 = new MovieShowDatesCSAdapter(SelectADate.this, getDates, comingSoonMovieData);
                adapter2.notifyDataSetChanged();
                recyclerView.setAdapter(adapter2);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

    }


    public static List<Date> getDatesBetween(Date startDate, Date endDate) {

        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar) || calendar.equals(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
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

}
