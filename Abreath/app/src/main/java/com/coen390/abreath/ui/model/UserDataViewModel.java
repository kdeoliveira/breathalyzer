package com.coen390.abreath.ui.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.entity.UserDataEntity;
import com.coen390.abreath.domain.GetHistoryUseCase;
import com.coen390.abreath.common.Tuple;
import com.coen390.abreath.domain.GetUserInfoUseCase;
import com.github.mikephil.charting.data.BarData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class UserDataViewModel extends ViewModel implements Observer {
    private MutableLiveData<Tuple<List<String>, BarData>> samples;
    private MutableLiveData<UserDataEntity> info_user;

    private final GetHistoryUseCase getHistoryUseCase;
    private final GetUserInfoUseCase getUserInfoUseCase;

    public UserDataViewModel(MockUpRepository mockUpRepository) {
        getUserInfoUseCase = new GetUserInfoUseCase();
        samples = new MutableLiveData<>();
        info_user = getUserInfoUseCase.call(null);

//        MockUpRepository mockUpRepository = new MockUpRepository(MockUpServiceBuilder.create(MockUpService.class));
        getHistoryUseCase = new GetHistoryUseCase(mockUpRepository);

        //Async call. Check https://developer.android.com/topic/libraries/architecture/livedata#java for for specific implementation
        getHistoryUseCase.addObserver(this);


        getHistoryUseCase.call(null);

    }

    public LiveData<Tuple<List<String>, BarData>> getUserData(){
        if(samples != null){
            return this.samples;
        }
        samples = new MutableLiveData<>();
        return samples;
    }

    public LiveData<UserDataEntity> getUserInfo(){
        return Transformations.switchMap(info_user, input -> getUserInfoUseCase.call(null));
    }

    @Override
    public void update(Observable observable, Object o) {

        if(o instanceof Tuple){
            samples.postValue( (Tuple<List<String>, BarData>) o );
        }else if(o instanceof UserDataEntity){
            info_user.postValue((UserDataEntity) o);
        }

    }
}
