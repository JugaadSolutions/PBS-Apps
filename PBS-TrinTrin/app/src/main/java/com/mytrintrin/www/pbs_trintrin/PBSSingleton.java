package com.mytrintrin.www.pbs_trintrin;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by hp on 11/27/2016.
 */
public class PBSSingleton {

    private  static PBSSingleton minstance;
    private static Context mctx;
    private RequestQueue requestQueue;


    private PBSSingleton(Context context)
    {
        mctx=context;
        requestQueue=getRequestQueue();
    }



    public RequestQueue getRequestQueue()
    {
        if (requestQueue==null)
        {
            requestQueue= Volley.newRequestQueue(mctx.getApplicationContext());
        }
           return requestQueue;
    }

    public  static  synchronized PBSSingleton getInstance(Context context)
    {
        if(minstance==null)
        {
            minstance = new PBSSingleton(context);
        }
        return  minstance;
    }

    public<T> void addtorequestqueue(Request<T> request)
    {
        requestQueue.add(request);
    }
}
