package com.ydbm.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ydbm.R;
import com.ydbm.phoneverification.Authentication;
import com.ydbm.session.SessionManager;
import com.ydbm.models.User;
import com.ydbm.utils.AppConstants;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.InternetStatus;
import com.ydbm.utils.NetworkStateReciver;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;
import java.util.Map;


/**
 * Created by maitri.
 */

public class CreateUserActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String tag = CreateUserActivity.class.getSimpleName();
    Bundle bundle;
    String username;
    private Button mNextButton;
    private static final String TOKEN_PREF_NAME = "TOKEN_PREF";
    private FirebaseAuth mAuth;
    String fcm_token;
    private EditText mUsernameET;
    private EditText name, email, designation;
    ProgressDialog progress;
    SessionManager sessionManager;
    RelativeLayout relativeLayout;
    User userMain;
    String ds_idmain, ds_nameMain;
    SQLiteHandler sqLiteHandler;
    String mUserName, mName, mDesignation, mEmail;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser currentUser;
    private boolean isIntenrnet = false;
    private NetworkStateReciver networkStateReceiver;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        relativeLayout = findViewById(R.id.relativeHead);
        findViewById(R.id.nextBtn).setOnClickListener(this);
        sessionManager = new SessionManager(CreateUserActivity.this);
        sqLiteHandler = new SQLiteHandler(CreateUserActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences(TOKEN_PREF_NAME, 0);

        fcm_token = sharedPreferences.getString("token", null);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        initViews();

    }


    private void initViews() {
        mNextButton = (Button) findViewById(R.id.nextBtn);
        mNextButton.setOnClickListener(this);
        mUsernameET = (EditText) findViewById(R.id.number);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        designation = (EditText) findViewById(R.id.designation);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            username = bundle.getString("username");
        }
        mUsernameET.setText(username);
        // mUsernameET.setText("8320592838");
        isIntenrnet = NetworkUtil.checkInternetConnection(CreateUserActivity.this);
        if (isIntenrnet) {
            callMatchUserAPI(currentUser);
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Connect To Internet!!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextBtn:
               /* progress = new ProgressDialog(CreateUserActivity.this);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();*/
                isIntenrnet = new InternetStatus().isInternetOn(CreateUserActivity.this);
                if (isIntenrnet) {
                    mNextButton.setEnabled(false);
                    onNextButtonClicked();
                } else {

                }

                // Toast.makeText(CreateUserActivity.this,"Please Wait Two Minutes",Toast.LENGTH_LONG).show();

//                Intent intent = new Intent(this, SplashScreen.class);
//                startActivity(intent);
                break;
        }

    }


    private void onNextButtonClicked() {

        mUserName = mUsernameET.getText().toString().trim();
        mEmail = email.getText().toString().trim();
        mName = name.getText().toString().trim();
        mDesignation = designation.getText().toString().trim();


        if (!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mEmail) && !TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mDesignation)) {
            if ((Patterns.EMAIL_ADDRESS.matcher(mEmail).matches())) {
                //onAuthSuccess(currentUser);
                isIntenrnet = NetworkUtil.checkInternetConnection(CreateUserActivity.this);
                if (isIntenrnet) {
                    callAddInfoAPI(userMain);
                } else {
                    Toast.makeText(CreateUserActivity.this, "Please enable interner connection!", Toast.LENGTH_SHORT).show();
                }
            } else {
                mNextButton.setEnabled(true);
                Toast.makeText(this, "Invalid Email Address.", Toast.LENGTH_LONG).show();
            }

        } else {
            mNextButton.setEnabled(true);
            Toast.makeText(this, "Empty Fields Not Allowed", Toast.LENGTH_LONG).show();
        }
    }


    private void callMatchUserAPI(final FirebaseUser firebaseUser) {
        User user = new User();
        showProgressDialog("Processing. Please Wait!");
        RequestQueue queue = Volley.newRequestQueue(CreateUserActivity.this);
        Log.d("CHECKPARAMS123", AppConstants.BASE_API_URL + "matchuser&mobilenumber=" + username + "&fid=" + firebaseUser.getUid() + "&fcm_id=" + fcm_token + "&is_mobile=APP");
        StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "matchuser&mobilenumber=" + username + "&fid=" + firebaseUser.getUid() + "&fcm_id=" + fcm_token + "&is_mobile=APP", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                Log.d("APICALLFLOW>>>", "-->" + "matchuser");

                //  Toast.makeText(CreateUserActivity.this,response,Toast.LENGTH_LONG).show();
                JSONObject jsonObject = null;

                try {
                    Log.d("CHECKRESPONSE123", response.toString());
                    jsonObject = new JSONObject(response);
                    String ds_id = "0";
                    String ds_name = "";

                    if (jsonObject.has("status")) {
                        if (jsonObject.getBoolean("status")) {
                            if (jsonObject.has("info")) {
                                JSONObject object = jsonObject.getJSONObject("info");


                                String nm = object.getString("name");
                                String email1 = object.getString("email");
                                String designation1 = object.getString("designation");

                                user.setUsername(username);
                                user.setName(nm);
                                user.setDesignation(designation1);
                                user.setEmail(email1);

                                mUsernameET.setText(username);
                                name.setText(nm);
                                email.setText(email1);
                                designation.setText(designation1);
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
                                        sessionManager.setDharamshala_id(dsidsListarry.get(0), dsiNmsListarry.get(0), "1");
                                    }
                                    if (dsidsListarry.size() > 0) {
                                        ds_id = dsidsListarry.get(0);
                                    }
                                    if (dsiNmsListarry.size() > 0) {
                                        ds_name = dsiNmsListarry.get(0);
                                    }
                                } else {
                                    Log.d("ANJJJKJASHJKAS", "==" + "5");
                                    sessionManager.setIsMultipleAccount(false);
                                    String ds_id1 = jsonObject.getString("ds_id");
                                    String ds_name1 = jsonObject.getString("ds_name");
                                    sessionManager.setDharamshala_id(ds_id1, ds_name1, "2");

                                }
                            } else {
                                sessionManager.setIsMultipleAccount(false);
                                ds_id = jsonObject.getString("ds_id");
                                ds_name = jsonObject.getString("ds_name");
                                sessionManager.setDharamshala_id(ds_id, ds_name, "3");

                            }
                            userMain = user;
                            ds_idmain = ds_id;
                            ds_nameMain = ds_name;
                            //   callAddInfoAPI(user, ds_id, ds_name);
                        } else {
                            hideProgressDialog();
                            Toast.makeText(CreateUserActivity.this, "Please Contact Yatradham.org !!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(CreateUserActivity.this, Authentication.class));
                            finish();
                        }
                    } else {
                        hideProgressDialog();
                        Toast.makeText(CreateUserActivity.this, "Please Contact Yatradham.org !!", Toast.LENGTH_LONG).show();
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
                hideProgressDialog();
                Toast.makeText(CreateUserActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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

    private void callAddInfoAPI(final User user) {
        showProgressDialog("Processing. Please Wait!");
        RequestQueue queue = Volley.newRequestQueue(CreateUserActivity.this);

        String name1 = mName;
        name1 = name1.trim();
        name1 = name1.replaceAll("\\s+", "");
        String email1 = mEmail;
        email1 = email1.trim();
        email1 = email1.replaceAll("\\s+", "");
        String dest = mDesignation;
        dest = dest.trim();
        dest = dest.replaceAll("\\s+", "");
        String usrnm = mUserName.replaceAll("\\s+", "");
        String addInfoURL = AppConstants.BASE_API_URL + "addinfo&number=" + usrnm + "&name=" + name1 + "&email=" + email1 + "&designation=" + dest + "&is_mobile=APP";
        Log.d("CHECKPARAMETERS", addInfoURL + "");

        StringRequest sr = new StringRequest(Request.Method.POST, addInfoURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("APICALLFLOW>>>", "-->" + "addinfo");
                hideProgressDialog();
                //  Toast.makeText(CreateUserActivity.this,response,Toast.LENGTH_LONG).show();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Log.d("CHECKRESPONSE", response.toString());
                    if (jsonObject.has("status")) {
                        if (jsonObject.getString("status").equalsIgnoreCase("Success")) {

                            final String role = jsonObject.getString("role");
                            user.setUsertype(role);
                            user.setFcm_id(fcm_token);
                            user.setUsername(username);

                            Log.d("ANJJJKJASHJKAS", "==" + "8");

                            sessionManager.createLoginSession(user);
                            mFirebaseAnalytics = FirebaseAnalytics.getInstance(CreateUserActivity.this);
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("user_detail", username + " at " + ConvertGMTtoIST.getCurrentDateAndTime());
                            bundle1.putString("Dharamshala_Name", sessionManager.getDharamshalanm());
                            mFirebaseAnalytics.logEvent("Sign_Up", bundle1);
                            Intent intent = new Intent(CreateUserActivity.this, MainScreen.class);
                            //  intent.putExtra("from", 1);

                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CreateUserActivity.this, "Please Contact Yatradham.org !!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(CreateUserActivity.this, "Please Contact Yatradham.org !!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(CreateUserActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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


    private ProgressDialog mProgressDialog;

    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(CreateUserActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(msg);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mNextButton.setEnabled(true);
        }
    }


}
