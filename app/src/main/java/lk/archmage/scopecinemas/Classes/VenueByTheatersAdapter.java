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
import android.widget.TextView;

import java.util.ArrayList;

import lk.archmage.scopecinemas.Models.MovieShowTimesTheatersData;
import lk.archmage.scopecinemas.Models.NowShowingMovieData;
import lk.archmage.scopecinemas.R;
import lk.archmage.scopecinemas.SelectAShowTime;

public class VenueByTheatersAdapter extends RecyclerView.Adapter<VenueByTheatersAdapter.MyViewHolder> {

    Context context;
    ArrayList<MovieShowTimesTheatersData> showTimeArray;
    String movieImage, runTime, checkAdults;
    ArrayList<String> genre;

    public VenueByTheatersAdapter(Context context, ArrayList<MovieShowTimesTheatersData> movieShowTimeArray,
                                  String movieImage, String runTime, String checkAdults, ArrayList<String> genre) {
        this.context = context;
        this.showTimeArray = movieShowTimeArray;
        this.movieImage = movieImage;
        this.runTime = runTime;
        this.checkAdults = checkAdults;
        this.genre = genre;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView venueNameTv;
        public RecyclerView recyclerViewTheaterImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            venueNameTv = itemView.findViewById(R.id.venueNameTv);

            recyclerViewTheaterImage = itemView.findViewById(R.id.recycler_view);

            recyclerViewTheaterImage.setOnFlingListener(new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {
                    recyclerViewTheaterImage.dispatchNestedFling(velocityX, velocityY, false);
                    return false;
                }
            });

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
            recyclerViewTheaterImage.setLayoutManager(mLayoutManager);
            recyclerViewTheaterImage.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
            recyclerViewTheaterImage.setItemAnimator(new DefaultItemAnimator());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.venu_by_theaters_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        MovieShowTimesTheatersData movieShowTimeData = showTimeArray.get(i);

        holder.venueNameTv.setText(movieShowTimeData.getTheaterName());

        TheatersByVenueAdapter adapter = new TheatersByVenueAdapter(context, movieShowTimeData.getExperinces(), movieShowTimeData,
                movieImage, runTime, checkAdults, genre);
        holder.recyclerViewTheaterImage.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return showTimeArray.size();
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
