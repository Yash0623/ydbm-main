package com.ydbm.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class BookingDetails implements Parcelable {
    private String ds_id, ds_name, ds_img;
    private String is_24Hours, room_id;
    private boolean isNewArrived;
    private String reply, suggestion, extraInstruction, bookingRoomsType, bookingBookedOn;
    private String extra_mattress_status;
    private String extra_mattress_label;
    private String extra_mattress_price;
    private String extra_mattress_convenience;
    private String total_mattress_price;
    private String no_of_mattress;

    ArrayList<RoomType> arrayListRooms;
    ArrayList<String> arrayTotalCheckins;
    ArrayList<RoomType> arryRoomTypes;

    ArrayList<HashMap<String, String>> roomList;

    private String bookingId,offlineRooms, yatriNm, checkinDt, checkoutDt, noOfRooms, person, roomTyp, neftNo, tranferDt, nights, contribution, bookingTotal, expectedCheckinTime, yatriMobile, yatriAddress, tranferAmt, bookingStatus, bookingConfirmedBy;

    public BookingDetails() {
    }

    public String getOfflineRooms() {
        return offlineRooms;
    }

    public void setOfflineRooms(String offlineRooms) {
        this.offlineRooms = offlineRooms;
    }

    public String getDs_id() {
        return ds_id;
    }

    public void setDs_id(String ds_id) {
        this.ds_id = ds_id;
    }

    public String getDs_name() {
        return ds_name;
    }

    public void setDs_name(String ds_name) {
        this.ds_name = ds_name;
    }

    public String getDs_img() {
        return ds_img;
    }

    public void setDs_img(String ds_img) {
        this.ds_img = ds_img;
    }

    public String getIs_24Hours() {
        return is_24Hours;
    }

    public void setIs_24Hours(String is_24Hours) {
        this.is_24Hours = is_24Hours;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public boolean isNewArrived() {
        return isNewArrived;
    }

    public void setNewArrived(boolean newArrived) {
        isNewArrived = newArrived;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getExtraInstruction() {
        return extraInstruction;
    }

    public void setExtraInstruction(String extraInstruction) {
        this.extraInstruction = extraInstruction;
    }

    public String getBookingRoomsType() {
        return bookingRoomsType;
    }

    public void setBookingRoomsType(String bookingRoomsType) {
        this.bookingRoomsType = bookingRoomsType;
    }

    public String getBookingBookedOn() {
        return bookingBookedOn;
    }

    public void setBookingBookedOn(String bookingBookedOn) {
        this.bookingBookedOn = bookingBookedOn;
    }

    public ArrayList<RoomType> getArrayListRooms() {
        return arrayListRooms;
    }

    public void setArrayListRooms(ArrayList<RoomType> arrayListRooms) {
        this.arrayListRooms = arrayListRooms;
    }

    public ArrayList<String> getArrayTotalCheckins() {
        return arrayTotalCheckins;
    }

    public void setArrayTotalCheckins(ArrayList<String> arrayTotalCheckins) {
        this.arrayTotalCheckins = arrayTotalCheckins;
    }

    public ArrayList<RoomType> getArryRoomTypes() {
        return arryRoomTypes;
    }

    public void setArryRoomTypes(ArrayList<RoomType> arryRoomTypes) {
        this.arryRoomTypes = arryRoomTypes;
    }

    public ArrayList<HashMap<String, String>> getRoomList() {
        return roomList;
    }

    public void setRoomList(ArrayList<HashMap<String, String>> roomList) {
        this.roomList = roomList;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getYatriNm() {
        return yatriNm;
    }

    public void setYatriNm(String yatriNm) {
        this.yatriNm = yatriNm;
    }

    public String getCheckinDt() {
        return checkinDt;
    }

    public void setCheckinDt(String checkinDt) {
        this.checkinDt = checkinDt;
    }

    public String getCheckoutDt() {
        return checkoutDt;
    }

    public void setCheckoutDt(String checkoutDt) {
        this.checkoutDt = checkoutDt;
    }

    public String getNoOfRooms() {
        return noOfRooms;
    }

    public void setNoOfRooms(String noOfRooms) {
        this.noOfRooms = noOfRooms;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getRoomTyp() {
        return roomTyp;
    }

    public void setRoomTyp(String roomTyp) {
        this.roomTyp = roomTyp;
    }

    public String getNeftNo() {
        return neftNo;
    }

    public void setNeftNo(String neftNo) {
        this.neftNo = neftNo;
    }

    public String getTranferDt() {
        return tranferDt;
    }

    public void setTranferDt(String tranferDt) {
        this.tranferDt = tranferDt;
    }

    public String getNights() {
        return nights;
    }

    public void setNights(String nights) {
        this.nights = nights;
    }

    public String getContribution() {
        return contribution;
    }

    public void setContribution(String contribution) {
        this.contribution = contribution;
    }

    public String getBookingTotal() {
        return bookingTotal;
    }

    public void setBookingTotal(String bookingTotal) {
        this.bookingTotal = bookingTotal;
    }

    public String getExpectedCheckinTime() {
        return expectedCheckinTime;
    }

    public void setExpectedCheckinTime(String expectedCheckinTime) {
        this.expectedCheckinTime = expectedCheckinTime;
    }

    public String getYatriMobile() {
        return yatriMobile;
    }

    public void setYatriMobile(String yatriMobile) {
        this.yatriMobile = yatriMobile;
    }

    public String getYatriAddress() {
        return yatriAddress;
    }

    public void setYatriAddress(String yatriAddress) {
        this.yatriAddress = yatriAddress;
    }

    public String getTranferAmt() {
        return tranferAmt;
    }

    public void setTranferAmt(String tranferAmt) {
        this.tranferAmt = tranferAmt;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getBookingConfirmedBy() {
        return bookingConfirmedBy;
    }

    public void setBookingConfirmedBy(String bookingConfirmedBy) {
        this.bookingConfirmedBy = bookingConfirmedBy;
    }

    public String getExtra_mattress_status() {
        return extra_mattress_status;
    }

    public void setExtra_mattress_status(String extra_mattress_status) {
        this.extra_mattress_status = extra_mattress_status;
    }

    public String getExtra_mattress_label() {
        return extra_mattress_label;
    }

    public void setExtra_mattress_label(String extra_mattress_label) {
        this.extra_mattress_label = extra_mattress_label;
    }

    public String getExtra_mattress_price() {
        return extra_mattress_price;
    }

    public void setExtra_mattress_price(String extra_mattress_price) {
        this.extra_mattress_price = extra_mattress_price;
    }

    public String getExtra_mattress_convenience() {
        return extra_mattress_convenience;
    }

    public void setExtra_mattress_convenience(String extra_mattress_convenience) {
        this.extra_mattress_convenience = extra_mattress_convenience;
    }

    public String getTotal_mattress_price() {
        return total_mattress_price;
    }

    public void setTotal_mattress_price(String total_mattress_price) {
        this.total_mattress_price = total_mattress_price;
    }

    public String getNo_of_mattress() {
        return no_of_mattress;
    }

    public void setNo_of_mattress(String no_of_mattress) {
        this.no_of_mattress = no_of_mattress;
    }

    public static Creator<BookingDetails> getCREATOR() {
        return CREATOR;
    }

    protected BookingDetails(Parcel in) {
        ds_id = in.readString();
        ds_name = in.readString();
        ds_img = in.readString();
        is_24Hours = in.readString();
        room_id = in.readString();
        isNewArrived = in.readByte() != 0;
        reply = in.readString();
        suggestion = in.readString();
        extraInstruction = in.readString();
        bookingRoomsType = in.readString();
        bookingBookedOn = in.readString();
        arrayTotalCheckins = in.createStringArrayList();
        bookingId = in.readString();
        yatriNm = in.readString();
        checkinDt = in.readString();
        checkoutDt = in.readString();
        noOfRooms = in.readString();
        person = in.readString();
        roomTyp = in.readString();
        neftNo = in.readString();
        tranferDt = in.readString();
        nights = in.readString();
        contribution = in.readString();
        bookingTotal = in.readString();
        expectedCheckinTime = in.readString();
        yatriMobile = in.readString();
        yatriAddress = in.readString();
        tranferAmt = in.readString();
        bookingStatus = in.readString();
        bookingConfirmedBy = in.readString();
        extra_mattress_status = in.readString();
        extra_mattress_label = in.readString();
        extra_mattress_price = in.readString();
        extra_mattress_convenience = in.readString();
        total_mattress_price = in.readString();
        no_of_mattress = in.readString();
    }

    public static final Creator<BookingDetails> CREATOR = new Creator<BookingDetails>() {
        @Override
        public BookingDetails createFromParcel(Parcel in) {
            return new BookingDetails(in);
        }

        @Override
        public BookingDetails[] newArray(int size) {
            return new BookingDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ds_id);
        dest.writeString(ds_name);
        dest.writeString(ds_img);
        dest.writeString(is_24Hours);
        dest.writeString(room_id);
        dest.writeByte((byte) (isNewArrived ? 1 : 0));
        dest.writeString(reply);
        dest.writeString(suggestion);
        dest.writeString(extraInstruction);
        dest.writeString(bookingRoomsType);
        dest.writeString(bookingBookedOn);
        dest.writeStringList(arrayTotalCheckins);
        dest.writeString(bookingId);
        dest.writeString(yatriNm);
        dest.writeString(checkinDt);
        dest.writeString(checkoutDt);
        dest.writeString(noOfRooms);
        dest.writeString(person);
        dest.writeString(roomTyp);
        dest.writeString(neftNo);
        dest.writeString(tranferDt);
        dest.writeString(nights);
        dest.writeString(contribution);
        dest.writeString(bookingTotal);
        dest.writeString(expectedCheckinTime);
        dest.writeString(yatriMobile);
        dest.writeString(yatriAddress);
        dest.writeString(tranferAmt);
        dest.writeString(bookingStatus);
        dest.writeString(bookingConfirmedBy);
        dest.writeString(extra_mattress_status);
        dest.writeString(extra_mattress_label);
        dest.writeString(extra_mattress_price);
        dest.writeString(extra_mattress_convenience);
        dest.writeString(total_mattress_price);
        dest.writeString(no_of_mattress);
    }
}