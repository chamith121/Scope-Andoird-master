package lk.archmage.scopecinemas.Classes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
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

import lk.archmage.scopecinemas.Interfaces.ConcessionInterface;
import lk.archmage.scopecinemas.Models.ConcessionData;
import lk.archmage.scopecinemas.PromosAndConfessionaies;
import lk.archmage.scopecinemas.R;

public class ConcessionsAdapter extends RecyclerView.Adapter<ConcessionsAdapter.MyViewHolder> {

    Context context;
    ArrayList<ConcessionData> itemsArray;
    ConcessionInterface concessionInterface;

    public ConcessionsAdapter(Context context, ArrayList<ConcessionData> itemsArray, ConcessionInterface concessionInterface) {
        this.context = context;
        this.itemsArray = itemsArray;


        this.concessionInterface = (ConcessionInterface) concessionInterface;
    }
    public ConcessionsAdapter(Context context, ArrayList<ConcessionData> itemsArray) {
        this.context = context;
        this.itemsArray = itemsArray;


        this.concessionInterface = (ConcessionInterface) concessionInterface;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageItem;
        TextView itemNameTv, itemNameTwoTv, priceTv, countQuantityTv;
        Button plusButton, minusButton, addBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageItem = itemView.findViewById(R.id.imageItem);
            itemNameTv = itemView.findViewById(R.id.itemNameTv);
            itemNameTwoTv = itemView.findViewById(R.id.itemNameTwoTv);
            priceTv = itemView.findViewById(R.id.priceTv);
            plusButton = itemView.findViewById(R.id.plusButton);
            minusButton = itemView.findViewById(R.id.minusButton);
            addBtn = itemView.findViewById(R.id.addBtn);
            countQuantityTv = itemView.findViewById(R.id.countQuantityTv);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_food_beverages, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        ConcessionData concessionData = itemsArray.get(i);

        System.out.println("=============="+ concessionData.getImage());

        if(!concessionData.getImage().equals("") || !concessionData.getImage().equals(null)){

            myViewHolder.imageItem.setVisibility(View.VISIBLE);
            Glide.with(context).load(concessionData.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(myViewHolder.imageItem);

        }else {

            System.out.println("99999999 NO IMAGE");
            myViewHolder.imageItem.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.no_image_food).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(myViewHolder.imageItem);
        }

        String itemName = "";
        String getItem = concessionData.getName();
        String[] words = getItem.split("\\s");

        for(String w:words){

            if(!w.equals("web")){

                itemName += w +" ";

            }
        }
        String itemNameTwo = itemName.toLowerCase();
        String output = itemNameTwo.substring(0, 1).toUpperCase() + itemNameTwo.substring(1);

        myViewHolder.itemNameTwoTv.setText(output.trim());
        myViewHolder.itemNameTv.setText(output.trim());

        Double total = Double.parseDouble(concessionData.getPrice());
        myViewHolder.priceTv.setText("Rs. "+ String.format("%.2f", total));

        myViewHolder.countQuantityTv.setText(concessionData.getCount()+"");

        if(concessionData.getCount() <= 0){
            myViewHolder.minusButton.setEnabled(false);
            myViewHolder.addBtn.setEnabled(false);
        }else {
            myViewHolder.minusButton.setEnabled(true);
            myViewHolder.addBtn.setEnabled(true);
        }

        if(concessionData.getStatus() == 1){

            myViewHolder.addBtn.setBackgroundResource(R.drawable.btn_button_remove);
            myViewHolder.addBtn.setText("Remove");
            myViewHolder.plusButton.setEnabled(false);
            myViewHolder.minusButton.setEnabled(false);
        }else {

            myViewHolder.addBtn.setBackgroundResource(R.drawable.btn_button_add);
            myViewHolder.addBtn.setText("Add");
            myViewHolder.plusButton.setEnabled(true);
            myViewHolder.minusButton.setEnabled(true);
        }

        myViewHolder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                concessionData.setCount(concessionData.getCount()+1);

                notifyDataSetChanged();
            }
        });

        myViewHolder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(concessionData.getCount() > 0) {
                    concessionData.setCount(concessionData.getCount() - 1);

                }

                notifyDataSetChanged();
            }
        });

        myViewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(concessionData.getStatus() == 0) {

                    concessionData.setStatus(1);

                    concessionInterface.setConcessionObject(concessionData);
                }else {

                    concessionData.setStatus(0);
                    concessionData.setCount(0);

                    concessionInterface.removeConcessionObject(concessionData);

                }

                notifyDataSetChanged();


            }
        });




    }

    @Override
    public int getItemCount() {
        return itemsArray.size();
    }


}
