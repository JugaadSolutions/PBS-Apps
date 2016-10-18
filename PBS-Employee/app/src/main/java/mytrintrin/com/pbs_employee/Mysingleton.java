package mytrintrin.com.pbs_employee;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by hp on 10/3/2016.
 */
public class Mysingleton {

    private  static  Mysingleton minstance;
    private static Context mctx;
    private RequestQueue requestQueue;


    private Mysingleton(Context context)
    {
        mctx= context;
        requestQueue= getRequestQueue();
    }

    public  RequestQueue getRequestQueue()
    {
        if(requestQueue == null)
        {
            requestQueue = Volley.newRequestQueue(mctx.getApplicationContext());

        }
        return requestQueue;
    }

    public  static  synchronized Mysingleton getInstance(Context context)
    {
        if(minstance==null)
        {
            minstance = new Mysingleton(context);
        }
        return  minstance;
    }

    public<T> void addtorequestqueue(Request<T> request)
    {
        requestQueue.add(request);
    }


}
