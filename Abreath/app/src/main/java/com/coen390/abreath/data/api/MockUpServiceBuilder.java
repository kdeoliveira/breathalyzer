package com.coen390.abreath.data.api;

import android.util.Log;

import com.coen390.abreath.common.Constant;
import com.coen390.abreath.common.Utility;
import com.coen390.abreath.data.entity.UserDataEntity;
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

/**
 * Factory class for MockupRepository
 * Initialize repository and overrides json deserialization so each instance can be properly converted into a UserDataEntity object
 */
public class MockUpServiceBuilder {
    //https://stackoverflow.com/questions/48043931/how-to-use-retrofit-2-and-gson-to-model-data-where-the-json-response-keys-chan
    private static final JsonDeserializer<UserDataEntity> deserializer = (json, typeOfT, context) -> {

        JsonObject jsonObject = json.getAsJsonObject();

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'", Locale.CANADA).parse(jsonObject.get("createdAt").getAsString());
            float mapped_bac = Utility.map(jsonObject.get("bac").getAsFloat(), 0.0f, 103172.0f, 0.0f, 0.12f);
            return new UserDataEntity(jsonObject.get("username").getAsString(),  mapped_bac, date,jsonObject.get("id").getAsInt(),jsonObject.get("name").getAsString(),jsonObject.get("lastName").getAsString(), jsonObject.get("age").getAsInt(), jsonObject.get("weight").getAsInt(), jsonObject.get("height").getAsFloat()/100.0f);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    };
    private static Gson gson = new GsonBuilder().registerTypeAdapter(UserDataEntity.class, deserializer).create();

    private static Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson)).baseUrl(Constant.BASE_URL).build();


    public static <T> T create(Class<T> builderClass){
        return retrofit.create(builderClass);
    }

}
