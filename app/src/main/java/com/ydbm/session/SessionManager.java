package com.ydbm.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ydbm.models.User;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SessionManager {

    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "YDBookingMgr";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_UID = "uid";
    public static final String KEY_UID1 = "uid1";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_BACKUP_URL = "url";
    public static final String KEY_MOBILE = "contact_no";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_TOKEN = "user_token";
    public static final String KEY_FCM_TOKEN = "fcm_token";
    public static final String KEY_USRENAME = "username";
    public static final String KEY_DES = "designation";

    public static final String KEY_USER_ROLE = "userrole";

    public static final String KEY_DHARAMSHALA_TIMEZONE = "timezon";

    public static final String KEY_DHARAMSHALA_TIME = "timezonselected";


    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(User user) {
        // Storing login value as TRUE


        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_UID, user.getUid());
        // Storing name in pref
        editor.putString(KEY_NAME, user.getName());
        // Storing email in pref
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PICTURE, user.getProfilePicUrl());
        editor.putString(KEY_DES, user.getDesignation());
        editor.putString(KEY_MOBILE, user.getUsername());
        editor.putString(KEY_USRENAME, user.getUsername());
        editor.putString(KEY_USER_TOKEN, user.getFcm_id());
        editor.putString(KEY_USER_ROLE, user.getUsertype());

        Log.d("ANJJJKJASHJKAS", "==" + "9==" + user.getD_id());

        // commit changes
        editor.commit();
    }

    public void editUserDetails(String name, String email, String designation) {
        editor.putString(KEY_NAME, name);
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_DES, designation);
        editor.commit();
    }

    public void setDharamshala_id(String id, String Nm,String type) {
        Log.d("ANJJJKJASHJKAS", "==" + id + "===1" + Nm+"==2"+type);
        editor.putString("ds_id", id);
        editor.putString("ds_nm", Nm);

        boolean result = editor.commit();
        if (result) {
            Log.d("ANJJJKJASHJKAS", "==" + result + "===");
            Log.d("JDAHADHJAH12", result + "==");
            EventBus.getDefault().post("changeName");

        }
    }

    public void setDharamshala_details(String details) {
        editor.putString("details", details);
        editor.commit();


    }

    public String getDharamshala_details() {
        String role = pref.getString("details", "0");
        return role;
    }

    public void setDharamshalaTimezone(boolean time) {
        editor.putBoolean(KEY_DHARAMSHALA_TIMEZONE, time);
        editor.commit();


    }

    public void setFixedTiming(String did, String time) {
        Set<String> newList = new HashSet<>();
        Set<String> oldList = getFixedTiming();
        if (oldList != null) {
            for (String aSiteId : oldList) {
                String arr[] = aSiteId.split(" ");
                String did1 = arr[0];
                String time1 = arr[1];
                if (did.equals(did1)) {
                    newList.add(did + " " + time);
                } else {
                    newList.add(did1 + " " + time1);
                }
            }
        } else
            newList.add(did + " " + time);
        editor.putStringSet("fixed", newList);
        editor.commit();


    }

    public Set<String> getFixedTiming() {
        Set<String> newList = pref.getStringSet("fixed", null);

        return newList;
    }

    public String getFixedTiming(String did) {
        String time = null;
        Set<String> oldList = getFixedTiming();
        if (oldList != null) {
            for (String aSiteId : oldList) {
                String arr[] = aSiteId.split(" ");
                String did1 = arr[0];
                String time1 = arr[1];
                if (did.equals(did1)) {
                    time = time1;
                    break;
                }
            }

        }
        return time;
    }

    public String geDharamshalaTimezoneSel() {
        String time = pref.getString(KEY_DHARAMSHALA_TIME, null);
        return time;
    }

    public boolean setDharamshalaTimezoneSel(String time) {
        editor.putString(KEY_DHARAMSHALA_TIME, time);
        boolean isDTime = editor.commit();
        return isDTime;

    }

    public boolean geDharamshalaTimezone() {
        boolean time = pref.getBoolean(KEY_DHARAMSHALA_TIMEZONE, true);
        return time;
    }

    public String getDharamshalanm() {
        String role = pref.getString("ds_nm", "YD Booking Manger");
        return role;
    }

    public void createDharamshalaList(Set<String> ds_ids, Set<String> ds_nms) {
        // Storing login value as TRUE
        String list[] = ds_nms.toArray(new String[ds_nms.size()]);
        Log.d("ASJIIAUHUA677", list[0] + "--==>>");
        Log.d("JHAJHJDJ134", ds_ids.size() + "===>" + ds_nms.size());
        editor.putStringSet("dsIds", ds_ids);
        editor.putStringSet("dsNms", ds_nms);

        // commit changes
        editor.commit();
    }

    public Set<String> getDharamshalaIdList() {
        // Storing login value as TRUE


        return pref.getStringSet("dsIds", null);

    }

    public Set<String> getDharamshalaNmList() {
        // Storing login value as TRUE


        return pref.getStringSet("dsNms", null);

    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_UID, pref.getString(KEY_UID, null));
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PICTURE, pref.getString(KEY_PICTURE, null));
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));
        user.put(KEY_USER_TOKEN, pref.getString(KEY_USER_TOKEN, null));
        user.put(KEY_USRENAME, pref.getString(KEY_USRENAME, null));
        user.put(KEY_DES, pref.getString(KEY_DES, null));
        user.put(KEY_USER_ROLE, pref.getString(KEY_USER_ROLE, null));
        user.put("ds_name", pref.getString("ds_name", ""));

        // return user
        return user;
    }

    public String getRole() {
        String role = pref.getString(KEY_USER_ROLE, "0");
        return role;
    }

    public void setBackupUrl(String url) {
        pref.edit().putString(KEY_BACKUP_URL, url).commit();

    }

    public String getbackupURL() {
        String url = pref.getString(KEY_BACKUP_URL, null);
        return url;
    }

    public String getFCMTOKEN() {
        String role = pref.getString(KEY_USER_TOKEN, "");
        return role;
    }

    public String getDharamshalaId() {
        String role = pref.getString("ds_id", "0");
        return role;
    }

    public void logoutUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.signOut();
        }
        editor.clear();
        editor.commit();


    }


    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public User getUserDetailsOb() {
        User user1 = new User();
        HashMap<String, String> user = new HashMap<String, String>();
        user1.setUid(pref.getString(KEY_UID, null));

        // user name
        user1.setName(pref.getString(KEY_NAME, null));
        // user email id
        user1.setEmail(pref.getString(KEY_EMAIL, null));
        user1.setProfilePicUrl(pref.getString(KEY_PICTURE, null));
        user1.setUsername(pref.getString(KEY_MOBILE, null));
        user1.setFcm_id(pref.getString(KEY_USER_TOKEN, null));
        user1.setUsername(pref.getString(KEY_USRENAME, null));
        user1.setDesignation(pref.getString(KEY_DES, null));
        user1.setUsertype(pref.getString(KEY_USER_ROLE, null));

        user1.setD_id(pref.getString("ds_id", null));

        // return user
        return user1;
    }

    public void setIsMultipleAccount(boolean value) {
        editor.putBoolean("isMultiple", value);
        editor.commit();


    }

    public boolean getIsmultiple() {
        boolean role = pref.getBoolean("isMultiple", false);
        return role;
    }

}
