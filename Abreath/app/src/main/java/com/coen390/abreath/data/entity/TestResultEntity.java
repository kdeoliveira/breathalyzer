package com.coen390.abreath.data.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Entity class for Test results stored in the database and displayed in the Horizontal Bar chart
 * A set of test results is linked to a specific user. Note that such dependency should be treated by the database (if using SQL)
 * Ideally, a more detailed table should be built for this Entity
 */
public class TestResultEntity {
    private float mTestResults;
    private Date mCreatedAt;

    public TestResultEntity(float testResults, Date createdAt){
        this.mTestResults = testResults;
        this.mCreatedAt = createdAt;
    }

    /*
    GETTERS AND SETTERS
     */

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

    /**
     * Compare dates
     * @param x Date
     * @return whether this.createdAt lower than x.createdAt
     */
    public boolean compareToMonth(Date x){
        long monthTime = 30L*24*60*60*1000;
        return this.mCreatedAt.getTime() < (x.getTime() - monthTime);
    }

    /**
     * Constructor overloading
     * Creates new instance of Test results from a string value (since values stored in db are in string)
     */
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
