package lk.archmage.scopecinemas.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class TheaterByMovieData implements Parcelable {

    private String theaterId;
    private String theaterName;
    private String theaterAddress;
    private String movieId;
    private String movieName;
    private String movieDescription;
    private String runTime;
    private String imdb;
    private String showTimeDate;
    private String movieImage;
    private ArrayList<String> showTimeData;

    public TheaterByMovieData(String theaterId, String theaterName, String theaterAddress,
                              String movieId, String movieName, String movieDescription, String runTime,
                              String imdb, String showTimeDate, String movieImage, ArrayList<String> showTimeData, String checkAdults) {
        this.theaterId = theaterId;
        this.theaterName = theaterName;
        this.theaterAddress = theaterAddress;
        this.movieId = movieId;
        this.movieName = movieName;
        this.movieDescription = movieDescription;
        this.runTime = runTime;
        this.imdb = imdb;
        this.showTimeDate = showTimeDate;
        this.movieImage = movieImage;
        this.showTimeData = showTimeData;
        this.checkAdults = checkAdults;
    }

    private String checkAdults;

    protected TheaterByMovieData(Parcel in) {
        theaterId = in.readString();
        theaterName = in.readString();
        theaterAddress = in.readString();
        movieId = in.readString();
        movieName = in.readString();
        movieDescription = in.readString();
        runTime = in.readString();
        imdb = in.readString();
        showTimeDate = in.readString();
        movieImage = in.readString();
        showTimeData = in.createStringArrayList();
        checkAdults = in.readString();
    }

    public static final Creator<TheaterByMovieData> CREATOR = new Creator<TheaterByMovieData>() {
        @Override
        public TheaterByMovieData createFromParcel(Parcel in) {
            return new TheaterByMovieData(in);
        }

        @Override
        public TheaterByMovieData[] newArray(int size) {
            return new TheaterByMovieData[size];
        }
    };

    public String getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(String theaterId) {
        this.theaterId = theaterId;
    }

    public String getTheaterName() {
        return theaterName;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    public String getTheaterAddress() {
        return theaterAddress;
    }

    public void setTheaterAddress(String theaterAddress) {
        this.theaterAddress = theaterAddress;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public void setMovieDescription(String movieDescription) {
        this.movieDescription = movieDescription;
    }

    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public String getShowTimeDate() {
        return showTimeDate;
    }

    public void setShowTimeDate(String showTimeDate) {
        this.showTimeDate = showTimeDate;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public ArrayList<String> getShowTimeData() {
        return showTimeData;
    }

    public void setShowTimeData(ArrayList<String> showTimeData) {
        this.showTimeData = showTimeData;
    }

    public String getCheckAdults() {
        return checkAdults;
    }

    public void setCheckAdults(String checkAdults) {
        this.checkAdults = checkAdults;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(theaterId);
        dest.writeString(theaterName);
        dest.writeString(theaterAddress);
        dest.writeString(movieId);
        dest.writeString(movieName);
        dest.writeString(movieDescription);
        dest.writeString(runTime);
        dest.writeString(imdb);
        dest.writeString(showTimeDate);
        dest.writeString(movieImage);
        dest.writeStringList(showTimeData);
        dest.writeString(checkAdults);
    }
}
