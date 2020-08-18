package lk.archmage.scopecinemas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;

public class OpenYoutube extends AppCompatActivity {

    YouTubePlayerView youTubePlayerView;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_youtube);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        youTubePlayerView = findViewById(R.id.youtube_player_view);

        Bundle bundle = getIntent().getExtras();

        String youTubeLink = bundle.get("linkYouTube").toString();

        if(youTubeLink != null && !youTubeLink.equals("")) {


            final String getIdUrl = youTubeLink.substring(youTubeLink.length() - 11);

            youTubePlayerView.initialize(new YouTubePlayerInitListener() {
                @Override
                public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                    initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady() {
                            // String videoId = "W4hTJybfU7s";
                            // initializedYouTubePlayer.loadVideo(videoId,0);
                            initializedYouTubePlayer.cueVideo(getIdUrl, 0);
                            //pause();
                        }


                    });
                }
            }, true);


        }
    }
}
