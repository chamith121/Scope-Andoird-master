package lk.archmage.scopecinemas.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class NowShowingMovieData implements Parcelable {

    private String movieId;
    private String movieName;
    private String startDate;
    private String endDate;
    private String youTubeUrl;
    private String youTubeId;
    private String runTime;
    private String movieLanguage;
    private String movieType;
    private String movieSynopsis;
    private String movieImage;
    private String movieLImage;
    private String movieMobileImage;
    private String trailer;
    private ArrayList<String> castObject;
    private String imdb;
    private String checkAdults;
    private ArrayList<String> vistaCode;
    private ArrayList<String> genre;
    private String fact;
    private String featured;
    private ArrayList<String> directors;
    private ArrayList<String> producers;
    private ArrayList<String> musicians;
    private ArrayList<String> writters;

    public NowShowingMovieData(String movieId, String movieName, String startDate, String endDate, String youTubeUrl,
                               String youTubeId, String runTime, String movieLanguage, String movieType,
                               String movieSynopsis, String movieImage, String movieLImage, String movieMobileImage,
                               String trailer, ArrayList<String> castObject, String imdb, String checkAdults,
                               ArrayList<String> vistaCode, ArrayList<String> genre, String fact, String featured,
                               ArrayList<String> directors, ArrayList<String> producers, ArrayList<String> musicians, ArrayList<String> writters) {
        this.setMovieId(movieId);
        this.setMovieName(movieName);
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setYouTubeUrl(youTubeUrl);
        this.setYouTubeId(youTubeId);
        this.setRunTime(runTime);
        this.setMovieLanguage(movieLanguage);
        this.setMovieType(movieType);
        this.setMovieSynopsis(movieSynopsis);
        this.setMovieImage(movieImage);
        this.setMovieLImage(movieLImage);
        this.setMovieMobileImage(movieMobileImage);
        this.setTrailer(trailer);
        this.setCastObject(castObject);
        this.setImdb(imdb);
        this.setCheckAdults(checkAdults);
        this.setVistaCode(vistaCode);
        this.setGenre(genre);
        this.setFact(fact);
        this.setFeatured(featured);
        this.setDirectors(directors);
        this.setProducers(producers);
        this.setMusicians(musicians);
        this.setWritters(writters);
    }

    protected NowShowingMovieData(Parcel in) {
        movieId = in.readString();
        movieName = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        youTubeUrl = in.readString();
        youTubeId = in.readString();
        runTime = in.readString();
        movieLanguage = in.readString();
        movieType = in.readString();
        movieSynopsis = in.readString();
        movieImage = in.readString();
        movieLImage = in.readString();
        movieMobileImage = in.readString();
        trailer = in.readString();
        castObject = in.createStringArrayList();
        imdb = in.readString();
        checkAdults = in.readString();
        vistaCode = in.createStringArrayList();
        genre = in.createStringArrayList();
        fact = in.readString();
        featured = in.readString();
        directors = in.createStringArrayList();
        producers = in.createStringArrayList();
        musicians = in.createStringArrayList();
        writters = in.createStringArrayList();
    }

    public static final Creator<NowShowingMovieData> CREATOR = new Creator<NowShowingMovieData>() {
        @Override
        public NowShowingMovieData createFromParcel(Parcel in) {
            return new NowShowingMovieData(in);
        }

        @Override
        public NowShowingMovieData[] newArray(int size) {
            return new NowShowingMovieData[size];
        }
    };

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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getYouTubeUrl() {
        return youTubeUrl;
    }

    public void setYouTubeUrl(String youTubeUrl) {
        this.youTubeUrl = youTubeUrl;
    }

    public String getYouTubeId() {
        return youTubeId;
    }

    public void setYouTubeId(String youTubeId) {
        this.youTubeId = youTubeId;
    }

    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public String getMovieLanguage() {
        return movieLanguage;
    }

    public void setMovieLanguage(String movieLanguage) {
        this.movieLanguage = movieLanguage;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    public String getMovieSynopsis() {
        return movieSynopsis;
    }

    public void setMovieSynopsis(String movieSynopsis) {
        this.movieSynopsis = movieSynopsis;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public String getMovieLImage() {
        return movieLImage;
    }

    public void setMovieLImage(String movieLImage) {
        this.movieLImage = movieLImage;
    }

    public String getMovieMobileImage() {
        return movieMobileImage;
    }

    public void setMovieMobileImage(String movieMobileImage) {
        this.movieMobileImage = movieMobileImage;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public ArrayList<String> getCastObject() {
        return castObject;
    }

    public void setCastObject(ArrayList<String> castObject) {
        this.castObject = castObject;
    }

    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public String getCheckAdults() {
        return checkAdults;
    }

    public void setCheckAdults(String checkAdults) {
        this.checkAdults = checkAdults;
    }

    public ArrayList<String> getVistaCode() {
        return vistaCode;
    }

    public void setVistaCode(ArrayList<String> vistaCode) {
        this.vistaCode = vistaCode;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

    public String getFact() {
        return fact;
    }

    public void setFact(String fact) {
        this.fact = fact;
    }

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

    public ArrayList<String> getDirectors() {
        return directors;
    }

    public void setDirectors(ArrayList<String> directors) {
        this.directors = directors;
    }

    public ArrayList<String> getProducers() {
        return producers;
    }

    public void setProducers(ArrayList<String> producers) {
        this.producers = producers;
    }

    public ArrayList<String> getMusicians() {
        return musicians;
    }

    public void setMusicians(ArrayList<String> musicians) {
        this.musicians = musicians;
    }

    public ArrayList<String> getWritters() {
        return writters;
    }

    public void setWritters(ArrayList<String> writters) {
        this.writters = writters;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieId);
        dest.writeString(movieName);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(youTubeUrl);
        dest.writeString(youTubeId);
        dest.writeString(runTime);
        dest.writeString(movieLanguage);
        dest.writeString(movieType);
        dest.writeString(movieSynopsis);
        dest.writeString(movieImage);
        dest.writeString(movieLImage);
        dest.writeString(movieMobileImage);
        dest.writeString(trailer);
        dest.writeStringList(castObject);
        dest.writeString(imdb);
        dest.writeString(checkAdults);
        dest.writeStringList(vistaCode);
        dest.writeStringList(genre);
        dest.writeString(fact);
        dest.writeString(featured);
        dest.writeStringList(directors);
        dest.writeStringList(producers);
        dest.writeStringList(musicians);
        dest.writeStringList(writters);
    }

}
