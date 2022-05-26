package com.coen390.abreath.api;
// api url: https://628ea476dc478523653294a8.mockapi.io/api/v1/bac_results




import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.coen390.abreath.model.SampleEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonDeserializer;

import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MockUpController {

    private final MockUpService service;
    private List<SampleEntity> results;

    public MockUpController(String url){
        results = new ArrayList<>();
//        https://stackoverflow.com/questions/48043931/how-to-use-retrofit-2-and-gson-to-model-data-where-the-json-response-keys-chan
        @SuppressLint("SimpleDateFormat") JsonDeserializer<SampleEntity> deserializer = (json, typeOfT, context) -> {
            JsonObject jsonObject = json.getAsJsonObject();

            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'").parse(jsonObject.get("createdAt").getAsString());

            return new SampleEntity(jsonObject.get("username").getAsString(), jsonObject.get("bac").getAsFloat(), date,jsonObject.get("id").getAsInt(),jsonObject.get("name").getAsString(),jsonObject.get("lastName").getAsString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        };

        Gson gson = new GsonBuilder().registerTypeAdapter(SampleEntity.class, deserializer).create();

        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson)).baseUrl(url).build();
        this.service = retrofit.create(MockUpService.class);
    }

    public List<SampleEntity> getUsers(ControllerListener controllerListener){
        Call<List<SampleEntity>> samples = this.service.getAll();

        samples.enqueue(
                new Callback<List<SampleEntity>>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(Call<List<SampleEntity>> call, Response<List<SampleEntity>> response) {
                        if(response.isSuccessful()){
                            assert response.body() != null;
                            //Requires a listener to advise when data is fetched since it is done asynchronously
                            controllerListener.onCompleted(response.body());

                        }else{
                            Log.e("inapp", "error");
                        }

                    }
                    @Override
                    public void onFailure(Call<List<SampleEntity>> call, Throwable t) {
                        Log.d("inapp - failure", t.toString());
                    }
                }
        );

        return results;

    }

    public interface ControllerListener{
        public void onCompleted(List<SampleEntity> data);
    }

}
