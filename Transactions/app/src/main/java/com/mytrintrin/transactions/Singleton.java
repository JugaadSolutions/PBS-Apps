package com.mytrintrin.transactions;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by hp on 10/18/2016.
 */
public class Singleton {

    private static Context mctx;
    private  static  Singleton minstance;
    public RequestQueue requestQueue;


    private Singleton(Context context)
    {
        mctx=context;
        requestQueue= getRequestQueue();
    }


    public RequestQueue getRequestQueue()
    {
        if(requestQueue==null)
        {
            requestQueue= Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return  requestQueue;
    }

    public static  synchronized Singleton getInstance(Context context)
    {
            if(minstance== null)
            {
                minstance = new Singleton(context);
            }

        return  minstance;

        }

    public <T> void addtorequest(Request<T> request)
    {
        requestQueue.add(request);
    }
}
