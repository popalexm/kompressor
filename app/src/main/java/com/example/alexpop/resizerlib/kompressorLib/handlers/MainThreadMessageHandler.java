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
import android.support.annotation.Nullable;

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

    public synchronized void postImageCopySuccessMessage(@Nullable IndividualItemCopyCallback copyCallback, @NonNull File copiedFile) {
        if (copyCallback != null) {
            Runnable mainThreadCallback = () -> copyCallback.onIndividualItemCopySuccess(copiedFile);
            mMainHandler.post(mainThreadCallback);
        }
    }

    public synchronized void postImageCopyFailedMessage(@Nullable IndividualItemCopyCallback copyCallback, @NonNull File failedFile) {
        if (copyCallback != null) {
            Runnable mainThreadCallback = () -> copyCallback.onIndividualItemCopyFailed(failedFile);
            mMainHandler.post(mainThreadCallback);
        }
    }

    public synchronized void postImageResizeSuccessMessage(@Nullable IndividualItemResizeCallback resizeCallback, @NonNull File resizedFile) {
        if (resizeCallback != null) {
            Runnable mainThreadCallback = () -> resizeCallback.onIndividualItemResizeSuccess(resizedFile);
            mMainHandler.post(mainThreadCallback);
        }
    }

    public synchronized void postImageResizeFailedMessage(@Nullable IndividualItemResizeCallback resizeCallback, @NonNull File failedFile) {
        if (resizeCallback != null) {
            Runnable mainThreadCallback = () -> resizeCallback.onIndividualItemResizeFailed(failedFile);
            mMainHandler.post(mainThreadCallback);
        }
    }
}
