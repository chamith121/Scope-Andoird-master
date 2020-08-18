package lk.archmage.scopecinemas.Classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import lk.archmage.scopecinemas.Interfaces.FoodItemInterface;
import lk.archmage.scopecinemas.Models.ConcessionData;
import lk.archmage.scopecinemas.R;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyViewHolder> {

    Context context;
    ArrayList<ConcessionData> selectedFoodArray;
    FoodItemInterface foodItemInterface;

    public FoodAdapter(@NonNull Context context, ArrayList<ConcessionData> selectedFoodArray, FoodItemInterface foodItemInterface) {
        this.context = context;
        this.selectedFoodArray = selectedFoodArray;
        this.foodItemInterface = foodItemInterface;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView itemNameTvFA, itemQtyTvFA, itemPriceTvFA;
        Button itemRemoveTvFA;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTvFA = itemView.findViewById(R.id.itemNameTvFA);
            itemQtyTvFA = itemView.findViewById(R.id.itemQtyTvFA);
            itemPriceTvFA = itemView.findViewById(R.id.itemPriceTvFA);
            itemRemoveTvFA = itemView.findViewById(R.id.itemRemoveTvFA);

        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_selected_fodd_item, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        ConcessionData concessionData = selectedFoodArray.get(i);

        holder.itemNameTvFA.setText(concessionData.getName());
        holder.itemQtyTvFA.setText("x "+concessionData.getCount());

        Double price = Double.parseDouble(concessionData.getPrice());
        holder.itemPriceTvFA.setText("Rs. "+ String.format("%.2f", price));

        holder.itemRemoveTvFA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedFoodArray.remove(i);
                foodItemInterface.removeFoodObject(concessionData);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return selectedFoodArray.size();
    }



}
