package com.example.alexpop.resizerlib.kompressorLib.taskmanager;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchCopySuccessListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchResizeListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemCopyListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemResizeListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.StartingAssignedTaskListener;
import com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType;
import com.example.alexpop.resizerlib.kompressorLib.tasks.KompressorParameters;
import com.example.alexpop.resizerlib.kompressorLib.tasks.MainTaskCallable;
import com.example.alexpop.resizerlib.kompressorLib.threadmanager.ThreadPoolCreator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType.TASK_COPY_TO_DIRECTORY;
import static com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType.TASK_RESIZE_AND_COMPRESS_TO_RATIO;

public final class TaskManager {

    private final String TAG = TaskManager.class.getSimpleName();
    private static TaskManager instance;
    /**
     * Task Manager status boolean,
     * set to true when worker threads are busy processing files,
     * else is set to false
     */
    public static AtomicBoolean isMainTaskThreadBusy = new AtomicBoolean();

    @Nullable
    private KompressorParameters kompressorParameters;
    /**
     * Callbacks back to the main thread that is calling the library
     */
    @Nullable
    private EntireBatchCopySuccessListener entireBatchCopySuccessListener;
    @Nullable
    private EntireBatchResizeListener entireBatchResizeListener;
    @Nullable
    private IndividualItemCopyListener individualItemCopyListener;
    @Nullable
    private IndividualItemResizeListener individualItemResizeListener;
    @Nullable
    private StartingAssignedTaskListener startingAssignedTaskListener;

    private ExecutorService mMainExecutorThread;

    private TaskManager() {
    }

    public static synchronized TaskManager getInstance() {
        if (TaskManager.instance == null) {
            TaskManager.instance = new TaskManager();
        }
        return TaskManager.instance;
    }

    public void setImageListCopyCallback(@NonNull EntireBatchCopySuccessListener imageCopyStatusCallback) {
        this.entireBatchCopySuccessListener = imageCopyStatusCallback;
    }

    public void setSingleImageCopyCallback(@NonNull IndividualItemCopyListener individualItemCopyListener) {
        this.individualItemCopyListener = individualItemCopyListener;
    }

    public void setImageListResizeCallback(@NonNull EntireBatchResizeListener entireBatchResizeListener) {
        this.entireBatchResizeListener = entireBatchResizeListener;
    }

    public void setSingleImageResizeCallback(@NonNull IndividualItemResizeListener imageListResizeCallback) {
        this.individualItemResizeListener = imageListResizeCallback;
    }

    public void setStartingAssignedTaskListener(@NonNull StartingAssignedTaskListener startingAssignedTaskListener) {
        this.startingAssignedTaskListener = startingAssignedTaskListener;
    }

    public void setTaskParameters(@NonNull KompressorParameters parameters) {
        this.kompressorParameters = parameters;
    }

    public void executeTask() {
        if (kompressorParameters != null) {
            mMainExecutorThread = ThreadPoolCreator.createMainExecutorService();
            if (!TaskManager.isMainTaskThreadBusy.get()) {
                assignParametersAndRun(kompressorParameters);
            }
        }
    }

    private void assignParametersAndRun(@NonNull KompressorParameters kompressorParameters) {
        List<File> imageFiles = kompressorParameters.getImageFiles();
        TaskType taskType = kompressorParameters.getTaskType();
        switch (taskType) {
            case TASK_COPY_TO_DIRECTORY:
                File copyToDestination = kompressorParameters.getToCopyDestinationDirectory();
                if (copyToDestination != null && entireBatchCopySuccessListener != null) {
                    startImageCopyToDestination(imageFiles, copyToDestination);
                }
                break;

            case TASK_JUST_RESIZE:

                break;

            case TASK_RESIZE_AND_COMPRESS_TO_RATIO:
                int maxResizeWidth = kompressorParameters.getMaximumResizeWidth();
                int maxCompressionRatio = kompressorParameters.getCompressionRatio();
                if (entireBatchResizeListener != null && maxResizeWidth != 0 && maxCompressionRatio != 0) {
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
        if (individualItemResizeListener != null) {
            mainTaskCallable.setIndividualItemResizeListener(individualItemResizeListener);
        }
        if (entireBatchResizeListener != null) {
            mainTaskCallable.setEntireBatchResizeListener(entireBatchResizeListener);
        }
        if (startingAssignedTaskListener != null) {
            mainTaskCallable.setStartingAssignedTaskListener(startingAssignedTaskListener);
        }
    }

    /**
     * Assign callbacks in the case of an copy task
     */
    private void assignCopyCallbacks(@NonNull MainTaskCallable mainTaskCallable) {
        if (individualItemCopyListener != null) {
            mainTaskCallable.setIndividualItemCopyListener(individualItemCopyListener);
        }
        if (entireBatchCopySuccessListener != null) {
            mainTaskCallable.setEntireBatchCopySuccessListener(entireBatchCopySuccessListener);
        }
        if (startingAssignedTaskListener != null) {
            mainTaskCallable.setStartingAssignedTaskListener(startingAssignedTaskListener);
        }
    }
}
