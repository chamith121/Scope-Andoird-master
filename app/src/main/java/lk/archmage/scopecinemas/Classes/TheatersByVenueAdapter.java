package lk.archmage.scopecinemas.Classes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lk.archmage.scopecinemas.Models.MovieShowTimesTheatersData;
import lk.archmage.scopecinemas.Models.NowShowingMovieData;
import lk.archmage.scopecinemas.R;

public class TheatersByVenueAdapter extends RecyclerView.Adapter<TheatersByVenueAdapter.MyViewHolder>  {

    Context context;
    MovieShowTimesTheatersData movieByTheaterData;
    ArrayList<String> subTheatersArray;
    String movieImage, runTime, checkAdults;
    ArrayList<String> genre;

    public TheatersByVenueAdapter(Context context, ArrayList<String> subTheatersArray , MovieShowTimesTheatersData movieByTheaterData,
                                  String movieImage, String runTime, String checkAdults, ArrayList<String> genre) {
        this.context = context;
        this.subTheatersArray = subTheatersArray;
        this.movieByTheaterData = movieByTheaterData;
        this.movieImage = movieImage;
        this.runTime = runTime;
        this.checkAdults = checkAdults;
        this.genre = genre;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView theaterNameIv;
        public TextView subTheaterNameTv;
        public RecyclerView recyclerViewShowTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            theaterNameIv = itemView.findViewById(R.id.theaterNameIv);
            subTheaterNameTv = itemView.findViewById(R.id.subTheaterNameTv);
            recyclerViewShowTime = itemView.findViewById(R.id.recycler_view);

            recyclerViewShowTime.setOnFlingListener(new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {
                    recyclerViewShowTime.dispatchNestedFling(velocityX, velocityY, false);
                    return false;
                }
            });

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 3);
            recyclerViewShowTime.setLayoutManager(mLayoutManager);
            recyclerViewShowTime.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
            recyclerViewShowTime.setItemAnimator(new DefaultItemAnimator());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.theater_by_venue_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        try {

            String subTheaterArray = subTheatersArray.get(i);

            JSONObject getSubTheaterJSONobj = new JSONObject(subTheaterArray);

            String getExperienceName = getSubTheaterJSONobj.getString("experience_name").toLowerCase();

            if (getExperienceName.equals("atmos")) {

                holder.subTheaterNameTv.setVisibility(View.GONE);
                holder.theaterNameIv.setImageResource(R.drawable.atmos);

            } else if(getExperienceName.equals("digital 2d")) {

                holder.subTheaterNameTv.setVisibility(View.GONE);
                holder.theaterNameIv.setImageResource(R.drawable.digital_2d);

            }  else if(getExperienceName.equals("digital 3d")) {

                holder.subTheaterNameTv.setVisibility(View.GONE);
                holder.theaterNameIv.setImageResource(R.drawable.digital_3d);

            } else if(getExperienceName.equals("gold class")) {

                holder.subTheaterNameTv.setVisibility(View.GONE);
                holder.theaterNameIv.setImageResource(R.drawable.gold_class);
            } else {

                holder.theaterNameIv.setVisibility(View.GONE);
                holder.subTheaterNameTv.setText(getSubTheaterJSONobj.getString("experience_name"));

            }

            JSONArray showTimesArray = getSubTheaterJSONobj.getJSONArray("showtimes");

            String experienceName = getSubTheaterJSONobj.getString("experience_name");

            ShowTimeAdapter adapter = new ShowTimeAdapter(context, showTimesArray, movieByTheaterData,
                    movieImage, runTime, checkAdults, genre, experienceName);
            holder.recyclerViewShowTime.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return subTheatersArray.size();
    }






    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
