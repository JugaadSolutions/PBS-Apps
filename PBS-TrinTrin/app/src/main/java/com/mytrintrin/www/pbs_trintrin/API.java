package com.mytrintrin.www.pbs_trintrin;


public class API {

  //public static  final String Baseurl="http://43.251.80.79:13070/api/";   //PBS Internal Test
 //public static  final String Baseurl="http://43.251.80.79:13060/api/";   //PBS Development
  //public static  final String Baseurl="http://43.251.80.79:13065/api/";   //PBS Admin Test : External Test Instance
  //public static  final String Baseurl="https://www.mytrintrin.com:13070/api/";   //PBS Admin Test : SDC Instance
  public static  final String Baseurl="https://www.mytrintrin.com:13060/api/";   //PBS Admin : SDC Instance

    public static final String register =Baseurl+"member";

    public static final String loginapi=Baseurl+"auth/login";

    public static final String loginnfcapi = Baseurl+"auth/login";

    public static final String getmembersapi=Baseurl+"member";

    public static final String addmember =Baseurl+"member/add";

    public static final String prospectivemember =Baseurl+"member";

    public static final String cancelmemberrequest =Baseurl+"member/";

    public static final String cancelmembership =Baseurl+"member/";

    public  static final String assignmembeship =Baseurl+"member/";

    public  static final String getplans =Baseurl+"membership/";

    public  static final String addcredit =Baseurl+"member/";

    public  static final String addcard =Baseurl+"member/";

    public static final String redistribution =Baseurl+"redistributionport/";

    public static final String checkinurl =Baseurl+"transactions/checkin/app";

    public  static final String checkouturl=Baseurl+"transactions/checkout/app";

    public  static final String fleetidurl=Baseurl+"fleet";

    public  static final String RVidurl=Baseurl+"redistributionport";

    public  static final String HAidurl=Baseurl+"holdingport";

    public  static final String MCidurl=Baseurl+"maintenanceport";

    public  static final String forgotpassword = Baseurl+"users/forgotpassword";

    public  static final String changepassword = Baseurl+"users/";

    public  static final String searchmember = Baseurl+"member/search/";
    //public  static final String searchmember = Baseurl+"users/search/";
    //public  static final String searchmember  ="https://www.mytrintrin.com:13070/api/users/search/";

    public  static final String updatemember = Baseurl+"member/";

    public  static final String getdepartment =Baseurl+ "employee/departments";

    public  static final String getrvempcycles = Baseurl+"employee/";

    public static final String alldockingstation =Baseurl+"dockstation";

    public static final String maintaincechecklist =Baseurl+"keysettings/Maintenance";

    public static final String repairchecklist =Baseurl+"keysettings/Repair";

    public static final String maintainedcycleinDS =Baseurl+"maintenancecenter/maintained";

    public static final String repairedcycleinDS =Baseurl+"maintenancecenter/repaired";

    public static final String ticketsfromrc =Baseurl+"tickets";

    public static final String addtofleet =Baseurl+"vehicle";

    public static final String gettopupplans =Baseurl+"topups";

    public static final String getmcemplist =Baseurl+"employee/mcstaff/emp";

    public static final String verifyotp =Baseurl+"member/verifyotp";

    public static final String resendotp =Baseurl+"member/requestotp";

    public static final String ridehistory =Baseurl+"transactions/myrides/member/";

    public static final String paymenthistory =Baseurl+"paymenttransaction/member/";

    public static final String getassignedregistrationcenter =Baseurl+"registrationcenter";

    public static final String getallvehicles =Baseurl+"vehicle";

    public static final String forcebicycle ="http://43.251.80.79:13080/api/transactions/checkin/app";

    public static final String updateemail = Baseurl+"member/email/";

    public static final String usersync ="http://43.251.80.79:13090/api/users/sync/";

    public static final  String releasecycle = "http://43.251.80.79:13090/api/transactions/unlock/app/test";

    public static final String createcheckout = "http://43.251.80.79:13090/api/transactions/checkout/app";

    public static final String opencheckout = "http://43.251.80.79:13090/api/transactions/open/";
}
