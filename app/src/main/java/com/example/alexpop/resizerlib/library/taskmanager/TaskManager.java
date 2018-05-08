package com.example.alexpop.resizerlib.library.taskmanager;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.alexpop.resizerlib.library.callbacks.ImageListCopyCallback;
import com.example.alexpop.resizerlib.library.callbacks.ImageListResizeCallback;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageCopyCallback;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageResizeCallback;
import com.example.alexpop.resizerlib.library.definitions.TaskDetails;
import com.example.alexpop.resizerlib.library.definitions.TaskModel;
import com.example.alexpop.resizerlib.library.threadmanager.ThreadPoolManager;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.example.alexpop.resizerlib.library.definitions.TaskType.TASK_COMPRESS_TO_RATIO;
import static com.example.alexpop.resizerlib.library.definitions.TaskType.TASK_MOVE_TO_DIRECTORY;
import static com.example.alexpop.resizerlib.library.definitions.TaskType.TASK_RESIZE;
import static com.example.alexpop.resizerlib.library.definitions.TaskType.TASK_RESIZE_AND_COMPRESS_TO_RATIO;

public class TaskManager {

    private String TAG =  TaskManager.class.getSimpleName();

    private static TaskManager mInstance;

    private TaskManager(){ }

    public static synchronized TaskManager getInstance (){
        if(mInstance == null){
            mInstance = new TaskManager();
        }
        return mInstance;
    }

    private ExecutorService mMainExecutorThread;
    private TaskModel mTaskModel;
    private ImageListCopyCallback mImageListCopyCallback;
    private ImageListResizeCallback mImageListResizeCallback;
    private SingleImageCopyCallback mSingleImageCopyCallback;
    private SingleImageResizeCallback mSingleImageResizeCallback;
    private MainListenerTaskCallable mMainListenerTaskCallable;

    public void setImageListCopyCallback(@NonNull ImageListCopyCallback imageCopyStatusCallback) {
        this.mImageListCopyCallback = imageCopyStatusCallback;
    }

    public void setSingleImageCopyCallback(@NonNull SingleImageCopyCallback singleImageCopyCallback) {
        this.mSingleImageCopyCallback = singleImageCopyCallback;
    }

    public void setImageListResizeCallback(@NonNull ImageListResizeCallback imageListResizeCallback) {
        this.mImageListResizeCallback = imageListResizeCallback;
    }

    public void setSingleImageResizeCallback(@NonNull SingleImageResizeCallback imageListResizeCallback) {
        this.mSingleImageResizeCallback = imageListResizeCallback;
    }

    public void setTask(@NonNull TaskModel mTaskModel) {
        this.mTaskModel = mTaskModel;
    }

    public void executeTask() {
        if (mTaskModel != null) {
            mMainExecutorThread = ThreadPoolManager.getInstance().createMainExecutorService();
            TaskDetails taskDetails = mTaskModel.getmTaskDetails();
            List<File> queueImageFiles = mTaskModel.getmQueueImageFiles();
            switch (mTaskModel.getmTaskType()) {
                case TASK_MOVE_TO_DIRECTORY:
                    if (taskDetails.getmDestinationPath() != null && mImageListCopyCallback != null ) {
                           startImageCopy(queueImageFiles,
                                          taskDetails.getmDestinationPath());
                    } else {
                        Log.e(TAG , "Operation " + TASK_MOVE_TO_DIRECTORY + " aborted , missing parameters");
                    }
                    break;

                case TASK_RESIZE:
                    if (taskDetails.getmMaxSize() != 0 && mSingleImageResizeCallback != null) {
                            startImageResize(queueImageFiles,
                                             taskDetails.getmMaxSize());
                    } else {
                        Log.e(TAG , "Operation " +TASK_RESIZE+ " aborted , missing parameters");
                    }
                    break;

                case TASK_RESIZE_AND_COMPRESS_TO_RATIO:
                    if (mImageListCopyCallback != null && taskDetails.getmMaxSize() != 0 && taskDetails.getmCompressionRatio() != 0) {
                        startImageResizeAndCompress(queueImageFiles,
                                                    taskDetails.getmMaxSize(),
                                                    taskDetails.getmCompressionRatio());
                    } else {
                        Log.e(TAG , "Operation " +TASK_RESIZE_AND_COMPRESS_TO_RATIO+ " aborted , missing parameters");
                    }
                    break;
                case TASK_COMPRESS_TO_RATIO:
                    if (taskDetails.getmCompressionRatio() != 0 && mImageListResizeCallback != null) {
                         startImageCompress(queueImageFiles,
                                            taskDetails.getmCompressionRatio());
                    } else {
                        Log.e(TAG , "Operation " + TASK_COMPRESS_TO_RATIO +" aborted , missing parameters");
                    }
                    break;
            }
        } else {
            Log.e(TAG, "mTaskModel is null , aborting");
        }
    }

    private void startImageResizeAndCompress(@NonNull List<File> mQueueImageFiles, int maximumResizeWidth, int compressionRatio) {
        Log.d(TAG , "Submitting assigned files to main executor thread instance at " + new Date());
        mMainListenerTaskCallable = new MainListenerTaskCallable(mQueueImageFiles, TASK_RESIZE_AND_COMPRESS_TO_RATIO);
        mMainListenerTaskCallable.setmCompressionRatio(compressionRatio);
        mMainListenerTaskCallable.setmMaximumResizeWidth(maximumResizeWidth);

        assignResizeCallbacks();
        mMainExecutorThread.submit(mMainListenerTaskCallable);
    }

    private void startImageResize(@NonNull List<File> mQueueImageFiles, int maximumResizeWidth) {
        Log.d(TAG , "Submitting assigned files to main executor thread instance at " + new Date());
        mMainListenerTaskCallable = new MainListenerTaskCallable(mQueueImageFiles, TASK_RESIZE_AND_COMPRESS_TO_RATIO );
        mMainListenerTaskCallable.setmMaximumResizeWidth(maximumResizeWidth);

        assignResizeCallbacks();
        mMainExecutorThread.submit(mMainListenerTaskCallable);
    }

    private void startImageCompress(@NonNull List<File> mQueueImageFiles, int compressionRatio) {
        Log.d(TAG , "Submitting assigned files to main executor thread instance at " + new Date());
        mMainListenerTaskCallable = new MainListenerTaskCallable(mQueueImageFiles, TASK_RESIZE_AND_COMPRESS_TO_RATIO );
        mMainListenerTaskCallable.setmCompressionRatio(compressionRatio);

        assignResizeCallbacks();
        mMainExecutorThread.submit(mMainListenerTaskCallable);
    }

    private void startImageCopy(@NonNull List<File> mQueueImageFiles, @NonNull File destinationDirectory){
        Log.d(TAG , "Submitting assigned files to main executor thread instance at " + new Date());
        mMainListenerTaskCallable = new MainListenerTaskCallable(mQueueImageFiles, TASK_MOVE_TO_DIRECTORY);
        mMainListenerTaskCallable.setmCopyDestinationDirectory(destinationDirectory);

        assignCopyCallbacks();
        mMainExecutorThread.submit(mMainListenerTaskCallable);
    }

    private void assignResizeCallbacks() {
        mMainListenerTaskCallable.setmSingleImageResizeCallback(mSingleImageResizeCallback);
        mMainListenerTaskCallable.setmImageListResizeCallback(mImageListResizeCallback);
    }

    private void assignCopyCallbacks() {
        Log.d(TAG , "Assigning image copy callbacks for the executed tasks");
        mMainListenerTaskCallable.setmImageListCopyCallback(mImageListCopyCallback);
        mMainListenerTaskCallable.setmSingleImageCopyCallback(mSingleImageCopyCallback);
    }
}
