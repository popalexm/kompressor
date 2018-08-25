package com.example.alexpop.resizerlib.app.useCases.base;

import io.reactivex.Maybe;

public interface BaseUseCaseMaybe<T> {

    Maybe<T> perform();
}
