package com.example.alexpop.resizerlib.app.base;

import android.support.annotation.NonNull;

public class BaseContract {

    public interface View {

        void showMessage(@NonNull String message);
    }

    public interface Presenter {

        void onAttach();

        void onDetach();

    }
}
