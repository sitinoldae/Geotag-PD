package com.example.plndpt;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface2 {


    //String URL_BASE = "http://59.180.230.28:8080/waterlogging/gsdl/waterlogging/";
  //  String URL_BASE = "http://10.0.2.2:8080/waterlogging/gsdl/waterlogging/";

    String URL_LOGIN = "http://map.gsdl.org.in:8080/planningdpt/";
    // String URL_LOGIN="http://10.0.2.2:8080/";


    String URL_REPORT = "http://map.gsdl.org.in:8080/planningdpt/";
    //String URL_REPORT="http://10.0.2.2:8080/";


//String URL_UPDATESTATUS = "http://10.0.2.2:8080/";

    String URL_UPDATESTATUS = "http://map.gsdl.org.in:8080/planningdpt/";

    @Headers("Content-Type: application/json")
    @POST("addReport")
    Call<User> getUser(@Body String body);


    @Headers("Content-Type: application/json")
    @POST("addLogin")
    Call<MyUserData> addLogin(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("projectStatusUpdate")
    Call<User> updateStatus(@Body String body);
}
