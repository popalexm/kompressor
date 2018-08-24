package com.example.alexpop.resizerlib.app.activity;

import android.content.Context;
import android.support.annotation.NonNull;

public class Injection {

    /*** Provides the global application context, not a memory leak .
     */
    @NonNull
    private static Context context;

    public static void initialize(@NonNull Context context) {
        Injection.context = context;
    }

    @NonNull
    public static Context provideGlobalContext() {
        return Injection.context;
    }

}
