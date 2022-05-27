package com.coen390.abreath.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.api.MockUpService;
import com.coen390.abreath.data.api.MockUpServiceBuilder;
import com.coen390.abreath.domain.GetHistoryUseCase;
import com.coen390.abreath.common.Tuple;
import com.github.mikephil.charting.data.BarData;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class UserDataViewModel extends ViewModel implements Observer {
    private MutableLiveData<Tuple<List<String>, BarData>> samples;
    private final GetHistoryUseCase getHistoryUseCase;

    public UserDataViewModel(MockUpRepository mockUpRepository) {
        samples = new MutableLiveData<>();
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

    @Override
    public void update(Observable observable, Object o) {
        samples.postValue( (Tuple<List<String>, BarData>) o );
    }
}
