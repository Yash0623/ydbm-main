package com.ydbm.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.StatsLog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ydbm.R;
import com.ydbm.adapters.InventoryMainRowAdapter;
import com.ydbm.dialogs.DatePickerCustomDiaog;
import com.ydbm.models.BookingDetails;
import com.ydbm.models.InventoryRow;
import com.ydbm.models.InventoryRowMain;
import com.ydbm.models.InventorySubDetails;
import com.ydbm.session.SessionManager;
import com.ydbm.utils.AppConstants;

import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity {
    RecyclerView recyleView;
    TextView buttonPick;
    SessionManager sessionManager;
    LinearLayout linearLayoutNodata;

    ArrayList<InventoryRowMain> mainArrayList = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private boolean isInternet;
    ArrayList<BookingDetails> bookingDetailsListFiltered = new ArrayList<>();
    ArrayList<BookingDetails> bookingDetailsListAfterFiltered = new ArrayList<>();
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    ArrayList<String> defaultSelection = new ArrayList<>();
    HashMap<String, ArrayList<String>> totalListFiltered = new HashMap<>();
    HashMap<String, ArrayList<BookingDetails>> totalList = new HashMap<>();
    ArrayList<InventoryRow> inventoryRows = new ArrayList<>();
    ArrayList<InventoryRow> inventoryRowsOriginal = new ArrayList<>();
    HashMap<String, Integer> rooms = new HashMap<>();
    InventoryMainRowAdapter inventoryMainRowAdapter;
    CharSequence[] values = {"24 Hrs.", "Fixed"};
    private ArrayList<BookingDetails> bookingDetailsListFilteredBookings = new ArrayList<>();
    AlertDialog alertDialog1;
    SQLiteHandler sqLiteHandler;
    private ArrayList<String> checkIndateList = new ArrayList<>();
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        initToolbar();
        initViews();

        // get10DaysAfterCurrentDt();
        sessionManager = new SessionManager(InventoryActivity.this);
        sqLiteHandler = new SQLiteHandler(InventoryActivity.this);
        final String d_id = sessionManager.getUserDetailsOb().getD_id();
        if (d_id != null) {
            isInternet = NetworkUtil.checkInternetConnection(InventoryActivity.this);
            if (isInternet) {
                fetchBookingListAPICall(d_id);
            } else {
                Toast.makeText(InventoryActivity.this, "Please Connect To Internet!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "dharamshala not found !!", Toast.LENGTH_SHORT).show();
            //Snackbar.make(findViewById(android.R.id.content), "Please Connect To Internet!", Snackbar.LENGTH_LONG);
        }
    }

    private String get10DaysAfterCurrentDt() {
        Date currentDate = new Date();
        System.out.println(dateFormat.format(currentDate));

        // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        // manipulate date
        c.add(Calendar.YEAR, 0);
        c.add(Calendar.MONTH, 0);
        c.add(Calendar.DATE, 10); //same with c.add(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.HOUR, 0);
        c.add(Calendar.MINUTE, 0);
        c.add(Calendar.SECOND, 0);

        // convert calendar to date
        Date currentDatePlusOne = c.getTime();
        Log.d("HDJHJHDJHJADhdjh", dateFormat.format(currentDatePlusOne) + "=");
        String dateResult = dateFormat.format(currentDatePlusOne);
        return dateResult;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle1 = new Bundle();
        bundle1.putString("Name", sessionManager.getUserDetailsOb().getName() + "");
        bundle1.putString("user_detail", "Booking List By " + sessionManager.getUserDetailsOb().getUsername() + " at " + ConvertGMTtoIST.getCurrentDateTimeLastSeen() + "");
        bundle1.putString("Dharamshala_Name", sessionManager.getDharamshalanm() + "");
        mFirebaseAnalytics.logEvent("Screen_View", bundle1);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_booking) {
            final String[] selected = {"24 Hrs."};
            if (sessionManager.geDharamshalaTimezoneSel() == null) {
               /* LayoutInflater inflater = getLayoutInflater();
                //View dialogView = inflater.inflate(R.layout.popup_radio, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(InventoryActivity.this);
                //builder.setView(dialogView);


                builder.setSingleChoiceItems(values, 0, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        switch (item) {
                            case 0:

                                Toast.makeText(InventoryActivity.this, "First Item Clicked", Toast.LENGTH_LONG).show();
                                int selectedId = item;
                                Log.i("ID", String.valueOf(selectedId) + "==");
                                RadioButton radioButton = findViewById(selectedId);
                                selected[0] = radioButton.getText().toString();
                                Log.d("ASJHSJSHj3434", selected[0] + "==");
                                boolean isTime = sessionManager.setDharamshalaTimezoneSel(radioButton.getText().toString());
                                if (isTime) {
                                    Intent intent = new Intent(InventoryActivity.this, AddofflineBookingActivity.class);
                                    startActivity(intent);
                                }
                                break;
                            case 1:

                                Toast.makeText(InventoryActivity.this, "Second Item Clicked", Toast.LENGTH_LONG).show();
                                break;

                        }
                        //dialog.dismiss();
                    }
                });
               /* RadioGroup radioGroupTimezone =dialogView.findViewById(R.id.radioGrpTimezone);

                radioGroupTimezone.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup arg0, int arg1) {
                        int selectedId = radioGroupTimezone.getCheckedRadioButtonId();
                        Log.i("ID", String.valueOf(selectedId)+"==");
                        RadioButton radioButton = findViewById(selectedId);
                        selected[0] = radioButton.getText().toString();
                        Log.d("ASJHSJSHj3434", selected[0] + "==");
                                       /* boolean isTime = sessionManager.setDharamshalaTimezoneSel(radioButton.getText().toString());
                                        if (isTime) {
                                            Intent intent = new Intent(InventoryActivity.this, AddofflineBookingActivity.class);
                                            startActivity(intent);
                                        }

                    }
                });
*/
                /*builder.setMessage("Please select timezone..")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //  Toast.makeText(InventoryActivity.this, "yes", Toast.LENGTH_SHORT).show();


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(InventoryActivity.this, "No", Toast.LENGTH_SHORT).show();

                            }
                        }).show();
                */
                AlertDialog.Builder builder = new AlertDialog.Builder(InventoryActivity.this);

                builder.setTitle("Select Timezone..");

                builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        switch (item) {
                            case 0:
                                boolean isTime = sessionManager.setDharamshalaTimezoneSel("24 Hrs.");
                                if (isTime) {
                                    Intent intent = new Intent(InventoryActivity.this, AddofflineBookingActivity.class);
                                    startActivity(intent);
                                }
                                // Toast.makeText(InventoryActivity.this, "First Item Clicked", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                boolean isTime1 = sessionManager.setDharamshalaTimezoneSel("Fixed");
                                if (isTime1) {
                                    Intent intent = new Intent(InventoryActivity.this, AddofflineBookingActivity.class);
                                    startActivity(intent);
                                }
                                //Toast.makeText(InventoryActivity.this, "Second Item Clicked", Toast.LENGTH_LONG).show();
                                break;

                        }
                        alertDialog1.dismiss();
                    }
                });
                alertDialog1 = builder.create();
                alertDialog1.show();

            } else {

                Intent intent = new Intent(InventoryActivity.this, AddofflineBookingActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.action_view_booking) {
            Intent intent = new Intent(InventoryActivity.this, ViewOfflineBookingsActivity.class);
            intent.putExtra("from", 12);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        recyleView = findViewById(R.id.recylerInventoryMain);
        buttonPick = findViewById(R.id.btnPickdt);
        linearLayoutNodata = findViewById(R.id.noDataLinear);
        recyleView.setLayoutManager(new GridLayoutManager(InventoryActivity.this, 2));

        //InventoryMainRowAdapter inventoryMainRowAdapter = new InventoryMainRowAdapter(mainArrayList, InventoryActivity.this);
        //recyleView.setAdapter(inventoryMainRowAdapter);
        buttonPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerCustomDiaog update = new DatePickerCustomDiaog(InventoryActivity.this, new DatePickerCustomDiaog.CalendarListener() {
                    @Override
                    public void onFirstDateSelected(Calendar startDate) {


                    }

                    @Override
                    public void onDateRangeSelected(Calendar startDate, Calendar endDate) {
                        getDatesTranferDts(dateFormat.format(startDate.getTime()), dateFormat.format(endDate.getTime()), rooms);
                    }

                    @Override
                    public void onResetBtnClicked() {

                    }

                });
                update.setCancelable(false);
                update.setCanceledOnTouchOutside(true);
                update.show();
            }
        });


    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void fetchBookingListAPICall(String dharamshala_id) {
        showProgressDialog("Fetching Booking Details.. Please Wait!");
        RequestQueue queue = Volley.newRequestQueue(InventoryActivity.this);
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
                            HashMap<String, ArrayList<String>> roomsTyp = new HashMap<>();
                            ArrayList<BookingDetails> bookingDetailsList = new ArrayList<>();

                            ArrayList<HashMap<String, String>> roomList = new ArrayList<>();
                            ArrayList<HashMap<String, String>> checkInList = new ArrayList<>();


                            HashMap<String, ArrayList<BookingDetails>> totalListBkngDetails = new HashMap<>();


                            HashMap<String, ArrayList<HashMap<String, ArrayList<BookingDetails>>>> totalFilteredList = new HashMap<>();
                            // sqLiteHandler.deleteBookings();
                            String ds_Nm = jsonObject.getString("d_name");
                            String dsid = jsonObject.getString("d_id");
                            int is_24 = jsonObject.getInt("is_24");
                            JSONArray jsonArrayInfo = jsonObject.getJSONArray("info");
                            JSONArray jsonArrayRoomTypes = jsonObject.getJSONArray("roomtypes");

                            for (int i = 0; i < jsonArrayRoomTypes.length(); i++) {

                                JSONObject object = jsonArrayRoomTypes.getJSONObject(i);
                                String id = object.getString("room_type_id");
                                String nm = object.getString("room_type_name");
                                HashMap hashMap = new HashMap();
                                hashMap.put(id, nm);
                                roomList.add(hashMap);
                                InventoryRow inventoryRow = new InventoryRow();
                                inventoryRow.setRoomType(nm);
                                inventoryRow.setRoomId(id);
                                inventoryRow.setCountBooked(0);
                                // ArrayList<String> list=totalFilteredList.get(key);
                                //Log.d("LJDASKJDAJDKJADK",ArrayList<> );
                                inventoryRows.add(inventoryRow);
                                inventoryRowsOriginal.add(inventoryRow);

                            }


                            // HashMap<String, ArrayList<BookingDetails>> mapRoomIdList = new HashMap<>();


                            for (int i = 0; i < jsonArrayInfo.length(); i++) {

                                BookingDetails bookingDetails = new BookingDetails();
                                String bookingTyp = "all";
                                JSONObject object = jsonArrayInfo.getJSONObject(i);
                                bookingDetails.setBookingId(object.getString("bookingid"));
                                bookingDetails.setDs_name(ds_Nm);
                                bookingDetails.setDs_id(dsid);
                                bookingDetails.setIs_24Hours(String.valueOf(is_24));
                                bookingDetails.setYatriNm(object.getString("yatriname"));
                                String rmType = object.getString("roomtype");
                                String roomId = object.getString("room_id");

                                bookingDetails.setRoom_id(roomId);
                                bookingDetails.setRoomTyp(rmType);

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

                                if (object.has("confirm_by"))
                                    bookingDetails.setBookingConfirmedBy(object.getString("confirm_by"));
                                if (object.has("booked_on"))
                                    bookingDetails.setBookingBookedOn(object.getString("booked_on"));
                                ArrayList<String> datesBetwn = getCheckInDates(object.getString("checkin").trim(), object.getString("checkout").trim());
                                bookingDetails.setExpectedCheckinTime(object.getString("expected_checkin_datetime"));
                                bookingDetails.setArrayTotalCheckins(datesBetwn);
                                bookingDetailsListFiltered.add(bookingDetails);


                                // String checkin = object.getString("checkin").trim();
                               /* if (i == 2) {
                                    checkin = "25-08-2019";
                                }*/
                                bookingDetailsList.add(bookingDetails);
                                ArrayList<String> totalCheckins = bookingDetails.getArrayTotalCheckins();
                                for (int j = 0; j < totalCheckins.size(); j++) {
                                    String checkin = totalCheckins.get(j);
                                    if (totalList.containsKey(checkin)) {
                                        ArrayList<BookingDetails> list3 = totalList.get(checkin);
                                        Log.d("ADJKADKJKADJ", list3.size() + "==");
                                        list3.add(bookingDetails);
                                        //totalList.put(checkin.trim(), list3);
                                        Log.d("ADJKADKJKADJ", list3.size() + "==");
                                        Log.d("JDAJJKDKJAKJADK122", checkin + "==" + list3.size());

                                        //   mapRoomIdList.put(roomId,list);

                                    } else {
                                        ArrayList<BookingDetails> list1 = new ArrayList<>();
                                        list1.add(bookingDetails);
                                        Log.d("ADJKADKJKADJ", list1.size() + "==");

                                        totalList.put(checkin.trim(), list1);
                                        Log.d("JDAJJKDKJAKJADK122", checkin + "==>" + list1.size());


                                    }
                                }


                            }


                            Date currentDate = new Date();
                            System.out.println(dateFormat.format(currentDate) + "=====");

                            String currentDT = dateFormat.format(currentDate);
                            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

                            ArrayList<HashMap<String, InventorySubDetails>> inventorySubDetails = new ArrayList<>();
                            HashMap<String, Integer> rooms = new HashMap<>();

                            for (Map.Entry<String, ArrayList<BookingDetails>> entry : totalList.entrySet()) {
                                String key = entry.getKey();
                                ArrayList<BookingDetails> value = totalList.get(key.trim());
                                Log.d("HDAJHJDAJADh13", key + "===" + value.size());


                                //InventoryRow inventoryRow = new InventoryRow();
                                // ArrayList<String> list=totalFilteredList.get(key);
                                //Log.d("LJDASKJDAJDKJADK",ArrayList<> );
                                InventorySubDetails inventorySubDetails1 = new InventorySubDetails();
                                inventorySubDetails1.setCheckIn(key);


                                for (int i = 0; i < value.size(); i++) {

                                    BookingDetails bookingDetails = value.get(i);

                                    String mainKey = key.trim() + "," + bookingDetails.getRoom_id();
                                    Log.d("HADJDHJDAHJADHJ", key + "===" + bookingDetails.getRoom_id());

                                    if (rooms.containsKey(mainKey)) {

                                        int rmTypes1 = rooms.get(mainKey);
                                        Log.d("AJKJKJKAJKADJ", bookingDetails.getNoOfRooms() + "===" + mainKey + "==" + rmTypes1 + "==>>" + 1);

                                        int rmTypes = rmTypes1 + (Integer.parseInt(bookingDetails.getNoOfRooms().trim()));
                                        Log.d("AJKJKJKAJKADJ", rmTypes + "");

                                        rooms.put(mainKey, rmTypes);
                                    } else {
                                        int rmTypes = 0;
                                        Log.d("AJKJKJKAJKADJ", bookingDetails.getNoOfRooms() + "===" + mainKey + "==>>" + 2);

                                        rmTypes = Integer.parseInt(bookingDetails.getNoOfRooms().trim());
                                        rooms.put(mainKey, rmTypes);
                                    }

                                    System.out.println("key : " + key + " value : " + mainKey + "===" + bookingDetails.getCheckinDt());
                                    Log.d("ASHHSHSH12378", key + "==>" + rooms.get(mainKey) + "==" + mainKey);
                                }


                            }


                            getDatesTranferDts(currentDT.trim(), get10DaysAfterCurrentDt().trim(), rooms);
                            //  getDates(currentDT,get10DaysAfterCurrentDt());

                            /*for (Map.Entry<String, ArrayList<String>> entry : totalListFiltered.entrySet()) {
                                String key = entry.getKey();
                                ArrayList<String> value = entry.getValue();
                                InventoryRow inventoryRow = new InventoryRow();
                                // ArrayList<String> list=totalFilteredList.get(key);
                                //Log.d("LJDASKJDAJDKJADK",ArrayList<> );
                                for (String aString : value) {
                                    System.out.println("key : " + key + " value : " + aString);


                                }
                            }

                            for (Map.Entry<String, ArrayList<BookingDetails>> entry : totalList.entrySet()) {
                                String key = entry.getKey();
                                ArrayList<BookingDetails> value = entry.getValue();
                                //InventoryRow inventoryRow = new InventoryRow();
                                // ArrayList<String> list=totalFilteredList.get(key);
                                //Log.d("LJDASKJDAJDKJADK",ArrayList<> );
                                for (BookingDetails aString : value) {
                                    System.out.println("key : " + key + " value : " + aString.getRoom_id() + "===" + aString.getCheckinDt());


                                }
                            }

*/


                        } else {
                            Toast.makeText(InventoryActivity.this, "No data Found!!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(InventoryActivity.this, "No data Found!!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(InventoryActivity.this, "Unable to handle server response!", Toast.LENGTH_LONG).show();
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
            mProgressDialog = new ProgressDialog(InventoryActivity.this);
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


    private void getDatesTranferDts(String dateString1, String dateString2, HashMap<String, Integer> rooms) {
        Log.d("HSAJHJSHJH65656", dateString1 + "===>" + dateString2);
        buttonPick.setText(dateString1 + " to " + dateString2 + "");
        mainArrayList.clear();
        inventoryRows.clear();
        inventoryRows.addAll(inventoryRowsOriginal);
        ArrayList<Date> dates = new ArrayList<Date>();
        ArrayList<String> datesFormatetd = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
            Calendar cal4 = Calendar.getInstance();

            // go back two days
            cal4.setTime(date2);
            cal4.add(Calendar.DATE, -1);
            date2=cal4.getTime();
            Log.d("checkdatelog", df1.format(cal4.getTime()) + "==="+dateString1+"    "+dateString2);
            Log.d("SKJKJKDJKSJ", date1 + "===>>" + date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        dates.add(cal1.getTime());

        Log.d("HAJHJHJHAJHA112", dateString1 + "===" + dateString2 + "===" + cal1.getTime() + "====>" + cal2.getTime());

       /* for (Map.Entry<String, ArrayList<BookingDetails>> entry : totalList.entrySet()) {
            String key = entry.getKey();
            ArrayList<BookingDetails> value = entry.getValue();
            //InventoryRow inventoryRow = new InventoryRow();
            // ArrayList<String> list=totalFilteredList.get(key);
            //Log.d("LJDASKJDAJDKJADK",ArrayList<> );
            for (BookingDetails aString : value) {
                System.out.println("key : " + key + " value : " + aString.getRoom_id() + "===" + aString.getCheckinDt());


            }
        }
*/
        while (!cal1.after(cal2)) {
            // dates.add(cal1.getTime());
            Log.d("DLAKLKLKLKALK11", dateString2 + "==>>" + df1.format(cal1.getTime()));
            if (!dateString2.equals(df1.format(cal1.getTime()))) {
                datesFormatetd.add(df1.format(cal1.getTime()));


                Log.d("HAJHJHJHAJHA", df1.format(cal1.getTime()) + "==");



                /*for (Map.Entry<String, Integer> entry : rooms.entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    Log.d("DLAKLKLKLKALK11", key + "==>>" + value);
                    String arr[] = key.split(",");
                    Log.d("TEST12323", arr[0] + "==>" + df1.format(cal1.getTime()).trim() + "==1");
                    if (arr[0].equals(df1.format(cal1.getTime()).trim())) {
                        Log.d("TEST12323", arr[0] + "==>" + df1.format(cal1.getTime()).trim());
                        for (int j = 0; j < inventoryRows.size(); j++) {


                            InventoryRow inventoryRow = inventoryRows.get(j);
                            inventoryRow.setKey(df1.format(cal1.getTime()).trim());

                            Log.d("TEST12323", j + "==>" + df1.format(cal1.getTime()).trim() + "==>" + arr[1] + "==" + inventoryRow.getRoomId());
                            if (arr[1].equals(inventoryRow.getRoomId())) {
                                Log.d("TEST12323", arr[1] + "==>" + df1.format(cal1.getTime()).trim() + "==>----" + value);

                                inventoryRow.setCountBooked(value);
                            }
                        }
                    }
                }*/

                    /*for (Map.Entry<String, Integer> entry : rooms.entrySet()) {
                        String key = entry.getKey();
                        int value = entry.getValue();
                        String arr[] = key.split(",");
                        if (arr[0].equals(df1.format(cal1.getTime()).trim())) {

                        }
                        //InventoryRow inventoryRow = new InventoryRow();
                        // ArrayList<String> list=totalFilteredList.get(key);
                        //Log.d("LJDASKJDAJDKJADK",ArrayList<> );
                      /*  for (BookingDetails bookingDetails : value) {
                            System.out.println("key : " + key + " value : " + bookingDetails.getRoom_id() + "===" + bookingDetails.getCheckinDt());
                            String dt = bookingDetails.getCheckinDt();

                            Log.d("HAJHJHJHAJHA123", df1.format(cal1.getTime()) + "==" + "===" + dt);

                            if (dt.trim().equals(df1.format(cal1.getTime()).trim())) {
                                Log.d("HAJHJHJHAJHA123", "true");

                                Log.d("JDAJJKDKJAKJADK122", dt + "===" + inventoryRows.size());
                                for (int j = 0; j < inventoryRows.size(); j++) {
                                    InventoryRow inventoryRow = inventoryRows.get(j);
                                    inventoryRow.setKey(dt);


                                    Collection<BookingDetails> smallList = new ArrayList<>();
                                    smallList.clear();

                                    smallList = CollectionUtils.select(totalList.get(dt.trim()), new Predicate() {
                                        public boolean evaluate(Object o) {
                                            BookingDetails c = (BookingDetails) o;
                                            Log.d("GGDAHGDAHGH1123", c.getRoom_id() + "==>" + inventoryRow.getRoomId());
                                            return totalList.containsKey(dt.trim()) && c.getRoom_id().equals(inventoryRow.getRoomId());
                                        }
                                    });
                                    Log.d("GGDAHGDAHGH1123", smallList.size() + "");
                                    if (smallList.size() > 0) {
                                        Log.d("ADJKJKJK", smallList.size() + "");
                                        inventoryRow.setCountBooked(smallList.size());
                                    } else {
                                        Log.d("ADJKJKJK", 2 + "");
                                        inventoryRow.setCountBooked(0);
                                    }

                                    smallList.clear();
                                    Log.d("JDAJJKDKJAKJADK122", dt + "===" + smallList.size());
                                }
                                bookingDetailsListAfterFiltered.add(bookingDetails);*/
                InventoryRowMain inventoryRowMain = new InventoryRowMain();
                inventoryRowMain.setDs_id(df1.format(cal1.getTime()).trim());
                inventoryRowMain.setDs_name(sessionManager.getDharamshalanm());
                inventoryRowMain.setList(inventoryRows);
                mainArrayList.add(inventoryRowMain);


            }

              /*  for (BookingDetails bookingDetails : bookingDetailsListFiltered) {
                    String dt = bookingDetails.getCheckinDt();

                    Log.d("HAJHJHJHAJHA123", df1.format(cal1.getTime()) + "==" + "===" + dt);

                    if (dt.trim().equals(df1.format(cal1.getTime()).trim())) {
                        Log.d("HAJHJHJHAJHA123", "true");
                 /*       Collection<String> filtered = Collections2.filter(bookingDetailsListFiltered.get(),
                                Predicates.containsPattern("How"));
*/

            // List<BookingDetails> bigList = new ArrayList<>(); // master list

                     /*   Log.d("JDAJJKDKJAKJADK122", dt + "===" + inventoryRows.size());
                        for (int j = 0; j < inventoryRows.size(); j++) {
                            InventoryRow inventoryRow = inventoryRows.get(j);
                            inventoryRow.setKey(dt);


                            Collection<BookingDetails> smallList = new ArrayList<>();
                            smallList.clear();

                            smallList = CollectionUtils.select(totalList.get(dt.trim()), new Predicate() {
                                public boolean evaluate(Object o) {
                                    BookingDetails c = (BookingDetails) o;
                                    Log.d("GGDAHGDAHGH1123", c.getRoom_id() + "==>" + inventoryRow.getRoomId());
                                    return totalList.containsKey(dt.trim()) && c.getRoom_id().equals(inventoryRow.getRoomId());
                                }
                            });
                            Log.d("GGDAHGDAHGH1123", smallList.size() + "");
                            if (smallList.size() > 0) {
                                Log.d("ADJKJKJK", smallList.size() + "");
                                inventoryRow.setCountBooked(smallList.size());
                            } else {
                                Log.d("ADJKJKJK", 2 + "");
                                inventoryRow.setCountBooked(0);
                            }

                            smallList.clear();
                            Log.d("JDAJJKDKJAKJADK122", dt + "===" + smallList.size());
                        }
                       bookingDetailsListAfterFiltered.add(bookingDetails);
                        InventoryRowMain inventoryRowMain = new InventoryRowMain();
                        inventoryRowMain.setDs_id(dt);
                        inventoryRowMain.setDs_name(sessionManager.getDharamshalanm());
                        inventoryRowMain.setList(inventoryRows);
                        mainArrayList.add(inventoryRowMain);
                    }
                } */


            cal1.add(Calendar.DATE, 1);

        }
           /* for (Map.Entry<String, ArrayList<BookingDetails>> entry : totalList.entrySet()) {
                String key = entry.getKey();
                ArrayList<BookingDetails> value = entry.getValue();
                //InventoryRow inventoryRow = new InventoryRow();
                // ArrayList<String> list=totalFilteredList.get(key);
                //Log.d("LJDASKJDAJDKJADK",ArrayList<> );

                InventoryRowMain inventoryRowMain = new InventoryRowMain();
                inventoryRowMain.setDs_id(key);
                inventoryRowMain.setDs_name(sessionManager.getDharamshalanm());

                inventoryRowMain.setList(inventoryRowsOriginal);
                mainArrayList.add(inventoryRowMain);

            }*/


        if (inventoryMainRowAdapter == null) {
            Log.d("LJADKJKDAJKAD", "1");
            if (mainArrayList.size() > 0) {
                linearLayoutNodata.setVisibility(View.GONE);
                recyleView.setVisibility(View.VISIBLE);

            } else {
                linearLayoutNodata.setVisibility(View.VISIBLE);
                recyleView.setVisibility(View.GONE);


            }
            inventoryMainRowAdapter = new InventoryMainRowAdapter(rooms, mainArrayList, InventoryActivity.this);
            recyleView.setAdapter(inventoryMainRowAdapter);
        } else {
            Log.d("LJADKJKDAJKAD", "2");
            if (mainArrayList.size() > 0) {
                linearLayoutNodata.setVisibility(View.GONE);
                recyleView.setVisibility(View.VISIBLE);

            } else {
                linearLayoutNodata.setVisibility(View.VISIBLE);
                recyleView.setVisibility(View.GONE);


            }
            recyleView.invalidate();
            inventoryMainRowAdapter.notifyDataSetChanged();

        }
    }

    private ArrayList<String> getCheckInDates(String dateString1, String dateString2) {
        ArrayList<Date> dates = new ArrayList<Date>();
        ArrayList<String> datesFormatetd = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");


        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
            Calendar cal4 = Calendar.getInstance();

            // go back two days
            cal4.setTime(date2);
            cal4.add(Calendar.DATE, -1);
            date2=cal4.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Log.d("HAJHJHJHAJHA113", dateString1 + "===" + dateString2);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        dates.add(cal1.getTime());
        while (!cal1.after(cal2)) {
            // dates.add(cal1.getTime());
            if (!dateString2.equals(df1.format(cal1.getTime()))) {
                datesFormatetd.add(df1.format(cal1.getTime()));

                Log.d("HAJHJHJHAJHA112", df1.format(cal1.getTime()) + "=="+ "==="+dateString1+"    "+dateString2);
               /* for (BookingDetails bookingDetails : bookingDetailsListFiltered) {
                    String[] dt = bookingDetails.getCheckinDt().split(" ");
                    Log.d("HAJHJHJHAJHA123", df1.format(cal1.getTime()) + "==" + "===" + dt[0]);
                    if (dt[0].equals(df1.format(cal1.getTime()))) {
                        bookingDetailsListFilteredBookings.add(bookingDetails);
                    } else {


                    }
                }*/


            }
            cal1.add(Calendar.DATE, 1);

        }
       /* bookingDetailsList.clear();
        bookingDetailsList.addAll(bookingDetailsListFilteredBookings);
        bookinListAdapter.notifyDataSetChanged();
        btnClearFilter.setVisibility(View.VISIBLE);
        isFiltered = true;*/
        return datesFormatetd;
    }


}
