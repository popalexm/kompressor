package com.example.alexpop.resizerlib.kompressorLib.callbacks;

import android.support.annotation.NonNull;

import java.io.File;

public interface SingleImageCopyCallback {

    /** Called once a file has been successfully copied
     */
    void onSingleImageCopySuccess(@NonNull File copiedFile);

    /** Called once a file has failed to copy
     */
    void onSingleImageCopyFailed(@NonNull File failedToCopyFile);

}
