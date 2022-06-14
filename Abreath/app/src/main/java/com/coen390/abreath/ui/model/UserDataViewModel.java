package com.coen390.abreath.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.coen390.abreath.data.entity.TestResultEntity;
import com.coen390.abreath.data.entity.UserDataEntity;

import com.coen390.abreath.domain.GetLastLevelsUseCase;
import com.coen390.abreath.domain.GetUserInfoUseCase;
import java.util.List;


/**
 * View model holding the User information data that will be then linked to the UI
 * View model initially performs the required Use Cases
 */
public class UserDataViewModel extends ViewModel {
    private MutableLiveData<List<TestResultEntity>> samples;
    private MutableLiveData<UserDataEntity> info_user;


//    private final GetHistoryUseCase getHistoryUseCase;
    private final GetUserInfoUseCase getUserInfoUseCase;
    private final GetLastLevelsUseCase getLastLevelsUseCase;

    public UserDataViewModel() {
//        new SaveLastLevelUseCase().call(1.5f);
        getUserInfoUseCase = new GetUserInfoUseCase();
        getLastLevelsUseCase = new GetLastLevelsUseCase();
        samples = getLastLevelsUseCase.call(null);
        info_user = getUserInfoUseCase.call(null);

//        MockUpRepository mockUpRepository = new MockUpRepository(MockUpServiceBuilder.create(MockUpService.class));
//        getHistoryUseCase = new GetHistoryUseCase(mockUpRepository);

        //Async call. Check https://developer.android.com/topic/libraries/architecture/livedata#java for for specific implementation
//        getHistoryUseCase.addObserver(this);
//        getHistoryUseCase.call(null);

    }

    /**
     * Data is refreshed if model has been modified
     */
    public LiveData<List<TestResultEntity>> getUserData(){
        return Transformations.switchMap(samples, input -> getLastLevelsUseCase.call(null));
    }


    /**
     * Data is refreshed if model has been modified
     */
    public LiveData<UserDataEntity> getUserInfo(){
        return Transformations.switchMap(info_user, input -> getUserInfoUseCase.call(null));
    }
}
