package com.example.alexpop.resizerlib.kompressorLib.tasks;

import com.example.alexpop.resizerlib.kompressorLib.actions.ImageCopyAction;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.SingleImageCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.handlers.MainThreadMessageHandler;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.io.File;
import java.io.IOException;

public class ImageCopyWorkerTask extends BaseWorkerTaskCallable {

    private final String TAG = ImageCopyWorkerTask.class.getSimpleName();
    @NonNull
    private final File copyToDirectory;
    @NonNull
    private final File copyFromDirectory;
    @NonNull
    private final SingleImageCopyCallback copyStatusCallback;

    ImageCopyWorkerTask(@NonNull File copyToDirectory, @NonNull File copyFromDirectory, @NonNull SingleImageCopyCallback copyStatusCallback) {
        this.copyToDirectory = copyToDirectory;
        this.copyFromDirectory = copyFromDirectory;
        this.copyStatusCallback = copyStatusCallback;
    }

    @Override
    public Pair<File, Boolean> call() {
        /* The mCopyStatus HashMap contains the destination file which has been successfully copied,
           with a boolean value of true or,in case of an execution error, the file which failed to copy,
           and the boolean status false assigned to it
        */
        MainThreadMessageHandler singleImageMessageHandler = MainThreadMessageHandler.getInstance();
        boolean copyStatus;
        try {
            File resultFile = ImageCopyAction.copyFileToDirectory(copyToDirectory, copyFromDirectory);
            singleImageMessageHandler.postImageCopySuccessMessage(copyStatusCallback, resultFile);
            copyStatus = true;
        } catch (IOException e) {
            e.printStackTrace();
            singleImageMessageHandler.postImageCopyFailedMessage(copyStatusCallback, copyToDirectory);
            copyStatus = false;
        }
        return Pair.create(copyFromDirectory, copyStatus);
    }
}
