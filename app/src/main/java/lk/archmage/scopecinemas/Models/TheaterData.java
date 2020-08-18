package lk.archmage.scopecinemas.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TheaterData implements Parcelable {

    private String theaterId;
    private String theaterName;
    private String theaterAddress;
    private String theaterDescrip;
    private String tExperiences;
    private String theaterContactNo;
    private String theaterImage;
    private String theaterMobileImage;
    private int isFavorite;
    private String theaterContactNo2;
    private String vistaCode;
    private ArrayList<String> screens;
    private String onlineBooking;
    private String gallery;
    private String iconImage;

    public TheaterData(String theaterId, String theaterName, String theaterAddress, String theaterDescrip,
                       String theaterContactNo, String theaterImage, String theaterMobileImage, int isFavorite,
                       String theaterContactNo2, String vistaCode, ArrayList<String> screens, String onlineBooking,
                       String gallery, String iconImage, String tExperiences) {
        this.theaterId = theaterId;
        this.theaterName = theaterName;
        this.theaterAddress = theaterAddress;
        this.theaterDescrip = theaterDescrip;
        this.theaterContactNo = theaterContactNo;
        this.theaterImage = theaterImage;
        this.theaterMobileImage = theaterMobileImage;
        this.isFavorite = isFavorite;
        this.theaterContactNo2 = theaterContactNo2;
        this.vistaCode = vistaCode;
        this.screens = screens;
        this.onlineBooking = onlineBooking;
        this.gallery = gallery;
        this.iconImage = iconImage;
        this.tExperiences = tExperiences;
    }

    protected TheaterData(Parcel in) {
        theaterId = in.readString();
        theaterName = in.readString();
        theaterAddress = in.readString();
        theaterDescrip = in.readString();
        theaterContactNo = in.readString();
        theaterImage = in.readString();
        theaterMobileImage = in.readString();
        isFavorite = in.readInt();
        theaterContactNo2 = in.readString();
        vistaCode = in.readString();
        screens = in.createStringArrayList();
        onlineBooking = in.readString();
        gallery = in.readString();
        iconImage = in.readString();
        tExperiences = in.readString();
    }

    public static final Creator<TheaterData> CREATOR = new Creator<TheaterData>() {
        @Override
        public TheaterData createFromParcel(Parcel in) {
            return new TheaterData(in);
        }

        @Override
        public TheaterData[] newArray(int size) {
            return new TheaterData[size];
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

    public String getTheaterDescrip() {
        return theaterDescrip;
    }

    public void setTheaterDescrip(String theaterDescrip) {
        this.theaterDescrip = theaterDescrip;
    }

    public String getTheaterContactNo() {
        return theaterContactNo;
    }

    public void setTheaterContactNo(String theaterContactNo) {
        this.theaterContactNo = theaterContactNo;
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

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getTheaterContactNo2() {
        return theaterContactNo2;
    }

    public void setTheaterContactNo2(String theaterContactNo2) {
        this.theaterContactNo2 = theaterContactNo2;
    }

    public String getVistaCode() {
        return vistaCode;
    }

    public void setVistaCode(String vistaCode) {
        this.vistaCode = vistaCode;
    }

    public ArrayList<String> getScreens() {
        return screens;
    }

    public void setScreens(ArrayList<String> screens) {
        this.screens = screens;
    }

    public String getOnlineBooking() {
        return onlineBooking;
    }

    public void setOnlineBooking(String onlineBooking) {
        this.onlineBooking = onlineBooking;
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public String getIconImage() {
        return iconImage;
    }

    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
    }



    public String gettExperiences() {
        return tExperiences;
    }

    public void settExperiences(String tExperiences) {
        this.tExperiences = tExperiences;
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
        dest.writeString(theaterDescrip);
        dest.writeString(theaterContactNo);
        dest.writeString(theaterImage);
        dest.writeString(theaterMobileImage);
        dest.writeInt(isFavorite);
        dest.writeString(theaterContactNo2);
        dest.writeString(vistaCode);
        dest.writeStringList(screens);
        dest.writeString(onlineBooking);
        dest.writeString(gallery);
        dest.writeString(iconImage);
        dest.writeString(tExperiences);
    }
}
