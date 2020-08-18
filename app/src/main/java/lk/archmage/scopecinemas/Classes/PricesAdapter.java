package lk.archmage.scopecinemas.Classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lk.archmage.scopecinemas.Interfaces.PriceInterface;
import lk.archmage.scopecinemas.R;

public class PricesAdapter extends RecyclerView.Adapter<PricesAdapter.MyViewHolder> {

    private Context context;
    JSONArray pricesList;
    int totalTicketCount;

    private PriceInterface priceInterface;


    public PricesAdapter(@NonNull Context context, JSONArray pricesList, int totalTicketCount, PriceInterface priceInterface) {
        this.context = context;
        this.pricesList = pricesList;
        this.priceInterface = priceInterface;
        this.totalTicketCount = totalTicketCount;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public Button minusButton, plusButton;
        public TextView titleTv, countTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.titleTv);
            countTv = itemView.findViewById(R.id.countTv);
            minusButton = itemView.findViewById(R.id.minusButton);
            plusButton = itemView.findViewById(R.id.plusButton);
        }


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prices_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        try {

                JSONObject jobj = pricesList.getJSONObject(i);

            int itemCount = Integer.parseInt(jobj.getString("count"));

            /*** --- Enable Disable Buttons  ---***/
            if(itemCount == 0){
                holder.minusButton.setEnabled(false);

            }else {

                holder.minusButton.setEnabled(true);
            }


            if(itemCount >= totalTicketCount){
                holder.plusButton.setEnabled(false);

            }else {

                holder.plusButton.setEnabled(true);
            }



            holder.titleTv.setText(jobj.getString("name"));

                holder.countTv.setText("" + jobj.getString("count"));

                holder.plusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {



                            int count = Integer.parseInt(jobj.getString("count"));

                            pricesList.getJSONObject(i).put("count",count + 1);

                            // REDUCE 1 FROM THE FIRST EVELMENT WE FIND THAT IS NOT THE SELECTED
                            for (int j = 0; j < pricesList.length(); j++) {

                                int tempCount =  Integer.parseInt(pricesList.getJSONObject(j).getString("count"));

                                if(j != i ){

                                    if(tempCount > 0){

                                        pricesList.getJSONObject(j).put("count",tempCount - 1);
                                        break;
                                    }

                                }


                            }

                            holder.countTv.setText("" + pricesList.getJSONObject(i).getString("count"));


                            priceInterface.getPriceJSONArray(pricesList);

                            notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                holder.minusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            int count = Integer.parseInt(jobj.getString("count"));

                            pricesList.getJSONObject(i).put("count",count - 1);

                            // REDUCE 1 FROM THE FIRST EVELMENT WE FIND THAT IS NOT THE SELECTED
                            for (int j = 0; j < pricesList.length(); j++) {

                                int tempCount =  Integer.parseInt(pricesList.getJSONObject(j).getString("count"));

                                if(j != i ){

                                        pricesList.getJSONObject(j).put("count",tempCount + 1);
                                        break;

                                }

                            }

                            holder.countTv.setText("" + pricesList.getJSONObject(i).getString("count"));

                            priceInterface.getPriceJSONArray(pricesList);

                            notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return pricesList.length();
    }



}
