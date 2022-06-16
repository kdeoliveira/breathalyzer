package com.coen390.abreath.domain;

import androidx.annotation.Nullable;

/**
 * Encapsulation of main features that needs to access business logic into single UseCases
 * In order to properly apply SRP, each UseCase should implements this callable interface
 * Note that ideally each Use Case must return a LiveData object so results can be properly handled by View Models
 */
public interface UseCase{
    /**
     * Adaptation of overloaded invoke method in Kotlin
     * Function to be called when feature is to be executed
     */
    <S> S call(@Nullable Object payload);
}

