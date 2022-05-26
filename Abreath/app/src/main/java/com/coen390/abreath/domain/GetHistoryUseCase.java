package com.coen390.abreath.domain;


import androidx.annotation.Nullable;

import com.coen390.abreath.R;
import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.model.SampleEntity;
import com.coen390.abreath.util.Tuple;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

public class GetHistoryUseCase extends Observable implements UseCase {

    private final MockUpRepository repository;

    public GetHistoryUseCase(MockUpRepository mock){
        repository = mock;
    }

    @Override
    public Object call(@Nullable Object payload) {
        ArrayList<BarEntry> values = new ArrayList<>();
        List<String> timeline = new ArrayList<>();
        this.repository.fetchAllSamples(new MockUpRepository.ControllerListener<List<SampleEntity>>() {
            @Override
            public void onCompleted(List<SampleEntity> data) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-DD", Locale.CANADA);
                for(int k = 0; k < data.size(); k++){
                    values.add(new BarEntry(k+1, data.get(k).getBac()));
                    timeline.add(simpleDateFormat.format(data.get(k).getCreated_at()));
                }
                timeline.add("");

                BarDataSet dataSet = new BarDataSet(values, "Previous Samples");

                dataSet.setColor(R.color.primaryDarkColor);
                dataSet.setFormSize(15f);
                dataSet.setDrawValues(false);
                dataSet.setValueTextSize(12f);


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
