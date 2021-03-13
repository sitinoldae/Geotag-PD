package com.example.plndpt;


import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;


/**
 * Created by admin on 27-Mar-18.
 */

public class AppController extends MultiDexApplication {

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    public AppCompatActivity activity;
    private RequestQueue mRequestQueue;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
//    public GoogleSignInOptions getGoogleSignInOptions(){
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        return gso;
//    }
//    public GoogleApiClient getGoogleApiClient(AppCompatActivity activity, GoogleApiClient.OnConnectionFailedListener listener){
//        this.activity = activity;
//        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this.activity, listener)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, getGoogleSignInOptions()).build();
//        return mGoogleApiClient;
//    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
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

}