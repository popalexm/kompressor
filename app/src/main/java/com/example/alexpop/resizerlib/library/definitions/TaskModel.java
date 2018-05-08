package com.example.alexpop.resizerlib.library.definitions;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

public class TaskModel {

    private TaskType mTaskType;
    private List<File> mQueueImageFiles;
    private TaskDetails mTaskDetails;

    public TaskModel(@NonNull TaskType mTaskType, List<File> mQueueImageFiles, TaskDetails mTaskDetails) {
        this.mTaskType = mTaskType;
        this.mQueueImageFiles = mQueueImageFiles;
        this.mTaskDetails = mTaskDetails;
    }

    public TaskType getmTaskType() {
        return mTaskType;
    }

    public void setmTaskType(TaskType mTaskType) {
        this.mTaskType = mTaskType;
    }

    public List<File> getmQueueImageFiles() {
        return mQueueImageFiles;
    }

    public void setmQueueImageFiles(List<File> mQueueImageFiles) {
        this.mQueueImageFiles = mQueueImageFiles;
    }

    public TaskDetails getmTaskDetails() {
        return mTaskDetails;
    }

    public void setmTaskDetails(TaskDetails mTaskDetails) {
        this.mTaskDetails = mTaskDetails;
    }

    @Override
    public String toString() {
        return "WorkerTaskCallable :-> " + mQueueImageFiles.size() + "files assigned - , type" + mTaskType;
    }
}
