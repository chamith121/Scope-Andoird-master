package lk.archmage.scopecinemas.Classes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import lk.archmage.scopecinemas.Models.ComingSoonMovieData;
import lk.archmage.scopecinemas.R;
import lk.archmage.scopecinemas.SelectAShowTime;

public class MovieShowDatesCSAdapter extends RecyclerView.Adapter<MovieShowDatesCSAdapter.MyViewHolder>  {

    Context context;
    ArrayList movieDatesArray;
    ComingSoonMovieData comingSoonMovieData;

    Intent intent;

    public MovieShowDatesCSAdapter(Context context, ArrayList getDates, ComingSoonMovieData comingSoonMovieData) {
        this.context = context;
        this.movieDatesArray = getDates;
        this.comingSoonMovieData = comingSoonMovieData;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView monthNameTv, dayTv, dayNameTv;
        LinearLayout layoutBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            monthNameTv = itemView.findViewById(R.id.monthNameTv);
            dayTv = itemView.findViewById(R.id.dayTv);
            dayNameTv = itemView.findViewById(R.id.dayNameTv);
            layoutBtn = itemView.findViewById(R.id.layoutBtn);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.select_date_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        try {

            if(comingSoonMovieData != null){

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf1 = new SimpleDateFormat("E-MMM-dd");
                Date d = (Date) movieDatesArray.get(i);

                final String date = sdf.format(d);

                Date dd = sdf.parse(date);

                String setDate = sdf1.format(dd);

                String[] splitDate = setDate.split("-");

                myViewHolder.monthNameTv.setText(splitDate[1].toUpperCase());
                myViewHolder.dayTv.setText(splitDate[2]);
                myViewHolder.dayNameTv.setText(splitDate[0].toUpperCase());

                Date cDate = new Date();
                String currentDate = sdf.format(cDate);
                Date setCurrentDate = sdf.parse(currentDate);

                if (dd.compareTo(setCurrentDate) > 0 || dd.compareTo(setCurrentDate) == 0) {

                    myViewHolder.layoutBtn.setEnabled(true);

                } else {

                    myViewHolder.layoutBtn.setEnabled(false);

                }

                myViewHolder.layoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (intent == null) {
                            intent = new Intent(context, SelectAShowTime.class);
                        }

                        intent.putExtra("comingSoonMovieOBJ", comingSoonMovieData);
                        intent.putExtra("selectDate", date);
                        context.startActivity(intent);
                    }
                });
            }



        } catch (Exception e) {
        e.printStackTrace();
    }
}

    @Override
    public int getItemCount() {
        return movieDatesArray.size();
    }

}
