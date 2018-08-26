package com.example.alexpop.resizerlib.app.viewModels;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;

public class ResizeSettingsDialogViewModel {

    @NonNull
    public ObservableField<String> maxHeight = new ObservableField<>("");
    @NonNull
    public ObservableField<String> compressionRatio = new ObservableField<>("");

}
