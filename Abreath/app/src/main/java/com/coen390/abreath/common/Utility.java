package com.coen390.abreath.common;

import android.util.Log;

public class Utility {
    public static float map(float x, float min, float max, float out_min, float out_max){
        if(x > max) return out_max;
        if(x < min) return out_min;
        return (x - min) * (out_max - out_min)/(max - min) + out_min;
    }

    public static float lbstokg(float lbs){
        return lbs * 0.45359237f;
    }

    public static float kgtolbs(float kg){
        return Math.round(kg * 2.2046226218f);
    }

    public static int[] cmtoin(float cm){
        float length = 100*(cm/100)/2.54f;
        int feet = (int) length / 12;

        return new int[]{feet, (int) (length - 12*feet)};
    }

    public static float intocm(int feet, int inch){
        float temp = (float) (feet*0.3048 + inch*0.0254);
        temp *=100;
        return temp;
    }
}
