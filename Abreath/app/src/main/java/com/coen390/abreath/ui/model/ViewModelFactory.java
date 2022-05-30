package com.coen390.abreath.ui.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.coen390.abreath.common.Factory;
import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.api.MockUpService;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final MockUpRepository repository;

    public ViewModelFactory(MockUpRepository repository){
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new UserDataViewModel(this.repository);
    }
}
