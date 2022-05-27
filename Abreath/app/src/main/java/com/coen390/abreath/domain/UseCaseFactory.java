package com.coen390.abreath.domain;

import androidx.annotation.NonNull;

import com.coen390.abreath.common.Factory;

public class UseCaseFactory implements Factory {
    @Override
    public <T> T createInstance(@NonNull Class<T> builder) {
        try {
            return builder.newInstance();
        } catch (IllegalAccessException |InstantiationException ignored) {
            return null;
        }
    }
}
