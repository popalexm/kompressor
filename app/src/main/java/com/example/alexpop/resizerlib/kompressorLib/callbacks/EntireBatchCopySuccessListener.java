package com.example.alexpop.resizerlib.kompressorLib.callbacks;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

public interface EntireBatchCopySuccessListener {

    /**Called upon when image copy tasks have been completed,
     */
    void onBatchCopySuccess(@NonNull List<File> filesCopiedSuccessfully);

    /**Called upon when image copy tasks have been completed,
     * returns a list of files that have failed to be copied to the destination directory
     */
    void onBatchCopyFailed(@NonNull List<File> failedToCopyFiles);
}
