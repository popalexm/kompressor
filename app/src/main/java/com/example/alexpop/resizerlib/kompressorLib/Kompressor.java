package com.example.alexpop.resizerlib.kompressorLib;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchCopySuccessListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchResizeListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemCopyListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemResizeListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.StartingAssignedTaskListener;
import com.example.alexpop.resizerlib.kompressorLib.definitions.CompressionParameters;
import com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType;
import com.example.alexpop.resizerlib.kompressorLib.taskmanager.TaskManager;
import com.example.alexpop.resizerlib.kompressorLib.tasks.KompressorParameters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Kompressor {

    @Nullable
    private static Kompressor instance;
    /**
     * Callbacks for image resize or copy tasks back to the UI / calling thread
     */
    @Nullable
    private EntireBatchResizeListener entireBatchResizeListener;
    @Nullable
    private EntireBatchCopySuccessListener entireBatchCopySuccessListener;
    @Nullable
    private IndividualItemCopyListener individualItemCopyListener;
    @Nullable
    private IndividualItemResizeListener individualItemResizeListener;
    @Nullable
    private StartingAssignedTaskListener startingAssignedTaskListener;

    private Kompressor() {
    }
    /**
     * Returns main library instance
     */
    public static synchronized Kompressor get() {
        if (Kompressor.instance == null) {
            Kompressor.instance = new Kompressor();
        }
        return Kompressor.instance;
    }

    /**
     * Assign callbacks back to the calling thread
     */

    public void withBatchResizeCallbacks(@NonNull EntireBatchResizeListener uiCallback) {
        this.entireBatchResizeListener = uiCallback;
    }

    public void withBatchCopyCallbacks(@NonNull EntireBatchCopySuccessListener uiCallback) {
        this.entireBatchCopySuccessListener = uiCallback;
    }

    public void withSingleItemCopyCallbacks(@NonNull IndividualItemCopyListener uiCallback) {
        this.individualItemCopyListener = uiCallback;
    }

    public void withSingleItemResizeCallbacks(@NonNull IndividualItemResizeListener uiCallback) {
        this.individualItemResizeListener = uiCallback;
    }

    public void withStartingAssignedTaskListener(StartingAssignedTaskListener uiCallback) {
        this.startingAssignedTaskListener = uiCallback;
    }

    /**
     * Verifies if the assigned task is valid or not , if a valid task is found, it is assigned to the task manager, else returns false
     */
    public void startTask(@NonNull KompressorParameters parameters) {
        for (TaskType availableTasks : TaskType.values()) {
            if (parameters.getTaskType()
                    .name()
                    .equals(availableTasks.name())) {
                if (parameters.getImageFiles()
                        .size() > 0) {
                    createAndExecuteTask(parameters);
                }
            }
        }
    }

    /**
     * Verifies if task parameters are assigned correctly, notifies the TaskManager class to start the assigned task
     *
     * @param parameters task parameters
     */
    private void createAndExecuteTask(@NonNull KompressorParameters parameters) {
        TaskManager taskManager = TaskManager.getInstance();
        switch (parameters.getTaskType()) {
            case TASK_RESIZE_AND_COMPRESS_TO_RATIO:
                int compressionRatio = parameters.getCompressionRatio();
                if (parameters.getMaximumResizeWidth() > 0 && compressionRatio > CompressionParameters.MIN_COMPRESSION_RATIO
                        && compressionRatio <= CompressionParameters.MAX_COMPRESSION_RATIO) {
                    startStartResizeTask(parameters, taskManager);
                }
                break;

            case TASK_COPY_TO_DIRECTORY:
                if (parameters.getToCopyDestinationDirectory() != null) {
                    startStartCopyTask(parameters, taskManager);
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
     * Validates callbacks , starts a resize task for the assigned TaskDetails object
     */
    private void startStartResizeTask(@NonNull KompressorParameters parameters, @NonNull TaskManager taskManager) {
        setTaskManagerResizeListeners(taskManager);
        setTaskStartingLisneter(taskManager);

        taskManager.setTaskParameters(parameters);
        taskManager.executeTask();
    }

    /**
     * Validates callbacks , starts a resize task for the assigned TaskDetails object
     */
    private void startStartCopyTask(@NonNull KompressorParameters parameters, @NonNull TaskManager taskManager) {
        setTaskManagerCopyListeners(taskManager);
        setTaskStartingLisneter(taskManager);

        taskManager.setTaskParameters(parameters);
        taskManager.executeTask();
    }

    private void setTaskManagerCopyListeners(@NonNull TaskManager taskManager) {
        if (entireBatchCopySuccessListener != null) {
            taskManager.setImageListCopyCallback(entireBatchCopySuccessListener);
        }
        if (individualItemCopyListener != null) {
            taskManager.setSingleImageCopyCallback(individualItemCopyListener);
        }
    }

    private void setTaskManagerResizeListeners(@NonNull TaskManager taskManager) {
        if (entireBatchResizeListener != null) {
            taskManager.setImageListResizeCallback(entireBatchResizeListener);
        }
        if (individualItemResizeListener != null) {
            taskManager.setSingleImageResizeCallback(individualItemResizeListener);
        }
    }

    private void setTaskStartingLisneter(@NonNull TaskManager taskManager) {
        if (startingAssignedTaskListener != null) {
            taskManager.setStartingAssignedTaskListener(startingAssignedTaskListener);
        }
    }
}