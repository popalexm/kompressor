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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


public class MainListenerTaskCallable implements Callable<List<File>> {

    private String TAG = MainListenerTaskCallable.class.getSimpleName();

    /**
      Assigned files to the processing queue
     */
    private List<File> mImageFiles;

    /**
      Executor service with a pool of threads for the worker callables
     */
    private ExecutorService mWorkerThreadsExecutorService;

    /** List of currently  assigned callables ,
     * on the worker thread pool
     */
    private List<WorkerTaskCallable> mRunningTasks;

    /** List of results from the operations , with a boolean status in the hashMap,
     * true for success, false for failed
     */
    private List<HashMap<File, Boolean>> mRunningTaskResults;

    /** Callbacks for the operations success / failure notification
     */
    private ImageListResizeCallback mImageListResizeCallback;
    private ImageListCopyCallback mImageListCopyCallback;
    private SingleImageCopyCallback mSingleImageCopyCallback;
    private SingleImageResizeCallback mSingleImageResizeCallback;

    /** Message handler, used to call back the UI thread with the results of the operation
     */
    private ImageListMessageHandler mImageListMessageHandler;

    /** Task parameters
     */
    private TaskType mAssignedTaskType;
    private File mCopyDestinationDirectory;
    private int mCompressionRatio;
    private int mMaximumResizeWidth;

    MainListenerTaskCallable(@NonNull List<File> imgsToProcess,
                             @NonNull TaskType assignedTaskType){
        this.mImageFiles = imgsToProcess;
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
        mImageListMessageHandler = ImageListMessageHandler.getInstance();
        mWorkerThreadsExecutorService = ThreadPoolManager.getInstance().createWorkerExecutorService();

        switch (mAssignedTaskType) {
          case  TASK_RESIZE_AND_COMPRESS_TO_RATIO:
                executeImageResizeTaskQueue();
                break;

           case TASK_MOVE_TO_DIRECTORY:
                executeImageCopyTaskQueue();
                break;

            case TASK_JUST_RESIZE:
                // TODO implement
                break;

            case TASK_JUST_COMPRESS:
                // TODO implement
                break;
        }
        sortResults(mRunningTaskResults , mAssignedTaskType);
        return null;
    }

    private void executeImageCopyTaskQueue() {
        mImageListMessageHandler.postCopyMessage(Message.PROCESSING_STARTED, mImageListCopyCallback, null, null);
        mRunningTasks = new ArrayList<>();
        for (int i = 0; i < mImageFiles.size() ; i++  ) {
            Log.d(TAG , "Submitting copy worker task to the thread pool");
            ImageCopyWorkerTask imageCopyWorkerTask = new ImageCopyWorkerTask(mImageFiles.get(i), mCopyDestinationDirectory, mSingleImageCopyCallback);
            mRunningTasks.add(imageCopyWorkerTask);
        }
        awaitResults();
    }

    private void executeImageResizeTaskQueue() {
        mImageListMessageHandler.postResizeMessage(Message.PROCESSING_STARTED , mImageListResizeCallback, null, null);
        mRunningTasks = new ArrayList<>();
        for (int i = 0; i < mImageFiles.size() ; i++  ) {
            Log.d(TAG , "Submitting resize worker task to the thread pool");
            String imgPath = mImageFiles.get(i).getPath();
            ImageResizeWorkerTask imageResizeWorkerTask = new ImageResizeWorkerTask(imgPath , mMaximumResizeWidth, mCompressionRatio, mSingleImageResizeCallback);
            mRunningTasks.add(imageResizeWorkerTask);
        }
        awaitResults();
    }

    /** Sorts the results of the resize or copy operations,
     *  and posts messages back to the main thread with the number of successfully copied / resized files,
     *  or the failed ones
     */
    private void sortResults(@NonNull List<HashMap<File, Boolean>> fileProcessingResults, @NonNull TaskType assignedTaskType) {
        ImageListMessageHandler imageListMessageHandler = ImageListMessageHandler.getInstance();
        List <File > success = new ArrayList<>();
        List <File> failed = new ArrayList<>();
        for (HashMap<File, Boolean> fileProcessingResult : fileProcessingResults) {
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
    /** Blocks the listener main callable thread using invokeAll()and waits until
     *  all submitted task in the pool are done,
     *  setting the TaskManager isBusy boolean to false upon completion
     */
    private void awaitResults() {
        Log.d(TAG , "trying to call :invokeAll() and awaiting for worker pool responses on thread - " + Thread.currentThread().getName());
        try{
            List<Future<HashMap<File, Boolean>>> futures = mWorkerThreadsExecutorService.invokeAll(mRunningTasks);
            mRunningTaskResults = new ArrayList<>();
            for(Future<HashMap<File, Boolean>> future : futures){
                try{
                    mRunningTaskResults.add(future.get());
                }
                catch (CancellationException | ExecutionException ce) {
                    ce.printStackTrace();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            mWorkerThreadsExecutorService.shutdown();
        } catch(Exception err){
            err.printStackTrace();
        }
        Log.d(TAG , " Worker thread pool returned - " + mRunningTaskResults.size() + " done futures");
        TaskManager.isMainTaskThreadBusy.set(false);
    }
}
