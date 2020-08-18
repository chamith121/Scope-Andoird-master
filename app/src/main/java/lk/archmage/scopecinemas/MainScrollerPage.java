package lk.archmage.scopecinemas;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import lk.archmage.scopecinemas.Models.NowShowingMovieData;


public class MainScrollerPage extends Fragment {

    TextView movieNameTv, imdbTv, moveGenreTv;
    Button buyTicketsBtn;
    LinearLayout youTubeLinkLayout, moreDetailsLayout;

    public MainScrollerPage() {

    }

    ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;

    ArrayList<NowShowingMovieData> nowShowingMovieArray;

    Intent intent, intentOne, intentYouTube;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_scroller_page, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        movieNameTv = view.findViewById(R.id.movieNameTv);
       //movieTimeTv = view.findViewById(R.id.movieTimeTv);
        imdbTv = view.findViewById(R.id.imdbTv);
        buyTicketsBtn = view.findViewById(R.id.buyTicketsBtn);
        youTubeLinkLayout = view.findViewById(R.id.youTubeLinkLayout);
        moreDetailsLayout = view.findViewById(R.id.moreDetailsLayout);
        moveGenreTv = view.findViewById(R.id.moveGenreTv);

        final Bundle bundel = getArguments();

        nowShowingMovieArray = bundel.getParcelableArrayList("nowShowningMovieList");

        viewPager = view.findViewById(R.id.viewPager);

        myViewPagerAdapter = new MyViewPagerAdapter(getContext(), nowShowingMovieArray);
        viewPager.setAdapter(myViewPagerAdapter);
        myViewPagerAdapter.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        changeContent(0);
        setupAutoPager();
        return view;
    }

    private void changeContent(int currentPosition) {

        for (int i = 0; i < nowShowingMovieArray.size(); i++){

            final NowShowingMovieData nowShowingMovieData = nowShowingMovieArray.get(i);

            if (currentPosition == i){

                movieNameTv.setText(nowShowingMovieData.getMovieName());

                /** Concat Action And movie Time  ***/
                ArrayList<String> genreArray = nowShowingMovieData.getGenre();
                String genre = "";

                for(int a = 0; a < genreArray.size(); a++){

                    genre += genreArray.get(a)+ " | ";
                }

                if (genre != null && genre.length() > 0 && genre.charAt(genre.length() - 2) == '|') {
                    genre = genre.substring(0, genre.length() - 2);
                }
              //  moveGenreTv.setText(genre);

                if(!nowShowingMovieData.getRunTime().equals("") || !nowShowingMovieData.getRunTime().isEmpty()) {

                    String movieTime = nowShowingMovieData.getRunTime();
                    String replaceString1 = movieTime.replace("Hour","h");
                    String replaceString2 = replaceString1.replace("Minutes","m");

                    moveGenreTv.setText(genre +"~ "+ replaceString2);

                  //  movieTimeTv.setText("~ "+ replaceString2);

                }else{
                    moveGenreTv.setText(genre);
                }

                if(nowShowingMovieData.getImdb() != null && !nowShowingMovieData.getImdb().equals("")){

                    float imdbRate = Float.parseFloat(nowShowingMovieData.getImdb());
                    imdbTv.setText(imdbRate+"");

                }else {

                    imdbTv.setText("0.0");
                }

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

                buyTicketsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(intent == null){
                            intent = new Intent(getContext(), SelectADate.class);
                        }

                        intent.putExtra("nowShowingMovieObj", nowShowingMovieData);
                        getContext().startActivity(intent);
                    }
                });

                youTubeLinkLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(intentYouTube == null){
                            intentYouTube = new Intent(getContext(), OpenYoutube.class);
                        }

                        intentYouTube.putExtra("linkYouTube", nowShowingMovieData.getYouTubeUrl());
                        getContext().startActivity(intentYouTube);
                    }
                });

                moreDetailsLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(intentOne == null){
                            intentOne = new Intent(getContext(), DetailsMovie.class);
                        }

                        intentOne.putExtra("nowShowingMovieObj", nowShowingMovieData);

                        getContext().startActivity(intentOne);

                    }
                });



            }else {
                //todo
            }
        }

    }

    public class MyViewPagerAdapter extends PagerAdapter {

        Context context;
        ArrayList<NowShowingMovieData> nowShowingMovieArray;
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter(Context context, ArrayList<NowShowingMovieData> nowShowingMovieArray){
            this.context = context;
            this.nowShowingMovieArray = nowShowingMovieArray;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.scrole_image_layout, container, false);

            ImageView mainImageIV = view.findViewById(R.id.mainImageIV);

            final NowShowingMovieData nowShowingMovieData = nowShowingMovieArray.get(position);

            if(!nowShowingMovieData.getMovieImage().equals("") || !nowShowingMovieData.getMovieImage().equals(null)) {
                Glide.with(context).load(nowShowingMovieData.getMovieImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(mainImageIV);
            }else {
                Glide.with(context).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(mainImageIV);
            }
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return nowShowingMovieArray.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            changeContent(position);

            currentPage = position;

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    Timer timer;
    private int currentPage = 0;

    private void setupAutoPager()
    {
        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run()
            {

                viewPager.setCurrentItem(currentPage, true);

                if(currentPage == nowShowingMovieArray.size())
                {
                    currentPage = 0;

                    viewPager.setCurrentItem(currentPage, true);
                }
                else
                {
                    ++currentPage ;
                }

            }
        };


        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 7500);
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
