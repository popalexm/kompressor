package com.example.alexpop.resizerlib.library.callables;

import android.support.annotation.NonNull;

import com.example.alexpop.resizerlib.library.actions.ImageCopyAction;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageCopyCallback;
import com.example.alexpop.resizerlib.library.handlers.SingleImageMessageHandler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ImageCopyWorkerTask extends WorkerTaskCallable {

    private String TAG = ImageCopyWorkerTask.class.getSimpleName();

    private File mToSaveContent;
    private File mMediaStorageDirectory;
    private SingleImageCopyCallback mCopyStatusCallback;

    /**The mCopyStatus HashMap contains the destination file which has been successfully copied,
     * with a boolean value of true or,
     * in case of an execution error, the file which failed to copy,
     * and the boolean status false assigned to it
     */
    private HashMap<File, Boolean> mCopyOperationStatus;

    public ImageCopyWorkerTask(@NonNull File toSaveContent , @NonNull File mediaStorageDirectory, @NonNull SingleImageCopyCallback copyStatusCallback){
        this.mToSaveContent = toSaveContent;
        this.mMediaStorageDirectory = mediaStorageDirectory;
        this.mCopyStatusCallback = copyStatusCallback;
    }

    @Override
    public HashMap<File, Boolean> call()  {
        mCopyOperationStatus = new LinkedHashMap<>();
        SingleImageMessageHandler mSingleImageMessageHandler = new SingleImageMessageHandler();
        try {
            File resultFile = new ImageCopyAction().copyFileToDirectory(mToSaveContent, mMediaStorageDirectory);
            if (resultFile != null) {
                mCopyOperationStatus.put(mToSaveContent, true);
                mSingleImageMessageHandler.sendCopySucessMessage(mCopyStatusCallback , resultFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mCopyOperationStatus.put(mToSaveContent, false);
            mSingleImageMessageHandler.sendCopyFailedMessage(mCopyStatusCallback , mToSaveContent);
        }
        return mCopyOperationStatus;
    }
}
