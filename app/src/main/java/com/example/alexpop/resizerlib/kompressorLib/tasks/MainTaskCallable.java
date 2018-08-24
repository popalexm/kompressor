package com.example.alexpop.resizerlib.kompressorLib.tasks;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemResizeCallback;
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
    private final KompressorParameters parameters;
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
    private EntireBatchResizeCallback entireBatchResizeCallback;
    @Nullable
    private EntireBatchCopyCallback entireBatchCopyCallback;
    @Nullable
    private IndividualItemCopyCallback individualItemCopyCallback;
    @Nullable
    private IndividualItemResizeCallback individualItemResizeCallback;

    public MainTaskCallable(@NonNull KompressorParameters parameters) {
        this.parameters = parameters;
        mainThreadMessageHandler = MainThreadMessageHandler.getInstance();
    }

    public void setEntireBatchResizeCallback(@NonNull EntireBatchResizeCallback entireBatchResizeCallback) {
        this.entireBatchResizeCallback = entireBatchResizeCallback;
    }

    public void setEntireBatchCopyCallback(@NonNull EntireBatchCopyCallback entireBatchCopyCallback) {
        this.entireBatchCopyCallback = entireBatchCopyCallback;
    }

    public void setIndividualItemCopyCallback(@NonNull IndividualItemCopyCallback individualItemCopyCallback) {
        this.individualItemCopyCallback = individualItemCopyCallback;
    }

    public void setIndividualItemResizeCallback(@NonNull IndividualItemResizeCallback individualItemResizeCallback) {
        this.individualItemResizeCallback = individualItemResizeCallback;
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
        CopyStatusMessage copyStatusMessage = new CopyStatusMessage.CopyMessageBuilder().setCallback(entireBatchCopyCallback)
                .setStatus(ProcessingStatus.PROCESSING_STARTED)
                .createCopyMessage();
        mainThreadMessageHandler.postBatchCopyMessage(copyStatusMessage);

        List<File> imageFiles = parameters.getImageFiles();
        File toCopyDestinationDirectory = parameters.getToCopyDestinationDirectory();

        if (toCopyDestinationDirectory != null) {
            activelyRunningTasks = new ArrayList<>();

            for (int i = 0; i < imageFiles.size(); i++) {
                ImageCopyWorkerTask imageCopyWorkerTask = new ImageCopyWorkerTask(imageFiles.get(i), toCopyDestinationDirectory, individualItemCopyCallback);
                activelyRunningTasks.add(imageCopyWorkerTask);
            }
            awaitResults();
        }
    }

    private void executeImageResizeTaskQueue() {
        ResizeStatusMessage resizeStatusMessage = new ResizeStatusMessage.ResizeMessageBuilder().setCallback(entireBatchResizeCallback)
                .setStatus(ProcessingStatus.PROCESSING_STARTED)
                .createResizeMessage();
        mainThreadMessageHandler.postBatchResizeMessage(resizeStatusMessage);

        List<File> imagesFiles = parameters.getImageFiles();
        int maxResizeWidth = parameters.getMaximumResizeWidth();
        int compressionRatio = parameters.getCompressionRatio();
        if (maxResizeWidth > 0 && compressionRatio > 0) {
            activelyRunningTasks = new ArrayList<>();
            for (int i = 0; i < imagesFiles.size(); i++) {
                String imgPath = imagesFiles.get(i)
                        .getPath();
                ImageResizeWorkerTask imageResizeWorkerTask = new ImageResizeWorkerTask(imgPath, maxResizeWidth, compressionRatio,
                        individualItemResizeCallback);
                activelyRunningTasks.add(imageResizeWorkerTask);
            }
            awaitResults();
        }
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
                    .setCallback(entireBatchResizeCallback)
                    .setSuccessfulImages(successfulImages)
                    .createResizeMessage();

            mainThreadMessageHandler.postBatchResizeMessage(resizeStatusMessage);
        }
        if (failedImages.size() > 0) {
            ResizeStatusMessage resizeStatusMessage = new ResizeStatusMessage.ResizeMessageBuilder().setStatus(ProcessingStatus.PROCESSING_FAILED)
                    .setCallback(entireBatchResizeCallback)
                    .setFailedImages(failedImages)
                    .createResizeMessage();

            mainThreadMessageHandler.postBatchResizeMessage(resizeStatusMessage);
        }
    }

    private void notifyMoveToDirectoryActionListeners(@NonNull List<File> successfulImages, @NonNull List<File> failedImages) {
        MainThreadMessageHandler mainThreadMessageHandler = MainThreadMessageHandler.getInstance();
        if (successfulImages.size() > 0) {
            CopyStatusMessage copyStatusMessage = new CopyStatusMessage.CopyMessageBuilder().setStatus(ProcessingStatus.PROCESSING_SUCCESS)
                    .setCallback(entireBatchCopyCallback)
                    .setSuccessfulImages(successfulImages)
                    .createCopyMessage();

            mainThreadMessageHandler.postBatchCopyMessage(copyStatusMessage);
        }
        if (failedImages.size() > 0) {
            CopyStatusMessage copyStatusMessage = new CopyStatusMessage.CopyMessageBuilder().setStatus(ProcessingStatus.PROCESSING_FAILED)
                    .setCallback(entireBatchCopyCallback)
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
