package com.coen390.abreath.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.api.MockUpService;
import com.coen390.abreath.data.api.MockUpServiceBuilder;
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
        samples = new MutableLiveData<>();
//        MockUpRepository mockUpRepository = new MockUpRepository(MockUpServiceBuilder.create(MockUpService.class));
        getHistoryUseCase = new GetHistoryUseCase(mockUpRepository);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid;
        if(user != null)
            uid = user.getUid();
        else
            uid = "";
        DatabaseReference auth = FirebaseDatabase.getInstance().getReference().child("user");

        getUserInfoUseCase = new GetUserInfoUseCase(auth.child(uid));

        //Async call. Check https://developer.android.com/topic/libraries/architecture/livedata#java for for specific implementation
        getHistoryUseCase.addObserver(this);

        getUserInfoUseCase.addObserver(this);

        getHistoryUseCase.call(null);
        getUserInfoUseCase.call(null);
    }

    public LiveData<Tuple<List<String>, BarData>> getUserData(){
        if(samples != null){
            return this.samples;
        }
        samples = new MutableLiveData<>();
        return samples;
    }

    public LiveData<UserDataEntity> getUserInfo(){
        if(info_user != null){
            return this.info_user;
        }
        info_user = new MutableLiveData<>();
        return info_user;
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
