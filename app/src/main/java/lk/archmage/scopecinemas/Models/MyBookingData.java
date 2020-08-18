package lk.archmage.scopecinemas.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class MyBookingData implements Serializable {

    private int createdAt;
    private int updatedAt;
    private String id;
    private String name;
    private String email;
    private String mobile;
    private String description;
    private int method;
    private int payment_type;
    private String salesChannel;
    private int status;
    private String transactionId;
    private int bookingId;
    private String stringBookingId;
    private String cinemaId;
    private int sessionId;
    private int totalTickets;
    private String amount;
    private String ticketAmount;
    private String foodAmount;
    private ArrayList<String> types;
    private String selectedSeats;
    private String filmName;
    private ArrayList<String> filmVistaCode;
    private String locationName;
    private String locationVistaCode;
    private String screenNum;
    private String screenName;
    private String date;
    private String dateTimestamp;
    private String time;
    private String showTime;
    private String successIndicator;
    private String sessionVersion;
    private String experince;
    private String staff;
    private String user;
    private String movieImage;
    private String mobileImage;
    private boolean active;

    public MyBookingData(int createdAt, int updatedAt, String id, String name, String email,
                         String mobile, String description, int method, int payment_type, String salesChannel,
                         int status, String transactionId, int bookingId, String stringBookingId, String cinemaId,
                         int sessionId, int totalTickets, String amount, String ticketAmount, String foodAmount,
                         ArrayList<String> types, String selectedSeats, String filmName, ArrayList<String> filmVistaCode,
                         String locationName, String locationVistaCode, String screenNum, String screenName,
                         String date, String dateTimestamp, String time, String showTime, String successIndicator,
                         String sessionVersion, String experince, String staff, String user, String movieImage, String mobileImage, String bannerImage, boolean active) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.description = description;
        this.method = method;
        this.payment_type = payment_type;
        this.salesChannel = salesChannel;
        this.status = status;
        this.transactionId = transactionId;
        this.bookingId = bookingId;
        this.stringBookingId = stringBookingId;
        this.cinemaId = cinemaId;
        this.sessionId = sessionId;
        this.totalTickets = totalTickets;
        this.amount = amount;
        this.ticketAmount = ticketAmount;
        this.foodAmount = foodAmount;
        this.types = types;
        this.selectedSeats = selectedSeats;
        this.filmName = filmName;
        this.filmVistaCode = filmVistaCode;
        this.locationName = locationName;
        this.locationVistaCode = locationVistaCode;
        this.screenNum = screenNum;
        this.screenName = screenName;
        this.date = date;
        this.dateTimestamp = dateTimestamp;
        this.time = time;
        this.showTime = showTime;
        this.successIndicator = successIndicator;
        this.sessionVersion = sessionVersion;
        this.experince = experince;
        this.staff = staff;
        this.user = user;
        this.movieImage = movieImage;
        this.mobileImage = mobileImage;
        this.bannerImage = bannerImage;
        this.active = active;
    }

    private String bannerImage;

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public int getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(int payment_type) {
        this.payment_type = payment_type;
    }

    public String getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getStringBookingId() {
        return stringBookingId;
    }

    public void setStringBookingId(String stringBookingId) {
        this.stringBookingId = stringBookingId;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTicketAmount() {
        return ticketAmount;
    }

    public void setTicketAmount(String ticketAmount) {
        this.ticketAmount = ticketAmount;
    }

    public String getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(String foodAmount) {
        this.foodAmount = foodAmount;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public String getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(String selectedSeats) {
        this.selectedSeats = selectedSeats;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public ArrayList<String> getFilmVistaCode() {
        return filmVistaCode;
    }

    public void setFilmVistaCode(ArrayList<String> filmVistaCode) {
        this.filmVistaCode = filmVistaCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationVistaCode() {
        return locationVistaCode;
    }

    public void setLocationVistaCode(String locationVistaCode) {
        this.locationVistaCode = locationVistaCode;
    }

    public String getScreenNum() {
        return screenNum;
    }

    public void setScreenNum(String screenNum) {
        this.screenNum = screenNum;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateTimestamp() {
        return dateTimestamp;
    }

    public void setDateTimestamp(String dateTimestamp) {
        this.dateTimestamp = dateTimestamp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getSuccessIndicator() {
        return successIndicator;
    }

    public void setSuccessIndicator(String successIndicator) {
        this.successIndicator = successIndicator;
    }

    public String getSessionVersion() {
        return sessionVersion;
    }

    public void setSessionVersion(String sessionVersion) {
        this.sessionVersion = sessionVersion;
    }

    public String getExperince() {
        return experince;
    }

    public void setExperince(String experince) {
        this.experince = experince;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public String getMobileImage() {
        return mobileImage;
    }

    public void setMobileImage(String mobileImage) {
        this.mobileImage = mobileImage;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


}
