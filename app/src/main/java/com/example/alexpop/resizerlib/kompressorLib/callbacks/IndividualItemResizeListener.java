package com.example.alexpop.resizerlib.kompressorLib.callbacks;

import android.support.annotation.NonNull;

import java.io.File;

public interface IndividualItemResizeListener {

    /** Called once a file has been successfully resized
     */
    void onIndividualItemResizeSuccess(@NonNull File file);

    /** Called once a file has been successfully resized
     */
    void onIndividualItemResizeFailed(@NonNull File file);

}
