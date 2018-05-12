package com.example.alexpop.resizerlib.library.callbacks;

import android.support.annotation.NonNull;

import java.io.File;

public interface SingleImageCopyCallback {

    /** Called once a file has been sucessfuly copied
       @return File
     */
    void onSingleImageCopySuccess(@NonNull File copiedFile);

    /** Called once a file has failed to copy
     @return File
     */
    void onSingleImageCopyFailed(@NonNull File failedToCopyFile);

}
