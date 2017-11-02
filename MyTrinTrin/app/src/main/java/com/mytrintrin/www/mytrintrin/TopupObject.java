package com.mytrintrin.www.mytrintrin;

/**
 * Created by gwr on 9/17/17.
 */

public class TopupObject {

    private String mPlanName;
    private String mUsagefee;
    private String mValidity;
    private String mTopupamount;


    public TopupObject(String planname,String usagefee,String validity,String topupamount)

    {
        mPlanName =planname;
        mUsagefee = usagefee;
        mValidity = validity;
        mTopupamount = topupamount;
    }

    public String getmPlanName() {
        return mPlanName;
    }

    public String getmUsagefee() {
        return mUsagefee;
    }

    public String getmValidity() {
        return mValidity;
    }

    public String getmTopupamount() {
        return mTopupamount;
    }
}
