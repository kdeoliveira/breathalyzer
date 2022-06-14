package com.coen390.abreath.ui.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.coen390.abreath.data.api.MockUpRepository;
import com.google.firebase.database.DatabaseReference;

/**
 * Factory class for ViewModel provider
 * This was used when model was sourced from REST API
 * No longer required
 */
public class ViewModelFactory implements ViewModelProvider.Factory {
    private final MockUpRepository repository;

    public ViewModelFactory(MockUpRepository repository){
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new UserDataViewModel();
    }
}
