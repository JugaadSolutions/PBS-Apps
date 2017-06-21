package com.mytrintrin.www.pbs_sync;


public class API {

    //public static  final String Baseurl="http://43.251.80.79:13070/api/";   //PBS Internal Test
  //public static  final String Baseurl="http://43.251.80.79:13060/api/";   //PBS Development
 //   public static  final String Baseurl="http://43.251.80.79:13065/api/";   //PBS Admin Test : External Test Instance
  // public static  final String Baseurl="https://43.251.80.72:13060/api/";   //PBS Admin Test : Internal  sync Test Instance
   public static  final String Baseurl="https://www.mytrintrin.com:13060/api/";   //PBS Admin Test : SDC Test Instance

    public static final String syncallusers = Baseurl+"sync/user";

    public static final String syncallvehicles = Baseurl+"sync/vehicle";

    public static final String syncuser = Baseurl+"sync/user/";

    public static final String syncvehicle = Baseurl+"sync/vehicle/";

    public static final String getopentransactions = Baseurl+"transactions";

    public static final String syncclearcheckout = Baseurl+"transactions/";

    public static final String alldockingstation =Baseurl+"dockstation";

    public static final String getallvehicles =Baseurl+"vehicle";

    public static final String createcheckin ="http://43.251.80.79:13080/api/transactions/checkin/bridge";

    public static final String createcheckout ="http://43.251.80.79:13080/api/transactions/checkout/bridge";

    public static final String getalluser = Baseurl+"member/qweuird78fj3498asdjkfhahsysd98y4rsdjhf";

    public static final String getallmutilpletransactions =Baseurl+"transactions/completed";
   // public static final String getallmutilpletransactions= "http://43.251.80.79:13060/api/transactions/completed";

    public static final  String clearallcheckouts = Baseurl+"transactions/checkout/all";
   //public static final  String clearallcheckouts = "http://43.251.80.79:13060/api/transactions/checkout/all";

    public static  final String getallopencheckins= Baseurl+"transactions/checkin";
    //public static  final String getallopencheckins= "http://43.251.80.79:13060/api/transactions/checkin";

    public static final String syncclearcheckin = Baseurl+"transactions/checkin/";
    //public static final String syncclearcheckin = "http://43.251.80.79:13060/api/transactions/checkin/";

    public static final  String clearallcheckins = Baseurl+"transactions/checkin/clear/open";
    //public static final  String clearallcheckins = "http://43.251.80.79:13060/api/transactions/checkin/clear/open";

}
