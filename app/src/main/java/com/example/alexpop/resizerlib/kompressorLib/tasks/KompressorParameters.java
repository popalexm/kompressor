package com.example.alexpop.resizerlib.kompressorLib.tasks;

import com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.List;

public class KompressorParameters {

    @NonNull
    private final List<File> imageFiles;
    @NonNull
    private final TaskType taskType;
    @Nullable
    private final File toCopyDestinationDirectory;
    private final int compressionRatio;
    private final int maximumResizeWidth;

    KompressorParameters(@NonNull List<File> imageFiles, @NonNull TaskType taskType, @Nullable File toCopyDestinationDirectory, int compressionRatio,
            int maximumResizeWidth) {
        this.imageFiles = imageFiles;
        this.taskType = taskType;
        this.toCopyDestinationDirectory = toCopyDestinationDirectory;
        this.compressionRatio = compressionRatio;
        this.maximumResizeWidth = maximumResizeWidth;
    }

    @NonNull
    public List<File> getImageFiles() {
        return imageFiles;
    }

    @NonNull
    public TaskType getTaskType() {
        return taskType;
    }

    @Nullable
    public File getToCopyDestinationDirectory() {
        return toCopyDestinationDirectory;
    }

    public int getCompressionRatio() {
        return compressionRatio;
    }

    public int getMaximumResizeWidth() {
        return maximumResizeWidth;
    }

    public static class MainTaskParametersBuilder {

        private List<File> imageFiles;
        private TaskType taskType;
        private File toCopyDestinationDirectory;
        private int compressionRatio;
        private int maximumResizeWidth;

        public MainTaskParametersBuilder setImageFiles(List<File> imageFiles) {
            this.imageFiles = imageFiles;
            return this;
        }

        public MainTaskParametersBuilder setTaskType(TaskType taskType) {
            this.taskType = taskType;
            return this;
        }

        public MainTaskParametersBuilder setToCopyDestinationDirectory(File toCopyDestinationDirectory) {
            this.toCopyDestinationDirectory = toCopyDestinationDirectory;
            return this;
        }

        public MainTaskParametersBuilder setCompressionRatio(int compressionRatio) {
            this.compressionRatio = compressionRatio;
            return this;
        }

        public MainTaskParametersBuilder setMaximumResizeWidth(int maximumResizeWidth) {
            this.maximumResizeWidth = maximumResizeWidth;
            return this;
        }

        public KompressorParameters createMainTaskParameters() {
            return new KompressorParameters(imageFiles, taskType, toCopyDestinationDirectory, compressionRatio, maximumResizeWidth);
        }
    }
}
