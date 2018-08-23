package com.example.alexpop.resizerlib.kompressorLib.tasks;

import com.example.alexpop.resizerlib.kompressorLib.actions.ImageResizeCompressAction;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.handlers.MainThreadMessageHandler;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.io.File;

public class ImageResizeWorkerTask extends BaseWorkerTaskCallable {

    private final String TAG = ImageResizeWorkerTask.class.getSimpleName();
    @NonNull
    private final String imagePath;
    private final int maximumWidth;
    private final int compressionRatio;
    @NonNull
    private final IndividualItemResizeCallback callback;

    ImageResizeWorkerTask(@NonNull String path, int maximumWidth, int compressionRatio, @NonNull IndividualItemResizeCallback imageResizeCallback) {
        this.imagePath = path;
        this.compressionRatio = compressionRatio;
        this.maximumWidth = maximumWidth;
        this.callback = imageResizeCallback;
    }

    @Override
    public Pair<File, Boolean> call() {
        File file = new ImageResizeCompressAction().resizeAndCompressAtPath(imagePath, maximumWidth, compressionRatio);
        MainThreadMessageHandler mSingleImageMessageHandler = MainThreadMessageHandler.getInstance();
        boolean resizeStatus;
        if (file != null) {
            resizeStatus = true;
            mSingleImageMessageHandler.postImageResizeSuccessMessage(callback, file);
        } else {
            resizeStatus = false;
            mSingleImageMessageHandler.postImageResizeFailedMessage(callback, new File(imagePath));
        }
        return new Pair<>(file, resizeStatus);
    }
}
