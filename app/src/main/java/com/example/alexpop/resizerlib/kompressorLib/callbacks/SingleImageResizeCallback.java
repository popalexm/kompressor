package com.example.alexpop.resizerlib.kompressorLib.callbacks;

import android.support.annotation.NonNull;

import java.io.File;

public interface SingleImageResizeCallback {

    /** Called once a file has been successfully resized
     */
    void onSingleImageResizeSuccess(@NonNull File resizedImage);

    /** Called once a file has been successfully resized
     */
    void onSingleImageResizeFailed(@NonNull File failedToResizeImage);

}
