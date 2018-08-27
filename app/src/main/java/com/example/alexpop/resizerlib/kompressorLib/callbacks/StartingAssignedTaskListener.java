package com.example.alexpop.resizerlib.kompressorLib.callbacks;

public interface StartingAssignedTaskListener {

    /**
     * Called upon when an image copy to directory tasks has begun
     */
    void onBatchCopyTaskStarted();

    /**
     * Called upon when an image resize tasks has begun
     */
    void onBatchResizeTaskStarted();
}
