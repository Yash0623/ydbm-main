package com.ydbm.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ydbm.App;
import com.ydbm.R;
import com.ydbm.adapters.ViewPagerTabAdapter;
import com.ydbm.dialogs.VersionUpgradeDialog;
import com.ydbm.fragments.AllInquiries;
import com.ydbm.fragments.LatestFragment;
import com.ydbm.models.BookingDetails;
import com.ydbm.models.InquiryWithType;
import com.ydbm.models.RoomType;
import com.ydbm.phoneverification.Authentication;
import com.ydbm.session.SessionManager;
import com.ydbm.ui.Database;
import com.ydbm.utils.AppConstants;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainScreen extends AppCompatActivity {
    ArrayList<BookingDetails> bookings = new ArrayList<>();
    ArrayList<BookingDetails> bookingsRecent = new ArrayList<>();
    SessionManager sessionManager;
    boolean isInternet = false;
    Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    private SQLiteHandler sqLiteHandler;
    private ViewPager mViewPager;
    ArrayList<Fragment> mFragments;
    BottomNavigationView bottomNavigationView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private String d_id;
    int from = 0;
    String isFrom = "user";


    public static final int REQUEST_CODE_PERMISSIONS = 2;
    private static final String TAG = MainScreen.class.getSimpleName();

    private LocationManager mLocationManager;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    FirebaseUser currentUser;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;
    FirebaseAuth firebaseAuth;

    String enqiry_id = null;
    String lastSeen = "";
    private Boolean mRequestingLocationUpdates = false;
    private String fcm_token;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Bundle bundle = getIntent().getExtras();

        sessionManager = new SessionManager(MainScreen.this);
        if (bundle != null) {
            from = bundle.getInt("from");
            isFrom = bundle.getString("fromuser", "user");
            enqiry_id = bundle.getString("enq_id");
            if (from == 1) {
                Log.d("DJhjhJHDASjh34", bundle.getString("dsid") + "===>" + bundle.getString("dsnm"));
                sessionManager.setDharamshala_id(bundle.getString("dsid"), bundle.getString("dsnm"), "4");
            }
        }


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mRequestingLocationUpdates = false;
        mSettingsClient = LocationServices.getSettingsClient(this);

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();


        forceUpdate();
        sqLiteHandler = new SQLiteHandler(MainScreen.this);
        // initToolbar();
        initViews();
        fcm_token = sessionManager.getUserDetailsOb().getFcm_id();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("BASHGHAHAGh", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("DAHHJHJAHJ1232", "===" + token);
                        if (token != null) {
                            fcm_token = token;
                        }
                        if (mAuth != null && mAuth.getUid() != null) {
                            if (!isFrom.equalsIgnoreCase("admin")) {
                                callMatchUserAPI(currentUser);
                            }
                        }
                        // Log and toast
                        //  String msg = getString(R.string.msg_token_fmt, token);
                        //   Log.d(TAG, msg);
                        // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //if (from == 7 && sessionManager.getRole().equals("1")) {
        getMenuInflater().inflate(R.menu.bookinglist, menu);
        MenuItem itemUsrLst = menu.findItem(R.id.usersList);
        MenuItem datepicker = menu.findItem(R.id.action_pdf);
        MenuItem filter = menu.findItem(R.id.action_filter);
        MenuItem menuItem = menu.findItem(R.id.action_inventory);

        filter.setVisible(false);
        if (sessionManager.getRole().equals("1")) {
            itemUsrLst.setVisible(true);
            datepicker.setVisible(false);
        } else {
            itemUsrLst.setVisible(false);
            datepicker.setVisible(false);

        }
        //   }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.usersList) {
            Intent intent = new Intent(MainScreen.this, RegisteredUsersList.class);
            startActivity(intent);
        } else if (id == R.id.profilescreen) {
            if (!isFrom.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(MainScreen.this, ProfileScreenActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainScreen.this, AdminActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.action_inventory) {
            Intent intent = new Intent(MainScreen.this, InventoryActivity.class);
            startActivity(intent);
        }else if (id == R.id.action_rewards) {
            Intent intent = new Intent(MainScreen.this, RewardListActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {

        initViewPager();
        initTabLayout();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        d_id = sessionManager.getUserDetailsOb().getD_id();
        // Log.d("TEST13232", sessionManager.getFCMTOKEN());
        //  storage = FirebaseStorage.getInstance();
        //    storageReference = storage.getReference();
        if (mAuth.getUid() != null) {

            Database.NODE_USERS_PATH.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        long time = currentUser.getMetadata().getCreationTimestamp();
                        Date date = new Date(time);
                        Format format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                        String timestmp = format.format(date);
                        Database.NODE_USERS_PATH.child(mAuth.getUid()).child("creationTime").setValue(timestmp);
                        lastSeen = ConvertGMTtoIST.getCurrentDateTimeLastSeen();
                        Database.NODE_USERS_PATH.child(mAuth.getUid()).child("lastSeen").setValue(ConvertGMTtoIST.getCurrentDateTimeLastSeen()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        //if (d_id != null) {
        isInternet = NetworkUtil.checkInternetConnection(MainScreen.this);
        if (isInternet) {
            fetchInquiryListAPICall(d_id);
        } else {

            Toast.makeText(MainScreen.this, "Please Connect To Internet!!", Toast.LENGTH_SHORT).show();
        }
      /*  } else {
            Toast.makeText(this, "dharamshala id null !!", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), "Please Connect To Internet!", Snackbar.LENGTH_LONG);
        }*/

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_booking:
                        // if (bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId()).getItemId() != menuItem.getItemId()) {
                        //   Log.d("KDJKDASHDJSJD", "1");
                        Bundle params = new Bundle();

                        // params.putString("dharamshala_name", sessionManager.getDharamshalanm() + "");
                        params.putString("user_detail", sessionManager.getUserDetailsOb().getUsername() + " at " + ConvertGMTtoIST.getCurrentDateTimeLastSeen() + "");
                        mFirebaseAnalytics.logEvent("show_booking_onclick", params);

                        Intent intent = new Intent(MainScreen.this, BookingListingActivity.class);
                        intent.putExtra("fromuser", isFrom);
                        startActivity(intent);

                        // }
                        return true;
                    case R.id.action_inquiry:
                        //if (bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId()).getItemId() != menuItem.getItemId()) {
                        Log.d("KDJKDASHDJSJD", "1");

                        Bundle params1 = new Bundle();
                        params1.putString("user_detail", sessionManager.getUserDetailsOb().getUsername() + " at " + ConvertGMTtoIST.getCurrentDateTimeLastSeen() + "");
                        mFirebaseAnalytics.logEvent("show_inquiry_onclick", params1);

                        Intent intent1 = new Intent(MainScreen.this, MainScreen.class);
                        intent1.putExtra("fromuser", isFrom);
                        startActivity(intent1);
                        //  }
                        return true;
                    case R.id.action_inventory:
                        //if (bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId()).getItemId() != menuItem.getItemId()) {
                        Log.d("KDJKDASHDJSJD", "1");

                        Bundle params2 = new Bundle();
                        params2.putString("user_detail", sessionManager.getUserDetailsOb().getUsername() + " at " + ConvertGMTtoIST.getCurrentDateTimeLastSeen() + "");
                        mFirebaseAnalytics.logEvent("show_inventory_onclick", params2);

                        Intent intent4 = new Intent(MainScreen.this, InventoryActivity.class);
                        intent4.putExtra("fromuser", isFrom);
                        startActivity(intent4);
                        //  }
                        return true;

                }

                return false;
            }
        };
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initViewPager() {
        mViewPager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.navigationBottom);
        List<String> tabNames = new ArrayList<String>();

        tabNames.add("Latest");
        tabNames.add("Recent");

        // tabNames.add("Tomorrow");
        // tabNames.add("Active");
        ViewPagerTabAdapter viewPagerTabAdapter = new ViewPagerTabAdapter(getSupportFragmentManager(), getFragments(), tabNames);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(viewPagerTabAdapter);
        //  viewpagerListener();

    }


    private List<Fragment> getFragments() {
        mFragments = new ArrayList<Fragment>();
        mFragments.add(AllInquiries.newInstance());
        mFragments.add(LatestFragment.newInstance());
        return mFragments;
    }

    private void initTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();

    }

    private void fetchInquiryListAPICall(String dharamshala_id) {
        showProgressDialog("Fetching Inquiry Details.. Please Wait!");
        RequestQueue queue = Volley.newRequestQueue(MainScreen.this);
        //Log.d("CHECKRESPONS", "https://yatradham.org/demosite/restapi.php?apiname=addgroup&number=" + finalMembers + "&group_id=" + mGroupId + "&is_mobile=APP");
        // Log.d("TEGSDHGHDSHD", AppConstants.BASE_API_URL + "enquirylist" + "&dsid=" + dharamshala_id + "&is_mobile=yes");
        //StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "enquirylist" + "&dsid=" + dharamshala_id + "&is_mobile=APP", new Response.Listener<String>() {
        //   Log.d("TEGSDHGHDSHD", AppConstants.BASE_API_URL + "enquirylist" + "&dsid=" + dharamshala_id + "&is_mobile=APP");
        //   StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "enquirylist" + "&dsid=" + dharamshala_id + "&is_mobile=APP", new Response.Listener<String>() {
        Log.d("TEGSDHGHDSHD", AppConstants.BASE_API_URL + "enquirylist" + "&dsid=" + dharamshala_id + "&is_mobile=yes");
        StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "enquirylist" + "&dsid=" + dharamshala_id + "&is_mobile=yes", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                //  Toast.makeText(CreateUserActivity.this,response,Toast.LENGTH_LONG).show();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Log.d("CHECKRESPONSE", response.toString());
                    if (jsonObject.has("status")) {
                        if (jsonObject.getInt("status") == 1) {
                            bookings.clear();
                            sqLiteHandler.deleteInquiries();
                            //sqLiteHandler.deleteBookings();
                            String ds_Nm = jsonObject.getString("d_name");
                            String dsid = jsonObject.getString("d_id");
                            int is_24 = jsonObject.getInt("is_24");
                            JSONArray jsonArrayInfo = jsonObject.getJSONArray("info");
                            JSONArray jsonArrayRecentList = jsonObject.getJSONArray("recent_list");

                            String rmTypes = jsonObject.getString("roomtypes");
                            ArrayList<RoomType> rooms = new ArrayList<>();
                            String arry[] = rmTypes.split(",");
                            //  List<String> stringList = new ArrayList<String>(Arrays.asList(arry));
                            for (int i = 0; i < arry.length; i++) {
                                rooms.add(new RoomType(arry[i], false));
                            }


                            ArrayList<String> dates = new ArrayList<>();
                            //   int count = jsonArrayInfo.length();

                            for (int i = 0; i < jsonArrayInfo.length(); i++) {


                                BookingDetails bookingDetails = new BookingDetails();

                                JSONObject object = jsonArrayInfo.getJSONObject(i);
                                bookingDetails.setBookingId(object.getString("bookingid"));
                                bookingDetails.setDs_name(ds_Nm);
                                bookingDetails.setDs_id(dsid);
                                bookingDetails.setIs_24Hours(String.valueOf(is_24));
                                bookingDetails.setYatriNm(object.getString("yatriname"));
                                bookingDetails.setRoomTyp(object.getString("roomtype"));
                                bookingDetails.setCheckinDt(object.getString("checkin"));
                                bookingDetails.setCheckoutDt(object.getString("checkout"));
                                bookingDetails.setNoOfRooms(object.getString("noofroom"));
                                bookingDetails.setPerson(object.getString("guests"));
                                bookingDetails.setNights(object.getString("nights"));
                                //bookingDetails.setContribution(object.getString("contribution"));
                                //   bookingDetails.setNeftNo(object.getString("neftno"));
                                // bookingDetails.setTranferDt(object.getString("transferdate"));
                                // bookingDetails.setTranferAmt(object.getString("transfer_amount"));
                                bookingDetails.setYatriAddress(object.getString("address"));
                                bookingDetails.setYatriMobile(object.getString("mobile_no"));
                                bookingDetails.setArrayListRooms(rooms);
                                // bookingDetails.setBookingTotal(object.getString("sub_total"));
                                //  bookingDetails.setBookingStatus(object.getString("order_status"));
                                bookingDetails.setExpectedCheckinTime(object.getString("expected_checkin_datetime"));

                                bookingDetails.setExtra_mattress_status(object.optString("extra_mattress_status"));
                                bookingDetails.setExtra_mattress_label(object.optString("extra_mattress_label"));
                                bookingDetails.setExtra_mattress_price(object.optString("extra_mattress_price"));
                                bookingDetails.setExtra_mattress_convenience(object.optString("extra_mattress_convenience"));
                                bookingDetails.setTotal_mattress_price(object.optString("total_mattress_price"));
                                bookingDetails.setNo_of_mattress(object.optString("no_of_mattress"));

                                if (enqiry_id != null) {
                                    if (bookingDetails.getBookingId().equals(enqiry_id)) {
                                        bookingDetails.setNewArrived(true);
                                    }
                                }
                                bookings.add(bookingDetails);


                            }

//                            AllInquiries.newInstance().onNotifyBookingList(bookings);
                            Collections.sort(bookings, new Comparator<BookingDetails>() {

                                public int compare(BookingDetails o1, BookingDetails o2) {
                                    // compare two instance of `Score` and return `int` as result.
                                    return o2.getBookingId().compareTo(o1.getBookingId());
                                }
                            });
                            // new BookingListingActivity.AsyncTaskDemo().execute(bookings);

                            //  new GetBookingList().getBookingList(bookings);
                            InquiryWithType bookingListWithType = new InquiryWithType();
                            bookingListWithType.setModels(bookings);
                            Log.d("ADKDKJJKHADJKHDAj", bookings.size() + "==" + "test");
                            EventBus.getDefault().post(bookingListWithType);

                            Log.d("ADJKJKKKAHDKA", bookings.size() + "==" + "==");

                            for (int i = 0; i < jsonArrayRecentList.length(); i++) {

                                BookingDetails bookingDetails = new BookingDetails();

                                JSONObject object = jsonArrayRecentList.getJSONObject(i);
                                bookingDetails.setBookingId(object.getString("bookingid"));
                                bookingDetails.setDs_name(ds_Nm);
                                bookingDetails.setDs_id(dsid);
                                bookingDetails.setIs_24Hours(String.valueOf(is_24));
                                bookingDetails.setYatriNm(object.getString("yatriname"));
                                bookingDetails.setRoomTyp(object.getString("roomtype"));
                                bookingDetails.setCheckinDt(object.getString("checkin"));
                                bookingDetails.setCheckoutDt(object.getString("checkout"));
                                bookingDetails.setNoOfRooms(object.getString("noofroom"));
                                bookingDetails.setPerson(object.getString("guests"));
                                bookingDetails.setNights(object.getString("nights"));

                                bookingDetails.setYatriAddress(object.getString("address"));
                                bookingDetails.setYatriMobile(object.getString("mobile_no"));
                                bookingDetails.setReply(object.getString("enquiry_reply"));

                                // bookingDetails.setSuggestion(object.getString("enquiry_suggession"));

                                String confirmed = object.getString("enquiry_suggession");
                                String arr[] = confirmed.split("#");
                                Log.d("DAHAJHJHJA1323", arr[0] + "==1");
                                if (arr.length > 1) {
                                    Log.d("DAHAJHJHJA1323", arr[1] + "==2");
                                    String otherArry[] = arr[1].split("\\|");
                                    Log.d("DAHAJHJHJA1323", otherArry[0] + "==0");
                                    if (otherArry[0] != null && otherArry[0].length() > 0) {
                                        Log.d("DAHAJHJHJA1323", otherArry[0] + "==3");
                                        bookingDetails.setBookingRoomsType(otherArry[0]);
                                        //bookingDetails.setExtraInstruction(otherArry);
                                    }
                                }
                                bookingDetails.setSuggestion(arr[0]);
                                bookingDetails.setBookingConfirmedBy(object.getString("reply_by"));
                                bookingDetails.setArrayListRooms(rooms);
                                // bookingDetails.setBookingTotal(object.getString("sub_total"));
                                //  bookingDetails.setBookingStatus(object.getString("order_status"));
                                bookingDetails.setExpectedCheckinTime(object.getString("expected_checkin_datetime"));
                                long id_res = sqLiteHandler.addBookingDetails(bookingDetails, "inquiryLatest");

                                bookingsRecent.add(bookingDetails);

                            }

                            EventBus.getDefault().post("latestdata");
                            if (isFrom.equals("admin")) {
                                Log.d("JGAGA1GH", "==" + ds_Nm + "==");
                                // getSupportActionBar().setTitle(ds_Nm + "");
                                TextView textview = new TextView(MainScreen.this);

                                ViewGroup.LayoutParams layoutparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                textview.setLayoutParams(layoutparams);

                                textview.setText(ds_Nm + "");

                                textview.setTextColor(Color.WHITE);

                                textview.setGravity(Gravity.CENTER);

                                textview.setTextSize(14);

                                getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

                                getSupportActionBar().setCustomView(textview);
                                sessionManager.setDharamshala_id(dsid, ds_Nm, "5");
                            }
                        } else {
                            if (jsonObject.has("d_name")) {

                                String ds_Nm = jsonObject.getString("d_name");
                                TextView textview = new TextView(MainScreen.this);

                                ViewGroup.LayoutParams layoutparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                textview.setLayoutParams(layoutparams);

                                textview.setText(ds_Nm + "");

                                textview.setTextColor(Color.WHITE);

                                textview.setGravity(Gravity.CENTER);

                                textview.setTextSize(14);

                                getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

                                getSupportActionBar().setCustomView(textview);

                                String dsid = jsonObject.getString("d_id");
                                //sessionManager.setDharamshala_id(dsid, ds_Nm,"6");

                            }
                            Toast.makeText(MainScreen.this, "No data Found!!", Toast.LENGTH_LONG).show();
                            bookings.clear();
                            InquiryWithType bookingListWithType = new InquiryWithType();
                            bookingListWithType.setModels(bookings);
                            EventBus.getDefault().post(bookingListWithType);
                        }
                    } else {
                        Toast.makeText(MainScreen.this, "No data Found!!", Toast.LENGTH_LONG).show();
                    }

                    // bookinListAdapter = new BookinListAdapter(bookings, BookingListingActivity.this);
                    //recyclerView.setAdapter(bookinListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Toast.makeText(MainScreen.this, error.toString(), Toast.LENGTH_LONG).show();
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

    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(MainScreen.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(msg);
        }
        if (mProgressDialog != null && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (!isFinishing() && mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("AJDGHADGDGH", "5");
        try {
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("AJDGHADGDGH", "6");
        EventBus.getDefault().unregister(this);
    }

    private void callMatchUserAPI(final FirebaseUser firebaseUser) {
        RequestQueue queue = Volley.newRequestQueue(MainScreen.this);
        String username = sessionManager.getUserDetailsOb().getUsername();

        Log.d("CHECKPARAMS123", AppConstants.BASE_API_URL + "matchuser&mobilenumber=" + username + "&fid=" + firebaseUser.getUid() + "&fcm_id=" + fcm_token + "&is_mobile=APP");
        StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "matchuser&mobilenumber=" + username + "&fid=" + firebaseUser.getUid() + "&fcm_id=" + fcm_token + "&is_mobile=APP", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;

                try {
                    Log.d("CHECKRESPONSE123", response.toString());
                    jsonObject = new JSONObject(response);


                    if (jsonObject.has("status")) {


                        if (jsonObject.getBoolean("status")) {
                            //       callAddInfoAPI(user, firebaseUser, ds_id, ds_name);
                            if (jsonObject.has("info")) {
                                JSONObject object = jsonObject.getJSONObject("info");

                                String nm = object.getString("name");
                                String email1 = object.getString("email");
                                String designation1 = object.getString("designation");
                                sessionManager.editUserDetails(nm, email1, designation1);


                            }
                            if (jsonObject.has("is_multiple")) {
                                if (jsonObject.getInt("is_multiple") == 1) {
                                    sessionManager.setIsMultipleAccount(true);
                                    JSONArray jsonArraydetails = jsonObject.getJSONArray("ds_details");
                                    Log.d("SDKDHASHDJHDJ", jsonArraydetails.toString());
                                    sessionManager.setDharamshala_details(jsonArraydetails.toString());

                                    List<String> dsidsListarry = new ArrayList<>();
                                    List<String> dsiNmsListarry = new ArrayList<>();
                                    for (int i = 0; i < jsonArraydetails.length(); i++) {
                                        JSONObject objectDetails = jsonArraydetails.getJSONObject(i);
                                        dsidsListarry.add(i, objectDetails.getString("ds_id"));
                                        dsiNmsListarry.add(i, objectDetails.getString("ds_name"));

                                        // Log.d("KJDSKHDHJSHJDH12", objectDetails.getString("ds_id") + "===>>>" + objectDetails.getString("ds_name"));
                                    }

                                    if (dsidsListarry.contains(sessionManager.getDharamshalaId())) {
                                        Log.d("LAJKDKJKDJ", 1 + "==");
                                    } else {
                                        Log.d("LAJKDKJKDJ", 2 + "==");
                                        sessionManager.setDharamshala_id(dsidsListarry.get(0), dsiNmsListarry.get(0), "7");
                                    }


                                } else {
                                  /*  String ds_id = jsonObject.getString("ds_id");
                                    String ds_name = jsonObject.getString("ds_name");
                                    sessionManager.setDharamshala_id(ds_id,ds_name);

*/
                                    sessionManager.setIsMultipleAccount(false);
                                }


                            } else {
                                sessionManager.setIsMultipleAccount(false);
                            }
                        } else {
                            SharedPreferences.Editor editor =
                                    getSharedPreferences("YDBookingMgr", Context.MODE_PRIVATE).edit();
                            editor.clear();
                            boolean result = editor.commit();
                            if (result) {
                                Toast.makeText(MainScreen.this, "User not found!! please contact yatradham.org", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainScreen.this, Authentication.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                    } else {
                        // hideProgressDialog();

                    }

                } catch (JSONException e) {
                    hideProgressDialog();
                    // hideProgressDialog();
                    e.printStackTrace();
                }


            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("MainScreen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Screen View")
                .setCustomDimension(0, sessionManager.getUserDetailsOb().getUsername())
                .setCustomDimension(1, sessionManager.getUserDetailsOb().getName())
                .build());
        //toolbar.setTitle(sessionManager.getDharamshalanm());
        if (!isFrom.equalsIgnoreCase("admin")) {
            mRequestingLocationUpdates = true;
            if (mRequestingLocationUpdates && checkPermissions()) {
                startLocationUpdates();
            } else if (!checkPermissions()) {
                requestPermissions();
            }
        }


    }


    public void forceUpdate() {
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        Log.d("JSHJHSJHJHSJHSJSHHSD", currentVersion + "==");
        new ForceUpdateAsync(currentVersion, MainScreen.this).execute();
    }

    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {

        private String latestVersion;
        private String currentVersion;
        private Context context;

        public ForceUpdateAsync(String currentVersion, Context context) {
            this.currentVersion = currentVersion;
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
                Log.e("latestversion", "---" + latestVersion);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (latestVersion != null) {

                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    // Toast.makeText(context,"update is available.",Toast.LENGTH_LONG).show();

                    if (!((Activity) context).isFinishing()) {
                        showForceUpdateDialog();
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }

        public void showForceUpdateDialog() {
            VersionUpgradeDialog update = new VersionUpgradeDialog(MainScreen.this);
            update.setCancelable(false);
            //change for release
            update.setCanceledOnTouchOutside(true);
            update.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getValueFromEventBus(String data) {
        Log.d("JDSKHJHJDS123", "122335" + "==" + data);
        if (data.equals("checkdata")) {
            Log.d("JDSKHJHJDS123", "12233");
            if (d_id != null) {
                isInternet = NetworkUtil.checkInternetConnection(MainScreen.this);
                if (isInternet) {
                    fetchInquiryListAPICall(d_id);
                } else {

                    Toast.makeText(MainScreen.this, "Please Connect To Internet!!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "dharamshala not found !!", Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(android.R.id.content), "Please Connect To Internet!", Snackbar.LENGTH_LONG);
            }
        }
        if (data.equals("changeName")) {
            //  toolbar.setTitle(sessionManager.getDharamshalanm());
        }
    }

    public boolean checkLocationPermission1() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                String lctn = getLocationText(mCurrentLocation);
                String userId = firebaseAuth.getUid();


                Log.d("HSDJHHSDJSJHHJHD", lctn + "==" + userId);
                lastSeen = ConvertGMTtoIST.getCurrentDateTimeLastSeen();

                callLocationAPI(lctn, userId);

                // mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                //  updateLocationUI();
            }
        };
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission

                        if (ContextCompat.checkSelfPermission(MainScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        // updateUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainScreen.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.CANCELED:
                                Toast.makeText(MainScreen.this, "The location settings on the device are not\n" +
                                        "adequate to run this sample. Fix in Settings.", Toast.LENGTH_SHORT).show();
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(MainScreen.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        //   updateUI();
                    }
                });
    }


    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                        //   setButtonsEnabledState();
                    }
                });
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainScreen.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainScreen.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public static String getLocationText(Location location) {
        return location == null ? "Unknown location" :
                location.getLatitude() + "," + location.getLongitude();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                "Location permission is needed for core functionality!",
                                Snackbar.LENGTH_LONG)
                                .show();
                        //  updateUI();
                        break;
                }

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        // Log.d(tag, " onPause()");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("KSJHJHJSHHJSSHD", "1");
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                Log.d("KSJHJHJSHHJSSHD", "2");
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }

    }

    private void callLocationAPI(String location, String userId) {
        RequestQueue queue = Volley.newRequestQueue(MainScreen.this);

        Log.d("CHECKPARAMS123", AppConstants.BASE_API_URL + "managerlocation&userid=" + userId + "&location=" + location + "&last_seen=" + lastSeen + "&is_mobile=APP");
        StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "managerlocation&userid=" + userId + "&location=" + location + "&last_seen=" + lastSeen + "&is_mobile=APP", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("APILOcation", "-->" + response);
                stopLocationUpdates();
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopLocationUpdates();
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
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }




}

