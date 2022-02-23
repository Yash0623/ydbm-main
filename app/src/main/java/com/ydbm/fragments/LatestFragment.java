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
import com.ydbm.adapters.InquiryListAdapter;
import com.ydbm.models.BookingDetails;
import com.ydbm.session.SessionManager;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class LatestFragment extends Fragment {

    RecyclerView recyclerView;
    InquiryListAdapter bookinListAdapter;
    ArrayList<BookingDetails> all = new ArrayList<>();
    SessionManager sessionManager;

    boolean isInternet = false;
    SQLiteHandler sqLiteHandler;
    private String d_id;
    private LinearLayout linearLayoutenquiries;
    private FirebaseAnalytics mFirebaseAnalytics;

    public LatestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_latest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    public static LatestFragment newInstance() {
        Log.d("AJDGHADGDGH", "8");
        LatestFragment fragment = new LatestFragment();
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
        linearLayoutenquiries = getActivity().findViewById(R.id.noBokingsLinear);
        sessionManager = new SessionManager(getActivity());
        d_id = sessionManager.getUserDetailsOb().getD_id();
        all = sqLiteHandler.fetchAllInquiries(d_id);
        Collections.reverse(all);

        if (bookinListAdapter != null) {
            bookinListAdapter.notifyDataSetChanged();
        } else {
            bookinListAdapter = new InquiryListAdapter(all, getActivity(), 1);
            recyclerView.setAdapter(bookinListAdapter);

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
                    bundle1.putString("user_detail",sessionManager.getUserDetailsOb().getUsername()+" # "+ ConvertGMTtoIST.getCurrentDateTimeLastSeen() + " # "+newText +" in "+"Latest Inquiries");
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
        recyclerView = view.findViewById(R.id.listAltestInquiries);
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
        Log.d("AJDGHADGDGH", "2");
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getValueFromEventBus(String data) {
        Log.d("JDSKHJHJDS123", "122335");
        if (data.equals("latestdata")) {
            all.clear();

            ArrayList<BookingDetails> details = sqLiteHandler.fetchAllInquiries(d_id);
            Collections.reverse(details);
            all.addAll(details);

            Log.d("DJKAJKKJDK1233", all.size() + "==");
            if (bookinListAdapter != null) {
                bookinListAdapter.notifyDataSetChanged();
            } else {
                bookinListAdapter = new InquiryListAdapter(all, getActivity(), 1);
                recyclerView.setAdapter(bookinListAdapter);

            }
        }
    }
}
