package lk.archmage.scopecinemas.Classes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lk.archmage.scopecinemas.Common.Functions;
import lk.archmage.scopecinemas.EditProfile;
import lk.archmage.scopecinemas.Models.MovieShowTimesTheatersData;
import lk.archmage.scopecinemas.R;
import lk.archmage.scopecinemas.SeatPlan;

public class ShowTimeAdapter extends RecyclerView.Adapter<ShowTimeAdapter.MyViewHolder> {

    Context context;
    MovieShowTimesTheatersData movieShowTheaterData;
    JSONArray showTimesArray;
    String movieImage, runTime, checkAdults, experienceName;
    ArrayList<String> genre;

    Intent intent;

    public ShowTimeAdapter(Context context, JSONArray showTimesArray, MovieShowTimesTheatersData movieShowTData,
                           String movieImage, String runTime, String checkAdults, ArrayList<String> genre, String experienceName) {
        this.context = context;
        this.showTimesArray = showTimesArray;
        this.movieShowTheaterData = movieShowTData;
        this.movieImage = movieImage;
        this.runTime = runTime;
        this.checkAdults = checkAdults;
        this.genre = genre;
        this.experienceName = experienceName;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView movieTimeTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTimeTv = itemView.findViewById(R.id.movieTimeTv);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_show_time, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        try {

            JSONObject getShowTimeOBJ = showTimesArray.getJSONObject(i);

            String showTime = getShowTimeOBJ.getString("showtime_name");

            if (getShowTimeOBJ.getString("showtime_status").equals("1")) {

                holder.movieTimeTv.setText(showTime);
                holder.movieTimeTv.setBackgroundResource(R.drawable.rounded_corner_movie_time_enabled);
                holder.movieTimeTv.setTextColor(context.getResources().getColor(R.color.text_color_one));

            } else if (getShowTimeOBJ.getString("showtime_status").equals("0")) {

                holder.movieTimeTv.setEnabled(false);
                holder.movieTimeTv.setText(showTime);
                holder.movieTimeTv.setBackgroundResource(R.drawable.rounded_corner_movie_time_disable);
                holder.movieTimeTv.setTextColor(context.getResources().getColor(R.color.text_color_two));
            }

            holder.movieTimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        SharedPreferences appSharedPrefa = context.getSharedPreferences("AppVersion", Context.MODE_PRIVATE);

                        JSONObject jo = new JSONObject(appSharedPrefa.getString("user", ""));


                        // f name . l name, mobile Email

                        if (jo.getString("emailAddress").equals("") || jo.getString("firstName").equals("")
                                || jo.getString("lastName").equals("") || jo.getString("mobile").length() < 8) {

                            showLogOutDialog();

                        } else {

                            if (Functions.isInternetAvailable(context.getApplicationContext())) {

                                Functions.dismisSnacbar();

                                if (intent == null) {
                                    intent = new Intent(context, SeatPlan.class);
                                }

                                final MovieShowTimesTheatersData movieShowDataOBJ = movieShowTheaterData;

                                intent.putExtra("movieShowTimeID", getShowTimeOBJ.getString("sid"));
                                intent.putExtra("cinemaId", getShowTimeOBJ.getString("cinema_id"));
                                intent.putExtra("selectedMovieShowTime", showTime);
                                intent.putExtra("selectedMovieDate", movieShowDataOBJ.getShowTimeDate());
                                intent.putExtra("theaterId", movieShowDataOBJ.getTheaterId());
                                intent.putExtra("movieId", movieShowDataOBJ.getMovieId());
                                intent.putExtra("movieShowDataOBJ", movieShowDataOBJ);

                                intent.putExtra("movieImage", movieImage);
                                intent.putExtra("runTime", runTime);
                                intent.putExtra("checkAdults", checkAdults);
                                intent.putStringArrayListExtra("genre", genre);
                                intent.putExtra("experienceName", experienceName);

                                context.startActivity(intent);

                            } else {
                                Functions.showSnackbar(v);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return showTimesArray.length();
    }


    public void showLogOutDialog() {
        Dialog noDataDialog = new Dialog(context);
        noDataDialog.setContentView(R.layout.dialog_no_user_data);

        noDataDialog.setCanceledOnTouchOutside(false);
        noDataDialog.show();

        Button btnOk = noDataDialog.findViewById(R.id.yesBtn);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                noDataDialog.dismiss();

                Intent intent = new Intent(context, EditProfile.class);
                context.startActivity(intent);

            }
        });


        noDataDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                noDataDialog.dismiss();
            }
        });

        noDataDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });


    }




}
