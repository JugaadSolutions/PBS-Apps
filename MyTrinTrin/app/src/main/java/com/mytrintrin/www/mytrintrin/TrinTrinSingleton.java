package com.mytrintrin.www.mytrintrin;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by gwr on 1/26/17.
 */

public class TrinTrinSingleton {

    private static TrinTrinSingleton minstance;
    private static Context mctx;
    private RequestQueue requestQueue;


    private TrinTrinSingleton(Context context) {
        mctx = context;
        requestQueue = getRequestQueue();
    }


    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized TrinTrinSingleton getInstance(Context context) {
        if (minstance == null) {
            minstance = new TrinTrinSingleton(context);
        }
        return minstance;
    }

    public <T> void addtorequestqueue(Request<T> request) {
        requestQueue.add(request);
    }
}