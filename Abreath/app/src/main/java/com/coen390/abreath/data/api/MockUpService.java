package com.coen390.abreath.data.api;

import com.coen390.abreath.data.model.SampleEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MockUpService {

    @GET("api/v1/bac_results")
    Call<List<SampleEntity>> getAll();


    @GET("api/v1/bac_results/{id}")
    Call<SampleEntity> getById(@Path("id") int id);

    @GET("api/v1/bac_results/?search={val}")
    Call<List<SampleEntity>> search(@Path("val") String val);

    @GET("api/v1/bac_results/?name={name}")
    Call<List<SampleEntity>> getByName(@Path("name") String name);

}
