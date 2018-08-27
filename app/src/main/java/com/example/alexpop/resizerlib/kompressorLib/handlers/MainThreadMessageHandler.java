package com.example.alexpop.resizerlib.kompressorLib.handlers;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchCopySuccessListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchResizeListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemCopyListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemResizeListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.StartingAssignedTaskListener;
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
            EntireBatchResizeListener callback = resizeStatusMessage.getCallback();
            switch (resizeStatusMessage.getStatus()) {
                case PROCESSING_SUCCESS:
                    callback.onBatchResizeSuccess(resizeStatusMessage.getSuccessfulImages());
                    break;
                case PROCESSING_FAILED:
                    callback.onBatchResizeFailed(resizeStatusMessage.getFailedImages());
                    break;
            }
        };
        mMainHandler.post(mainThreadRunnable);
    }

    public synchronized void postBatchCopyMessage(@NonNull CopyStatusMessage copyStatusMessage) {
        mMainHandler = new Handler(Looper.getMainLooper());
        Runnable mainThreadRunnable = () -> {
            EntireBatchCopySuccessListener callback = copyStatusMessage.getCallback();
            switch (copyStatusMessage.getStatus()) {
                case PROCESSING_SUCCESS:
                    callback.onBatchCopySuccess(copyStatusMessage.getSuccessfulImages());
                    break;
                case PROCESSING_FAILED:
                    callback.onBatchCopyFailed(copyStatusMessage.getFailedImages());
                    break;
            }
        };
        mMainHandler.post(mainThreadRunnable);
    }

    public synchronized void postBatchCopyStarted(@NonNull StartingAssignedTaskListener startingAssignedTaskListener) {
        mMainHandler = new Handler(Looper.getMainLooper());
        Runnable mainThreadRunnable = startingAssignedTaskListener::onBatchCopyTaskStarted;
        mMainHandler.post(mainThreadRunnable);
    }

    public synchronized void postBatchResizeStarted(@NonNull StartingAssignedTaskListener startingAssignedTaskListener) {
        mMainHandler = new Handler(Looper.getMainLooper());
        Runnable mainThreadRunnable = startingAssignedTaskListener::onBatchResizeTaskStarted;
        mMainHandler.post(mainThreadRunnable);
    }

    public synchronized void postImageCopySuccessMessage(@Nullable IndividualItemCopyListener copyCallback, @NonNull File copiedFile) {
        if (copyCallback != null) {
            Runnable mainThreadCallback = () -> copyCallback.onIndividualItemCopySuccess(copiedFile);
            mMainHandler.post(mainThreadCallback);
        }
    }

    public synchronized void postImageCopyFailedMessage(@Nullable IndividualItemCopyListener copyCallback, @NonNull File failedFile) {
        if (copyCallback != null) {
            Runnable mainThreadCallback = () -> copyCallback.onIndividualItemCopyFailed(failedFile);
            mMainHandler.post(mainThreadCallback);
        }
    }

    public synchronized void postImageResizeSuccessMessage(@Nullable IndividualItemResizeListener resizeCallback, @NonNull File resizedFile) {
        if (resizeCallback != null) {
            Runnable mainThreadCallback = () -> resizeCallback.onIndividualItemResizeSuccess(resizedFile);
            mMainHandler.post(mainThreadCallback);
        }
    }

    public synchronized void postImageResizeFailedMessage(@Nullable IndividualItemResizeListener resizeCallback, @NonNull File failedFile) {
        if (resizeCallback != null) {
            Runnable mainThreadCallback = () -> resizeCallback.onIndividualItemResizeFailed(failedFile);
            mMainHandler.post(mainThreadCallback);
        }
    }
}
