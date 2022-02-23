package com.ydbm.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ydbm.R;
import com.ydbm.activities.BookingDetailActivity;
import com.ydbm.models.BookingDetails;
import com.ydbm.models.RoomType;
import com.ydbm.utils.LabelLayout;

import java.util.ArrayList;
import java.util.List;

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.MyViewHolder> {
    private ArrayList<RoomType> mDataset, copyList, testList;
            ArrayList<String> selectedList;
    Context mcontext;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewRoomnm;
        ImageView imgCancel;
        CheckBox checkBox;

        public MyViewHolder(View v) {
            super(v);
            // imageButtonDetails = v.findViewById(R.id.btn_bookingDetails);

            textViewRoomnm = v.findViewById(R.id.textViewRoomNm);
            checkBox = v.findViewById(R.id.checkboxSelected);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RoomTypeAdapter(ArrayList<RoomType> myDataset1, Context context) {
        this.mDataset = myDataset1;
        this.selectedList = new ArrayList<>();
        this.mcontext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RoomTypeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inquiry_item_row_add_member, parent, false);
        RoomTypeAdapter.MyViewHolder vh = new RoomTypeAdapter.MyViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RoomTypeAdapter.MyViewHolder holder, final int position) {
        String rmName = mDataset.get(position).getRoomTypNm();
        holder.textViewRoomnm.setText(rmName);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedList.add(mDataset.get(position).getRoomTypNm());
                } else {
                    selectedList.remove(mDataset.get(position));
                }
                mDataset.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });


    }

    public ArrayList<String> getSelectedItems() {
        return selectedList;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
