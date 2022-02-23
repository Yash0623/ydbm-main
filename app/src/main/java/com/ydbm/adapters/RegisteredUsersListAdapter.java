package com.ydbm.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ydbm.R;
import com.ydbm.models.User;

import java.util.ArrayList;
import java.util.List;

public class RegisteredUsersListAdapter extends RecyclerView.Adapter<RegisteredUsersListAdapter.ViewHolder> implements Filterable {

    private List<User> mData, copyList;
    private LayoutInflater mInflater;
    Context context;

    // data is passed into the constructor
    public RegisteredUsersListAdapter(Context context, List<User> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.copyList = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public RegisteredUsersListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_group_list_row, parent, false);
        return new RegisteredUsersListAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RegisteredUsersListAdapter.ViewHolder holder, final int position) {
        String firstLetter = "";
        if (mData.get(position).getName() != null)
            firstLetter = String.valueOf(mData.get(position).getName().charAt(0));

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(position);
        //int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px
        holder.imageLetter.setImageDrawable(drawable);
        holder.myTextView.setText(mData.get(position).getName());
        holder.msgTimestamp.setText(mData.get(position).getUsername());
        if (mData.get(position).getD_id() != null) {
            holder.textViewDID.setText(mData.get(position).getD_id());
        }
        holder.lastSeenAtTextView.setText("Last Seen At " + mData.get(position).getLastSeen());

        if (mData.get(position).getCreationTime() != null) {

            holder.lastMessageTV.setText(mData.get(position).getCreationTime());

        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    mData = copyList;
                } else {
                    if (mData.size() == 0) {
                        mData = copyList;
                    }
                    List<User> filteredList = new ArrayList<>();
                    for (User row : mData) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getD_id().toLowerCase().contains(charString.toLowerCase()) || row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getCreationTime().toLowerCase().contains(charString.toLowerCase()) || row.getUsername().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    mData = (ArrayList<User>) filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mData = (ArrayList<User>) filterResults.values;
                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView, lastMessageTV, msgTimestamp, lastSeenAtTextView, textViewDID;
        ImageView imageLetter;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.item_name);
            lastSeenAtTextView = itemView.findViewById(R.id.lastSeenTime);
            imageLetter = itemView.findViewById(R.id.userIVGrp);
            lastMessageTV = itemView.findViewById(R.id.messageTV);
            msgTimestamp = itemView.findViewById(R.id.lastMessageTimeTV);
            textViewDID = itemView.findViewById(R.id.textViewDid);

        }


    }


    // allows clicks events to be caught

}
