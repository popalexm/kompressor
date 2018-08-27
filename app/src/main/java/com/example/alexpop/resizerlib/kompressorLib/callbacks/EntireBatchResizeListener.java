package com.example.alexpop.resizerlib.kompressorLib.callbacks;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

public interface EntireBatchResizeListener {

    /**Called upon when image resize tasks have been completed, returns a list of successfully resized files
     */
    void onBatchResizeSuccess(@NonNull List<File> files);

    /**Called upon when image resize tasks have been completed,
     * returns a list of files that have failed to be resized
     */
    void onBatchResizeFailed(@NonNull List<File> files);
}
