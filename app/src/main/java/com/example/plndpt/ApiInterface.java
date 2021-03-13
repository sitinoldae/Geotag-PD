package com.example.plndpt;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("SMS/Smsservice.aspx")
    Call<String> getVerifyMobileNumber(@Query("mobile") String mobile_number, @Query("sms") String otp_sms);


    @GET("edu_test/WebService.asmx?op=selectDrain")
    Call<String> getDivision(@Query("77.223719") String log, @Query("28.68198") String lat);


    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("edu_test/WebService.asmx/UPLOD_IMP")
    Call<JsonElement> getUploadIssueForm(@Field("app_id") String appID,
                                         @Field("f_name") String first_name,
                                         @Field("l_name") String last_name,
                                         @Field("email") String email,
                                         @Field("mobile") String mobile,
                                         @Field("type_req") String req_type,
                                         @Field("lat") String latitude,
                                         @Field("log") String longitude,
                                         @Field("image") String image,
                                         @Field("remarks") String remark,
                                         @Field("division") String division,
                                         @Field("status") String status
    );


    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("edu_test/WebService.asmx/Login")
    Call<JsonElement> getUser_Registration(@Field("name") String user_namee,
                                           @Field("email") String user_emaill,
                                           @Field("mobile") String user_mobile,
                                           @Field("date") String user_date);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("edu_test/WebService.asmx/updateUser")
    Call<JsonElement> getUser_profile_update(@Field("name") String user_nameee,
                                             @Field("email") String user_emailll,
                                             @Field("mobile") String user_mobilee,
                                             @Field("date") String user_datee);


    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("edu_test/WebService.asmx/selectUser")
    Call<JsonElement> get_Log_IN_Info(@Field("mobile") String mobile_number);


}
