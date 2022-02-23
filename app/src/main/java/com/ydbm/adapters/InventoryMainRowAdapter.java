package com.ydbm.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ydbm.R;
import com.ydbm.activities.BookingListingActivity;
import com.ydbm.activities.ViewOfflineBookingsActivity;
import com.ydbm.models.BookingDetails;
import com.ydbm.models.InventoryRow;
import com.ydbm.models.InventoryRowMain;
import com.ydbm.session.SessionManager;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.SQLiteHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryMainRowAdapter extends RecyclerView.Adapter<InventoryMainRowAdapter.MyViewHolder> {
    private final FirebaseAnalytics mFirebaseAnalytics;
    private ArrayList<InventoryRowMain> mDataset;
    SQLiteHandler sqLiteHandler;
    Context mcontext;
    SessionManager sessionManager;
    HashMap<String, Integer> rooms;
    HashMap<String, ArrayList<BookingDetails>> bookingDatesList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        RecyclerView recyclerViewMain;
        TableLayout tableLayout;
        TextView textViewHeading;
        ImageView onlineData, offlineData;


        public MyViewHolder(View v) {
            super(v);
            // imageButtonDetails = v.findViewById(R.id.btn_InventoryRowMain);
            // recyclerViewMain = v.findViewById(R.id.recylerviewInventoryRowList);
            tableLayout = v.findViewById(R.id.tableBookingDetails);
            textViewHeading = v.findViewById(R.id.heading);
            onlineData = v.findViewById(R.id.onlineData);
            offlineData = v.findViewById(R.id.offlineData);


            //  recyclerViewMain.setLayoutManager(new LinearLayoutManager(mcontext));

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public InventoryMainRowAdapter(HashMap<String, Integer> rooms1, ArrayList<InventoryRowMain> myDataset1, Context context) {
        setHasStableIds(true);
        this.mDataset = myDataset1;
        Log.d("DHJDJHDJHDJ", mDataset.size() + "===");
        this.rooms = rooms1;
        this.mcontext = context;
        this.bookingDatesList = new HashMap<>();
        sqLiteHandler = new SQLiteHandler(mcontext);
        sessionManager=new SessionManager(mcontext);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mcontext);
    }

    public List<InventoryRowMain> cloneItems() {
        return new ArrayList<InventoryRowMain>(mDataset);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InventoryMainRowAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_item_main_row, parent, false);
        InventoryMainRowAdapter.MyViewHolder vh = new InventoryMainRowAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(InventoryMainRowAdapter.MyViewHolder holder, int position) {
        holder.tableLayout.removeAllViews();
        InventoryRowMain InventoryRowMain = mDataset.get(position);
        String dtMain = InventoryRowMain.getDs_id();
        ArrayList<InventoryRow> inventoryRows = InventoryRowMain.getList();
        Log.d("JADKDAJKDHD44", inventoryRows.size() + "==" + position);
        // ArrayList<InventoryRow> inventoryRows1 = InventoryRowMain.getList();
        //inventoryRows1.addAll(inventoryRows);
        TableRow row1 = new TableRow(mcontext);
        holder.textViewHeading.setText(InventoryRowMain.getDs_id());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row1.setLayoutParams(lp);

        TextView textVie = new TextView(mcontext);
        TextView textVie1 = new TextView(mcontext);
        TextView textVie2 = new TextView(mcontext);

        textVie.setTextColor(mcontext.getResources().getColor(R.color.colorPrimaryDark));
        textVie1.setTextColor(mcontext.getResources().getColor(R.color.colorPrimaryDark));
        textVie2.setTextColor(mcontext.getResources().getColor(R.color.colorPrimaryDark));

        textVie.setPadding(6, 6, 0, 6);
        textVie1.setPadding(6, 6, 0, 6);
        textVie2.setPadding(6, 6, 0, 6);

        textVie.setText("Room type");
        textVie1.setText("Bkd");
        textVie2.setText("Off");
        textVie1.setGravity(Gravity.LEFT);
        //textVie2.setText("Bkd");
        row1.addView(textVie);
        row1.addView(textVie1);
        row1.addView(textVie2);
        textVie.setGravity(Gravity.LEFT);
        textVie2.setGravity(Gravity.LEFT);

        holder.tableLayout.addView(row1, 0);

        for (Map.Entry<String, Integer> entry : rooms.entrySet()) {
            Log.d("TEYTYETYTWE", entry.getKey() + "");
            String key = entry.getKey();
            int value = entry.getValue();
            Log.d("DLAKLKLKLKALK11", key + "==>>" + value);
            String arr[] = key.split(",");
            Log.d("TEST12323", arr[0] + "==>" + dtMain + "==1");
            if (arr[0].equals(dtMain)) {
                Log.d("TEST12323", arr[0] + "==>" + dtMain);
                for (int j = 0; j < inventoryRows.size(); j++) {

                    InventoryRow inventoryRow = inventoryRows.get(j);


                    Log.d("TEST12323", j + "==>" + dtMain + "==>" + arr[1] + "==" + inventoryRow.getRoomId());
                    if (arr[1].equals(inventoryRow.getRoomId())) {
                        inventoryRow.setKey(arr[0]);
                        Log.d("TEST12323", arr[1] + "==>" + dtMain + "==>----" + value);

                        inventoryRow.setCountBooked(value);
                    }

                }
            }
        }

        //ArrayList<BookingDetails> offlineBookings = sqLiteHandler.fetchAllOfflineBookings("offline", dsid);

        for (int i = 0; i < inventoryRows.size(); i++) {
            int j = i + 1;
            Log.d("TEYTYETYTWE", i + "==>" + j);

            InventoryRow inventoryRow = inventoryRows.get(i);
            TableRow row = new TableRow(mcontext);
            Log.d("ADHJHDAJHADJH33", inventoryRow.getKey() + "===" + inventoryRow.getRoomId() + "==" + inventoryRow.getCountBooked());
            TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp1);

            TextView textView = new TextView(mcontext);
            TextView textView1 = new TextView(mcontext);
            TextView textView2 = new TextView(mcontext);

            textView.setTextColor(mcontext.getResources().getColor(R.color.black));
            textView1.setTextColor(mcontext.getResources().getColor(R.color.blue));
            textView2.setTextColor(mcontext.getResources().getColor(R.color.stepview_mark));

            textVie.setEllipsize(TextUtils.TruncateAt.END);
            textVie.setMaxLines(1);
            textVie.setSingleLine(true);

            textView.setGravity(Gravity.LEFT);
            textView1.setGravity(Gravity.CENTER);

            textView2.setGravity(Gravity.CENTER);


            textView.setPadding(6, 6, 4, 6);
            textView1.setPadding(6, 6, 4, 6);
            textView2.setPadding(6, 6, 4, 6);
            Log.d("AKJKHJHJHHJDAJ", inventoryRow.getRoomId() + "===>" + InventoryRowMain.getDs_id() + "===");

            String data = sqLiteHandler.fetchOfflineRoomTotal(inventoryRow.getRoomId(), InventoryRowMain.getDs_id(), sessionManager.getDharamshalaId()+"");
            Log.d("SAHJHSJHAJHAS", data + "==" + InventoryRowMain.getDs_id());
            if (data == null) {
                data = "0";
                textView2.setText(data);
            } else {
                textView2.setText(data);
            }


            if (inventoryRow.getRoomType().length() > 11) {
                String txt = inventoryRow.getRoomType().substring(0, 11) + "..";
                Log.d("HAJDHJHJ13243", txt + "===>");
                textView.setText(txt);
            } else {
                textView.setText(inventoryRow.getRoomType());
            }
            Log.d("AJKKKJ454", inventoryRow.getCountBooked() + "");
            if (inventoryRow.getKey() != null && inventoryRow.getKey().equals(InventoryRowMain.getDs_id())) {
                textView1.setText(inventoryRow.getCountBooked() + "");

            } else {
                textView1.setText(0 + "");
            }
            //  textView2.setText(inventoryRow.getOccupied());

            row.addView(textView);
            row.addView(textView1);
            row.addView(textView2);

            holder.tableLayout.addView(row, j);
        }
        // inventoryRows.clear();
        // inventoryRows.addAll(inventoryRows1);

        //InventoryRowAdapter inventoryRowAdapter = new InventoryRowAdapter(inventoryRows, mcontext);
        //  holder.recyclerViewMain.setAdapter(inventoryRowAdapter);
        holder.onlineData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HADHJDDHJDAHJDAHJDAH", dtMain + "==");

                Bundle bundle1 = new Bundle();
                bundle1.putString("Name", sessionManager.getUserDetailsOb().getName()+"");
                bundle1.putString("View_By","date : "+dtMain +" View by "+sessionManager.getUserDetailsOb().getUsername()+" at "+ ConvertGMTtoIST.getCurrentDateAndTime());
                bundle1.putString("Dharamshala_Name", sessionManager.getDharamshalanm()+"");

                mFirebaseAnalytics.logEvent("Online_Inventory_Click", bundle1);
                Intent intent = new Intent(mcontext, BookingListingActivity.class);

                intent.putExtra("key", dtMain);
                intent.putExtra("from", 16);
                mcontext.startActivity(intent);

            }
        });
        holder.offlineData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("Name", sessionManager.getUserDetailsOb().getName()+"");
                bundle1.putString("View_By","date : "+dtMain +" View by "+sessionManager.getUserDetailsOb().getUsername()+" at "+ ConvertGMTtoIST.getCurrentDateAndTime());
                bundle1.putString("Dharamshala_Name", sessionManager.getDharamshalanm()+"");

                mFirebaseAnalytics.logEvent("Online_Inventory_Click", bundle1);
                Log.d("HADHJDDHJDAHJDAHJDAH", dtMain + "==");
                Intent intent = new Intent(mcontext, ViewOfflineBookingsActivity.class);

                intent.putExtra("key", dtMain);
                intent.putExtra("from", 16);
                mcontext.startActivity(intent);

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
