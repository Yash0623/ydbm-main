package com.ydbm.models;

import java.util.ArrayList;
import java.util.HashMap;

public class InventoryModel {
    ArrayList<HashMap<String,String>> totalRoomTypes;
    int totalBookedRoom;
    String checkInDate;
    int availableRooms;

    public ArrayList<HashMap<String, String>> getTotalRoomTypes() {
        return totalRoomTypes;
    }

    public void setTotalRoomTypes(ArrayList<HashMap<String, String>> totalRoomTypes) {
        this.totalRoomTypes = totalRoomTypes;
    }

    public int getTotalBookedRoom() {
        return totalBookedRoom;
    }

    public void setTotalBookedRoom(int totalBookedRoom) {
        this.totalBookedRoom = totalBookedRoom;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }
}
