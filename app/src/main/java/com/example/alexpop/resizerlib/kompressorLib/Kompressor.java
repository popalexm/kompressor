package com.example.alexpop.resizerlib.kompressorLib;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.ImageListCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.ImageListResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.SingleImageCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.SingleImageResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.definitions.CompressionParameters;
import com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType;
import com.example.alexpop.resizerlib.kompressorLib.taskmanager.TaskManager;
import com.example.alexpop.resizerlib.kompressorLib.tasks.MainTaskParameters;

import android.support.annotation.NonNull;

public final class Kompressor {

    private static Kompressor instance;
    private final String TAG = Kompressor.class.getSimpleName();
    /**
     * Callbacks for image resize or copy tasks back to the UI / calling thread
     */
    private ImageListResizeCallback imageListResizeCallback;
    private ImageListCopyCallback imageListCopyCallback;
    private SingleImageCopyCallback singleImageCopyCallback;
    private SingleImageResizeCallback singleImageResizeCallback;

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

    public void withResizeCallback(@NonNull ImageListResizeCallback uiCallback) {
        this.imageListResizeCallback = uiCallback;
    }

    public void withCopyCallback(@NonNull ImageListCopyCallback uiCallback) {
        this.imageListCopyCallback = uiCallback;
    }

    public void withSingleImageCopyCallback(@NonNull SingleImageCopyCallback uiCallback) {
        this.singleImageCopyCallback = uiCallback;
    }

    public void withSingleImageResizeCallback(@NonNull SingleImageResizeCallback uiCallback) {
        this.singleImageResizeCallback = uiCallback;
    }

    /**
     * Verifies if the assigned task is valid or not , if a valid task is found, it is assigned to the task manager, else returns false
     */
    public void startTask(@NonNull MainTaskParameters parameters) {
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
    private void createAndExecuteTask(@NonNull MainTaskParameters parameters) {
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
    private void startStartResizeTask(@NonNull MainTaskParameters parameters, @NonNull TaskManager taskManager) {
        if (imageListResizeCallback != null) {
            taskManager.setImageListResizeCallback(imageListResizeCallback);
        }
        if (singleImageResizeCallback != null) {
            taskManager.setSingleImageResizeCallback(singleImageResizeCallback);
        }
        taskManager.setTaskParameters(parameters);
        taskManager.executeTask();
    }

    /**
     * Validates callbacks , starts a resize task for the assigned TaskDetails object
     */
    private void startStartCopyTask(@NonNull MainTaskParameters parameters, @NonNull TaskManager taskManager) {
        if (imageListCopyCallback != null) {
            taskManager.setImageListCopyCallback(imageListCopyCallback);
        }
        if (singleImageCopyCallback != null) {
            taskManager.setSingleImageCopyCallback(singleImageCopyCallback);
        }
        taskManager.setTaskParameters(parameters);
        taskManager.executeTask();
    }
}