package lk.archmage.scopecinemas;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import lk.archmage.scopecinemas.Models.ComingSoonMovieData;
import lk.archmage.scopecinemas.Models.NowShowingMovieData;

public class DetailsMovie extends AppCompatActivity {

    YouTubePlayerView youTubePlayerView;

    NowShowingMovieData nowShowingMovieData;
    ComingSoonMovieData comingSoonMovieData;

    ImageView movieImageIV;
    TextView movieNameTv, ratingTv, moveGenreTv;
    RatingBar rating;

    Button buyTicketsBtn;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    Intent intent;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_movie);

        Toolbar toolbar = findViewById(R.id.toolBarDetails);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        youTubePlayerView = findViewById(R.id.youtube_player_view);

        nowShowingMovieData = getIntent().getParcelableExtra("nowShowingMovieObj");
        comingSoonMovieData = getIntent().getParcelableExtra("comingSoonMovieDataObj");

        movieImageIV = findViewById(R.id.movieImageIV);
        movieNameTv = findViewById(R.id.movieNameTv);
        rating = findViewById(R.id.rating);
        ratingTv = findViewById(R.id.ratingTv);
        buyTicketsBtn = findViewById(R.id.buyTicketsBtn);
        moveGenreTv = findViewById(R.id.moveGenreTv);

        viewPager = findViewById(R.id.viewpager);


        if(nowShowingMovieData != null){

            if(!nowShowingMovieData.getMovieImage().equals("") || !nowShowingMovieData.getMovieImage().equals(null)) {

                Glide.with(DetailsMovie.this).load(nowShowingMovieData.getMovieImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);
            }else {
                Glide.with(DetailsMovie.this).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);

            }

            movieNameTv.setText(nowShowingMovieData.getMovieName());

            if((nowShowingMovieData.getEndDate().equals(""))
                    && (!nowShowingMovieData.getStartDate().equals(""))){

                try {
                    buyTicketsBtn.setEnabled(false);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MMM-dd");

                    String getDate = nowShowingMovieData.getStartDate();
                    Date d = sdf.parse(getDate);

                    final String date = sdf2.format(d);
                    //yyyy-MMM-dd
                    String[] splitDate = date.split("-");

                    String getDaySuffix = getDayOfMonthSuffix(Integer.parseInt(splitDate[2]));

                    buyTicketsBtn.setText(splitDate[2] + getDaySuffix + " " + splitDate[1] + " " + splitDate[0]);

                }catch (ParseException e){
                    e.printStackTrace();
                }

            }else if((nowShowingMovieData.getEndDate().equals(""))
                    && (nowShowingMovieData.getStartDate().equals(""))){

                buyTicketsBtn.setText("COMING SOON");
                buyTicketsBtn.setEnabled(false);

            }else if((!nowShowingMovieData.getEndDate().equals(""))
                    && (!nowShowingMovieData.getStartDate().equals(""))){

                buyTicketsBtn.setText("BUY TICKETS");
                buyTicketsBtn.setEnabled(true);

            }else if((!nowShowingMovieData.getEndDate().equals(""))
                    && (nowShowingMovieData.getStartDate().equals(""))){

                buyTicketsBtn.setText("COMING SOON");
                buyTicketsBtn.setEnabled(false);

            }

            String replaceString2 = "";
            if(!nowShowingMovieData.getRunTime().equals("") || !nowShowingMovieData.getRunTime().isEmpty()) {

                String movieTime = nowShowingMovieData.getRunTime();
                String replaceString1 = movieTime.replace("Hour","h");
                replaceString2 = "~"+ replaceString1.replace("Minutes","m");


            }

            ArrayList<String> genreArray = nowShowingMovieData.getGenre();
            String genre = "";

            for(int a = 0; a < genreArray.size(); a++){

                genre += genreArray.get(a)+ " | ";
            }

            if (genre != null && genre.length() > 0 && genre.charAt(genre.length() - 2) == '|') {
                genre = genre.substring(0, genre.length() - 2);
            }
            moveGenreTv.setText(genre +" "+ replaceString2);

            if(nowShowingMovieData.getImdb() != null && !nowShowingMovieData.getImdb().equals("")){

                float imdbRate = Float.parseFloat(nowShowingMovieData.getImdb());

                rating.setRating(imdbRate);
                ratingTv.setText(imdbRate+"");

            }else {

                rating.setRating(Float.parseFloat("0.0"));
                ratingTv.setText("0.0");
            }


            if(nowShowingMovieData.getYouTubeUrl() != null && !nowShowingMovieData.getYouTubeUrl().equals("")) {

                String youTubeLink = nowShowingMovieData.getYouTubeUrl();

               final String getIdUrl = youTubeLink.substring(youTubeLink.length() - 11);

                youTubePlayerView.initialize(new YouTubePlayerInitListener() {
                    @Override
                    public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                        initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady() {
                                // String videoId = "W4hTJybfU7s";
                                // initializedYouTubePlayer.loadVideo(videoId,0);
                                initializedYouTubePlayer.cueVideo(getIdUrl, 0);
                                //pause();
                            }


                        });
                    }
                }, true);


            }

        }else if(comingSoonMovieData != null){

            if(!comingSoonMovieData.getMovieImage().equals("") || !comingSoonMovieData.getMovieImage().equals(null)) {

                Glide.with(DetailsMovie.this).load(comingSoonMovieData.getMovieImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);
            }else {
                Glide.with(DetailsMovie.this).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(movieImageIV);

            }

            movieNameTv.setText(comingSoonMovieData.getMovieName());

            if(comingSoonMovieData.getEndDate().equals("") && (!comingSoonMovieData.getStartDate().equals(""))){


                try {
                    buyTicketsBtn.setEnabled(false);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MMM-dd");

                    String getDate = comingSoonMovieData.getStartDate();
                    Date d = sdf.parse(getDate);

                    final String date = sdf2.format(d);
                    //yyyy-MMM-dd
                    String[] splitDate = date.split("-");

                    String getDaySuffix = getDayOfMonthSuffix(Integer.parseInt(splitDate[2]));

                    buyTicketsBtn.setText(splitDate[2] + getDaySuffix + " " + splitDate[1] + " " + splitDate[0]);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }else if((comingSoonMovieData.getEndDate().equals(""))
                    && (comingSoonMovieData.getStartDate().equals(""))){

                buyTicketsBtn.setText("COMING SOON");
                buyTicketsBtn.setEnabled(false);

            }else if((!comingSoonMovieData.getEndDate().equals(""))
                    && (!comingSoonMovieData.getStartDate().equals(""))){

                buyTicketsBtn.setText("BUY TICKETS");
                buyTicketsBtn.setEnabled(true);

            }else if((!comingSoonMovieData.getEndDate().equals(""))
                    && (comingSoonMovieData.getStartDate().equals(""))){
                buyTicketsBtn.setText("COMING SOON");
                buyTicketsBtn.setEnabled(false);

            }

            String replaceString2 = "";
            if(!comingSoonMovieData.getRunTime().equals("") || !comingSoonMovieData.getRunTime().isEmpty()) {

                String movieTime = comingSoonMovieData.getRunTime();
                String replaceString1 = movieTime.replace("Hour","h");
                replaceString2 = "~" +replaceString1.replace("Minutes","m");

            }

            ArrayList<String> genreArray = comingSoonMovieData.getGenre();
            String genre = "";

            for(int a = 0; a < genreArray.size(); a++){

                genre += genreArray.get(a)+ " | ";
            }

            if (genre != null && genre.length() > 0 && genre.charAt(genre.length() - 2) == '|') {
                genre = genre.substring(0, genre.length() - 2);
            }
            moveGenreTv.setText(genre + " " + replaceString2);

            if(comingSoonMovieData.getImdb() != null && !comingSoonMovieData.getImdb().equals("")){

                float imdbRate = Float.parseFloat(comingSoonMovieData.getImdb());

                rating.setRating(imdbRate);
                ratingTv.setText(imdbRate+"");

            }else {

                rating.setRating(Float.parseFloat("0.0"));
                ratingTv.setText("0.0");
            }


            if(comingSoonMovieData.getYouTubeUrl() != null && !comingSoonMovieData.getYouTubeUrl().equals("")) {

                String youTubeLink = comingSoonMovieData.getYouTubeUrl();

                final String getIdUrl = youTubeLink.substring(youTubeLink.length() - 11);

                youTubePlayerView.initialize(new YouTubePlayerInitListener() {
                    @Override
                    public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                        initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady() {
                                // String videoId = "W4hTJybfU7s";
                                // initializedYouTubePlayer.loadVideo(videoId,0);
                                initializedYouTubePlayer.cueVideo(getIdUrl, 0);
                                //pause();
                            }


                        });
                    }
                }, true);


            }

        }


        setUpViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        buyTicketsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(intent == null){
                    intent = new Intent(DetailsMovie.this, SelectADate.class);
                }

                if(nowShowingMovieData != null) {

                    intent.putExtra("nowShowingMovieObj", nowShowingMovieData);

                }else if(comingSoonMovieData != null){

                    intent.putExtra("comingSoonMovieDataObj", comingSoonMovieData);
                }
                startActivity(intent);
            }
        });

    }


    private void setUpViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(DetailsMovie.this.getSupportFragmentManager());

        MovieStoryLine movieStoryLine = new MovieStoryLine();

        Bundle bundleMSL = new Bundle();

        if(nowShowingMovieData != null) {
            bundleMSL.putParcelable("nowShowningMovieOBJ", getIntent().getParcelableExtra("nowShowingMovieObj"));

        }else if(comingSoonMovieData != null){
            bundleMSL.putParcelable("comingSoonMovieDataObj", getIntent().getParcelableExtra("comingSoonMovieDataObj"));
        }
        movieStoryLine.setArguments(bundleMSL);

        MovieTeamAndCast movieTeamAndCast = new MovieTeamAndCast();

        Bundle bundleTAC = new Bundle();
        if(nowShowingMovieData != null) {
        bundleTAC.putParcelable("nowShowningMovieOBJ", getIntent().getParcelableExtra("nowShowingMovieObj"));

        }else if(comingSoonMovieData != null){
            bundleTAC.putParcelable("comingSoonMovieDataObj", getIntent().getParcelableExtra("comingSoonMovieDataObj"));
        }
        movieTeamAndCast.setArguments(bundleTAC);

        adapter.addFragment(movieStoryLine, "Storyline");
        adapter.addFragment(movieTeamAndCast, "Team & Cast");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);

        }

        @Override
        public Fragment getItem(int i) {
            return mFragmentList.get(i);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
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

    String getDayOfMonthSuffix(final int n) {
        // checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }


}
