package com.example.alexpop.resizerlib.library.callbacks;

import android.support.annotation.NonNull;

import java.io.File;

public interface SingleImageCopyCallback {

    void onSingleImageCopySuccess(@NonNull File copiedFile);

    void onSingleImageCopyFailed(@NonNull File failedToCopyFile);

}
