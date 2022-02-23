package com.ydbm.activities;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ydbm.R;
import com.ydbm.adapters.RegisteredUsersListAdapter;
import com.ydbm.ui.Database;
import com.ydbm.models.User;

import java.util.ArrayList;

public class RegisteredUsersList extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView noDataFoundTextView, textCount;
    ArrayList<User> usresList = new ArrayList<>();
    RegisteredUsersListAdapter registeredUsersListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_users_list);
        intiViews();
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textCount = findViewById(R.id.textViewCount);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //   TextView nameTV = (TextView) findViewById(R.id.nameTV);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("Registered Users");

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

    private void intiViews() {
        recyclerView = findViewById(R.id.recyclerRegisterdUsers);
        // noDataFoundTextView=findViewById(R.id.noDatafoundTextView);
        recyclerView.setLayoutManager(new LinearLayoutManager(RegisteredUsersList.this));
        registeredUsersListAdapter = new RegisteredUsersListAdapter(RegisteredUsersList.this, usresList);
        recyclerView.setAdapter(registeredUsersListAdapter);
        Database.NODE_USERS_PATH.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String count = String.valueOf(dataSnapshot.getChildrenCount());
                textCount.setText(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Database.NODE_USERS_PATH.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("AHJJHADJHJAHJDAJ", dataSnapshot + "==");
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.getUsername() != null) {
                        usresList.add(user);
                        registeredUsersListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.registered_usre_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search Here");
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("SXKJJKJKJSJ", "submit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                registeredUsersListAdapter.getFilter().filter(newText);
                Log.d("SXKJJKJKJSJ", "text change");
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("SHDJHJHJSH", "closed");
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
