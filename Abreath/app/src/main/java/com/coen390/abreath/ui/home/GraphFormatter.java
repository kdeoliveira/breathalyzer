package com.coen390.abreath.ui.home;

import android.util.Log;

import com.coen390.abreath.data.entity.TestResultEntity;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

/**
 * Formatter class implementation used by the MPAndroidChart library
 * Properly displays the x-axis label according to the number of elements present
 */
public class GraphFormatter extends ValueFormatter {
    private boolean formatMonth;
    private int size;
    private List<TestResultEntity> listData;
    public GraphFormatter(List<TestResultEntity> listData){
        this.listData = listData;
        this.size = listData.size();
        this.formatMonth = listData.get(0).compareToMonth(listData.get(this.size - 1).getCreatedAtDate());
    }
    @Override
    public String getFormattedValue(float value) {
        int index = (int) (value + 0.5f);
        if(size <= index){
            return "";
        }
        if(formatMonth){
            if(size > 6){
                if(index % 2 == 0)
                    return listData.get(index).getCreatedAt();
                else
                    return "";
            }else{
                return listData.get(index).getCreatedAt();
            }
        }else{
            if(size > 6){
                if(index % 2 == 0)
                    return listData.get(index).getCreatedAt("dd-MMM");
                else
                    return "";
            }else{
                return listData.get(index).getCreatedAt("dd-MMM");
            }
        }

    }
}
