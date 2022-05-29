package com.coen390.abreath.domain;

import android.util.Log;

import androidx.annotation.Nullable;

import com.coen390.abreath.common.Tuple;
import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.entity.UserDataEntity;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

public class GetUserInfoUseCase extends Observable implements UseCase {
    private final MockUpRepository repository;

    public GetUserInfoUseCase(MockUpRepository mock){
        this.repository = mock;
    }

    @Override
    public Object call(@Nullable Object payload) {
        this.repository.fetchSample(new MockUpRepository.ControllerListener<UserDataEntity>() {
            @Override
            public void onCompleted(UserDataEntity data) {
                Log.d("inapp", data.getLast_name());
                setChanged();
                notifyObservers(data);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("[debug]", t.toString());
            }
        });

        return payload;
    }
}
