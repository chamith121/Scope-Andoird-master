package lk.archmage.scopecinemas.Classes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import lk.archmage.scopecinemas.DetailsMovie;
import lk.archmage.scopecinemas.Models.NowShowingMovieData;
import lk.archmage.scopecinemas.R;
import lk.archmage.scopecinemas.SelectADate;

public class NowShowingMovieAdapter extends RecyclerView.Adapter<NowShowingMovieAdapter.MyViewHolder> {

    Context context;
    ArrayList<NowShowingMovieData> nowShowingArray;

    Intent intent, intentOne;


    public class MyViewHolder extends RecyclerView.ViewHolder{

        Button buyTicketsBtn;
        TextView movieNameTv, ratingTv, moveGenreTv;
        ImageView movieImageIV;
        RatingBar rating;
        CardView cardview_id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            movieImageIV = itemView.findViewById(R.id.movieImageIV);
            buyTicketsBtn = itemView.findViewById(R.id.buyTicketsBtn);
            movieNameTv = itemView.findViewById(R.id.movieNameTv);
            //movieTimeTv = itemView.findViewById(R.id.movieTimeTv);
            ratingTv = itemView.findViewById(R.id.ratingTv);
            rating = itemView.findViewById(R.id.rating);
            cardview_id = itemView.findViewById(R.id.cardview_id);
            moveGenreTv = itemView.findViewById(R.id.moveGenreTv);

        }
    }

    public NowShowingMovieAdapter(Context context, ArrayList<NowShowingMovieData> nowShowingMovieArray) {
        this.context =context;
        this.nowShowingArray = nowShowingMovieArray;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_album, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        final NowShowingMovieData nowShowingMovieData = nowShowingArray.get(i);

        if(!nowShowingMovieData.getMovieImage().equals("") || !nowShowingMovieData.getMovieImage().equals(null)) {

            Glide.with(context).load(nowShowingMovieData.getMovieImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.movieImageIV);

        }else {

            Glide.with(context).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.movieImageIV);
        }

        holder.movieNameTv.setText(nowShowingMovieData.getMovieName());

        if((nowShowingMovieData.getEndDate().equals(""))
                && (!nowShowingMovieData.getStartDate().equals(""))){

            try {
                holder.buyTicketsBtn.setEnabled(false);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MMM-dd");

                String getDate = nowShowingMovieData.getStartDate();
                Date d = sdf.parse(getDate);

                final String date = sdf2.format(d);
                //yyyy-MMM-dd
                String[] splitDate = date.split("-");

                String getDaySuffix = getDayOfMonthSuffix(Integer.parseInt(splitDate[2]));

                holder.buyTicketsBtn.setText(splitDate[2] + getDaySuffix + " " + splitDate[1] + " " + splitDate[0]);

            }catch (ParseException e){
                e.printStackTrace();
            }

        }else if((nowShowingMovieData.getEndDate().equals(""))
                && (nowShowingMovieData.getStartDate().equals(""))){

            holder.buyTicketsBtn.setText("COMING SOON");
            holder.buyTicketsBtn.setEnabled(false);

        }else if((!nowShowingMovieData.getEndDate().equals(""))
                && (!nowShowingMovieData.getStartDate().equals(""))){

            holder.buyTicketsBtn.setText("BUY TICKETS");
            holder.buyTicketsBtn.setEnabled(true);

        }else if((!nowShowingMovieData.getEndDate().equals(""))
                && (nowShowingMovieData.getStartDate().equals(""))){

            holder.buyTicketsBtn.setText("COMING SOON");
            holder.buyTicketsBtn.setEnabled(false);

        }

        ArrayList<String> genreArray = nowShowingMovieData.getGenre();
        String genre = "";

        for(int a = 0; a < genreArray.size(); a++){

            genre += genreArray.get(a)+ " | ";
        }

        if (genre != null && genre.length() > 0 && genre.charAt(genre.length() - 2) == '|') {
            genre = genre.substring(0, genre.length() - 2);
        }

        String replaceString2 = "";
        if(!nowShowingMovieData.getRunTime().equals("") || !nowShowingMovieData.getRunTime().isEmpty()) {

            String movieTime = nowShowingMovieData.getRunTime();
            String replaceString1 = movieTime.replace("Hour","h");
            replaceString2 = "~"+ replaceString1.replace("Minutes","m");
           // holder.movieTimeTv.setText("~ "+ replaceString2);

        }else{
            //holder.movieTimeTv.setText("");
        }

        holder.moveGenreTv.setText(genre + " "+ replaceString2);

        if(nowShowingMovieData.getImdb() != null && !nowShowingMovieData.getImdb().equals("")){

            float imdbRate = Float.parseFloat(nowShowingMovieData.getImdb());

            holder.rating.setRating(imdbRate);
            holder.ratingTv.setText(imdbRate+"");

        }else {

            holder.rating.setRating(Float.parseFloat("0.0"));
            holder.ratingTv.setText("0.0");
        }

        holder.buyTicketsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(intent == null){
                    intent = new Intent(context, SelectADate.class);
                }

                intent.putExtra("nowShowingMovieObj", nowShowingMovieData);
                context.startActivity(intent);
            }
        });

        holder.cardview_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(intentOne == null){
                    intentOne = new Intent(context, DetailsMovie.class);
                }

                intentOne.putExtra("nowShowingMovieObj", nowShowingMovieData);
                context.startActivity(intentOne);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nowShowingArray.size();
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
