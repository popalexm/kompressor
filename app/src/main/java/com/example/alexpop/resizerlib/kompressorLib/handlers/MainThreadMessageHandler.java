package com.example.alexpop.resizerlib.kompressorLib.handlers;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.ImageListCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.ImageListResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.SingleImageCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.SingleImageResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.handlers.models.CopyStatusMessage;
import com.example.alexpop.resizerlib.kompressorLib.handlers.models.ResizeStatusMessage;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.File;

public class MainThreadMessageHandler {

    private static MainThreadMessageHandler sInstance;
    private final String TAG = MainThreadMessageHandler.class.getSimpleName();
    private Handler mMainHandler;

    public static synchronized MainThreadMessageHandler getInstance() {
        if (MainThreadMessageHandler.sInstance == null) {
            MainThreadMessageHandler.sInstance = new MainThreadMessageHandler();
        }
        return MainThreadMessageHandler.sInstance;
    }

    public synchronized void postBatchResizeMessage(@NonNull ResizeStatusMessage resizeStatusMessage) {
        mMainHandler = new Handler(Looper.getMainLooper());
        Runnable mainThreadRunnable = () -> {
            ImageListResizeCallback callback = resizeStatusMessage.getCallback();
            switch (resizeStatusMessage.getStatus()) {
                case PROCESSING_STARTED:
                    callback.onImageListResizeStartedListener();
                    break;
                case PROCESSING_SUCCESS:
                    callback.onImageListResizeSuccessListener(resizeStatusMessage.getSuccessfulImages());
                    break;
                case PROCESSING_FAILED:
                    callback.onImageListResizeFailedListener(resizeStatusMessage.getFailedImages());
                    break;
            }
        };
        mMainHandler.post(mainThreadRunnable);
    }

    public synchronized void postBatchCopyMessage(@NonNull CopyStatusMessage copyStatusMessage) {
        mMainHandler = new Handler(Looper.getMainLooper());
        Runnable mainThreadRunnable = () -> {
            ImageListCopyCallback callback = copyStatusMessage.getCallback();
            switch (copyStatusMessage.getStatus()) {
                case PROCESSING_STARTED:
                    callback.onImageListCopyStartedListener();
                    break;
                case PROCESSING_SUCCESS:
                    callback.onImageListCopySuccessListener(copyStatusMessage.getSuccessfulImages());
                    break;
                case PROCESSING_FAILED:
                    callback.onImageListFailedListener(copyStatusMessage.getFailedImages());
                    break;
            }
        };
        mMainHandler.post(mainThreadRunnable);
    }

    public synchronized void postImageCopySuccessMessage(@NonNull SingleImageCopyCallback singleImageCopyCallback, @NonNull File copiedFile) {
        Runnable mainThreadCallback = () -> singleImageCopyCallback.onSingleImageCopySuccess(copiedFile);
        mMainHandler.post(mainThreadCallback);
    }

    public synchronized void postImageCopyFailedMessage(@NonNull SingleImageCopyCallback singleImageCopyCallback, @NonNull File failedFile) {
        Runnable mainThreadCallback = () -> singleImageCopyCallback.onSingleImageCopyFailed(failedFile);
        mMainHandler.post(mainThreadCallback);
    }

    public synchronized void postImageResizeSuccessMessage(@NonNull SingleImageResizeCallback singleImageCopyCallback, @NonNull File resizedFile) {
        Runnable mainThreadCallback = () -> singleImageCopyCallback.onSingleImageResizeSuccess(resizedFile);
        mMainHandler.post(mainThreadCallback);
    }

    public synchronized void postImageResizeFailedMessage(@NonNull SingleImageResizeCallback singleImageCopyCallback, @NonNull File failedFile) {
        Runnable mainThreadCallback = () -> singleImageCopyCallback.onSingleImageResizeFailed(failedFile);
        mMainHandler.post(mainThreadCallback);
    }
}
