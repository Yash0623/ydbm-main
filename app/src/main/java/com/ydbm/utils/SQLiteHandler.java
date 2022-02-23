package com.ydbm.utils;
/**
 * Created by Hyvikk-android2 on 18-05-2017.
 */
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


import com.ydbm.models.BookingDetails;
import com.ydbm.models.ChatMessage;
import com.ydbm.models.ChatRoom;
import com.ydbm.models.GroupSeen;
import com.ydbm.models.NotificationFailed;
import com.ydbm.models.RoomType;
import com.ydbm.models.UserDetails;
import com.ydbm.models.UserType;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();
    SharedPreferences sharedPreferencesbackup;
    private static String PREFNAME = "databackup";
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 8;

    // Database Name
    private static final String DATABASE_NAME = "yd_database";

    // Login table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_PLANS = "plans";
    private static final String TABLE_BOOKING_DETAILS = "booking_details";
    private static final String TABLE_OFFLINE_BOOKING_DETAILS = "booking_off_details";
    private static final String TABLE_OFFLINE_ROOM_TYPES = "room_types";
    private static final String TABLE_INVENTORY = "inventory";


    //trip table name
    private static final String TABLE_TRIP_MEDIA = "trips";

    private static final String KEY_TRIP_MEDIA_TRIP_ID = "trip_id";
    private static final String KEY_TRIP_MEDIA_MSG_TIMESTAMP = "msg_timestamp";

    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String TABLE_TOTAL_CONTACTS = "total_contacts";

    private static final String TABLE_GROUP = "user_group";

    private static final String OTHER_USER = "other_user";
    private static final String TABLE_Contacts = "contacts";
    private static final String TABLE_CONTACTS_NEW = "contacts_new";
    private static final String TABLE_MESSAGES = "messages";

    private static final String TABLE_ROOM = "rooms";
    private static final String TABLE_GRP_MSG_READ = "grp_msg_read";
    private static final String TABLE_ROOM_MSG = "room_msg";

    private static SQLiteHandler sInstance;
    private static final String KEY_TOTAL_CONTACT_ID = "id";
    private static final String KEY_TOTAL_CONTACT_COUNT = "count";
    private static final String KEY_TOTAL_CONTACT_TT = "tt";

    //plan table columns
    private static final String KEY_PLAN_ID = "plan_id";
    private static final String KEY_PLAN_TYPE = "plan_type";
    private static final String KEY_PLAN_AMOUNT = "plan_amt";
    private static final String KEY_PLAN_FEATURES = "plan_features";
    private static final String KEY_PLAN_VALIDITY = "plan_validity";
    private static final String KEY_PLAN_EXTRA_FIELD = "plan_extra";

    //Group ColumnDetails
    private static final String KEY_GROUP_ID = "grp_id";
    private static final String KEY_GROUP_NAME = "grp_name";
    private static final String KEY_GROUP_MEMBERS = "grp_members";
    private static final String KEY_GROUP_PIC = "grp_pic";
    private static final String KEY_GROUP_OWNER = "grp_owner";
    private static final String KEY_GROUP_MEMBER_FCM_IDS = "grp_members_fcm_ids";

    private static final String KEY_GROUP_MSG_READ_ID = "id";
    private static final String KEY_GROUP_MSG_READ_USERANAME = "grpuser_name";
    private static final String KEY_GROUP_MSG_READ__STATUS = "status";
    private static final String KEY_GROUP__MSG_READ_ROOM_ID = "roomid";


    //transaction table columns
    private static final String KEY_TRANSACTION_ID = "txn_id";
    private static final String KEY_TRANSACTION_AMT = "amount";
    private static final String KEY_TRANSACTION_TIMESTAMP = "timestamp";
    private static final String KEY_TRANSACTION_STATUS = "txn_status";
    private static final String KEY_TRANSACTION_TYPE = "txn_type";

    //Room column details
    private static final String KEY_ROOM_ID = "room_id";
    private static final String KEY_ROOM_USERNAME_SELF = "room_username_self";
    private static final String KEY_ROOM_USERNAME_OTHER = "room_username_other";
    private static final String KEY_ROOM_MSG_LAST_TEXT = "room_msg_last_txt";
    private static final String KEY_ROOM_MSG_LAST_TIMESTAMP = "room_msg_last_timestamp";
    private static final String KEY_ROOM_MSG_READ = "room_msg_read";
    private static final String KEY_ROOM_MSG_UNREAD_COUNT = "room_msg_unread_count";
    private static final String KEY_ROOM_OTHER_USER_STATUS = "room_user_status";
    private static final String KEY_ROOM_OTHER_USER_PIC = "room_orther_pic";
    private static final String KEY_ROOM_GROUP_PIC = "room_grp_pic";
    private static final String KEY_ROOM_GROUP_NAME = "room_grp_name";
    private static final String KEY_ROOM_GROUP_CREATION_DATE = "room_grp_create_date";
    private static final String KEY_ROOM_TYPE = "room_type"; //if type=p -->private group  if type=d -->direct message
    private static final String KEY_ROOM_GROUP_MEMBERS = "room_grp_members";
    private static final String KEY_ROOM_NAME = "room_name";
    private static final String KEY_ROOM_IS_BLOCKED = "room_is_blocked";
    private static final String KEY_ROOM_SELF_MOBILENUMBER = "room_self_mobile";
    private static final String KEY_ROOM_GROUP_BG_PIC = "room_bg_pic";


    // Login Table Columns names
    private static final String KEY_USER_ACTIVE = "user_active";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_USERNAME = "user_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_PROFILE_PIC = "profile_pic";
    private static final String KEY_BACKGROUND_PIC = "background_pic";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ANNIVERSARY = "anniversary";
    private static final String KEY_BIRTHDATE = "birthdate";
    private static final String KEY_USER_STATUS = "user_status";
    private static final String KEY_USER_FCM_TOKEN = "fcm_token";
    private static final String KEY_USER_COUNTRY_CODE = "country_code";
    private static final String KEY_CONTACT_ID = "contact_id";
    private static final String KEY_CONTACT_NAME = "contact_name";
    private static final String KEY_CONTACT_NUMBER = "contact_number";
    private static final String KEY_CONTACT_PHOTO = "contact_photo";
    private static final String KEY_CONTACT_RC_USERNAME = "user_name";
    private static final String KEY_CONTACT_RC_RNAME = "user_rcname";
    private static final String KEY_CONTACT_EMAIL = "email";
    private static final String KEY_CONTACT_GENDER = "gender";
    private static final String KEY_CONTACT_PROFILE_PIC = "profile_pic";
    private static final String KEY_CONTACT_BACKGROUND_PIC = "background_pic";
    private static final String KEY_CONTACT_PASSWORD = "password";
    private static final String KEY_CONTACT_ANNIVERSARY = "anniversary";
    private static final String KEY_CONTACT_BIRTHDATE = "birthdate";
    private static final String KEY_CONTACT_TYPE = "user_typ"; //u or i or d
    private static final String KEY_CONTACT_ISMULTIPLE_NUMBER = "is_multiple_number"; //u or i
    private static final String KEY_CONTACT_USER_STATUS = "user_status";
    private static final String KEY_CONTACT_USER_COUNTRY_CODE = "country_code";
    private static final String KEY_CONTACT_ACTIVE = "active_status";
    private static final String KEY_CONTACT_RC_USERID = "rc_userid";
    private static final String KEY_CONTACT_LAST_UPDATE = "last_update";
    private static final String KEY_CONTACT_LOCAL_LAST_UPDATE = "last_local_update";
    private static final String KEY_CONTACT_FCM_TOKEN = "fcm_token";
    private static final String KEY_CONTACT_ISBLOCKED = "is_contact_blocked"; //0 or 1

    //MSG-ROOMS
    private static final String KEY_MMSG_ID = "mmsg_id";
    private static final String KEY_RROOM_ID = "rroom_id";

    //Message Table Column names
    private static final String KEY_MSG_UNIQUE_ID = "msg_unique_id";
    private static final String KEY_MSG_ID = "msg_id";
    private static final String KEY_FROM_ME = "msg_key_from_me"; //0-incoming   1-outgoing
    private static final String KEY_MSG_TYPE = "msg_type";  //0-text  1-image 2-audio 3-geo position
    private static final String KEY_MSG_DATA = "msg_text"; //if type-0

    private static final String KEY_MSG_STATUS = "msg_status";  //if 1-sent 4-waiting
    private static final String KEY_MSG_PARTNER_USERNAME = "msg_partner_username";
    private static final String KEY_MSG_TIMESTAMP = "msg_timestamp"; //if type-1 type-2
    private static final String KEY_MSG_RECEIVED_TIMESTAMP = "msg_recieved_timestamp";
    private static final String KEY_MSG_RECIEPT_TIMESTAMP = "msg_reciept_timestamp";
    private static final String KEY_MSG_MEDIA_ISUPLOADED = "msg_media_isdownloaded";
    private static final String KEY_MSG_MEDIA_ISDOWNLOADED = "msg_reciept_isuploaded";
    private static final String KEY_MSG_READ_STATUS = "msg_read_status";
    private static final String KEY_MSG_ROOM_ID = "msg_room_id";
    private static final String KEY_MSG_TYPE_TC = "msg_typ_tc";
    private static final String KEY_MSG_NOTI_FAILED = "msg_noti_failed";

    //Notifications
    private static final String TABLE_NOTIFICATIONS = "notifications";

    private static final String KEY_NOTI_MSG_ID = "noti_msgid";
    private static final String KEY_NOTI_TITLE = "noti_title";
    private static final String KEY_NOTI_BODY = "noti_body";
    private static final String KEY_NOTI_MSGTRIPID = "noti_msgtripId";
    private static final String KEY_NOTI_MSG_SENDER = "noti_msgtripsenderTT";
    private static final String KEY_NOTI_MSG_TYP = "noti_msg_typ";
    private static final String KEY_NOTI_GROUPNAME = "noti_grpname";
    private static final String KEY_NOTI_TYPE = "noti_type";
    private static final String KEY_NOTI_SEND_TO = "noti_send_to";
    private static final String KEY_NOTI_OTHER_USER = "noti_other_user";


    private static final String KEY_BOOKING_ID = "booking_id";
    private static final String KEY_BOOKING_DID = "d_id";
    private static final String KEY_BOOKING_DS_NM = "ds_name";
    private static final String KEY_BOOKING_YATRI_NAME = "yatri_name";
    private static final String KEY_BOOKING_YATRIADDRESS = "address";
    private static final String KEY_BOOKING_Y_MOB_NO = "mob_no";
    private static final String KEY_BOOKING_ROOM_TYPE = "room_type";
    private static final String KEY_BOOKING_CHECK_IN = "checkin";
    private static final String KEY_BOOKING_CHECKOUT = "checkout";
    private static final String KEY_BOOKING_NO_ROOM = "rooms";
    private static final String KEY_BOOKING_GUESTS = "guests";
    private static final String KEY_BOOKING_CONTIBUTION = "contibution";
    private static final String KEY_BOOKING_SUBTOTAL = "subtotal";
    private static final String KEY_BOOKING_NEFT_NO = "neftno";
    private static final String KEY_BOOKING_TRANFER_DATE = "tranfer_dt";
    private static final String KEY_BOOKING_TRANFER_AMT = "tranfer_amt";
    private static final String KEY_BOOKING_EXPECTED_DATETIME = "expected_time";
    private static final String KEY_BOOKING_ORDER_STATUS = "order_status";
    private static final String KEY_BOOKING_TYPE = "booking_type";
    private static final String KEY_EXTRA_FIELD = "booking_extra_field";
    private static final String KEY_EXTRA_FIELD1 = "booking_extra_field1";
    private static final String KEY_EXTRA_FIELD2 = "booking_extra_field2";
    private static final String KEY_EXTRA_FIELD3 = "booking_extra_field3";
    private static final String KEY_NIGHTS = "nights";

    //Room Types
    private static final String KEY_ROOM_TYPE_DID = "room_type_did";
    private static final String KEY_ROOM_TYPE_NAME = "room_type_name";
    private static final String KEY_ROOM_TYPE_ROOM_ID = "room_type_room_id";


    //Offline Bookings
    private static final String KEY_OFF_BOOKING_ID = "booking_id";
    private static final String KEY_OFF_BOOKING_DID = "d_id";
    private static final String KEY_OFF_BOOKING_DS_NM = "ds_name";
    private static final String KEY_OFF_BOOKING_YATRI_NAME = "yatri_name";
    private static final String KEY_OFF_BOOKING_YATRIADDRESS = "address";
    private static final String KEY_OFF_BOOKING_Y_MOB_NO = "mob_no";
    private static final String KEY_OFF_BOOKING_ROOM_TYPE = "room_type";
    private static final String KEY_OFF_BOOKING_CHECK_IN = "checkin";
    private static final String KEY_OFF_BOOKING_CHECKOUT = "checkout";
    private static final String KEY_OFF_BOOKING_NO_ROOM = "rooms";
    private static final String KEY_OFF_BOOKING_GUESTS = "guests";
    private static final String KEY_OFF_BOOKING_CONTIBUTION = "contibution";
    private static final String KEY_OFF_BOOKING_SUBTOTAL = "subtotal";
    private static final String KEY_OFF_BOOKING_NEFT_NO = "neftno";
    private static final String KEY_OFF_BOOKING_TRANFER_DATE = "tranfer_dt";
    private static final String KEY_OFF_BOOKING_TRANFER_AMT = "tranfer_amt";
    private static final String KEY_OFF_BOOKING_EXPECTED_DATETIME = "expected_time";
    private static final String KEY_OFF_BOOKING_ORDER_STATUS = "order_status";
    private static final String KEY_OFF_BOOKING_TYPE = "booking_type";
    private static final String KEY_OFF_ROOM_ID = "booking_extra_field";
    private static final String KEY_OFF_EXTRA_FIELD1 = "booking_extra_field1";
    private static final String KEY_OFF_EXTRA_FIELD2 = "booking_extra_field2";
    private static final String KEY_OFF_EXTRA_FIELD3 = "booking_extra_field3";
    private static final String KEY_OFF_NIGHTS = "nights";

    private static final String KEY_INEVNTORY_BOOKING_ID = "inventory_booking_id";
    private static final String KEY_INEVNTORY_CHECKIN = "in_checkin";
    private static final String KEY_INEVNTORY_ROOM_ID = "inventory_room_id";
    private static final String KEY_INEVNTORY_TOTAL = "total";
    private static final String KEY_INEVNTORY_EXTRAFIELD = "extra";
    private static final String KEY_INEVNTORY_EXTRAFIELD1 = "extra2";


    Context context;

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // sInstance=SQLiteHandler.getInstance(context);
        this.context = context;
        sharedPreferencesbackup = context.getSharedPreferences(PREFNAME, 0);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USER_ID + " TEXT," + KEY_NAME + " TEXT," + KEY_PASSWORD + " TEXT,"
                + KEY_EMAIL + " TEXT, " + KEY_PHONE + " TEXT ," + KEY_GENDER + " TEXT ," +
                KEY_PROFILE_PIC + " TEXT ," + KEY_BACKGROUND_PIC + " TEXT ," + KEY_ANNIVERSARY + " TEXT ," + KEY_BIRTHDATE + " TEXT ," + KEY_USER_COUNTRY_CODE + " TEXT ," + KEY_USER_FCM_TOKEN + " TEXT ," + KEY_USER_STATUS + " TEXT " + ")";
        try {
            db.execSQL(CREATE_USER_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        String CREATE_MESSAGE_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_MSG_UNIQUE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  , "
                + KEY_MSG_ID + " TEXT UNIQUE , " + KEY_FROM_ME + " TEXT," + KEY_MSG_TYPE + " TEXT,"
                + KEY_MSG_DATA + " TEXT, " + KEY_MSG_STATUS + " TEXT ," + KEY_MSG_PARTNER_USERNAME + " TEXT ," + KEY_MSG_TIMESTAMP + " TEXT ," + KEY_MSG_RECEIVED_TIMESTAMP + " TEXT ," + KEY_MSG_MEDIA_ISUPLOADED + " TEXT ," + KEY_MSG_MEDIA_ISDOWNLOADED + " TEXT ," + KEY_MSG_RECIEPT_TIMESTAMP + " TEXT , " + KEY_MSG_ROOM_ID + " TEXT , " + KEY_MSG_READ_STATUS + " TEXT , " + KEY_MSG_TYPE_TC + " TEXT , " + KEY_MSG_NOTI_FAILED + " TEXT " + ")";
        try {
            db.execSQL(CREATE_MESSAGE_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        String CREATE_ROOM_TABLE = "CREATE TABLE " + TABLE_ROOM + "("
                + KEY_ROOM_ID + " TEXT UNIQUE, " + KEY_ROOM_USERNAME_SELF + " TEXT ," + KEY_ROOM_USERNAME_OTHER + " TEXT ," + KEY_ROOM_MSG_LAST_TEXT + " TEXT ," + KEY_ROOM_MSG_LAST_TIMESTAMP + " TEXT ," + KEY_ROOM_MSG_READ + " TEXT ," + KEY_ROOM_OTHER_USER_PIC + " TEXT ," + KEY_ROOM_OTHER_USER_STATUS + " TEXT ," + KEY_ROOM_MSG_UNREAD_COUNT + " TEXT ," + KEY_ROOM_GROUP_NAME + " TEXT ," + KEY_ROOM_GROUP_PIC + " TEXT ," + KEY_ROOM_TYPE + " TEXT ," + KEY_ROOM_GROUP_CREATION_DATE + " TEXT ," + KEY_ROOM_NAME + " TEXT ," + KEY_ROOM_IS_BLOCKED + " TEXT ," + KEY_ROOM_GROUP_MEMBERS + " TEXT ," + KEY_ROOM_SELF_MOBILENUMBER + " TEXT " + ")";
        try {
            db.execSQL(CREATE_ROOM_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        String CREATE_MSG_READ_TABLE = "CREATE TABLE " + TABLE_GRP_MSG_READ + "("
                + KEY_GROUP_MSG_READ_ID + " TEXT, " + KEY_GROUP_MSG_READ_USERANAME + " TEXT ," + KEY_GROUP_MSG_READ__STATUS + " TEXT ," + KEY_GROUP__MSG_READ_ROOM_ID + " TEXT " + ")";
        db.execSQL(CREATE_MSG_READ_TABLE);


        String CREATE_GROUP_TABLE = "CREATE TABLE " + TABLE_GROUP + "("
                + KEY_GROUP_ID + " TEXT UNIQUE, " + KEY_GROUP_OWNER + " TEXT ," + KEY_GROUP_PIC + " TEXT ," + KEY_GROUP_NAME + " TEXT ," + KEY_GROUP_MEMBERS + " TEXT ," + KEY_GROUP_MEMBER_FCM_IDS + " TEXT " + ")";
        try {
            db.execSQL(CREATE_GROUP_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        String CREATE_ROOM_MSG_TABLE = "CREATE TABLE " + TABLE_ROOM_MSG + "("
                + KEY_MMSG_ID + " TEXT , " + KEY_RROOM_ID + " TEXT " + ")";
        try {
            db.execSQL(CREATE_ROOM_MSG_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        String CREATE_STUDENT_TABLE = "CREATE TABLE " + TABLE_Contacts + "("
                + KEY_CONTACT_ID + " TEXT, " + KEY_CONTACT_NAME + " TEXT , " + KEY_CONTACT_PHOTO + " TEXT , " + KEY_CONTACT_NUMBER + " TEXT" + ")";
        try {
            db.execSQL(CREATE_STUDENT_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        String CREATE_NOTI_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + KEY_NOTI_MSG_ID + " TEXT, " + KEY_NOTI_BODY + " TEXT , " + KEY_NOTI_GROUPNAME + " TEXT , " + KEY_NOTI_MSG_TYP + " TEXT , " + KEY_NOTI_MSGTRIPID + " TEXT , " + KEY_NOTI_TITLE + " TEXT , " + KEY_NOTI_TYPE + " TEXT , " + KEY_NOTI_SEND_TO + " TEXT , " + KEY_NOTI_MSG_SENDER + " TEXT , " + KEY_NOTI_OTHER_USER + " TEXT " + ")";
        try {
            db.execSQL(CREATE_NOTI_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        String CREATE_OTHER_USER_TABLE = "CREATE TABLE " + OTHER_USER + "("
                + KEY_USER_ID + " TEXT ," + KEY_NAME + " TEXT ," + KEY_PHONE + " TEXT ," +
                KEY_PROFILE_PIC + " TEXT ," + KEY_BACKGROUND_PIC + " TEXT ," + KEY_USER_COUNTRY_CODE + " TEXT ," + KEY_USER_ACTIVE + " TEXT ," + KEY_USER_STATUS + " TEXT " + ")";
        try {
            db.execSQL(CREATE_OTHER_USER_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        String CREATE_BOOKIN_DETAILS = "CREATE TABLE " + TABLE_BOOKING_DETAILS + "("
                + KEY_BOOKING_ID + " TEXT UNIQUE," + KEY_BOOKING_DID + " TEXT ," + KEY_BOOKING_DS_NM + " TEXT ," +
                KEY_BOOKING_YATRI_NAME + " TEXT ," + KEY_BOOKING_YATRIADDRESS + " TEXT ," + KEY_BOOKING_Y_MOB_NO + " TEXT ," + KEY_BOOKING_CHECK_IN + " TEXT ," + KEY_BOOKING_CHECKOUT + " TEXT ," +
                KEY_BOOKING_NO_ROOM + " TEXT ," + KEY_BOOKING_GUESTS + " TEXT ," +
                KEY_BOOKING_CONTIBUTION + " TEXT ," + KEY_BOOKING_SUBTOTAL + " TEXT ," + KEY_BOOKING_NEFT_NO + " TEXT ," + KEY_BOOKING_TRANFER_DATE + " TEXT ," + KEY_BOOKING_TRANFER_AMT + " TEXT , "
                + KEY_BOOKING_ORDER_STATUS + " TEXT ," + KEY_BOOKING_EXPECTED_DATETIME + " TEXT ," + KEY_BOOKING_ROOM_TYPE + " TEXT ," + KEY_BOOKING_TYPE + " TEXT ," + KEY_EXTRA_FIELD + " TEXT ," + KEY_EXTRA_FIELD1 + " TEXT ," + KEY_EXTRA_FIELD2 + " TEXT ," + KEY_EXTRA_FIELD3 + " TEXT ," + KEY_NIGHTS + " TEXT " + ")";
        try {
            db.execSQL(CREATE_BOOKIN_DETAILS);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }


        String CREATE_OFFLINE_BOOKIN_DETAILS = "CREATE TABLE " + TABLE_OFFLINE_BOOKING_DETAILS + "("
                + KEY_OFF_BOOKING_ID + " TEXT UNIQUE," + KEY_OFF_BOOKING_DID + " TEXT ," + KEY_OFF_BOOKING_DS_NM + " TEXT ," +
                KEY_OFF_BOOKING_YATRI_NAME + " TEXT ," + KEY_OFF_BOOKING_YATRIADDRESS + " TEXT ," + KEY_OFF_BOOKING_Y_MOB_NO + " TEXT ," + KEY_OFF_BOOKING_CHECK_IN + " TEXT ," + KEY_OFF_BOOKING_CHECKOUT + " TEXT ," +
                KEY_OFF_BOOKING_NO_ROOM + " TEXT ," + KEY_OFF_BOOKING_GUESTS + " TEXT ," +
                KEY_OFF_BOOKING_CONTIBUTION + " TEXT ," + KEY_OFF_BOOKING_SUBTOTAL + " TEXT ," + KEY_OFF_BOOKING_NEFT_NO + " TEXT ," + KEY_OFF_BOOKING_TRANFER_DATE + " TEXT ," + KEY_OFF_BOOKING_TRANFER_AMT + " TEXT , "
                + KEY_OFF_BOOKING_ORDER_STATUS + " TEXT ," + KEY_OFF_BOOKING_EXPECTED_DATETIME + " TEXT ," + KEY_OFF_BOOKING_ROOM_TYPE + " TEXT ," + KEY_OFF_BOOKING_TYPE + " TEXT ," + KEY_OFF_ROOM_ID + " TEXT ," + KEY_OFF_EXTRA_FIELD1 + " TEXT ," + KEY_OFF_EXTRA_FIELD2 + " TEXT ," + KEY_OFF_EXTRA_FIELD3 + " TEXT ," + KEY_OFF_NIGHTS + " TEXT " + ")";
        try {
            db.execSQL(CREATE_OFFLINE_BOOKIN_DETAILS);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        String CREATE_OFFLINE_ROOMTYPES = "CREATE TABLE " + TABLE_OFFLINE_ROOM_TYPES + "("
                + KEY_ROOM_TYPE_DID + " TEXT , " + KEY_ROOM_TYPE_ROOM_ID + " TEXT ," + KEY_ROOM_TYPE_NAME + " TEXT " + ")";
        try {
            db.execSQL(CREATE_OFFLINE_ROOMTYPES);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        String CREATE_INVENTORY_TABLE = "CREATE TABLE " + TABLE_INVENTORY + "("
                + KEY_INEVNTORY_BOOKING_ID + " TEXT, " + KEY_INEVNTORY_CHECKIN + " TEXT , " + KEY_INEVNTORY_ROOM_ID + " TEXT , " + KEY_INEVNTORY_TOTAL + " TEXT , " + KEY_INEVNTORY_EXTRAFIELD + " TEXT , " + KEY_INEVNTORY_EXTRAFIELD1 + " TEXT" + ")";
        try {
            db.execSQL(CREATE_INVENTORY_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
     /*   db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOM_MSG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Contacts);
        db.execSQL("DROP TABLE IF EXISTS " + OTHER_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRP_MSG_READ);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);*/
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING_DETAILS);
        String CREATE_BOOKIN_DETAILS = "CREATE TABLE " + TABLE_BOOKING_DETAILS + "("
                + KEY_BOOKING_ID + " TEXT UNIQUE," + KEY_BOOKING_DID + " TEXT ," + KEY_BOOKING_DS_NM + " TEXT ," +
                KEY_BOOKING_YATRI_NAME + " TEXT ," + KEY_BOOKING_YATRIADDRESS + " TEXT ," + KEY_BOOKING_Y_MOB_NO + " TEXT ," + KEY_BOOKING_CHECK_IN + " TEXT ," + KEY_BOOKING_CHECKOUT + " TEXT ," +
                KEY_BOOKING_NO_ROOM + " TEXT ," + KEY_BOOKING_GUESTS + " TEXT ," +
                KEY_BOOKING_CONTIBUTION + " TEXT ," + KEY_BOOKING_SUBTOTAL + " TEXT ," + KEY_BOOKING_NEFT_NO + " TEXT ," + KEY_BOOKING_TRANFER_DATE + " TEXT ," + KEY_BOOKING_TRANFER_AMT + " TEXT , "
                + KEY_BOOKING_ORDER_STATUS + " TEXT ," + KEY_BOOKING_EXPECTED_DATETIME + " TEXT ," + KEY_BOOKING_ROOM_TYPE + " TEXT ," + KEY_BOOKING_TYPE + " TEXT ," + KEY_EXTRA_FIELD + " TEXT ," + KEY_EXTRA_FIELD1 + " TEXT ," + KEY_EXTRA_FIELD2 + " TEXT ," + KEY_EXTRA_FIELD3 + " TEXT ," + KEY_NIGHTS + " TEXT " + ")";
        try {
            db.execSQL(CREATE_BOOKIN_DETAILS);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (oldVersion == 2 || oldVersion == 3) {
            String DATABASE_ALTER_TEAM_1 = "ALTER TABLE "
                    + TABLE_BOOKING_DETAILS + " ADD COLUMN " + KEY_EXTRA_FIELD1 + " TEXT;";
            String DATABASE_ALTER_TEAM_2 = "ALTER TABLE "
                    + TABLE_BOOKING_DETAILS + " ADD COLUMN " + KEY_EXTRA_FIELD2 + " TEXT;";
            String DATABASE_ALTER_TEAM_3 = "ALTER TABLE "
                    + TABLE_BOOKING_DETAILS + " ADD COLUMN " + KEY_EXTRA_FIELD3 + " TEXT;";
            String DATABASE_ALTER_TEAM_4 = "ALTER TABLE "
                    + TABLE_BOOKING_DETAILS + " ADD COLUMN " + KEY_NIGHTS + " TEXT;";
            try {
                db.execSQL(DATABASE_ALTER_TEAM_1);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(DATABASE_ALTER_TEAM_2);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            try {
                Log.d("DLJDKJDKJD", "1");
                db.execSQL(DATABASE_ALTER_TEAM_3);
            } catch (SQLiteException e) {
                e.printStackTrace();
                Log.d("DLJDKJDKJD", "2");


            }
            try {
                db.execSQL(DATABASE_ALTER_TEAM_4);
            } catch (SQLiteException e) {

            }
        }
        // Create tables again
        String CREATE_OFFLINE_BOOKIN_DETAILS = "CREATE TABLE " + TABLE_OFFLINE_BOOKING_DETAILS + "("
                + KEY_OFF_BOOKING_ID + " TEXT UNIQUE," + KEY_OFF_BOOKING_DID + " TEXT ," + KEY_OFF_BOOKING_DS_NM + " TEXT ," +
                KEY_OFF_BOOKING_YATRI_NAME + " TEXT ," + KEY_OFF_BOOKING_YATRIADDRESS + " TEXT ," + KEY_OFF_BOOKING_Y_MOB_NO + " TEXT ," + KEY_OFF_BOOKING_CHECK_IN + " TEXT ," + KEY_OFF_BOOKING_CHECKOUT + " TEXT ," +
                KEY_OFF_BOOKING_NO_ROOM + " TEXT ," + KEY_OFF_BOOKING_GUESTS + " TEXT ," +
                KEY_OFF_BOOKING_CONTIBUTION + " TEXT ," + KEY_OFF_BOOKING_SUBTOTAL + " TEXT ," + KEY_OFF_BOOKING_NEFT_NO + " TEXT ," + KEY_OFF_BOOKING_TRANFER_DATE + " TEXT ," + KEY_OFF_BOOKING_TRANFER_AMT + " TEXT , "
                + KEY_OFF_BOOKING_ORDER_STATUS + " TEXT ," + KEY_OFF_BOOKING_EXPECTED_DATETIME + " TEXT ," + KEY_OFF_BOOKING_ROOM_TYPE + " TEXT ," + KEY_OFF_BOOKING_TYPE + " TEXT ," + KEY_OFF_ROOM_ID + " TEXT ," + KEY_OFF_EXTRA_FIELD1 + " TEXT ," + KEY_OFF_EXTRA_FIELD2 + " TEXT ," + KEY_OFF_EXTRA_FIELD3 + " TEXT ," + KEY_OFF_NIGHTS + " TEXT " + ")";
        try {
            db.execSQL(CREATE_OFFLINE_BOOKIN_DETAILS);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        String CREATE_OFFLINE_ROOMTYPES = "CREATE TABLE " + TABLE_OFFLINE_ROOM_TYPES + "("
                + KEY_ROOM_TYPE_DID + " TEXT , " + KEY_ROOM_TYPE_ROOM_ID + " TEXT ," + KEY_ROOM_TYPE_NAME + " TEXT " + ")";
        try {
            db.execSQL(CREATE_OFFLINE_ROOMTYPES);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        String CREATE_INVENTORY_TABLE = "CREATE TABLE " + TABLE_INVENTORY + "("
                + KEY_INEVNTORY_BOOKING_ID + " TEXT, " + KEY_INEVNTORY_CHECKIN + " TEXT , " + KEY_INEVNTORY_ROOM_ID + " TEXT , " + KEY_INEVNTORY_TOTAL + " TEXT , " + KEY_INEVNTORY_EXTRAFIELD + " TEXT , " + KEY_INEVNTORY_EXTRAFIELD1 + " TEXT" + ")";
        try {
            db.execSQL(CREATE_INVENTORY_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public long addInventory(String bookingId, String checkIn, String total, String did) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_INEVNTORY_BOOKING_ID, bookingId); // Name
        values.put(KEY_INEVNTORY_CHECKIN, checkIn); // Name
        values.put(KEY_INEVNTORY_TOTAL, total);
        values.put(KEY_INEVNTORY_EXTRAFIELD, did);
        long id_res = db.insert(TABLE_INVENTORY, null, values);
        // db.close(); // Closing database connection
        Log.d(TAG, "New Inventory inserted into sqlite : " + id_res);
        return id_res;

    }

    public int updateInventoryData(String msgId, String member) {
        Log.d("CHECKTHREFLOW", "in update -->" + msgId + "---" + member);
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;
        String timeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
        try {
            ContentValues CV = new ContentValues();


            CV.put(KEY_GROUP_MSG_READ__STATUS, timeStamp);
            //values.put(KEY_USERNAME, username); // Name

            int rows = db.update(TABLE_GRP_MSG_READ, CV, KEY_GROUP_MSG_READ_ID + " = ? AND " + KEY_GROUP_MSG_READ_USERANAME + " = ? AND " + KEY_GROUP_MSG_READ__STATUS + " = ?", new String[]{msgId, member, "0"});
            u = rows;
            Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");
            Log.d("CHECKTHREFLOW", "result-- -->" + u);

            // String wheres = KEY_USER_ID + "=?";
            // String[] whereArgs = {user_id.toString()};
            //db.update(OTHER_USER, CV, wheres, whereArgs);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    public ArrayList<String> getGrpUnseenMsgs(String roomId, String member) {

        ArrayList<String> roomMembers = new ArrayList<>();
        String sttus = "0";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Log.d("CHECKTHREFLOW", "in getGrpUnseeenn -->" + roomId + "---" + member + "-->" + sttus);
        Log.d("CHECK00133234", roomId + "---" + member);
        try {
            cursor = db.query(TABLE_GRP_MSG_READ, new String[]{KEY_GROUP_MSG_READ_ID}, KEY_GROUP__MSG_READ_ROOM_ID + "=? AND " + KEY_GROUP_MSG_READ_USERANAME + "=? AND " + KEY_GROUP_MSG_READ__STATUS + "=?", new String[]{roomId, member, sttus}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list

        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            do {
                roomMembers.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return roomMembers;
    }

    public int updateSeenData(String msgId, String member) {
        Log.d("CHECKTHREFLOW", "in update -->" + msgId + "---" + member);
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;
        String timeStamp = ConvertGMTtoIST.convertISTtoGMTFormat();
        try {
            ContentValues CV = new ContentValues();


            CV.put(KEY_GROUP_MSG_READ__STATUS, timeStamp);
            //values.put(KEY_USERNAME, username); // Name

            int rows = db.update(TABLE_GRP_MSG_READ, CV, KEY_GROUP_MSG_READ_ID + " = ? AND " + KEY_GROUP_MSG_READ_USERANAME + " = ? AND " + KEY_GROUP_MSG_READ__STATUS + " = ?", new String[]{msgId, member, "0"});
            u = rows;
            Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");
            Log.d("CHECKTHREFLOW", "result-- -->" + u);

            // String wheres = KEY_USER_ID + "=?";
            // String[] whereArgs = {user_id.toString()};
            //db.update(OTHER_USER, CV, wheres, whereArgs);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }


    public int checkOfflineInventoryIfexist(String bookingId, String roomId, String checkIn, String did, int countAdd) {
        boolean existIn = false;
        int i = 0;
        ArrayList<String> roomMembers = new ArrayList<>();
        String sttus = "0";

        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db1 = this.getWritableDatabase();
        Cursor cursor = null;
        Log.d("CHECKTHREFLOW", "in getGrpUnseeenn -->" + roomId + "---" + checkIn + "-->" + did);

        Log.d("ADHKHJKHJHJADH", roomId + "---" + checkIn + "-->" + did + "=====8");
        //   Log.d("CHECK00133234", roomId + "---" + member);
        try {
            cursor = db.query(TABLE_INVENTORY, new String[]{KEY_INEVNTORY_TOTAL}, KEY_INEVNTORY_ROOM_ID + "=? AND " + KEY_INEVNTORY_CHECKIN + "=? AND " + KEY_INEVNTORY_EXTRAFIELD + "=?", new String[]{roomId, checkIn, did}, null, null, null);
            Log.d("ADHKHJKHJHJADH", cursor.getCount() + "====-");
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list

            Log.d("JKAJDKAKJ", cursor.getCount() + "");
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst()) {


            if (cursor.getCount() > 0) {
                int count = Integer.parseInt(cursor.getString(0));
                int newCount = count + countAdd;
                ContentValues CV = new ContentValues();


                CV.put(KEY_INEVNTORY_TOTAL, newCount + "");
                int rows = db1.update(TABLE_INVENTORY, CV, KEY_INEVNTORY_ROOM_ID + "=? AND " + KEY_INEVNTORY_CHECKIN + "=? AND " + KEY_INEVNTORY_EXTRAFIELD + "=?", new String[]{roomId, checkIn, did});
                i = rows;
                Log.d("ADHKHJKHJHJADH", count + "===" + newCount + "==" + countAdd + "===" + rows + "==>2");

            } else {
                ContentValues values = new ContentValues();
                values.put(KEY_INEVNTORY_BOOKING_ID, bookingId); // Name
                values.put(KEY_INEVNTORY_CHECKIN, checkIn); // Name
                values.put(KEY_INEVNTORY_TOTAL, countAdd);
                values.put(KEY_INEVNTORY_EXTRAFIELD, did);
                long id_res = db.insert(TABLE_INVENTORY, null, values);
                Log.d("ADHKHJKHJHJADH", id_res + "=====1");
            }

        } else {
            ContentValues values = new ContentValues();
            values.put(KEY_INEVNTORY_BOOKING_ID, bookingId); // Name
            values.put(KEY_INEVNTORY_CHECKIN, checkIn); // Name
            values.put(KEY_INEVNTORY_TOTAL, countAdd);
            values.put(KEY_INEVNTORY_EXTRAFIELD, did);
            values.put(KEY_INEVNTORY_ROOM_ID, roomId);
            long id_res = db.insert(TABLE_INVENTORY, null, values);
            Log.d("ADHKHJKHJHJADH", id_res + "=====4");
        }
        return i;
    }

    //check this
    public int getGrpUnseenMsgs3(String roomId, String msgId) {
        Log.d("JJAHJHAA", roomId + "--" + msgId);
        int count = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_GRP_MSG_READ, new String[]{KEY_GROUP_MSG_READ_ID}, KEY_GROUP__MSG_READ_ROOM_ID + "=? AND " + KEY_GROUP_MSG_READ_ID + "=? AND " + KEY_GROUP_MSG_READ__STATUS + "=?", new String[]{roomId, msgId, "0"}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            Log.d("JJAHJHAA", cursor.getCount() + "--");
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst())
            count = cursor.getCount();

        return count;
    }

    public ArrayList<String> getGALLMSGS(String roomId) {
        ArrayList<String> roomMembers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Log.d("CHECK00133234", roomId + "---");
        try {
            cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_ID}, KEY_MSG_TYPE + "=? AND " + KEY_MSG_ROOM_ID + "=?", new String[]{"0", roomId}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            Log.d("CHECK00133234", cursor.getCount() + "--->>");
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst()) {


            do {
//                Log.d("CHECK00133234",cursor.getString(1)+"--->>"+cursor.getString(0));
                roomMembers.add(cursor.getString(0));

            } while (cursor.moveToNext());
        }
        return roomMembers;
    }

    public ArrayList<GroupSeen> getGrpSeenMsgs(String msgId) {
        ArrayList<GroupSeen> roomMembers = new ArrayList<>();
        Log.d("CEHCK000111", msgId + "<<<---");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_GRP_MSG_READ, new String[]{KEY_GROUP_MSG_READ_USERANAME, KEY_GROUP_MSG_READ__STATUS}, KEY_GROUP_MSG_READ_ID + "=? AND " + KEY_GROUP_MSG_READ__STATUS + "!=?", new String[]{msgId, "0"}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            Log.d("CEHCK000111", cursor.getCount() + "---");

        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    GroupSeen groupSeen = new GroupSeen();
                    groupSeen.setUserName(cursor.getString(0));
                    groupSeen.setTimeStamp(cursor.getString(1));
                    roomMembers.add(groupSeen);
                } while (cursor.moveToNext());
            }
        }
        return roomMembers;


    }

    private Long generateUniqueId() {
        long val = -1;

        do {
            final UUID uid = UUID.randomUUID();
            final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
            buffer.putLong(uid.getLeastSignificantBits());
            buffer.putLong(uid.getMostSignificantBits());
            final BigInteger bi = new BigInteger(buffer.array());
            val = bi.longValue();
        }
        // We also make sure that the ID is in positive space, if its not we simply repeat the process
        while (val < 0);
        return val;
    }

    public long addMessage(String KEY_MSG_ID1, String KEY_MSG_DATA1, String KEY_FROM_ME1, String KEY_MSG_TYPE1, String KEY_MSG_STATUS1, String KEY_MSG_RECEIVED_TIMESTAMP1, String KEY_MSG_PARTNER_USERNAME1, String KEY_MSG_TIMESTAMP1, String KEY_MSG_RECIEPT_TIMESTAMP1, String isDownloded, String isUploaded, String readStatus, String roomId, String msgType) {
        KEY_MSG_RECEIVED_TIMESTAMP1 = String.valueOf(generateUniqueId());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MSG_ID, KEY_MSG_ID1); // Name
        values.put(KEY_MSG_DATA, KEY_MSG_DATA1); // Name
        //values.put(KEY_USERNAME, username); // Name
        values.put(KEY_FROM_ME, KEY_FROM_ME1); // Name
        values.put(KEY_MSG_TYPE, KEY_MSG_TYPE1); // Email

        //  values.put(KEY_USER_TOKEN, userToken);

        values.put(KEY_MSG_STATUS, KEY_MSG_STATUS1);
        values.put(KEY_MSG_PARTNER_USERNAME, KEY_MSG_PARTNER_USERNAME1);
        values.put(KEY_MSG_TIMESTAMP, KEY_MSG_TIMESTAMP1);
        values.put(KEY_MSG_RECEIVED_TIMESTAMP, KEY_MSG_RECEIVED_TIMESTAMP1);
        values.put(KEY_MSG_RECIEPT_TIMESTAMP, KEY_MSG_RECIEPT_TIMESTAMP1);
        values.put(KEY_MSG_MEDIA_ISDOWNLOADED, isDownloded);
        values.put(KEY_MSG_MEDIA_ISUPLOADED, isUploaded);
        values.put(KEY_MSG_READ_STATUS, readStatus);
        values.put(KEY_MSG_ROOM_ID, roomId);
        values.put(KEY_MSG_TYPE_TC, msgType);

        values.put(KEY_MSG_NOTI_FAILED, "1");
        // Inserting Row
        long id_res = db.insert(TABLE_MESSAGES, null, values);
        // db.close(); // Closing database connection

        Log.d(TAG, "New MSG inserted into sqlite : " + id_res);
        return id_res;
    }

    public ArrayList<ChatRoom> getAllRooms(String self, String type) {
        ArrayList<ChatRoom> msgList = new ArrayList<>();
        Log.d("JHAJAHHJHAJ", self + "==" + type);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID, KEY_ROOM_USERNAME_OTHER, KEY_ROOM_MSG_LAST_TEXT, KEY_ROOM_MSG_LAST_TIMESTAMP, KEY_ROOM_OTHER_USER_PIC, KEY_ROOM_NAME, KEY_ROOM_GROUP_NAME, KEY_ROOM_GROUP_PIC, KEY_ROOM_GROUP_MEMBERS, KEY_ROOM_TYPE, KEY_ROOM_GROUP_CREATION_DATE, KEY_ROOM_OTHER_USER_STATUS, KEY_ROOM_IS_BLOCKED}, KEY_ROOM_USERNAME_SELF + "=? AND " + KEY_ROOM_TYPE + " =?", new String[]{self, type}, null, null, KEY_ROOM_MSG_LAST_TIMESTAMP + " desc");
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("CHECKCURSORSIZE9999", cursor.getCount() + "--");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.d("CHECKCURSORSIZE9999666", "--" + cursor.getString(5) + "==" + cursor.getString(1) + "===" + cursor.getString(8));

                ChatRoom contact = new ChatRoom();
                contact.setId(cursor.getString(0));
                contact.setUsername(cursor.getString(1));
                contact.setLastMessage(cursor.getString(2));
                contact.setTimestamp(cursor.getString(3));
                contact.setOtherProfpic(cursor.getString(4));
                contact.setName(cursor.getString(5));
                if (type.equals("g"))
                    contact.setGrp_members(cursor.getString(8));
                contact.setRoom_TYpe(cursor.getString(9));
               /* contact.setGrp_name(cursor.getString(6));
                contact.setGrp_pic(cursor.getString(7));
                contact.setGrp_members(cursor.getString(8));
                contact.setRoom_TYpe(cursor.getString(9));
                contact.setCreation_date(cursor.getString(10));
                if (cursor.getString(11).equals("1")) {
                    contact.setRoomActive("1");
                } else {
                    contact.setRoomActive("0");
                }*/
                if (cursor.getString(12).equals("1")) {
                    contact.setBlocked(true);
                } else {
                    contact.setBlocked(false);
                }
                //contact.setMessageTimeNew(cursor.getString(2));

//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
                // Adding contact to list
              /*  if (cursor.getString(2) != null && cursor.getString(3) != null && !cursor.getString(2).equals("") && !cursor.getString(3).equals("")) {
                    if (cursor.getString(0).equals("deletedEntry")) {
                        msgList.add(contact);
                    } else {*/
                msgList.add(contact);
                // }


            } while (cursor.moveToNext());

        }

        // return contact list
        return msgList;
    }

    public ArrayList<ChatRoom> getAllRoomsGrp(String self) {
        ArrayList<ChatRoom> msgList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID, KEY_ROOM_USERNAME_OTHER, KEY_ROOM_MSG_LAST_TEXT, KEY_ROOM_MSG_LAST_TIMESTAMP, KEY_ROOM_OTHER_USER_PIC, KEY_ROOM_MSG_READ, KEY_ROOM_GROUP_NAME, KEY_ROOM_GROUP_PIC, KEY_ROOM_GROUP_MEMBERS, KEY_ROOM_TYPE, KEY_ROOM_GROUP_CREATION_DATE}, KEY_ROOM_TYPE + "=? AND " + KEY_ROOM_SELF_MOBILENUMBER + "=?", new String[]{"p", self}, null, null, KEY_ROOM_MSG_LAST_TIMESTAMP);
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("CHECKCURSORSIZE9999", cursor.getCount() + "--");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.d("CHECKCURSORSIZE9999", "--" + cursor.getString(4));

                ChatRoom contact = new ChatRoom();
                contact.setId(cursor.getString(0));
                contact.setUsername(cursor.getString(1));
                contact.setLastMessage(cursor.getString(2));
                contact.setTimestamp(cursor.getString(3));
                contact.setOtherProfpic(cursor.getString(4));
                contact.setName(cursor.getString(5));
                contact.setGrp_name(cursor.getString(6));
                contact.setGrp_pic(cursor.getString(7));
                contact.setGrp_members(cursor.getString(8));
                contact.setRoom_TYpe(cursor.getString(9));
                contact.setCreation_date(cursor.getString(10));
                //contact.setMessageTimeNew(cursor.getString(2));

//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
                // Adding contact to list
                if (!cursor.getString(2).equals("") && !cursor.getString(3).equals("")) {
                    if (cursor.getString(0).equals("deletedEntry")) {
                        msgList.add(contact);
                    } else {
                        msgList.add(contact);
                    }
                }

            } while (cursor.moveToNext());

        }

        // return contact list
        return msgList;
    }

    public ArrayList<ChatMessage> fetchAllMediaFromMsgs(String otherUSer) {
        ArrayList<ChatMessage> msgList = new ArrayList<>();
        String holiday_name = "";

        String prevTime = "", latestTime = "";
        String fetch_user_class = "SELECT " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_PARTNER_USERNAME + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_ROOM_ID + "='" + otherUSer + "'" + " AND " + KEY_MSG_TYPE + "=1" + " OR " + KEY_MSG_ROOM_ID + "='" + otherUSer + "'" + " AND " + KEY_MSG_TYPE + "=4";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(fetch_user_class, null);
        Log.d("CURSORCOUNT8855555", cursor.getCount() + " - -");
        if (cursor.moveToFirst()) {
            do {
                Log.d("CURSORCOUNT88RECORDS123", cursor.getString(6) + " - -" + cursor.getString(2) + " - -" + cursor.getString(7));
                ChatMessage contact = new ChatMessage();
                contact.setId(cursor.getString(0));
                Log.d("CHECKTIMEBEFOREtEST", "-->" + "" + cursor.getString(0));

                contact.setMessageText(cursor.getString(1));
                String time = cursor.getString(2);

                Log.d("CHECKTIMEBEFOREtEST", time + "--");
                latestTime = ConvertGMTtoIST.getDateFromTt(time);
                if (!latestTime.equals(prevTime)) {
                    contact.setFirstMsg(true);
                    contact.setDateChanged(true);
                    prevTime = latestTime;
                } else {
                    contact.setDateChanged(false);
                    contact.setFirstMsg(false);
                }
                contact.setMessageTimeNew(time);
                int k;
                //contact.setMessageType("text");
                if (cursor.getString(3).equals("0")) {
                    Log.d("CEHCKMESSAGETYP", "otherrrr");
                    contact.setUserType(UserType.OTHER);
                    k = 0;
                    Log.d("CGEBJHBbdfsnkd", cursor.getString(10) + "===");
                    String name = this.getContactInfoByPhone(cursor.getString(10)).getName();
                    Log.d("CGEBJHBbdfsnkd", name + "--===");
                    contact.setOtherUsreName(name);
                } else {
                    Log.d("CEHCKMESSAGETYP", "selff");
                    contact.setUserType(UserType.SELF);
                    k = 1;
                    contact.setOtherUsreName("You");
                }

                if (k == 1) {
                    if (cursor.getString(4).equals("1"))
                        contact.setMessageType("image_self");
                    else if (cursor.getString(4).equals("4"))
                        contact.setMessageType("video_self");
                } else {
                    if (cursor.getString(4).equals("1")) {
                        contact.setMessageType("image_other");
                    } else if (cursor.getString(4).equals("4"))
                        contact.setMessageType("video_other");
                }

                contact.setImgURL(cursor.getString(5));
                contact.setAudioFilePath(cursor.getString(6));
                contact.setMsgStatus(cursor.getString(7));
                Log.d("SQLITEDATA9998", cursor.getString(8) + "--");
                contact.setImgThumb(cursor.getString(8));
                if (cursor.getString(9) != null) {
                    contact.setIsUploaded(cursor.getString(9));
                }


//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
                // Adding contact to list
                if (cursor.getString(6) == null || cursor.getString(6).equals("")) {

                } else {
                    msgList.add(contact);
                }
            } while (cursor.moveToNext());
        }
        return msgList;

    }

    public long getProfilesCount(String roomid) {
        SQLiteDatabase db = this.getReadableDatabase();

        long count = DatabaseUtils.queryNumEntries(db, TABLE_MESSAGES, KEY_MSG_ROOM_ID + "=?", new String[]{roomid});
        // db.close();
        return count;
    }

    public ArrayList<ChatMessage> fetchAllMsgsten(String roomId, int limit, int offset, int fromtype1) {
        ArrayList<ChatMessage> msgList = new ArrayList<>();

        String prevTime = "", latestTime = "";
        String fetch_user_class = "";
        //   String fetch_user_class =  "select * from(" +"SELECT " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_MEDIA_URL + ", " + KEY_MSG_LOCAL_URI + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_THUMB_URL + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_READ_STATUS + ", " + KEY_MSG_PARTNER_USERNAME + ", "+ KEY_MSG_RECEIVED_TIMESTAMP + ", "+ KEY_MSG_MEDIA_DURATION + ", " + KEY_MSG_ROOM_ID + " FROM " + TABLE_MESSAGES +"order by"+ KEY_MSG_TIMESTAMP+" limit 10"+ " WHERE " + KEY_MSG_ROOM_ID + "='" + roomId + "'";
        if (fromtype1 != 88) {
            Log.d("TETSTTRT2323", "1");
            fetch_user_class = "select " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_READ_STATUS + ", " + KEY_MSG_PARTNER_USERNAME + ", " + KEY_MSG_RECEIVED_TIMESTAMP + ", " + KEY_MSG_ROOM_ID + ", " + KEY_MSG_RECIEPT_TIMESTAMP + ", " + KEY_MSG_RECEIVED_TIMESTAMP + ", " + KEY_MSG_MEDIA_ISDOWNLOADED + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_ROOM_ID + "='" + roomId + "'" + " order by " + KEY_MSG_TIMESTAMP + " desc limit " + limit + " offset " + offset;


        } else {
            Log.d("TETSTTRT2323", "3");
            fetch_user_class = "select " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_READ_STATUS + ", " + KEY_MSG_PARTNER_USERNAME + ", " + KEY_MSG_RECEIVED_TIMESTAMP + ", " + KEY_MSG_ROOM_ID + ", " + KEY_MSG_RECIEPT_TIMESTAMP + ", " + KEY_MSG_RECEIVED_TIMESTAMP + ", " + KEY_MSG_MEDIA_ISDOWNLOADED + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_ROOM_ID + "='" + roomId + "'" + " AND " + KEY_MSG_TYPE_TC + "='" + "inquiry" + "'" + " order by " + KEY_MSG_TIMESTAMP + " desc limit " + limit + " offset " + offset;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(fetch_user_class, null);
        Log.d("CURSORCOUNT88", cursor.getCount() + " - -");
        if (cursor.moveToFirst()) {
            do {
                Log.d("CURSORCOUNT88RECORDS", cursor.getString(1) + " - -" + cursor.getString(1) + " - -" + cursor.getString(7));
                ChatMessage contact = new ChatMessage();
                contact.setId(cursor.getString(0));
                Log.d("CHECKTIMEBEFOREtEST", "-->" + "" + cursor.getString(0));
                contact.setMessageText(cursor.getString(1));

                String time = cursor.getString(2);

                Log.d("CHECKTIMEBEFOREtEST", time + "--");
                latestTime = ConvertGMTtoIST.getDateFromTt(time);
                contact.setMessageTimeNew(time);
                int k;
                //contact.setMessageType("text");
                if (cursor.getString(3).equals("0")) {
                    Log.d("CEHCKMESSAGETYP", "otherrrr");
                    contact.setUserType(UserType.OTHER);
                    k = 0;
                } else {
                    Log.d("CEHCKMESSAGETYP", "selff");
                    contact.setUserType(UserType.SELF);
                    k = 1;
                }
                if (cursor.getString(4).equals("0")) {
                    if (k == 1)
                        contact.setMessageType("text_self");
                    else
                        contact.setMessageType("text_other");
                    Log.d("SQLITEDATA999AUDIO666", "audio-->" + cursor.getString(7));
                    contact.setMsgStatus(cursor.getString(7));
                    Log.d("SQLITEDATA999", cursor.getString(4) + "--");
                } else if (cursor.getString(4).equals("6")) {
                    contact.setMessageType("groupheader");

                } else if (cursor.getString(4).equals("7")) {
                    contact.setMessageType("chatheader");

                } else if (cursor.getString(4).equals("2")) {
                    contact.setMessageType("text_self_q");

                } else if (cursor.getString(4).equals("3")) {
                    contact.setMessageType("text_other_q");

                } else if (cursor.getString(4).equals("4")) {
                    contact.setMessageType("text_other_inquiry");

                }

                contact.setMsgStatus(cursor.getString(5));
                contact.setReadStatus(cursor.getString(7));
                contact.setOtherUsreName(cursor.getString(8));

                // contact.setUniqueMsgId(Long.parseLong(cursor.getString(12)));
                Log.d("asdfkhjhjhjhs", cursor.getString(10) + "-=-=-=-=-=-=");
                contact.setMsgRoomId(cursor.getString(10));
                contact.setMsgQuotedText(cursor.getString(11));
                contact.setMsgQuotedId(cursor.getString(12));
                contact.setMsgQuotePosition(cursor.getString(13));

//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
                // Adding contact to list

                msgList.add(contact);


            }
            while (cursor.moveToNext());
        }
        return msgList;
    }

    public ArrayList<ChatMessage> fetchAllMsgs(String roomId) {
        ArrayList<ChatMessage> msgList = new ArrayList<>();
        String holiday_name = "";
        String prevTime = "", latestTime = "";
        String fetch_user_class = "SELECT " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_READ_STATUS + ", " + KEY_MSG_PARTNER_USERNAME + ", " + KEY_MSG_RECEIVED_TIMESTAMP + ", " + KEY_MSG_ROOM_ID + ", " + KEY_MSG_NOTI_FAILED + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_ROOM_ID + "='" + roomId + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(fetch_user_class, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d("CURSORCOUNT88RECORDS", cursor.getString(1) + " - -" + cursor.getString(1) + " - -" + cursor.getString(7));
                ChatMessage contact = new ChatMessage();
                contact.setId(cursor.getString(0));
                Log.d("CHECKTIMEBEFOREtEST", "-->" + "" + cursor.getString(0));
                contact.setMessageText(cursor.getString(1));

                String time = cursor.getString(2);

                //     Log.d("CHECKTIMEBEFOREtEST", time + "--");
                // latestTime = ConvertGMTtoIST.getDateFromTt(time);

                contact.setMessageTimeNew(time);
                int k;
                //contact.setMessageType("text");
                if (cursor.getString(3).equals("0")) {
                    Log.d("CEHCKMESSAGETYP", "otherrrr");
                    contact.setUserType(UserType.OTHER);
                    k = 0;
                } else {
                    Log.d("CEHCKMESSAGETYP", "selff");
                    contact.setUserType(UserType.SELF);
                    k = 1;
                }
                if (cursor.getString(4).equals("0")) {
                    if (k == 1)
                        contact.setMessageType("text_self");
                    else
                        contact.setMessageType("text_other");

                    contact.setMsgStatus(cursor.getString(7));
                    Log.d("SQLITEDATA999", cursor.getString(4) + "--");
                } else if (cursor.getString(4).equals("6")) {
                    contact.setMessageType("groupheader");

                } else if (cursor.getString(4).equals("7")) {
                    contact.setMessageType("chatheader");

                }
                contact.setMsgStatus(cursor.getString(5));
                contact.setReadStatus(cursor.getString(7));
                contact.setOtherUsreName(cursor.getString(8));

                // contact.setUniqueMsgId(Long.parseLong(cursor.getString(12)));
                contact.setMsgRoomId(cursor.getString(10));
                //  contact.setTripId(cursor.getString(15));
                //  contact.setIsFailed(cursor.getString(11));
//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
                //                // Adding contact to list
                msgList.add(contact);

            }
            while (cursor.moveToNext());
        }
        return msgList;
    }


    public long addUser(String user_id, String name, String password, String email, String phone, String profile_pic, String bg_pic, String gender, String birthdate, String anniversary, String status, String country_code, String fcm_token) {
        Log.d("STATUSHJK", status);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user_id); // Name
        values.put(KEY_NAME, name); // Name
        //values.put(KEY_USERNAME, username); // Name
        values.put(KEY_PASSWORD, password); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PHONE, phone); // Name
        values.put(KEY_PROFILE_PIC, profile_pic); // Email
        values.put(KEY_BACKGROUND_PIC, bg_pic); // Email
        values.put(KEY_GENDER, gender);
        //  values.put(KEY_USER_TOKEN, userToken);
        values.put(KEY_ANNIVERSARY, anniversary);
        values.put(KEY_BIRTHDATE, birthdate);
        values.put(KEY_USER_COUNTRY_CODE, country_code);
        values.put(KEY_USER_STATUS, status);
        values.put(KEY_USER_FCM_TOKEN, fcm_token);


        // Inserting Row
        long id_res = db.insert(TABLE_USER, null, values);
        // db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id_res);
        return id_res;
    }

    public void addContact(long user_id, String name, String phone) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CONTACT_ID, user_id); // Name
        values.put(KEY_CONTACT_NAME, name); // Name
        values.put(KEY_CONTACT_PHOTO, ""); // Name
        values.put(KEY_CONTACT_NUMBER, phone); // Name
        // Inserting Row
        long id_res = db.insert(TABLE_Contacts, null, values);
        // db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id_res);
    }


    public UserDetails getUserDetailByID(String userId) {
        ArrayList<UserDetails> userDetailList = new ArrayList<UserDetails>();
        // Select All Query

        UserDetails contact = new UserDetails();
        SQLiteDatabase db = this.getReadableDatabase();
        try {


            Cursor cursor = db.query(TABLE_USER, null, KEY_USER_ID + "=?", new String[]{userId}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {

                contact.setUser_id(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setPassword(cursor.getString(2));
                contact.setEmail(cursor.getString(3));
                contact.setPhone(cursor.getString(4));
                contact.setGender(cursor.getString(5));
                contact.setProfile_pic(cursor.getString(6));
                contact.setBackground_pic(cursor.getString(7));

                // contact.setUsertoken(cursor.getString(8));
                contact.setAnniversary(cursor.getString(8));
                contact.setBirthdate(cursor.getString(9));
                contact.setCountry_code(cursor.getString(10));
                contact.setFcm_token(cursor.getString(11));
                contact.setStatus(cursor.getString(12));
                Log.d("jhjgjjkgj55", cursor.getString(10) + "---" + cursor.getString(1));
                // Adding contact to list
                // db.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // return contact list
        return contact;
    }

    public ArrayList<ChatMessage> fetchAllMediaFromMsgsFromRoomId(String otherUSer) {
        ArrayList<ChatMessage> msgList = new ArrayList<>();
        String holiday_name = "";

        String prevTime = "", latestTime = "";
        String fetch_user_class = "SELECT " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_PARTNER_USERNAME + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_ROOM_ID + "='" + otherUSer + "'" + " AND " + KEY_MSG_TYPE + "=1" + " OR " + KEY_MSG_ROOM_ID + "='" + otherUSer + "'" + " AND " + KEY_MSG_TYPE + "=4";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(fetch_user_class, null);
        Log.d("CURSORCOUNT8855555", cursor.getCount() + " - -");
        if (cursor.moveToFirst()) {
            do {
                Log.d("CURSORCOUNT88RECORDS", cursor.getString(1) + " - -" + cursor.getString(1) + " - -" + cursor.getString(7));
                ChatMessage contact = new ChatMessage();
                contact.setId(cursor.getString(0));
                Log.d("CHECKTIMEBEFOREtEST", "-->" + "" + cursor.getString(0));

                contact.setMessageText(cursor.getString(1));
                String time = cursor.getString(2);

                Log.d("CHECKTIMEBEFOREtEST", time + "--");
                latestTime = ConvertGMTtoIST.getDateFromTt(time);
                if (!latestTime.equals(prevTime)) {
                    contact.setFirstMsg(true);
                    contact.setDateChanged(true);
                    prevTime = latestTime;
                } else {
                    contact.setDateChanged(false);
                    contact.setFirstMsg(false);
                }
                contact.setMessageTimeNew(time);
                int k;
                //contact.setMessageType("text");
                if (cursor.getString(3).equals("0")) {
                    Log.d("CEHCKMESSAGETYP", "otherrrr");
                    contact.setUserType(UserType.OTHER);
                    k = 0;
                } else {
                    Log.d("CEHCKMESSAGETYP", "selff");
                    contact.setUserType(UserType.SELF);

                    k = 1;
                }
                if (k == 1) {
                    if (cursor.getString(4).equals("1"))
                        contact.setMessageType("image_self");
                    else if (cursor.getString(4).equals("4"))
                        contact.setMessageType("video_self");
                    contact.setOtherUsreName("You");
                } else {
                    if (cursor.getString(4).equals("1")) {
                        contact.setMessageType("image_other");
                    } else if (cursor.getString(4).equals("4"))
                        contact.setMessageType("video_other");
                    String name = null;
                    Log.d("CHECKTHIS5555", cursor.getString(10) + "=====");
                    name = this.getContactInfoByPhone(cursor.getString(10)).getName();
                    Log.d("CHECKTHIS5555", name + "=====1");

                    contact.setOtherUsreName(name);
                }


                contact.setImgURL(cursor.getString(5));
                contact.setAudioFilePath(cursor.getString(6));
                contact.setMsgStatus(cursor.getString(7));
                Log.d("SQLITEDATA9998", cursor.getString(8) + "--");
                contact.setImgThumb(cursor.getString(8));
                if (cursor.getString(9) != null) {
                    contact.setIsUploaded(cursor.getString(9));
                }


//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
                // Adding contact to list
                if (cursor.getString(6) == null || cursor.getString(6).equals("")) {

                } else {
                    msgList.add(contact);
                }
            } while (cursor.moveToNext());
        }
        return msgList;

    }

    public int UpdateUserFcmId(String user_id, String fcmid) {
        Log.d("LDAJKLJKL", user_id + "====" + fcmid);
        SQLiteDatabase db = this.getWritableDatabase();
        int id_res = 0;
        try {
            ContentValues CV = new ContentValues();

            if (user_id != null) {
                CV.put(KEY_USER_FCM_TOKEN, fcmid);
                String wheres = KEY_USER_ID + "=?";
                String[] whereArgs = {user_id.toString()};
                id_res = db.update(TABLE_USER, CV, wheres, whereArgs);
                Log.d("LDAJKLJKL", user_id + "====" + id_res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_res;
    }


    public void UpdateUserData(String user_id, String name, String email, String profile_pic, String bg_pic, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues CV = new ContentValues();

            if (user_id != null) {

                CV.put(KEY_NAME, String.valueOf(name));
                CV.put(KEY_EMAIL, String.valueOf(email));
                CV.put(KEY_PROFILE_PIC, String.valueOf(profile_pic));
                CV.put(KEY_BACKGROUND_PIC, bg_pic);
                CV.put(KEY_USER_STATUS, status);

                String wheres = KEY_USER_ID + "=?";
                String[] whereArgs = {user_id.toString()};
                db.update(TABLE_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RoomType> fetchRoomTypes(String did) {
        ArrayList<RoomType> list = new ArrayList<>();
        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_OFFLINE_ROOM_TYPES + " WHERE " + KEY_ROOM_TYPE_DID + "='" + did + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("CEHCKFETCHEDDATA7", cursor.getCount() + "----");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                RoomType contact = new RoomType();
                contact.setRoom_did(cursor.getString(0));
                contact.setRoom_id(cursor.getString(1));
                contact.setRoomTypNm(cursor.getString(2));
                list.add(contact);
            } while (cursor.moveToNext());
            // Adding contact to list
        }
        // db.close();
        // return contact list
        return list;
    }

    public long addNotificationFaildData(String msg_id, String title, String body, String msg_typ, String msgtrtipid, String send_to) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.d("CHCECK99", room_id + "--" + username + "--" + msgcount + "-" + lastmsg);
        ContentValues values = new ContentValues();
        values.put(KEY_NOTI_MSG_ID, msg_id); // Name
        values.put(KEY_NOTI_TITLE, title); // Name
        values.put(KEY_NOTI_BODY, body); // Name
        values.put(KEY_NOTI_MSG_TYP, msg_typ); // Name

        values.put(KEY_NOTI_MSGTRIPID, msgtrtipid); // Name
        values.put(KEY_NOTI_SEND_TO, send_to); // Name

        long id_res = db.insert(TABLE_NOTIFICATIONS, null, values);
        // db.close(); // Closing database connection

        Log.d(TAG, "New room inserted into sqlite: " + id_res);
        return id_res;
    }

    public long addRoom(String room_id, String usernameSelf, String usernameOther, String msgLast, String msgTt, String msgRead, String unreadCount, String other_user_pic, String othr_user_status, String grpname, String grppic, String roomtype, String grpMembers, String creationDate, String roomName, String mob_no) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.d("CHCECK99", room_id + "--" + username + "--" + msgcount + "-" + lastmsg);
        ContentValues values = new ContentValues();
        values.put(KEY_ROOM_ID, room_id); // Name
        values.put(KEY_ROOM_USERNAME_SELF, usernameSelf);// Name
        values.put(KEY_ROOM_USERNAME_OTHER, usernameOther);
        values.put(KEY_ROOM_MSG_LAST_TEXT, msgLast);
        values.put(KEY_ROOM_MSG_LAST_TIMESTAMP, msgTt); // Name
        values.put(KEY_ROOM_MSG_READ, msgRead); // Name
        values.put(KEY_ROOM_OTHER_USER_PIC, other_user_pic); // Name
        values.put(KEY_ROOM_OTHER_USER_STATUS, othr_user_status); // Name
        values.put(KEY_ROOM_MSG_UNREAD_COUNT, unreadCount); // Name
        values.put(KEY_ROOM_GROUP_NAME, grpname); // Name
        values.put(KEY_ROOM_GROUP_PIC, grppic); // Name
        values.put(KEY_ROOM_GROUP_MEMBERS, grpMembers); // Name
        values.put(KEY_ROOM_TYPE, roomtype); // Name
        values.put(KEY_ROOM_GROUP_CREATION_DATE, creationDate);
        values.put(KEY_ROOM_NAME, roomName);
        values.put(KEY_ROOM_IS_BLOCKED, "0");
        values.put(KEY_ROOM_SELF_MOBILENUMBER, mob_no);
        // Inserting Row
        long id_res = db.insert(TABLE_ROOM, null, values);
        // db.close(); // Closing database connection


        Log.d(TAG, "New room inserted into sqlite: " + id_res);
        return id_res;
    }

    public String getRoomDByusername(String usernameSelf, String usernameOther) {
        String room_id = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID}, KEY_ROOM_USERNAME_SELF + "=? AND " + KEY_ROOM_USERNAME_OTHER + "=?", new String[]{usernameSelf, usernameOther}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor != null && cursor.moveToFirst()) {

                room_id = cursor.getString(0);
            }
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        } finally {

            if (cursor != null)
                cursor.close();

        }
        return room_id;
    }

    public String getContactLastUpdated(String phone) {
        String lastUpdateTime = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CONTACTS_NEW, new String[]{KEY_CONTACT_LAST_UPDATE}, KEY_CONTACT_RC_USERNAME + "=?", new String[]{phone}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst()) {

            lastUpdateTime = cursor.getString(0);
        }
        return lastUpdateTime;
    }

    public String getContactIdFromUsername(String phone) {
        String lastUpdateTime = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CONTACTS_NEW, new String[]{KEY_CONTACT_ID}, KEY_CONTACT_RC_USERNAME + "=?", new String[]{phone}, null, null, null);
            Log.d("CHECK65656", cursor.getCount() + "--");
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst()) {

            lastUpdateTime = cursor.getString(0);

        }
        return lastUpdateTime;
    }


    public String getRcidfromnumber(String phone) {
        String lastUpdateTime = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CONTACTS_NEW, new String[]{KEY_CONTACT_RC_USERID}, KEY_CONTACT_RC_USERNAME + "=?", new String[]{phone}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.moveToFirst();
            lastUpdateTime = cursor.getString(0);
        }
        return lastUpdateTime;
    }


    public ArrayList<String> getMsgIdsUnReadMsgd(String roomID) {
        ArrayList<String> room_ids = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_TIMESTAMP}, KEY_MSG_ROOM_ID + "=? AND " + KEY_MSG_READ_STATUS + "=? AND " + KEY_MSG_STATUS + "=?", new String[]{roomID, "0", "1"}, null, null, null);
        // Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String room_id = cursor.getString(0);
                room_ids.add(room_id);

            } while (cursor.moveToNext());
        }

        return room_ids;
    }

    public ArrayList<String> getUnReadMsgIds(String roomID) {
        ArrayList<String> room_ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_ID}, KEY_MSG_ROOM_ID + "=? AND " + KEY_MSG_STATUS + "=?", new String[]{roomID, "7"}, null, null, null);
        // Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String room_id = cursor.getString(0);
                room_ids.add(room_id);

            } while (cursor.moveToNext());
        }

        return room_ids;
    }

    public ArrayList<ChatMessage> getMsgIdsFromMsgStatus(String msgStatus, String msg_room_id) {
        ArrayList<ChatMessage> room_ids = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_ID, KEY_MSG_DATA, KEY_MSG_TIMESTAMP, KEY_MSG_TYPE, KEY_MSG_MEDIA_ISUPLOADED, KEY_MSG_ROOM_ID}, KEY_MSG_STATUS + "=? AND " + KEY_MSG_ROOM_ID + "=?", new String[]{msgStatus, msg_room_id}, null, null, null);
        // Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(cursor.getString(0));
                chatMessage.setMessageText(cursor.getString(1));
                chatMessage.setMessageTimeNew(cursor.getString(2));
                chatMessage.setMessageType(cursor.getString(3));
                chatMessage.setMsgRoomId(cursor.getString(5));

                //  chatMessage.setImgURL(cursor.getString(4));
                //  chatMessage.setAudioFilePath(cursor.getString(5));
                //chatMessage.setIsUploaded(cursor.getString(6));
                //chatMessage.setAudioFilename(cursor.getString(7));
                room_ids.add(chatMessage);

            } while (cursor.moveToNext());
        }

        return room_ids;
    }


    public void deleteContactFromID(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.delete(TABLE_CONTACTS_NEW, KEY_CONTACT_ID + "=" + id, null);
            // db.execSQL("delete from " + TABLE_MESSAGES + " where " + KEY_MSG_PARTNER_USERNAME + "='" + id + "'");
            //   int i = db.delete(TABLE_MESSAGES, KEY_MSG_ID + "=" + id, null);
            //  Log.d("CHECKMSGDELETEE", i + " ---");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // db.close();
    }


    public int deleteRoomeFromID(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        try {

            i = db.delete(TABLE_ROOM, KEY_ROOM_ID + "=?", new String[]{id});
            // db.execSQL("delete from " + TABLE_MESSAGES + " where " + KEY_MSG_PARTNER_USERNAME + "='" + id + "'");
            //   int i = db.delete(TABLE_MESSAGES, KEY_MSG_ID + "=" + id, null);
            //  Log.d("CHECKMSGDELETEE", i + " ---");
            Log.d("CHECKMSGDELETEE555", i + " ---11");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // db.close();
        return i;
    }

    public int deleteContacteFromID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        int i = 0;
        try {

            i = db.delete(TABLE_CONTACTS_NEW, KEY_CONTACT_ID + "=?", new String[]{id});
            // db.execSQL("delete from " + TABLE_MESSAGES + " where " + KEY_MSG_PARTNER_USERNAME + "='" + id + "'");
            //   int i = db.delete(TABLE_MESSAGES, KEY_MSG_ID + "=" + id, null);
            //  Log.d("CHECKMSGDELETEE", i + " ---");
            Log.d("CHECKMSGDELETEE555", i + " ---11");
            // db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // db.close();
        return i;
    }


    public int deleteMSgsFromid(String otherid) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        Log.d("CHECKMSGDELETEE555", otherid + " ---");
        try {

            i = db.delete(TABLE_MESSAGES, KEY_MSG_ROOM_ID + "=?", new String[]{otherid});
            // db.execSQL("delete from " + TABLE_MESSAGES + " where " + KEY_MSG_PARTNER_USERNAME + "='" + id + "'");
            //   int i = db.delete(TABLE_MESSAGES, KEY_MSG_ID + "=" + id, null);
            Log.d("CHECKMSGDELETEE555", i + " ---");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // db.close();
        return i;
    }

    public int deleteMSgsFromidAndRoomId(String roomId, String msgId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        Log.d("CHECKMSGDELETEE555", roomId + " ---" + msgId);
        try {

            i = db.delete(TABLE_MESSAGES, KEY_MSG_ROOM_ID + "=? AND " + KEY_MSG_TIMESTAMP + "=?", new String[]{roomId, msgId});
            // db.execSQL("delete from " + TABLE_MESSAGES + " where " + KEY_MSG_PARTNER_USERNAME + "='" + id + "'");
            //   int i = db.delete(TABLE_MESSAGES, KEY_MSG_ID + "=" + id, null);
            Log.d("CHECKMSGDELETEE555", i + " ---");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // db.close();
        return i;
    }

    public UserDetails getOtherUserDetailByID(String phone) {
        UserDetails contact = new UserDetails();
        try {


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(OTHER_USER, null, KEY_PHONE + "=?", new String[]{phone}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list

            if (cursor != null && cursor.moveToFirst()) {
                Log.d("CursorCOUNT999", cursor.getCount() + "--" + cursor.getString(0) + "--" + cursor.getString(1) + "--" + cursor.getString(2) + "--" + cursor.getString(4) + "---->" + cursor.getString(6) + "---->" + cursor.getString(7));
                contact.setUser_id(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contact.setProfile_pic(cursor.getString(3));
                contact.setBackground_pic(cursor.getString(4));
                contact.setCountry_code(cursor.getString(5));
                contact.setStatus(cursor.getString(7));
                contact.setActive(cursor.getString(6));

                // Adding contact to list
            }


        } catch (Exception e) {
            Log.d("CUSRSORCOUNT555", "ERror" + "--");
            // return contact list
            e.printStackTrace();
        }
        return contact;
    }

    public UserDetails getOtherContactDetailByID(String phone) {
        UserDetails contact = new UserDetails();
        try {


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_CONTACTS_NEW, new String[]{KEY_CONTACT_RC_USERID, KEY_CONTACT_NAME, KEY_CONTACT_NUMBER, KEY_CONTACT_PHOTO, KEY_CONTACT_BACKGROUND_PIC, KEY_USER_COUNTRY_CODE, KEY_CONTACT_USER_STATUS, KEY_CONTACT_ACTIVE}, KEY_CONTACT_RC_USERNAME + "=?", new String[]{phone}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list

            if (cursor != null && cursor.moveToFirst()) {
                Log.d("CursorCOUNT999", cursor.getCount() + "--" + cursor.getString(0) + "--" + cursor.getString(1) + "--" + cursor.getString(2) + "--" + cursor.getString(4) + "---->" + cursor.getString(6) + "---->" + cursor.getString(7));
                contact.setUser_id(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contact.setProfile_pic(cursor.getString(3));
                contact.setBackground_pic(cursor.getString(4));
                contact.setCountry_code(cursor.getString(5));
                contact.setStatus(cursor.getString(6));
                contact.setActive(cursor.getString(7));

                // Adding contact to list
            }


        } catch (Exception e) {
            Log.d("CUSRSORCOUNT555", "ERror" + "--");
            // return contact list
            e.printStackTrace();
        }
        return contact;
    }

    public ArrayList<UserDetails> getAllOtherUserDetail() {

        ArrayList<UserDetails> list = new ArrayList<>();


        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + OTHER_USER;

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.d("CHECKOFFLINEDATA", "-" + cursor.getString(0) + "-" + cursor.getString(1) + "-" + cursor.getString(2) + "-" + cursor.getString(3));
                UserDetails contact = new UserDetails();
                contact.setUser_id(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contact.setProfile_pic(cursor.getString(3));
                contact.setBackground_pic(cursor.getString(4));
                contact.setCountry_code(cursor.getString(5));
                contact.setStatus(cursor.getString(6));
                contact.setActive(cursor.getString(7));

                list.add(contact);

                // Adding contact to list


            } while (cursor.moveToNext());
        }
        // return contact list
        return list;

    }


    /*public void updareLocalFileUri(String msgId, String uri) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues CV = new ContentValues();

            if (msgId != null) {

                CV.put(KEY_MSG_LOCAL_URI, uri);

                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_ID + " = ? ", new String[]{msgId});
                Log.d(TAG, "CHECKSMESSAGEUPDATE111: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/


    public void updateRoomData(String roomId, String msgLastText, String msgLastTt, String userid) {

        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "CHECKROOMUPDATEVALUE: " + roomId + "-->" + msgLastText + "-->" + msgLastTt);
        try {
            ContentValues CV = new ContentValues();

            if (roomId != null) {

                CV.put(KEY_ROOM_MSG_LAST_TEXT, msgLastText);
                CV.put(KEY_ROOM_MSG_LAST_TIMESTAMP, msgLastTt);
                //CV.put(KEY_ROOM_USERNAME_OTHER, userid);
                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_ROOM, CV, KEY_ROOM_ID + " = ? ", new String[]{roomId});
                Log.d("JDKJKSJD", "=====" + rows);
                Log.d(TAG, "CHECKROOMUPDATEVALUE: " + rows + "--rows");
                if (rows > 0) {
                    EventBus.getDefault().post("updtRecents" + "," + roomId);
                }

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRoomuserName(String roomId, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "CHECKROOMUPDATEVALUE: " + roomId + "-->" + username);
        try {
            ContentValues CV = new ContentValues();

            if (roomId != null) {

                CV.put(KEY_ROOM_ID, roomId);
                CV.put(KEY_ROOM_USERNAME_OTHER, username);
                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_ROOM, CV, KEY_ROOM_ID + " = ? ", new String[]{roomId});

                Log.d(TAG, "CHECKROOMUPDATEVALUE: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int updateRoomDataMemberAdded(String roomId, String memberList) {
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;

        try {
            ContentValues CV = new ContentValues();

            if (roomId != null) {


                CV.put(KEY_ROOM_GROUP_MEMBERS, memberList);
                CV.put(KEY_ROOM_USERNAME_OTHER, memberList); // Name

                int rows = db.update(TABLE_ROOM, CV, KEY_ROOM_ID + " = ? ", new String[]{roomId});
                u = rows;
                Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }


    public int updateRoomDataOwner(String roomId, String owner) {
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;

        try {
            ContentValues CV = new ContentValues();

            if (roomId != null) {


                CV.put(KEY_ROOM_USERNAME_SELF, owner);
                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_ROOM, CV, KEY_ROOM_ID + " = ? ", new String[]{roomId});
                u = rows;
                Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    public int updateRoomBlocked(String roomId, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;

        try {
            ContentValues CV = new ContentValues();

            if (roomId != null) {


                CV.put(KEY_ROOM_IS_BLOCKED, value);
                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_ROOM, CV, KEY_ROOM_ID + " = ? ", new String[]{roomId});
                u = rows;
                Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    public int updateMsgData(String msgId, String msgStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        int updated = 0;
        try {
            ContentValues CV = new ContentValues();


            CV.put(KEY_MSG_ID, msgId);
            CV.put(KEY_MSG_STATUS, msgStatus);

            //values.put(KEY_USERNAME, username); // Name
            // Log.d("SENDVIDEOAPI", " 111" + " --" + timestmp + "------>" + msgStatus);
            int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_ID + " = ? ", new String[]{msgId});
            updated = rows;
            Log.d(TAG, "CHECKUPDATEMSG888912311: " + rows + "-->>>>>>>>>>>>>>>>>>>>>>>>>." + msgId);
            Log.d(TAG, "CHECKUPDATEMSG8889: " + rows + "--rows");
            Log.d("SENDVIDEOAPI", " 2222" + " --" + rows);
            // String wheres = KEY_USER_ID + "=?";
            // String[] whereArgs = {user_id.toString()};
            //db.update(OTHER_USER, CV, wheres, whereArgs);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }

    public int updateMsgNoti(String msg_id, String fail_status) {
        SQLiteDatabase db = this.getWritableDatabase();
        int updated = 0;
        try {
            ContentValues CV = new ContentValues();


            CV.put(KEY_MSG_NOTI_FAILED, fail_status);
            //values.put(KEY_USERNAME, username); // Name

            int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_ID + " = ? ", new String[]{msg_id});
            updated = rows;

            Log.d(TAG, "CHECKUPDATEMSG8889: " + rows + "--rows");
            Log.d("SENDVIDEOAPI", " 2222" + " --" + rows);
            // String wheres = KEY_USER_ID + "=?";
            // String[] whereArgs = {user_id.toString()};
            //db.update(OTHER_USER, CV, wheres, whereArgs);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }

    /* public int updateMsgSeenGrp(String msgId, String seenBy) {
         SQLiteDatabase db = this.getWritableDatabase();
         int updated = 0;
         try {
             ContentValues CV = new ContentValues();

             if (msgId != null) {


                 CV.put(KEY_MSG_GEO_POSITION, seenBy);
                 //values.put(KEY_USERNAME, username); // Name

                 int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_ID + " = ? ", new String[]{msgId});
                 updated = rows;
                 Log.d(TAG, "CHECKUPDATEMSG8889: " + rows + "--rows");
                 Log.d("SENDVIDEOAPI", " 2222" + " --" + rows);
                 // String wheres = KEY_USER_ID + "=?";
                 // String[] whereArgs = {user_id.toString()};
                 //db.update(OTHER_USER, CV, wheres, whereArgs);

             }
         } catch (Exception e) {
             e.printStackTrace();
         }
         return updated;
     }
 */
    public int updateMsgReadStatusFromId(String msgId, String roomId) {

        SQLiteDatabase db = this.getWritableDatabase();
        int updated = 0;
        Log.d(TAG, "CHECKUPDATEMSG8889123: " + msgId + "===");

        try {
            ContentValues CV = new ContentValues();

            if (msgId != null) {
                CV.put(KEY_MSG_READ_STATUS, "1");
                //values.put(KEY_USERNAME, username); // Name
                int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_ID + " = ? " + " AND " + KEY_MSG_ROOM_ID + " = ? ", new String[]{msgId, roomId});
                updated = rows;

                Log.d("CEHCK88888", "update==" + rows);
                Log.d(TAG, "CHECKUPDATEMSG8889: " + rows + "--rows");
                Log.d("SENDVIDEOAPI", " 2222" + " --" + rows);
                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }

    public int updateMsgReadStatus(String msgId) {

        SQLiteDatabase db = this.getWritableDatabase();
        int updated = 0;
        Log.d(TAG, "CHECKUPDATEMSG8889: " + msgId + "===");

        try {
            ContentValues CV = new ContentValues();

            if (msgId != null) {
                CV.put(KEY_MSG_READ_STATUS, "1");
                //values.put(KEY_USERNAME, username); // Name
                int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_ID + " = ? ", new String[]{msgId});
                updated = rows;

                Log.d("CEHCK88888", "update==" + rows);
                Log.d(TAG, "CHECKUPDATEMSG8889: " + rows + "--rows");
                Log.d("SENDVIDEOAPI", " 2222" + " --" + rows);
                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }

    public int updateMsgsIdFirstMsg(String msgId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int updated = 0;
        try {
            ContentValues CV = new ContentValues();

            if (msgId != null) {


                CV.put(KEY_MSG_MEDIA_ISDOWNLOADED, "1");
                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_ID + " = ? ", new String[]{msgId});
                updated = rows;
                Log.d(TAG, "CHECKUPDATEMSG8889: " + rows + "--rows");
                Log.d("SENDVIDEOAPI", " 2222" + " --" + rows);
                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }

    public int updateMsgRecieverStatus(String msgId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int updated = 0;
        try {
            ContentValues CV = new ContentValues();

            if (msgId != null) {


                CV.put(KEY_MSG_READ_STATUS, "1");
                int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_ID + " = ? ", new String[]{msgId});
                updated = rows;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }

  /*  public int updateMsgIsUploadeMedia(String timestmp, String isupload, String mediaUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        int updated = 0;
        try {
            ContentValues CV = new ContentValues();

            if (timestmp != null) {

                CV.put(KEY_MSG_MEDIA_ISUPLOADED, isupload);
                CV.put(KEY_MSG_MEDIA_URL, mediaUrl);
                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_ID + " = ? ", new String[]{timestmp});
                updated = rows;
                Log.d(TAG, "CHECKUPDATEMSG888955: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }

  public int updateMsgThumbMedia(String timestmp, String thumbUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        int updated = 0;
        try {
            ContentValues CV = new ContentValues();

            if (timestmp != null) {


                CV.put(KEY_MSG_MEDIA_THUMB_URL, thumbUrl);
                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_ID + " = ? ", new String[]{timestmp});
                updated = rows;
                Log.d(TAG, "CHECKUPDATEMSTHUMB1: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }
*/
  /*  public int updateMsgMediaPath(String timestmp, String mediaPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        int updated = 0;
        try {
            ContentValues CV = new ContentValues();

            if (timestmp != null) {


                CV.put(KEY_MSG_LOCAL_URI, mediaPath);
                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_ID + " = ? ", new String[]{timestmp});
                updated = rows;
                Log.d(TAG, "CHECKUPDATEMSG888955: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }*/

    /*  public String getMsgMediaUrl(String timestmp) {
          SQLiteDatabase db = this.getReadableDatabase();
          String lastTt = null;

          try {
              Cursor cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_MEDIA_URL}, KEY_MSG_ID + "=?", new String[]{timestmp}, null, null, null);
              //
              Log.d("CHECKEXCEigg", cursor.getCount() + "-----");
              if (cursor.moveToFirst())
                  lastTt = cursor.getString(0);
              Log.d("CHECKEXCEigg", lastTt + "-----");
          } catch (Exception e) {
              Log.d("CHECKEXCEPFETCHROOMSING", "hjds");
              e.printStackTrace();
          }


          //--get other cols values

          return lastTt;
      }
  */
    public ChatRoom getRoomDByID(String id) {
        ChatRoom chatRoom = new ChatRoom();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID, KEY_ROOM_GROUP_NAME, KEY_ROOM_GROUP_PIC, KEY_ROOM_GROUP_CREATION_DATE, KEY_ROOM_GROUP_MEMBERS, KEY_ROOM_USERNAME_OTHER, KEY_ROOM_USERNAME_SELF, KEY_ROOM_OTHER_USER_PIC, KEY_ROOM_TYPE}, KEY_ROOM_ID + "=?", new String[]{id}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor != null && cursor.moveToFirst()) {

                chatRoom.setId(cursor.getString(0));
                chatRoom.setGrp_name(cursor.getString(1));
                chatRoom.setGrp_pic(cursor.getString(2));
                chatRoom.setCreation_date(cursor.getString(3));
                chatRoom.setGrp_members(cursor.getString(4));
                chatRoom.setOwnerName(cursor.getString(6));
                chatRoom.setOtherProfpic(cursor.getString(7));
                chatRoom.setRoom_TYpe(cursor.getString(8));
                chatRoom.setUsername(cursor.getString(5));
            } else
                cursor = null;

        } catch (Exception e) {
            Log.d("CHECKEXCEPFETCHROOMSING", "hjds");
            e.printStackTrace();
        }
        return chatRoom;
    }

    public ChatRoom getRoomByID(String id) {
        ChatRoom contact = new ChatRoom();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {

            cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID, KEY_ROOM_USERNAME_OTHER, KEY_ROOM_MSG_LAST_TEXT, KEY_ROOM_MSG_LAST_TIMESTAMP, KEY_ROOM_OTHER_USER_PIC, KEY_ROOM_MSG_READ, KEY_ROOM_GROUP_NAME, KEY_ROOM_GROUP_PIC, KEY_ROOM_GROUP_MEMBERS, KEY_ROOM_TYPE, KEY_ROOM_GROUP_CREATION_DATE, KEY_ROOM_OTHER_USER_STATUS}, KEY_ROOM_ID + "=?", new String[]{id}, null, null, KEY_ROOM_MSG_LAST_TIMESTAMP);

            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor != null && cursor.moveToFirst()) {

                contact.setId(cursor.getString(0));
                contact.setUsername(cursor.getString(1));
                contact.setLastMessage(cursor.getString(2));
                contact.setTimestamp(cursor.getString(3));
                contact.setOtherProfpic(cursor.getString(4));
                contact.setName(cursor.getString(5));
                contact.setGrp_name(cursor.getString(6));
                contact.setGrp_pic(cursor.getString(7));
                contact.setGrp_members(cursor.getString(8));
                contact.setRoom_TYpe(cursor.getString(9));
                contact.setCreation_date(cursor.getString(10));
                if (cursor.getString(11).equals("1")) {
                    contact.setRoomActive("1");
                } else {
                    contact.setRoomActive("0");
                }
            } else
                cursor = null;

        } catch (Exception e) {
            Log.d("CHECKEXCEPFETCHROOMSING", "hjds");
            e.printStackTrace();
        }
        return contact;
    }

    public String getLastMessageTimestamp() {
        SQLiteDatabase db = this.getReadableDatabase();
        String lastTt = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_TIMESTAMP}, null, null, null, null, null);
            //
        } catch (Exception e) {
            Log.d("CHECKEXCEPFETCHROOMSING", "hjds");
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            lastTt = cursor.getString(0);
            Log.d("CEHCKLASTTT", lastTt + "---");
            //--get other cols values
        }
        return lastTt;
    }

    public String geUseIDFromRoom(String roomId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String lastTt = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_SELF_MOBILENUMBER}, KEY_ROOM_ID + "=?", new String[]{roomId}, null, null, null);
            //
        } catch (Exception e) {
            Log.d("CHECKEXCEPFETCHROOMSING", "hjds");
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst()) {
            lastTt = cursor.getString(0);
            Log.d("CEHCKLASTTT", lastTt + "---");
            //--get other cols values
        }
        return lastTt;
    }

    public String getFirstMessageTimestampFrmRoom(String roomId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String lastTt = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_TIMESTAMP}, KEY_MSG_ROOM_ID + "=?", new String[]{roomId}, null, null, null);
            //
        } catch (Exception e) {
            Log.d("CHECKEXCEPFETCHROOMSING", "hjds");
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            lastTt = cursor.getString(0);
            Log.d("CEHCKLASTTT", lastTt + "---");
            //--get other cols values
        }
        return lastTt;
    }

    public String getLastMessageTimestampFrmRoom(String roomId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String lastTt = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_TIMESTAMP}, KEY_MSG_ROOM_ID + "=?", new String[]{roomId}, null, null, null);
            //

            if (cursor != null) {
                if (cursor.moveToLast()) {
                    lastTt = cursor.getString(0);
                    Log.d("CEHCKLASTTT", lastTt + "---");
                    //--get other cols values
                }
            }

        } finally {
            // this gets called even if there is an exception somewhere above
            if (cursor != null)
                cursor.close();
        }
        return lastTt;
    }

    public String getLastMessageTimestampFrmRoomInquiry(String roomId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String lastTt = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_TIMESTAMP}, KEY_MSG_ROOM_ID + "=? AND " + KEY_MSG_TYPE_TC + "=?", new String[]{roomId, "inquiry"}, null, null, null);
            //
        } catch (Exception e) {
            Log.d("CHECKEXCEPFETCHROOMSING", "hjds");
            e.printStackTrace();
        }
        if (cursor != null) {
            if (cursor.moveToLast()) {
                lastTt = cursor.getString(0);
                Log.d("CEHCKLASTTT", lastTt + "---");
                //--get other cols values
            }
        }
        return lastTt;
    }

  /*  public String getVideoThumb(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String lastTt = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_MEDIA_DURATION}, KEY_MSG_TIMESTAMP + "=?", new String[]{id}, null, null, null);
            //
        } catch (Exception e) {
            Log.d("CHECKEXCEPFETCHROOMSING", "hjds");
            e.printStackTrace();
        }
        if (cursor.moveToFirst()) {
            lastTt = cursor.getString(0);
            Log.d("CEHCKLASTTT", lastTt + "---");
            //--get other cols values
        }
        return lastTt;
    }*/

    public String getLastMessageTimestampByuset(String other) {
        SQLiteDatabase db = this.getReadableDatabase();
        String lastTt = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_TIMESTAMP}, KEY_MSG_PARTNER_USERNAME + "=?", new String[]{other}, null, null, null);
            //
        } catch (Exception e) {
            Log.d("CHECKEXCEPFETCHROOMSING", "hjds");
            e.printStackTrace();
        }
        if (cursor.moveToLast()) {
            lastTt = cursor.getString(0);
            Log.d("CEHCKLASTTT", lastTt + "---");
            //--get other cols values
        }
        return lastTt;
    }

    public long InsertOrUpdateContactNewData(String contact_id, String rc_userid, String name, String rcname, String rcusername, String profile_pic, String bg_pic, String phone, String email, String status, String active_status, String gender, String last_updatedAt, String country_code, String ismultiplenumber, String userType, String localupdateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        long v = -1;
        Log.d("CHECKCONTACTADDED", "in insert method");
        Log.d("CHECKCONTACTADDED888", contact_id + "-->" + name + "-->" + phone);
        try {
            ContentValues values = new ContentValues();
            //ContentValues values = new ContentValues();
            // Name
            values.put(KEY_CONTACT_ID, contact_id);
            values.put(KEY_CONTACT_RC_USERID, rc_userid);
            values.put(KEY_CONTACT_NAME, name);
            values.put(KEY_CONTACT_RC_RNAME, rcname);
            values.put(KEY_CONTACT_RC_USERNAME, rcusername);
            values.put(KEY_CONTACT_PHOTO, profile_pic); // Name
            values.put(KEY_CONTACT_NUMBER, phone); // Name
            values.put(KEY_CONTACT_BACKGROUND_PIC, bg_pic);
            values.put(KEY_CONTACT_EMAIL, email); // Name
            values.put(KEY_CONTACT_USER_STATUS, status);
            values.put(KEY_CONTACT_ACTIVE, active_status); // Name
            values.put(KEY_CONTACT_USER_COUNTRY_CODE, country_code);
            values.put(KEY_CONTACT_LAST_UPDATE, last_updatedAt);
            values.put(KEY_CONTACT_GENDER, gender);
            values.put(KEY_CONTACT_TYPE, userType);
            values.put(KEY_CONTACT_ISMULTIPLE_NUMBER, ismultiplenumber);//values.put(KEY_USERNAME, username); // Name
            values.put(KEY_CONTACT_LOCAL_LAST_UPDATE, localupdateTime);
            values.put(KEY_CONTACT_ISBLOCKED, "0");

            v = db.insert(TABLE_CONTACTS_NEW, null, values);
            Log.d(TAG, "CHECKSIZEOTHERdataCONTACTNEW: " + v + "--insert");

            // String wheres = KEY_USER_ID + "=?";
            // String[] whereArgs = {user_id.toString()};
            //db.update(OTHER_USER, CV, wheres, whereArgs);

            //    // db.close();
        } catch (Exception e) {
            Log.d("CHECKCONTACTADDED", "EXCEPTION");
            e.printStackTrace();
        }
        return v;
    }

    public int updateContactNewData(String cid, String rc_userid, String rcname, String rcusername, String profile_pic, String bg_pic, String phone, String email, String status, String gender, String last_updatedAt, String country_code, String userType, String fcm_token) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        try {
            ContentValues values = new ContentValues();


            //ContentValues values = new ContentValues();
            // Name
            values.put(KEY_CONTACT_RC_USERID, rc_userid);
            values.put(KEY_CONTACT_RC_RNAME, rcname);
            values.put(KEY_CONTACT_RC_USERNAME, rcusername);
            values.put(KEY_CONTACT_PHOTO, profile_pic); // Name
            values.put(KEY_CONTACT_BACKGROUND_PIC, bg_pic);
            values.put(KEY_CONTACT_EMAIL, email); // Name
            values.put(KEY_CONTACT_USER_STATUS, status);
            values.put(KEY_CONTACT_NUMBER, phone);
            // Name
            values.put(KEY_CONTACT_USER_COUNTRY_CODE, country_code);
            values.put(KEY_CONTACT_LAST_UPDATE, last_updatedAt);
            values.put(KEY_CONTACT_GENDER, gender);
            values.put(KEY_CONTACT_TYPE, userType);
            values.put(KEY_CONTACT_FCM_TOKEN, fcm_token);
            //values.put(KEY_USERNAME, username); // Name
            rows = db.update(TABLE_CONTACTS_NEW, values, KEY_CONTACT_ID + " = ? ", new String[]{cid});
            Log.d(TAG, "UPDATECONTACTNEW: " + rows + "--rows");

        } catch (Exception e) {
            e.printStackTrace();
        }
        // db.close();
        return rows;
    }

    public String getContactByPhone(String phoneCheck) {
        Log.d("cHECKCOUNTCUROSR", phoneCheck + "---in");
        String contactId1 = null;
        String fetch_user_Contactid = "SELECT " + KEY_CONTACT_ID + " , " + KEY_CONTACT_NUMBER + " , " + KEY_CONTACT_ISMULTIPLE_NUMBER + " FROM " + TABLE_CONTACTS_NEW;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(fetch_user_Contactid, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals("0")) {
                    Log.d("cHECKCOUNTCUROSRSPLIT77", "11" + "------" + cursor.getString(1));
                    if (cursor.getString(1).equals(phoneCheck)) {
                        contactId1 = cursor.getString(0);
                    }

                } else if (cursor.getString(2).equals("1")) {
                    Log.d("cHECKCOUNTCUROSRSPLIT77", "22" + "------" + cursor.getString(1));
                    String num = cursor.getString(1);
                    String nums[] = num.split(",");
                    Log.d("cHECKCOUNTCUROSRSPLIT", contactId1 + "------");
                    for (int i = 0; i < nums.length; i++) {
                        if (nums[i].equals(phoneCheck)) {
                            Log.d("cHECKCOUNTCUROSRSPLIT", nums[i] + "------" + phoneCheck);
                            contactId1 = cursor.getString(0);
                        }
                    }

                }
                Log.d("cHECKCOUNTCUROSR", contactId1 + "------");
            } while (cursor.moveToNext());
        }
        return contactId1;

    }

    public UserDetails getContactInfoByPhone(String phoneCheck) {
        String contactId = null;
        UserDetails userDetails = new UserDetails();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            if (phoneCheck != null) {
                String fetch_user_Contactid = "SELECT " + KEY_CONTACT_PHOTO + " , " + KEY_CONTACT_NUMBER + " , " + KEY_CONTACT_NAME + " , " + KEY_CONTACT_FCM_TOKEN + " , " + KEY_CONTACT_RC_RNAME + " , " + KEY_CONTACT_ACTIVE + " FROM " + TABLE_CONTACTS_NEW + " WHERE " + KEY_CONTACT_NUMBER + "='" + phoneCheck + "'";
                // String[] selectionArgs = {phoneCheck + ""};
                cursor = sqLiteDatabase.rawQuery(fetch_user_Contactid, null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        if (cursor.moveToFirst()) {
                            userDetails.setProfile_pic(cursor.getString(0));
                            userDetails.setPhone(cursor.getString(1));
                            userDetails.setName(cursor.getString(2));
                            userDetails.setFcm_token(cursor.getString(3));
                            userDetails.setRcName(cursor.getString(4));
                            userDetails.setIsActive(cursor.getString(5));

                        }
                    }
                }
            }
        } finally {
            // this gets called even if there is an exception somewhere above
            if (cursor != null)
                cursor.close();
            //  sqLiteDatabase.close();
        }
        return userDetails;

    }


    public ArrayList<ChatMessage> fetchAllImageMsgs(String otherUSer) {
        ArrayList<ChatMessage> msgList = new ArrayList<>();

        String fetch_user_class = "SELECT " + KEY_MSG_ID + ", " + KEY_FROM_ME + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_PARTNER_USERNAME + "='" + otherUSer + "'" + " AND " + KEY_MSG_TYPE + "=1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(fetch_user_class, null);
        Log.d("CURSORCOUNT88", cursor.getCount() + " - -");
        if (cursor.moveToFirst()) {
            do {
                Log.d("CURSORCOUNT88RECORDS", cursor.getString(0) + " - -" + cursor.getString(1));
                ChatMessage contact = new ChatMessage();
                contact.setId(cursor.getString(0));
                Log.d("CHECKTIMEBEFOREtEST", "-->" + "" + cursor.getString(0));
                contact.setAudioFilePath(cursor.getString(1).toString());
                if (cursor.getString(2).equals("0")) {
                    contact.setUserType(UserType.OTHER);

                } else {
                    contact.setUserType(UserType.SELF);

                }
                msgList.add(contact);
            } while (cursor.moveToNext());
        }
        return msgList;
    }


    public int updateFCMData(String cid, String fcm_token) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        try {
            ContentValues values = new ContentValues();


            //ContentValues values = new ContentValues();
            // Name

            values.put(KEY_CONTACT_FCM_TOKEN, fcm_token);
            //values.put(KEY_USERNAME, username); // Name
            rows = db.update(TABLE_CONTACTS_NEW, values, KEY_CONTACT_ID + " = ? ", new String[]{cid});
            Log.d(TAG, "UPDATECONTACTNEW: " + rows + "--rows");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public int updateFCMDataUsingPhone(String phone, String fcm_token) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        try {
            ContentValues values = new ContentValues();


            //ContentValues values = new ContentValues();
            // Name

            values.put(KEY_CONTACT_FCM_TOKEN, fcm_token);

            rows = db.update(TABLE_CONTACTS_NEW, values, KEY_CONTACT_NUMBER + " = ? ", new String[]{phone});
            Log.d(TAG, "UPDATECONTACTNEW5465654566: " + rows + "--rows");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public int updateContactBlockStatus(String phone, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        Log.d("JGHDAadadd", phone + " ==" + status);
        try {
            ContentValues values = new ContentValues();


            //ContentValues values = new ContentValues();
            // Name

            values.put(KEY_CONTACT_ISBLOCKED, status);

            rows = db.update(TABLE_CONTACTS_NEW, values, KEY_CONTACT_NUMBER + " = ? ", new String[]{phone});
            Log.d(TAG, "UPDATECONTACTNEW5465654566123: " + rows + "--rows");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public long insertCOntactSFORGRP(long contact_id, String rc_userid, String name, String rcname, String rcusername, String profile_pic, String bg_pic, String phone, String email, String status, String active_status, String gender, String last_updatedAt, String country_code, String ismultiplenumber, String userType, String fcm_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("CHECKCONTACTADDED", "in insert method");
        long v = -1;
        // Log.d("CHECKCONTACTADDED888", contact_id + "-->" + name + "-->" + phone);
        try {
            ContentValues values = new ContentValues();


            //ContentValues values = new ContentValues();
            // Name
            values.put(KEY_CONTACT_ID, contact_id);
            values.put(KEY_CONTACT_RC_USERID, rc_userid);
            values.put(KEY_CONTACT_NAME, name);
            values.put(KEY_CONTACT_RC_RNAME, rcname);
            values.put(KEY_CONTACT_RC_USERNAME, rcusername);
            values.put(KEY_CONTACT_PHOTO, profile_pic); // Name
            values.put(KEY_CONTACT_NUMBER, phone); // Name
            values.put(KEY_CONTACT_BACKGROUND_PIC, bg_pic);
            values.put(KEY_CONTACT_EMAIL, email); // Name
            values.put(KEY_CONTACT_USER_STATUS, status);
            values.put(KEY_CONTACT_ACTIVE, active_status); // Name
            values.put(KEY_CONTACT_USER_COUNTRY_CODE, country_code);
            values.put(KEY_CONTACT_LAST_UPDATE, last_updatedAt);
            values.put(KEY_CONTACT_GENDER, gender);
            values.put(KEY_CONTACT_TYPE, userType);
            values.put(KEY_CONTACT_ISMULTIPLE_NUMBER, ismultiplenumber);//values.put(KEY_USERNAME, username); // Name
            values.put(KEY_CONTACT_FCM_TOKEN, fcm_id);
            values.put(KEY_CONTACT_ISBLOCKED, "0");

            v = db.insert(TABLE_CONTACTS_NEW, null, values);
            Log.d(TAG, "CHECKSIZEOTHERdataCONTACTNEW: " + v + "--insert");

            // String wheres = KEY_USER_ID + "=?";
            // String[] whereArgs = {user_id.toString()};
            //db.update(OTHER_USER, CV, wheres, whereArgs);


        } catch (Exception e) {
            Log.d("CHECKCONTACTADDED", "EXCEPTION");
            e.printStackTrace();
        }
        return v;
    }

    public boolean CheckIsContactAlreadyInDBorNot(String fieldValue) {
        SQLiteDatabase sqldb = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_CONTACTS_NEW + " where " + KEY_CONTACT_RC_USERNAME + " = " + fieldValue;
        Cursor cursor = sqldb.rawQuery(Query, null);
        Log.d("JAHJKHD", cursor.getCount() + "==");
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean CheckIsRoomAlreadyInDBorNot(String fieldValue) {
        SQLiteDatabase sqldb = this.getReadableDatabase();
        // String Query = "Select * from " + TABLE_ROOM + " where '" + KEY_ROOM_ID + " = " + fieldValue;
        Cursor cursor = sqldb.query(TABLE_ROOM, null, KEY_ROOM_ID + "=?", new String[]{fieldValue}, null, null, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public String getFCMToken(String phone) {
        String fcmtoken = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CONTACTS_NEW, new String[]{KEY_CONTACT_FCM_TOKEN}, KEY_CONTACT_RC_USERNAME + "=?", new String[]{phone}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list

            if (cursor.moveToFirst()) {
                if (cursor.getString(0) != null)
                    fcmtoken = cursor.getString(0);
            }
            return fcmtoken;
        } finally {
            // this gets called even if there is an exception somewhere above
            if (cursor != null)
                cursor.close();
        }

    }

    public String getFCMTokenOwn(String phone) {
        String fcmtoken = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USER, new String[]{KEY_USER_FCM_TOKEN}, KEY_PHONE + "=?", new String[]{phone}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list

            if (cursor.moveToFirst()) {
                if (cursor.getString(0) != null)
                    fcmtoken = cursor.getString(0);
            }
            return fcmtoken;
        } finally {
            // this gets called even if there is an exception somewhere above
            if (cursor != null)
                cursor.close();
        }

    }


    public String getRoomName(String roomId) {
        String roomID = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_NAME}, KEY_ROOM_ID + "=?", new String[]{roomId}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst()) {

            if (cursor.getString(0) != null)
                roomID = cursor.getString(0);
        }
        return roomID;
    }

    public int getUnreadMessages(String roomId) {
        int total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_ID}, KEY_MSG_ROOM_ID + "=? AND " + KEY_FROM_ME + "=? AND " + KEY_MSG_READ_STATUS + "=?", new String[]{roomId, "0", "0"}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            total = cursor.getCount();
            Log.d("CHECKEXCEPTHROOMUNRE", "---->" + total + "===" + roomId);
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.moveToFirst();


        }
        return total;
    }


    public int getUnreadMessagesTotal() {
        int total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_ID}, KEY_MSG_RECIEPT_TIMESTAMP + "=?", new String[]{"0"}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            total = cursor.getCount();
            Log.d("CHECKEXCEPTHROOMUNRE", "---->" + total);
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.moveToFirst();


        }
        return total;
    }

    public String getRoomId(String phone) {
        String roomID = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID}, KEY_ROOM_USERNAME_OTHER + "=?", new String[]{phone}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (cursor.getString(0) != null)
                    roomID = cursor.getString(0);
            }
        }
        return roomID;
    }

    public int getRoomGrpExist(String msgid) {
        int count = 0;
        Log.d("CEHCKHGDJGHFDJK", msgid + "---");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            if (msgid != null) {
                cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_ID}, KEY_MSG_ID + "=?", new String[]{msgid}, null, null, null);
            }
            Log.d("CEHCKHGDJGHFDJK", cursor.getCount() + "---");
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor == null) {
            count = 0;
        } else {
            count = cursor.getCount();
        }
        if (cursor != null)
            cursor.close();
        return count;
    }


    public String getRoomMembers(String roomId) {
        String roomMembers = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_GROUP_MEMBERS}, KEY_ROOM_ID + "=?", new String[]{roomId}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {

                if (cursor.getString(0) != null)
                    roomMembers = cursor.getString(0);

                Log.d("jhjusdhfjkhadjk", roomMembers + "--");
            }

        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        return roomMembers;
    }

    public ArrayList<ChatRoom> getAllRoomsGroupDirect(String self) {
        ArrayList<ChatRoom> msgList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID, KEY_ROOM_USERNAME_OTHER, KEY_ROOM_MSG_LAST_TEXT, KEY_ROOM_MSG_LAST_TIMESTAMP, KEY_ROOM_OTHER_USER_PIC, KEY_ROOM_MSG_READ, KEY_ROOM_GROUP_NAME, KEY_ROOM_GROUP_PIC, KEY_ROOM_GROUP_MEMBERS, KEY_ROOM_TYPE, KEY_ROOM_GROUP_CREATION_DATE, KEY_ROOM_OTHER_USER_STATUS, KEY_ROOM_IS_BLOCKED}, KEY_ROOM_SELF_MOBILENUMBER + "=?", new String[]{self}, null, null, KEY_ROOM_MSG_LAST_TIMESTAMP);
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("CHECKCURSORSIZE9999", cursor.getCount() + "--");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.d("JHKJAHHJHJH123-----", "--" + cursor.getString(0) + "===" + cursor.getString(9));
                int a = 0;
                ChatRoom contact = new ChatRoom();
                contact.setId(cursor.getString(0));
                contact.setTimestamp(cursor.getString(3));
                contact.setRoom_TYpe(cursor.getString(9));
                contact.setLastMessage(cursor.getString(2));
                if (cursor.getString(9).equals("d")) {

                    contact.setUsername(cursor.getString(1));


                    contact.setOtherProfpic(cursor.getString(4));
                    contact.setName(cursor.getString(5));
                    contact.setCreation_date(cursor.getString(10));
                    contact.setRoomActive(cursor.getString(11));
                    if (cursor.getString(12).equals("1")) {
                        contact.setBlocked(true);
                    } else {
                        contact.setBlocked(false);
                    }
                } else if (cursor.getString(9).equals("p")) {
                    contact.setUsername(cursor.getString(8));
                    contact.setGrp_name(cursor.getString(6));
                    contact.setGrp_pic(cursor.getString(7));
                    contact.setGrp_members(cursor.getString(8));
                    contact.setName(cursor.getString(6));
                    contact.setOtherProfpic(cursor.getString(4));
                    contact.setCreation_date(cursor.getString(10));
                }


                //contact.setMessageTimeNew(cursor.getString(2));

//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
                // Adding contact to list

                msgList.add(contact);


            } while (cursor.moveToNext());

        }

        // return contact list
        return msgList;
    }


    public int updateRoomDataBgpic(String roomId, String picturebg) {
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;

        try {
            ContentValues CV = new ContentValues();

            if (roomId != null) {


                CV.put(KEY_ROOM_OTHER_USER_PIC, picturebg);
                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_ROOM, CV, KEY_ROOM_ID + " = ? ", new String[]{roomId});
                u = rows;
                Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    public int updateRoomDataPicture(String roomId, String picture) {
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;

        try {
            ContentValues CV = new ContentValues();

            if (roomId != null) {


                CV.put(KEY_ROOM_GROUP_PIC, picture);
                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_ROOM, CV, KEY_ROOM_ID + " = ? ", new String[]{roomId});
                u = rows;
                Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }


    public int updateRoomDataName(String roomId, String nameOfGrp) {
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;

        try {
            ContentValues CV = new ContentValues();

            if (roomId != null) {


                CV.put(KEY_ROOM_GROUP_NAME, nameOfGrp);
                CV.put(KEY_ROOM_NAME, nameOfGrp);
                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_ROOM, CV, KEY_ROOM_ID + " = ? ", new String[]{roomId});
                u = rows;
                Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    public int updateRoomMobData(String roomId, String usreid) {
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;

        try {
            ContentValues CV = new ContentValues();

            if (roomId != null) {


                CV.put(KEY_ROOM_SELF_MOBILENUMBER, usreid);

                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_ROOM, CV, KEY_ROOM_ID + " = ? ", new String[]{roomId});
                u = rows;
                Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    public int updateRoomType(String roomId, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;

        try {
            ContentValues CV = new ContentValues();

            if (roomId != null) {


                CV.put(KEY_ROOM_TYPE, type);

                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_ROOM, CV, KEY_ROOM_ID + " = ? ", new String[]{roomId});
                u = rows;
                Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    public ChatMessage fetchSingleMsg(String msgid) {
        ChatMessage contact = new ChatMessage();
        Log.d("MESSSAGEID11111", msgid + "<<<<<<<<--");
        String prevTime = "", latestTime = "";
        String fetch_user_class = "SELECT " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_READ_STATUS + ", " + KEY_MSG_PARTNER_USERNAME + ", " + KEY_MSG_ROOM_ID + ", " + KEY_MSG_TYPE_TC + ", " + KEY_MSG_RECIEPT_TIMESTAMP + ", " + KEY_MSG_RECEIVED_TIMESTAMP + ", " + KEY_MSG_MEDIA_ISDOWNLOADED + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_ID + "='" + msgid + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(fetch_user_class, null);
        Log.d("CURSORCOUNT88", cursor.getCount() + " - -");
        if (cursor.moveToFirst()) {

            contact.setId(cursor.getString(0));
            Log.d("CHECKTIMEBEFOREtEST", "-->" + "" + cursor.getString(0));

            contact.setMessageText(cursor.getString(1));
            String time = cursor.getString(2);

            Log.d("CHECKTIMEBEFOREtEST", time + "--");
            latestTime = ConvertGMTtoIST.getDateFromTt(time);

            contact.setMessageTimeNew(time);
            int k;
            //contact.setMessageType("text");
            if (cursor.getString(3).equals("0")) {
                Log.d("CEHCKMESSAGETYP", "otherrrr");
                contact.setUserType(UserType.OTHER);
                k = 0;
            } else {
                Log.d("CEHCKMESSAGETYP", "selff");
                contact.setUserType(UserType.SELF);
                k = 1;
            }
            if (cursor.getString(4).equals("0")) {
                if (k == 1)
                    contact.setMessageType("text_self");
                else
                    contact.setMessageType("text_other");
                Log.d("SQLITEDATA999AUDIO666", "audio-->" + cursor.getString(7));
                contact.setMsgStatus(cursor.getString(7));
                Log.d("SQLITEDATA999", cursor.getString(4) + "--");
            } else if (cursor.getString(4).equals("6")) {
                contact.setMessageType("groupheader");

            } else if (cursor.getString(4).equals("7")) {
                contact.setMessageType("chatheader");
            } else if (cursor.getString(4).equals("2")) {
                contact.setMessageType("text_self_q");

            } else if (cursor.getString(4).equals("3")) {
                contact.setMessageType("text_other_q");

            } else if (cursor.getString(4).equals("4")) {
                contact.setMessageType("text_other_inquiry");

            }
            contact.setMsgStatus(cursor.getString(5));
            contact.setReadStatus(cursor.getString(7));
            contact.setOtherUsreName(cursor.getString(8));
            contact.setMsgRoomId(cursor.getString(9));
            contact.setMsgQuotedText(cursor.getString(11));
            contact.setMsgQuotedId(cursor.getString(12));
            contact.setMsgQuotePosition(cursor.getString(13));
//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
            // Adding contact to list


        }
        return contact;
    }

    public String getMsgIdfromTimestamps(String roomId) {
        String roomMembers = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_ID}, KEY_MSG_TIMESTAMP + "=?", new String[]{roomId}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null)
                roomMembers = cursor.getString(0);
        }
        return roomMembers;
    }

    public int getRoomDirectExist(String roomId) {
        int count = 0;
        Log.d("CEHCKHGDJGHFDJK", roomId + "---");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID}, KEY_ROOM_ID + "=? ", new String[]{roomId}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            Log.d("CEHCKHGDJGHFDJK", cursor.getCount() + "---");
            if (cursor == null) {
                count = 0;
            } else {
                count = cursor.getCount();
            }
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }

        return count;
    }

    public int updateContactName(String contactid, String contactname) {
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;

        try {
            ContentValues CV = new ContentValues();

            if (contactid != null) {


                CV.put(KEY_CONTACT_NAME, contactname);

                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_CONTACTS_NEW, CV, KEY_CONTACT_NUMBER + " = ? ", new String[]{contactid});
                u = rows;
                Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    public int updateIsActive3(String id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        try {
            ContentValues values = new ContentValues();


            //ContentValues values = new ContentValues();
            // Name

            values.put(KEY_USER_STATUS, status);
            //values.put(KEY_USERNAME, username); // Name
            rows = db.update(TABLE_USER, values, KEY_PHONE + " = ? ", new String[]{id});
            Log.d(TAG, "UPDATECONTACTNEW: " + rows + "--rows");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public int updateIsActive(String id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        try {
            ContentValues values = new ContentValues();


            //ContentValues values = new ContentValues();
            // Name

            values.put(KEY_CONTACT_ACTIVE, status);
            //values.put(KEY_USERNAME, username); // Name
            rows = db.update(TABLE_CONTACTS_NEW, values, KEY_CONTACT_NUMBER + " = ? ", new String[]{id});
            Log.d(TAG, "UPDATECONTACTNEW: " + rows + "--rows");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public int updateIsActiveRoom(String id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        try {
            ContentValues values = new ContentValues();


            //ContentValues values = new ContentValues();
            // Name

            values.put(KEY_ROOM_OTHER_USER_STATUS, status);
            //values.put(KEY_USERNAME, username); // Name
            rows = db.update(TABLE_ROOM, values, KEY_ROOM_ID + " = ? ", new String[]{id});
            Log.d(TAG, "UPDATECONTACTNEW: " + rows + "--rows");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public int updateReadStatus(String id, String status) {
        String tt = ConvertGMTtoIST.convertISTtoGMTFormat();
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        try {
            ContentValues values = new ContentValues();


            //ContentValues values = new ContentValues();
            // Name

            values.put(KEY_MSG_RECIEPT_TIMESTAMP, tt);
            //values.put(KEY_USERNAME, username); // Name
            rows = db.update(TABLE_MESSAGES, values, KEY_MSG_ROOM_ID + " = ? ", new String[]{id});
            Log.d(TAG, "UPDATECONTACTNEW: " + rows + "--rows");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public String getRoomByOtherYYsername(String usernameOther) {
        String room_id = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID}, KEY_ROOM_USERNAME_OTHER + "=?", new String[]{usernameOther}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst()) {

            room_id = cursor.getString(0);
        }
        return room_id;
    }


    public String getRoomStatus(String usernam) {
        String room_id = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CONTACTS_NEW, new String[]{KEY_CONTACT_ACTIVE}, KEY_CONTACT_NUMBER + "=?", new String[]{usernam}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst()) {

            room_id = cursor.getString(0);
        }
        return room_id;
    }

    public int getRoomPrivateExist(String roomId) {
        int count = 0;
        Log.d("CEHCKHGDJGHFDJK", roomId + "---");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID}, KEY_ROOM_ID + "=? AND " + KEY_ROOM_TYPE + "=?", new String[]{roomId, "d"}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            Log.d("CEHCKHGDJGHFDJK", cursor.getCount() + "---");
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor == null) {
            count = 0;
        } else {
            count = cursor.getCount();
        }
        return count;
    }

    public int deletePlans() {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        Log.d("CHECKMSGDELETEE555", " ---");
        try {

            db.execSQL("delete from " + TABLE_PLANS);

            Log.d("CHECKMSGDELETEE555", i + " ---");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  // db.close();
        return i;
    }

    public int deleteBookings() {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        Log.d("CHECKMSGDELETEE555", " ---");
        try {

            db.execSQL("delete from " + TABLE_BOOKING_DETAILS + " WHERE " + KEY_BOOKING_TYPE + "='" + "inquiryLatest " + "'" + " OR " + KEY_BOOKING_TYPE + "='" + " active" + "'"+ " OR " + KEY_BOOKING_TYPE + "='" + "all" + "'" + " OR " + KEY_BOOKING_TYPE + "='" + "today" + "'" + " OR " + KEY_BOOKING_TYPE + "='" + "tomorrow" + "'");

            Log.d("CHECKMSGDELETEE555", i + " ---");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  // db.close();
        return i;
    }

    public int deleteInquiries() {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        Log.d("CHECKMSGDELETEE555", " ---");
        try {
            int j = db.delete(TABLE_BOOKING_DETAILS, KEY_BOOKING_TYPE + "=? ", new String[]{"inquiryLatest"});
            // db.execSQL("delete from " + TABLE_BOOKING_DETAILS + " WHERE " + KEY_BOOKING_TYPE + "='" + " inquiryLatest" + "'");

            Log.d("CHECKMSGDELETEE555", i + " ---" + j);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  // db.close();
        return i;
    }

    public String getContactActive(String roomId) {
        String roomMembers = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CONTACTS_NEW, new String[]{KEY_CONTACT_ACTIVE}, KEY_CONTACT_NUMBER + "=?", new String[]{roomId}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }


        if (cursor != null && cursor.moveToFirst())
            roomMembers = cursor.getString(0);

        return roomMembers;
    }
    public int deleteBookingsAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        Log.d("CHECKMSGDELETEE555", " ---");
        try {

            db.execSQL("delete from " + TABLE_BOOKING_DETAILS );

            Log.d("CHECKMSGDELETEE555678", i + " ---");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  // db.close();
        return i;
    }

    public long addTransactionDetail(String transactionId, String amount, String timestamp, String status, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TRANSACTION_ID, transactionId); // Name
        values.put(KEY_TRANSACTION_AMT, amount); // Name
        values.put(KEY_TRANSACTION_TIMESTAMP, timestamp);
        values.put(KEY_TRANSACTION_STATUS, status);
        values.put(KEY_TRANSACTION_TYPE, title);
        long id_res = db.insert(TABLE_TRANSACTIONS, null, values);
        // db.close(); // Closing database connection
        Log.d(TAG, "New transaction inserted into sqlite : " + id_res);
        return id_res;

    }

    public int deleteTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        Log.d("CHECKMSGDELETEE555", " ---");
        try {

            db.execSQL("delete from " + TABLE_TRANSACTIONS);

            Log.d("CHECKMSGDELETEE555", i + " ---");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // db.close();
        return i;
    }


    public String getConactDetailByID(String userId) {
        String contact_id = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS_NEW, new String[]{KEY_CONTACT_TYPE}, KEY_CONTACT_NUMBER + "=?", new String[]{userId}, null, null, null);
        // Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            contact_id = cursor.getString(0);
            Log.d("jhjgjjkgj", cursor.getString(0) + "---");
            // Adding contact to list


        }

        // return contact list
        return contact_id;
    }

    public ArrayList<ChatRoom> getAllRoomsMediaShare(String userSELF) {
        ArrayList<ChatRoom> msgList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID, KEY_ROOM_USERNAME_OTHER, KEY_ROOM_MSG_LAST_TEXT, KEY_ROOM_MSG_LAST_TIMESTAMP, KEY_ROOM_OTHER_USER_PIC, KEY_ROOM_MSG_READ, KEY_ROOM_GROUP_NAME, KEY_ROOM_GROUP_PIC, KEY_ROOM_GROUP_MEMBERS, KEY_ROOM_TYPE, KEY_ROOM_GROUP_CREATION_DATE, KEY_ROOM_OTHER_USER_STATUS}, KEY_ROOM_SELF_MOBILENUMBER + "=?", new String[]{userSELF}, null, null, KEY_ROOM_MSG_LAST_TIMESTAMP);
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("CHECKCURSORSIZE9999", cursor.getCount() + "--");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.d("CHECKCURSORSIZE9999666", "--" + cursor.getString(11));

                ChatRoom contact = new ChatRoom();
                contact.setId(cursor.getString(0));
                contact.setUsername(cursor.getString(1));
                contact.setLastMessage(cursor.getString(2));
                contact.setTimestamp(cursor.getString(3));
                contact.setOtherProfpic(cursor.getString(4));
                contact.setName(cursor.getString(5));
                contact.setGrp_name(cursor.getString(6));
                contact.setGrp_pic(cursor.getString(7));
                contact.setGrp_members(cursor.getString(8));
                contact.setRoom_TYpe(cursor.getString(9));
                contact.setCreation_date(cursor.getString(10));
                if (cursor.getString(11).equals("1")) {
                    contact.setRoomActive("1");
                } else {
                    contact.setRoomActive("0");
                }

                msgList.add(contact);


            } while (cursor.moveToNext());

        }

        // return contact list
        return msgList;
    }

    public int getContactExist(String contactId) {
        int count = 0;
        Log.d("CEHCKHGD11", contactId + "---");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CONTACTS_NEW, new String[]{KEY_CONTACT_NUMBER}, KEY_CONTACT_NUMBER + "=?", new String[]{contactId}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            Log.d("WHEREFLOW1235454", " -->>>7" + cursor.getCount() + "--" + contactId);
            Log.d("CEHCKHGD11", cursor.getCount() + "<<---");
            Log.d("CEHCKHGDJGHFDJK", cursor.getCount() + "---");
            if (cursor == null) {
                count = 0;
            } else if (cursor.moveToFirst()) {
                count = cursor.getCount();
            }
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }

//        cursor.close();
        // db.close();
        return count;
    }

    public int getPhoneNumberExist(String phone) {
        int count = 0;
        Log.d("CEHCKHGDJGHFDJK", phone + "---");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CONTACTS_NEW, new String[]{KEY_CONTACT_ID}, KEY_CONTACT_NUMBER + "=?", new String[]{phone}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            Log.d("CEHCKHGDJGHFDJK", cursor.getCount() + "---");
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor == null) {
            count = 0;
        } else {
            count = cursor.getCount();
        }
        cursor.close();
        return count;
    }

    public int getRoomDirectExistByPhone(String phone) {
        int count = 0;
        Log.d("CEHCKHGDJGHFDJK", phone + "---");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID}, KEY_ROOM_USERNAME_OTHER + "=?", new String[]{phone}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor == null) {
                count = 0;
            } else {
                count = cursor.getCount();
            }

            Log.d("CEHCKHGDJGHFDJK", cursor.getCount() + "---");
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        // db.close();
        return count;
    }


    public boolean getContactExistByID(String contactId) {
        Log.d("CHECKFORUPDATE123 2", contactId + "--");
        Log.d("CHECK0890808080", "------------------------->>>>> in method" + " --" + contactId);
        SQLiteDatabase sqlDB = this.getReadableDatabase();
        Cursor DB_cursor = null;
        try {
            String DB_query = "SELECT * FROM " + TABLE_CONTACTS_NEW + " WHERE " + KEY_CONTACT_ID + "='" + contactId + "'";

            DB_cursor = sqlDB.rawQuery(DB_query, null);
            Log.d("CHECKFORUPDATE123 3", DB_cursor.getCount() + "---==");
            if (DB_cursor.moveToFirst()) {
                DB_cursor.close();

                return true;
            } else {
                DB_cursor.close();

                return false;
            }

        } catch (Exception ex) {
            DB_cursor.close();
            ex.printStackTrace();
            Log.d("CEHCKERROR999", "Error : " + ex.getMessage());
            return false;
        }

    }

    public int updateContactNewName(String cid, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        try {
            ContentValues values = new ContentValues();


            //ContentValues values = new ContentValues();
            // Name

            values.put(KEY_CONTACT_NAME, name);
            //values.put(KEY_USERNAME, username); // Name
            rows = db.update(TABLE_CONTACTS_NEW, values, KEY_CONTACT_NUMBER + " = ? ", new String[]{cid});
            Log.d(TAG, "UPDATECONTACTNEW: " + rows + "--rows");

        } catch (Exception e) {
            e.printStackTrace();
        }
        // db.close();
        return rows;
    }

    public ArrayList<ChatMessage> fetchAllfIRSTmSGS(String roomId, int fromtype1) {
        ArrayList<ChatMessage> msgList = new ArrayList<>();
        String holiday_name = "";
        Log.d("AJKSJJSHJHSJAAS", "2");
        String fetch_user_class = "";
        String prevTime = "", latestTime = "";
        if (fromtype1 == 88) {
            Log.d("TETSTTRT2323", "1");
            fetch_user_class = "select " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_READ_STATUS + ", " + KEY_MSG_PARTNER_USERNAME + ", " + KEY_MSG_RECEIVED_TIMESTAMP + ", " + KEY_MSG_ROOM_ID + ", " + KEY_MSG_RECIEPT_TIMESTAMP + ", " + KEY_MSG_RECEIVED_TIMESTAMP + ", " + KEY_MSG_MEDIA_ISDOWNLOADED + ", " + KEY_MSG_NOTI_FAILED + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_ROOM_ID + "='" + roomId + "'" + " AND " + KEY_MSG_TYPE_TC + "='" + "inquiry" + "'" + " order by " + KEY_MSG_TIMESTAMP + " desc limit " + 30;

        } else {
            Log.d("TETSTTRT2323", "2");
            //   String fetch_user_class =  "select * from(" +"SELECT " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_MEDIA_URL + ", " + KEY_MSG_LOCAL_URI + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_THUMB_URL + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_READ_STATUS + ", " + KEY_MSG_PARTNER_USERNAME + ", "+ KEY_MSG_RECEIVED_TIMESTAMP + ", "+ KEY_MSG_MEDIA_DURATION + ", " + KEY_MSG_ROOM_ID + " FROM " + TABLE_MESSAGES +"order by"+ KEY_MSG_TIMESTAMP+" limit 10"+ " WHERE " + KEY_MSG_ROOM_ID + "='" + roomId + "'";
            fetch_user_class = "select " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_READ_STATUS + ", " + KEY_MSG_PARTNER_USERNAME + ", " + KEY_MSG_RECEIVED_TIMESTAMP + ", " + KEY_MSG_ROOM_ID + ", " + KEY_MSG_RECIEPT_TIMESTAMP + ", " + KEY_MSG_RECEIVED_TIMESTAMP + ", " + KEY_MSG_MEDIA_ISDOWNLOADED + ", " + KEY_MSG_NOTI_FAILED + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_ROOM_ID + "='" + roomId + "'" + " order by " + KEY_MSG_TIMESTAMP + " desc limit " + 30;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(fetch_user_class, null);
        Log.d("CURSORCOUNT88", cursor.getCount() + " - -");
        Log.d("AJKSJJSHJHSJAAS", "4");
        if (cursor.moveToFirst()) {
            do {

                ChatMessage contact = new ChatMessage();


                contact.setId(cursor.getString(0));
                Log.d("CHECKTIMEBEFOREtEST", "-->" + "" + cursor.getString(1));
                contact.setMessageText(cursor.getString(1));

                String time = cursor.getString(2);
                contact.setMessageTimeNew(time);
                int k;
                //contact.setMessageType("text");
                if (cursor.getString(3).equals("0")) {
                    Log.d("CEHCKMESSAGETYP", "otherrrr");
                    contact.setUserType(UserType.OTHER);
                    k = 0;
                } else {
                    Log.d("CEHCKMESSAGETYP", "selff");
                    contact.setUserType(UserType.SELF);
                    k = 1;
                }
                if (cursor.getString(4).equals("0")) {
                    if (k == 1)
                        contact.setMessageType("text_self");
                    else
                        contact.setMessageType("text_other");
                    Log.d("SQLITEDATA999AUDIO666", "audio-->" + cursor.getString(7));
                    contact.setMsgStatus(cursor.getString(7));
                    Log.d("SQLITEDATA999", cursor.getString(4) + "--");
                } else if (cursor.getString(4).equals("6")) {
                    contact.setMessageType("groupheader");

                } else if (cursor.getString(4).equals("7")) {
                    contact.setMessageType("chatheader");

                } else if (cursor.getString(4).equals("2")) {
                    contact.setMessageType("text_self_q");

                } else if (cursor.getString(4).equals("3")) {
                    contact.setMessageType("text_other_q");

                } else if (cursor.getString(4).equals("4")) {
                    contact.setMessageType("text_other_inquiry");

                }

                contact.setReadStatus(cursor.getString(7));
                //  Log.d("CHECK97975464", cursor.getString(8));
                contact.setOtherUsreName(cursor.getString(8));

                contact.setMsgStatus(cursor.getString(5));

                contact.setMsgRoomId(cursor.getString(10));
                contact.setMsgQuotedText(cursor.getString(11));
                contact.setMsgQuotedId(cursor.getString(12));
                contact.setMsgQuotePosition(cursor.getString(13));

                // contact.setIsFailed(cursor.getString(13));
//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
                // Adding contact to list

                msgList.add(contact);


            }
            while (cursor.moveToNext());
        }
        return msgList;
    }


    public ArrayList<ChatMessage> fetchAllMsgsUnread(String roomId) {
        ArrayList<ChatMessage> msgList = new ArrayList<>();
        String holiday_name = "";

        String prevTime = "", latestTime = "";
        String fetch_user_class = "SELECT " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_READ_STATUS + ", " + KEY_MSG_PARTNER_USERNAME + ", " + KEY_MSG_RECEIVED_TIMESTAMP + ", " + KEY_MSG_ROOM_ID + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_ROOM_ID + "='" + roomId + "'" + " AND " + KEY_MSG_READ_STATUS + "!='" + 1 + "'" + " AND " + KEY_FROM_ME + "='" + 0 + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(fetch_user_class, null);

        if (cursor.moveToFirst()) {
            do {
                Log.d("CURSORCOUNT88RECORDS", cursor.getString(1) + " - -" + cursor.getString(1) + " - -" + cursor.getString(7));
                ChatMessage contact = new ChatMessage();
                contact.setId(cursor.getString(0));
                Log.d("CHECKTIMEBEFOREtEST", "-->" + "" + cursor.getString(0));
           /*     contact.setMessageText(cursor.getString(1));

                String time = cursor.getString(2);

                Log.d("CHECKTIMEBEFOREtEST", time + "--");
                latestTime = ConvertGMTtoIST.getDateFromTt(time);
                if (!latestTime.equals(prevTime)) {
                    contact.setFirstMsg(true);
                    contact.setDateChanged(true);
                    prevTime = latestTime;
                } else {
                    contact.setDateChanged(false);
                    contact.setFirstMsg(false);
                }
                contact.setMessageTimeNew(time);
                int k;
                //contact.setMessageType("text");
                if (cursor.getString(3).equals("0")) {
                    Log.d("CEHCKMESSAGETYP", "otherrrr");
                    contact.setUserType(UserType.OTHER);
                    k = 0;
                } else {
                    Log.d("CEHCKMESSAGETYP", "selff");
                    contact.setUserType(UserType.SELF);
                    k = 1;
                }
                if (cursor.getString(4).equals("0")) {
                    if (k == 1)
                        contact.setMessageType("text_self");
                    else
                        contact.setMessageType("text_other");
                    Log.d("SQLITEDATA999AUDIO666", "audio-->" + cursor.getString(7));
                    contact.setMsgStatus(cursor.getString(7));
                    Log.d("SQLITEDATA999", cursor.getString(4) + "--");
                } else if (cursor.getString(4).equals("1")) {
                    if (k == 1)
                        contact.setMessageType("image_self");

                    else
                        contact.setMessageType("image_other");
                    contact.setImgURL(cursor.getString(5));
                    contact.setAudioFilePath(cursor.getString(6));
                    contact.setMsgStatus(cursor.getString(7));
                    Log.d("SQLITEDATA9998", cursor.getString(8) + "--");
                    contact.setImgThumb(cursor.getString(8));
                    if (cursor.getString(9) != null) {
                        contact.setIsUploaded(cursor.getString(9));
                    }
                } else if (cursor.getString(4).equals("2")) {

                    contact.setMsgStatus(cursor.getString(7));
                    if (k == 1)
                        contact.setMessageType("audio_self");
                    else
                        contact.setMessageType("audio_other");
                    contact.setImgURL(cursor.getString(5));
                    if (cursor.getString(9) != null) {
                        contact.setIsUploaded(cursor.getString(9));
                    }
                    Log.d("SQLITEDATA999AUDIO", "audio-->" + cursor.getString(5));

                    if (cursor.getString(6) != null)
                        contact.setAudioFilePath(cursor.getString(6));
                } else if (cursor.getString(4).equals("3")) {
                    if (k == 1)
                        contact.setMessageType("map_self");
                    else
                        contact.setMessageType("map_other");

                    try {


                        String splited[] = cursor.getString(1).split("\\s+");
                        Log.d("CHECKBEFORE999", splited[1] + " -->" + splited[2]);
                        contact.setLatitude(Double.parseDouble(splited[1]));
                        contact.setLongitude(Double.parseDouble(splited[2]));
                        contact.setMsgStatus(cursor.getString(7));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                } else if (cursor.getString(4).equals("4")) {
                    Log.d("SQLITEDATA999VIDEO", "video");
                    if (k == 1) {
                        contact.setMessageType("video_self");
                        contact.setImgThumb(cursor.getString(13));
                    } else
                        contact.setMessageType("video_other");
                    contact.setMsgStatus(cursor.getString(7));
                    contact.setImgURL(cursor.getString(5));
                    if (cursor.getString(9) != null) {
                        contact.setIsUploaded(cursor.getString(9));
                    }
                    Log.d("SQLITEDATA999AUDIO", "audio-->" + cursor.getString(5));

                    if (cursor.getString(6) != null)
                        contact.setAudioFilePath(cursor.getString(6));


                } else if (cursor.getString(4).equals("6")) {
                    contact.setMessageType("groupheader");

                } else if (cursor.getString(4).equals("7")) {
                    contact.setMessageType("chatheader");

                }
                contact.setReadStatus(cursor.getString(10));
                contact.setOtherUsreName(cursor.getString(11));
                Log.d("CHECK9797", cursor.getString(12));
                contact.setUniqueMsgId(Long.parseLong(cursor.getString(12)));
                contact.setMsgRoomId(cursor.getString(14));
                contact.setTripId(cursor.getString(15));
//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
                //                // Adding contact to list*/
                msgList.add(contact);


            }
            while (cursor.moveToNext());
        }
        return msgList;
    }

    public ArrayList<ChatMessage> fetchAllMediaFromMsgsFromTripId(String otherUSer, String tripId) {
        ArrayList<ChatMessage> msgList = new ArrayList<>();
        String holiday_name = "";

        String prevTime = "", latestTime = "";
        String fetch_user_class = "SELECT " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_PARTNER_USERNAME + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_ROOM_ID + "='" + otherUSer + "'" + " AND " + KEY_MSG_TYPE + "=1" + " OR " + KEY_MSG_ROOM_ID + "='" + otherUSer + "'" + " AND " + KEY_MSG_TYPE + "= chat";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(fetch_user_class, null);
        Log.d("CURSORCOUNT8855555", cursor.getCount() + " - -");
        if (cursor.moveToFirst()) {
            do {
                Log.d("CURSORCOUNT88RECORDS", cursor.getString(1) + " - -" + cursor.getString(1) + " - -" + cursor.getString(7));
                ChatMessage contact = new ChatMessage();
                contact.setId(cursor.getString(0));
                Log.d("CHECKTIMEBEFOREtEST", "-->" + "" + cursor.getString(0));

                contact.setMessageText(cursor.getString(1));
                String time = cursor.getString(2);

                Log.d("CHECKTIMEBEFOREtEST", time + "--");
                latestTime = ConvertGMTtoIST.getDateFromTt(time);
                if (!latestTime.equals(prevTime)) {
                    contact.setFirstMsg(true);
                    contact.setDateChanged(true);
                    prevTime = latestTime;
                } else {
                    contact.setDateChanged(false);
                    contact.setFirstMsg(false);
                }
                contact.setMessageTimeNew(time);
                int k;
                //contact.setMessageType("text");
                if (cursor.getString(3).equals("0")) {
                    Log.d("CEHCKMESSAGETYP", "otherrrr");
                    contact.setUserType(UserType.OTHER);
                    k = 0;
                } else {
                    Log.d("CEHCKMESSAGETYP", "selff");
                    contact.setUserType(UserType.SELF);

                    k = 1;
                }
                if (k == 1) {
                    if (cursor.getString(4).equals("1"))
                        contact.setMessageType("image_self");
                    else if (cursor.getString(4).equals("4"))
                        contact.setMessageType("video_self");
                    contact.setOtherUsreName("You");
                } else {
                    if (cursor.getString(4).equals("1")) {
                        contact.setMessageType("image_other");
                    } else if (cursor.getString(4).equals("4"))
                        contact.setMessageType("video_other");
                    String name = null;
                    Log.d("CHECKTHIS5555", cursor.getString(10) + "=====");
                    name = this.getContactInfoByPhone(cursor.getString(10)).getName();
                    Log.d("CHECKTHIS5555", name + "=====1");

                    contact.setOtherUsreName(name);
                }


                contact.setImgURL(cursor.getString(5));
                contact.setAudioFilePath(cursor.getString(6));
                contact.setMsgStatus(cursor.getString(7));
                Log.d("SQLITEDATA9998", cursor.getString(8) + "--");
                contact.setImgThumb(cursor.getString(8));
                if (cursor.getString(9) != null) {
                    contact.setIsUploaded(cursor.getString(9));
                }


//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
                // Adding contact to list
                if (cursor.getString(6) == null || cursor.getString(6).equals("")) {

                } else {
                    msgList.add(contact);
                }
            } while (cursor.moveToNext());
        }
        return msgList;

    }


    public ChatMessage fetchSingleMsgFromTimestamp(String msgid) {

        ChatMessage contact = new ChatMessage();
        Log.d("CURSORCOUNT88", msgid + " - -");
        String prevTime = "", latestTime = "";
        String fetch_user_class = "SELECT " + KEY_MSG_ID + ", " + KEY_MSG_DATA + ", " + KEY_MSG_TIMESTAMP + ", " + KEY_FROM_ME + ", " + KEY_MSG_TYPE + ", " + KEY_MSG_STATUS + ", " + KEY_MSG_MEDIA_ISUPLOADED + ", " + KEY_MSG_READ_STATUS + ", " + KEY_MSG_PARTNER_USERNAME + ", " + KEY_MSG_ROOM_ID + ", " + KEY_MSG_TYPE_TC + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_TIMESTAMP + "='" + msgid + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(fetch_user_class, null);
        Log.d("CURSORCOUNT88", cursor.getCount() + " - -");
        if (cursor.moveToFirst()) {

            Log.d("CEHCKPARAMS9999123", cursor.getString(13) + "--inserted====------------------" + cursor.getString(14));
            contact.setId(cursor.getString(0));
            Log.d("CHECKTIMEBEFOREtEST", "-->" + "" + cursor.getString(0));

            contact.setMessageText(cursor.getString(1));
            String time = cursor.getString(2);

            Log.d("CHECKTIMEBEFOREtEST", time + "--");
            latestTime = ConvertGMTtoIST.getDateFromTt(time);

            contact.setMessageTimeNew(time);
            int k;
            //contact.setMessageType("text");
            if (cursor.getString(3).equals("0")) {
                Log.d("CEHCKMESSAGETYP", "otherrrr");
                contact.setUserType(UserType.OTHER);
                k = 0;
            } else {
                Log.d("CEHCKMESSAGETYP", "selff");
                contact.setUserType(UserType.SELF);
                k = 1;
            }
            if (cursor.getString(4).equals("0")) {
                if (k == 1)
                    contact.setMessageType("text_self");
                else
                    contact.setMessageType("text_other");
                Log.d("SQLITEDATA999AUDIO666", "audio-->" + cursor.getString(7));
                contact.setMsgStatus(cursor.getString(7));
                Log.d("SQLITEDATA999", cursor.getString(4) + "--");
            } else if (cursor.getString(4).equals("1")) {
                if (k == 1)
                    contact.setMessageType("image_self");

                else
                    contact.setMessageType("image_other");
                contact.setImgURL(cursor.getString(5));
                contact.setAudioFilePath(cursor.getString(6));
                contact.setMsgStatus(cursor.getString(7));
                Log.d("SQLITEDATA9998", cursor.getString(8) + "--");
                contact.setImgThumb(cursor.getString(8));
                if (cursor.getString(9) != null) {
                    contact.setIsUploaded(cursor.getString(9));
                }
            } else if (cursor.getString(4).equals("2")) {

                contact.setMsgStatus(cursor.getString(7));
                if (k == 1)
                    contact.setMessageType("audio_self");
                else
                    contact.setMessageType("audio_other");
                contact.setImgURL(cursor.getString(5));
                if (cursor.getString(9) != null) {
                    contact.setIsUploaded(cursor.getString(9));
                }
                Log.d("SQLITEDATA999AUDIO", "audio-->" + cursor.getString(5));

                if (cursor.getString(6) != null)
                    contact.setAudioFilePath(cursor.getString(6));
            } else if (cursor.getString(4).equals("3")) {
                if (k == 1)
                    contact.setMessageType("map_self");
                else
                    contact.setMessageType("map_other");

                try {


                    String splited[] = cursor.getString(1).split("\\s+");
                    Log.d("CHECKBEFORE999", splited[1] + " -->" + splited[2]);
                    contact.setLatitude(Double.parseDouble(splited[1]));
                    contact.setLongitude(Double.parseDouble(splited[2]));
                    contact.setMsgStatus(cursor.getString(7));
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            } else if (cursor.getString(4).equals("4")) {
                Log.d("SQLITEDATA999VIDEO", "video");
                if (k == 1)
                    contact.setMessageType("video_self");
                else
                    contact.setMessageType("video_other");
                contact.setMsgStatus(cursor.getString(7));
                contact.setImgURL(cursor.getString(5));
                if (cursor.getString(9) != null) {
                    contact.setIsUploaded(cursor.getString(9));
                }
                Log.d("SQLITEDATA999AUDIO", "audio-->" + cursor.getString(5));

                if (cursor.getString(6) != null)
                    contact.setAudioFilePath(cursor.getString(6));


            } else if (cursor.getString(4).equals("6")) {
                contact.setMessageType("groupheader");

            } else if (cursor.getString(4).equals("7")) {
                contact.setMessageType("chatheader");
            }
            contact.setReadStatus(cursor.getString(10));
            contact.setOtherUsreName(cursor.getString(11));
            contact.setMsgRoomId(cursor.getString(12));
            contact.setTripId(cursor.getString(13));
            contact.setMsgType(cursor.getString(14));
            contact.setSenderTripTimestamp(cursor.getString(15));
//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
            // Adding contact to list


        }
        return contact;
    }

    public UserDetails getContactByPhoneID(String id) {
        UserDetails contact = new UserDetails();
        try {


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_CONTACTS_NEW, new String[]{KEY_CONTACT_RC_USERID, KEY_CONTACT_NAME, KEY_CONTACT_NUMBER, KEY_CONTACT_PHOTO, KEY_CONTACT_BACKGROUND_PIC, KEY_USER_COUNTRY_CODE, KEY_CONTACT_USER_STATUS, KEY_CONTACT_ACTIVE}, KEY_CONTACT_ID + "=?", new String[]{id}, null, null, null);
            // Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list

            if (cursor != null && cursor.moveToFirst()) {
                Log.d("CursorCOUNT999", cursor.getCount() + "--" + cursor.getString(0) + "--" + cursor.getString(1) + "--" + cursor.getString(2) + "--" + cursor.getString(4) + "---->" + cursor.getString(6) + "---->" + cursor.getString(7));
                contact.setUser_id(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contact.setProfile_pic(cursor.getString(3));
                contact.setBackground_pic(cursor.getString(4));
                contact.setCountry_code(cursor.getString(5));
                contact.setStatus(cursor.getString(6));
                contact.setActive(cursor.getString(7));

                // Adding contact to list
            }


        } catch (Exception e) {
            Log.d("CUSRSORCOUNT555", "ERror" + "--");
            // return contact list
            e.printStackTrace();
        }
        return contact;
    }

    public int updateContactNameById(String contactid, String contactname) {
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;

        try {
            ContentValues CV = new ContentValues();

            if (contactid != null) {


                CV.put(KEY_CONTACT_NAME, contactname);

                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_CONTACTS_NEW, CV, KEY_CONTACT_ID + " = ? ", new String[]{contactid});
                u = rows;
                Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

  /*  public int updateMsgTripSenderTimestamp(String msgId, String tt) {

        SQLiteDatabase db = this.getWritableDatabase();
        int updated = 0;
        Log.d(TAG, "CHECKUPDATEMSG88891213: " + msgId + "===" + tt);

        try {
            ContentValues CV = new ContentValues();

            if (msgId != null) {
                CV.put(KEY_MSG_MEDIA_DURATION, tt);
                //values.put(KEY_USERNAME, username); // Name
                int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_TIMESTAMP + " = ? ", new String[]{msgId});
                updated = rows;

                Log.d("CEHCK88888", "update==" + rows);
                Log.d(TAG, "CHECKUPDATEMS111G8889: " + rows + "--rows");
                Log.d("SENDVIDEOAPI", " 2222" + " --" + rows);
                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }
*/


    public int updateContactNameByNumber(String contactid, String contactname) {
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;

        try {
            ContentValues CV = new ContentValues();

            if (contactid != null) {


                CV.put(KEY_CONTACT_NAME, contactname);

                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_CONTACTS_NEW, CV, KEY_CONTACT_NUMBER + " = ? ", new String[]{contactid});
                u = rows;
                Log.d(TAG, "CHECKROOMUPDATEVALUE555: " + rows + "--rows");

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    public String getContactByPhoneForTopicMsgs(String phoneCheck) {
        Log.d("cHECKCOUNTCUROSR", phoneCheck + "---in");
        Log.d("CHECCKUPDATES11", "--- in sql" + phoneCheck);
        String contactId1 = null;
        String fetch_user_Contactid = "SELECT " + KEY_CONTACT_ID + " , " + KEY_CONTACT_NUMBER + " , " + KEY_CONTACT_ISMULTIPLE_NUMBER + " FROM " + TABLE_CONTACTS_NEW + " WHERE " + KEY_CONTACT_NUMBER + "='" + phoneCheck + "'";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        SQLiteDatabase sqLiteDatabase1 = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(fetch_user_Contactid, null);
        if (cursor.moveToFirst()) {

            Log.d("CHECCKUPDATES11", "--- iff" + cursor.getCount());
            contactId1 = cursor.getString(0);
            cursor.close();

            return contactId1;
        } else {
            String fetch_user_Contactid1 = "SELECT " + KEY_CONTACT_ID + " , " + KEY_CONTACT_NUMBER + " , " + KEY_CONTACT_ISMULTIPLE_NUMBER + " FROM " + TABLE_CONTACTS_NEW + " WHERE " + KEY_CONTACT_ISMULTIPLE_NUMBER + "='" + "1" + "'";
            Cursor cursor1 = sqLiteDatabase1.rawQuery(fetch_user_Contactid1, null);
            Log.d("CHECCKUPDATES11", "--- else" + phoneCheck);
            if (cursor1.moveToFirst()) {
                do {

                    String num = cursor1.getString(1);
                    String nums[] = num.split(",");
                    Log.d("cHECKCOUNTCUROSRSPLIT", contactId1 + "------");
                    for (int i = 0; i < nums.length; i++) {
                        if (nums[i].equals(phoneCheck)) {
                            Log.d("cHECKCOUNTCUROSRSPLIT", nums[i] + "------" + phoneCheck);
                            contactId1 = cursor1.getString(0);
                        }
                    }

                }
                while (cursor1.moveToNext());
                cursor1.close();
            }

            return contactId1;
        }


    }

    public int fetchTotalAppUsers(String mob) {


        Log.d("JJDJAKKJD", mob);
        // Select All Query
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS_NEW + " WHERE " + KEY_CONTACT_TYPE + "='u' AND " + KEY_CONTACT_NUMBER + "!='" + mob + "'";


        Cursor cursor = db.rawQuery(selectQuery, null);


        // return contact list
        return cursor.getCount();
    }

    public int fetchTotalMsgs(String roomId) {


        // Select All Query
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_ROOM_ID + "!='" + roomId + "'";


        Cursor cursor = db.rawQuery(selectQuery, null);


        // return contact list
        return cursor.getCount();
    }


    public int updateContactType(String contactid, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        int u = 0;

        try {
            ContentValues CV = new ContentValues();

            if (contactid != null) {


                CV.put(KEY_CONTACT_TYPE, type);

                //values.put(KEY_USERNAME, username); // Name

                int rows = db.update(TABLE_CONTACTS_NEW, CV, KEY_CONTACT_NUMBER + " = ? ", new String[]{contactid});
                u = rows;
                Log.d(TAG, "CHECKROOMUPDATEVALUE55511: " + rows + "--rows" + contactid);

                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    public int updateContactIdAndTyp(String cid, String userType, String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        try {
            ContentValues values = new ContentValues();


            //ContentValues values = new ContentValues();
            // Name
            values.put(KEY_CONTACT_ID, cid);
            values.put(KEY_CONTACT_TYPE, userType);

            rows = db.update(TABLE_CONTACTS_NEW, values, KEY_CONTACT_NUMBER + " = ? ", new String[]{number});
            Log.d(TAG, "UPDATECONTACTNEW: " + rows + "--rows");

        } catch (Exception e) {
            e.printStackTrace();
        }
        // db.close();
        return rows;
    }

    public ArrayList<ChatRoom> getAllRoomsGroup(String self) {
        ArrayList<ChatRoom> msgList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID, KEY_ROOM_NAME, KEY_ROOM_MSG_LAST_TEXT, KEY_ROOM_MSG_LAST_TIMESTAMP, KEY_ROOM_OTHER_USER_PIC, KEY_ROOM_MSG_READ, KEY_ROOM_GROUP_NAME, KEY_ROOM_GROUP_PIC, KEY_ROOM_GROUP_MEMBERS, KEY_ROOM_TYPE, KEY_ROOM_GROUP_CREATION_DATE, KEY_ROOM_OTHER_USER_STATUS}, KEY_ROOM_TYPE + "=? AND " + KEY_ROOM_USERNAME_SELF + "=?", new String[]{"g", self}, null, null, null);
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("CHECKCURSORSIZE9999", cursor.getCount() + "--");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                ChatRoom contact = new ChatRoom();
                contact.setId(cursor.getString(0));
                contact.setUsername(cursor.getString(1));
                contact.setLastMessage(cursor.getString(2));
                contact.setTimestamp(cursor.getString(3));
                contact.setOtherProfpic(cursor.getString(4));
                contact.setName(cursor.getString(1));
                contact.setGrp_name(cursor.getString(6));
                contact.setGrp_pic(cursor.getString(7));
                contact.setGrp_members(cursor.getString(8));
                contact.setRoom_TYpe(cursor.getString(9));


                msgList.add(contact);


            } while (cursor.moveToNext());

        }

        // return contact list
        return msgList;
    }

    public int updateMsgStatusForBlockedUsers(String timestmp, String s) {
        SQLiteDatabase db = this.getWritableDatabase();
        int updated = 0;
        try {
            ContentValues CV = new ContentValues();

            if (timestmp != null) {


                CV.put(KEY_MSG_STATUS, "8");

                //values.put(KEY_USERNAME, username); // Name
                Log.d("SENDVIDEOAPI", " 111" + " --" + timestmp + "------>" + s);
                int rows = db.update(TABLE_MESSAGES, CV, KEY_MSG_ID + " = ? ", new String[]{timestmp});
                updated = rows;
                Log.d(TAG, "CHECKUPDATEMSG888912311: " + rows + "-->>>>>>>>>>>>>>>>>>>>>>>>>." + s);
                Log.d(TAG, "CHECKUPDATEMSG8889: " + rows + "--rows");
                Log.d("SENDVIDEOAPI", " 2222" + " --" + rows);
                // String wheres = KEY_USER_ID + "=?";
                // String[] whereArgs = {user_id.toString()};
                //db.update(OTHER_USER, CV, wheres, whereArgs);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }

    public int getFailedNotiExistSingle(String msgIdId) {
        int count = 0;
        Log.d("CEHCKHGDJGHFDJK", msgIdId + "---");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {

            cursor = db.query(TABLE_NOTIFICATIONS, new String[]{KEY_NOTI_MSG_ID}, KEY_NOTI_MSG_ID + "=?", new String[]{msgIdId}, null, null, null);

            //   Log.d("CEHCKHGDJGHFDJK", cursor.getCount() + "---");
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor == null) {
            count = 0;
        } else {
            count = cursor.getCount();
        }
        if (cursor != null)
            cursor.close();
        return count;
    }

    public int getFailedNotiExist(String msgIdId, String member) {
        int count = 0;
        Log.d("CEHCKHGDJGHFDJK", msgIdId + "---");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {

            cursor = db.query(TABLE_NOTIFICATIONS, new String[]{KEY_NOTI_MSG_ID}, KEY_NOTI_MSG_ID + "=? AND " + KEY_NOTI_OTHER_USER + "=?", new String[]{msgIdId, member}, null, null, null);

            //   Log.d("CEHCKHGDJGHFDJK", cursor.getCount() + "---");
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor == null) {
            count = 0;
        } else {
            count = cursor.getCount();
        }
        if (cursor != null)
            cursor.close();
        return count;
    }

    public boolean ifDataExists() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGES, null);
        Boolean rowExists;

        if (mCursor.moveToFirst()) {
            // DO SOMETHING WITH CURSOR
            rowExists = true;

        } else {
            // I AM EMPTY
            rowExists = false;
        }
        return rowExists;
    }

    public int deleteFailedNotificatiom(String msgId, String member) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        try {
            i = db.delete(TABLE_NOTIFICATIONS, KEY_NOTI_MSG_ID + "=? AND " + KEY_NOTI_OTHER_USER + "=?", new String[]{msgId, member});
            // db.execSQL("delete from " + TABLE_MESSAGES + " where " + KEY_MSG_PARTNER_USERNAME + "='" + id + "'");
            //   int i = db.delete(TABLE_MESSAGES, KEY_MSG_ID + "=" + id, null);
            //  Log.d("CHECKMSGDELETEE", i + " ---");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;

        // db.close();
    }

    public int deleteFailedNotificatnSingle(String msgId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        try {
            i = db.delete(TABLE_NOTIFICATIONS, KEY_NOTI_MSG_ID + "=?", new String[]{msgId});
            // db.execSQL("delete from " + TABLE_MESSAGES + " where " + KEY_MSG_PARTNER_USERNAME + "='" + id + "'");
            //   int i = db.delete(TABLE_MESSAGES, KEY_MSG_ID + "=" + id, null);
            //  Log.d("CHECKMSGDELETEE", i + " ---");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;

        // db.close();
    }

    public void backup(String out, File newFolder, String s, String uid, String outFileName) {

        //database path
        final String inFileName = context.getDatabasePath(DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(out);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();
            uploadFileToServer(newFolder, s, uid, outFileName);
            Toast.makeText(context, "Backup Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, "Unable to backup database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void importDB(String inFileName, String uid) {

        final String outFileName = context.getDatabasePath(DATABASE_NAME).getPath().toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();
            Log.d("HDAJHDJAHJADH", "imported completed");
            Toast.makeText(context, "Import Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, "Unable to import database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void uploadFileToServer(File folder, String out, String uid, String username) {
        Log.d("APICALL", folder.getAbsolutePath() + "==" + out + "====" + uid);
        Log.d("TEST12234523", folder.exists() + "==");

        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", folder.getName(),
                            RequestBody.create(folder, MediaType.parse("db")))
                    .addFormDataPart("fid", uid)
                    .addFormDataPart("mobile", username)
                    .addFormDataPart("is_mobile", "APP")
                    .build();

            Request request = new Request.Builder()
                    .url(AppConstants.BASE_API_URL + "uploaddbfile")
                    .post(requestBody)
                    .build();


            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(final Call call, final IOException e) {
                    //      Toast.makeText(context, "failed :  ", Toast.LENGTH_SHORT).show();

                    e.printStackTrace();
                    // Handle the error
                }

                @Override
                public void onResponse(final Call call, final Response response) throws IOException {

                    String jsonData = response.body().string();
                    Log.d("JASKAJKJHKAH", jsonData + "==");
                    try {
                        JSONObject Jobject = new JSONObject(jsonData);

                        if (Jobject.has("status")) {
                            if (Jobject.getBoolean("status")) {
                                Log.d("KJHDHJSDHJHJSHJ", "2");
                                sharedPreferencesbackup.edit().putBoolean("flag", false).apply();
                                sharedPreferencesbackup.edit().putString("time", ConvertGMTtoIST.getCurrentDateTime()).apply();
                            } else {
                                Log.d("TESTBACKUP", "false");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (!response.isSuccessful()) {
                        // Handle the error
                    }
                    // Upload successful
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle the error
        }
        //return false;
    }

    public ArrayList<ChatRoom> getAllRoomsInquiryGrp(String self, String type) {
        ArrayList<ChatRoom> msgList = new ArrayList<>();
        Log.d("JHAJAHHJHAJ", self + "==" + type);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROOM, new String[]{KEY_ROOM_ID, KEY_ROOM_USERNAME_OTHER, KEY_ROOM_MSG_LAST_TEXT, KEY_ROOM_MSG_LAST_TIMESTAMP, KEY_ROOM_OTHER_USER_PIC, KEY_ROOM_NAME, KEY_ROOM_GROUP_NAME, KEY_ROOM_GROUP_PIC, KEY_ROOM_GROUP_MEMBERS, KEY_ROOM_TYPE, KEY_ROOM_GROUP_CREATION_DATE, KEY_ROOM_OTHER_USER_STATUS, KEY_ROOM_IS_BLOCKED}, KEY_ROOM_USERNAME_SELF + "=? AND " + KEY_ROOM_TYPE + " =?", new String[]{self, type}, null, null, null);
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("CHECKCURSORSIZE9999", cursor.getCount() + "--");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.d("CHECKCURSORSIZE9999666", "--" + cursor.getString(5) + "==" + cursor.getString(1) + "===" + cursor.getString(8));

                ChatRoom contact = new ChatRoom();
                contact.setId(cursor.getString(0));
                contact.setUsername(cursor.getString(1));
                contact.setLastMessage(cursor.getString(2));
                contact.setTimestamp(cursor.getString(3));
                contact.setOtherProfpic(cursor.getString(4));
                contact.setName(cursor.getString(5));
                if (type.equals("g"))
                    contact.setGrp_members(cursor.getString(8));
                contact.setRoom_TYpe(cursor.getString(9));
               /* contact.setGrp_name(cursor.getString(6));
                contact.setGrp_pic(cursor.getString(7));
                contact.setGrp_members(cursor.getString(8));
                contact.setRoom_TYpe(cursor.getString(9));
                contact.setCreation_date(cursor.getString(10));
                if (cursor.getString(11).equals("1")) {
                    contact.setRoomActive("1");
                } else {
                    contact.setRoomActive("0");
                }*/
                if (cursor.getString(12).equals("1")) {
                    contact.setBlocked(true);
                } else {
                    contact.setBlocked(false);
                }
                //contact.setMessageTimeNew(cursor.getString(2));

//            Log.d("jhjgjjkgj", cursor.getString(10) + "---");
                // Adding contact to list
              /*  if (cursor.getString(2) != null && cursor.getString(3) != null && !cursor.getString(2).equals("") && !cursor.getString(3).equals("")) {
                    if (cursor.getString(0).equals("deletedEntry")) {
                        msgList.add(contact);
                    } else {*/
                msgList.add(contact);
                // }


            } while (cursor.moveToNext());

        }

        // return contact list
        return msgList;
    }

    public long addBookingDetails(BookingDetails bookingDetails, String type) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BOOKING_ID, bookingDetails.getBookingId()); // Name
        values.put(KEY_BOOKING_DID, bookingDetails.getDs_id()); // Name
        //values.put(KEY_USERNAME, username); // Name
        values.put(KEY_BOOKING_DS_NM, bookingDetails.getDs_name()); // Name
        values.put(KEY_BOOKING_YATRI_NAME, bookingDetails.getYatriNm()); // Email

        //  values.put(KEY_USER_TOKEN, userToken);

        values.put(KEY_BOOKING_YATRIADDRESS, bookingDetails.getYatriAddress()); // Name
        values.put(KEY_BOOKING_Y_MOB_NO, bookingDetails.getYatriMobile()); // Name
        //values.put(KEY_USERNAME, username); // Name
        values.put(KEY_BOOKING_ROOM_TYPE, bookingDetails.getRoomTyp()); // Name
        values.put(KEY_BOOKING_CHECK_IN, bookingDetails.getCheckinDt()); // Email
        values.put(KEY_BOOKING_CHECKOUT, bookingDetails.getCheckoutDt()); // Name
        values.put(KEY_BOOKING_NO_ROOM, bookingDetails.getNoOfRooms()); // Name
        //values.put(KEY_USERNAME, username); // Name
        values.put(KEY_BOOKING_GUESTS, bookingDetails.getPerson()); // Name
        values.put(KEY_BOOKING_CONTIBUTION, bookingDetails.getContribution()); // Email
        if(type.equals("inquiryLatest")) {
            values.put(KEY_BOOKING_SUBTOTAL, bookingDetails.getSuggestion()); // Name //suggestion
            values.put(KEY_BOOKING_NEFT_NO, bookingDetails.getReply());
        }else{
            values.put(KEY_BOOKING_SUBTOTAL, bookingDetails.getBookingTotal());
            values.put(KEY_BOOKING_NEFT_NO, bookingDetails.getNeftNo()); // Name    //reply
        }

        values.put(KEY_BOOKING_TRANFER_DATE, bookingDetails.getTranferDt()); // Name
        values.put(KEY_BOOKING_TRANFER_AMT, bookingDetails.getTranferAmt()); // Email
        values.put(KEY_BOOKING_ORDER_STATUS, bookingDetails.getBookingStatus()); // Name
        values.put(KEY_BOOKING_EXPECTED_DATETIME, bookingDetails.getExpectedCheckinTime()); // Name
        values.put(KEY_BOOKING_TYPE, type); // Name
        values.put(KEY_NIGHTS, bookingDetails.getNights());
        values.put(KEY_EXTRA_FIELD, bookingDetails.getIs_24Hours()); // Name
        values.put(KEY_EXTRA_FIELD1, bookingDetails.getBookingConfirmedBy());
        Log.d("KAJKSJKKS", bookingDetails.getBookingRoomsType() + "===");
        values.put(KEY_EXTRA_FIELD2, bookingDetails.getBookingRoomsType());
        values.put(KEY_EXTRA_FIELD3, bookingDetails.getBookingBookedOn());
        // Inserting Row
        long id_res = db.insert(TABLE_BOOKING_DETAILS, null, values);
        // db.close(); // Closing database connection

        Log.d(TAG, "New Booking Details inserted into sqlite : " + id_res + "==" + type);
        return id_res;
    }

    public ArrayList<BookingDetails> fetchAllBookings(String type, String d_id) {
        Log.d("JGSDHGHGDGDHGH123", type + "===>" + d_id);
        ArrayList<BookingDetails> bookingsList = new ArrayList<>();
        try {


            String fetch_user_class = "SELECT " + KEY_BOOKING_ID + ", " + KEY_BOOKING_DID + ", " + KEY_BOOKING_DS_NM + ", " + KEY_BOOKING_YATRI_NAME + ", " + KEY_BOOKING_YATRIADDRESS + ", " + KEY_BOOKING_Y_MOB_NO + ", " + KEY_BOOKING_ROOM_TYPE + ", "
                    + KEY_BOOKING_CHECK_IN + ", " + KEY_BOOKING_CHECKOUT + ", " + KEY_BOOKING_NO_ROOM + ", " + KEY_BOOKING_GUESTS + ", " + KEY_BOOKING_CONTIBUTION + ", " + KEY_BOOKING_SUBTOTAL + ", " + KEY_BOOKING_NEFT_NO + ", "
                    + KEY_BOOKING_TRANFER_DATE + ", " + KEY_BOOKING_TRANFER_AMT + ", " + KEY_BOOKING_ORDER_STATUS + ", " + KEY_BOOKING_EXPECTED_DATETIME + ", " + KEY_EXTRA_FIELD + ", " + KEY_NIGHTS + ", " + KEY_EXTRA_FIELD3 + " FROM " + TABLE_BOOKING_DETAILS + " WHERE " + KEY_BOOKING_DID + "='" + d_id + "'" + " AND " + KEY_BOOKING_TYPE + "='" + type + "'" + " order by " + KEY_BOOKING_CHECK_IN + " desc";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(fetch_user_class, null);
            Log.d("JGSDHGHGDGDHGH123", type + "===>" + d_id + "===>" + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    Log.d("CURSORCOUNT88RECORDS", cursor.getString(0) + " - -" + cursor.getString(1) + " - -" + cursor.getString(7));
                    BookingDetails bookingDetails = new BookingDetails();
                    bookingDetails.setBookingId(cursor.getString(0));

                    bookingDetails.setDs_id(cursor.getString(1));
                    bookingDetails.setDs_name(cursor.getString(2));
                    //contact.setMessageType("text");
                    bookingDetails.setYatriNm(cursor.getString(3));

                    bookingDetails.setYatriAddress(cursor.getString(4));
                    bookingDetails.setYatriMobile(cursor.getString(5));
                    bookingDetails.setRoomTyp(cursor.getString(6));
                    //contact.setMessageType("text");
                    bookingDetails.setCheckinDt(cursor.getString(7));

                    bookingDetails.setCheckoutDt(cursor.getString(8));
                    bookingDetails.setNoOfRooms(cursor.getString(9));
                    bookingDetails.setPerson(cursor.getString(10));
                    bookingDetails.setContribution(cursor.getString(11));
                    bookingDetails.setBookingTotal(cursor.getString(12));
                    //contact.setMessageType("text");
                    bookingDetails.setNeftNo(cursor.getString(13));
                    bookingDetails.setTranferDt(cursor.getString(14));
                    bookingDetails.setTranferAmt(cursor.getString(15));
                    bookingDetails.setBookingStatus(cursor.getString(16));
                    //contact.setMessageType("text");
                    Log.d("ASDJHJHJJHAJH", cursor.getString(18) + "===");
                    bookingDetails.setExpectedCheckinTime(cursor.getString(17));
                    bookingDetails.setIs_24Hours(cursor.getString(18));
                    bookingDetails.setNights(cursor.getString(19));
                    if (cursor.getString(20) != null)
                        bookingDetails.setBookingBookedOn(cursor.getString(20));
               /* if (type.equals("active")) {
                    String dt = bookingDetails.getCheckinDt().trim();
                    String arr[] = dt.trim().split("\\s+");
                    if (arr[0].equals(ConvertGMTtoIST.getCurrentDateBookingList())) {
                        String dtCheckout = bookingDetails.getCheckoutDt().trim();

                        String arrCheckout[] = dtCheckout.trim().split("\\s+");
                        if (ConvertGMTtoIST.getDateActiveCheckout(arrCheckout[0]).compareTo(ConvertGMTtoIST.getCurrentDateActive()) > 0) {
                            bookingsList.add(bookingDetails);
                        }
                    }
                } else {*/
                    bookingsList.add(bookingDetails);
                    //}


                }
                while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookingsList;
    }

    public void deleteFirstRow() {
        SQLiteDatabase db = this.getReadableDatabase();
        //  SQLiteDatabase db1 = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_BOOKING_DETAILS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            String rowId = cursor.getString(cursor.getColumnIndex(KEY_BOOKING_ID));
            Log.d("LADJKJDJDK", rowId + "==");

            db.delete(TABLE_BOOKING_DETAILS, KEY_BOOKING_ID + "=?", new String[]{rowId});
        }
        db.close();
    }

    public ArrayList<BookingDetails> fetchAllInquiries(String d_id) {
        String type = "inquiryLatest";
        Log.d("JGSDHGHGDGDHGH123", type + "===>" + d_id);
        ArrayList<BookingDetails> bookingsList = new ArrayList<>();

        String fetch_user_class = "SELECT " + KEY_BOOKING_ID + ", " + KEY_BOOKING_DID + ", " + KEY_BOOKING_DS_NM + ", " + KEY_BOOKING_YATRI_NAME + ", " + KEY_BOOKING_YATRIADDRESS + ", " + KEY_BOOKING_Y_MOB_NO + ", " + KEY_BOOKING_ROOM_TYPE + ", "
                + KEY_BOOKING_CHECK_IN + ", " + KEY_BOOKING_CHECKOUT + ", " + KEY_BOOKING_NO_ROOM + ", " + KEY_BOOKING_GUESTS + ", "
                + KEY_BOOKING_TRANFER_DATE + ", " + KEY_EXTRA_FIELD + ", " + KEY_NIGHTS + ", " + KEY_BOOKING_NEFT_NO + ", " + KEY_BOOKING_SUBTOTAL + ", " + KEY_EXTRA_FIELD1 + ", " + KEY_EXTRA_FIELD2 + " FROM " + TABLE_BOOKING_DETAILS + " WHERE " + KEY_BOOKING_DID + "='" + d_id + "'" + " AND " + KEY_BOOKING_TYPE + "='" + type + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(fetch_user_class, null);
        Log.d("JGSDHGHGDGDHGH123", type + "===>" + d_id + "===>" + cursor.getCount() + "==");

        if (cursor != null && cursor.moveToLast()) {
            do {

                Log.d("CURSORCOUNT88RECORDS", cursor.getString(0) + " - -" + cursor.getString(1) + " - -" + cursor.getString(7) + "-->>" + cursor.getString(17));
                BookingDetails bookingDetails = new BookingDetails();
                bookingDetails.setBookingId(cursor.getString(0));

                bookingDetails.setDs_id(cursor.getString(1));
                bookingDetails.setDs_name(cursor.getString(2));
                //contact.setMessageType("text");
                bookingDetails.setYatriNm(cursor.getString(3));
                bookingDetails.setYatriAddress(cursor.getString(4));
                bookingDetails.setYatriMobile(cursor.getString(5));
                bookingDetails.setRoomTyp(cursor.getString(6));
                //contact.setMessageType("text");
                bookingDetails.setCheckinDt(cursor.getString(7));
                bookingDetails.setCheckoutDt(cursor.getString(8));
                bookingDetails.setNoOfRooms(cursor.getString(9));
                bookingDetails.setPerson(cursor.getString(10));
                bookingDetails.setNights(cursor.getString(13));
                bookingDetails.setReply(cursor.getString(14));
                bookingDetails.setSuggestion(cursor.getString(15));
                bookingDetails.setBookingConfirmedBy(cursor.getString(16));
                bookingDetails.setBookingRoomsType(cursor.getString(17));
                bookingsList.add(bookingDetails);


            }
            while (cursor.moveToPrevious());
        }
        return bookingsList;
    }


    public ArrayList<NotificationFailed> fetchAllNotifications(String type, String d_id) {
        ArrayList<NotificationFailed> list = new ArrayList<>();
        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTIFICATIONS + " WHERE " + KEY_NOTI_MSGTRIPID + "='" + d_id + "'" + " AND " + KEY_NOTI_MSG_TYP + "='" + type + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("CEHCKFETCHEDDATA7", cursor.getCount() + "----");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NotificationFailed contact = new NotificationFailed();
                contact.setMsg_id(cursor.getString(0));
                contact.setBody(cursor.getString(1));
                // contact.setGrp_name(cursor.getString(2));
                contact.setMsg_typ(cursor.getString(3));
                contact.setMsgtrtipid(cursor.getString(4));
                contact.setTitle(cursor.getString(5));
                // contact.setNoti_typ(cursor.getString(6));
                contact.setSend_to(cursor.getString(7));
                // contact.setSender(cursor.getString(8));
                //  contact.setUserName(cursor.getString(9));
                list.add(contact);
            } while (cursor.moveToNext());
            // Adding contact to list
        }
        // db.close();
        // return contact list
        return list;
    }

    public long addOfflineBookingDetails(BookingDetails bookingDetails, String type) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_OFF_BOOKING_ID, bookingDetails.getBookingId()); // Name
        values.put(KEY_OFF_BOOKING_DID, bookingDetails.getDs_id()); // Name
        //values.put(KEY_USERNAME, username); // Name
        values.put(KEY_OFF_BOOKING_DS_NM, bookingDetails.getDs_name()); // Name
        values.put(KEY_OFF_BOOKING_YATRI_NAME, bookingDetails.getYatriNm()); // Email

        //  values.put(KEY_USER_TOKEN, userToken);

        values.put(KEY_OFF_BOOKING_YATRIADDRESS, bookingDetails.getYatriAddress()); // Name
        values.put(KEY_OFF_BOOKING_Y_MOB_NO, bookingDetails.getYatriMobile()); // Name
        //values.put(KEY_USERNAME, username); // Name
        values.put(KEY_OFF_BOOKING_ROOM_TYPE, bookingDetails.getRoomTyp()); // Name
        values.put(KEY_OFF_BOOKING_CHECK_IN, bookingDetails.getCheckinDt()); // Email
        values.put(KEY_OFF_BOOKING_CHECKOUT, bookingDetails.getCheckoutDt()); // Name
        values.put(KEY_OFF_BOOKING_NO_ROOM, bookingDetails.getNoOfRooms()); // Name
        //values.put(KEY_USERNAME, username); // Name
        values.put(KEY_OFF_BOOKING_GUESTS, bookingDetails.getPerson()); // Name
        values.put(KEY_OFF_BOOKING_CONTIBUTION, bookingDetails.getContribution()); // Email
        values.put(KEY_OFF_BOOKING_SUBTOTAL, bookingDetails.getSuggestion()); // Name //suggestion
        values.put(KEY_OFF_BOOKING_NEFT_NO, bookingDetails.getReply()); // Name    //reply
        //values.put(KEY_USERNAME, username); // Name
        values.put(KEY_OFF_BOOKING_TRANFER_DATE, bookingDetails.getTranferDt()); // Name
        values.put(KEY_OFF_BOOKING_TRANFER_AMT, bookingDetails.getTranferAmt()); // Email
        values.put(KEY_OFF_BOOKING_ORDER_STATUS, bookingDetails.getBookingStatus()); // Name
        values.put(KEY_OFF_BOOKING_EXPECTED_DATETIME, bookingDetails.getExpectedCheckinTime()); // Name
        values.put(KEY_OFF_BOOKING_TYPE, type); // Name

        //values.put(KEY_USERNAME, username); // Name
        values.put(KEY_OFF_NIGHTS, bookingDetails.getNights());
        values.put(KEY_OFF_ROOM_ID, bookingDetails.getRoom_id()); // Name
        values.put(KEY_OFF_EXTRA_FIELD1, bookingDetails.getBookingConfirmedBy());
        Log.d("KAJKSJKKS", bookingDetails.getBookingRoomsType() + "===");
        values.put(KEY_OFF_EXTRA_FIELD2, bookingDetails.getBookingRoomsType());
        values.put(KEY_OFF_EXTRA_FIELD3, bookingDetails.getBookingBookedOn());
        // Inserting Row
        long id_res = db.insert(TABLE_OFFLINE_BOOKING_DETAILS, null, values);
        // db.close(); // Closing database connection

        Log.d(TAG, "New OFFLINE Booking Details inserted into sqlite : " + id_res + "==" + type);
        return id_res;
    }

    public int deleteOfflineRoomTypes() {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        Log.d("CHECKMSGDELETEE555", " ---");
        try {
            int j = db.delete(TABLE_OFFLINE_ROOM_TYPES, null, null);
            // db.execSQL("delete from " + TABLE_BOOKING_DETAILS + " WHERE " + KEY_BOOKING_TYPE + "='" + " inquiryLatest" + "'");

            Log.d("CHECKMSGDELETEE555ROOMS", i + " ---" + j);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  // db.close();
        return i;
    }

    public long addRoomTypes(String room_id, String roomType, String roomDID) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.d("CHCECK99", room_id + "--" + username + "--" + msgcount + "-" + lastmsg);
        ContentValues values = new ContentValues();
        values.put(KEY_ROOM_TYPE_ROOM_ID, room_id); // Name
        values.put(KEY_ROOM_TYPE_NAME, roomType);// Name
        values.put(KEY_ROOM_TYPE_DID, roomDID);

        // Inserting Row
        long id_res = db.insert(TABLE_OFFLINE_ROOM_TYPES, null, values);
        // db.close(); // Closing database connection


        Log.d(TAG, "New OFFLINE room inserted into sqlite: " + id_res);
        return id_res;
    }

    public int deleteOfflineBooking(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        Log.d("CHECKMSGDELETEE555", " ---");
        try {
            i = db.delete(TABLE_OFFLINE_BOOKING_DETAILS, KEY_OFF_BOOKING_TYPE + "=? AND " + KEY_OFF_BOOKING_ID + "=?", new String[]{"offline", id});

            // db.execSQL("delete from " + TABLE_BOOKING_DETAILS + " WHERE " + KEY_BOOKING_TYPE + "='" + " inquiryLatest" + "'");

            Log.d("CHECKMSGDELETEE555", i + " ---");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  // db.close();
        return i;
    }

    public ArrayList<BookingDetails> fetchAllOfflineBookings(String type, String d_id) {
        Log.d("JGSDHGHGDGDHGH123", type + "===>" + d_id);
        ArrayList<BookingDetails> bookingsList = new ArrayList<>();
        try {


            String fetch_user_class = "SELECT " + KEY_OFF_BOOKING_ID + ", " + KEY_OFF_BOOKING_DID + ", " + KEY_OFF_BOOKING_DS_NM + ", " + KEY_OFF_BOOKING_YATRI_NAME + ", " + KEY_OFF_BOOKING_YATRIADDRESS + ", " + KEY_OFF_BOOKING_Y_MOB_NO + ", " + KEY_OFF_BOOKING_ROOM_TYPE + ", "
                    + KEY_OFF_BOOKING_CHECK_IN + ", " + KEY_OFF_BOOKING_CHECKOUT + ", " + KEY_OFF_BOOKING_NO_ROOM + ", " + KEY_OFF_BOOKING_GUESTS + ", " + KEY_OFF_BOOKING_CONTIBUTION + ", " + KEY_OFF_BOOKING_SUBTOTAL + ", " + KEY_OFF_BOOKING_NEFT_NO + ", "
                    + KEY_OFF_BOOKING_TRANFER_DATE + ", " + KEY_OFF_BOOKING_TRANFER_AMT + ", " + KEY_OFF_BOOKING_ORDER_STATUS + ", " + KEY_OFF_BOOKING_EXPECTED_DATETIME + ", " + KEY_OFF_ROOM_ID + ", " + KEY_OFF_NIGHTS + ", " + KEY_OFF_EXTRA_FIELD3 + " FROM " + TABLE_OFFLINE_BOOKING_DETAILS + " WHERE " + KEY_OFF_BOOKING_DID + "='" + d_id + "'" + " AND " + KEY_OFF_BOOKING_TYPE + "='" + type + "'";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(fetch_user_class, null);
            Log.d("JGSDHGHGDGDHGH123", type + "===>" + d_id + "===>" + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    Log.d("CURSORCOUNT88RECORDS", cursor.getString(0) + " - -" + cursor.getString(1) + " - -" + cursor.getString(7));
                    BookingDetails bookingDetails = new BookingDetails();
                    bookingDetails.setBookingId(cursor.getString(0));

                    bookingDetails.setDs_id(cursor.getString(1));
                    bookingDetails.setDs_name(cursor.getString(2));
                    //contact.setMessageType("text");
                    bookingDetails.setYatriNm(cursor.getString(3));

                    bookingDetails.setYatriAddress(cursor.getString(4));
                    bookingDetails.setYatriMobile(cursor.getString(5));
                    bookingDetails.setRoomTyp(cursor.getString(6));
                    //contact.setMessageType("text");
                    bookingDetails.setCheckinDt(cursor.getString(7));

                    bookingDetails.setCheckoutDt(cursor.getString(8));
                    bookingDetails.setNoOfRooms(cursor.getString(9));
                    bookingDetails.setPerson(cursor.getString(10));
                    bookingDetails.setContribution(cursor.getString(11));
                    bookingDetails.setBookingTotal(cursor.getString(12));
                    //contact.setMessageType("text");
                    bookingDetails.setNeftNo(cursor.getString(13));
                    bookingDetails.setTranferDt(cursor.getString(14));
                    bookingDetails.setTranferAmt(cursor.getString(15));
                    bookingDetails.setBookingStatus(cursor.getString(16));
                    //contact.setMessageType("text");
                    Log.d("ASDJHJHJJHAJH", cursor.getString(18) + "===");
                    bookingDetails.setExpectedCheckinTime(cursor.getString(17));
                    bookingDetails.setRoom_id(cursor.getString(18));
                    bookingDetails.setNights(cursor.getString(19));
                    if (cursor.getString(20) != null)
                        bookingDetails.setBookingBookedOn(cursor.getString(20));
               /* if (type.equals("active")) {
                    String dt = bookingDetails.getCheckinDt().trim();
                    String arr[] = dt.trim().split("\\s+");
                    if (arr[0].equals(ConvertGMTtoIST.getCurrentDateBookingList())) {
                        String dtCheckout = bookingDetails.getCheckoutDt().trim();

                        String arrCheckout[] = dtCheckout.trim().split("\\s+");
                        if (ConvertGMTtoIST.getDateActiveCheckout(arrCheckout[0]).compareTo(ConvertGMTtoIST.getCurrentDateActive()) > 0) {
                            bookingsList.add(bookingDetails);
                        }
                    }
                } else {*/
                    bookingsList.add(bookingDetails);
                    //}


                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookingsList;
    }

    public String fetchOfflineRoomTotal(String roomId, String checkIn, String did) {
        Log.d("AKJKHJHJHHJDAJ", roomId + "===>" + checkIn + "===5");
        String data = "0";
        Log.d("AKJKHJHJHHJDAJ", roomId + "==>" + checkIn + "==>" + did + "==>" + "333");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            //if (msgid != null) {
            cursor = db.query(TABLE_INVENTORY, new String[]{KEY_INEVNTORY_TOTAL}, KEY_INEVNTORY_ROOM_ID + "=? AND " + KEY_INEVNTORY_CHECKIN + "=? AND " + KEY_INEVNTORY_EXTRAFIELD + "=?", new String[]{roomId, checkIn, did}, null, null, null);
            // }
            if (cursor != null && cursor.moveToFirst()) {
                Log.d("CEHCKHGDJGHFDJK", cursor.getCount() + "---" + cursor.getString(0));
                Log.d("AKJKHJHJHHJDAJ", cursor.getCount() + "---" + cursor.getString(0) + "<=====>");
                data = cursor.getString(0);
            }

        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "");
            e.printStackTrace();
        }


        if (cursor != null)
            cursor.close();
        return data;
    }

    public String tableToString() {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("", "tableToString called");
        String tableString = String.format("Table %s:\n", TABLE_INVENTORY);
        Cursor allRows = db.rawQuery("SELECT * FROM " + TABLE_INVENTORY, null);
        tableString += cursorToString(allRows);
        return tableString;
    }

    public String cursorToString(Cursor cursor) {
        String cursorString = "";
        if (cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            for (String name : columnNames)
                cursorString += String.format("%s ][ ", name);
            cursorString += "\n";
            do {
                for (String name : columnNames) {
                    cursorString += String.format("%s ][ ",
                            cursor.getString(cursor.getColumnIndex(name)));
                }
                cursorString += "\n";
            } while (cursor.moveToNext());
        }
        return cursorString;
    }
    public int deleteOfflineInventoryExist( String roomId, String checkIn, String did, int countAdd) {

        int i = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db1 = this.getWritableDatabase();
        Cursor cursor = null;
        Log.d("CHECKTHREFLOW", "in getGrpUnseeenn -->" + roomId + "---" + checkIn + "-->" + did);

        Log.d("ADHKHJKHJHJADH", roomId + "---" + checkIn + "-->" + did + "=====8");
        //   Log.d("CHECK00133234", roomId + "---" + member);
        try {
            cursor = db.query(TABLE_INVENTORY, new String[]{KEY_INEVNTORY_TOTAL}, KEY_INEVNTORY_ROOM_ID + "=? AND " + KEY_INEVNTORY_CHECKIN + "=? AND " + KEY_INEVNTORY_EXTRAFIELD + "=?", new String[]{roomId, checkIn, did}, null, null, null);
            Log.d("ADHKHJKHJHJADH", cursor.getCount() + "====-");
            // Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list

            Log.d("JKAJDKAKJ", cursor.getCount() + "");
        } catch (Exception e) {
            Log.d("CHECKEXCEPTIONFETCHROOM", "hjds");
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst()) {


            if (cursor.getCount() > 0) {
                int count = Integer.parseInt(cursor.getString(0));
                int newCount=0;
                if(countAdd<count)
                 newCount = count - countAdd;

                ContentValues CV = new ContentValues();


                CV.put(KEY_INEVNTORY_TOTAL, newCount + "");
                int rows = db1.update(TABLE_INVENTORY, CV, KEY_INEVNTORY_ROOM_ID + "=? AND " + KEY_INEVNTORY_CHECKIN + "=? AND " + KEY_INEVNTORY_EXTRAFIELD + "=?", new String[]{roomId, checkIn, did});
                i = rows;
                Log.d("ADHKHJKHJHJADH", count + "===" + newCount + "==" + countAdd + "===" + rows + "==>2");

            }

        }
        return i;
    }

}