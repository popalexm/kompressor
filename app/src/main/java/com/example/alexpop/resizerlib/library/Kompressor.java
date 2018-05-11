package com.example.alexpop.resizerlib.library;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.alexpop.resizerlib.library.callbacks.ImageListCopyCallback;
import com.example.alexpop.resizerlib.library.callbacks.ImageListResizeCallback;
import com.example.alexpop.resizerlib.library.callbacks.KompressorStatusCallback;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageCopyCallback;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageResizeCallback;
import com.example.alexpop.resizerlib.library.definitions.TaskDetails;
import com.example.alexpop.resizerlib.library.definitions.TaskModel;
import com.example.alexpop.resizerlib.library.definitions.TaskType;
import com.example.alexpop.resizerlib.library.taskmanager.TaskManager;

import java.io.File;
import java.util.ArrayList;

public class Kompressor {

    private String TAG = Kompressor.class.getSimpleName();

    private static Kompressor mInstance;

    private Kompressor(){ }

    public static synchronized Kompressor get(){
        if(mInstance == null){
            mInstance = new Kompressor();
        }
        return mInstance;
    }

    private ArrayList<File> mQueueImageFiles;

    private ImageListResizeCallback mImageListResizeCallbackListener;
    private ImageListCopyCallback mImageListCopyCallbackListener;
    private SingleImageCopyCallback mSingleImageCopyListener;
    private SingleImageResizeCallback mSingleImageResizeListener;
    private KompressorStatusCallback mKompressorStatusListener;

    private TaskManager mTaskManager;

    private int mMaxSize = 0;
    private int mCompressionRatio = 0;
    private File mDestinationDirectory;

    public void loadResources(@NonNull ArrayList<File> imagesToProcess) {
        mQueueImageFiles = imagesToProcess;
    }

    public void withMaxHeight(int maximumSize) {
        this.mMaxSize = maximumSize;
    }

    public void withCompressionRatio(int compressionRatio) {
        this.mCompressionRatio = compressionRatio;
    }

    public void toDestinationPath(@NonNull File destPath) {
        this.mDestinationDirectory = destPath;
    }

    public void withResizeCallback(@NonNull ImageListResizeCallback uiCallback) {
        this.mImageListResizeCallbackListener = uiCallback;
    }

    public void withCopyCallback(@NonNull ImageListCopyCallback uiCallback) {
        this.mImageListCopyCallbackListener = uiCallback;
    }

    public void withSingleImageCopyCallback(@NonNull SingleImageCopyCallback uiCallback) {
        this.mSingleImageCopyListener = uiCallback;
    }

    public void withSingleImageResizeCallback(@NonNull SingleImageResizeCallback uiCallback) {
        this.mSingleImageResizeListener = uiCallback;
    }

    public void withStatusCallback(@NonNull KompressorStatusCallback statusCallback){
        this.mKompressorStatusListener = statusCallback;
    }

    public void startTask(@NonNull TaskType assignedTaskType) {
        boolean foundValidAssignedTask = false;
        for (TaskType availableTasks : TaskType.values()) {
            if (assignedTaskType.name().equals(availableTasks.name())) {
                if (mQueueImageFiles != null && mQueueImageFiles.size()> 0){
                    Log.d(TAG, "Sending " + mQueueImageFiles.size() + " images with assigned task " + assignedTaskType.name());
                    createAndExecuteTask(assignedTaskType);
                    foundValidAssignedTask = true;
                } else {
                    Log.e(TAG, "There are no assigned images to process, returning callback to main thread and exiting -1");
                }
            }
        }
        if (!foundValidAssignedTask){
            Log.d(TAG , "Could not find any available task with this value, exiting with status code -1 ");
        }
    }

    private void createAndExecuteTask(@NonNull TaskType assignedTaskType) {
        TaskDetails taskDetails =  new TaskDetails();
        mTaskManager = TaskManager.getInstance();

        switch (assignedTaskType) {
            case TASK_RESIZE_AND_COMPRESS_TO_RATIO:
                if (mMaxSize > 0) {
                    if (mCompressionRatio > 0) {
                        Log.d(TAG, "Setting maxSize to " + mMaxSize + " compression ratio" + mCompressionRatio);
                        taskDetails.setmMaxSize(mMaxSize);
                        taskDetails.setmCompressionRatio(mCompressionRatio);
                        startStartResizeTask(taskDetails, assignedTaskType);
                    } else {
                        Log.e(TAG, "Invalid parameters, compression ratio has not been set !");
                    }
                } else {
                    Log.e(TAG, "Invalid parameter, maximum image size has not been set !");
                }
                break;

            case TASK_MOVE_TO_DIRECTORY:
                if (mDestinationDirectory != null) {
                        Log.d(TAG, "Setting copy destination path to " + mDestinationDirectory);
                        taskDetails.setmDestinationPath(mDestinationDirectory);
                        startStartCopyTask(taskDetails, assignedTaskType);
                } else {
                    Log.e(TAG, "Invalid task parameters , destination directory has not been set");
                }
                break;
        }
    }

    private void startStartResizeTask(@NonNull TaskDetails taskDetails, @NonNull TaskType taskType) {
        TaskModel taskModel = new TaskModel(taskType , mQueueImageFiles, taskDetails);
        Log.d(TAG , "Setting task model " + taskModel.toString());
        if (mImageListResizeCallbackListener != null) {
            mTaskManager.setImageListResizeCallback(mImageListResizeCallbackListener);
        }
        if (mSingleImageResizeListener != null) {
            mTaskManager.setSingleImageResizeCallback(mSingleImageResizeListener);
        }
        if (mKompressorStatusListener != null) {
            mTaskManager.setmKompressorStatusCallback(mKompressorStatusListener);
        }
        mTaskManager.setTask(taskModel);
        mTaskManager.executeTask();
    }

    private void startStartCopyTask(@NonNull TaskDetails taskDetails, @NonNull TaskType taskType) {
        TaskModel taskModel = new TaskModel(taskType , mQueueImageFiles, taskDetails);
        Log.d(TAG , "Setting task model " + taskModel.toString());
        if (mImageListCopyCallbackListener != null) {
            mTaskManager.setImageListCopyCallback(mImageListCopyCallbackListener);
        }
        if (mSingleImageCopyListener != null) {
            mTaskManager.setSingleImageCopyCallback(mSingleImageCopyListener);
        }
        if (mKompressorStatusListener != null) {
            mTaskManager.setmKompressorStatusCallback(mKompressorStatusListener);
        }
        mTaskManager.setTask(taskModel);
        mTaskManager.executeTask();
    }
}