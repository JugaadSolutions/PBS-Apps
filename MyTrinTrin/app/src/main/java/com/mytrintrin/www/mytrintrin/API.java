package com.mytrintrin.www.mytrintrin;

/**
 * Created by gwr on 1/26/17.
 */

public class API {

    //public static  String Baseurl ="https://test.mytrintrin.com:13065/api/";
    //public static  String Baseurl ="http://43.251.80.79:13070/api/";
    public static  String Baseurl ="http://www.mytrintrin.com:13060/api/";

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
    public static String getplans =Baseurl+"membership/";
    public static  String topup =Baseurl+"member/";
    public static final String gettopupplans =Baseurl+"topups";
    public static final String ticketsfromuser =Baseurl+"tickets";
    public static final String getusertickets =Baseurl+"tickets/user/";


    /*without https*/
   /* public static String login = "http://43.251.80.79:13065/api/auth/login";
    public static String register ="http://43.251.80.79:13065/api/member";
    public static String alldockingstation ="http://43.251.80.79:13065/api/dockstation";
    public static String allregistrationcentre ="http://43.251.80.79:13065/api/registrationcenter";
    public static String changepassword ="http://43.251.80.79:13065/api/users/";
    public static String forgotpassword ="http://43.251.80.79:13065/api/users/forgotpassword";
    public static String getmemberdetail ="http://43.251.80.79:13065/api/member/";
    public static String paymenthistory ="http://43.251.80.79:13065/api/paymenttransaction/member/";
    public static String ridehistory ="http://43.251.80.79:13065/api/transactions/myrides/member/";
    public static String feedback ="http://43.251.80.79:13065/api/tickets";
    public static String updatepic ="http://43.251.80.79:13065/api/member/";
    public static String getplans ="http://43.251.80.79:13065/api/membership/";
    public static String topup ="http://43.251.80.79:13065/api/member/";
    */
}
