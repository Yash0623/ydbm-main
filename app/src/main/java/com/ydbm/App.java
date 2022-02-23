package com.ydbm;

import android.app.Application;
import androidx.multidex.MultiDex;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Maitri
 */

public class App extends Application {
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    public static final String TAG = App.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private static App mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sAnalytics = GoogleAnalytics.getInstance(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mInstance=this;
        MultiDex.install(this);
        init();
    }

    private void init()
    {
        initFirebase();
    }

    private void initFirebase()
    {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    public static synchronized App getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }

    public void trackScreenView(String screenName,String userName,String name) {
        Tracker t = getDefaultTracker();
        // Set screen name.
        t.setScreenName(screenName);
        t.setTitle(name);
        t.setClientId(userName);
        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }
}
