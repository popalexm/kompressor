package com.example.alexpop.resizerlib.library.callbacks;

import android.support.annotation.NonNull;

import java.io.File;

public interface SingleImageResizeCallback {

    void onSingleImageResizeSuccess(@NonNull File resizedImage);

    void onSingleImageResizeFailed(@NonNull File failedToResizeImage);

}
