package com.example.alexpop.resizerlib.library.callbacks;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

public interface ImageListResizeCallback {

    /**Called upon when image resize tasks has begun
     */
    void onImageListResizeStartedListener();

    /**Called upon when image resize tasks have been completed, returns a list of successfully resized files
     * @return List<File>
     */
    void onImageListResizeSuccessListener(@NonNull List<File> successfullyResizedFiles);

    /**Called upon when image resize tasks have been completed,
     * returns a list of files that have failed to be resized
     * @return List<File>
     */
    void onImageListResizeFailedListener(@NonNull List<File> failedToResizeFiles);
}
