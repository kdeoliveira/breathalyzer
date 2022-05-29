package com.coen390.abreath.common;

public class Utility {
    public static float map(float x, float min, float max, float out_min, float out_max){
        return (x - min) * (out_max - out_min)/(max - min) + out_min;
    }
}
