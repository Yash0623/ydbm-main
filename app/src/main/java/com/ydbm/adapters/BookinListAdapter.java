package com.ydbm.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
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
import com.ydbm.models.BookingDetails;

import com.ydbm.utils.LabelLayout;
import com.ydbm.utils.SQLiteHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookinListAdapter extends RecyclerView.Adapter<BookinListAdapter.MyViewHolder> implements Filterable {
    private ArrayList<BookingDetails> mDataset = new ArrayList<>(), copyList = new ArrayList<>(), testList;
    Context mcontext;
    int type = 5;
    SQLiteHandler sqLiteHandler;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewBookingId, textViewYatrinm, textViewCheckin, textViewCheckout, textViewRmType, textViewConfirmBy;
        TextView textViewCheckNow, txtExtraMattressQty,lblMattress;
        ImageButton imageButtonDetails, imageBtnShare, imageBtnDelete;
        LabelLayout labelLayout;
        LinearLayout linearConfirmBy, linearExtraMattress;
        ImageView imgDharamshala;

        public MyViewHolder(View v) {
            super(v);
            // imageButtonDetails = v.findViewById(R.id.btn_bookingDetails);
            textViewBookingId = v.findViewById(R.id.textViewBookingIdValue);
            lblMattress = v.findViewById(R.id.lblMattress);
            textViewYatrinm = v.findViewById(R.id.textViewYatriNamevalue);
            textViewCheckin = v.findViewById(R.id.textViewCheckInvalue);
            textViewCheckout = v.findViewById(R.id.textViewCheckoutvalue);
            txtExtraMattressQty = v.findViewById(R.id.txtExtraMattressQty);
            labelLayout = v.findViewById(R.id.label_layout_pending);
            textViewRmType = v.findViewById(R.id.textViewRmType);
            imageBtnShare = v.findViewById(R.id.btn_bookingShare);
            imageBtnDelete = v.findViewById(R.id.btn_bookingDelete);
            linearExtraMattress = v.findViewById(R.id.linearExtraMattress);
            //linearConfirmBy = v.findViewById(R.id.linearConfirmBy);
            //  textViewConfirmBy = v.findViewById(R.id.textViewConfirmByValue);


            //  textView = v.findViewById(R.id.textViewDname);
            // textViewCheckNow = v.findViewById(R.id.btn_bookNow);
            // imgDharamshala = v.findViewById(R.id.dm_image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BookinListAdapter(ArrayList<BookingDetails> myDataset1, Context context) {
        this.mDataset = myDataset1;
        this.copyList = myDataset1;
        this.mcontext = context;
    }

    public BookinListAdapter(ArrayList<BookingDetails> myDataset1, Context context, int typ) {

        this.mDataset = myDataset1;
        this.copyList = myDataset1;
        this.mcontext = context;
        this.type = typ;
        sqLiteHandler = new SQLiteHandler(mcontext);
    }

    public List<BookingDetails> cloneItems() {
        return new ArrayList<BookingDetails>(mDataset);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BookinListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_row_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final BookingDetails bookingDetails = mDataset.get(position);

        holder.textViewBookingId.setText(bookingDetails.getBookingId());
        holder.textViewYatrinm.setText(bookingDetails.getYatriNm());
        holder.textViewCheckin.setText(bookingDetails.getCheckinDt());
        holder.textViewCheckout.setText(bookingDetails.getCheckoutDt());
        holder.textViewRmType.setText(bookingDetails.getRoomTyp());
        holder.lblMattress.setText(bookingDetails.getExtra_mattress_label() +" : ");

        if (TextUtils.isEmpty(bookingDetails.getExtra_mattress_status())) {
            holder.linearExtraMattress.setVisibility(View.GONE);

        } else {
            if (bookingDetails.getExtra_mattress_status().contentEquals("1")) {
                holder.linearExtraMattress.setVisibility(View.VISIBLE);
                holder.txtExtraMattressQty.setText(bookingDetails.getNo_of_mattress());
            } else {
                holder.linearExtraMattress.setVisibility(View.GONE);
            }

        }


        if (type == 9) {
            holder.imageBtnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.imageBtnDelete.setVisibility(View.GONE);
        }
        holder.imageBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = sqLiteHandler.deleteOfflineBooking(bookingDetails.getBookingId());
                if (i > 0) {
                    mDataset.remove(bookingDetails);
                    String checkIn = bookingDetails.getCheckinDt().split(" ")[0];
                    String checkout = bookingDetails.getCheckoutDt().split(" ")[0];
                    ArrayList<String> datesBetwn = getCheckInDates(checkIn.trim(), checkout.trim());
                    for (int j = 0; j < datesBetwn.size(); j++) {

                        sqLiteHandler.deleteOfflineInventoryExist(bookingDetails.getRoom_id(), datesBetwn.get(j), bookingDetails.getDs_id(), Integer.parseInt(bookingDetails.getNoOfRooms()));
                    }
                    notifyDataSetChanged();
                }
                Log.d("HAHJGHGSH133", i + "");
            }
        });
        holder.imageBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "";
                if (bookingDetails.getNeftNo() == null) {
                    shareBody = "Booking Confirmation Voucher" + " \n" + " \n" + "Neft. No :- " + bookingDetails.getNeftNo() + "\n" + "Tranfer date :- " + bookingDetails.getTranferDt() + "\n" + "Tranfer amount :- " + bookingDetails.getTranferAmt() + "\n" + "\n"
                            + "Yatri Name :- " + bookingDetails.getYatriNm() + "\n" + "Address :- " + bookingDetails.getYatriAddress() + "\n" + "Mobile No :- " + bookingDetails.getYatriMobile() + "\n"
                            + "\n" + "Bhavan Name :- " + bookingDetails.getDs_name() + "\n" + "Booking Id :- " + bookingDetails.getBookingId() + "\n" + "Room Type :- " + bookingDetails.getRoomTyp() + "\n"
                            + "Check in :- " + bookingDetails.getCheckinDt() + "\n" + "Check out :- " + bookingDetails.getCheckoutDt() + "\n" + "Nights :- " + bookingDetails.getNights() + "\n"
                            + "Rooms :- " + bookingDetails.getNoOfRooms() + "\n" + "Guests :- " + bookingDetails.getPerson() + "\n" + "Booking total :- " + bookingDetails.getBookingTotal() + "\n" + "\n" + "Thanks.";

                } else {
                    if (bookingDetails.getNeftNo() != null && bookingDetails.getNeftNo().equals("null") || bookingDetails.getTranferDt().equals("null") || bookingDetails.getTranferAmt().equals("null")) {
                        shareBody = "Booking Confirmation Voucher" + " \n" + " \n"
                                + "Yatri Name :- " + bookingDetails.getYatriNm() + "\n" + "Address :- " + bookingDetails.getYatriAddress() + "\n" + "Mobile No :- " + bookingDetails.getYatriMobile() + "\n"
                                + "\n" + "Bhavan Name :- " + bookingDetails.getDs_name() + "\n" + "Booking Id :- " + bookingDetails.getBookingId() + "\n" + "Room Type :- " + bookingDetails.getRoomTyp() + "\n"
                                + "Check in :- " + bookingDetails.getCheckinDt() + "\n" + "Check out :- " + bookingDetails.getCheckoutDt() + "\n" + "Nights :- " + bookingDetails.getNights() + "\n"
                                + "Rooms :- " + bookingDetails.getNoOfRooms() + "\n" + "Guests :- " + bookingDetails.getPerson() + "\n" + "Booking total :- " + bookingDetails.getBookingTotal() + "\n" + "\n" + "Thanks.";
                    } else {
                        shareBody = "Booking Confirmation Voucher" + " \n" + " \n" + "Neft. No :- " + bookingDetails.getNeftNo() + "\n" + "Tranfer date :- " + bookingDetails.getTranferDt() + "\n" + "Tranfer amount :- " + bookingDetails.getTranferAmt() + "\n" + "\n"
                                + "Yatri Name :- " + bookingDetails.getYatriNm() + "\n" + "Address :- " + bookingDetails.getYatriAddress() + "\n" + "Mobile No :- " + bookingDetails.getYatriMobile() + "\n"
                                + "\n" + "Bhavan Name :- " + bookingDetails.getDs_name() + "\n" + "Booking Id :- " + bookingDetails.getBookingId() + "\n" + "Room Type :- " + bookingDetails.getRoomTyp() + "\n"
                                + "Check in :- " + bookingDetails.getCheckinDt() + "\n" + "Check out :- " + bookingDetails.getCheckoutDt() + "\n" + "Nights :- " + bookingDetails.getNights() + "\n"
                                + "Rooms :- " + bookingDetails.getNoOfRooms() + "\n" + "Guests :- " + bookingDetails.getPerson() + "\n" + "Booking total :- " + bookingDetails.getBookingTotal() + "\n" + "\n" + "Thanks.";
                    }
                }
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Yatradham Booking Confirmation Voucher");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                mcontext.startActivity(Intent.createChooser(sharingIntent, "Share via"));


            }
        });


        Log.d("JJGDSHGSHSGH", bookingDetails.getNeftNo() + "==");
        if (bookingDetails.getNeftNo() == null || bookingDetails.getNeftNo().equals("null")) {

            if (bookingDetails.getBookingStatus() != null && bookingDetails.getBookingStatus().equals("complete_cancel")) {
                ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(mcontext, R.color.red));
                holder.labelLayout.setLabelBackground(colorDrawable);
            } else {
                Log.d("GHDGHGAHG", "1" + bookingDetails.getNeftNo());
                ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(mcontext, R.color.colorPrimary));
                holder.labelLayout.setLabelBackground(colorDrawable);
            }
        } else {
            Log.d("GHDGHGAHG", "2" + bookingDetails.getNeftNo());
            if (bookingDetails.getBookingStatus() != null && bookingDetails.getBookingStatus().equals("complete_cancel")) {
                ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(mcontext, R.color.red));
                holder.labelLayout.setLabelBackground(colorDrawable);
            } else {
                ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(mcontext, R.color.green));
                holder.labelLayout.setLabelBackground(colorDrawable);
            }
        }
        if (bookingDetails.isNewArrived()) {
            holder.labelLayout.setLabelText("New");
        } else {
            holder.labelLayout.setLabelText("");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, BookingDetailActivity.class);
                intent.putExtra("bookingdetail", bookingDetails);
                mcontext.startActivity(intent);
            }
        });
        /* holder.imageButtonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, BookingDetailActivity.class);
                intent.putExtra("bookingdetail", bookingDetails);
                mcontext.startActivity(intent);
            }
        }); */


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
                    List<BookingDetails> filteredList = new ArrayList<>();
                    for (BookingDetails row : mDataset) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (type == 9) {
                            if (row.getYatriMobile().toLowerCase().contains(charString.toLowerCase()) || row.getYatriNm().toLowerCase().contains(charString.toLowerCase()) || row.getCheckinDt().toLowerCase().contains(charString.toLowerCase()) || row.getBookingId().toLowerCase().contains(charString.toLowerCase()) || row.getRoomTyp().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }

                        } else {
                            if (row.getYatriMobile().toLowerCase().contains(charString.toLowerCase()) || row.getNeftNo().toLowerCase().contains(charString.toLowerCase()) || row.getYatriNm().toLowerCase().contains(charString.toLowerCase()) || row.getCheckinDt().toLowerCase().contains(charString.toLowerCase()) || row.getBookingId().toLowerCase().contains(charString.toLowerCase()) || row.getRoomTyp().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                    }
                    mDataset = (ArrayList<BookingDetails>) filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataset;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataset = (ArrayList<BookingDetails>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    public List<BookingDetails> filterList(String charString) {

        List<BookingDetails> filteredList = new ArrayList<>();
        for (BookingDetails row : mDataset) {

            // name match condition. this might differ depending on your requirement
            // here we are looking for name or phone number match
            if (row.getCheckinDt().toLowerCase().contains(charString.toLowerCase())) {
                filteredList.add(row);

            }

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
               /* for (BookingDetails bookingDetails : bookingDetailsListFiltered) {
                    String[] dt = bookingDetails.getCheckinDt().split(" ");
                    Log.d("HAJHJHJHAJHA123", df1.format(cal1.getTime()) + "==" + "===" + dt[0]);
                    if (dt[0].equals(df1.format(cal1.getTime()))) {
                        bookingDetailsListFilteredBookings.add(bookingDetails);
                    } else {


                    }
                }*/


            }
            cal1.add(Calendar.DATE, 1);

        }
       /* bookingDetailsList.clear();
        bookingDetailsList.addAll(bookingDetailsListFilteredBookings);
        bookinListAdapter.notifyDataSetChanged();
        btnClearFilter.setVisibility(View.VISIBLE);
        isFiltered = true;*/
        return datesFormatetd;
    }

}