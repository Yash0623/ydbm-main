package com.ydbm.fragments;


import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ydbm.R;
import com.ydbm.activities.BookingListingActivity;
import com.ydbm.adapters.BookinListAdapter;
import com.ydbm.models.BookingDetails;
import com.ydbm.models.BookingTomorrow;
import com.ydbm.session.SessionManager;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class TomorrowFragment extends Fragment {
    RecyclerView recyclerView;
    BookinListAdapter bookinListAdapter;
    ArrayList<BookingDetails> bookings = new ArrayList<>();
    SessionManager sessionManager;
    ImageView imageViewBack;
    boolean isInternet = false;
    SearchView searchView;
    LinearLayout linearLayoutNobbokings;
    private ProgressDialog mProgressDialog;
    static BookingListingActivity.GetBookingList getBookingList;
    private SQLiteHandler sqLiteHandler;
    private FirebaseAnalytics mFirebaseAnalytics;


    public TomorrowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tomorrow, container, false);
    }

    public static TomorrowFragment newInstance(BookingListingActivity.GetBookingList getBookingListListener) {

        TomorrowFragment fragment = new TomorrowFragment();


        return fragment;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }


    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.listBookingsTomorrow);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BookingTomorrow list) {
        Log.d("onEvent", "() called with: list = [" + list + "]");

        ArrayList<BookingDetails> bookingDetailsList = new ArrayList<>();
        if (bookingDetailsList.size() > 0) {
            bookingDetailsList.clear();
        }
        bookingDetailsList.addAll(list.getModels());
        Collections.reverse(bookingDetailsList);
        if (bookinListAdapter != null) {
            bookinListAdapter.notifyDataSetChanged();
        } else {
            bookinListAdapter = new BookinListAdapter(bookingDetailsList, getActivity());
            recyclerView.setAdapter(bookinListAdapter);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sqLiteHandler = new SQLiteHandler(getActivity());
        isInternet = NetworkUtil.checkInternetConnection(getActivity());
        sessionManager = new SessionManager(getActivity());
        final String d_id = sessionManager.getUserDetailsOb().getD_id();
        if (!isInternet) {
            ArrayList<BookingDetails> bookingsTomorrow = new ArrayList<>();


            bookingsTomorrow = sqLiteHandler.fetchAllBookings("tomorrow", d_id);

            // bookingDetailsList.addAll(bookingsToday);

            if (bookingsTomorrow.size() > 0) {
                Collections.reverse(bookingsTomorrow);
                if (bookinListAdapter != null) {
                    bookinListAdapter.notifyDataSetChanged();
                } else {
                    bookinListAdapter = new BookinListAdapter(bookingsTomorrow, getActivity());
                    recyclerView.setAdapter(bookinListAdapter);
                }
            }
        }
    }

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
                    bundle1.putString("user_detail","Search By "+ sessionManager.getUserDetailsOb().getUsername()+" # "+ ConvertGMTtoIST.getCurrentDateTimeLastSeen() + " # "+newText +"Tomorrow Bookings");
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


}

