package com.coen390.abreath.domain;


import androidx.annotation.Nullable;

import com.coen390.abreath.R;
import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.entity.UserDataEntity;
import com.coen390.abreath.common.Tuple;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

/**
 * Get history results of each user from Mock Up API
 * Notifies passive object of any new object fetched
 */
public class GetHistoryUseCase extends Observable implements UseCase {

    private final MockUpRepository repository;

    public GetHistoryUseCase(MockUpRepository mock){
        repository = mock;
    }

    @Override
    public Object call(@Nullable Object payload) {
        ArrayList<BarEntry> values = new ArrayList<>();
        List<String> timeline = new ArrayList<>();
        this.repository.fetchAllSamples(new MockUpRepository.ControllerListener<List<UserDataEntity>>() {
            @Override
            public void onCompleted(List<UserDataEntity> data) {
                long yearTime = 365L *24*60*60*1000;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-dd", Locale.CANADA);
                for(int k = 0; k < data.size(); k++){
                    //Discard data that was created later than a year ago
                    if(data.get(k).getCreated_at().getTime() < (new Date().getTime() - yearTime)) continue;
                    values.add(new BarEntry(k+1, data.get(k).getBac()));
                    timeline.add(simpleDateFormat.format(data.get(k).getCreated_at()));
                }
                timeline.add("");

                BarDataSet dataSet = new BarDataSet(values, "Previous Samples");

                BarData barData = new BarData(dataSet);

                setChanged();
                notifyObservers(new Tuple<>(timeline, barData));

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        return payload;
    }

}
