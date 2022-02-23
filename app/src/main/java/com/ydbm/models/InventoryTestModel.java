package com.ydbm.models;

import java.util.HashMap;

public class InventoryTestModel {
    HashMap<String, Integer> rmDetail;
    String checkin;

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public HashMap<String, Integer> getRmDetail() {
        return rmDetail;
    }

    public void setRmDetail(HashMap<String, Integer> rmDetail) {
        this.rmDetail = rmDetail;
    }
}
