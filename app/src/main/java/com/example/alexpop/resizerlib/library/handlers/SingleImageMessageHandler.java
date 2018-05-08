package com.example.alexpop.resizerlib.library.handlers;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.example.alexpop.resizerlib.library.callbacks.SingleImageCopyCallback;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageResizeCallback;

import java.io.File;

public class SingleImageMessageHandler {

    private Handler mMainThreadLooper;

    public SingleImageMessageHandler() {
        this.mMainThreadLooper = new Handler(Looper.getMainLooper());
    }

    public void sendCopySucessMessage(@NonNull SingleImageCopyCallback singleImageCopyCallback, @NonNull File copiedFile) {
        Runnable mainThreadCallback = new Runnable() {
            @Override
            public void run() {
                singleImageCopyCallback.onSingleImageCopySuccess(copiedFile);
            }
        };
        mMainThreadLooper.post(mainThreadCallback);
    }


    public void sendCopyFailedMessage(@NonNull SingleImageCopyCallback singleImageCopyCallback, @NonNull File failedFile) {
        Runnable mainThreadCallback = new Runnable() {
            @Override
            public void run() {
                singleImageCopyCallback.onSingleImageCopyFailed(failedFile);
            }
        };
        mMainThreadLooper.post(mainThreadCallback);
    }

    public void sendResizeSuccessMessage(@NonNull SingleImageResizeCallback singleImageCopyCallback, @NonNull File resizedFile) {
        Runnable mainThreadCallback = new Runnable() {
            @Override
            public void run() {
                singleImageCopyCallback.onSingleImageResizeSuccess(resizedFile);
            }
        };
        mMainThreadLooper.post(mainThreadCallback);
    }


    public void sendResizeFailed (@NonNull SingleImageResizeCallback singleImageCopyCallback, @NonNull File failedFile) {
        Runnable mainThreadCallback = new Runnable() {
            @Override
            public void run() {
                singleImageCopyCallback.onSingleImageResizeFailed(failedFile);
            }
        };
        mMainThreadLooper.post(mainThreadCallback);
    }
}
