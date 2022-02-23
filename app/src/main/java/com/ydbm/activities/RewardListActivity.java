package com.ydbm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.ydbm.R;
import com.ydbm.adapters.RewardsAdapter;
import com.ydbm.models.RewardModel;

import java.util.ArrayList;

public class RewardListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<RewardModel> rewardModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_list);
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.listRewardList);
        recyclerView.setLayoutManager(new LinearLayoutManager(RewardListActivity.this));
        setData();

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

    private void setData() {
        for (int i = 0; i < 10; i++) {
            RewardModel rewardModel = new RewardModel();
            rewardModel.setD_id("101");
            rewardModel.setId("1");
            rewardModel.setTotalPoints("508");
            rewardModel.setType("Inquiry Reply");
            rewardModelArrayList.add(rewardModel);
        }
        RewardsAdapter rewardsAdapter = new RewardsAdapter(rewardModelArrayList, RewardListActivity.this);
        recyclerView.setAdapter(rewardsAdapter);

    }
}
