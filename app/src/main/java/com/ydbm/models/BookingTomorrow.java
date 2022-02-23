package com.ydbm.models;

import java.util.ArrayList;

public class BookingTomorrow {
    ArrayList<BookingDetails> models;
    String type;

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
}
