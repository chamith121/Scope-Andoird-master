package lk.archmage.scopecinemas.Classes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import lk.archmage.scopecinemas.Models.OffersData;
import lk.archmage.scopecinemas.OffersView;
import lk.archmage.scopecinemas.R;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.MyViewHolder> {

    Context context;
    ArrayList<OffersData> offersDataArray;

    public OffersAdapter(Context context, ArrayList<OffersData> offersDataArray) {
        this.context = context;
        this.offersDataArray = offersDataArray;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        Button btnMoreInfo;
        TextView titleTV, subTitleTV;
        ImageView offerImageTwoIV, offerImageIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            btnMoreInfo = itemView.findViewById(R.id.btnMoreInfo);
            titleTV = itemView.findViewById(R.id.titleTV);
            subTitleTV = itemView.findViewById(R.id.subTitleTV);
            offerImageTwoIV = itemView.findViewById(R.id.offerImageTwoIV);
            offerImageIV = itemView.findViewById(R.id.offerImageIV);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_offers, viewGroup,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        OffersData offersData = offersDataArray.get(i);

        if(!offersData.getImageOne().equals("") || !offersData.getImageOne().equals(null)) {
            Glide.with(context).load(offersData.getImageOne()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(myViewHolder.offerImageIV);
        }else {
            Glide.with(context).load(R.drawable.ttb_no_image_two).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(myViewHolder.offerImageIV);
        }
        Glide.with(context).load(offersData.getImageTwo()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(myViewHolder.offerImageTwoIV);

        myViewHolder.titleTV.setText(offersData.getName());
        myViewHolder.subTitleTV.setText(offersData.getSubTitle());

        myViewHolder.btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, OffersView.class);
                intent.putExtra("offerObj", offersData);
                context.startActivity(intent);
            }
        });
        
    }

    @Override
    public int getItemCount() {
        return offersDataArray.size();
    }






}
