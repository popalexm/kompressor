package com.example.alexpop.resizerlib.library.callbacks;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

public interface ImageListCopyCallback {

    void onImageListCopyStartedListener();

    /**Called upon when image copy tasks have been completed,
     * @return List<File>
     */
    void onImageListCopySuccessListener(@NonNull List<File> filesCopiedSuccessfully);

    /**Called upon when image copy tasks have been completed,
     * returns a list of files that have failed to be copied to the destination directory
     * @return List<File>
     */
    void onImageListFailedListener(@NonNull List<File> failedToCopyFiles);
}
