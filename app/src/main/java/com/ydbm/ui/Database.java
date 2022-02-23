package com.ydbm.ui;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by vihaan on 18/06/17.
 */

public class Database {

    //public static final String NODE_USERS = "users";
    // public static final String NODE_USER_CHATS = "users-chats";
    // public static final String NODE_MESSAGES = "messages";
    //  public static final String NODE_GROUP = "groupChat";
    public static final FirebaseDatabase FB_DB = FirebaseDatabase.getInstance();

    //for live

 /*   public static final DatabaseReference NODE_USERS_PATH = FB_DB.getReference("users");
    public static final DatabaseReference NODE_USER_CHATS_PATH = FB_DB.getReference("users-chats");
    public static final DatabaseReference NODE_MESSAGES_PATH = FB_DB.getReference("messages");
    public static final DatabaseReference NODE_INQUIRY_GROUP_LIST_PATH = FB_DB.getReference("groupinquiryList");
    public static final DatabaseReference NODE_INQUIRYCHAT_PATH = FB_DB.getReference("inquirychat");
    public static final DatabaseReference NODE_GROUP_LIST_PATH = FB_DB.getReference("groupList");
    public static final DatabaseReference NODE_GROUPCHAT_PATH = FB_DB.getReference("groupChat");
    public static final DatabaseReference NODE_MESSAGE_DELIVERED_PATH = FB_DB.getReference("messagesDelivery");
    public static final DatabaseReference NODE_EXCEPTIONS_PATH = FB_DB.getReference("exceptions");
*/

    //for test
    public static final DatabaseReference NODE_USERS_PATH = FB_DB.getReference("users");
    //  public static final DatabaseReference NODE_USER_CHATS_PATH = FB_DB.getReference("users-chatstest");
    public static final DatabaseReference NODE_MESSAGES_PATH = FB_DB.getReference("messagestest");
    public static final DatabaseReference NODE_GROUP_LIST_PATH = FB_DB.getReference("groupListtest");
    public static final DatabaseReference NODE_INQUIRY_GROUP_LIST_PATH = FB_DB.getReference("groupinquiryListtest");

    public static final DatabaseReference NODE_GROUPCHAT_PATH = FB_DB.getReference("groupChattest");
    public static final DatabaseReference NODE_INQUIRYCHAT_PATH = FB_DB.getReference("inquirychattest");
    public static final DatabaseReference NODE_MESSAGE_DELIVERED_PATH = FB_DB.getReference("messagesDeliverytest");

    public static final DatabaseReference NODE_EXCEPTIONS_PATH = FB_DB.getReference("exceptionsTest");


}
