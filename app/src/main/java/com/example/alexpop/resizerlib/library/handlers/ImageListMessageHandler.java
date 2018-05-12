package com.example.alexpop.resizerlib.library.handlers;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.alexpop.resizerlib.library.callbacks.ImageListCopyCallback;
import com.example.alexpop.resizerlib.library.callbacks.ImageListResizeCallback;
import com.example.alexpop.resizerlib.library.definitions.Message;

import java.io.File;
import java.util.List;


public class ImageListMessageHandler {

    private String TAG = ImageListMessageHandler.class.getSimpleName();

    private Handler mMainHandler;

    private static ImageListMessageHandler sInstance;

    public static synchronized ImageListMessageHandler getInstance(){
        if(sInstance == null){
            sInstance = new ImageListMessageHandler();
        }
        return sInstance;
    }

    public synchronized void postResizeMessage (@NonNull Message msg,
                                                @NonNull ImageListResizeCallback imageListResizeCallback,
                                                @Nullable List<File> successfulImages,
                                                @Nullable List<File> failedImages){
        mMainHandler = new Handler(Looper.getMainLooper());
        Runnable mainThreadCallback = () -> {
            Log.d(TAG , "Posting " + msg.toString() + " on " + Thread.currentThread().getName());
            switch (msg) {
                case PROCESSING_STARTED:
                     imageListResizeCallback.onImageListResizeStartedListener();
                     break;
                case PROCESSING_SUCCESS:
                     imageListResizeCallback.onImageListResizeSuccessListener(successfulImages);
                     break;
                case PROCESSING_FAILED:
                     imageListResizeCallback.onImageListResizeFailedListener(failedImages);
                     break;
            }
        };
        mMainHandler.post(mainThreadCallback);
    }

    public synchronized void postCopyMessage (@NonNull Message msg,
                                              @NonNull ImageListCopyCallback imageListCopyCallback,
                                              @Nullable List<File> successfulImages,
                                              @Nullable List<File> failedImages){
        mMainHandler = new Handler(Looper.getMainLooper());
        Runnable mainThreadCallback = () -> {
            Log.d(TAG , "Posting " + msg.toString() + " on " + Thread.currentThread().getName());
            switch (msg) {
                case PROCESSING_STARTED:
                    imageListCopyCallback.onImageListCopyStartedListener();
                    break;
                case PROCESSING_SUCCESS:
                    imageListCopyCallback.onImageListCopySuccessListener(successfulImages);
                    break;
                case PROCESSING_FAILED:
                    imageListCopyCallback.onImageListFailedListener(failedImages);
                    break;
            }
        };
        mMainHandler.post(mainThreadCallback);
    }
}
