package com.example.alexpop.resizerlib.app.injection;

import com.example.alexpop.resizerlib.app.utils.GlobalConstants;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class Injection {

    /*** Provides the global application singleton context, not the Activity / Fragment context, so it's not a memory leak
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

    @NonNull
    public static SharedPreferences provideSharedPreferences() {
        return Injection.context.getSharedPreferences(GlobalConstants.KOMPRESSOR_LIB_PREFERENCES, Context.MODE_PRIVATE);
    }
}
