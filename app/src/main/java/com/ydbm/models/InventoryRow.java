package com.ydbm.models;

public class InventoryRow {
    String roomType,roomId;
    String avaiable,occupied;
    String key;
    int countBooked;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getAvaiable() {
        return avaiable;
    }

    public void setAvaiable(String avaiable) {
        this.avaiable = avaiable;
    }

    public String getOccupied() {
        return occupied;
    }

    public void setOccupied(String occupied) {
        this.occupied = occupied;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getCountBooked() {

        return countBooked;
    }

    public void setCountBooked(int countBooked) {
        this.countBooked = countBooked;
    }
}
