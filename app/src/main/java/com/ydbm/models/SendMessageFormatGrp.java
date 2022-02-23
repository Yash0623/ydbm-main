package com.ydbm.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SendMessageFormatGrp implements Parcelable {
    private String userId,message,roomName;

    protected SendMessageFormatGrp(Parcel in) {
        userId = in.readString();
        message = in.readString();
        roomName = in.readString();
    }

    public static final Creator<SendMessageFormatGrp> CREATOR = new Creator<SendMessageFormatGrp>() {
        @Override
        public SendMessageFormatGrp createFromParcel(Parcel in) {
            return new SendMessageFormatGrp(in);
        }

        @Override
        public SendMessageFormatGrp[] newArray(int size) {
            return new SendMessageFormatGrp[size];
        }
    };

    public SendMessageFormatGrp() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(message);
        parcel.writeString(roomName);
    }
}
