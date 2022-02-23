package com.ydbm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ydbm.activities.BookingListingActivity;
import com.ydbm.activities.MainScreen;

import com.ydbm.session.SessionManager;
import com.ydbm.ui.Database;

import com.ydbm.utils.AppConstants;
import com.ydbm.utils.NotificationID;
import com.ydbm.utils.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TOKEN_PREF_NAME = "TOKEN_PREF";
    SQLiteHandler sqLiteHandler;
    SessionManager sessionManager;
    private static NotificationChannel mChannel1;
    FirebaseAuth firebaseAuth;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(final RemoteMessage message) {

        sqLiteHandler = new SQLiteHandler(this);
        firebaseAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);
        Log.d("RecievedMessage123", message.getData().toString());


        final String booking_id = message.getData().get("booking_id");
        final String data = message.getData().get("message");
        String msg_type = message.getData().get("message_type1");
        //String ds_nm = message.getData().get("ds_name");
        if (msg_type != null && booking_id != null) {
            if (message.getData().get("ds_id") != null) {
                //sessionManager.setDharamshala_id(message.getData().get("ds_id"), message.getData().get("ds_name"));
                showNotification(this, booking_id, data, msg_type, message);


            }
        }


        //  mNotificationManager = getSystemService(NotificationManager.class);

        //createNotificationChannel();

        /*
        //sendMyNotification(message.getNotification().getBody());
        //sendMyNotification(message.getNotification().getBody());

        if (message.getData().get("title").equals("deliveryReport")) {
            final String roomId = message.getData().get("body");
            final String msg_id = message.getData().get("msg_id");
            int u = sqLiteHandler.updateMsgData(msg_id, "7");

            if (u > 0) {
                EventBus.getDefault().post("updatechatd" + "," + roomId + "," + msg_id);
            }
        } else if (message.getData().get("title").equals("msgRead")) {
            String roomId = message.getData().get("body");
            Log.d("TEHGSHGSHDGHGDSH", roomId + "======");
            ArrayList<String> msgIDs = sqLiteHandler.getUnReadMsgIds(roomId);

            for (int i = 0; i < msgIDs.size(); i++) {
                ChatMessage chatmessage = sqLiteHandler.fetchSingleMsg(msgIDs.get(i));
                if (chatmessage.getMsgStatus().equals("7")) {
                    int u = sqLiteHandler.updateMsgData(msgIDs.get(i), "9");
                    if (u > 0) {
                        EventBus.getDefault().post("updatechatr" + "," + roomId + "," + msgIDs.get(i));
                    }
                }
            }


        } else if (message.getData().get("title").equals("Yatradham_Message")) {
            final String ds_id = message.getData().get("ds_id");
            final String data = message.getData().get("message");
            final String ds_name = message.getData().get("ds_name");
            final String group_id = message.getData().get("group_id");
            final String[] message_id = {message.getData().get("message_id")};
            final String user_id = message.getData().get("user_id");

            //String typeMsg = message.getData().get("receiver_type");
            // String type = null;
            final String[] roomId = {null};
            final String ts1 = ConvertGMTtoIST.convertISTtoGMTFormat();

            //  if (typeMsg.equalsIgnoreCase("private")) {
            // type = "s";

            if (sessionManager.getRole().equals("0")) {
                roomId[0] = ds_id + user_id;
                if (sqLiteHandler.getRoomDirectExist(roomId[0]) == 0) {
                    Log.d("TEWYGDGHSGHSD", "1");
                    long id_res = -1;
                    id_res = sqLiteHandler.addRoom(roomId[0], firebaseAuth.getUid(), "YatraDham", data, ts1, "", "", "", "", "YatraDham", "", "s", "", ts1, "YatraDham", "");
                    if (id_res > 0) {
                        String lastTimestamp = sqLiteHandler.getLastMessageTimestampFrmRoom(roomId[0]);
                        if (lastTimestamp == null) {
                            Log.d("BJDBJBDJBDD", "2");
                            String firsttimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                            ChatMessage chatMsg = new ChatMessage();
                            chatMsg.setMessageType("chatheader");
                            chatMsg.setMessageTimeNew(firsttimeStamp);
                            sqLiteHandler.addMessage(firsttimeStamp, "", "1", "7", "", "", "", firsttimeStamp, "", "", "", "1", roomId[0], "chat");
                        } else {
                            Log.d("BJDBJBDJBDD", "3");
                            String currentTimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                            if (!ConvertGMTtoIST.getDateFromTt(lastTimestamp).equals(ConvertGMTtoIST.getDateFromTt(currentTimeStamp))) {
                                ChatMessage chatMsg = new ChatMessage();
                                chatMsg.setMessageType("chatheader");
                                chatMsg.setMessageTimeNew(currentTimeStamp);
                                sqLiteHandler.addMessage(currentTimeStamp, "", "1", "7", "", "", "", currentTimeStamp, "", "", "", "1", roomId[0], "chat");
                                EventBus.getDefault().post("updatechats" + "," + currentTimeStamp + "," + roomId[0]);
                            }
                        }
                        if (message_id[0].isEmpty() || message_id[0] == null) {
                            message_id[0] = String.valueOf(System.currentTimeMillis());
                        }

                        long id_res1 = -1;
                        final String ts = ConvertGMTtoIST.convertISTtoGMTFormat();

                        String otherUsername = "YatraDham";
                        try {
                            id_res1 = sqLiteHandler.addMessage(message_id[0], data, "0", "0", "", "", otherUsername, ts, "", "", "", "0", roomId[0], "chat");
                        } catch (SQLiteException e) {
                            ErrorModel errorModel = new ErrorModel();
                            errorModel.setErrMsg(e.getMessage());
                            errorModel.setTime(ConvertGMTtoIST.getCurrentDateTimeLastSeen());
                            Database.NODE_EXCEPTIONS_PATH.child(sessionManager.getUserDetailsOb().getUsername()).push().setValue(errorModel);

                        } catch (Exception e) {
                            ErrorModel errorModel = new ErrorModel();
                            errorModel.setErrMsg(e.getMessage());
                            errorModel.setTime(ConvertGMTtoIST.getCurrentDateTimeLastSeen());
                            Database.NODE_EXCEPTIONS_PATH.child(sessionManager.getUserDetailsOb().getUsername()).push().setValue(errorModel);
                        }

                        if (id_res1 != -1) {
                            String timeStamp1 = ConvertGMTtoIST.convertISTtoGMTFormat();
                            GroupSeen groupSeen = new GroupSeen();
                            groupSeen.setTimeStamp(timeStamp1);
                            groupSeen.setUserName(sessionManager.getUserDetails().get("name"));

                            Database.NODE_MESSAGE_DELIVERED_PATH.child(message_id[0]).child("delivered").push().setValue(groupSeen);

                            sendMsgDeliveredStatus(message_id[0]);

                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setUserType(UserType.OTHER);
                            chatMessage.setMessageType("text_other");
                            chatMessage.setMsgStatus("4");
                            chatMessage.setOtherUsreName(otherUsername);
                            chatMessage.setId(message_id[0]);

                            chatMessage.setMsgType("chat");
                            chatMessage.setMsgRoomId(roomId[0]);
                            chatMessage.setMessageText(data);
                            chatMessage.setReadStatus("0");
                            chatMessage.setMessageTimeNew(ts);

                            // d()java.lang.NullPointerException: Can't pass null for argument 'pathString' in chil
                            Database.NODE_MESSAGES_PATH.child(roomId[0]).child(message_id[0]).setValue(chatMessage);

                            sqLiteHandler.updateRoomData(roomId[0], data, ts, "");
                            EventBus.getDefault().post("addMessages" + "," + roomId[0] + "," + message_id[0]);

                            handleNotification(message, roomId[0], message_id[0], ds_name, "s");

                        }

                    }

                } else {
                    Log.d("TEWYGDGHSGHSD", "2");
                    String lastTimestamp = sqLiteHandler.getLastMessageTimestampFrmRoom(roomId[0]);
                    if (lastTimestamp == null) {
                        Log.d("BJDBJBDJBDD", "2");
                        String firsttimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                        ChatMessage chatMsg = new ChatMessage();
                        chatMsg.setMessageType("chatheader");
                        chatMsg.setMessageTimeNew(firsttimeStamp);

                        sqLiteHandler.addMessage(firsttimeStamp, "", "1", "7", "", "", "", firsttimeStamp, "", "", "", "1", roomId[0], "chat");
                    } else {
                        Log.d("BJDBJBDJBDD", "3");
                        String currentTimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                        if (!ConvertGMTtoIST.getDateFromTt(lastTimestamp).equals(ConvertGMTtoIST.getDateFromTt(currentTimeStamp))) {
                            ChatMessage chatMsg = new ChatMessage();
                            chatMsg.setMessageType("chatheader");
                            chatMsg.setMessageTimeNew(currentTimeStamp);
                            sqLiteHandler.addMessage(currentTimeStamp, "", "1", "7", "", "", "", currentTimeStamp, "", "", "", "1", roomId[0], "chat");
                            EventBus.getDefault().post("updatechats" + "," + currentTimeStamp + "," + roomId[0]);
                        }
                    }
                    Log.d("SJKJSJKJSJSDK", message_id[0] + "==");
                    String messageId;
                    if (message_id[0] == null || message_id[0].isEmpty()) {
                        Log.d("SJKJSJKJSJSDK", "1+");
                        messageId = String.valueOf(System.currentTimeMillis());
                    } else {
                        messageId = message_id[0];
                    }

                    long id_res1 = -1;
                    final String ts = ConvertGMTtoIST.convertISTtoGMTFormat();

                    String otherUsername = "YatraDham";
                    try {
                        id_res1 = sqLiteHandler.addMessage(messageId, data, "0", "0", "", "", otherUsername, ts, "", "", "", "0", roomId[0], "chat");
                    } catch (SQLiteException e) {
                        ErrorModel errorModel = new ErrorModel();
                        errorModel.setErrMsg(e.getMessage());
                        errorModel.setTime(ConvertGMTtoIST.getCurrentDateTimeLastSeen());
                        Database.NODE_EXCEPTIONS_PATH.child(sessionManager.getUserDetailsOb().getUsername()).push().setValue(errorModel);

                    } catch (Exception e) {
                        ErrorModel errorModel = new ErrorModel();
                        errorModel.setErrMsg(e.getMessage());
                        errorModel.setTime(ConvertGMTtoIST.getCurrentDateTimeLastSeen());
                        Database.NODE_EXCEPTIONS_PATH.child(sessionManager.getUserDetailsOb().getUsername()).push().setValue(errorModel);
                    }
                    Log.d("TEWYGDGHSGHSD", "3" + "===" + id_res1 + "===" + roomId[0]);
                    if (id_res1 != -1) {
                        String timeStamp1 = ConvertGMTtoIST.convertISTtoGMTFormat();
                        GroupSeen groupSeen = new GroupSeen();
                        groupSeen.setTimeStamp(timeStamp1);
                        groupSeen.setUserName(sessionManager.getUserDetails().get("name"));

                        Database.NODE_MESSAGE_DELIVERED_PATH.child(message_id[0]).child("delivered").push().setValue(groupSeen);

                        sendMsgDeliveredStatus(messageId);
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setUserType(UserType.OTHER);
                        chatMessage.setMessageType("text_other");
                        chatMessage.setMsgStatus("4");
                        chatMessage.setOtherUsreName(otherUsername);
                        chatMessage.setId(messageId);
                        chatMessage.setMsgType("chat");
                        chatMessage.setMsgRoomId(roomId[0]);
                        chatMessage.setMessageText(data);
                        chatMessage.setReadStatus("0");
                        chatMessage.setMessageTimeNew(ts);

                        Database.NODE_MESSAGES_PATH.child(roomId[0]).child(message_id[0]).setValue(chatMessage);

                        //Database.NODE_INQUIRYCHAT_PATH.child(roomId[0]).child("messages").child(chatMessage.getId()).setValue(chatMessage);


                        sqLiteHandler.updateRoomData(roomId[0], data, ts, "");
                        EventBus.getDefault().post("addMessages" + "," + roomId[0] + "," + message_id[0]);

                        handleNotification(message, roomId[0], message_id[0], ds_name, "s");

                    }


                }
            } else {
                //String roomId1=roomId;
                Log.d("HZDJHJDSHHJSHDHDhj", ds_id + "==");
                roomId[0] = ds_id + user_id;
                Log.d("HZDJHJDSHHJSHDHDhj", roomId[0] + "==");
                if (sqLiteHandler.getRoomDirectExist(roomId[0]) == 0) {
                    if (user_id != null) {
                        Query userQuery = Database.NODE_USERS_PATH.child(user_id);
                        //  final String finalType = type;
                        userQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                if (dataSnapshot1.exists()) {
                                    Log.d("HZDJHJDSHHJSHDHDhj", dataSnapshot1 + "==");

                                    User user = dataSnapshot1.getValue(User.class);
                                    roomId[0] = ds_id + user_id;
                                    String roomName = user.getName();
                                    if (roomName == null) {
                                        roomName = ds_name;
                                    }
                                    Log.d("TEWYGDGHSGHSD", "0" + "===>" + roomId[0] + "===" + roomName);

                                    Log.d("TEWYGDGHSGHSD", "1");
                                    long id_res = -1;
                                    Log.d("HZDJHJDSHHJSHDHDhj", roomId[0] + "==");
                                    id_res = sqLiteHandler.addRoom(roomId[0], firebaseAuth.getUid(), roomName, data, ts1, "", "", "", "", roomName, "", "s", "", ts1, roomName, user_id);
                                    if (id_res > 0) {
                                        String lastTimestamp = sqLiteHandler.getLastMessageTimestampFrmRoom(roomId[0]);
                                        if (lastTimestamp == null) {
                                            Log.d("BJDBJBDJBDD", "2");
                                            String firsttimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                                            ChatMessage chatMsg = new ChatMessage();
                                            chatMsg.setMessageType("chatheader");
                                            chatMsg.setMessageTimeNew(firsttimeStamp);

                                            sqLiteHandler.addMessage(firsttimeStamp, "", "1", "7", "", "", "", firsttimeStamp, "", "", "", "1", roomId[0], "chat");
                                        } else {
                                            Log.d("BJDBJBDJBDD", "3");
                                            String currentTimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                                            if (!ConvertGMTtoIST.getDateFromTt(lastTimestamp).equals(ConvertGMTtoIST.getDateFromTt(currentTimeStamp))) {
                                                ChatMessage chatMsg = new ChatMessage();
                                                chatMsg.setMessageType("chatheader");
                                                chatMsg.setMessageTimeNew(currentTimeStamp);
                                                sqLiteHandler.addMessage(currentTimeStamp, "", "1", "7", "", "", "", currentTimeStamp, "", "", "", "1", roomId[0], "chat");
                                                EventBus.getDefault().post("updatechats" + "," + currentTimeStamp + "," + roomId[0]);
                                            }
                                        }
                                        if (message_id[0].isEmpty() || message_id[0] == null) {
                                            Log.d("SJKJSJKJSJSDK", "1+");
                                            message_id[0] = String.valueOf(System.currentTimeMillis());
                                        } else {
                                            message_id[0] = message_id[0];
                                        }
                                        long id_res1 = -1;
                                        final String ts = ConvertGMTtoIST.convertISTtoGMTFormat();

                                        String otherUsername = roomName;

                                        id_res1 = sqLiteHandler.addMessage(message_id[0], data, "0", "0", "", "", otherUsername, ts, "", "", "", "0", roomId[0], "chat");


                                        if (id_res1 != -1) {
                                            //     sendMsgDeliveredStatus(message_id[0]);
                                            ChatMessage chatMessage = new ChatMessage();
                                            chatMessage.setUserType(UserType.OTHER);
                                            chatMessage.setMessageType("text_other");
                                            chatMessage.setMsgStatus("4");
                                            chatMessage.setOtherUsreName(otherUsername);
                                            chatMessage.setId(message_id[0]);

                                            chatMessage.setMsgType("chat");
                                            chatMessage.setMsgRoomId(roomId[0]);
                                            chatMessage.setMessageText(data);
                                            chatMessage.setReadStatus("0");
                                            chatMessage.setMessageTimeNew(ts);
                                            //  if (finalType.equals("s")) {
                                            // d()java.lang.NullPointerException: Can't pass null for argument 'pathString' in chil
                                            Database.NODE_MESSAGES_PATH.child(roomId[0]).child(message_id[0]).setValue(chatMessage);
                                            //} else
                                              //  Database.NODE_INQUIRYCHAT_PATH.child(roomId[0]).child("messages").child(chatMessage.getId()).setValue(chatMessage);

                                            Log.d("BJDBJBDJBDD", "6" + id_res);
                                            sqLiteHandler.updateRoomData(roomId[0], data, ts, "");
                                            EventBus.getDefault().post("addMessages" + "," + roomId[0] + "," + message_id[0]);


                                        }

                                    }


                                }
                                handleNotification(message, roomId[0], message_id[0], ds_name, "s");

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                } else {
                    Log.d("TEWYGDGHSGHSD", "2" + "===>" + roomId[0]);
                    sqLiteHandler.updateRoomMobData(roomId[0], user_id);
                    String lastTimestamp = sqLiteHandler.getLastMessageTimestampFrmRoom(roomId[0]);
                    if (lastTimestamp == null) {
                        Log.d("BJDBJBDJBDD", "2");
                        String firsttimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                        ChatMessage chatMsg = new ChatMessage();
                        chatMsg.setMessageType("chatheader");
                        chatMsg.setMessageTimeNew(firsttimeStamp);

                        sqLiteHandler.addMessage(firsttimeStamp, "", "1", "7", "", "", "", firsttimeStamp, "", "", "", "1", roomId[0], "chat");
                    } else {
                        Log.d("BJDBJBDJBDD", "3");
                        String currentTimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                        if (!ConvertGMTtoIST.getDateFromTt(lastTimestamp).equals(ConvertGMTtoIST.getDateFromTt(currentTimeStamp))) {
                            ChatMessage chatMsg = new ChatMessage();
                            chatMsg.setMessageType("chatheader");
                            chatMsg.setMessageTimeNew(currentTimeStamp);
                            sqLiteHandler.addMessage(currentTimeStamp, "", "1", "7", "", "", "", currentTimeStamp, "", "", "", "1", roomId[0], "chat");
                            EventBus.getDefault().post("updatechats" + "," + currentTimeStamp + "," + roomId[0]);
                        }
                    }
                    String messageId;
                    Log.d("SJKJSJKJSJSDK", message_id[0] + "==");
                    if (message_id[0].isEmpty() || message_id[0] == null) {
                        Log.d("SJKJSJKJSJSDK", "1+");
                        messageId = String.valueOf(System.currentTimeMillis());
                    } else {
                        messageId = message_id[0];
                    }

                    long id_res1 = -1;
                    final String ts = ConvertGMTtoIST.convertISTtoGMTFormat();
                    String roomName = sqLiteHandler.getRoomByID(roomId[0]).getName();
                    String otherUsername = roomName;
                    Log.d("TEWYGDGHSGHSD", "5" + "===>" + roomId[0] + "===" + messageId);

                    id_res1 = sqLiteHandler.addMessage(messageId, data, "0", "0", "", "", otherUsername, ts, "", "", "", "0", roomId[0], "chat");


                    if (id_res1 != -1) {
                        // sendMsgDeliveredStatus(messageId);
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setUserType(UserType.OTHER);
                        chatMessage.setMessageType("text_other");
                        chatMessage.setMsgStatus("4");
                        chatMessage.setOtherUsreName(otherUsername);
                        chatMessage.setId(messageId);
                        chatMessage.setMsgType("chat");
                        chatMessage.setMsgRoomId(roomId[0]);
                        chatMessage.setMessageText(data);
                        chatMessage.setReadStatus("0");
                        chatMessage.setMessageTimeNew(ts);

                        // d()java.lang.NullPointerException: Can't pass null for argument 'pathString' in chil
                        Database.NODE_MESSAGES_PATH.child(roomId[0]).child(message_id[0]).setValue(chatMessage);

                        sqLiteHandler.updateRoomData(roomId[0], data, ts, "");
                        EventBus.getDefault().post("addMessages" + "," + roomId[0] + "," + message_id[0]);


                    }
                    handleNotification(message, roomId[0], message_id[0], ds_name, "s");
                }


            }


            if (group_id != null) {
                // if (message_id[0].isEmpty() || message_id[0] == null) {
                message_id[0] = String.valueOf(System.currentTimeMillis());
                //  }
                final String finalMessage_id = message_id[0];
                roomId[0] = group_id;
                if (sqLiteHandler.getRoomDirectExist(group_id) == 0) {

                    Database.NODE_GROUPCHAT_PATH.child(group_id).child("groupName").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String nm = dataSnapshot.getValue().toString();
                                long id_res1 = sqLiteHandler.addRoom(group_id, firebaseAuth.getUid(), "", "", ts1, "", "", "", "", nm, "", "g", "", ts1, nm, "");
                                Log.d("KJDKHJHJHJDHSHDJJ123", "3" + id_res1);
                                if (id_res1 != -1) {

                                    String lastTimestamp = sqLiteHandler.getLastMessageTimestampFrmRoom(group_id);
                                    if (lastTimestamp == null) {
                                        Log.d("BJDBJBDJBDD", "2");
                                        String firsttimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                                        ChatMessage chatMsg = new ChatMessage();
                                        chatMsg.setMessageType("chatheader");
                                        chatMsg.setMessageTimeNew(firsttimeStamp);
                                        sqLiteHandler.addMessage(firsttimeStamp, "", "1", "7", "", "", "", firsttimeStamp, "", "", "", "1", getPackageCodePath(), "chat");
                                    } else {
                                        Log.d("BJDBJBDJBDD",
                                                "3");
                                        String currentTimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                                        if (!ConvertGMTtoIST.getDateFromTt(lastTimestamp).equals(ConvertGMTtoIST.getDateFromTt(currentTimeStamp))) {
                                            ChatMessage chatMsg = new ChatMessage();
                                            chatMsg.setMessageType("chatheader");
                                            chatMsg.setMessageTimeNew(currentTimeStamp);
                                            sqLiteHandler.addMessage(currentTimeStamp, "", "1", "7", "", "", "", currentTimeStamp, "", "", "", "1", group_id, "chat");
                                            EventBus.getDefault().post("updatechats" + "," + currentTimeStamp + "," + group_id);
                                        }
                                    }
                                    String ts = ConvertGMTtoIST.convertISTtoGMTFormat();
                                    EventBus.getDefault().post("updateDB");
                                    ChatMessage chatMessage = new ChatMessage();
                                    chatMessage.setUserType(UserType.OTHER);
                                    chatMessage.setMessageType("text_other");
                                    chatMessage.setMsgStatus("4");
                                    chatMessage.setOtherUsreName("YatraDham");
                                    chatMessage.setId(message_id[0]);
                                    chatMessage.setMsgType("chat");
                                    chatMessage.setMsgRoomId(group_id);
                                    chatMessage.setMessageText(data);
                                    chatMessage.setReadStatus("0");
                                    chatMessage.setMessageTimeNew(ts);


                                    //     sqLiteHandler.updateRoomData(group_id, chatMessage.getMessageText(), ts, "");


                                    //  Log.d("JKJKJK", messageText + "==");
                                    String otherUsername = "YatraDham";
                                    long id_res = sqLiteHandler.addMessage(finalMessage_id, chatMessage.getMessageText(), "0", "0", "", "", otherUsername, ts, "", "", "", "0", group_id, "chat");
                                    //   Log.d("BJDBJBDJBDD", "5" + id_res);

                                    if (id_res != -1) {
                                        Database.NODE_GROUPCHAT_PATH.child(group_id).child("messages").child(finalMessage_id).setValue(chatMessage);

                                        String timeStamp1 = ConvertGMTtoIST.convertISTtoGMTFormat();
                                        GroupSeen groupSeen = new GroupSeen();
                                        groupSeen.setTimeStamp(timeStamp1);
                                        groupSeen.setUserName(sessionManager.getUserDetails().get("name"));

                                        Database.NODE_MESSAGE_DELIVERED_PATH.child(finalMessage_id).child("delivered").push().setValue(groupSeen);

                                        Log.d("BJDBJBDJBDD", "6");
                                        sqLiteHandler.updateRoomData(group_id, chatMessage.getMessageText(), ts, "");
                                        EventBus.getDefault().post("addMessages" + "," + group_id);

                                        EventBus.getDefault().post("updatechats" + "," + finalMessage_id + "," + group_id);

                                    }

                                }
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } else {
                    String otherUsername = "Yatradham";

                    EventBus.getDefault().post("updateDB");
                    String lastTimestamp = sqLiteHandler.getLastMessageTimestampFrmRoom(group_id);
                    if (lastTimestamp == null) {
                        Log.d("BJDBJBDJBDD", "2");
                        String firsttimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                        ChatMessage chatMsg = new ChatMessage();
                        chatMsg.setMessageType("chatheader");
                        chatMsg.setMessageTimeNew(firsttimeStamp);
                        sqLiteHandler.addMessage(firsttimeStamp, "", "1", "7", "", "", "", firsttimeStamp, "", "", "", "1", getPackageCodePath(), "chat");
                    } else {
                        Log.d("BJDBJBDJBDD", "3");
                        String currentTimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                        if (!ConvertGMTtoIST.getDateFromTt(lastTimestamp).equals(ConvertGMTtoIST.getDateFromTt(currentTimeStamp))) {
                            ChatMessage chatMsg = new ChatMessage();
                            chatMsg.setMessageType("chatheader");
                            chatMsg.setMessageTimeNew(currentTimeStamp);
                            sqLiteHandler.addMessage(currentTimeStamp, "", "1", "7", "", "", "", currentTimeStamp, "", "", "", "1", group_id, "chat");
                            EventBus.getDefault().post("updatechats" + "," + currentTimeStamp + "," + group_id);
                        }
                    }

                    String timeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setUserType(UserType.OTHER);
                    chatMessage.setMessageType("text_other");
                    chatMessage.setMsgStatus("4");
                    chatMessage.setOtherUsreName(otherUsername);
                    chatMessage.setId(finalMessage_id);
                    chatMessage.setMsgType("chat");
                    chatMessage.setMsgRoomId(roomId[0]);
                    chatMessage.setMessageText(data);
                    chatMessage.setReadStatus("0");
                    chatMessage.setMessageTimeNew(timeStamp);


                    //  Log.d("JKJKJK", messageText + "==");

                    long id_res = sqLiteHandler.addMessage(finalMessage_id, chatMessage.getMessageText(), "0", "0", "", "", otherUsername, timeStamp, "", "", "", "0", group_id, "chat");
                    Log.d("BJDBJBDJBDD", "5" + id_res);

                    if (id_res != -1) {
                        Database.NODE_GROUPCHAT_PATH.child(group_id).child("messages").child(finalMessage_id).setValue(chatMessage);
                        String timeStamp1 = ConvertGMTtoIST.convertISTtoGMTFormat();
                        GroupSeen groupSeen = new GroupSeen();
                        groupSeen.setTimeStamp(timeStamp1);
                        groupSeen.setUserName(sessionManager.getUserDetails().get("name"));

                        Database.NODE_MESSAGE_DELIVERED_PATH.child(finalMessage_id).child("delivered").push().setValue(groupSeen);

                        Log.d("BJDBJBDJBDD", "6");
                        sqLiteHandler.updateRoomData(group_id, chatMessage.getMessageText(), timeStamp, "");
                        EventBus.getDefault().post("addMessages" + "," + group_id);

                        EventBus.getDefault().post("updatechats" + "," + finalMessage_id + "," + group_id);


                    }


                }
            }


           // } else {
                //String group_id = message.getData().get("group_id");
               // type = "g";
                roomId = group_id;
               // if (sqLiteHandler.getRoomDirectExist(roomId) == 0) {

                //}
           // }


        } else if (message.getData().get("title").equals("Yatradham")) {
            Log.d("KJDKHJHJHJDHSHDJJ123", "1");
            String typeMsg = message.getData().get("receiver_type");
            String message_id = message.getData().get("message_id");
            String type = null;
            String otherUsername = "Yatradham";
            final String[] nm = {"Yatradham"};
            Log.d("JDASGHGDHSGSDHG", type + "====");
            if (typeMsg.equalsIgnoreCase("private")) {
                type = "s";
            } else {
                type = "g";
            }
            final String[] roomName = {"yatradham"};
            String ds_id = message.getData().get("ds_id");
            final String roomId;

            String groupId = null;

            String read_status = "0";

            if (type.equals("s")) {
                roomId = ds_id;
                String dharamshala_name = message.getData().get("ds_name");


                // if (sessionManager.getRole().equals("1") || sessionManager.getRole().equals("2")) {
                roomName[0] = dharamshala_name;
                Log.d("hjghjhdfjgf", "1" + roomName[0]);


                //}

            } else {
                read_status = "1";
                groupId = message.getData().get("group_id");

                if (sqLiteHandler.getRoomDirectExist(groupId) != 0) {
                    roomName[0] = sqLiteHandler.getRoomName(groupId);
                } else {

                }
                Log.d("hjghjhdfjgf", "2" + roomName[0]);
                roomId = groupId + ds_id;

            }
            Log.d("HSDUGSYGYGASY123", roomName[0] + "====" + roomId);
            final String data = message.getData().get("message");
            final long[] id_res1 = {-1};
            final String ts1 = ConvertGMTtoIST.convertISTtoGMTFormat();

            if (sqLiteHandler.getRoomDirectExist(roomId) == 0) {
                if (type.equals("s")) {
                    final String finalRoomId = roomId;
                    final String finalRoomName = roomName[0];

                    Database.NODE_USERS_PATH.orderByChild("usertype").equalTo("1").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> membersOfGrp = new ArrayList<>();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                User user = dataSnapshot1.getValue(User.class);
                                if (!firebaseAuth.getUid().equals(user.getUid())) {

                                    membersOfGrp.add(user.getUid());
                                }

                            }
                            membersOfGrp.add(message.getData().get("user_id"));
                            String membersnew = android.text.TextUtils.join(",", membersOfGrp);
                            id_res1[0] = sqLiteHandler.addRoom(finalRoomId, firebaseAuth.getUid(), finalRoomName, data, ts1, "", "", "", "", finalRoomName, "", "si", membersnew, ts1, finalRoomName, "");


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } else {
                    Log.d("KJKJKDJKJKJAK", "12");


                    final String finalRoomid = roomId;
                    final String finalGroupId = groupId;
                    final String finalGroupId1 = groupId;
                    Database.NODE_GROUP_LIST_PATH.child(firebaseAuth.getUid()).child(groupId).child("groupName").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                roomName[0] = dataSnapshot.getValue().toString();
                                id_res1[0] = sqLiteHandler.addRoom(roomId, firebaseAuth.getUid(), roomName[0], data, ts1, "", "", "", "", roomName[0], "", "gi", "", ts1, roomName[0], "");

                                if (id_res1[0] > 0) {
                                    Log.d("KJKJKDJKJKJAK", "9");
                                    final String finalRoomId1 = roomId;
                                    final String finalRoomName3 = roomName[0];

                                    Database.NODE_GROUPCHAT_PATH.child(finalGroupId).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Log.d("KJKJKDJKJKJAK", "if");
                                                sqLiteHandler.updateRoomDataMemberAdded(finalRoomid, dataSnapshot.getValue().toString());
                                                final String members = dataSnapshot.getValue().toString();

                                                // final String mGroupId = Database.NODE_INQUIRYCHAT_PATH.push().getKey();
                                                Log.d("JAKJKADADHD", finalRoomName3 + "=====" + finalRoomid);


                                                Database.NODE_INQUIRYCHAT_PATH.child(finalRoomid).child("groupName").setValue(finalRoomName3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("JAKJKADADHD", "succesess");
                                                        ArrayList<String> selected = new ArrayList<>();
                                                        selected = new ArrayList(Arrays.asList(members.split("\\s*,\\s*")));
                                                        Log.d("JAKJKADADHD", "succesess==" + selected.size());

                                                        final ArrayList<String> finalSelected = selected;
                                                        for (int i = 0; i < finalSelected.size(); i++) {
                                                            Log.d("JAKJKADADHD", "succesess123");

                                                            Database.NODE_INQUIRY_GROUP_LIST_PATH.child(selected.get(i)).child(finalRoomId1).child("groupName").setValue(finalRoomName3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Database.NODE_INQUIRYCHAT_PATH.child(finalRoomId1).child("members").setValue(members).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {


                                                                        }
                                                                    });

                                                                }
                                                            });

                                                        }
                                                    }
                                                });


                                            } else {
                                                Log.d("KJKJKDJKJKJAK", "else");
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }

                            } else {
                                id_res1[0] = sqLiteHandler.addRoom(roomId, firebaseAuth.getUid(), roomName[0], data, ts1, "", "", "", "", roomName[0], "", "gi", "", ts1, roomName[0], "");
                                if (id_res1[0] > 0) {
                                    Log.d("KJKJKDJKJKJAK", "9");
                                    final String finalRoomId1 = roomId;
                                    final String finalRoomName3 = roomName[0];

                                    Database.NODE_GROUPCHAT_PATH.child(finalGroupId1).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Log.d("KJKJKDJKJKJAK", "if");
                                                sqLiteHandler.updateRoomDataMemberAdded(finalRoomid, dataSnapshot.getValue().toString());
                                                final String members = dataSnapshot.getValue().toString();

                                                // final String mGroupId = Database.NODE_INQUIRYCHAT_PATH.push().getKey();
                                                Log.d("JAKJKADADHD", finalRoomName3 + "=====" + finalRoomid);


                                                Database.NODE_INQUIRYCHAT_PATH.child(finalRoomid).child("groupName").setValue(finalRoomName3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("JAKJKADADHD", "succesess");
                                                        ArrayList<String> selected = new ArrayList<>();
                                                        selected = new ArrayList(Arrays.asList(members.split("\\s*,\\s*")));
                                                        Log.d("JAKJKADADHD", "succesess==" + selected.size());

                                                        final ArrayList<String> finalSelected = selected;
                                                        for (int i = 0; i < finalSelected.size(); i++) {
                                                            Log.d("JAKJKADADHD", "succesess123");

                                                            Database.NODE_INQUIRY_GROUP_LIST_PATH.child(selected.get(i)).child(finalRoomId1).child("groupName").setValue(finalRoomName3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Database.NODE_INQUIRYCHAT_PATH.child(finalRoomId1).child("members").setValue(members).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {


                                                                        }
                                                                    });

                                                                }
                                                            });

                                                        }
                                                    }
                                                });


                                            } else {
                                                Log.d("KJKJKDJKJKJAK", "else");
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                Log.d("KJKJKDJKJKJAK", "15");
                sqLiteHandler.updateRoomData(roomId, data, ts1, "");
                //sqLiteHandler.updateRoomDataName(roomId, roomName);
            }
            Log.d("KJDKHJHJHJDHSHDJJ123", "3" + id_res1[0]);
            //if (id_res1 != -1) {

            // EventBus.getDefault().post("updateDB");
            String lastTimestamp = sqLiteHandler.getLastMessageTimestampFrmRoomInquiry(roomId);
            if (lastTimestamp == null) {
                Log.d("BJDBJBDJBDD", "2");
                String firsttimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                ChatMessage chatMsg = new ChatMessage();
                chatMsg.setMessageType("chatheader");
                chatMsg.setMessageTimeNew(firsttimeStamp);

                sqLiteHandler.addMessage(firsttimeStamp, "", "1", "7", "", "", "", firsttimeStamp, "", "", "", "1", roomId, "inquiry");
            } else {
                Log.d("BJDBJBDJBDD", "3");
                String currentTimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                if (!ConvertGMTtoIST.getDateFromTt(lastTimestamp).equals(ConvertGMTtoIST.getDateFromTt(currentTimeStamp))) {
                    ChatMessage chatMsg = new ChatMessage();
                    chatMsg.setMessageType("chatheader");
                    chatMsg.setMessageTimeNew(currentTimeStamp);
                    sqLiteHandler.addMessage(currentTimeStamp, "", "1", "7", "", "", "", currentTimeStamp, "", "", "", "1", roomId, "inquiry");
                    EventBus.getDefault().post("updatechats" + "," + currentTimeStamp + "," + roomId);
                }
            }
            String messageId = null;
            if (message_id == null) {
                if (!message_id.isEmpty())
                    messageId = String.valueOf(System.currentTimeMillis());
            } else {
                messageId = message_id;

            }
            String otherUserNm = "Yatradham";
            long id_res = -1;
            final String ts = ConvertGMTtoIST.convertISTtoGMTFormat();

            if (sessionManager.getRole().equals("1") || sessionManager.getRole().equals("2")) {
                otherUsername = roomName[0];
                id_res = sqLiteHandler.addMessage(messageId, data, "0", "0", "", "", otherUsername, ts, "", "", "", "0", roomId, "inquiry");


            } else {
                String timeStamp1 = ConvertGMTtoIST.convertISTtoGMTFormat();
                GroupSeen groupSeen = new GroupSeen();
                groupSeen.setTimeStamp(timeStamp1);
                groupSeen.setUserName(sessionManager.getUserDetails().get("name"));

                Database.NODE_MESSAGE_DELIVERED_PATH.child(message_id).child("delivered").push().setValue(groupSeen);

                otherUsername = roomName[0];
                id_res = sqLiteHandler.addMessage(messageId, data, "0", "4", "", "", otherUsername, ts, "", "", "", "0", roomId, "inquiry");
            }


            Log.d("BJDBJBDJBDD", "5" + id_res);

            if (id_res != -1) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setUserType(UserType.OTHER);
                chatMessage.setMessageType("text_other");
                chatMessage.setMsgStatus("4");
                chatMessage.setOtherUsreName(otherUsername);
                chatMessage.setId(messageId);

                chatMessage.setMsgType("inquiry");
                chatMessage.setMsgRoomId(roomId);
                chatMessage.setMessageText(data);
                chatMessage.setReadStatus(read_status);
                chatMessage.setMessageTimeNew(ts);
                if (type.equals("s")) {
                    // d()java.lang.NullPointerException: Can't pass null for argument 'pathString' in chil
                    Database.NODE_MESSAGES_PATH.child(roomId).child(messageId).setValue(chatMessage);
                } else
                    Database.NODE_INQUIRYCHAT_PATH.child(roomId).child("messages").child(chatMessage.getId()).setValue(chatMessage);

                Log.d("BJDBJBDJBDD", "6" + id_res);
                sqLiteHandler.updateRoomData(roomId, data, ts, "");
                EventBus.getDefault().post("addMessages" + "," + roomId + "," + messageId);

                handleNotification(message, roomId, messageId, roomName[0], type);

            }
            // processNotificationInquiry(message);
        } else if (message.getData().get("title").equals("Yatradham1")) {
            String ts = ConvertGMTtoIST.convertISTtoGMTFormat();
            Log.d("KJDKHJHJHJDHSHDJJ123", "1");
            String type = message.getData().get("type");
            String roomName = message.getData().get("roomname");
            String sender = message.getData().get("senderName");
            String members = message.getData().get("reciever");
            final String roomId = message.getData().get("roomId");
            final String msgQuotedText = message.getData().get("quoted_text");
            final String msgQuotedId = message.getData().get("quoted_id");
            String msg_type = message.getData().get("msg_type");

            if (msg_type == null) {
                msg_type = "0";
            }
            if (msg_type.equals("2")) {
                msg_type = "3";
            } else {
                msg_type = "0";
            }
            String groupId = null;
            String read_status = "0";
            groupId = roomId;
            String data = message.getData().get("body");
            //  String dharamshala_name = message.getData().get("ds_name");

            long id_res1 = -1;
            if (sqLiteHandler.getRoomDirectExist(roomId) == 0) {
                if (type.equals("s")) {
                    id_res1 = sqLiteHandler.addRoom(roomId, firebaseAuth.getUid(), sender, data, ts, "", "", "", "", message.getData().get("name"), "", "si", members, ts, message.getData().get("name"), "");
                } else {

                    final String finalRoomid = roomId;

                    id_res1 = sqLiteHandler.addRoom(roomId, firebaseAuth.getUid(), sender, data, ts, "", "", "", "", roomName, "", "gi", members, ts, roomName, "");
                }
            } else {
                sqLiteHandler.updateRoomDataName(roomId, roomName);
                sqLiteHandler.updateRoomData(roomId, data, ts, "");
            }
            Log.d("KJDKHJHJHJDHSHDJJ123", "3" + id_res1);
            //if (id_res1 != -1) {

            // EventBus.getDefault().post("updateDB");
            String lastTimestamp = sqLiteHandler.getLastMessageTimestampFrmRoomInquiry(roomId);
            if (lastTimestamp == null) {
                Log.d("BJDBJBDJBDD", "2");
                String firsttimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                ChatMessage chatMsg = new ChatMessage();
                chatMsg.setMessageType("chatheader");
                chatMsg.setMessageTimeNew(firsttimeStamp);

                sqLiteHandler.addMessage(firsttimeStamp, "", "1", "7", "", "", "", firsttimeStamp, "", "", "", "1", roomId, "inquiry");
            } else {
                Log.d("BJDBJBDJBDD", "3");
                String currentTimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                if (!ConvertGMTtoIST.getDateFromTt(lastTimestamp).equals(ConvertGMTtoIST.getDateFromTt(currentTimeStamp))) {
                    ChatMessage chatMsg = new ChatMessage();
                    chatMsg.setMessageType("chatheader");
                    chatMsg.setMessageTimeNew(currentTimeStamp);
                    sqLiteHandler.addMessage(currentTimeStamp, "", "1", "7", "", "", "", currentTimeStamp, "", "", "", "1", roomId, "inquiry");
                    EventBus.getDefault().post("updatechats" + "," + currentTimeStamp + "," + roomId);
                }
            }
            String timeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
            final String messageId = message.getData().get("msg_id");
            long id_res = sqLiteHandler.addMessage(messageId, data, "0", msg_type, "", msgQuotedId, sender, timeStamp, msgQuotedText, "", "", "0", roomId, "inquiry");
            Log.d("BJDBJBDJBDD", roomId + "==" + messageId);

            if (id_res != -1) {
                sqLiteHandler.updateRoomData(roomId, data, ts, "");
                EventBus.getDefault().post("updateDB");
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setUserType(UserType.OTHER);
                chatMessage.setMessageType("text_other");
                chatMessage.setMsgStatus("4");
                chatMessage.setOtherUsreName(sender);
                chatMessage.setId(messageId);
                chatMessage.setMsgType("inquiry");
                chatMessage.setMsgRoomId(roomId);
                chatMessage.setMessageText(data);
                chatMessage.setReadStatus(read_status);
                chatMessage.setMessageTimeNew(timeStamp);
                if (type.equals("s")) {
                    String timeStamp1 = ConvertGMTtoIST.convertISTtoGMTFormat();
                    GroupSeen groupSeen = new GroupSeen();
                    groupSeen.setTimeStamp(timeStamp1);
                    groupSeen.setUserName(sessionManager.getUserDetails().get("name"));

                    Database.NODE_MESSAGE_DELIVERED_PATH.child(messageId).child("delivered").push().setValue(groupSeen);

                    Database.NODE_MESSAGES_PATH.child(roomId).child(messageId).child("msgStatus").setValue("7").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            try {
                                sendReqNotification(message.getData().get("fcm_id"), roomId, messageId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }


                    });
                    EventBus.getDefault().post("updatechats" + "," + messageId + "," + roomId);

                } else {


                    Database.NODE_GROUPCHAT_PATH.child(roomId).child("messages").child(chatMessage.getId()).setValue(chatMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            String timeStamp1 = ConvertGMTtoIST.convertISTtoGMTFormat();

                            GroupSeen groupSeen = new GroupSeen();
                            groupSeen.setTimeStamp(timeStamp1);
                            groupSeen.setUserName(sessionManager.getUserDetails().get("name"));
                            Database.NODE_MESSAGE_DELIVERED_PATH.child(messageId).child("delivered").push().setValue(groupSeen);


                        }
                    });
                }
                Log.d("BJDBJBDJBDD", "6" + id_res);
                sqLiteHandler.updateRoomDataName(roomId, roomName);
                //  sqLiteHandler.updateRoomData(roomId, data, timeStamp, "");
                EventBus.getDefault().post("addMessages" + "," + roomId + "," + messageId);
                handleNotification(message, roomId, messageId, sender, type);

            }
            // processNotificationInquiry(message);
        } else {
            processNotification(message);
        }
*/
    }

    private void sendMsgDeliveredStatus(String messageId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        //  Log.d("CHECKPARAMETERS", "https://yatradham.org/demosite/restapi.php?apiname=addadmin&number=" + mobNumber + "&is_mobile=APP");

        StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "receivemsg&msg_id=" + messageId + "&is_mobile=APP", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //  Toast.makeText(CreateUserActivity.this,response,Toast.LENGTH_LONG).show();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Log.d("CHECKRESPONSEDelivery", response.toString());
                    if (jsonObject.has("status")) {
                        if (jsonObject.getBoolean("status")) {


                        } else {
                            // Toast.makeText(MainActivity.this, "can not add as admin role !!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Toast.makeText(MainActivity.this, "Please Contact Yatradham.org !!", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> param = new HashMap<String, String>();

                return param;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        sr.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(sr);
    }


    @Override
    public void onNewToken(final String s) {
        super.onNewToken(s);
        SharedPreferences sharedPreferences = getSharedPreferences(TOKEN_PREF_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", s);
        editor.commit();
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            if (firebaseAuth != null) {
                if (firebaseAuth.getUid() != null) {
                    Database.NODE_USERS_PATH.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (s != null) {
                                    Database.NODE_USERS_PATH.child(firebaseAuth.getUid()).child("fcm_id").setValue(s);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }
    }


    /*private void processNotification(final RemoteMessage extras) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        System.out.println("remoteMessage = " + extras);
        // Check if message contains a data payload.

        if (!extras.getData().isEmpty()) {
//			Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //System.out.println("Message data payload: " + extras.getData());
            //System.out.println("Message data payload type: " + extras.getData().get("type"));

            nameOther = extras.getData().get("name");
            final String roomId = extras.getData().get("roomId");
            final String msg_id = extras.getData().get("msg_id");
            final String userid = extras.getData().get("userid");
            final String msgQuotedText = extras.getData().get("quoted_text");
            final String msgQuotedId = extras.getData().get("quoted_id");
            String msg_type = extras.getData().get("msg_type");

            if (msg_type == null) {
                msg_type = "0";
            }
            if (msg_type.equals("2")) {
                msg_type = "3";
            } else {
                msg_type = "0";
            }
            final String sender = extras.getData().get("sender");
            final String username1 = extras.getData().get("name");
            String type = extras.getData().get("type");

            handleNotification(extras, roomId, msg_id, username1, extras.getData().get("type"));

            if (type.equals("s")) {
                if (sessionManager.getRole().equals("1")) {
                    sqLiteHandler.updateRoomMobData(roomId, sender);

                }
                final String finalMsg_type = msg_type;
                Database.NODE_MESSAGES_PATH.child(roomId).child(msg_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Log.d("NJDHHHJDHJHHD", dataSnapshot + "");
                        if (dataSnapshot != null) {
                            if (dataSnapshot.exists()) {

                                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                                String ts = ConvertGMTtoIST.convertISTtoGMTFormat();
                                if (sqLiteHandler.getRoomDirectExist(roomId) == 0) {
                                    long id_res1 = sqLiteHandler.addRoom(roomId, firebaseAuth.getUid(), sender, chatMessage.getMessageText(), ts, "", "", "", "", username1, "", "s", "", ts, username1, "");
                                    if (id_res1 != -1) {
                                        EventBus.getDefault().post("updateDB");
                                    }
                                } else {
                                    sqLiteHandler.updateRoomData(roomId, chatMessage.getMessageText(), ts, sender);
                                }
                                if (sqLiteHandler.getRoomGrpExist(msg_id) == 0) {

                                    Log.d("BJDBJBDJBDD", "1");


                                    String lastTimestamp = sqLiteHandler.getLastMessageTimestampFrmRoom(roomId);
                                    if (lastTimestamp == null) {
                                        Log.d("BJDBJBDJBDD", "2");
                                        String firsttimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                                        ChatMessage chatMsg = new ChatMessage();
                                        chatMsg.setMessageType("chatheader");
                                        chatMsg.setMessageTimeNew(firsttimeStamp);
                                        sqLiteHandler.addMessage(firsttimeStamp, "", "1", "7", "", "", "", firsttimeStamp, "", "", "", "1", roomId, "chat");
                                    } else {
                                        Log.d("BJDBJBDJBDD", "3");
                                        String currentTimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                                        if (!ConvertGMTtoIST.getDateFromTt(lastTimestamp).equals(ConvertGMTtoIST.getDateFromTt(currentTimeStamp))) {
                                            ChatMessage chatMsg = new ChatMessage();
                                            chatMsg.setMessageType("chatheader");
                                            chatMsg.setMessageTimeNew(currentTimeStamp);
                                            sqLiteHandler.addMessage(currentTimeStamp, "", "1", "7", "", "", "", currentTimeStamp, "", "", "", "1", roomId, "chat");
                                            EventBus.getDefault().post("updatechats" + "," + currentTimeStamp + "," + roomId);
                                        }
                                    }
                                    String timeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();

                                    long id_res = sqLiteHandler.addMessage(msg_id, chatMessage.getMessageText(), "0", finalMsg_type, "", msgQuotedId, username1, timeStamp, msgQuotedText, "", "", "0", roomId, "chat");
                                    Log.d("BJDBJBDJBDD", "5" + id_res);

                                    if (id_res != -1) {
                                        String timeStamp1 = ConvertGMTtoIST.convertISTtoGMTFormat();
                                        GroupSeen groupSeen = new GroupSeen();
                                        groupSeen.setTimeStamp(timeStamp1);
                                        groupSeen.setUserName(sessionManager.getUserDetails().get("name"));

                                        Database.NODE_MESSAGE_DELIVERED_PATH.child(msg_id).child("delivered").push().setValue(groupSeen);

                                        Log.d("BJDBJBDJBDD", "6" + id_res);
                                        sqLiteHandler.updateRoomData(roomId, chatMessage.getMessageText(), timeStamp, "");
                                        //  Log.d("TESTMSGS888", messageText + "===>" + timeStamp + "===>" + roomId);
                                        //sqLiteHandler.updateRoomData(roomId, messageText, timeStamp);
                                        //  ChatRoom chatRoom=sqLiteHandler.getRoomDByID(roomId);
                                        //sqLiteHandler.updateRoomDataName(roomId,chatRoom)
                                        //removed updatedb        EventBus.getDefault().post("updateDB");
                                        Log.d("KFJKFKJFKKFJK", roomId + "==" + msg_id);
                                        Database.NODE_MESSAGES_PATH.child(roomId).child(msg_id).child("msgStatus").setValue("7").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                if (extras.getData().get("type").equals("s")) {
                                                    try {
                                                        sendReqNotification(extras.getData().get("fcm_id"), roomId, msg_id);
                                                        EventBus.getDefault().post("addMessages" + "," + roomId + "," + msg_id);

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }


                                        });
                                        EventBus.getDefault().post("updatechats" + "," + msg_id + "," + roomId);

                                    }
                                }
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
//  final String members = extras.getData().get("members");
                final String finalMsg_type1 = msg_type;
                Database.NODE_GROUPCHAT_PATH.child(roomId).child("messages").child(msg_id).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Log.d("NJDHHHJDHJHHD123", dataSnapshot + "");
                        if (dataSnapshot != null) {
                            if (dataSnapshot.exists()) {
                                final ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                                Log.d("BJDBJBDJBDD", "1");

                                String ts = ConvertGMTtoIST.convertISTtoGMTFormat();
                                sqLiteHandler.updateRoomData(roomId, chatMessage.getMessageText(), ts, userid);

                                String lastTimestamp = sqLiteHandler.getLastMessageTimestampFrmRoom(roomId);
                                if (lastTimestamp == null) {
                                    Log.d("BJDBJBDJBDD", "2");
                                    String firsttimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                                    ChatMessage chatMsg = new ChatMessage();
                                    chatMsg.setMessageType("chatheader");
                                    chatMsg.setMessageTimeNew(firsttimeStamp);
                                    sqLiteHandler.addMessage(firsttimeStamp, "", "1", "7", "", "", "", firsttimeStamp, "", "", "", "1", roomId, "chat");
                                } else {
                                    Log.d("BJDBJBDJBDD", "3");
                                    String currentTimeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                                    if (!ConvertGMTtoIST.getDateFromTt(lastTimestamp).equals(ConvertGMTtoIST.getDateFromTt(currentTimeStamp))) {
                                        ChatMessage chatMsg = new ChatMessage();
                                        chatMsg.setMessageType("chatheader");
                                        chatMsg.setMessageTimeNew(currentTimeStamp);
                                        sqLiteHandler.addMessage(currentTimeStamp, "", "1", "7", "", "", "", currentTimeStamp, "", "", "", "1", roomId, "chat");
                                        EventBus.getDefault().post("updatechats" + "," + currentTimeStamp + "," + roomId);
                                    }
                                }
                                String timeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
                                //  Log.d("JKJKJK", messageText + "==");
                                Log.d("SHJGGHJSDJDSGJDSG", finalMsg_type1);
                                long id_res = sqLiteHandler.addMessage(msg_id, chatMessage.getMessageText(), "0", finalMsg_type1, "", msgQuotedId, username1, timeStamp, msgQuotedText, "", "", "0", roomId, "chat");
                                Log.d("BJDBJBDJBDD", "5" + id_res);

                                if (id_res != -1) {
                                    String timeStamp1 = ConvertGMTtoIST.convertISTtoGMTFormat();
                                    GroupSeen groupSeen = new GroupSeen();
                                    groupSeen.setTimeStamp(timeStamp1);
                                    groupSeen.setUserName(sessionManager.getUserDetails().get("name"));

                                    Database.NODE_MESSAGE_DELIVERED_PATH.child(msg_id).child("delivered").push().setValue(groupSeen);

                                    Log.d("BJDBJBDJBDD", "6");
                                    sqLiteHandler.updateRoomData(roomId, chatMessage.getMessageText(), timeStamp, "");
                                    EventBus.getDefault().post("addMessages" + "," + roomId);

                                    EventBus.getDefault().post("updatechats" + "," + msg_id + "," + roomId);

                                }

                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }


    }*/



    public void showNotification(Context context, String id, String data, String msg_type, RemoteMessage message) {
         PendingIntent contentIntent;
        int id1 = (int) (Math.random() * 10);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mChannel1 == null) {
                mChannel1 = new NotificationChannel
                        (id, "enquiries", importance);
                mChannel1.setDescription("");
                mChannel1.enableVibration(true);
                mNotificationManager.createNotificationChannel(mChannel1);
            }
        }
        Intent intent = null;
        //  sessionManager.setDharamshala_id(message.getData().get("ds_id"), message.getData().get("ds_name"));

        if (msg_type.equals("enquiry")) {
            intent = new Intent(this, MainScreen.class);
            intent.putExtra("from", 1);
            intent.putExtra("dsid", message.getData().get("ds_id"));
            intent.putExtra("dsnm", message.getData().get("ds_name"));
        } else if (msg_type.equals("booking")) {
            intent = new Intent(this, BookingListingActivity.class);
            intent.putExtra("from", 1);
            intent.putExtra("dsid", message.getData().get("ds_id"));
            intent.putExtra("dsnm", message.getData().get("ds_name"));

        } else {
            intent = new Intent(this, MainScreen.class);
        }
        intent.putExtra("enq_id", id);

        contentIntent = PendingIntent.getActivity(this, id1, intent,   PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        try {


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id)
                    .setContentTitle(message.getData().get("ds_name"))
                    .setContentText(message.getData().get("message"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setContentIntent(contentIntent)
                    .setSmallIcon(R.mipmap.ic_launcher_round);


      /*  NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(title);
        int size = notificationMessages.size();
        for (int i = 0; i < notificationMessages.size(); i++) {
            if (notificationMessages.get(i).getId().equals(message.getId())) {
                if (message.getCount() < 3) {
                    inboxStyle.addLine(notificationMessages.get(i).getMessage());
                }
                message.setCount(message.getCount() + 1);

            }
        }
        if (message.getCount() > 3) {
            int c = message.getCount() - 3;
            inboxStyle.setSummaryText("+" + c + " more");
        }*/
            //  builder.setContentIntent(pendingIntent);

            /*Notification notification = builder.build();

            String id1 = String.valueOf(System.currentTimeMillis());
            Log.d("TRWTRTRTRT", id1 + "");

            mNotificationManager.notify(id1,NotificationID.getID(), notification);*/

            if (mNotificationManager != null) {
                Log.d("YTYTAYYETYYW",id1+"===>");
                mNotificationManager.notify(id1, builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}