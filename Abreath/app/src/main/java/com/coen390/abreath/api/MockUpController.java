package com.coen390.abreath.api;
// api url: https://628ea476dc478523653294a8.mockapi.io/api/v1/bac_results


import retrofit2.Retrofit;

public class MockUpController {

    private final MockUpService service;
    public MockUpController(String url){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).build();

        this.service = retrofit.create(MockUpService.class);

    }

}
