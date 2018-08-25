package com.example.alexpop.resizerlib.app.application;

import com.example.alexpop.resizerlib.app.injection.Injection;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Injection.initialize(getApplicationContext());
    }
}
