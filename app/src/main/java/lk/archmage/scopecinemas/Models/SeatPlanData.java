package lk.archmage.scopecinemas.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class SeatPlanData implements Serializable {

    private ArrayList<String> seats;
    private ArrayList<String> rowNumbers;
    private String status;
    private String error;
    private int sessionId;
    private String transactionId;
    private int rowCount;
    private int colCount;
    private ArrayList<String> allSessionsSameday;

    public SeatPlanData(ArrayList<String> seats, ArrayList<String> rowNumbers, String status,
                        String error, int sessionId, String transactionId, int rowCount, int colCount,
                        ArrayList<String> allSessionsSameday, ArrayList<String> prices) {
        this.seats = seats;
        this.rowNumbers = rowNumbers;
        this.status = status;
        this.error = error;
        this.sessionId = sessionId;
        this.transactionId = transactionId;
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.allSessionsSameday = allSessionsSameday;
        this.prices = prices;
    }

    private ArrayList<String> prices;


    public ArrayList<String> getSeats() {
        return seats;
    }

    public void setSeats(ArrayList<String> seats) {
        this.seats = seats;
    }

    public ArrayList<String> getRowNumbers() {
        return rowNumbers;
    }

    public void setRowNumbers(ArrayList<String> rowNumbers) {
        this.rowNumbers = rowNumbers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColCount() {
        return colCount;
    }

    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    public ArrayList<String> getAllSessionsSameday() {
        return allSessionsSameday;
    }

    public void setAllSessionsSameday(ArrayList<String> allSessionsSameday) {
        this.allSessionsSameday = allSessionsSameday;
    }

    public ArrayList<String> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<String> prices) {
        this.prices = prices;
    }
}
