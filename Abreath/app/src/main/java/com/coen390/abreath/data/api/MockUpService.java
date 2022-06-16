package com.coen390.abreath.data.api;

import com.coen390.abreath.data.entity.UserDataEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Service API used for accessing the remote API
 * Implementation based on the guidelines provided by retrofit
 */
public interface MockUpService extends Service {

    @GET("api/v1/bac_results?sortBy=createdAt&order=desc")
    Call<List<UserDataEntity>> getAll();


    @GET("api/v1/bac_results/{id}")
    Call<UserDataEntity> getById(@Path("id") int id);

    @GET("api/v1/bac_results/?search={val}")
    Call<List<UserDataEntity>> search(@Path("val") String val);

    @GET("api/v1/bac_results/?name={name}")
    Call<List<UserDataEntity>> getByName(@Path("name") String name);

}
