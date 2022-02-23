package com.ydbm.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ydbm.R;
import com.ydbm.activities.BookingDetailActivity;
import com.ydbm.models.BookingDetails;
import com.ydbm.session.SessionManager;
import com.ydbm.models.User;
import com.ydbm.utils.AppConstants;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.LabelLayout;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InquiryListAdapter extends RecyclerView.Adapter<InquiryListAdapter.MyViewHolder> implements Filterable {
    private ArrayList<BookingDetails> mDataset, copyList;
    Context mcontext;
    SessionManager sessionManager;
    SQLiteHandler sqLiteHandler;
    int from = 0;
    User user;
    BottomSheetDialog dialogBottomSheet;
    private boolean isInternet = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewBookingId, textViewYatrinm, textViewCheckin, textViewCheckout, textViewRmType, btnOptionYes, btnOptionNo, textViewConfirmBy;
        TextView textViewCheckNow, tvNights, tvRooms, tvGuests, tvReply,
                tvSuggestion,textViewExtraMattressValue,textViewExtraMattressTitle;
        ImageButton imageButtonDetails, imageBtnShare;
        LabelLayout labelLayout;
        RadioGroup radioGroup;
        LinearLayout linearLayoutBooking, linearInquiry;
        LinearLayout linearConfirmBy,linearExtraMattress;
        ImageView imgDharamshala;
        Button imageViewBtnOptions;
        LabelLayout labelLayoutNewly;

        public MyViewHolder(View v) {
            super(v);
            imageButtonDetails = v.findViewById(R.id.btn_bookingDetails);
            //  imageViewBtnOptions = v.findViewById(R.id.btnOptions);
            textViewBookingId = v.findViewById(R.id.textViewBookingIdValue);
            textViewYatrinm = v.findViewById(R.id.textViewYatriNamevalue);
            btnOptionYes = v.findViewById(R.id.btnOptionYes);
            btnOptionNo = v.findViewById(R.id.btnOptionNo);
            labelLayoutNewly = v.findViewById(R.id.label_layout_pending);
            textViewCheckin = v.findViewById(R.id.textViewCheckInvalue);
            textViewCheckout = v.findViewById(R.id.textViewCheckoutvalue);
            radioGroup = v.findViewById(R.id.radioGrp);
            linearInquiry = v.findViewById(R.id.linearInquiryRply);
            linearLayoutBooking = v.findViewById(R.id.linearResonse);
            tvReply = v.findViewById(R.id.tvInquiryReply);
            tvSuggestion = v.findViewById(R.id.tvInquirySuggestion);

            tvNights = v.findViewById(R.id.tv_NoNightsValue);
            tvGuests = v.findViewById(R.id.tv_persons_number);
            tvRooms = v.findViewById(R.id.tv_rooms_number);
            labelLayout = v.findViewById(R.id.label_layout_pending);
            textViewRmType = v.findViewById(R.id.textViewRmType);
            imageBtnShare = v.findViewById(R.id.btn_bookingShare);
            linearConfirmBy = v.findViewById(R.id.linearConfirmBy);
            textViewConfirmBy = v.findViewById(R.id.textViewConfirmByValue);
            textViewExtraMattressTitle = v.findViewById(R.id.textViewExtraMattressTitle);
            textViewExtraMattressValue = v.findViewById(R.id.textViewExtraMattressValue);
            linearExtraMattress = v.findViewById(R.id.linearExtraMattress);

            //  textView = v.findViewById(R.id.textViewDname);
            // textViewCheckNow = v.findViewById(R.id.btn_bookNow);
            // imgDharamshala = v.findViewById(R.id.dm_image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public InquiryListAdapter(ArrayList<BookingDetails> myDataset1, Context context, int fromType) {
        this.mDataset = myDataset1;
        this.copyList = myDataset1;
        this.mcontext = context;
        this.from = fromType;
        sqLiteHandler = new SQLiteHandler(mcontext);
        sessionManager = new SessionManager(mcontext);
        user = sessionManager.getUserDetailsOb();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mcontext);

    }

    // Create new views (invoked by the layout manager)
    @Override
    public InquiryListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inquiry_row_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final InquiryListAdapter.MyViewHolder holder, int position) {

        final BookingDetails bookingDetails = mDataset.get(position);

        Log.e("TAG", "onBindViewHolderrr: "+bookingDetails.getNo_of_mattress());
        Log.e("TAG", "onBindViewHolderrr: "+bookingDetails.getExtra_mattress_label());
//        holder.textViewExtraMattressValue.setText(bookingDetails.getNo_of_mattress());
//        holder.textViewExtraMattressTitle.setText(bookingDetails.getExtra_mattress_label());

        if(!TextUtils.isEmpty(bookingDetails.getNo_of_mattress()))
        {
            holder.textViewExtraMattressValue.setText(bookingDetails.getNo_of_mattress());
            holder.textViewExtraMattressValue.setVisibility(View.VISIBLE);
        }else{
            holder.textViewExtraMattressValue.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(bookingDetails.getExtra_mattress_label())){
            holder.textViewExtraMattressTitle.setText(bookingDetails.getExtra_mattress_label());
            holder.linearExtraMattress.setVisibility(View.VISIBLE);

        }

       if(TextUtils.isEmpty(bookingDetails.getExtra_mattress_status())){
           holder.linearExtraMattress.setVisibility(View.GONE);

       }else{
        if(bookingDetails.getExtra_mattress_status().contentEquals("1")){
            holder.linearExtraMattress.setVisibility(View.VISIBLE);
        }else{
            holder.linearExtraMattress.setVisibility(View.GONE);
        }

       }

        if (from == 0) {
            holder.linearLayoutBooking.setVisibility(View.VISIBLE);
            holder.linearInquiry.setVisibility(View.GONE);
        } else {
            holder.linearLayoutBooking.setVisibility(View.GONE);
            holder.linearInquiry.setVisibility(View.VISIBLE);
        }
        if (bookingDetails.getReply() != null) {
            if (bookingDetails.getReply().equalsIgnoreCase("no")) {
                String confirmed = bookingDetails.getSuggestion();
                holder.tvSuggestion.setVisibility(View.VISIBLE);
                holder.tvSuggestion.setText(bookingDetails.getBookingRoomsType());
                holder.tvReply.setText("Reply : " + bookingDetails.getReply() + "\n" + "(" + confirmed + ")");

            } else {
                holder.tvReply.setText("Reply : " + bookingDetails.getReply());

            }

        }
        if (bookingDetails.isNewArrived()) {
            holder.labelLayoutNewly.setVisibility(View.VISIBLE);
        } else {
            holder.labelLayoutNewly.setVisibility(View.GONE);
        }
        holder.textViewBookingId.setText(bookingDetails.getBookingId());
        holder.textViewYatrinm.setText(bookingDetails.getYatriNm());
        holder.textViewCheckin.setText(bookingDetails.getCheckinDt());
        holder.textViewCheckout.setText(bookingDetails.getCheckoutDt());
        holder.textViewRmType.setText(bookingDetails.getRoomTyp());
        holder.tvNights.setText(bookingDetails.getNights());
        holder.tvRooms.setText(bookingDetails.getNoOfRooms());
        holder.tvGuests.setText(bookingDetails.getPerson());
        Log.d("KJDAKJKKA123", bookingDetails.getBookingConfirmedBy() + "==");
        if (bookingDetails.getBookingConfirmedBy() != null) {
            if (bookingDetails.getBookingConfirmedBy().isEmpty() || bookingDetails.getBookingConfirmedBy().equals("null")) {
                holder.linearConfirmBy.setVisibility(View.GONE);
            } else {
                holder.linearConfirmBy.setVisibility(View.VISIBLE);
                holder.textViewConfirmBy.setText(bookingDetails.getBookingConfirmedBy());
            }
        } else {
            holder.linearConfirmBy.setVisibility(View.GONE);
        }
        holder.imageBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "";

                shareBody = " \n" + " \n"
                        + "Yatri Name :- " + bookingDetails.getYatriNm()
                        + "\n" + "Bhavan Name :- " + bookingDetails.getDs_name() + "\n" + "Booking Id :- " + bookingDetails.getBookingId() + "\n" + "Room Type :- " + bookingDetails.getRoomTyp() + "\n"
                        + "Check in :- " + bookingDetails.getCheckinDt() + "\n" + "Check out :- " + bookingDetails.getCheckoutDt() + "\n" + "Nights :- " + bookingDetails.getNights() + "\n"
                        + "Rooms :- " + bookingDetails.getNoOfRooms() + "\n" + "Guests :- " + bookingDetails.getPerson() + "\n" + "\n" + "Thanks.";

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Booking Inquiry");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                mcontext.startActivity(Intent.createChooser(sharingIntent, "Share via"));


            }
        });

        holder.btnOptionYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnOptionYes.setBackgroundResource(R.drawable.confirm_btn_bg_round);
                holder.btnOptionYes.setTextColor(ContextCompat.getColor(mcontext, R.color.white));
                holder.btnOptionNo.setBackgroundColor(ContextCompat.getColor(mcontext, android.R.color.transparent));
                holder.btnOptionNo.setTextColor(ContextCompat.getColor(mcontext, R.color.colorPrimary));
                holder.radioGroup.setVisibility(View.GONE);
                isInternet = NetworkUtil.checkInternetConnection(mcontext);
                if (isInternet) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked

                                    sendReplyAPICall("YES", "", bookingDetails, holder.getAdapterPosition(), "", "");
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                } else {
                    Toast.makeText(mcontext, "Please Connect To Internet!!", Toast.LENGTH_SHORT).show();

                }

            }
        });
        holder.btnOptionNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternet = NetworkUtil.checkInternetConnection(mcontext);
                if (isInternet) {
                    holder.btnOptionYes.setBackgroundColor(ContextCompat.getColor(mcontext, android.R.color.transparent));
                    holder.btnOptionYes.setTextColor(ContextCompat.getColor(mcontext, R.color.colorPrimary));
                    holder.btnOptionNo.setBackgroundResource(R.drawable.confirm_btn_bg_round);
                    holder.btnOptionNo.setTextColor(ContextCompat.getColor(mcontext, R.color.white));
                    holder.radioGroup.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(mcontext, "Please Connect To Internet!!", Toast.LENGTH_SHORT).show();

                }
            }
        });
        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioNA:
                        //   EventBus.getDefault().post("checkdata");
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        isInternet = NetworkUtil.checkInternetConnection(mcontext);
                                        if (isInternet) {
                                            sendReplyAPICall("NO", "not available", bookingDetails, holder.getAdapterPosition(), "", "");
                                        } else {
                                            Toast.makeText(mcontext, "Please Connect To Internet!", Toast.LENGTH_SHORT).show();
                                        }
                                        //Yes button clicked
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                        break;

                    case R.id.radioOA:

                        isInternet = NetworkUtil.checkInternetConnection(mcontext);
                        if (isInternet) {
                            showBottomSheetDialog(bookingDetails, holder.getAdapterPosition());
                        } else {
                            Toast.makeText(mcontext, "Please Connect To Internet!", Toast.LENGTH_SHORT).show();
                        }
                        //    EventBus.getDefault().post("checkdata");
                        break;

                }
            }
        });

  /*      if (bookingDetails.getNeftNo() == null || bookingDetails.getNeftNo().equals("null")) {
            if (bookingDetails.getBookingStatus().equals("complete_cancel")) {
                ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(mcontext, R.color.red));
                holder.labelLayout.setLabelBackground(colorDrawable);
            } else {
                Log.d("GHDGHGAHG", "1" + bookingDetails.getNeftNo());
                ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(mcontext, R.color.colorPrimary));
                holder.labelLayout.setLabelBackground(colorDrawable);
            }
        } else {
            Log.d("GHDGHGAHG", "2" + bookingDetails.getNeftNo());
            if (bookingDetails.getBookingStatus().equals("complete_cancel")) {
                ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(mcontext, R.color.red));
                holder.labelLayout.setLabelBackground(colorDrawable);
            } else {
                ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(mcontext, R.color.green));
                holder.labelLayout.setLabelBackground(colorDrawable);
            }
        }
*/
        holder.imageButtonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, BookingDetailActivity.class);
                intent.putExtra("bookingdetail", bookingDetails);
                mcontext.startActivity(intent);
            }
        });


        final Button button = holder.imageViewBtnOptions;
   /*     holder.imageViewBtnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mcontext, button);

                popup.inflate(R.menu.custom_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_view:

                                //integerList.remove(holder.getAdapterPosition());
                                //  notifyItemRemoved(holder.getAdapterPosition());
                                return true;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });*/
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
                        if (row != null) {
                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getYatriMobile().toLowerCase().contains(charString.toLowerCase()) || row.getYatriNm().toLowerCase().contains(charString.toLowerCase()) || row.getCheckinDt().toLowerCase().contains(charString.toLowerCase()) || row.getBookingId().toLowerCase().contains(charString.toLowerCase()) || row.getRoomTyp().toLowerCase().contains(charString.toLowerCase())) {
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

    private void sendReplyAPICall(final String reply, final String suggestion, final BookingDetails bookingDetails, int adapterPosition, String selected, String s) {
        final ProgressDialog dialog = new ProgressDialog(mcontext);
        dialog.setMessage("Replying .. Please Wait!");
        dialog.setCancelable(false);
        dialog.show();
        //  showProgressDialog("Fetching Inquiry Details.. Please Wait!");
        RequestQueue queue = Volley.newRequestQueue(mcontext);


        //Log.d("CHECKRESPONS", "https://yatradham.org/demosite/restapi.php?apiname=addgroup&number=" + finalMembers + "&group_id=" + mGroupId + "&is_mobile=APP");
        // Log.d("TEGSDHGHDSHD", AppConstants.BASE_API_URL + "enquirylist" + "&dsid=" + dharamshala_id + "&is_mobile=yes");
        //StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "enquirylist" + "&dsid=" + dharamshala_id + "&is_mobile=APP", new Response.Listener<String>() {
        //   Log.d("TEGSDHGHDSHD", AppConstants.BASE_API_URL + "enquirylist" + "&dsid=" + dharamshala_id + "&is_mobile=APP");
        //   StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "enquirylist" + "&dsid=" + dharamshala_id + "&is_mobile=APP", new Response.Listener<String>() {
        //   Log.d("TEGSDHGHDSHD", AppConstants.BASE_API_URL + "enquiryreply" + "&bookingid=" + bookingDetails.getBookingId() + "&contactno=" + user.getUsername() + "&managername=" + user.getUsername() + "&reply=" + reply + "&suggestion=" + suggestion + "&is_mobile=yes");
        Log.d("TEGSDHGHDSHD", AppConstants.BASE_API_URL + "enquiryreply" + "&bookingid=" + bookingDetails.getBookingId() + "&contactno=" + user.getUsername() + "&suggestion_rooms=" + selected + "&suggestion_msg=" + s + "&managername=" + user.getName() + "&reply=" + reply + "&suggestion=" + suggestion + "&is_mobile=yes");
        StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.BASE_API_URL + "enquiryreply" + "&bookingid=" + bookingDetails.getBookingId() + "&contactno=" + user.getUsername() + "&suggestion_rooms=" + selected + "&suggestion_msg=" + s + "&managername=" + user.getName() + "&reply=" + reply + "&suggestion=" + suggestion + "&is_mobile=yes", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                //  Toast.makeText(CreateUserActivity.this,response,Toast.LENGTH_LONG).show();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Log.d("CHECKRESPONSEREPY", response.toString());
                    if (jsonObject.has("status")) {
                        if (jsonObject.getInt("status") == 1) {
                            //sqLiteHandler.deleteBookings();
                            EventBus.getDefault().post("checkdata");
                            bookingDetails.setReply(reply);
                            bookingDetails.setSuggestion(suggestion);
                            if (dialogBottomSheet != null && dialogBottomSheet.isShowing()) {
                                dialogBottomSheet.dismiss();
                            }
                            Bundle params = new Bundle();
                            // params.putString("dharamshala_name", sessionManager.getDharamshalanm() + "");
                            params.putString("user_detail", sessionManager.getUserDetailsOb().getDs_name() +"("+sessionManager.getUserDetailsOb().getUsername()+")"+ " at " + ConvertGMTtoIST.getCurrentDateTimeLastSeen() + " Booking Id : " + bookingDetails.getBookingId() + " reply : " + reply);
                            mFirebaseAnalytics.logEvent("Inquiry_Reply", params);



                        } else {
                            Toast.makeText(mcontext, "No data Found!!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(mcontext, "No data Found!!", Toast.LENGTH_LONG).show();
                    }

                    // bookinListAdapter = new BookinListAdapter(bookings, BookingListingActivity.this);
                    //recyclerView.setAdapter(bookinListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(mcontext, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> param = new HashMap<String, String>();

                return param;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        sr.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(sr);
    }

    public void showBottomSheetDialog(final BookingDetails bookingDetails, final int adapterPosition) {
        LayoutInflater inflater = ((Activity) mcontext).getLayoutInflater();
        View view = inflater.inflate(R.layout.bottomsheet_inquiry, null);
        dialogBottomSheet = new BottomSheetDialog(mcontext);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRoomList);
        final EditText editTextSuggestion = view.findViewById(R.id.ediTextSuggestion);
        String msg = "";

        recyclerView.setLayoutManager(new LinearLayoutManager(mcontext));
        ImageView imgCancel = view.findViewById(R.id.imageCancel);
        Button buttonContinue = view.findViewById(R.id.btnContinue);


        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBottomSheet.dismiss();
            }
        });
        final RoomTypeAdapter roomTypeAdapter = new RoomTypeAdapter(bookingDetails.getArrayListRooms(), mcontext);
        recyclerView.setAdapter(roomTypeAdapter);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DAJKDJHDJ", roomTypeAdapter.getSelectedItems().size() + "==");
                if (roomTypeAdapter.getSelectedItems().size() > 0) {
                    final String selected = StringUtils.join(roomTypeAdapter.getSelectedItems(), ',');
                    DialogInterface.OnClickListener dialogClickListener1 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:

                                    sendReplyAPICall("NO", "other available", bookingDetails, adapterPosition, selected, editTextSuggestion.getText().toString());
                                    //Yes button clicked
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mcontext);
                    builder1.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener1)
                            .setNegativeButton("No", dialogClickListener1).show();

                } else {
                    Toast.makeText(mcontext, "please select room type!!", Toast.LENGTH_SHORT).show();
                }
            }

        });
        dialogBottomSheet.setContentView(view);
        dialogBottomSheet.show();

    }


}
