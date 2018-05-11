package com.example.alexpop.resizerlib.library.callables;

import android.support.annotation.NonNull;

import com.example.alexpop.resizerlib.library.actions.ImageResizeCompressAction;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageResizeCallback;
import com.example.alexpop.resizerlib.library.handlers.SingleImageMessageHandler;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ImageResizeWorkerTask extends WorkerTaskCallable {

    private String TAG = ImageResizeWorkerTask.class.getSimpleName();

    private String mImgPath;
    private int mMaximumWidth;
    private int mCompressionRatio;

    private SingleImageResizeCallback mSingleImageResizeCallback;
    private HashMap<File, Boolean> mResizedFileStatus;

    public ImageResizeWorkerTask(@NonNull String path, int maximumWidth, int compressionRatio, @NonNull SingleImageResizeCallback imageResizeCallback){
        this.mImgPath = path;
        this.mCompressionRatio = compressionRatio;
        this.mMaximumWidth = maximumWidth;
        this.mSingleImageResizeCallback = imageResizeCallback;
    }

    @Override
    public HashMap<File, Boolean> call() {
        mResizedFileStatus = new LinkedHashMap<>();
        File file = new ImageResizeCompressAction().resizeAndCompressAtPath(mImgPath, mMaximumWidth, mCompressionRatio);
        SingleImageMessageHandler mSingleImageMessageHandler = new SingleImageMessageHandler();
        if (file != null) {
            mResizedFileStatus.put(file , true);
            mSingleImageMessageHandler.sendResizeSuccessMessage(mSingleImageResizeCallback , file);
        } else {
            mResizedFileStatus.put(file , false);
            mSingleImageMessageHandler.sendResizeFailed(mSingleImageResizeCallback , new File(mImgPath));
        }
        return mResizedFileStatus;
    }
}
