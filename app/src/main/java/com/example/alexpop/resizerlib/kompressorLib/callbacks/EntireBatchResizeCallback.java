package com.example.alexpop.resizerlib.kompressorLib.callbacks;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

public interface EntireBatchResizeCallback {

    /**Called upon when image resize tasks has begun
     */
    void onBatchResizeStartedListener();

    /**Called upon when image resize tasks have been completed, returns a list of successfully resized files
     */
    void onBatchResizeSuccessListener(@NonNull List<File> successfullyResizedFiles);

    /**Called upon when image resize tasks have been completed,
     * returns a list of files that have failed to be resized
     */
    void onBatchResizeFailedListener(@NonNull List<File> failedToResizeFiles);
}
