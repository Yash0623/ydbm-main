package com.ydbm.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ydbm.R;
import com.ydbm.activities.BookingDetailActivity;

import com.ydbm.models.InventoryRow;
import com.ydbm.utils.LabelLayout;

import java.util.ArrayList;
import java.util.List;

public class InventoryRowAdapter extends RecyclerView.Adapter<InventoryRowAdapter.MyViewHolder> {
    private ArrayList<InventoryRow> mDataset, copyList, testList;
    Context mcontext;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewRoomTyp, textViewAcailable, textViewOccupied;


        public MyViewHolder(View v) {
            super(v);
            // imageButtonDetails = v.findViewById(R.id.btn_InventoryRow);
            textViewRoomTyp = v.findViewById(R.id.textRmType);
            textViewAcailable = v.findViewById(R.id.textAvailable);
            textViewOccupied = v.findViewById(R.id.textOccupied);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public InventoryRowAdapter(ArrayList<InventoryRow> myDataset1, Context context) {
        this.mDataset = myDataset1;
        this.copyList = myDataset1;
        this.mcontext = context;
    }

    public List<InventoryRow> cloneItems() {
        return new ArrayList<InventoryRow>(mDataset);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InventoryRowAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory_row, parent, false);
       MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(InventoryRowAdapter.MyViewHolder holder, int position) {
        final InventoryRow InventoryRow = mDataset.get(position);

        holder.textViewRoomTyp.setText(InventoryRow.getRoomType());
        holder.textViewAcailable.setText(InventoryRow.getAvaiable());
        holder.textViewOccupied.setText(InventoryRow.getOccupied());


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
