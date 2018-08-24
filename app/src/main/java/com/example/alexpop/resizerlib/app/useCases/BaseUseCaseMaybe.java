package com.example.alexpop.resizerlib.app.useCases;

import io.reactivex.Maybe;

public interface BaseUseCaseMaybe<T> {

    Maybe<T> perform();
}
