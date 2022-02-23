package com.ydbm.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ydbm.R;
import com.ydbm.activities.BookingListingActivity;
import com.ydbm.adapters.BookinListAdapter;
import com.ydbm.models.ActiveBookings;
import com.ydbm.models.BookingDetails;
import com.ydbm.models.BookingListWithType;
import com.ydbm.session.SessionManager;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveFragment extends Fragment {
    RecyclerView recyclerView;
    BookinListAdapter bookinListAdapter;

    SessionManager sessionManager;

    boolean isInternet = false;


    SQLiteHandler sqLiteHandler;
    private FirebaseAnalytics mFirebaseAnalytics;


    public ActiveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    public static ActiveFragment newInstance() {
        Log.d("AJDGHADGDGH", "8");

        ActiveFragment fragment = new ActiveFragment();
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
        isInternet = NetworkUtil.checkInternetConnection(getActivity());
        sessionManager = new SessionManager(getActivity());
        final String d_id = sessionManager.getUserDetailsOb().getD_id();
        if (!isInternet) {
            ArrayList<BookingDetails> bookingDetailsList = new ArrayList<>();

            bookingDetailsList = sqLiteHandler.fetchAllBookings("active", d_id);


            // Collections.reverse(bookingDetailsList);
            if (bookinListAdapter != null) {
                bookinListAdapter.notifyDataSetChanged();
            } else {
                bookinListAdapter = new BookinListAdapter(bookingDetailsList, getActivity());
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
                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Name", sessionManager.getUserDetailsOb().getName()+"");
                    bundle1.putString("user_detail",sessionManager.getUserDetailsOb().getUsername()+" # "+ ConvertGMTtoIST.getCurrentDateTimeLastSeen() + " # "+newText +"Active Bookings");
                    bundle1.putString("Dharamshala_Name", sessionManager.getDharamshalanm()+"");
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
        recyclerView = view.findViewById(R.id.listActiveBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


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
        EventBus.getDefault().unregister(this);
        super.onStop();

    }


    @Subscribe
    public void onEvent(ActiveBookings list) {
        Log.d("onEvent", "() called with: list = [" + list + "]");

        ArrayList<BookingDetails> bookingDetailsList = new ArrayList<>();
        if (bookingDetailsList.size() > 0) {
            Log.d("KADJKJAD", "1");
            bookingDetailsList.clear();
        }
        bookingDetailsList.addAll(list.getModels());
        //  Collections.reverse(bookingDetailsList);
        if (bookinListAdapter != null) {
            bookinListAdapter.notifyDataSetChanged();
        } else {
            bookinListAdapter = new BookinListAdapter(bookingDetailsList, getActivity());
            recyclerView.setAdapter(bookinListAdapter);
        }
    }

}
