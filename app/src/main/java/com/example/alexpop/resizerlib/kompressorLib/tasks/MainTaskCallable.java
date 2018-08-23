package com.example.alexpop.resizerlib.kompressorLib.tasks;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.ImageListCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.ImageListResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.SingleImageCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.SingleImageResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.definitions.ProcessingStatus;
import com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType;
import com.example.alexpop.resizerlib.kompressorLib.handlers.MainThreadMessageHandler;
import com.example.alexpop.resizerlib.kompressorLib.handlers.models.CopyStatusMessage;
import com.example.alexpop.resizerlib.kompressorLib.handlers.models.ResizeStatusMessage;
import com.example.alexpop.resizerlib.kompressorLib.taskmanager.TaskManager;
import com.example.alexpop.resizerlib.kompressorLib.threadmanager.ThreadPoolCreator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MainTaskCallable implements Callable<List<File>> {

    private final String TAG = MainTaskCallable.class.getSimpleName();
    /**
     * ProcessingStatus handler, used to call back the UI thread with the results of the operation
     */
    @NonNull
    private final MainThreadMessageHandler mainThreadMessageHandler;
    @NonNull
    private final MainTaskParameters parameters;
    /**
     * List of currently  assigned callable , on the worker thread pool
     */
    private List<BaseWorkerTaskCallable> activelyRunningTasks;
    /**
     * List of results from the operations , with a boolean status in the hashMap,
     * true for success, false for failed
     */
    private List<Pair<File, Boolean>> completedTasksResults;
    /**
     * Callbacks for the operations success / failure notification
     */
    @Nullable
    private ImageListResizeCallback imageListResizeCallback;
    @Nullable
    private ImageListCopyCallback imageListCopyCallback;
    @Nullable
    private SingleImageCopyCallback singleImageCopyCallback;
    @Nullable
    private SingleImageResizeCallback singleImageResizeCallback;

    public MainTaskCallable(@NonNull MainTaskParameters parameters) {
        this.parameters = parameters;
        mainThreadMessageHandler = MainThreadMessageHandler.getInstance();
    }

    public void setImageListResizeCallback(@NonNull ImageListResizeCallback imageListResizeCallback) {
        this.imageListResizeCallback = imageListResizeCallback;
    }

    public void setImageListCopyCallback(@NonNull ImageListCopyCallback imageListCopyCallback) {
        this.imageListCopyCallback = imageListCopyCallback;
    }

    public void setSingleImageCopyCallback(@NonNull SingleImageCopyCallback singleImageCopyCallback) {
        this.singleImageCopyCallback = singleImageCopyCallback;
    }

    public void setSingleImageResizeCallback(@NonNull SingleImageResizeCallback singleImageResizeCallback) {
        this.singleImageResizeCallback = singleImageResizeCallback;
    }

    @Override
    public List<File> call() {
        TaskType taskType = parameters.getTaskType();
        switch (taskType) {
            case TASK_RESIZE_AND_COMPRESS_TO_RATIO:
                executeImageResizeTaskQueue();
                break;

            case TASK_COPY_TO_DIRECTORY:
                executeImageCopyTaskQueue();
                break;

            case TASK_JUST_RESIZE:
                // TODO implement
                break;

            case TASK_JUST_COMPRESS:
                // TODO implement
                break;
        }
        sortResults(completedTasksResults, taskType);
        return null;
    }

    private void executeImageCopyTaskQueue() {
        CopyStatusMessage copyStatusMessage = new CopyStatusMessage.CopyMessageBuilder().setCallback(imageListCopyCallback)
                .setStatus(ProcessingStatus.PROCESSING_STARTED)
                .createCopyMessage();
        mainThreadMessageHandler.postBatchCopyMessage(copyStatusMessage);

        List<File> imageFiles = parameters.getImageFiles();
        File toCopyDestinationDirectory = parameters.getToCopyDestinationDirectory();

        if (toCopyDestinationDirectory != null && singleImageCopyCallback != null) {
            activelyRunningTasks = new ArrayList<>();
            for (int i = 0; i < imageFiles.size(); i++) {
                ImageCopyWorkerTask imageCopyWorkerTask = new ImageCopyWorkerTask(imageFiles.get(i), toCopyDestinationDirectory, singleImageCopyCallback);
                activelyRunningTasks.add(imageCopyWorkerTask);
            }
            awaitResults();
        }
    }

    private void executeImageResizeTaskQueue() {
        ResizeStatusMessage resizeStatusMessage = new ResizeStatusMessage.ResizeMessageBuilder().setCallback(imageListResizeCallback)
                .setStatus(ProcessingStatus.PROCESSING_STARTED)
                .createResizeMessage();
        mainThreadMessageHandler.postBatchResizeMessage(resizeStatusMessage);

        List<File> imagesFiles = parameters.getImageFiles();
        int maxResizeWidth = parameters.getMaximumResizeWidth();
        int compressionRatio = parameters.getCompressionRatio();
        if (maxResizeWidth > 0 && compressionRatio > 0 && singleImageResizeCallback != null) {
            for (int i = 0; i < imagesFiles.size(); i++) {
                String imgPath = imagesFiles.get(i)
                        .getPath();
                activelyRunningTasks = new ArrayList<>();
                ImageResizeWorkerTask imageResizeWorkerTask = new ImageResizeWorkerTask(imgPath, maxResizeWidth, compressionRatio, singleImageResizeCallback);
                activelyRunningTasks.add(imageResizeWorkerTask);
            }
        }
        awaitResults();
    }

    /**
     * Sorts the results of the resize or copy operations,
     * and posts messages back to the main thread with the number of successfully copied / resized files,
     * or the failed ones
     */
    private void sortResults(@NonNull List<Pair<File, Boolean>> fileProcessingResults, @NonNull TaskType assignedTaskType) {
        List<File> successfulImages = new ArrayList<>();
        List<File> failedImages = new ArrayList<>();

        for (Pair<File, Boolean> fileProcessingResult : fileProcessingResults) {
            File imgFile = fileProcessingResult.first;
            Boolean operationStatus = fileProcessingResult.second;
            if (operationStatus) {
                successfulImages.add(imgFile);
            } else {
                failedImages.add(imgFile);
            }
        }
        switch (assignedTaskType) {
            case TASK_COPY_TO_DIRECTORY:
                notifyMoveToDirectoryActionListeners(successfulImages, failedImages);
                break;

            case TASK_RESIZE_AND_COMPRESS_TO_RATIO:
                notifyResizeAndCompressActionListeners(successfulImages, failedImages);
                break;

            case TASK_JUST_RESIZE:
                break;

            case TASK_JUST_COMPRESS:
                break;
        }
    }

    private void notifyResizeAndCompressActionListeners(@NonNull List<File> successfulImages, @NonNull List<File> failedImages) {
        MainThreadMessageHandler mainThreadMessageHandler = MainThreadMessageHandler.getInstance();
        if (successfulImages.size() > 0) {
            ResizeStatusMessage resizeStatusMessage = new ResizeStatusMessage.ResizeMessageBuilder().setStatus(ProcessingStatus.PROCESSING_SUCCESS)
                    .setCallback(imageListResizeCallback)
                    .setSuccessfulImages(successfulImages)
                    .createResizeMessage();

            mainThreadMessageHandler.postBatchResizeMessage(resizeStatusMessage);
        }
        if (failedImages.size() > 0) {
            ResizeStatusMessage resizeStatusMessage = new ResizeStatusMessage.ResizeMessageBuilder().setStatus(ProcessingStatus.PROCESSING_FAILED)
                    .setCallback(imageListResizeCallback)
                    .setFailedImages(failedImages)
                    .createResizeMessage();

            mainThreadMessageHandler.postBatchResizeMessage(resizeStatusMessage);
        }
    }

    private void notifyMoveToDirectoryActionListeners(@NonNull List<File> successfulImages, @NonNull List<File> failedImages) {
        MainThreadMessageHandler mainThreadMessageHandler = MainThreadMessageHandler.getInstance();
        if (successfulImages.size() > 0) {
            CopyStatusMessage copyStatusMessage = new CopyStatusMessage.CopyMessageBuilder().setStatus(ProcessingStatus.PROCESSING_SUCCESS)
                    .setCallback(imageListCopyCallback)
                    .setSuccessfulImages(successfulImages)
                    .createCopyMessage();

            mainThreadMessageHandler.postBatchCopyMessage(copyStatusMessage);
        }
        if (failedImages.size() > 0) {
            CopyStatusMessage copyStatusMessage = new CopyStatusMessage.CopyMessageBuilder().setStatus(ProcessingStatus.PROCESSING_FAILED)
                    .setCallback(imageListCopyCallback)
                    .setFailedImages(failedImages)
                    .createCopyMessage();

            mainThreadMessageHandler.postBatchCopyMessage(copyStatusMessage);
        }
    }

    /**
     * Blocks the listener main callable thread using invokeAll()and waits until
     * all submitted task in the pool are done,
     * setting the TaskManager isBusy boolean to false upon completion
     */
    private void awaitResults() {
        ExecutorService workerThreadsExecutorService = ThreadPoolCreator.createWorkerExecutorService();
        try {
            List<Future<Pair<File, Boolean>>> futures = workerThreadsExecutorService.invokeAll(activelyRunningTasks);
            completedTasksResults = new ArrayList<>();
            for (Future<Pair<File, Boolean>> future : futures) {
                try {
                    completedTasksResults.add(future.get());
                } catch (CancellationException | ExecutionException exception) {
                    exception.printStackTrace();
                } catch (InterruptedException e) {
                    Thread.currentThread()
                            .interrupt();
                    e.printStackTrace();
                }
            }
            workerThreadsExecutorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TaskManager.isMainTaskThreadBusy.set(false);
    }
}
