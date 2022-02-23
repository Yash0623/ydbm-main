package com.ydbm.phoneverification;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.shuhart.stepview.StepView;
import com.ydbm.R;
import com.ydbm.activities.CreateUserActivity;
import com.ydbm.activities.ProfileScreenActivity;
import com.ydbm.utils.AppConstants;
import com.ydbm.utils.InternetStatus;
import com.ydbm.utils.NetworkStateReciver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Authentication extends AppCompatActivity {

    private int currentStep = 0;
    LinearLayout layout1, layout2, layout3;
    StepView stepView;
    AlertDialog dialog_verifying, profile_dialog;
    private static String uniqueIdentifier = null;
    private static final String UNIQUE_ID = "UNIQUE_ID";
    private static final long ONE_HOUR_MILLI = 60 * 60 * 1000;
    private static final String TAG = "FirebasePhoneNumAuth";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String phoneNumber;
    private Button sendCodeButton;
    private Button verifyCodeButton;
    private Button signOutButton;
    private Button button3;
    private EditText phoneNum;
    private PinView verifyCodeET;
    private TextView phonenumberText;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


    private FirebaseAuth mAuth;
    private boolean isIntenrnet=false;
    private NetworkStateReciver networkStateReceiver;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("IN");
        mAuth.useAppLanguage();
        layout1 = (LinearLayout) findViewById(R.id.layout1);
        layout2 = (LinearLayout) findViewById(R.id.layout2);
        layout3 = (LinearLayout) findViewById(R.id.layout3);
        sendCodeButton = (Button) findViewById(R.id.submit1);
        verifyCodeButton = (Button) findViewById(R.id.submit2);
        button3 = (Button) findViewById(R.id.submit3);
        phoneNum = (EditText) findViewById(R.id.phonenumber);
        verifyCodeET = (PinView) findViewById(R.id.pinView);
        phonenumberText = (TextView) findViewById(R.id.phonenumberText);

        stepView = findViewById(R.id.step_view);
        stepView.setStepsNumber(3);
        stepView.go(0, true);
        layout1.setVisibility(View.VISIBLE);

        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isIntenrnet = new InternetStatus().isInternetOn(Authentication.this);
                if (isIntenrnet) {
                    checkNumberIfExists();
                }

            else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Connect To Internet!!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                //Toast.makeText(Authentication.this, "Please Connect To Internet!!", Toast.LENGTH_SHORT).show();
            }

            }


        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                String code = credential.getSmsCode();
                if (code != null) {
                    verifyCodeET.setText(code);
                }
                //  Toast.makeText(Authentication.this,"dipaksolanki"+phoneNum.getText().toString(),Toast.LENGTH_LONG).show();

                signInWithPhoneAuthCredential(credential);


                //  signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(Authentication.this, "Invalid Request !! Please Try Again !", Toast.LENGTH_SHORT).show();
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(Authentication.this, "Too Many Request for OTP !! Please Try Again later !", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(Authentication.this, "Verification Failed !! Please Try Again later !", Toast.LENGTH_SHORT).show();

                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };
        verifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String verificationCode = verifyCodeET.getText().toString();
                if (verificationCode.isEmpty()) {
                    Toast.makeText(Authentication.this, "Enter verification code", Toast.LENGTH_SHORT).show();
                } else {

                 /*   LayoutInflater inflater = getLayoutInflater();
                    View alertLayout= inflater.inflate(R.layout.processing_dialog,null);
                    AlertDialog.Builder show = new AlertDialog.Builder(Authentication.this);

                    show.setView(alertLayout);
                    show.setCancelable(false);
                    dialog_verifying = show.create();
                    dialog_verifying.show();*/


                    try {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    } catch (Exception e) {
                        Toast toast = Toast.makeText(Authentication.this, "Verification Code is wrong!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }


                }
            }
        });

      /*  button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentStep < stepView.getStepCount() - 1) {
                    currentStep++;
                    stepView.go(currentStep, true);
                } else {
                    stepView.done(true);
                }
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.profile_create_dialog, null);
                AlertDialog.Builder show = new AlertDialog.Builder(Authentication.this);
                show.setView(alertLayout);
                show.setCancelable(false);
                profile_dialog = show.create();
                profile_dialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        profile_dialog.dismiss();
                        startActivity(new Intent(Authentication.this, ProfileScreenActivity.class));
                        finish();
                    }
                }, 3000);
            }
        });*/

    }

    private void checkNumberIfExists() {
        RequestQueue queue = Volley.newRequestQueue(Authentication.this);
        Log.d("sgdgshg",AppConstants.BASE_API_URL + "checknumber&mobilenumber=" + phoneNum.getText().toString() + "&is_mobile=APP");
        StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "checknumber&mobilenumber=" + phoneNum.getText().toString() + "&is_mobile=APP", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("APICALLFLOW>>>","-->" +"checknumber");
                //  Toast.makeText(Authentication.this,response,Toast.LENGTH_LONG).show();
                Log.d("CHECKNUMBERRESPONSE", response.toString());
                if (response.equals("true")) {
                    String phoneNumber = "+91" + phoneNum.getText().toString();
                    phonenumberText.setText(phoneNumber);

                    if (TextUtils.isEmpty(phoneNumber)) {
                        phoneNum.setError("Enter a Phone Number");
                        phoneNum.requestFocus();
                    } else if (phoneNumber.length() < 10) {
                        phoneNum.setError("Please enter a valid phone");
                        phoneNum.requestFocus();
                    } else {

                        if (currentStep < stepView.getStepCount() - 1) {
                            currentStep++;
                            stepView.go(currentStep, true);
                        } else {
                            stepView.done(true);
                        }
                        layout1.setVisibility(View.GONE);
                        layout2.setVisibility(View.VISIBLE);


                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                Authentication.this,               // Activity (for callback binding)
                                mCallbacks);        // OnVerificationStateChangedCallbacks
                    }
                } else {
                    Toast.makeText(Authentication.this, "Please Contact Yatradham.org", Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                String errorMsg = "";
                if(response != null && response.data != null){
                    String errorString = new String(response.data);
                    Log.i("log error", errorString);
                }
                 Toast.makeText(Authentication.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phoneNum", phoneNum.getText().toString());


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

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                         /*   dialog_verifying.dismiss();
                            if (currentStep < stepView.getStepCount() - 1) {
                                currentStep++;
                                stepView.go(currentStep, true);
                            } else {
                                stepView.done(true);
                            }
                            layout1.setVisibility(View.GONE);
                            layout2.setVisibility(View.GONE);
                            layout3.setVisibility(View.VISIBLE);
                            */
                            Intent intent = new Intent(Authentication.this,  CreateUserActivity.class);
                            intent.putExtra("username", phoneNum.getText().toString());

                            startActivity(intent);
                            finish();
                            // ...
                        } else {
                            if (dialog_verifying != null)
                                dialog_verifying.dismiss();
                            Toast.makeText(Authentication.this, "Something wrong", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }


}
