package com.coen390.abreath.data.entity;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestResultEntity {
    private float mTestResults;
    private Date mCreatedAt;

    public TestResultEntity(float testResults, Date createdAt){
        this.mTestResults = testResults;
        this.mCreatedAt = createdAt;
    }

    public float getTestResult(){
        return mTestResults;
    }

    public String getCreatedAt(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-dd", Locale.CANADA);
        return simpleDateFormat.format(this.mCreatedAt);
    }
    public String getCreatedAt(String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CANADA);
        return simpleDateFormat.format(this.mCreatedAt);
    }

    public Date getCreatedAtDate(){
        return this.mCreatedAt;
    }

    public boolean compareToMonth(Date x){
        long monthTime = 30L*24*60*60*1000;
        return this.mCreatedAt.getTime() < (x.getTime() - monthTime);
    }

    public TestResultEntity(String value){
        try {
            String[] vals = new String[2];
            vals = value.split(",");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss", Locale.CANADA);
            this.mCreatedAt = simpleDateFormat.parse(vals[0]);
            this.mTestResults = Float.parseFloat(vals[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
