package com.example.alexpop.resizerlib.library.taskmanager;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.alexpop.resizerlib.library.callables.ImageCopyWorkerTask;
import com.example.alexpop.resizerlib.library.callables.ImageResizeWorkerTask;
import com.example.alexpop.resizerlib.library.callables.WorkerTaskCallable;
import com.example.alexpop.resizerlib.library.callbacks.ImageListCopyCallback;
import com.example.alexpop.resizerlib.library.callbacks.ImageListResizeCallback;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageCopyCallback;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageResizeCallback;
import com.example.alexpop.resizerlib.library.definitions.Message;
import com.example.alexpop.resizerlib.library.definitions.TaskType;
import com.example.alexpop.resizerlib.library.handlers.ImageListMessageHandler;
import com.example.alexpop.resizerlib.library.threadmanager.ThreadPoolManager;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


public class MainListenerTaskCallable implements Callable<List<File>> {

    private String TAG = MainListenerTaskCallable.class.getSimpleName();

    private int mCompressionRatio;
    private int mMaximumResizeWidth;

    private List<File> mQueueImageFiles;
    private ExecutorService mExecutorService;

    private List<WorkerTaskCallable> mRunningTasks;
    private List<LinkedHashMap<File, Boolean>> mRunningTaskResults;

    private ImageListResizeCallback mImageListResizeCallback;
    private ImageListCopyCallback mImageListCopyCallback;
    private SingleImageCopyCallback mSingleImageCopyCallback;
    private SingleImageResizeCallback mSingleImageResizeCallback;

    private TaskType mAssignedTaskType;
    private File mCopyDestinationDirectory;

    MainListenerTaskCallable(@NonNull  List<File> imgsToProcess,
                             @NonNull   TaskType assignedTaskType){
        this.mQueueImageFiles = imgsToProcess;
        this.mAssignedTaskType = assignedTaskType;
    }

    public void setmImageListResizeCallback(@NonNull ImageListResizeCallback imageListResizeCallback) {
        this.mImageListResizeCallback = imageListResizeCallback;
    }

    public void setmImageListCopyCallback(@NonNull ImageListCopyCallback imageListCopyCallback) {
        this.mImageListCopyCallback = imageListCopyCallback;
    }

    public void setmSingleImageCopyCallback(@NonNull SingleImageCopyCallback singleImageCopyCallback) {
        this.mSingleImageCopyCallback = singleImageCopyCallback;
    }

    public void setmSingleImageResizeCallback(@NonNull SingleImageResizeCallback singleImageResizeCallback) {
        this.mSingleImageResizeCallback = singleImageResizeCallback;
    }

    public void setmCompressionRatio(int compressionRatio) {
        this.mCompressionRatio = compressionRatio;
    }

    public void setmMaximumResizeWidth(int maximumResizeWidth) {
        this.mMaximumResizeWidth = maximumResizeWidth;
    }

    public void setmCopyDestinationDirectory(@NonNull File destinationDirectory) {
        this.mCopyDestinationDirectory = destinationDirectory;
    }

    @Override
    public List<File> call() {
        switch (mAssignedTaskType) {
          case  TASK_RESIZE_AND_COMPRESS_TO_RATIO:
                executeImageResizeTaskQueue();
                break;

           case TASK_MOVE_TO_DIRECTORY:
                executeImageCopyTaskQueue();
                break;
        }
        sortResults(mRunningTaskResults , mAssignedTaskType);
        return null;
    }

    private void executeImageCopyTaskQueue() {
        Log.d(TAG , "Preparing worker pool on thread + " + Thread.currentThread().getName());
        ImageListMessageHandler imageListMessageHandler = ImageListMessageHandler.getInstance();
        imageListMessageHandler.postCopyMessage(Message.PROCESSING_STARTED, mImageListCopyCallback, null, null);

        mExecutorService = ThreadPoolManager.getInstance().createWorkerExecutorService();
        mRunningTasks = new ArrayList<>();
        for (int i = 0; i < mQueueImageFiles.size() ; i++  ) {
            ImageCopyWorkerTask imageCopyWorkerTask = new ImageCopyWorkerTask(mQueueImageFiles.get(i), mCopyDestinationDirectory, mSingleImageCopyCallback);
            mRunningTasks.add(imageCopyWorkerTask);
            mExecutorService.submit(imageCopyWorkerTask);
        }

        mRunningTaskResults = returnWorkerTaskResults(mRunningTasks);
        mExecutorService.shutdown();
    }

    private void executeImageResizeTaskQueue() {
        Log.d(TAG , "Preparing worker pool on thread + " + Thread.currentThread().getName());
        ImageListMessageHandler imageListMessageHandler = ImageListMessageHandler.getInstance();
        imageListMessageHandler.postResizeMessage(Message.PROCESSING_STARTED , mImageListResizeCallback, null, null);

        mExecutorService= ThreadPoolManager.getInstance().createWorkerExecutorService();
        mRunningTasks= new ArrayList<>();
        for (int i = 0; i < mQueueImageFiles.size() ; i++  ) {
            String imgPath = mQueueImageFiles.get(i).getPath();
            ImageResizeWorkerTask imageResizeWorkerTask = new ImageResizeWorkerTask(imgPath , mMaximumResizeWidth, mCompressionRatio, mSingleImageResizeCallback);
            mRunningTasks.add(imageResizeWorkerTask);
            mExecutorService.submit(imageResizeWorkerTask);
        }

        mRunningTaskResults = returnWorkerTaskResults(mRunningTasks);
        mExecutorService.shutdown();
    }

    private void sortResults(@NonNull List<LinkedHashMap<File, Boolean>> fileProcessingResults, @NonNull TaskType assignedTaskType) {
        ImageListMessageHandler imageListMessageHandler = ImageListMessageHandler.getInstance();
        List <File > success = new ArrayList<>();
        List <File> failed = new ArrayList<>();
        for (LinkedHashMap<File, Boolean> fileProcessingResult : fileProcessingResults) {
            for (File imageFile : fileProcessingResult.keySet()) {
                Boolean operationStatus = fileProcessingResult.get(imageFile);
                if (!operationStatus) {
                    failed.add(imageFile);
                } else {
                    success.add(imageFile);
                }
            }
        }

        if (assignedTaskType == TaskType.TASK_MOVE_TO_DIRECTORY) {
            if (success.size() > 0) {
                imageListMessageHandler.postCopyMessage(Message.PROCESSING_SUCCESS, mImageListCopyCallback, success, null);
            }
            if (failed.size() > 0) {
                imageListMessageHandler.postCopyMessage(Message.PROCESSING_FAILED, mImageListCopyCallback, null, failed);
            }
        } else {
            if (success.size() > 0) {
                imageListMessageHandler.postResizeMessage(Message.PROCESSING_SUCCESS, mImageListResizeCallback, success, null);
            }
            if (failed.size() > 0) {
                imageListMessageHandler.postResizeMessage(Message.PROCESSING_FAILED, mImageListResizeCallback, null, failed);
            }
        }
    }

    private List<LinkedHashMap<File, Boolean>> returnWorkerTaskResults(@NonNull List<WorkerTaskCallable> workerTaskCallableList) {
        Log.d(TAG , "trying to call :invokeAll() and wait for worker pool responses on thread - " + Thread.currentThread().getName());
        List<LinkedHashMap<File, Boolean>> compressedImages = new ArrayList<>();
        try{
            List<Future<LinkedHashMap<File, Boolean>>> futures = mExecutorService.invokeAll(workerTaskCallableList);
            for(Future<LinkedHashMap<File, Boolean>> future : futures){
                try{
                    compressedImages.add(future.get());
                }
                catch (CancellationException | ExecutionException ce) {
                    ce.printStackTrace();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // ignore/reset
                }
            }
        } catch(Exception err){
            err.printStackTrace();
        }
        Log.d(TAG , " Worker thread pool returned - " + compressedImages.size() + " processed images");
        return compressedImages;
    }
}
