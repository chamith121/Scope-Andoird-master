package lk.archmage.scopecinemas.Classes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import lk.archmage.scopecinemas.Models.TheaterData;
import lk.archmage.scopecinemas.R;
import lk.archmage.scopecinemas.TheaterView;

public class TheaterAdapter extends RecyclerView.Adapter<TheaterAdapter.MyViewHolder>{

    Context context;
    private List<TheaterData> theaterArray;

    Intent intent;

    public TheaterAdapter(Context context, List<TheaterData> theaterDataArray) {
        this.context = context;
        this.theaterArray = theaterDataArray;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView theaterImageIV, theaterNameIV;
        TextView theaterNameTv;
        CardView cardview_id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            theaterNameTv = itemView.findViewById(R.id.theaterNameTv);
            theaterImageIV = itemView.findViewById(R.id.theaterImageIV);
            theaterNameIV = itemView.findViewById(R.id.thaterNameIV);
            cardview_id = itemView.findViewById(R.id.cardview_id);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.theater_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final TheaterData theaterData = theaterArray.get(i);

        if(!theaterData.getTheaterImage().equals("") || !theaterData.getTheaterImage().equals(null)) {

            Glide.with(context).load(theaterData.getTheaterImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(myViewHolder.theaterImageIV);
        }else {
            Glide.with(context).load(R.drawable.ttb_no_image).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(myViewHolder.theaterImageIV);
        }
       Glide.with(context).load(theaterData.getIconImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).into(myViewHolder.theaterNameIV);

        myViewHolder.theaterNameTv.setText(theaterData.getTheaterName());

        myViewHolder.cardview_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(intent == null){
                    intent = new Intent(context, TheaterView.class);
                }

                intent.putExtra("theaterOBJ", theaterData);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return theaterArray.size();
    }


}
