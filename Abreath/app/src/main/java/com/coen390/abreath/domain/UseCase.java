package com.coen390.abreath.domain;

import androidx.annotation.Nullable;

public interface UseCase{
    <S> S call(@Nullable Object payload);
}

