package com.mytrintrin.www.mytrintrin;

/**
 * Created by gwr on 1/26/17.
 */

public class API {


    public static  final String Baseurl="https://www.mytrintrin.com:13060/api/";   //SDC Instance

    public static String login = Baseurl+"auth/login";
    public static String register =Baseurl+"member";
    public static String alldockingstation =Baseurl+"dockstation";
    public static String allregistrationcentre =Baseurl+"registrationcenter";
    public static String changepassword =Baseurl+"users/";
    public static String forgotpassword =Baseurl+"users/forgotpassword";
    public static String getmemberdetail =Baseurl+"member/";
    public static String paymenthistory =Baseurl+"paymenttransaction/member/";
    public static String ridehistory =Baseurl+"transactions/myrides/member/";
    public static String feedback =Baseurl+"tickets";
    public static String updatepic =Baseurl+"member/";
    public static String getplans =Baseurl+"membership/v1/new";
    public static  String topup =Baseurl+"member/";
    public static final String gettopupplans =Baseurl+"topups";
    public static final String ticketsfromuser =Baseurl+"tickets";
    public static final String getusertickets =Baseurl+"tickets/user/";
    public static final String replytotickets =Baseurl+"tickets/";
    //public static final String selectplan_longterm = "https://www.mytrintrin.com/app/androidPaygovRequest.php";
    public static final String selectplan_longterm = "https://www.mytrintrin.com/appFail/androidPaygovRequest.php";
    public static final String selectplan_shorterm = "https://www.mytrintrin.com/apptest/androidPaygovRequest.php";
    public static final String getmessage = Baseurl+"keysettings/app/msg";
    public static final String onlinerefund = Baseurl+"onlinerefund";

}
