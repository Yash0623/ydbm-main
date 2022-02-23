package com.ydbm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ydbm.App;
import com.ydbm.R;
import com.ydbm.models.BookingDetails;
import com.ydbm.session.SessionManager;
import com.ydbm.utils.ConvertGMTtoIST;



public class BookingDetailActivity extends AppCompatActivity {
    ImageView imageViewNavback;
    BookingDetails bookingDetails = null;
    TextView textViewCheckin, textViewCheckout, textViewNoRooms, textViewPersons, textViewroomType, textViewYatriNm, textViewNeftNo, textViewTransferDt, textViewNights, textViewContibution, textViewBookingId, textViewDs_name;
    TextView textViewExpectedCheckInTm, textViewYatriMob, textViewYatriAddress, textSubtTotal, textViewTransferAmt, textViewExpectedTitle, textViewbookedOn;
    private Tracker mTracker;
    SessionManager sessionManager;
    private FirebaseAnalytics mFirebaseAnalytics;
    private LinearLayout linearExtraMattressQty,linearExtraMattressTotal;
    private TextView tvMattressQty,tvTotalMattress,lblTotalExtraMattress,lblTotalExtraMattressPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);
        sessionManager = new SessionManager(BookingDetailActivity.this);
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        getData();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle1 = new Bundle();
        bundle1.putString("Name", sessionManager.getUserDetailsOb().getName()+"");
        bundle1.putString("View_By", bookingDetails.getBookingId()+" View by "+sessionManager.getUserDetailsOb().getUsername()+" at "+ ConvertGMTtoIST.getCurrentDateAndTime());
        bundle1.putString("Dharamshala_Name", sessionManager.getDharamshalanm()+"");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle1);
        mFirebaseAnalytics.logEvent("View_Booking_Detail", bundle1);
        initViews();
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingDetails = bundle.getParcelable("bookingdetail");
        }
    }

    private void initViews() {
        imageViewNavback = findViewById(R.id.imageviewNavBack);
        textViewCheckin = findViewById(R.id.tv_date_check_in);
        textViewCheckout = findViewById(R.id.tv_date_check_out);
        textViewNoRooms = findViewById(R.id.tv_rooms_number);
        textViewPersons = findViewById(R.id.tv_persons_number);
        textViewroomType = findViewById(R.id.room_type_name);
        textViewYatriNm = findViewById(R.id.tv_yatriNm);
        textViewDs_name = findViewById(R.id.textViewDNm);
        textViewBookingId = findViewById(R.id.tv_BookingId);
        textViewNeftNo = findViewById(R.id.tvNeft_No);
        textViewTransferDt = findViewById(R.id.tv_tranferDt);
        textViewYatriAddress = findViewById(R.id.tvYatriAddress_value);
        textViewYatriMob = findViewById(R.id.tvYatriMobNo_value);
        textSubtTotal = findViewById(R.id.tvTotalAmtValue);
        textViewContibution = findViewById(R.id.tvContributionValue);
        textViewNights = findViewById(R.id.tv_NoNightsValue);
        textViewbookedOn = findViewById(R.id.tv_bookedon_value);
        textViewExpectedCheckInTm = findViewById(R.id.tvExpectedTimeValue);
        textViewTransferAmt = findViewById(R.id.tv_tranferAmount);
        textViewExpectedTitle = findViewById(R.id.tvExpectedTimeTitle);
        linearExtraMattressQty = findViewById(R.id.linearExtraMattressQty);
        linearExtraMattressTotal = findViewById(R.id.linearExtraMattressTotal);
        tvMattressQty = findViewById(R.id.tvMattressQty);
        lblTotalExtraMattress = findViewById(R.id.lblTotalExtraMattress);
        tvTotalMattress = findViewById(R.id.tvTotalMattress);
        lblTotalExtraMattressPrice = findViewById(R.id.lblTotalExtraMattressPrice);

        imageViewNavback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });
        if (bookingDetails != null) {
            if (bookingDetails.getBookingBookedOn() != null) {
                String dt[] = bookingDetails.getBookingBookedOn().split(" ");
                textViewbookedOn.setText(dt[0]);
            }
            //put 24hrs condition
            if (bookingDetails.getYatriAddress() != null) {
                textViewYatriAddress.setText(bookingDetails.getYatriAddress());
            }
            if (bookingDetails.getYatriMobile() != null) {
                textViewYatriMob.setText(bookingDetails.getYatriMobile());
            }
            if (bookingDetails.getTranferAmt() != null) {
                if (bookingDetails.getTranferAmt().equals("null")) {
                    textViewTransferAmt.setText("NA");
                } else
                    textViewTransferAmt.setText(bookingDetails.getTranferAmt());
            } else {
                textViewTransferAmt.setText("NA");
            }
            if (bookingDetails.getNights() != null) {
                textViewNights.setText(bookingDetails.getNights());
            }
            if (bookingDetails.getBookingTotal() != null) {
                textSubtTotal.setText((bookingDetails.getBookingTotal()));
            }
            if (bookingDetails.getIs_24Hours() != null) {
                Log.d("KSJKJKSKK", bookingDetails.getIs_24Hours() + "===>" + bookingDetails.getExpectedCheckinTime());
                if (Integer.parseInt(bookingDetails.getIs_24Hours()) == 1) {
                    if (bookingDetails.getExpectedCheckinTime().isEmpty()) {
                        textViewExpectedCheckInTm.setVisibility(View.GONE);
                        textViewExpectedTitle.setVisibility(View.GONE);

                    } else {
                        textViewExpectedCheckInTm.setVisibility(View.GONE);
                        textViewExpectedTitle.setVisibility(View.GONE);
                    }

                } else {
                    textViewExpectedCheckInTm.setVisibility(View.VISIBLE);
                    textViewExpectedTitle.setVisibility(View.VISIBLE);
                    textViewExpectedCheckInTm.setText(bookingDetails.getExpectedCheckinTime());


                }
            }

            //for Extra Mattress

            if(!TextUtils.isEmpty(bookingDetails.getExtra_mattress_label())){
                lblTotalExtraMattress.setText(bookingDetails.getExtra_mattress_label());
                lblTotalExtraMattressPrice.setText(bookingDetails.getExtra_mattress_label());
            }

            if(TextUtils.isEmpty(bookingDetails.getExtra_mattress_status())){
                linearExtraMattressQty.setVisibility(View.GONE);
                linearExtraMattressTotal.setVisibility(View.GONE);

            }else{
                if(bookingDetails.getExtra_mattress_status().contentEquals("1")){
                    tvMattressQty.setText(bookingDetails.getNo_of_mattress());
                    tvTotalMattress.setText(bookingDetails.getTotal_mattress_price());
                    linearExtraMattressQty.setVisibility(View.VISIBLE);
                    linearExtraMattressTotal.setVisibility(View.VISIBLE);
                }else{
                    linearExtraMattressQty.setVisibility(View.GONE);
                    linearExtraMattressTotal.setVisibility(View.GONE);
                }


            }

            if (bookingDetails.getCheckinDt() != null)
                textViewCheckin.setText(bookingDetails.getCheckinDt());
            if (bookingDetails.getCheckoutDt() != null)
                textViewCheckout.setText(bookingDetails.getCheckoutDt());
            if (bookingDetails.getPerson() != null)
                textViewPersons.setText(bookingDetails.getPerson());
            if (bookingDetails.getNoOfRooms() != null)
                textViewNoRooms.setText(bookingDetails.getNoOfRooms());
            if (bookingDetails.getRoomTyp() != null)
                textViewroomType.setText(bookingDetails.getRoomTyp());
            if (bookingDetails.getBookingId() != null)
                textViewBookingId.setText(bookingDetails.getBookingId());
            if (bookingDetails.getYatriNm() != null)
                textViewYatriNm.setText(bookingDetails.getYatriNm());
            if (bookingDetails.getNeftNo() != null) {
                if (bookingDetails.getNeftNo().equals("null")) {
                    textViewNeftNo.setText("NA");
                } else
                    textViewNeftNo.setText(bookingDetails.getNeftNo());
            } else {
                textViewNeftNo.setText(bookingDetails.getNeftNo());
            }
            if (bookingDetails.getTranferDt() != null) {
                if (bookingDetails.getTranferDt().equals("null")) {
                    textViewTransferDt.setText("NA");
                } else
                    textViewTransferDt.setText(bookingDetails.getTranferDt());
            } else {
                textViewTransferDt.setText(bookingDetails.getTranferDt());
            }
            if (bookingDetails.getDs_name() != null)
                textViewDs_name.setText(bookingDetails.getDs_name());
            if (bookingDetails.getContribution() != null)
                textViewContibution.setText(bookingDetails.getContribution());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* mTracker.setScreenName("BookingDetail");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Show Booking Detail")
                .setAction("Screen View")
                .setCustomDimension(0, sessionManager.getUserDetailsOb().getUsername() + "")
                .setCustomDimension(1, sessionManager.getUserDetailsOb().getName() + "")
                .setCustomDimension(2, bookingDetails.getBookingId() + "")
                .build());
*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
