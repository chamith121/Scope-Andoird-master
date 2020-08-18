package lk.archmage.scopecinemas.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class ReservationsData implements Serializable {

    private String reservationId;
    private int createdAt;
    private int updatedAt;
    private String name;
    private String email;
    private String mobile;
    private String description;
    private int method;
    private int paymentType;
    private String selesChannel;
    private String status;
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
    private ArrayList<ConcessionData> selectedFoodItems = new ArrayList<ConcessionData>();
    private String filmId;
    private String filmName;
    private String locationId;
    private String locationName;
    private int screenNumber;
    private String screenName;
    private String date;
    private int dateTimestamp;
    private String time;
    private String showTime;
    private String successIndicator;
    private String sessionVersion;
    private String experience;
    private String staff;
    private String user;
    private String movieImage;
    private String mobileImage;
    private String bannerImage;
    private ArrayList<String> genre;
    private ArrayList<String> foodItem;

    public ReservationsData(String reservationId, int createdAt, int updatedAt, String name, String email, String mobile,
                            String description, int method, int paymentType, String selesChannel, String status, String transactionId,
                            int bookingId, String stringBookingId, String cinemaId, int sessionId, int totalTickets, String amount, String ticketAmount,
                            String foodAmount, ArrayList<String> types, String selectedSeats,
                            String filmId, String filmName, String locationId, String locationName, int screenNumber, String screenName,
                            String date, int dateTimestamp, String time, String showTime, String successIndicator, String sessionVersion,
                            String experience, String staff, String user, String movieImage, String mobileImage, String bannerImage, ArrayList<String> genre, ArrayList<String> foodItem) {
        this.reservationId = reservationId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.description = description;
        this.method = method;
        this.paymentType = paymentType;
        this.selesChannel = selesChannel;
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

        this.filmId = filmId;
        this.filmName = filmName;
        this.locationId = locationId;
        this.locationName = locationName;
        this.screenNumber = screenNumber;
        this.screenName = screenName;
        this.date = date;
        this.dateTimestamp = dateTimestamp;
        this.time = time;
        this.showTime = showTime;
        this.successIndicator = successIndicator;
        this.sessionVersion = sessionVersion;
        this.experience = experience;
        this.staff = staff;
        this.user = user;
        this.movieImage = movieImage;
        this.mobileImage = mobileImage;
        this.bannerImage = bannerImage;
        this.genre = genre;
        this.foodItem = foodItem;
    }


    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

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

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public String getSelesChannel() {
        return selesChannel;
    }

    public void setSelesChannel(String selesChannel) {
        this.selesChannel = selesChannel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public ArrayList<ConcessionData> getSelectedFoodItems() {
        return selectedFoodItems;
    }

    public void setSelectedFoodItems(ArrayList<ConcessionData> selectedFoodItems) {
        this.selectedFoodItems = selectedFoodItems;
    }

    public String getFilmId() {
        return filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getScreenNumber() {
        return screenNumber;
    }

    public void setScreenNumber(int screenNumber) {
        this.screenNumber = screenNumber;
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

    public int getDateTimestamp() {
        return dateTimestamp;
    }

    public void setDateTimestamp(int dateTimestamp) {
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

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }



    public ArrayList<String> getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(ArrayList<String> foodItem) {
        this.foodItem = foodItem;
    }

}
