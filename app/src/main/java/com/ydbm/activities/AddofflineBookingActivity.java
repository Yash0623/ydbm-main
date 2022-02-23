package com.ydbm.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.ydbm.R;
import com.ydbm.models.BookingDetails;
import com.ydbm.models.RoomType;
import com.ydbm.session.SessionManager;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddofflineBookingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Spinner spinnerCheckIn, spinnerCheckOut, sp_am_pm1, sp_am_pm2, spinnerRoomTypes, sp_checkin_time, sp_checkout_time;
    RadioButton radioButton24, radioButtonFixed;
    LinearLayout linearLayoutTimePick, linearLayout24Hrs, linearLayoutFixed, linearLayoutEditTime;
    RadioGroup radioGroupTimezone;
    String did, timezone;
    SessionManager sessionManager;
    TextView textViewHrs;
    SQLiteHandler sqLiteHandler;
    Button btnAdd;
    private String dateeee = "";
    private String date = "";
    CharSequence[] values = {"24 Hrs.", "Fixed"};
    AlertDialog alertDialog1;
    TextView textViewTime;
    EditText editPersonName, editPhone, editNoofRooms, editAddress, editPrice, totalNights, editTextPerson;
    boolean is24Selected = true;
    private LinearLayout ll_checkin, ll_check_out, ll_checkinTime, ll_checkoutTime;
    private String flag = "";
    private String date_input_checkin;
    private String date_input_checkout;
    private TextView txt_checkin, txt_checkout, tvBookingTot;
    private FirebaseAnalytics mFirebaseAnalytics;
    private boolean isIntenrnet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addoffline_booking);
        sqLiteHandler = new SQLiteHandler(AddofflineBookingActivity.this);
        sessionManager = new SessionManager(AddofflineBookingActivity.this);
        timezone = sessionManager.geDharamshalaTimezoneSel();
        String inventory = sqLiteHandler.tableToString();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(AddofflineBookingActivity.this);

        Log.d("JDKKDAJJKDAJKAD", inventory + "");
        initToolbar();
        initViews();
    }

    private void initViews() {
        radioGroupTimezone = findViewById(R.id.radioGrpTimezone);
        // radioGroupTimezone = findViewById(R.id.radioGrpTimezone);
        spinnerCheckIn = findViewById(R.id.sp_checkin_time);
        totalNights = findViewById(R.id.textViewNightsValue);
        spinnerCheckOut = findViewById(R.id.sp_checkout_time);
        spinnerRoomTypes = findViewById(R.id.sp_room_type);
        textViewTime = findViewById(R.id.textViewTime);
        sp_am_pm1 = findViewById(R.id.sp_am_pm1);
        sp_am_pm2 = findViewById(R.id.sp_am_pm2);
        sp_checkin_time = findViewById(R.id.sp_checkin_time);
        sp_checkout_time = findViewById(R.id.sp_checkout_time);
        btnAdd = findViewById(R.id.btnAdd);

        editPersonName = findViewById(R.id.editTextNm);
        editPhone = findViewById(R.id.editTextPhone);
        editPrice = findViewById(R.id.editNoPrice);
        editTextPerson = findViewById(R.id.editGuests);

        editAddress = findViewById(R.id.textArea_information);

        editNoofRooms = findViewById(R.id.editNoOfRooms);
        textViewHrs = findViewById(R.id.textViewHrs);
        txt_checkin = (TextView) findViewById(R.id.txt_checkin);
        txt_checkout = (TextView) findViewById(R.id.txt_checkout);
        tvBookingTot = findViewById(R.id.textViewBookingTotal);


        linearLayoutTimePick = findViewById(R.id.ll_24hrs_time);
        linearLayoutFixed = findViewById(R.id.linearFixed);
        ll_checkinTime = findViewById(R.id.ll_checkin_time);
        ll_checkoutTime = findViewById(R.id.ll_checkout_time);
        editPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0 && totalNights.getText().toString().length() > 0) {
                    if (editPrice.getText().length() > 0 && Integer.parseInt(editPrice.getText().toString()) == 0) {
                        Toast.makeText(AddofflineBookingActivity.this, "please enter valid price !!", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(editNoofRooms.getText().toString()) == 0 || editNoofRooms.getText().toString().length() == 0) {
                        Toast.makeText(AddofflineBookingActivity.this, "please enter Number Of Rooms !!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!editPrice.getText().toString().isEmpty() && !editNoofRooms.getText().toString().isEmpty()) {
                            Double total = Double.parseDouble(editPrice.getText().toString()) * Integer.parseInt(totalNights.getText().toString()) * Integer.parseInt(editNoofRooms.getText().toString());
                            tvBookingTot.setText(total + "");
                        }
                    }
                }
            }
        });

        editNoofRooms.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0 && totalNights.getText().toString().length() > 0) {
                    if (editPrice.getText().length() > 0 && Integer.parseInt(editPrice.getText().toString()) == 0) {
                        Toast.makeText(AddofflineBookingActivity.this, "please enter valid price !!", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(editNoofRooms.getText().toString()) == 0 || editNoofRooms.getText().toString().length() == 0) {
                        Toast.makeText(AddofflineBookingActivity.this, "please enter Number Of Rooms !!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!editPrice.getText().toString().isEmpty() && !editNoofRooms.getText().toString().isEmpty()) {
                            Double total = Double.parseDouble(editPrice.getText().toString()) * Integer.parseInt(totalNights.getText().toString()) * Integer.parseInt(editNoofRooms.getText().toString());
                            tvBookingTot.setText(total + "");
                        }
                    }
                }
            }
        });
        linearLayoutEditTime = findViewById(R.id.linearEditTime);

        ll_checkin = (LinearLayout) findViewById(R.id.ll_checkin);
        ll_check_out = (LinearLayout) findViewById(R.id.ll_check_out);

        ll_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "from_checkin";
                showDatePickerDialog(1);
            }
        });

        ll_check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flag = "from_checkout";
                showDatePickerDialog(2);
            }
        });

        final ArrayAdapter<String> adapter_checkin = new ArrayAdapter<String>(AddofflineBookingActivity.this,
                R.layout.selected_item, getResources().getStringArray(R.array.check_in_out_time_new));

        final ArrayAdapter<String> adapter_am_pm1 = new ArrayAdapter<String>(AddofflineBookingActivity.this,
                R.layout.selected_item, getResources().getStringArray(R.array.time_format));

        spinnerCheckIn.setAdapter(adapter_checkin);
        spinnerCheckOut.setAdapter(adapter_checkin);

        sp_am_pm1.setAdapter(adapter_am_pm1);
        sp_am_pm2.setAdapter(adapter_am_pm1);

        did = sessionManager.getDharamshalaId();

        ArrayList<RoomType> roomTypes = sqLiteHandler.fetchRoomTypes(did);
        ArrayAdapter roomTypAdapter = new ArrayAdapter(this, R.layout.selected_item, roomTypes);
        spinnerRoomTypes.setAdapter(roomTypAdapter);

        sp_am_pm1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (txt_checkin.getText().toString().length() > 0 && txt_checkout.getText().toString().length() > 0) {
                    String checkInTime24 = convertTo24Hours(sp_checkin_time.getSelectedItem() + " " + sp_am_pm1.getSelectedItem());
                    String checkOutTime24 = convertTo24Hours(sp_checkout_time.getSelectedItem() + " " + sp_am_pm2.getSelectedItem());
                    int c = getTotalNights(txt_checkin.getText().toString() + " " + checkInTime24, txt_checkout.getText().toString() + " " + checkOutTime24);
                    {
                        Log.d("HGHghh5452", c + "");
                        totalNights.setText(c + "");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        sp_am_pm2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (txt_checkin.getText().toString().length() > 0 && txt_checkout.getText().toString().length() > 0) {
                    String checkInTime24 = convertTo24Hours(sp_checkin_time.getSelectedItem() + " " + sp_am_pm1.getSelectedItem());
                    String checkOutTime24 = convertTo24Hours(sp_checkout_time.getSelectedItem() + " " + sp_am_pm2.getSelectedItem());
                    int c = getTotalNights(txt_checkin.getText().toString() + " " + checkInTime24, txt_checkout.getText().toString() + " " + checkOutTime24);
                    {
                        Log.d("HGHghh5452", c + "");
                        totalNights.setText(c + "");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        sp_checkin_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (txt_checkin.getText().toString().length() > 0 && txt_checkout.getText().toString().length() > 0) {
                    String checkInTime24 = convertTo24Hours(sp_checkin_time.getSelectedItem() + " " + sp_am_pm1.getSelectedItem());
                    String checkOutTime24 = convertTo24Hours(sp_checkout_time.getSelectedItem() + " " + sp_am_pm2.getSelectedItem());
                    int c = getTotalNights(txt_checkin.getText().toString() + " " + checkInTime24, txt_checkout.getText().toString() + " " + checkOutTime24);
                    {
                        Log.d("HGHghh5452", c + "");
                        totalNights.setText(c + "");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        totalNights.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0 && totalNights.getText().toString().length() > 0 && editPrice.getText().toString().length() > 0) {
                    if (editPrice.getText().length() > 0 && Integer.parseInt(editPrice.getText().toString()) == 0) {
                        Toast.makeText(AddofflineBookingActivity.this, "please enter valid price !!", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(editNoofRooms.getText().toString()) == 0 || editNoofRooms.getText().toString().length() == 0) {
                        Toast.makeText(AddofflineBookingActivity.this, "please enter Number Of Rooms !!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!editPrice.getText().toString().isEmpty() && !editNoofRooms.getText().toString().isEmpty()) {
                            Double total = Double.parseDouble(editPrice.getText().toString()) * Integer.parseInt(totalNights.getText().toString()) * Integer.parseInt(editNoofRooms.getText().toString());
                            tvBookingTot.setText(total + "");
                        }
                    }
                }
            }
        });
        sp_checkout_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (txt_checkin.getText().toString().length() > 0 && txt_checkout.getText().toString().length() > 0) {
                    String checkInTime24 = convertTo24Hours(sp_checkin_time.getSelectedItem() + " " + sp_am_pm1.getSelectedItem());
                    String checkOutTime24 = convertTo24Hours(sp_checkout_time.getSelectedItem() + " " + sp_am_pm2.getSelectedItem());
                    int c = getTotalNights(txt_checkin.getText().toString() + " " + checkInTime24, txt_checkout.getText().toString() + " " + checkOutTime24);
                    {
                        Log.d("HGHghh5452", c + "");
                        totalNights.setText(c + "");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strUserName = editPersonName.getText().toString();
                String phone = editPhone.getText().toString();
                String address = editAddress.getText().toString();
                String noOfRooms = editNoofRooms.getText().toString();
                String price = editPrice.getText().toString();
                String totalPerson = editTextPerson.getText().toString();
                if (TextUtils.isEmpty(strUserName)) {
                    editPersonName.setError("Please enter yatri name");
                } else if (TextUtils.isEmpty(phone)) {
                    editPhone.setError("Please enter phone number");
                } else if (TextUtils.isEmpty(address)) {
                    editAddress.setError("Please enter address");
                } else if (TextUtils.isEmpty(noOfRooms)) {
                    editNoofRooms.setError("Please enter No. of rooms");

                } else if (TextUtils.isEmpty(price)) {
                    editPrice.setError("Please enter valid price");

                } else if (TextUtils.isEmpty(totalPerson)) {
                    editTextPerson.setError("Please enter Persons");

                } else {
                    if (Integer.parseInt(noOfRooms) > 0) {
                        BookingDetails bookingDetails = new BookingDetails();
                        bookingDetails.setYatriNm(strUserName);
                        bookingDetails.setYatriMobile(phone);

                        bookingDetails.setYatriAddress(address);
                        bookingDetails.setRoomTyp(spinnerRoomTypes.getSelectedItem().toString());

                        bookingDetails.setBookingId(String.valueOf(System.currentTimeMillis()));
                        bookingDetails.setDs_id(did);
                        bookingDetails.setSuggestion(tvBookingTot.getText().toString());
                        bookingDetails.setNoOfRooms(noOfRooms);
                        bookingDetails.setNights(totalNights.getText().toString());
                        bookingDetails.setPerson(editTextPerson.getText().toString());
                        bookingDetails.setBookingBookedOn(ConvertGMTtoIST.getCurrentDate());
                        RoomType roomType = (RoomType) spinnerRoomTypes.getSelectedItem();
                        Log.d("AJKJKKJ22", roomType.getRoom_id() + "==");

                        bookingDetails.setRoom_id(roomType.getRoom_id());
                        bookingDetails.setDs_name(sessionManager.getDharamshalanm());
                        Log.d("fdkfhdshfsdhsjh", txt_checkin.getText().toString() + "===" + txt_checkout.getText().toString());
                        if (sessionManager.geDharamshalaTimezoneSel().equalsIgnoreCase("fixed")) {
                            if (txt_checkin.getText().toString().length() > 0 && txt_checkout.getText().toString().length() > 0) {
                                bookingDetails.setExpectedCheckinTime(textViewHrs.getText().toString() + "");
                                String checkinTime = txt_checkin.getText() + "";
                                String checkoutTime = txt_checkout.getText() + "";

                                bookingDetails.setCheckinDt(checkinTime + "");
                                bookingDetails.setCheckoutDt(checkoutTime + "");
                                bookingDetails.setIs_24Hours("0");
                                isIntenrnet = NetworkUtil.checkInternetConnection(AddofflineBookingActivity.this);
                                if (isIntenrnet) {
                                    insertBookingApi(bookingDetails, 0);
                                } else {
                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Connect To Internet!!", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }

                            } else {
                                Log.d("fdkfhdshfsdhsjh", txt_checkin.getText().toString() + "1===" + txt_checkout.getText().toString());

                                Toast.makeText(AddofflineBookingActivity.this, "Please enter date !", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            if (txt_checkin.getText().toString().length() > 0 && txt_checkout.getText().toString().length() > 0) {

                                String checkinTime = txt_checkin.getText() + " " + spinnerCheckIn.getSelectedItem().toString() + sp_am_pm1.getSelectedItem().toString();
                                String checkoutTime = txt_checkout.getText() + " " + spinnerCheckOut.getSelectedItem().toString() + sp_am_pm2.getSelectedItem().toString();

                                bookingDetails.setCheckinDt(checkinTime + "");
                                bookingDetails.setCheckoutDt(checkoutTime + "");
                                bookingDetails.setIs_24Hours("1");

                                Bundle params = new Bundle();
                                // params.putString("dharamshala_name", sessionManager.getDharamshalanm() + "");
                                params.putString("user_detail", sessionManager.getUserDetailsOb().getDs_name() + " at " + ConvertGMTtoIST.getCurrentDateTimeLastSeen() + " check in : " + bookingDetails.getCheckinDt() + " checkout : " + bookingDetails.getCheckoutDt());
                                mFirebaseAnalytics.logEvent("Offline_Booking", params);
                                isIntenrnet = NetworkUtil.checkInternetConnection(AddofflineBookingActivity.this);
                                if (isIntenrnet) {
                                    insertBookingApi(bookingDetails, 1);
                                } else {
                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Connect To Internet!!", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }


                            } else {
                                Log.d("fdkfhdshfsdhsjh", txt_checkin.getText().toString() + "1===" + txt_checkout.getText().toString());

                                Toast.makeText(AddofflineBookingActivity.this, "Please enter date !", Toast.LENGTH_SHORT).show();

                            }
                        }
                    } else {
                        Toast.makeText(AddofflineBookingActivity.this, "please select no of rooms should be atleast 1 !", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        linearLayoutFixed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddofflineBookingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String AM_PM;
                        String hrs, mins;
                        if (selectedHour < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                        }
                        if (selectedHour < 10) {
                            hrs = "0" + selectedHour;
                        } else {
                            hrs = String.valueOf(selectedHour);

                        }
                        if (selectedMinute < 10) {
                            mins = "00";
                        } else {
                            mins = "00";

                        }

                        textViewHrs.setText(hrs + " : " + mins + " " + AM_PM);
                        sessionManager.setFixedTiming(did, hrs + ":" + mins + AM_PM);

                        //  textViewHrs.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, 00, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        Log.d("HJAHJHJH13213", sessionManager.geDharamshalaTimezoneSel() + "===");
        textViewTime.setText(sessionManager.geDharamshalaTimezoneSel() + "");
        if (sessionManager.getFixedTiming(did) != null) {
            textViewHrs.setText(sessionManager.getFixedTiming(did) + "");
        }
        if (sessionManager.geDharamshalaTimezoneSel().equalsIgnoreCase("fixed")) {
            is24Selected = false;
            ll_checkoutTime.setVisibility(View.GONE);
            ll_checkinTime.setVisibility(View.GONE);
            linearLayoutFixed.setVisibility(View.VISIBLE);

        } else {
            is24Selected = true;
            linearLayoutFixed.setVisibility(View.GONE);
            //linearLayoutTimePick.setVisibility(View.VISIBLE);
            ll_checkoutTime.setVisibility(View.VISIBLE);
            ll_checkinTime.setVisibility(View.VISIBLE);

        }

        linearLayoutEditTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddofflineBookingActivity.this);

                builder.setTitle("Select Timezone..");

                builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        switch (item) {
                            case 0:
                                boolean isTime = sessionManager.setDharamshalaTimezoneSel("24 Hrs.");
                                if (isTime) {
                                    is24Selected = true;
                                    linearLayoutFixed.setVisibility(View.GONE);
                                    //linearLayoutTimePick.setVisibility(View.VISIBLE);
                                    ll_checkoutTime.setVisibility(View.VISIBLE);
                                    ll_checkinTime.setVisibility(View.VISIBLE);
                                    textViewTime.setText(sessionManager.geDharamshalaTimezoneSel() + "");
                                    txt_checkin.setText("");
                                    txt_checkout.setText("");

                                }
                                // Toast.makeText(InventoryActivity.this, "First Item Clicked", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                boolean isTime1 = sessionManager.setDharamshalaTimezoneSel("Fixed");
                                if (isTime1) {
                                    is24Selected = false;
                                    ll_checkoutTime.setVisibility(View.GONE);
                                    ll_checkinTime.setVisibility(View.GONE);
                                    linearLayoutFixed.setVisibility(View.VISIBLE);
                                    textViewTime.setText(sessionManager.geDharamshalaTimezoneSel() + "");
                                }
                                //Toast.makeText(InventoryActivity.this, "Second Item Clicked", Toast.LENGTH_LONG).show();
                                break;

                        }
                        alertDialog1.dismiss();
                    }
                });
                alertDialog1 = builder.create();
                alertDialog1.show();
            }
        });

       /* RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGrpTimezone);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                //  View radioButton = group.findViewById(checkedId);
                switch (checkedId) {
                    case R.id.radioButtonIs24:
                        is24Selected = true;
                        linearLayoutFixed.setVisibility(View.GONE);
                        linearLayoutTimePick.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radioButtonFixed:
                        is24Selected = false;
                        linearLayoutTimePick.setVisibility(View.GONE);
                        linearLayoutFixed.setVisibility(View.VISIBLE);
                        break;

                }

            }
        });*/

       /* if (is24Selected) {
            linearLayoutFixed.setVisibility(View.GONE);
            linearLayoutTimePick.setVisibility(View.VISIBLE);

        } else {
            linearLayoutTimePick.setVisibility(View.GONE);
            linearLayoutFixed.setVisibility(View.VISIBLE);

        }*/

    }

    private void insertBookingApi(BookingDetails bookingDetails, int i) {
        showProgressDialog("Processing. Please Wait!");
        RequestQueue queue = Volley.newRequestQueue(AddofflineBookingActivity.this);
        String url = "";
        if (i == 1) {
            url = "http://192.168.0.105/manager_api/INSERTDISPLAYDATA/action/insert-manager-data.php?dharmshala_id=" + bookingDetails.getDs_id() + "&dharmshala_name=" + bookingDetails.getDs_name() + "&checkindate=" + txt_checkin.getText().toString() + "&checkoutdate=" + txt_checkout.getText().toString() + "&checkintime=" + spinnerCheckIn.getSelectedItem().toString() + "" + "&checkouttime=" + spinnerCheckOut.getSelectedItem().toString() + "" + "&checkintimezone=" + sp_am_pm1.getSelectedItem().toString() + "" + "&checkouttimezone=" + sp_am_pm2.getSelectedItem().toString() + "" + "&expected_datetime=" + bookingDetails.getExpectedCheckinTime() + "&is24=" + i + "&yatri_name=" + bookingDetails.getYatriNm() + "&yatri_contactnumber=" + bookingDetails.getYatriMobile() + "&yatri_address=" + bookingDetails.getYatriAddress() + "&nights=" + bookingDetails.getNights() + "&guests=" + bookingDetails.getPerson() + "&room_type=" + bookingDetails.getRoomTyp() + "&rooms=" + bookingDetails.getNoOfRooms() + "&room_price=" + editPrice.getText().toString() + "&total=" + bookingDetails.getSuggestion() + "&booked_by=" + sessionManager.getUserDetailsOb().getUsername()+ "&room_type_id=" +bookingDetails.getRoom_id();
        } else {
            url = "http://192.168.0.105/manager_api/INSERTDISPLAYDATA/action/insert-manager-data.php?dharmshala_id=" + bookingDetails.getDs_id() + "&dharmshala_name=" + bookingDetails.getDs_name() + "&checkindate=" + txt_checkin.getText().toString() + "&checkoutdate=" + txt_checkout.getText().toString() + "&checkintime=" +  "" + "&checkouttime="+"" + "&checkintimezone=" + "" + "&checkouttimezone=" +  "" + "&expected_datetime=" + txt_checkin.getText().toString()+" "+bookingDetails.getExpectedCheckinTime()+"-"+ txt_checkout.getText().toString()+" "+bookingDetails.getExpectedCheckinTime() + "&is24=" + i + "&yatri_name=" + bookingDetails.getYatriNm() + "&yatri_contactnumber=" + bookingDetails.getYatriMobile() + "&yatri_address=" + bookingDetails.getYatriAddress() + "&nights=" + bookingDetails.getNights() + "&guests=" + bookingDetails.getPerson() + "&room_type=" + bookingDetails.getRoomTyp() + "&rooms=" + bookingDetails.getNoOfRooms() + "&room_price=" + editPrice.getText().toString() + "&total=" + bookingDetails.getSuggestion() + "&booked_by=" + sessionManager.getUserDetailsOb().getUsername()+ "&room_type_id=" +bookingDetails.getRoom_id();

        }
        //  String addInfoURL = AppConstants.BASE_API_URL + "addinfo&number=" + usrnm + "&name=" + name1 + "&email=" + email1 + "&designation=" + dest + "&is_mobile=APP";
        Log.d("CHECKPARAMETERS", url + "");

        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("APICALLFLOW>>>", "-->" + "addinfo");
                hideProgressDialog();
                //  Toast.makeText(CreateUserActivity.this,response,Toast.LENGTH_LONG).show();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Log.d("CHECKRESPONSE", response.toString());
                    if (jsonObject.has("success")) {
                        if (jsonObject.getBoolean("success")) {
                            if (i == 0) {
                                /*long id_res = sqLiteHandler.addOfflineBookingDetails(bookingDetails, "offline");

                                if (id_res > 0) {
                                    Bundle params = new Bundle();
                                    // params.putString("dharamshala_name", sessionManager.getDharamshalanm() + "");
                                    params.putString("user_detail", sessionManager.getUserDetailsOb().getDs_name() + " at " + ConvertGMTtoIST.getCurrentDateTimeLastSeen() + " check in : " + bookingDetails.getCheckinDt() + " checkout : " + bookingDetails.getCheckoutDt());
                                    mFirebaseAnalytics.logEvent("Offline_Booking", params);
                                    Toast.makeText(AddofflineBookingActivity.this, "Booking Added Successfully !!", Toast.LENGTH_SHORT).show();
                                    String checkIn = bookingDetails.getCheckinDt().split(" ")[0];
                                    String checkout = bookingDetails.getCheckoutDt().split(" ")[0];
                                    ArrayList<String> datesBetwn = getCheckInDates(checkIn.trim(), checkout.trim());

                                    Log.d("HAJHJAJHJH", datesBetwn.size() + "==3");
                                    for (int i = 0; i < datesBetwn.size(); i++) {
                                        sqLiteHandler.checkOfflineInventoryIfexist(bookingDetails.getBookingId(), bookingDetails.getRoom_id(), datesBetwn.get(i), did, Integer.parseInt(bookingDetails.getNoOfRooms()));

                                    }*/
                                    Intent intent = new Intent(AddofflineBookingActivity.this, ViewOfflineBookingsActivity.class);
                                    startActivity(intent);
                                    finish();
                                /*} else {
                                    Toast.makeText(AddofflineBookingActivity.this, "Error in adding booking!!", Toast.LENGTH_SHORT).show();

                                }*/
                            } else {

                                /*long id_res = sqLiteHandler.addOfflineBookingDetails(bookingDetails, "offline");

                                if (id_res > 0) {
                                    Toast.makeText(AddofflineBookingActivity.this, "Booking Added Successfully !!", Toast.LENGTH_SHORT).show();
                                    String checkIn = bookingDetails.getCheckinDt().split(" ")[0];
                                    String checkout = bookingDetails.getCheckoutDt().split(" ")[0];
                                    ArrayList<String> datesBetwn = getCheckInDates(checkIn.trim(), checkout.trim());
                                    Log.d("HAJHJAJHJH", datesBetwn.size() + "==");
                                    for (int i = 0; i < datesBetwn.size(); i++) {
                                        // sqLiteHandler.checkOfflineRoomExist(bookingDetails.getRoom_id(), datesBetwn.get(i), did);
                                        sqLiteHandler.checkOfflineInventoryIfexist(bookingDetails.getBookingId(), bookingDetails.getRoom_id(), datesBetwn.get(i), did, Integer.parseInt(bookingDetails.getNoOfRooms()));

                                    }*/
                                    Intent intent = new Intent(AddofflineBookingActivity.this, ViewOfflineBookingsActivity.class);
                                    startActivity(intent);
                                    finish();
                              /*  } else {
                                    Toast.makeText(AddofflineBookingActivity.this, "Error in adding booking!!", Toast.LENGTH_SHORT).show();

                                }*/
                            }
                        } else {
                            if (jsonObject.has("message")) {

                                Toast.makeText(AddofflineBookingActivity.this, jsonObject.getString("message") + "", Toast.LENGTH_LONG).show();

                            } else
                                Toast.makeText(AddofflineBookingActivity.this, "Error Occured..", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AddofflineBookingActivity.this, "Please Contact Yatradham.org !!", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    hideProgressDialog();

                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Toast.makeText(AddofflineBookingActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
/*
                params.put("dharmshala_id", bookingDetails.getDs_id());
                params.put("dharmshala_name", bookingDetails.getDs_name());
                params.put("checkindate", bookingDetails.getCheckinDt());
                params.put("checkoutdate", bookingDetails.getCheckoutDt());

                params.put("checkintime", spinnerCheckIn.getSelectedItem().toString() + "");
                params.put("checkouttime", spinnerCheckOut.getSelectedItem().toString() + "");
                params.put("checkintimezone", sp_am_pm1.getSelectedItem().toString() + "");
                params.put("checkouttimezone", sp_am_pm2.getSelectedItem().toString() + "");
                params.put("expected_datetime", bookingDetails.getExpectedCheckinTime());

                params.put("is24", i + "");
                params.put("yatri_name", bookingDetails.getYatriNm());
                params.put("yatri_contactnumber", bookingDetails.getYatriMobile());
                params.put("yatri_address", bookingDetails.getYatriAddress());
                params.put("nights", bookingDetails.getNights());
                params.put("guests", bookingDetails.getPerson());
                params.put("room_type", bookingDetails.getRoomTyp());
                params.put("rooms", bookingDetails.getNoOfRooms());
                params.put("room_price", editPrice.getText().toString());
                params.put("total", bookingDetails.getSuggestion());
                params.put("booked_by", sessionManager.getUserDetailsOb().getUsername() + "");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    Log.d("HAJHJHJDHJDHJHj", key + "==>" + value);
                    // do stuff
                }*/

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
    }

    public void showDatePickerDialog(final int check) {
//        DialogFragment newFragment = new DatePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "datePicker");
        if (check == 1) {

            Calendar now = Calendar.getInstance();

            DatePickerDialog startDpd =
                    DatePickerDialog.newInstance(
                            this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)

                    );


//        21-7-2018
            startDpd.setMinDate(now);
            //   minAge.set(Calendar.YEAR, now.get(Calendar.YEAR));
            //startDpd.setMaxDate(calendarEndDate);

            // startDpd.setMaxDate(minAge);
            // startDpd.setMinDate();
            startDpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
            //startDpd.show(AddofflineBookingActivity.this, "Datepickerdialog");
            startDpd.show(getSupportFragmentManager(), "datepicker");

        } else if (check == 2) {

            Calendar now = Calendar.getInstance();
            DatePickerDialog startDpd =
                    DatePickerDialog.newInstance(
                            this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)

                    );

            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date11 = null;
            try {
                date11 = (Date) formatter.parse(dateeee);
                now.setTime(date11);
                startDpd.setMinDate(now);
            } catch (ParseException e) {
                e.printStackTrace();
            }


//        21-7-2018

            //   minAge.set(Calendar.YEAR, now.get(Calendar.YEAR));
            //startDpd.setMaxDate(calendarEndDate);

            // startDpd.setMaxDate(minAge);
            // startDpd.setMinDate();
            startDpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
            // startDpd.show(getActivity().getSupportFragmentManager(), "Datepickerdialog");
            startDpd.show(getSupportFragmentManager(), "datepicker");
//        Calendar now = Calendar.getInstance();
//        DatePickerDialog startDpd =
//                DatePickerDialog.newInstance(
//                        this,
//                        now.get(Calendar.YEAR),
//                        now.get(Calendar.MONTH),
//                        now.get(Calendar.DAY_OF_MONTH)
//
//                );
//        Calendar minAge = Calendar.getInstance();
//        Log.d(TAG, "showDatePickerDialog: "+minAge);
////        21-7-2018
//        startDpd.setMinDate(minAge);
//        //   minAge.set(Calendar.YEAR, now.get(Calendar.YEAR));
//        //startDpd.setMaxDate(calendarEndDate);
//
//        // startDpd.setMaxDate(minAge);
//        // startDpd.setMinDate();
//        startDpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
//        startDpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
        }


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = new StringBuilder().append(dayOfMonth).append("-").append(monthOfYear + 1).append("-").append(year).toString();
        dateeee = new StringBuilder().append(dayOfMonth + 1).append("-").append(monthOfYear + 1).append("-").append(year).toString();


        //set intput date for api params
        String date_input = new StringBuilder().append(year).append("-").append(monthOfYear + 1).append("-").append(dayOfMonth).toString();
        // txtBirthday.setText(date);

        String[] separated = date_input.split("-");
        Log.d("DKJKDSDJKDJD", separated[1] + "==" + separated[2] + "==" + separated[1].length());
        if (separated[1].length() == 1) {
            separated[1] = "0" + separated[1];
        }
        if (separated[2].length() == 1) {
            separated[2] = "0" + separated[2];
        }

        date_input = separated[0] + "-" + separated[1] + "-" + separated[2];
        Log.d("DKJKDSDJKDJD", separated[1] + "==" + separated[2] + "==" + separated[0]);
        String dt = separated[2] + "-" + separated[1] + "-" + separated[0];

        if (flag.equalsIgnoreCase("from_checkin")) {
            date_input_checkin = date_input;
            Log.d("HSAJHJSAHJSAH", "onDateSet: " + date);
            txt_checkin.setText(dt);
        } else if (flag.equalsIgnoreCase("from_checkout")) {
            date_input_checkout = date_input;
            txt_checkout.setText(dt);
        }
        flag = "";
        if (txt_checkin.getText().toString().length() > 0 && txt_checkout.getText().toString().length() > 0) {
            String checkInTime24 = convertTo24Hours(sp_checkin_time.getSelectedItem() + " " + sp_am_pm1.getSelectedItem());
            String checkOutTime24 = convertTo24Hours(sp_checkout_time.getSelectedItem() + " " + sp_am_pm2.getSelectedItem());
            int c = getTotalNights(txt_checkin.getText().toString() + " " + checkInTime24, txt_checkout.getText().toString() + " " + checkOutTime24);
            {
                Log.d("HGHghh5452", c + "");
                totalNights.setText(c + "");
            }
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

                Log.d("HAJHJHJHAJHA112", df1.format(cal1.getTime()) + "==");
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

    private int getTotalNights(String dateString1, String dateString2) {

        ArrayList<Date> dates = new ArrayList<Date>();
        ArrayList<String> datesFormatetd = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        datesFormatetd.clear();
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
        Log.d("HAJHJHJHAJHA113", dateString1 + "===" + dateString2);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        dates.add(cal1.getTime());
        while (!cal1.after(cal2)) {
            // dates.add(cal1.getTime());
            if (!dateString2.equals(df1.format(cal1.getTime()))) {
                datesFormatetd.add(df1.format(cal1.getTime()));

                Log.d("HAJHJHJHAJHA112", df1.format(cal1.getTime()) + "==");
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
        return datesFormatetd.size();
    }

    public String convertTo24Hours(String time) {
        String result = null;
        try {
            // String _24HourTime = "22:15";
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _12HourDt = _12HourSDF.parse(time);
            Log.d("ADJBHJHHD", time + "");
            result = _24HourSDF.format(_12HourDt);
            System.out.println(_12HourDt);
            System.out.println(_24HourSDF.format(_12HourDt));
            Log.d("BADHGHGHDG11", _12HourDt + "==>" + _24HourSDF.format(_12HourDt));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private ProgressDialog mProgressDialog;

    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(AddofflineBookingActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(msg);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            // mNextButton.setEnabled(true);
        }
    }
}
