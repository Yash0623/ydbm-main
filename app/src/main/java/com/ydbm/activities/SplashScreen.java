package com.ydbm.activities;


import android.content.Intent;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ydbm.R;
import com.ydbm.phoneverification.Authentication;
import com.ydbm.session.SessionManager;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.InternetStatus;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    SessionManager sessionManager;
    private boolean isIntenrnet;
    String isFrom = "user";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        sessionManager = new SessionManager(SplashScreen.this);
        mAuth = FirebaseAuth.getInstance();



        FirebaseUser currentUser = mAuth.getCurrentUser();
     /*   networkStateReceiver = new NetworkStateReciver();
        networkStateReceiver.addListener(SplashScreen.this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));*/
        Bundle b = getIntent().getExtras();

        if (currentUser != null && sessionManager.isLoggedIn()) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            Bundle bundle1 = new Bundle();
            bundle1.putString("Name", sessionManager.getUserDetailsOb().getName());
            bundle1.putString("user_detail", sessionManager.getUserDetailsOb().getUsername()+" at "+ ConvertGMTtoIST.getCurrentDateAndTime());
            bundle1.putString("Dharamshala_Name", sessionManager.getDharamshalanm());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle1);


            //Sets whether analytics collection is enabled for this app on this device.
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

            //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 20 seconds just for the fun
            mFirebaseAnalytics.setMinimumSessionDuration(1000);
            mFirebaseAnalytics.setUserProperty("Name",sessionManager.getUserDetailsOb().getName()+"");
            mFirebaseAnalytics.setUserProperty("Phone_Number",sessionManager.getUserDetailsOb().getUsername()+"");
            mFirebaseAnalytics.setUserProperty("Dharamshala_Name",sessionManager.getUserDetailsOb().getDs_name()+"");

            //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).

            Intent intent = null;

            if (b != null && b.get("booking_id") != null) {
                if (b.get("message_type1").equals("booking")) {
                    if (isFrom.equalsIgnoreCase("user")) {
                        isIntenrnet = new InternetStatus().isInternetOn(SplashScreen.this);
                        if (!isIntenrnet) {
                            intent = new Intent(SplashScreen.this, BookingListingActivity.class).putExtra("fromuser", isFrom);
                        }
                        else{
                            intent = new Intent(SplashScreen.this, MainScreen.class).putExtra("fromuser", isFrom);
                        }
                    }else {
                        intent = new Intent(SplashScreen.this, AdminActivity.class).putExtra("fromuser",isFrom);

                    }
                } else {
                    if (isFrom.equalsIgnoreCase("user")) {
                        isIntenrnet = new InternetStatus().isInternetOn(SplashScreen.this);
                        if (!isIntenrnet) {
                            intent = new Intent(SplashScreen.this, BookingListingActivity.class).putExtra("fromuser", isFrom);
                        }
                        else{
                            intent = new Intent(SplashScreen.this, MainScreen.class).putExtra("fromuser", isFrom);
                        }
                    }
                    else {
                        intent = new Intent(SplashScreen.this, AdminActivity.class).putExtra("fromuser",isFrom);
                    }

                }
            } else {
                if (isFrom.equalsIgnoreCase("user")) {
                    isIntenrnet = new InternetStatus().isInternetOn(SplashScreen.this);
                    if (!isIntenrnet) {
                        intent = new Intent(SplashScreen.this, BookingListingActivity.class).putExtra("fromuser", isFrom);
                    }
                    else{
                        intent = new Intent(SplashScreen.this, MainScreen.class).putExtra("fromuser", isFrom);
                    }
                }
                else {
                    intent = new Intent(SplashScreen.this, AdminActivity.class).putExtra("fromuser",isFrom);

                }
            }
            if (b != null && b.get("booking_id") != null) {
                Log.d("cites@123", b.get("booking_id") + "===");
                intent.putExtra("enq_id", b.get("booking_id").toString());
            }
            startActivity(intent);
            finish();
        } else {
            isIntenrnet = new InternetStatus().isInternetOn(SplashScreen.this);
            if (!isIntenrnet) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Connect To Internet!!", Snackbar.LENGTH_LONG);
                snackbar.show();
                //Toast.makeText(Authentication.this, "Please Connect To Internet!!", Toast.LENGTH_SHORT).show();
            } else {
                new Handler().postDelayed(new Runnable() {

                    /*
                     * Showing splash screen with a timer. This will be useful when you
                     * want to show case your app logo / company
                     */

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        if (isFrom.equalsIgnoreCase("user")) {
                        startActivity(new Intent(SplashScreen.this, Authentication.class).putExtra("fromuser",isFrom));
   //                        startActivity(new Intent(SplashScreen.this, AdminActivity.class).putExtra("fromuser",isFrom));
                        } else {
                            startActivity(new Intent(SplashScreen.this, AdminActivity.class).putExtra("fromuser",isFrom));

                        }
                        // close this activity
                        finish();
                    }
                }, 2000);

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main1);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
