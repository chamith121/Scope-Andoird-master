package lk.archmage.scopecinemas.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class MovieShowTimesTheatersData implements Serializable {


    private String theaterName;
    private String theaterCity;
    private String movieId;
    private String theaterId;
    private String movieName;
    private String showTimeDate;
    private String bookingEndDate;
    private String theaterImage;
    private String theaterMobileImage;
    private ArrayList<String> experinces;
    private boolean isFavorite;
    private String tVistaCode;

    public MovieShowTimesTheatersData(String theaterName, String theaterCity, String movieId, String theaterId,
                                      String movieName, String showTimeDate, String bookingEndDate, String theaterImage,
                                      String theaterMobileImage, ArrayList<String> experinces, boolean isFavorite, String tVistaCode) {
        this.theaterName = theaterName;
        this.theaterCity = theaterCity;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.movieName = movieName;
        this.showTimeDate = showTimeDate;
        this.bookingEndDate = bookingEndDate;
        this.theaterImage = theaterImage;
        this.theaterMobileImage = theaterMobileImage;
        this.experinces = experinces;
        this.isFavorite = isFavorite;
        this.tVistaCode = tVistaCode;
    }


    public String getTheaterName() {
        return theaterName;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    public String getTheaterCity() {
        return theaterCity;
    }

    public void setTheaterCity(String theaterCity) {
        this.theaterCity = theaterCity;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(String theaterId) {
        this.theaterId = theaterId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getShowTimeDate() {
        return showTimeDate;
    }

    public void setShowTimeDate(String showTimeDate) {
        this.showTimeDate = showTimeDate;
    }

    public String getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(String bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public String getTheaterImage() {
        return theaterImage;
    }

    public void setTheaterImage(String theaterImage) {
        this.theaterImage = theaterImage;
    }

    public String getTheaterMobileImage() {
        return theaterMobileImage;
    }

    public void setTheaterMobileImage(String theaterMobileImage) {
        this.theaterMobileImage = theaterMobileImage;
    }

    public ArrayList<String> getExperinces() {
        return experinces;
    }

    public void setExperinces(ArrayList<String> experinces) {
        this.experinces = experinces;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String gettVistaCode() {
        return tVistaCode;
    }

    public void settVistaCode(String tVistaCode) {
        this.tVistaCode = tVistaCode;
    }
}
