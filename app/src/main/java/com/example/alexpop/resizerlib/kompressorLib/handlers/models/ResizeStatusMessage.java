package com.example.alexpop.resizerlib.kompressorLib.handlers.models;

import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchResizeListener;
import com.example.alexpop.resizerlib.kompressorLib.definitions.ProcessingStatus;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

public final class ResizeStatusMessage {

    @NonNull
    private final ProcessingStatus status;
    @NonNull
    private final EntireBatchResizeListener callback;
    @NonNull
    private final List<File> successfulImages;
    @NonNull
    private final List<File> failedImages;

    private ResizeStatusMessage(@NonNull ProcessingStatus status, @NonNull EntireBatchResizeListener callback, @NonNull List<File> successfulImages,
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
    public EntireBatchResizeListener getCallback() {
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

    public static class ResizeMessageBuilder {

        private ProcessingStatus status;
        private EntireBatchResizeListener callback;
        private List<File> successfulImages;
        private List<File> failedImages;

        public ResizeMessageBuilder setStatus(ProcessingStatus status) {
            this.status = status;
            return this;
        }

        public ResizeMessageBuilder setCallback(EntireBatchResizeListener callback) {
            this.callback = callback;
            return this;
        }

        public ResizeMessageBuilder setSuccessfulImages(List<File> successfulImages) {
            this.successfulImages = successfulImages;
            return this;
        }

        public ResizeMessageBuilder setFailedImages(List<File> failedImages) {
            this.failedImages = failedImages;
            return this;
        }

        public ResizeStatusMessage createResizeMessage() {
            return new ResizeStatusMessage(status, callback, successfulImages, failedImages);
        }
    }
}
