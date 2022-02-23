package com.ydbm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ydbm.R;
import com.ydbm.session.SessionManager;
import com.ydbm.models.User;
import com.ydbm.utils.ConvertGMTtoIST;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ProfileScreenActivity extends AppCompatActivity {
    TextView txtName, txtUserType, txtDesignation, txtEmail, txtPhone, txtVersion, txtSwitchAcc, textViewEditProfile;
    SessionManager sessionManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        sessionManager = new SessionManager(ProfileScreenActivity.this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ProfileScreenActivity.this);

        initToolbar();
        initViews();


        User user = sessionManager.getUserDetailsOb();
        if (user != null) {
            if (!user.getName().isEmpty() && user.getName() != null) {
                txtName.setText(user.getName());
            }
            if (!sessionManager.getRole().isEmpty() && sessionManager.getRole() != null) {
                if (sessionManager.getRole().equals("0")) {
                    txtUserType.setText("Manager");
                } else if (sessionManager.getRole().equals("1")) {
                    txtUserType.setText("Admin");
                } else if (sessionManager.getRole().equals("2")) {
                    txtUserType.setText("Copy");
                }

            }
            if (!user.getDesignation().isEmpty() && user.getDesignation() != null) {
                txtDesignation.setText(user.getDesignation());
            }
            if (!user.getEmail().isEmpty() && user.getEmail() != null) {
                txtEmail.setText(user.getEmail());
            }
            if (!user.getUsername().isEmpty() && user.getUsername() != null) {
                txtPhone.setText(user.getUsername());
            }
        }
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            if (version != null) {
                txtVersion.setText("App Version : " + version);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        txtName = findViewById(R.id.textView);
        textViewEditProfile = findViewById(R.id.textVieweditProf);
        txtUserType = findViewById(R.id.textView2);
        txtDesignation = findViewById(R.id.textView7);
        txtSwitchAcc = findViewById(R.id.textViewSwitchUsr);
        txtEmail = findViewById(R.id.textView9);
        txtPhone = findViewById(R.id.textView11);
        txtVersion = findViewById(R.id.textViewAppVesrion);
        if (sessionManager.getIsmultiple()) {
            txtSwitchAcc.setVisibility(View.VISIBLE);
        } else {
            txtSwitchAcc.setVisibility(View.GONE);
        }
        txtSwitchAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showBottomSheetDialog();
            }
        });
        textViewEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileScreenActivity.this, EditProfileActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showBottomSheetDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.switch_account, null);
        final RadioGroup radioGroup = view.findViewById(R.id.radioGrp6);
        ImageView imgCancel = view.findViewById(R.id.imageCancel);
        Button buttonContinue = view.findViewById(R.id.btnContinue);

        final BottomSheetDialog dialogBottomSheet = new BottomSheetDialog(ProfileScreenActivity.this);
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBottomSheet.dismiss();
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldDsnm=sessionManager.getDharamshalanm();
                dialogBottomSheet.dismiss();
                int checkedId = radioGroup.getCheckedRadioButtonId();

                // Log.d("AHJHJHj12223", checkedId + "==");
                String text = "";
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    RadioButton btn = (RadioButton) radioGroup.getChildAt(i);
                    Log.d("AJKKJKASJKAJK", i + "==");
                    if (btn.getId() == checkedId) {
                        text = btn.getText().toString();
                        Log.d("AJKKJKASJKAJK11", text + "==");
                        sessionManager.setDharamshala_id(String.valueOf(checkedId), text, "9");

                        Bundle params = new Bundle();
                        params.putString("user_detail", "Switch From : " +oldDsnm+" Switch To : " + text + " By  " + sessionManager.getUserDetailsOb().getUsername() + " at " + ConvertGMTtoIST.getCurrentDateTimeLastSeen());
                        mFirebaseAnalytics.logEvent("Switch_Account", params);

                        // do something with text

                        break;
                    }
                }
                Intent intent = new Intent(ProfileScreenActivity.this, MainScreen.class);
                startActivity(intent);
                finish();

            }
        });

        String jsonarrayDetails = sessionManager.getDharamshala_details();
        Log.d("DHHJDJHAJHADJAH", jsonarrayDetails + "===");
        ArrayList<String> dsidsListarry = new ArrayList<>();
        ArrayList<String> dsiNmsListarry = new ArrayList<>();
        JSONArray jsonArraydetails = null;
        try {
            jsonArraydetails = new JSONArray(jsonarrayDetails);
            for (int i = 0; i < jsonArraydetails.length(); i++) {
                JSONObject objectDetails = jsonArraydetails.getJSONObject(i);
                dsidsListarry.add(i, objectDetails.getString("ds_id"));
                dsiNmsListarry.add(i, objectDetails.getString("ds_name"));

                // Log.d("KJDSKHDHJSHJDH12", objectDetails.getString("ds_id") + "===>>>" + objectDetails.getString("ds_name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addRadioButtons(dsidsListarry.size(), dsidsListarry, dsiNmsListarry, radioGroup);
        dialogBottomSheet.setContentView(view);
        dialogBottomSheet.show();

    }

    public void addRadioButtons(int number, ArrayList<String> dsids, ArrayList<String> dsnms, RadioGroup ll) {
        String dsId = sessionManager.getDharamshalaId();
        for (int i = 0; i < number; i++) {
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setPadding(8, 8, 8, 8);
            rdbtn.setId(Integer.parseInt(dsids.get(i)));
            rdbtn.setText(dsnms.get(i));
            rdbtn.setTextSize(14);

            if (dsId.equals(dsids.get(i))) {
                rdbtn.setChecked(true);
            }

            ll.addView(rdbtn);
        }


    }

}
