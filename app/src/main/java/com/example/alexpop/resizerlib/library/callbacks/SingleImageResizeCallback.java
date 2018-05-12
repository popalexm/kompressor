package com.example.alexpop.resizerlib.library.callbacks;

import android.support.annotation.NonNull;

import java.io.File;

public interface SingleImageResizeCallback {

    /** Called once a file has been successfully resized
     @return File
     */
    void onSingleImageResizeSuccess(@NonNull File resizedImage);

    /** Called once a file has been successfully resized
     @return File
     */
    void onSingleImageResizeFailed(@NonNull File failedToResizeImage);

}
