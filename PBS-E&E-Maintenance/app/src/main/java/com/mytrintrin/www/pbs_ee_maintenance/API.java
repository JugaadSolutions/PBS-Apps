package com.mytrintrin.www.pbs_ee_maintenance;


public class API {

   // public static  final String Baseurl="http://43.251.80.79:13070/api/";   //PBS Internal Test
    public static  final String Baseurl="http://43.251.80.79:13060/api/";   //PBS Development
 //   public static  final String Baseurl="http://43.251.80.79:13065/api/";   //PBS Admin Test : External Test Instance

    public static final String register =Baseurl+"member";

    public static final String loginapi=Baseurl+"auth/login";

    public static  final String emaintenance = Baseurl+"enereports";

    public static  final String portmaintenance = Baseurl+"ports/report";

    public static final String alldockingstation =Baseurl+"dockstation";
}
