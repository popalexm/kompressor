package com.example.alexpop.resizerlib.kompressorLib.handlers.models;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.ImageListCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.definitions.ProcessingStatus;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

public final class CopyStatusMessage {

    @NonNull
    private final ProcessingStatus status;
    @NonNull
    private final ImageListCopyCallback callback;
    @NonNull
    private final List<File> successfulImages;
    @NonNull
    private final List<File> failedImages;

    private CopyStatusMessage(@NonNull ProcessingStatus status, @NonNull ImageListCopyCallback callback, @NonNull List<File> successfulImages,
            @NonNull List<File> failedImages) {
        this.status = status;
        this.callback = callback;
        this.successfulImages = successfulImages;
        this.failedImages = failedImages;
    }

    @NonNull
    public ProcessingStatus getStatus() {
        return status;
    }

    @NonNull
    public ImageListCopyCallback getCallback() {
        return callback;
    }

    @NonNull
    public List<File> getSuccessfulImages() {
        return successfulImages;
    }

    @NonNull
    public List<File> getFailedImages() {
        return failedImages;
    }

    public static class CopyMessageBuilder {

        private ProcessingStatus status;
        private ImageListCopyCallback callback;
        private List<File> successfulImages;
        private List<File> failedImages;

        public CopyMessageBuilder setStatus(ProcessingStatus status) {
            this.status = status;
            return this;
        }

        public CopyMessageBuilder setCallback(ImageListCopyCallback callback) {
            this.callback = callback;
            return this;
        }

        public CopyMessageBuilder setSuccessfulImages(List<File> successfulImages) {
            this.successfulImages = successfulImages;
            return this;
        }

        public CopyMessageBuilder setFailedImages(List<File> failedImages) {
            this.failedImages = failedImages;
            return this;
        }

        public CopyStatusMessage createCopyMessage() {
            return new CopyStatusMessage(status, callback, successfulImages, failedImages);
        }
    }
}
