package com.ydbm.adapters;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;
import com.ydbm.R;
import com.ydbm.models.RewardModel;
import com.ydbm.utils.LabelLayout;
import com.ydbm.utils.SQLiteHandler;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.MyViewHolder> implements Filterable {
    private ArrayList<RewardModel> mDataset = new ArrayList<>(), copyList = new ArrayList<>(), testList;
    Context mcontext;
    int type = 5;
    SQLiteHandler sqLiteHandler;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewBookingId, textViewYatrinm, textViewCheckin, textViewCheckout, textViewRmType, textViewConfirmBy;
        TextView textViewCheckNow;
        ImageButton imageButtonDetails, imageBtnShare, imageBtnDelete;
        LabelLayout labelLayout;
        LinearLayout linearConfirmBy;
        ImageView imgDharamshala;

        public MyViewHolder(View v) {
            super(v);
            // imageButtonDetails = v.findViewById(R.id.btn_RewardModel);
            textViewBookingId = v.findViewById(R.id.textViewBookingIdValue);
            textViewYatrinm = v.findViewById(R.id.textViewYatriNamevalue);
            textViewCheckin = v.findViewById(R.id.textViewCheckInvalue);
            textViewCheckout = v.findViewById(R.id.textViewCheckoutvalue);
            labelLayout = v.findViewById(R.id.label_layout_pending);
            textViewRmType = v.findViewById(R.id.textViewRmType);
            imageBtnShare = v.findViewById(R.id.btn_bookingShare);
            imageBtnDelete = v.findViewById(R.id.btn_bookingDelete);
            //linearConfirmBy = v.findViewById(R.id.linearConfirmBy);
            //  textViewConfirmBy = v.findViewById(R.id.textViewConfirmByValue);


            //  textView = v.findViewById(R.id.textViewDname);
            // textViewCheckNow = v.findViewById(R.id.btn_bookNow);
            // imgDharamshala = v.findViewById(R.id.dm_image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RewardsAdapter(ArrayList<RewardModel> myDataset1, Context context) {
        this.mDataset = myDataset1;
        this.copyList = myDataset1;
        this.mcontext = context;
    }

    public RewardsAdapter(ArrayList<RewardModel> myDataset1, Context context, int typ) {

        this.mDataset = myDataset1;
        this.copyList = myDataset1;
        this.mcontext = context;
        this.type = typ;
        sqLiteHandler = new SQLiteHandler(mcontext);
    }

    public List<RewardModel> cloneItems() {
        return new ArrayList<RewardModel>(mDataset);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RewardsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reward_row, parent, false);
        RewardsAdapter.MyViewHolder vh = new RewardsAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RewardsAdapter.MyViewHolder holder, int position) {
        final RewardModel RewardModel = mDataset.get(position);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                Log.d("HDJAHJHAJ", "sdfkjdf");
                if (charString.isEmpty()) {
                    mDataset = copyList;
                } else {
                    if (mDataset.size() == 0) {
                        mDataset = copyList;
                    }
                    List<RewardModel> filteredList = new ArrayList<>();
                    for (RewardModel row : mDataset) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (type == 9) {
                            if (row.getType().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }

                        } else {
                            if (row.getType().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                    }
                    mDataset = (ArrayList<RewardModel>) filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataset;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataset = (ArrayList<RewardModel>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    public List<RewardModel> filterList(String charString) {

        List<RewardModel> filteredList = new ArrayList<>();
        for (RewardModel row : mDataset) {

            // name match condition. this might differ depending on your requirement
            // here we are looking for name or phone number match


        }

        return filteredList;

    }

    private ArrayList<String> getCheckInDates(String dateString1, String dateString2) {
        ArrayList<Date> dates = new ArrayList<Date>();
        ArrayList<String> datesFormatetd = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");


        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Log.d("HAJHJHJHAJHA113", dateString1 + "===" + dateString2);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        dates.add(cal1.getTime());
        while (!cal1.after(cal2)) {
            // dates.add(cal1.getTime());
            if (!dateString2.equals(df1.format(cal1.getTime()))) {
                datesFormatetd.add(df1.format(cal1.getTime()));

                Log.d("HAJHJHJHAJHA112", df1.format(cal1.getTime()) + "==");
               /* for (RewardModel RewardModel : RewardModelListFiltered) {
                    String[] dt = RewardModel.getCheckinDt().split(" ");
                    Log.d("HAJHJHJHAJHA123", df1.format(cal1.getTime()) + "==" + "===" + dt[0]);
                    if (dt[0].equals(df1.format(cal1.getTime()))) {
                        RewardModelListFilteredBookings.add(RewardModel);
                    } else {


                    }
                }*/


            }
            cal1.add(Calendar.DATE, 1);

        }
       /* RewardModelList.clear();
        RewardModelList.addAll(RewardModelListFilteredBookings);
        RewardsAdapter.notifyDataSetChanged();
        btnClearFilter.setVisibility(View.VISIBLE);
        isFiltered = true;*/
        return datesFormatetd;
    }

}

