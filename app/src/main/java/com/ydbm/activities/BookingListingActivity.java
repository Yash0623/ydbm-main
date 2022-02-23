package com.ydbm.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.clearcut.ClearcutLogger;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ydbm.R;
import com.ydbm.adapters.BookinListAdapter;
import com.ydbm.fragments.ActiveFragment;
import com.ydbm.fragments.AllBookingsFragment;
import com.ydbm.fragments.TodaysBookingFragment;
import com.ydbm.fragments.TomorrowFragment;
import com.ydbm.models.ActiveBookings;
import com.ydbm.models.BookingDetails;
import com.ydbm.models.BookingListWithType;
import com.ydbm.models.BookingTodays;
import com.ydbm.models.BookingTomorrow;
import com.ydbm.models.InventoryRow;
import com.ydbm.session.SessionManager;
import com.ydbm.adapters.ViewPagerTabAdapter;
import com.ydbm.utils.AppConstants;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingListingActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private boolean isInternet;
    SessionManager sessionManager;
    ArrayList<BookingDetails> bookings = new ArrayList<>();
    ArrayList<BookingDetails> bookingsToday = new ArrayList<>();
    ArrayList<BookingDetails> bookingsTomorrow = new ArrayList<>();
    ArrayList<BookingDetails> bookingsActives = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    GetBookingList getBookingListListener;
    SQLiteHandler sqLiteHandler;
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    int from = 0;
    String isFrom = "user";
    String bookingDate;
    List<String> datesBeetween30=new ArrayList<>();
    private String enqiry_id;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_listing);
        Bundle bundle = getIntent().getExtras();
        sessionManager = new SessionManager(BookingListingActivity.this);
        if (bundle != null) {
            from = bundle.getInt("from");
            isFrom = bundle.getString("fromuser","user");
            enqiry_id = bundle.getString("enq_id");
            if (from == 1) {
                if (!isFrom.equals("admin"))
                    sessionManager.setDharamshala_id(bundle.getString("dsid"), bundle.getString("dsnm"),"13");
            }
            if (from == 16) {
                bookingDate = bundle.getString("key");
                Log.d("TEWYTYET1232", bookingDate);
            }
        }
        sqLiteHandler = new SQLiteHandler(BookingListingActivity.this);
        initToolbar();
        initViews();



    }

    public interface GetBookingList {
        void getBookingList(ArrayList<BookingDetails> bookingDetails);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {


        //initToolbar();
        initViewPager();
        initTabLayout();


        final String d_id = sessionManager.getUserDetailsOb().getD_id();
        if (d_id != null) {
            isInternet = NetworkUtil.checkInternetConnection(BookingListingActivity.this);
            if (isInternet) {
                fetchBookingListAPICall(d_id);
            } else {

                Toast.makeText(BookingListingActivity.this, "Please Connect To Internet!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "dharamshala not found !!", Toast.LENGTH_SHORT).show();
            //Snackbar.make(findViewById(android.R.id.content), "Please Connect To Internet!", Snackbar.LENGTH_LONG);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("AJDGHADGDGH", "4");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle1 = new Bundle();
        bundle1.putString("Name", sessionManager.getUserDetailsOb().getName()+"");
        bundle1.putString("user_detail","Booking List By "+ sessionManager.getUserDetailsOb().getUsername()+" at "+ConvertGMTtoIST.getCurrentDateTimeLastSeen() + "");
        bundle1.putString("Dharamshala_Name", sessionManager.getDharamshalanm()+"");
        mFirebaseAnalytics.logEvent("Screen_View", bundle1);

    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpagerBookings);
        List<String> tabNames = new ArrayList<String>();
        tabNames.add("All");
        tabNames.add("Today");
        tabNames.add("Tomorrow");
        tabNames.add("Active");
        ViewPagerTabAdapter viewPagerTabAdapter = new ViewPagerTabAdapter(getSupportFragmentManager(), getFragments(), tabNames);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(viewPagerTabAdapter);
        //  viewpagerListener();

    }


    private List<Fragment> getFragments() {
        mFragments = new ArrayList<Fragment>();
        mFragments.add(AllBookingsFragment.newInstance(getBookingListListener));
        mFragments.add(TodaysBookingFragment.newInstance(getBookingListListener));
        mFragments.add(TomorrowFragment.newInstance(getBookingListListener));
        mFragments.add(ActiveFragment.newInstance());
        return mFragments;
    }

    private void initTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBookings);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));


    }


    @Override
    protected void onStart() {
        super.onStart();
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(BookingListingActivity.this, MainScreen.class);
        intent1.putExtra("fromuser", isFrom);
        startActivity(intent1);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //if (from == 7 && sessionManager.getRole().equals("1")) {
        getMenuInflater().inflate(R.menu.bookinglist, menu);
        MenuItem itemUsrLst = menu.findItem(R.id.usersList);
        if (sessionManager.getRole().equals("1")) {
            itemUsrLst.setVisible(true);
        } else {
            itemUsrLst.setVisible(false);
        }
        //   }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.usersList) {
            Intent intent = new Intent(BookingListingActivity.this, RegisteredUsersList.class);
            startActivity(intent);
        } else if (id == R.id.profilescreen) {

            if (!isFrom.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(BookingListingActivity.this, ProfileScreenActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(BookingListingActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.action_inventory) {
            Intent intent = new Intent(BookingListingActivity.this, InventoryActivity.class);
            startActivity(intent);
        }else if (id == R.id.action_rewards) {
            Intent intent = new Intent(BookingListingActivity.this, RewardListActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void fetchBookingListAPICall(String dharamshala_id) {
        showProgressDialog("Fetching Booking Details.. Please Wait!");
        RequestQueue queue = Volley.newRequestQueue(BookingListingActivity.this);
        //Log.d("CHECKRESPONS", "https://yatradham.org/demosite/restapi.php?apiname=addgroup&number=" + finalMembers + "&group_id=" + mGroupId + "&is_mobile=APP");
        Log.d("TEGSDHGHDSHD", AppConstants.BASE_API_URL + "fetchbookinglist" + "&dsid=" + dharamshala_id + "&is_mobile=APP");
        StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "fetchbookinglist" + "&dsid=" + dharamshala_id + "&is_mobile=APP", new Response.Listener<String>() {
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
                           // sqLiteHandler.deleteBookings();
                            String ds_Nm = jsonObject.getString("d_name");
                            String dsid = jsonObject.getString("d_id");
                            int is_24 = jsonObject.getInt("is_24");
                            JSONArray jsonArrayInfo = jsonObject.getJSONArray("info");

                            JSONArray jsonArrayRoomTypes = jsonObject.getJSONArray("roomtypes");
                            sqLiteHandler.deleteOfflineRoomTypes();
                            sqLiteHandler.deleteBookingsAll();
                            for (int i = 0; i < jsonArrayRoomTypes.length(); i++) {

                                JSONObject object = jsonArrayRoomTypes.getJSONObject(i);
                                String id = object.getString("room_type_id");
                                String nm = object.getString("room_type_name");
                                HashMap hashMap = new HashMap();
                                hashMap.put(id, nm);
                               // roomList.add(hashMap);
                                InventoryRow inventoryRow = new InventoryRow();
                                inventoryRow.setRoomType(nm);
                                inventoryRow.setRoomId(id);
                                inventoryRow.setCountBooked(0);
                                sqLiteHandler.addRoomTypes(id,nm,dsid);
                                // ArrayList<String> list=totalFilteredList.get(key);
                                //Log.d("LJDASKJDAJDKJADK",ArrayList<> );
                               // inventoryRows.add(inventoryRow);
                              //  inventoryRowsOriginal.add(inventoryRow);

                            }

                            ArrayList<String> dates = new ArrayList<>();
                            //   int count = jsonArrayInfo.length();

                            for (int i = 0; i < jsonArrayInfo.length(); i++) {
                                BookingDetails bookingDetails = new BookingDetails();
                                JSONObject object = jsonArrayInfo.getJSONObject(i);
                                String bookingTyp = "all";

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
                                bookingDetails.setContribution(object.getString("contribution"));
                                bookingDetails.setNeftNo(object.getString("neftno"));
                                bookingDetails.setTranferDt(object.getString("transferdate"));
                                bookingDetails.setTranferAmt(object.getString("transfer_amount"));
                                bookingDetails.setYatriAddress(object.getString("address"));
                                bookingDetails.setYatriMobile(object.getString("mobile_no"));
                                bookingDetails.setBookingTotal(object.getString("sub_total"));
                                bookingDetails.setBookingStatus(object.getString("order_status"));
                                bookingDetails.setExtra_mattress_status(object.optString("extra_mattress_status"));
                                bookingDetails.setExtra_mattress_label(object.optString("extra_mattress_label"));
                                bookingDetails.setExtra_mattress_price(object.optString("extra_mattress_price"));
                                bookingDetails.setExtra_mattress_convenience(object.optString("extra_mattress_convenience"));
                                bookingDetails.setTotal_mattress_price(object.optString("total_mattress_price"));
                                Log.e("TAG", "no_of_mattress: "+ object.optString("no_of_mattress") );
                                Log.e("TAG", "extra_mattress_label: "+ object.optString("extra_mattress_label") );
                                bookingDetails.setNo_of_mattress(object.optString("no_of_mattress"));



                                if (object.has("confirm_by"))
                                    bookingDetails.setBookingConfirmedBy(object.getString("confirm_by"));
                                if (object.has("booked_on"))
                                    bookingDetails.setBookingBookedOn(object.getString("booked_on"));

                                bookingDetails.setExpectedCheckinTime(object.getString("expected_checkin_datetime"));

                                if (enqiry_id != null) {
                                    if (bookingDetails.getBookingId().equals(enqiry_id)) {
                                        bookingDetails.setNewArrived(true);
                                    }
                                }
                                bookings.add(bookingDetails);


                                String dt = bookingDetails.getCheckinDt().trim();
                                String arr[] = dt.trim().split("\\s+");
                                String dtCheckout = bookingDetails.getCheckoutDt().trim();

                                String arrCheckout[] = dtCheckout.trim().split("\\s+");
                                if (arr[0].equals(ConvertGMTtoIST.getCurrentDateBookingList())) {
                                    Log.d("AJKDJKDHJDHAJH", "1====" + bookingDetails.getCheckinDt() + "====" + ConvertGMTtoIST.getCurrentDateBookingList());
                                    bookingTyp = "today";
                                    bookingsToday.add(bookingDetails);
                                    sqLiteHandler.addBookingDetails(bookingDetails, bookingTyp);

                                }
                                if (arr[0].equals(ConvertGMTtoIST.getTomorrowDateString())) {
                                    bookingTyp = "tomorrow";
                                    bookingsTomorrow.add(bookingDetails);
                                    sqLiteHandler.addBookingDetails(bookingDetails, bookingTyp);
                                }

                                List<String> listDts = getDates(arr[0], arrCheckout[0]);

                                boolean isCheckIn = false;

                                for (int j = 0; j < listDts.size(); j++) {
                                    if (listDts.get(j).equals(ConvertGMTtoIST.getCurrentDateBookingList())) {
                                        isCheckIn = true;
                                        break;

                                    }

                                }
                                if (isCheckIn) {
                                    bookingTyp = "active";
                                    bookingsActives.add(bookingDetails);
                                    sqLiteHandler.addBookingDetails(bookingDetails, bookingTyp);
                                }

                            }

                            Collections.sort(bookings, new Comparator<BookingDetails>() {

                                public int compare(BookingDetails o1, BookingDetails o2) {

                                    // compare two instance of `Score` and return `int` as result.
                                    return o2.getBookingId().compareTo(o1.getBookingId());
                                }
                            });
                            new AsyncTaskDemo().execute(bookings);

                            //  new GetBookingList().getBookingList(bookings);


                            BookingTodays bookingTodays1 = new BookingTodays();
                            bookingTodays1.setModels(bookingsToday);
                            Log.d("DKAJKJAHAJHA", bookingsToday.size() + "===");
                            EventBus.getDefault().post(bookingTodays1);

                            BookingTomorrow bookingTomorrow = new BookingTomorrow();
                            bookingTomorrow.setModels(bookingsTomorrow);
                            EventBus.getDefault().post(bookingTomorrow);

                            ActiveBookings bookingsActive = new ActiveBookings();
                            bookingsActive.setModels(bookingsActives);
                            EventBus.getDefault().post(bookingsActive);

                            BookingListWithType bookingListWithType = new BookingListWithType();
                            bookingListWithType.setModels(bookings);
                            bookingListWithType.setBookingDate(bookingDate);
                            Log.d("ADKHKAKHKAHD",bookingListWithType+"");
                            EventBus.getDefault().post(bookingListWithType);
                            Log.d("ADJKJKKKAHDKA", bookings.size() + "==" + "==" + bookingsActives.size());
                            //   getBookingListListener.getBookingList(bookings);
                            // setListner(getBookingListListener);
                            //getBookingListListener.getBookingList(bookings);
                            if (isFrom.equals("admin")) {
                                Log.d("JGAGA1GH", "==" + ds_Nm + "==");
                                // getSupportActionBar().setTitle(ds_Nm + "");
                                TextView textview = new TextView(BookingListingActivity.this);

                                ViewGroup.LayoutParams layoutparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                textview.setLayoutParams(layoutparams);

                                textview.setText(ds_Nm + "");

                                textview.setTextColor(Color.WHITE);

                                textview.setGravity(Gravity.CENTER);

                                textview.setTextSize(12);

                                getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

                                getSupportActionBar().setCustomView(textview);

                                sessionManager.setDharamshala_id(dsid, ds_Nm,"10");
                            }
                        } else {
                            Toast.makeText(BookingListingActivity.this, "No data Found!!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(BookingListingActivity.this, "No data Found!!", Toast.LENGTH_LONG).show();
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
                Log.d("ERROR132", error.toString());
                Toast.makeText(BookingListingActivity.this, "Unable to handle server response!", Toast.LENGTH_LONG).show();
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
            mProgressDialog = new ProgressDialog(BookingListingActivity.this);
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

    public void setListner(GetBookingList getBookingListList) {
        getBookingListListener = getBookingListList;
    }

    private class AsyncTaskDemo extends AsyncTask<ArrayList<BookingDetails>, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showProgressDialog("Please Wait While Storing Data!!");
        }

        @Override
        protected Void doInBackground(ArrayList<BookingDetails>... params) {
            ArrayList<BookingDetails> bookingDetailsLst = params[0];
            Date currentDate = new Date();
            //System.out.println(dateFormat.format(currentDate) + "=====");

            String currentDT = dateFormat.format(currentDate);
            Date currentdt=null;
            DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
            try {
                currentdt = df1.parse(currentDT);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal3 = Calendar.getInstance();
            cal3.setTime(currentdt);
            // datesBeetween30=getDatesBeetween(currentDT,get30DaysAfterCurrentDt());

            int count = 0;
            for (int i = 0; i < bookingDetailsLst.size(); i++) {
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = df1.parse(bookingDetailsLst.get(i).getCheckinDt());
                    date2 = df1.parse(get30DaysAfterCurrentDt());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date1);
                Log.d("HAJHJHJHAJHA33", date1 + "===" + date2);

                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(date2);
                if (cal1.after(cal3) && cal1.before(cal2) ) //change to 30 from 10
                {
                    long id_res = -1;

                   id_res = sqLiteHandler.addBookingDetails(bookingDetailsLst.get(i), "all");

                } else if (date1.compareTo(currentdt) == 0) {
                    long id_res = -1;

                    id_res = sqLiteHandler.addBookingDetails(bookingDetailsLst.get(i), "all");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgressDialog();


        }

    }
    private String get30DaysAfterCurrentDt() {
        Date currentDate = new Date();
        System.out.println(dateFormat.format(currentDate));

        // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        // manipulate date
        c.add(Calendar.YEAR, 0);
        c.add(Calendar.MONTH, 0);
        c.add(Calendar.DATE, 32); //same with c.add(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.HOUR, 0);
        c.add(Calendar.MINUTE, 0);
        c.add(Calendar.SECOND, 0);

        // convert calendar to date
        Date currentDatePlusOne = c.getTime();
        Log.d("HDJHJHDJHJADhdjh", dateFormat.format(currentDatePlusOne) + "=");
        String dateResult = dateFormat.format(currentDatePlusOne);
        return dateResult;
    }
    private List<String> getDatesBeetween(String dateString1, String dateString2) {
        Log.d("HAJHJHJHAJHA11", dateString1 + "===" + dateString2);
        ArrayList<Date> dates = new ArrayList<Date>();
        ArrayList<String> datesFormatetd = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");


        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Log.d("HAJHJHJHAJHA33", dateString1 + "===" + dateString2);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        dates.add(cal1.getTime());
        while (!cal1.after(cal2)) {
            // dates.add(cal1.getTime());
            if (!dateString2.equals(df1.format(cal1.getTime()))) {
                datesFormatetd.add(df1.format(cal1.getTime()));
                Log.d("HAJHJHJHAJHA123", df1.format(cal1.getTime()) + "==");
            }
            cal1.add(Calendar.DATE, 1);

        }
        return datesFormatetd;
    }
    private static List<String> getDates(String dateString1, String dateString2) {
        ArrayList<Date> dates = new ArrayList<Date>();
        ArrayList<String> datesFormatetd = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Log.d("HAJHJHJHAJHA", dateString1 + "===" + dateString2);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        dates.add(cal1.getTime());
        while (!cal1.after(cal2)) {
            // dates.add(cal1.getTime());
            if (!dateString2.equals(df1.format(cal1.getTime()))) {
                datesFormatetd.add(df1.format(cal1.getTime()));

                Log.d("HAJHJHJHAJHA", df1.format(cal1.getTime()) + "==");


            }
            cal1.add(Calendar.DATE, 1);

        }
        return datesFormatetd;
    }
}
