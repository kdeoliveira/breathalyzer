package com.coen390.abreath.data.api;
// api url: https://628ea476dc478523653294a8.mockapi.io/api/v1/bac_results




import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.coen390.abreath.data.model.SampleEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MockUpRepository {

    private final MockUpService service;

    public MockUpRepository(MockUpService service){
        this.service = service;
    }

    public void fetchSample(ControllerListener<SampleEntity> controllerListener){
        Call<SampleEntity> samples = this.service.getById(1);

        samples.enqueue(
                new Callback<SampleEntity>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(Call<SampleEntity> call, Response<SampleEntity> response) {
                        if(response.isSuccessful()){
                            assert response.body() != null;
                            //Requires a listener to advise when data is fetched since it is done asynchronously
                            controllerListener.onCompleted(response.body());

                        }else{
                            Log.e("inapp", response.message());
                        }

                    }
                    @Override
                    public void onFailure(Call<SampleEntity> call, Throwable t) {
                        Log.d("inapp - failure", t.toString());
                    }
                }
        );

    }


    public void fetchAllSamples(ControllerListener<List<SampleEntity>> controllerListener){
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
                            Log.e("inapp", response.message());
                        }

                    }
                    @Override
                    public void onFailure(Call<List<SampleEntity>> call, Throwable t) {
                        Log.d("inapp - failure", t.toString());
                    }
                }
        );
    }

    public interface ControllerListener<T>{
        void onCompleted(T data);
        void onFailure(Throwable t);
    }

}
