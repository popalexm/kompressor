package com.example.alexpop.resizerlib.library.callbacks;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

public interface ImageListResizeCallback {

    void onImageListResizeStartedListener();

    void onImageListResizeSuccessListener(@NonNull List<File> resizedFiles);

    void onImageListResizeFailedListener(@NonNull List<File> failedToResizeFiles);
}
