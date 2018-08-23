package com.example.alexpop.resizerlib.kompressorLib.callbacks;

import android.support.annotation.NonNull;

import java.io.File;

public interface IndividualItemCopyCallback {

    /** Called once a file has been successfully copied
     */
    void onIndividualItemCopySuccess(@NonNull File copiedFile);

    /** Called once a file has failed to copy
     */
    void onIndividualItemCopyFailed(@NonNull File failedToCopyFile);

}
