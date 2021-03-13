package com.example.plndpt;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MyApi {

    @FormUrlEncoded
    @POST("signup.php")
    Call<ResponseBody> createNewAcount(@Field("name") String username,
                                       @Field("email") String email,
                                       @Field("password") String pass
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<MyUserData> logIn(@Field("email") String email,
                           @Field("password") String pass
    );


}
