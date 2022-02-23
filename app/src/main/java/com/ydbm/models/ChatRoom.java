package com.ydbm.models;

import java.io.Serializable;

/**
 * Created by Lincoln on 07/01/16.
 */
public class ChatRoom implements Serializable {
    String id, name, lastMessage, timestamp, otherProfpic, username, room_TYpe, grp_pic, grp_name, grp_members, creation_date, ownerName;
    int unreadCount;
    boolean isBlocked;

    private boolean isSelected;
    String roomActive ="0";

    public String getRoomActive() {
        return roomActive;
    }

    public void setRoomActive(String roomActive) {
        this.roomActive = roomActive;
    }
    public ChatRoom() {
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getGrp_members() {
        return grp_members;
    }

    public void setGrp_members(String grp_members) {
        this.grp_members = grp_members;
    }

    public String getRoom_TYpe() {
        return room_TYpe;
    }

    public String getGrp_pic() {
        return grp_pic;
    }

    public void setGrp_pic(String grp_pic) {
        this.grp_pic = grp_pic;
    }

    public String getGrp_name() {
        return grp_name;
    }

    public void setGrp_name(String grp_name) {
        this.grp_name = grp_name;
    }

    public void setRoom_TYpe(String room_TYpe) {
        this.room_TYpe = room_TYpe;
    }

    public ChatRoom(String id, String name, String lastMessage, String timestamp, int unreadCount) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOtherProfpic() {
        return otherProfpic;
    }

    public void setOtherProfpic(String otherProfpic) {
        this.otherProfpic = otherProfpic;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }
}
