package com.example.alexpop.resizerlib.library.callbacks;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

public interface ImageListCopyCallback {

    void onImageListCopyStartedListener();

    void onImageListCopySuccessListener(@NonNull List<File> filesCopiedSuccessfully);

    void onImageListFailedListener(@NonNull List<File> failedToCopyFiles);
}
