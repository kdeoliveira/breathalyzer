package com.coen390.abreath.common;

/**
 * Helper class that implements an equivalent std::c++ tuple
 * @param <T> First item
 * @param <S> Second item
 */
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
