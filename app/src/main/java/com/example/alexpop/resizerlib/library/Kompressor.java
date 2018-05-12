package com.example.alexpop.resizerlib.library;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.alexpop.resizerlib.library.callbacks.ImageListCopyCallback;
import com.example.alexpop.resizerlib.library.callbacks.ImageListResizeCallback;
import com.example.alexpop.resizerlib.library.callbacks.KompressorStatusCallback;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageCopyCallback;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageResizeCallback;
import com.example.alexpop.resizerlib.library.definitions.Parameters;
import com.example.alexpop.resizerlib.library.definitions.TaskDetails;
import com.example.alexpop.resizerlib.library.definitions.TaskModel;
import com.example.alexpop.resizerlib.library.definitions.TaskType;
import com.example.alexpop.resizerlib.library.taskmanager.TaskManager;

import java.io.File;
import java.util.ArrayList;

public class Kompressor {

    private String TAG = Kompressor.class.getSimpleName();

    private static Kompressor sInstance;

    private Kompressor(){ }

    /**
      Returns main library instance
     */
    public static synchronized Kompressor get(){
        if(sInstance == null){
            sInstance = new Kompressor();
        }
        return sInstance;
    }

    private ArrayList<File> mQueueImageFiles;

    /**Callbacks for image resize or copy tasks back to the UI / calling thread
     */
    private ImageListResizeCallback mImageListResizeCallbackListener;
    private ImageListCopyCallback mImageListCopyCallbackListener;
    private SingleImageCopyCallback mSingleImageCopyListener;
    private SingleImageResizeCallback mSingleImageResizeListener;
    private KompressorStatusCallback mKompressorStatusListener;

    private TaskManager mTaskManager;

    private int mMaxSize = 0;
    private int mCompressionRatio = 0;
    private File mDestinationDirectory;

    /** Assign a list of image files to be assigned to the processing queue
     */
    public void loadResources(@NonNull ArrayList<File> imagesToProcess) {
        mQueueImageFiles = imagesToProcess;
    }

    /** Assign a maximum height at this the image files should be resized
     */
    public void withMaxHeight(int maximumSize) {
        this.mMaxSize = maximumSize;
    }
    /** Assign a compression , valid values being between 0 and 100
    */
    public void withCompressionRatio(int compressionRatio) {
        this.mCompressionRatio = compressionRatio;
    }

    /** Assign a destination path to copy tasks
     */
    public void toDestinationPath(@NonNull File destPath) {
        this.mDestinationDirectory = destPath;
    }

    /** Assign callbacks back to the calling thread */

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

    /** Verifies if the assigned task is valid or not , if a valid task is found, it is assigned to the task manager, else returns false
      @return boolean
     */
    public boolean startTask(@NonNull TaskType assignedTaskType) {
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
        return foundValidAssignedTask;
    }

    /** Verifies if task parameters are assigned correctly,
     *  creates a TaskDetails object and notifies the TaskManager class to start the assigned task
     *  @param assignedTaskType task type
     */
    private void createAndExecuteTask(@NonNull TaskType assignedTaskType) {
        TaskDetails taskDetails =  new TaskDetails();
        mTaskManager = TaskManager.getInstance();

        switch (assignedTaskType) {
            case TASK_RESIZE_AND_COMPRESS_TO_RATIO:
                if (mMaxSize > 0) {
                    if (mCompressionRatio > 0) {
                        if (mCompressionRatio > Parameters.MIN_COMPPRESSION_RATIO && mCompressionRatio <= Parameters.MAX_COMPPRESSION_RATIO) {
                            taskDetails.setmMaxSize(mMaxSize);
                            taskDetails.setmCompressionRatio(mCompressionRatio);
                            startStartResizeTask(taskDetails, assignedTaskType);
                        } else {
                            Log.e(TAG,  mCompressionRatio + " has not been set between " + Parameters.MIN_COMPPRESSION_RATIO + " and " + Parameters.MAX_COMPPRESSION_RATIO);
                        }
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

            case TASK_JUST_RESIZE:
                // TODO Implement just just resize task functionality
                break;

            case TASK_JUST_COMPRESS:
                // TODO Implement just just compress task functionality
                break;
        }
    }
    /**
      Validates callbacks , starts a resize task for the assigned TaskDetails object
     @param taskDetails task details object
     @param taskType task type
     */
    private void startStartResizeTask(@NonNull TaskDetails taskDetails, @NonNull TaskType taskType) {
        TaskModel taskModel = new TaskModel(taskType, mQueueImageFiles, taskDetails);
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
    /**
     Validates callbacks , starts a resize task for the assigned TaskDetails object
     @param taskDetails
     @param taskType
     */
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