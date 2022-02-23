package com.ydbm.models;

public class NotificationFailed {
    String msg_id , title,body, msg_typ, noti_typ, sender, msgtrtipid, send_to, grp_name, userName;

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMsg_typ() {
        return msg_typ;
    }

    public void setMsg_typ(String msg_typ) {
        this.msg_typ = msg_typ;
    }

    public String getNoti_typ() {
        return noti_typ;
    }

    public void setNoti_typ(String noti_typ) {
        this.noti_typ = noti_typ;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsgtrtipid() {
        return msgtrtipid;
    }

    public void setMsgtrtipid(String msgtrtipid) {
        this.msgtrtipid = msgtrtipid;
    }

    public String getSend_to() {
        return send_to;
    }

    public void setSend_to(String send_to) {
        this.send_to = send_to;
    }

    public String getGrp_name() {
        return grp_name;
    }

    public void setGrp_name(String grp_name) {
        this.grp_name = grp_name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
