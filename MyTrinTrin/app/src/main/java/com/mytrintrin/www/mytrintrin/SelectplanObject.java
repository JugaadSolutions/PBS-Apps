package com.mytrintrin.www.mytrintrin;

/**
 * Created by gwr on 9/18/17.
 */

public class SelectplanObject {

    private String mPlanname;
    private String mValidity;
    private String mSecurityfee;
    private String mProcessingfee;
    private String mUsageFee;
    private String mPlantype;
    private String mPlandescription;
    private int mPlanTotal;

    public SelectplanObject(String planname,String validity,String securityfee,String processingfee,String usagegfee,int plantotal,String plantype,String plandescription)
    {

        mPlanname = planname;
        mValidity = validity;
        mSecurityfee = securityfee;
        mProcessingfee = processingfee;
        mUsageFee = usagegfee;
        mPlanTotal = plantotal;
        mPlantype = plantype;
        mPlandescription = plandescription;
    }

    public String getmPlanname() {
        return mPlanname;
    }

    public String getmValidity() {
        return mValidity;
    }

    public String getmSecurityfee() {
        return mSecurityfee;

    }

    public String getmProcessingfee() {
        return mProcessingfee;
    }

    public String getmUsageFee() {
        return mUsageFee;
    }

    public int getmPlanTotal() {
        return mPlanTotal;
    }

    public String getmPlantype() {
        return mPlantype;
    }

    public String getmPlandescription() {
        return mPlandescription;
    }
}
