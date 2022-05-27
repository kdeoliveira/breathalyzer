package com.coen390.abreath.common;

public interface Factory{
    <T> T createInstance(Class<T> builder);
}
