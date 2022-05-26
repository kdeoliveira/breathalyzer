package com.coen390.abreath.util;

public class Tuple <T,S>{
    private T first;

    public T getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    private S second;

    public Tuple(T first, S second){
        this.first = first;
        this.second = second;
    }
}
