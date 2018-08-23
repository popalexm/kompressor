package com.example.alexpop.resizerlib.kompressorLib;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.definitions.CompressionParameters;
import com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType;
import com.example.alexpop.resizerlib.kompressorLib.taskmanager.TaskManager;
import com.example.alexpop.resizerlib.kompressorLib.tasks.KompressorParameters;

import android.support.annotation.NonNull;

public final class Kompressor {

    private static Kompressor instance;
    private final String TAG = Kompressor.class.getSimpleName();
    /**
     * Callbacks for image resize or copy tasks back to the UI / calling thread
     */
    private EntireBatchResizeCallback entireBatchResizeCallback;
    private EntireBatchCopyCallback entireBatchCopyCallback;
    private IndividualItemCopyCallback singleImageCopyCallback;
    private IndividualItemResizeCallback individualItemResizeCallback;

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

    public void withResizeCallback(@NonNull EntireBatchResizeCallback uiCallback) {
        this.entireBatchResizeCallback = uiCallback;
    }

    public void withCopyCallback(@NonNull EntireBatchCopyCallback uiCallback) {
        this.entireBatchCopyCallback = uiCallback;
    }

    public void withSingleImageCopyCallback(@NonNull IndividualItemCopyCallback uiCallback) {
        this.singleImageCopyCallback = uiCallback;
    }

    public void withSingleImageResizeCallback(@NonNull IndividualItemResizeCallback uiCallback) {
        this.individualItemResizeCallback = uiCallback;
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
        if (entireBatchResizeCallback != null) {
            taskManager.setImageListResizeCallback(entireBatchResizeCallback);
        }
        if (individualItemResizeCallback != null) {
            taskManager.setSingleImageResizeCallback(individualItemResizeCallback);
        }
        taskManager.setTaskParameters(parameters);
        taskManager.executeTask();
    }

    /**
     * Validates callbacks , starts a resize task for the assigned TaskDetails object
     */
    private void startStartCopyTask(@NonNull KompressorParameters parameters, @NonNull TaskManager taskManager) {
        if (entireBatchCopyCallback != null) {
            taskManager.setImageListCopyCallback(entireBatchCopyCallback);
        }
        if (singleImageCopyCallback != null) {
            taskManager.setSingleImageCopyCallback(singleImageCopyCallback);
        }
        taskManager.setTaskParameters(parameters);
        taskManager.executeTask();
    }
}