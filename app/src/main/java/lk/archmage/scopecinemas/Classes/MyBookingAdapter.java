package lk.archmage.scopecinemas.Classes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import lk.archmage.scopecinemas.Models.MyBookingData;
import lk.archmage.scopecinemas.MyTicketsView;
import lk.archmage.scopecinemas.R;

public class MyBookingAdapter extends RecyclerView.Adapter<MyBookingAdapter.MyViewHolder> {

    Context context;
    ArrayList<MyBookingData> bookingArray;

    public MyBookingAdapter(@NonNull Context context, ArrayList<MyBookingData> bookingArray) {
        this.context = context;
        this.bookingArray = bookingArray;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView theaterNameMt, movieNameMt, movieDateMt, movieShowTimeMt, movieTicketsCountTv, availabilityText;
        LinearLayout clickBtn, availabilityLayout;;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            theaterNameMt = itemView.findViewById(R.id.theaterNameMt);
            movieNameMt = itemView.findViewById(R.id.movieNameMt);
            movieDateMt = itemView.findViewById(R.id.movieDateMt);
            movieShowTimeMt = itemView.findViewById(R.id.movieShowTimeMt);
            clickBtn = itemView.findViewById(R.id.clickBtn);
            movieTicketsCountTv = itemView.findViewById(R.id.movieTicketsCountTv);
            availabilityText = itemView.findViewById(R.id.availabilityText);
            availabilityLayout = itemView.findViewById(R.id.availabilityLayout);
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tickets_data_for_list, viewGroup,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        MyBookingData bookingData = bookingArray.get(i);

        holder.theaterNameMt.setText(bookingData.getLocationName());
        holder.movieNameMt.setText(bookingData.getFilmName());
        holder.movieDateMt.setText(bookingData.getDate());
        holder.movieShowTimeMt.setText(bookingData.getShowTime());

        holder.movieTicketsCountTv.setText(bookingData.getSelectedSeats());

        if(bookingData.isActive()){

            holder.clickBtn.setEnabled(true);
            holder.availabilityLayout.setBackgroundColor(Color.parseColor("#009900"));
            holder.availabilityText.setText("Active");
        }else {

            holder.clickBtn.setEnabled(false);
            holder.availabilityLayout.setBackgroundColor(Color.parseColor("#999999"));
            holder.availabilityText.setText("Inactive");
        }

        holder.clickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MyTicketsView.class);
                intent.putExtra("bookingData", bookingData);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookingArray.size();
    }





}
