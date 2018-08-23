package com.example.alexpop.resizerlib.kompressorLib.callbacks;

import android.support.annotation.NonNull;

import java.io.File;

public interface IndividualItemResizeCallback {

    /** Called once a file has been successfully resized
     */
    void onIndividualItemResizeSuccess(@NonNull File resizedImage);

    /** Called once a file has been successfully resized
     */
    void onIndividualItemResizeFailed(@NonNull File failedToResizeImage);

}
