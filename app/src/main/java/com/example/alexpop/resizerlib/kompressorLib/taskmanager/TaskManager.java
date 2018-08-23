package com.example.alexpop.resizerlib.kompressorLib.taskmanager;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.ImageListCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.ImageListResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.SingleImageCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.SingleImageResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType;
import com.example.alexpop.resizerlib.kompressorLib.tasks.MainTaskCallable;
import com.example.alexpop.resizerlib.kompressorLib.tasks.MainTaskParameters;
import com.example.alexpop.resizerlib.kompressorLib.threadmanager.ThreadPoolCreator;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType.TASK_COPY_TO_DIRECTORY;
import static com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType.TASK_RESIZE_AND_COMPRESS_TO_RATIO;

public final class TaskManager {

    private static TaskManager instance;
    /**
     * Task Manager status boolean,
     * set to true when worker threads are busy processing files,
     * else is set to false
     */
    public static AtomicBoolean isMainTaskThreadBusy = new AtomicBoolean();
    private final String TAG = TaskManager.class.getSimpleName();
    private MainTaskParameters mainTaskParameters;
    /**
     * Callbacks back to the main thread that is calling the library
     */
    private ImageListCopyCallback mImageListCopyCallback;
    private ImageListResizeCallback mImageListResizeCallback;
    private SingleImageCopyCallback mSingleImageCopyCallback;
    private SingleImageResizeCallback mSingleImageResizeCallback;
    private ExecutorService mMainExecutorThread;

    private TaskManager() {
    }

    public static synchronized TaskManager getInstance() {
        if (TaskManager.instance == null) {
            TaskManager.instance = new TaskManager();
        }
        return TaskManager.instance;
    }

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

    public void setTaskParameters(@NonNull MainTaskParameters parameters) {
        this.mainTaskParameters = parameters;
    }

    public void executeTask() {
        if (mainTaskParameters != null) {
            mMainExecutorThread = ThreadPoolCreator.createMainExecutorService();
            if (!TaskManager.isMainTaskThreadBusy.get()) {
                assignParametersAndRun();
            }
        }
    }

    private void assignParametersAndRun() {
        List<File> imageFiles = mainTaskParameters.getImageFiles();
        TaskType taskType = mainTaskParameters.getTaskType();
        switch (taskType) {
            case TASK_COPY_TO_DIRECTORY:
                File copyToDestination = mainTaskParameters.getToCopyDestinationDirectory();
                if (copyToDestination != null && mImageListCopyCallback != null) {
                    startImageCopyToDestination(imageFiles, copyToDestination);
                }
                break;

            case TASK_JUST_RESIZE:

                break;

            case TASK_RESIZE_AND_COMPRESS_TO_RATIO:
                int maxResizeWidth = mainTaskParameters.getMaximumResizeWidth();
                int maxCompressionRatio = mainTaskParameters.getCompressionRatio();

                if (mImageListCopyCallback != null && maxResizeWidth != 0 && maxCompressionRatio != 0) {
                    startImageResizeAndCompress(imageFiles, maxResizeWidth, maxCompressionRatio);
                }
                break;
            case TASK_JUST_COMPRESS:

                break;
        }
    }

    private void startImageResizeAndCompress(@NonNull List<File> toProcessFiles, int maximumResizeWidth, int compressionRatio) {
        MainTaskParameters mainTaskParameters = new MainTaskParameters.MainTaskParametersBuilder().setImageFiles(toProcessFiles)
                .setTaskType(TASK_RESIZE_AND_COMPRESS_TO_RATIO)
                .setMaximumResizeWidth(maximumResizeWidth)
                .setCompressionRatio(compressionRatio)
                .createMainTaskParameters();

        MainTaskCallable mainTaskCallable = new MainTaskCallable(mainTaskParameters);
        assignResizeCallbacks(mainTaskCallable);

        mMainExecutorThread.submit(mainTaskCallable);
        TaskManager.isMainTaskThreadBusy.set(true);
    }

    private void startImageCopyToDestination(@NonNull List<File> toProcessFiles, @NonNull File destinationDirectory) {
        MainTaskParameters mainTaskParameters = new MainTaskParameters.MainTaskParametersBuilder().setImageFiles(toProcessFiles)
                .setTaskType(TASK_COPY_TO_DIRECTORY)
                .setToCopyDestinationDirectory(destinationDirectory)
                .createMainTaskParameters();

        MainTaskCallable mainTaskCallable = new MainTaskCallable(mainTaskParameters);
        assignCopyCallbacks(mainTaskCallable);

        mMainExecutorThread.submit(mainTaskCallable);
        TaskManager.isMainTaskThreadBusy.set(true);
    }

    /**
     * Assign callbacks in the case of an resize task
     */
    private void assignResizeCallbacks(@NonNull MainTaskCallable mainTaskCallable) {
        mainTaskCallable.setSingleImageResizeCallback(mSingleImageResizeCallback);
        mainTaskCallable.setImageListResizeCallback(mImageListResizeCallback);
    }

    /**
     * Assign callbacks in the case of an copy task
     */
    private void assignCopyCallbacks(@NonNull MainTaskCallable mainTaskCallable) {
        mainTaskCallable.setImageListCopyCallback(mImageListCopyCallback);
        mainTaskCallable.setSingleImageCopyCallback(mSingleImageCopyCallback);
    }
}
