package com.coen390.abreath.common;

import android.util.Log;

public class Utility {
    public static float map(float x, float min, float max, float out_min, float out_max){
        return (x - min) * (out_max - out_min)/(max - min) + out_min;
    }

    public static float lbstokg(float lbs){
        lbs *= 0.45359237f;
        return Math.round(lbs);
    }

    public static float kgtolbs(float kg){
        return Math.round(kg * 2.2046226218f);
    }

    public static int[] cmtoin(float cm){
        float length = 100*(cm/100)/2.54f;
        int feet = (int) length / 12;

        return new int[]{feet, (int) (length - 12*feet)};
    }
}
