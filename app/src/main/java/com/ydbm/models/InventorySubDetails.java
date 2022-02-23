package com.ydbm.models;

import java.util.HashMap;

public class InventorySubDetails {
    int totalcount;
    String roomId;
    String checkIn;
    HashMap<String, Integer> rmDetail;

    public int getTotalcount() {
        return totalcount;
    }

    public HashMap<String, Integer> getRmDetail() {
        return rmDetail;
    }

    public void setRmDetail(HashMap<String, Integer> rmDetail) {
        this.rmDetail = rmDetail;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }
}
