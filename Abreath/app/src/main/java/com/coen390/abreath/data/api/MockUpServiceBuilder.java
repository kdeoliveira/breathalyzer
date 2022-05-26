package com.coen390.abreath.data.api;

import com.coen390.abreath.data.model.SampleEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MockUpServiceBuilder {
    //https://stackoverflow.com/questions/48043931/how-to-use-retrofit-2-and-gson-to-model-data-where-the-json-response-keys-chan

    private static final String BASE_URL = "https://628ea476dc478523653294a8.mockapi.io/";
    private static final JsonDeserializer<SampleEntity> deserializer = (json, typeOfT, context) -> {

        JsonObject jsonObject = json.getAsJsonObject();

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'", Locale.CANADA).parse(jsonObject.get("createdAt").getAsString());

            return new SampleEntity(jsonObject.get("username").getAsString(), jsonObject.get("bac").getAsFloat(), date,jsonObject.get("id").getAsInt(),jsonObject.get("name").getAsString(),jsonObject.get("lastName").getAsString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    };
    private static Gson gson = new GsonBuilder().registerTypeAdapter(SampleEntity.class, deserializer).create();

    private static Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson)).baseUrl(BASE_URL).build();


    public static <T> T create(Class<T> builderClass){
        return retrofit.create(builderClass);
    }

}
