package com.example.alexpop.resizerlib.kompressorLib.taskmanager;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType;
import com.example.alexpop.resizerlib.kompressorLib.tasks.KompressorParameters;
import com.example.alexpop.resizerlib.kompressorLib.tasks.MainTaskCallable;
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
    private KompressorParameters kompressorParameters;
    /**
     * Callbacks back to the main thread that is calling the library
     */
    private EntireBatchCopyCallback mEntireBatchCopyCallback;
    private EntireBatchResizeCallback mEntireBatchResizeCallback;
    private IndividualItemCopyCallback mIndividualItemCopyCallback;
    private IndividualItemResizeCallback mIndividualItemResizeCallback;
    private ExecutorService mMainExecutorThread;

    private TaskManager() {
    }

    public static synchronized TaskManager getInstance() {
        if (TaskManager.instance == null) {
            TaskManager.instance = new TaskManager();
        }
        return TaskManager.instance;
    }

    public void setImageListCopyCallback(@NonNull EntireBatchCopyCallback imageCopyStatusCallback) {
        this.mEntireBatchCopyCallback = imageCopyStatusCallback;
    }

    public void setSingleImageCopyCallback(@NonNull IndividualItemCopyCallback individualItemCopyCallback) {
        this.mIndividualItemCopyCallback = individualItemCopyCallback;
    }

    public void setImageListResizeCallback(@NonNull EntireBatchResizeCallback entireBatchResizeCallback) {
        this.mEntireBatchResizeCallback = entireBatchResizeCallback;
    }

    public void setSingleImageResizeCallback(@NonNull IndividualItemResizeCallback imageListResizeCallback) {
        this.mIndividualItemResizeCallback = imageListResizeCallback;
    }

    public void setTaskParameters(@NonNull KompressorParameters parameters) {
        this.kompressorParameters = parameters;
    }

    public void executeTask() {
        if (kompressorParameters != null) {
            mMainExecutorThread = ThreadPoolCreator.createMainExecutorService();
            if (!TaskManager.isMainTaskThreadBusy.get()) {
                assignParametersAndRun();
            }
        }
    }

    private void assignParametersAndRun() {
        List<File> imageFiles = kompressorParameters.getImageFiles();
        TaskType taskType = kompressorParameters.getTaskType();
        switch (taskType) {
            case TASK_COPY_TO_DIRECTORY:
                File copyToDestination = kompressorParameters.getToCopyDestinationDirectory();
                if (copyToDestination != null && mEntireBatchCopyCallback != null) {
                    startImageCopyToDestination(imageFiles, copyToDestination);
                }
                break;

            case TASK_JUST_RESIZE:

                break;

            case TASK_RESIZE_AND_COMPRESS_TO_RATIO:
                int maxResizeWidth = kompressorParameters.getMaximumResizeWidth();
                int maxCompressionRatio = kompressorParameters.getCompressionRatio();
                if (mEntireBatchCopyCallback != null && maxResizeWidth != 0 && maxCompressionRatio != 0) {
                    startImageResizeAndCompress(imageFiles, maxResizeWidth, maxCompressionRatio);
                }
                break;
            case TASK_JUST_COMPRESS:

                break;
        }
    }

    private void startImageResizeAndCompress(@NonNull List<File> toProcessFiles, int maximumResizeWidth, int compressionRatio) {
        KompressorParameters kompressorParameters = new KompressorParameters.MainTaskParametersBuilder().setImageFiles(toProcessFiles)
                .setTaskType(TASK_RESIZE_AND_COMPRESS_TO_RATIO)
                .setMaximumResizeWidth(maximumResizeWidth)
                .setCompressionRatio(compressionRatio)
                .createMainTaskParameters();

        MainTaskCallable mainTaskCallable = new MainTaskCallable(kompressorParameters);
        assignResizeCallbacks(mainTaskCallable);

        mMainExecutorThread.submit(mainTaskCallable);
        TaskManager.isMainTaskThreadBusy.set(true);
    }

    private void startImageCopyToDestination(@NonNull List<File> toProcessFiles, @NonNull File destinationDirectory) {
        KompressorParameters kompressorParameters = new KompressorParameters.MainTaskParametersBuilder().setImageFiles(toProcessFiles)
                .setTaskType(TASK_COPY_TO_DIRECTORY)
                .setToCopyDestinationDirectory(destinationDirectory)
                .createMainTaskParameters();

        MainTaskCallable mainTaskCallable = new MainTaskCallable(kompressorParameters);
        assignCopyCallbacks(mainTaskCallable);

        mMainExecutorThread.submit(mainTaskCallable);
        TaskManager.isMainTaskThreadBusy.set(true);
    }

    /**
     * Assign callbacks in the case of an resize task
     */
    private void assignResizeCallbacks(@NonNull MainTaskCallable mainTaskCallable) {
        mainTaskCallable.setIndividualItemResizeCallback(mIndividualItemResizeCallback);
        mainTaskCallable.setEntireBatchResizeCallback(mEntireBatchResizeCallback);
    }

    /**
     * Assign callbacks in the case of an copy task
     */
    private void assignCopyCallbacks(@NonNull MainTaskCallable mainTaskCallable) {
        mainTaskCallable.setEntireBatchCopyCallback(mEntireBatchCopyCallback);
        mainTaskCallable.setIndividualItemCopyCallback(mIndividualItemCopyCallback);
    }
}
