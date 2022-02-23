package com.ydbm.models;

import java.util.ArrayList;

public class BookingListWithType {
    ArrayList<BookingDetails> models;
    String type;
    String bookingDate;

    public ArrayList<BookingDetails> getModels() {
        return models;
    }

    public void setModels(ArrayList<BookingDetails> models) {
        this.models = models;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
}
