package lk.archmage.scopecinemas;


import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import lk.archmage.scopecinemas.Classes.ComingSoonMovieAdapter;
import lk.archmage.scopecinemas.Classes.NowShowingMovieAdapter;
import lk.archmage.scopecinemas.Models.ComingSoonMovieData;
import lk.archmage.scopecinemas.Models.NowShowingMovieData;


public class ComingSoon extends Fragment {

    TextView msgTv;
    private RecyclerView recyclerView;
    ComingSoonMovieAdapter adapter;


    public ComingSoon() {
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coming_soon, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        msgTv = view.findViewById(R.id.msgTv);
        recyclerView = view.findViewById(R.id.recycler_viewCSM);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Bundle bundel = getArguments();

        ArrayList<ComingSoonMovieData> comingSoonMovieArray = bundel.getParcelableArrayList("comingSoonMovieList");

        if(comingSoonMovieArray.size() != 0){

            msgTv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new ComingSoonMovieAdapter(getContext(), comingSoonMovieArray);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);

        }else {

            recyclerView.setVisibility(View.GONE);
            msgTv.setVisibility(View.VISIBLE);
        }


        return view;
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
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}
