package com.ciuc.andrii.my_retrofit_1.interfaces;

import com.ciuc.andrii.my_retrofit_1.pojo.MyRandomUser;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Hari on 20/11/17.
 */

public interface RandomUsersApi {

    @GET("api")
    Call<MyRandomUser> getRandomUsers(@Query("results") double size);
}