package com.ydbm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.ydbm.R;
import com.ydbm.session.SessionManager;

public class AdminActivity extends AppCompatActivity {
    EditText editTextDid;
    Button buttonNxt;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        sessionManager = new SessionManager(AdminActivity.this);

        editTextDid = findViewById(R.id.editDharamshalaId);
        buttonNxt = findViewById(R.id.buttonNxt);
        initToolbar();

        buttonNxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d_id = editTextDid.getText().toString();
                if (d_id.length() > 0) {
                    Log.d("YTAYSTYTTYT123", d_id + "");

                    sessionManager.setDharamshala_id(d_id, "","12");

                    Intent intent = new Intent(AdminActivity.this, MainScreen.class);
                    intent.putExtra("from", 4);
                    intent.putExtra("fromuser","admin");
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(AdminActivity.this, "Please enter dharamshala id", Toast.LENGTH_SHORT).show();
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


}
