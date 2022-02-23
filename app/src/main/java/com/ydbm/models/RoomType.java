package com.ydbm.models;

public class RoomType {
    String roomTypNm;
    boolean selected;
    String room_id;
    String room_did;


    public RoomType(String roomTypNm, boolean selected) {
        this.roomTypNm = roomTypNm;
        this.selected = selected;
    }

    public RoomType() {
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_did() {
        return room_did;
    }

    public void setRoom_did(String room_did) {
        this.room_did = room_did;
    }

    public String getRoomTypNm() {
        return roomTypNm;
    }

    public void setRoomTypNm(String roomTypNm) {
        this.roomTypNm = roomTypNm;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return this.roomTypNm;            // What to display in the Spinner list.
    }
}
