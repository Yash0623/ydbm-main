package com.ydbm.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ydbm.R;
import com.ydbm.activities.BookingListingActivity;
import com.ydbm.adapters.BookinListAdapter;
import com.ydbm.adapters.InquiryListAdapter;
import com.ydbm.models.BookingDetails;
import com.ydbm.models.BookingListWithType;
import com.ydbm.models.InquiryWithType;
import com.ydbm.session.SessionManager;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllInquiries extends Fragment {
    RecyclerView recyclerView;
    InquiryListAdapter bookinListAdapter;

    SessionManager sessionManager;

    boolean isInternet = false;
    SQLiteHandler sqLiteHandler;
    ArrayList<BookingDetails> bookingDetailsList = new ArrayList<>();
    private LinearLayout linearLayoutenquiries;
    private FirebaseAnalytics mFirebaseAnalytics;

    public AllInquiries() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_inquiries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    public static AllInquiries newInstance() {
        Log.d("AJDGHADGDGH", "8");

        AllInquiries fragment = new AllInquiries();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AJDGHADGDGH", "5");
        setHasOptionsMenu(true);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sqLiteHandler = new SQLiteHandler(getActivity());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        linearLayoutenquiries = getActivity().findViewById(R.id.noBokingsLinear);
        isInternet = NetworkUtil.checkInternetConnection(getActivity());
        sessionManager = new SessionManager(getActivity());
        final String d_id = sessionManager.getUserDetailsOb().getD_id();
        if (!isInternet) {
            ArrayList<BookingDetails> bookingDetailsList = new ArrayList<>();

            /*ArrayList<BookingDetails> all = sqLiteHandler.fetchAllBookings("all", d_id);
            bookingDetailsList.addAll(all);
            ArrayList<BookingDetails> bookingsTomorrow = sqLiteHandler.fetchAllBookings("tomorrow", d_id);
            Collections.reverse(bookingsTomorrow);
            bookingDetailsList.addAll(bookingsTomorrow);
            ArrayList<BookingDetails> bookingsToday = sqLiteHandler.fetchAllBookings("today", d_id);
            Collections.reverse(bookingsToday);
            bookingDetailsList.addAll(bookingsToday);
            ArrayList<BookingDetails> bookingsActive = sqLiteHandler.fetchAllBookings("active", d_id);
            Collections.reverse(bookingsActive);
            bookingDetailsList.addAll(bookingsActive);*/


             Collections.reverse(bookingDetailsList);
            if (bookinListAdapter != null) {
                bookinListAdapter.notifyDataSetChanged();
            } else {

                bookinListAdapter = new InquiryListAdapter(bookingDetailsList, getActivity(),0);
                recyclerView.setAdapter(bookinListAdapter);
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search);

        item.setVisible(true);


        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search Here");

        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (bookinListAdapter != null) {
                    bookinListAdapter.getFilter().filter(newText);

                    Bundle bundle1 = new Bundle();
                    bundle1.putString("user_detail",sessionManager.getUserDetailsOb().getUsername()+" # "+ ConvertGMTtoIST.getCurrentDateTimeLastSeen() + " # "+newText +" in "+"All Inquiries");
                    mFirebaseAnalytics.logEvent("Search_Event", bundle1);
                }
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
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.listInquiries);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

     /*   searchView.setQueryHint("Search Here");

        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bookinListAdapter.getFilter().filter(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("SHDJHJHJSH", "closed");
                return false;
            }
        });*/

    }

    @Override
    public void onStart() {
        Log.d("AJDGHADGDGH", "1");
        try {
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.d("AJDGHADGDGH", "2");
        EventBus.getDefault().unregister(this);
        super.onStop();

    }


    @Subscribe
    public void onEvent(InquiryWithType list) {
        Log.d("onEvent", "() called with: list = [" + list.getModels().size() + "]");
        Log.d("ADKDKJJKHADJKHDAj", list.getModels().size() + "==");

        if (bookingDetailsList.size() > 0) {
            Log.d("KADJKJAD", "1");
            bookingDetailsList.clear();
        }
        bookingDetailsList.clear();
        bookingDetailsList.addAll(list.getModels());
        //  Collections.reverse(bookingDetailsList);
        if (bookingDetailsList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            linearLayoutenquiries.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            linearLayoutenquiries.setVisibility(View.VISIBLE);
        }
        if (bookinListAdapter != null) {
            Log.d("ADKDKJJKHADJKHDAj", "1" + "==" +"=="+bookingDetailsList.size());
            bookinListAdapter.notifyDataSetChanged();
        } else {
            Log.d("ADKDKJJKHADJKHDAj", "2" + "==");
            bookinListAdapter = new InquiryListAdapter(bookingDetailsList, getActivity(),0);
            recyclerView.setAdapter(bookinListAdapter);
        }
    }


    public void onNotifyBookingList(ArrayList<BookingDetails> bookingDetails) {
        bookingDetailsList.addAll( bookingDetails);
        if (bookinListAdapter != null) {
            bookinListAdapter.notifyDataSetChanged();
        } else {

            bookinListAdapter = new InquiryListAdapter(bookingDetailsList, getActivity(),0);
            recyclerView.setAdapter(bookinListAdapter);
        }
    }
}
