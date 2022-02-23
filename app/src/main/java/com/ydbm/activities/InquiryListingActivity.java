package com.ydbm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.ydbm.R;
import com.ydbm.adapters.InquiryListAdapter;
import com.ydbm.fragments.AllInquiries;
import com.ydbm.models.BookingDetails;

import com.ydbm.session.SessionManager;
import com.ydbm.adapters.ViewPagerTabAdapter;
import com.ydbm.utils.AppConstants;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InquiryListingActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    InquiryListAdapter inquiryListAdapter;
    ArrayList<BookingDetails> bookings = new ArrayList<>();
    SessionManager sessionManager;
    ImageView imageViewBack;
    boolean isInternet = false;
    SearchView searchView;
    LinearLayout linearLayoutNobbokings;
    private ProgressDialog mProgressDialog;
    private SQLiteHandler sqLiteHandler;
    private ViewPager mViewPager;
    ArrayList<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_listing);
        sqLiteHandler = new SQLiteHandler(InquiryListingActivity.this);
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //if (from == 7 && sessionManager.getRole().equals("1")) {
        getMenuInflater().inflate(R.menu.bookinglist, menu);
        //   }
        return true;
    }
    private void initViews() {
        initViewPager();
        initTabLayout();

        sessionManager = new SessionManager(InquiryListingActivity.this);
        final String d_id = sessionManager.getUserDetailsOb().getD_id();
        if (d_id != null) {
            isInternet = NetworkUtil.checkInternetConnection(InquiryListingActivity.this);
            if (isInternet) {
                fetchInquiryListAPICall(d_id);
            } else {

                Toast.makeText(InquiryListingActivity.this, "Please Connect To Internet!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "dharamshala id null !!", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), "Please Connect To Internet!", Snackbar.LENGTH_LONG);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpagerBookings);
        List<String> tabNames = new ArrayList<String>();
        tabNames.add("All");
        tabNames.add("Latest");
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
        mFragments.add(AllInquiries.newInstance());

        return mFragments;
    }

    private void initTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBookings);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void fetchInquiryListAPICall(String dharamshala_id) {
        showProgressDialog("Fetching Booking Details.. Please Wait!");
        RequestQueue queue = Volley.newRequestQueue(InquiryListingActivity.this);
        //Log.d("CHECKRESPONS", "https://yatradham.org/demosite/restapi.php?apiname=addgroup&number=" + finalMembers + "&group_id=" + mGroupId + "&is_mobile=APP");
        Log.d("TEGSDHGHDSHD", AppConstants.BASE_API_URL + "enquirylist" + "&dsid=" + dharamshala_id + "&is_mobile=yes");
        StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "enquirylist" + "&dsid=" + dharamshala_id + "&is_mobile=APP", new Response.Listener<String>() {
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
                            //sqLiteHandler.deleteBookings();
                            String ds_Nm = jsonObject.getString("d_name");
                            String dsid = jsonObject.getString("d_id");
                            int is_24 = jsonObject.getInt("is_24");
                            JSONArray jsonArrayInfo = jsonObject.getJSONArray("info");
                            JSONArray jsonArrayRecentList = jsonObject.getJSONArray("recent_list");
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
                               // bookingDetails.setBookingTotal(object.getString("sub_total"));
                              //  bookingDetails.setBookingStatus(object.getString("order_status"));
                                bookingDetails.setExpectedCheckinTime(object.getString("expected_checkin_datetime"));
                                bookingDetails.setExtra_mattress_status(object.optString("extra_mattress_status"));
                                bookingDetails.setExtra_mattress_label(object.optString("extra_mattress_label"));
                                bookingDetails.setExtra_mattress_price(object.optString("extra_mattress_price"));
                                bookingDetails.setExtra_mattress_convenience(object.optString("extra_mattress_convenience"));
                                bookingDetails.setTotal_mattress_price(object.optString("total_mattress_price"));
                                bookingDetails.setNo_of_mattress(object.optString("no_of_mattress"));

                                Log.e("TAG", "no_of_mattressss: "+ object.optString("no_of_mattress") );
                                Log.e("TAG", "extra_mattress_labelll: "+ object.optString("extra_mattress_label") );


                                bookings.add(bookingDetails);


/*
                                Log.d("AJKDJKDHJDHAJH", bookingDetails.getCheckinDt() + "====" + ConvertGMTtoIST.getCurrentDateBookingList());
                                if (bookingDetails.getCheckinDt().contains("am") || bookingDetails.getCheckinDt().contains("pm")) {
                                    Log.d("JKJDAKKADJKAKJ", "1" + "==" + bookingDetails.getCheckinDt());
                                }
                                //  String dt = ConvertGMTtoIST.getCurrentDATE(bookingDetails.getCheckinDt().replaceAll(" ",""));
                                //   Log.d("KJAKAHAS",dt+"==");
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
                                        //     Log.d("JGHASGAHHAGAGH", ConvertGMTtoIST.getDateActiveCheckout(arrCheckout[0]) + "====" + ConvertGMTtoIST.getCurrentDateActive() + "==>");


                                    }

                                }
                                if (isCheckIn) {
                                    bookingTyp = "active";
                                    bookingsActives.add(bookingDetails);
                                    sqLiteHandler.addBookingDetails(bookingDetails, bookingTyp);
                                }


                               /* if (bookingTyp.equals("all")) {
                                    count--;
                                    if (count ) {
                                        sqLiteHandler.addBookingDetails(bookingDetails, bookingTyp);
                                    }

                                }*/

                            }

                            AllInquiries.newInstance().onNotifyBookingList(bookings);
                            Collections.sort(bookings, new Comparator<BookingDetails>() {

                                public int compare(BookingDetails o1, BookingDetails o2) {
                                    // compare two instance of `Score` and return `int` as result.
                                    return o2.getBookingId().compareTo(o1.getBookingId());
                                }
                            });
                            // new BookingListingActivity.AsyncTaskDemo().execute(bookings);

                            //  new GetBookingList().getBookingList(bookings);
                        /*    InquiryWithType bookingListWithType = new InquiryWithType();
                            bookingListWithType.setModels(bookings);
                            EventBus.getDefault().post(bookingListWithType);*/

                          /*  BookingTodays bookingTodays1 = new BookingTodays();
                            bookingTodays1.setModels(bookingsToday);
                            Log.d("DKAJKJAHAJHA", bookingsToday.size() + "===");
                            EventBus.getDefault().post(bookingTodays1);

                            BookingTomorrow bookingTomorrow = new BookingTomorrow();
                            bookingTomorrow.setModels(bookingsTomorrow);
                            EventBus.getDefault().post(bookingTomorrow);

                            ActiveBookings bookingsActive = new ActiveBookings();
                            bookingsActive.setModels(bookingsActives);
                            EventBus.getDefault().post(bookingsActive);
                            */
                            Log.d("ADJKJKKKAHDKA", bookings.size() + "==" + "==");
                            //   getBookingListListener.getBookingList(bookings);
                            // setListner(getBookingListListener);
                            //getBookingListListener.getBookingList(bookings);

                        } else {
                            Toast.makeText(InquiryListingActivity.this, "No data Found!!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(InquiryListingActivity.this, "No data Found!!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(InquiryListingActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
            mProgressDialog = new ProgressDialog(InquiryListingActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(msg);
        }
        if (mProgressDialog != null && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();

        }
    }
}
