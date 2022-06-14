package com.coen390.abreath.data.api;




import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.coen390.abreath.data.entity.UserDataEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// api url: https://628ea476dc478523653294a8.mockapi.io/api/v1/bac_results

/**
 * Repository class providing handlers to access mock API via retrofit
 * Requests are made asynchronously
 */
public class MockUpRepository {

    private final MockUpService service;

    public MockUpRepository(MockUpService service){
        this.service = service;
    }

    /**
     * Fetch a single sample from the mocked API
     * @param controllerListener callback
     */
    public void fetchSample(ControllerListener<UserDataEntity> controllerListener){
        Call<UserDataEntity> samples = this.service.getById(1);

        samples.enqueue(
                new Callback<UserDataEntity>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(Call<UserDataEntity> call, Response<UserDataEntity> response) {
                        if(response.isSuccessful()){
                            assert response.body() != null;
                            //Requires a listener to advise when data is fetched since it is done asynchronously
                            controllerListener.onCompleted(response.body());

                        }else{
                            Log.e("inapp", response.message());
                        }

                    }
                    @Override
                    public void onFailure(Call<UserDataEntity> call, Throwable t) {
                        Log.d("inapp - failure", t.toString());
                    }
                }
        );

    }

    /**
     * Fetch all data from the mocked API
     * @param controllerListener callback
     */
    public void fetchAllSamples(ControllerListener<List<UserDataEntity>> controllerListener){
        Call<List<UserDataEntity>> samples = this.service.getAll();

        samples.enqueue(
                new Callback<List<UserDataEntity>>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(Call<List<UserDataEntity>> call, Response<List<UserDataEntity>> response) {
                        if(response.isSuccessful()){
                            assert response.body() != null;
                            //Requires a listener to advise when data is fetched since it is done asynchronously
                            controllerListener.onCompleted(response.body());

                        }else{
                            Log.e("inapp", response.message());
                        }

                    }
                    @Override
                    public void onFailure(Call<List<UserDataEntity>> call, Throwable t) {
                        Log.d("inapp - failure", t.toString());
                    }
                }
        );
    }

    /**
     * Listener interface used as a callback when all fetch operations have either been completed or failed
     * @param <T>
     */
    public interface ControllerListener<T>{
        void onCompleted(T data);
        void onFailure(Throwable t);
    }

}
