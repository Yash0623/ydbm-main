
package com.ydbm.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@IgnoreExtraProperties
public class User implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("ds_name")
    @Expose
    private String ds_name = "";

    @SerializedName("d_id")
    @Expose
    private String d_id = "";

    @SerializedName("profile_pic_url")
    @Expose
    private String profilePicUrl = "";
    @SerializedName("last_seen")
    @Expose
    private String lastSeen = "";
    @SerializedName("is_typing")
    @Expose
    private Boolean isTyping = false;
    @SerializedName("status")
    @Expose
    private String status = "";

    @SerializedName("uid")
    @Expose
    private String uid = "";

    @SerializedName("groupIds")
    ArrayList<String> groupIds;

    @SerializedName("fcm_id")
    @Expose
    private String fcm_id = "";




    @SerializedName("username")
    @Expose
    private String username = "";

    @SerializedName("email")
    @Expose
    private String email = "";
    @SerializedName("designation")
    @Expose
    private String designation = "";
    @SerializedName("usertype")
    private String creationTime = "";
    @SerializedName("creationTime")
    @Expose
    private String usertype = "0";
    public User() {

    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getDs_name() {
        return ds_name;
    }

    public void setDs_name(String ds_name) {
        this.ds_name = ds_name;
    }

    public String getD_id() {
        return d_id;
    }

    public void setD_id(String d_id) {
        this.d_id = d_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Boolean getTyping() {
        return isTyping;
    }

    public void setTyping(Boolean typing) {
        isTyping = typing;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(ArrayList<String> groupIds) {
        this.groupIds = groupIds;
    }

    public String getFcm_id() {
        return fcm_id;
    }

    public void setFcm_id(String fcm_id) {
        this.fcm_id = fcm_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    protected User(Parcel in) {
        name = in.readString();
        profilePicUrl = in.readString();
        lastSeen = in.readString();
        byte tmpIsTyping = in.readByte();
        isTyping = tmpIsTyping == 0 ? null : tmpIsTyping == 1;
        status = in.readString();
        uid = in.readString();
        groupIds = in.createStringArrayList();
        fcm_id = in.readString();
        username = in.readString();
        email = in.readString();
        designation = in.readString();
        usertype = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(profilePicUrl);
        parcel.writeString(lastSeen);
        parcel.writeByte((byte) (isTyping == null ? 0 : isTyping ? 1 : 2));
        parcel.writeString(status);
        parcel.writeString(uid);
        parcel.writeStringList(groupIds);
        parcel.writeString(fcm_id);
        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(designation);
        parcel.writeString(usertype);
    }
}
