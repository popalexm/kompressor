package com.example.alexpop.resizerlib.kompressorLib.tasks;

import com.example.alexpop.resizerlib.kompressorLib.actions.ImageCopyAction;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.IndividualItemCopyListener;
import com.example.alexpop.resizerlib.kompressorLib.handlers.MainThreadMessageHandler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import java.io.File;
import java.io.IOException;

public class ImageCopyWorkerTask extends BaseWorkerTaskCallable {

    private final String TAG = ImageCopyWorkerTask.class.getSimpleName();
    @NonNull
    private final File copyToDirectory;
    @NonNull
    private final File toCopyFile;
    @Nullable
    private final IndividualItemCopyListener copyStatusCallback;

    ImageCopyWorkerTask(@NonNull File copyToDirectory, @NonNull File toCopyFile, @Nullable IndividualItemCopyListener copyStatusCallback) {
        this.copyToDirectory = copyToDirectory;
        this.toCopyFile = toCopyFile;
        this.copyStatusCallback = copyStatusCallback;
    }

    @Override
    public Pair<File, Boolean> call() {
        /* The mCopyStatus HashMap contains the destination file which has been successfully copied,
           with a boolean value of true or,in case of an execution error, the file which failed to copy,
           and the boolean status false assigned to it
        */
        MainThreadMessageHandler singleImageMessageHandler = MainThreadMessageHandler.getInstance();
        try {
            File resultFile = ImageCopyAction.copyFileToDirectory(copyToDirectory, toCopyFile);
            singleImageMessageHandler.postImageCopySuccessMessage(copyStatusCallback, resultFile);
            return Pair.create(resultFile, true);
        } catch (IOException e) {
            e.printStackTrace();
            singleImageMessageHandler.postImageCopyFailedMessage(copyStatusCallback, copyToDirectory);
            return Pair.create(toCopyFile, false);
        }
    }
}
