package com.ydbm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.ydbm.models.User;
import com.ydbm.models.UserDetails;
import com.ydbm.session.SessionManager;
import com.ydbm.utils.AppConstants;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private EditText mUsernameET;
    private EditText name, email, designation;
    private SQLiteHandler sqLiteHandler;
    private Button mNextButton;
    String mUserName, mName, mDesignation, mEmail;
    private SessionManager sessionManager;
    private boolean isIntenrnet;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initToolbar();
        sessionManager = new SessionManager(EditProfileActivity.this);
        sqLiteHandler = new SQLiteHandler(EditProfileActivity.this);
        initViews();
    }

    private void initViews() {
        mNextButton = (Button) findViewById(R.id.nextBtn);
        mUsernameET = (EditText) findViewById(R.id.number);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        designation = (EditText) findViewById(R.id.designation);

        User userDetails = sessionManager.getUserDetailsOb();
        mUsernameET.setText(userDetails.getUsername());
        name.setText(userDetails.getName());
        email.setText(userDetails.getEmail());
        designation.setText(userDetails.getDesignation());


        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserName = mUsernameET.getText().toString().trim();
                mEmail = email.getText().toString().trim();
                mName = name.getText().toString().trim();
                mDesignation = designation.getText().toString().trim();

                if (!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mEmail) && !TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mDesignation)) {
                    if ((Patterns.EMAIL_ADDRESS.matcher(mEmail).matches())) {
                        //onAuthSuccess(currentUser);
                        isIntenrnet = NetworkUtil.checkInternetConnection(EditProfileActivity.this);
                        if (isIntenrnet) {
                            callAddInfoAPI();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Please enable interner connection!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mNextButton.setEnabled(true);
                        Toast.makeText(EditProfileActivity.this, "Invalid Email Address.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    mNextButton.setEnabled(true);
                    Toast.makeText(EditProfileActivity.this, "Empty Fields Not Allowed", Toast.LENGTH_LONG).show();
                }
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
    }

    private void callAddInfoAPI() {
        showProgressDialog("Processing. Please Wait!");
        RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);

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

        String finalDest = dest;
        String finalEmail = email1;
        String finalName = name1;
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

                            Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully!!", Toast.LENGTH_LONG).show();
                            sessionManager.editUserDetails(finalName, finalEmail, finalDest);
                            Intent intent = new Intent(EditProfileActivity.this, ProfileScreenActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Please Contact Yatradham.org !!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Please Contact Yatradham.org !!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(EditProfileActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
            mProgressDialog = new ProgressDialog(EditProfileActivity.this);
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
