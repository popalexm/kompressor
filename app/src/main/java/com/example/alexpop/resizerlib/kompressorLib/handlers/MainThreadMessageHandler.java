package com.example.alexpop.resizerlib.kompressorLib.handlers;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemResizeCallback;
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
            EntireBatchResizeCallback callback = resizeStatusMessage.getCallback();
            switch (resizeStatusMessage.getStatus()) {
                case PROCESSING_STARTED:
                    callback.onBatchResizeStartedListener();
                    break;
                case PROCESSING_SUCCESS:
                    callback.onBatchResizeSuccessListener(resizeStatusMessage.getSuccessfulImages());
                    break;
                case PROCESSING_FAILED:
                    callback.onBatchResizeFailedListener(resizeStatusMessage.getFailedImages());
                    break;
            }
        };
        mMainHandler.post(mainThreadRunnable);
    }

    public synchronized void postBatchCopyMessage(@NonNull CopyStatusMessage copyStatusMessage) {
        mMainHandler = new Handler(Looper.getMainLooper());
        Runnable mainThreadRunnable = () -> {
            EntireBatchCopyCallback callback = copyStatusMessage.getCallback();
            switch (copyStatusMessage.getStatus()) {
                case PROCESSING_STARTED:
                    callback.onBatchCopyStartedListener();
                    break;
                case PROCESSING_SUCCESS:
                    callback.onBatchCopySuccessListener(copyStatusMessage.getSuccessfulImages());
                    break;
                case PROCESSING_FAILED:
                    callback.onBatchFailedListener(copyStatusMessage.getFailedImages());
                    break;
            }
        };
        mMainHandler.post(mainThreadRunnable);
    }

    public synchronized void postImageCopySuccessMessage(@NonNull IndividualItemCopyCallback individualItemCopyCallback, @NonNull File copiedFile) {
        Runnable mainThreadCallback = () -> individualItemCopyCallback.onIndividualItemCopySuccess(copiedFile);
        mMainHandler.post(mainThreadCallback);
    }

    public synchronized void postImageCopyFailedMessage(@NonNull IndividualItemCopyCallback individualItemCopyCallback, @NonNull File failedFile) {
        Runnable mainThreadCallback = () -> individualItemCopyCallback.onIndividualItemCopyFailed(failedFile);
        mMainHandler.post(mainThreadCallback);
    }

    public synchronized void postImageResizeSuccessMessage(@NonNull IndividualItemResizeCallback singleImageCopyCallback, @NonNull File resizedFile) {
        Runnable mainThreadCallback = () -> singleImageCopyCallback.onIndividualItemResizeSuccess(resizedFile);
        mMainHandler.post(mainThreadCallback);
    }

    public synchronized void postImageResizeFailedMessage(@NonNull IndividualItemResizeCallback singleImageCopyCallback, @NonNull File failedFile) {
        Runnable mainThreadCallback = () -> singleImageCopyCallback.onIndividualItemResizeFailed(failedFile);
        mMainHandler.post(mainThreadCallback);
    }
}
