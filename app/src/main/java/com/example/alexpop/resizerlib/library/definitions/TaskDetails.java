package com.example.alexpop.resizerlib.library.definitions;

import java.io.File;

public class TaskDetails {

    private int mMaxSize;
    private int mCompressionRatio;
    private File mDestinationPath;

    public int getmMaxSize() {
        return mMaxSize;
    }

    public void setmMaxSize(int mMaxSize) {
        this.mMaxSize = mMaxSize;
    }

    public int getmCompressionRatio() {
        return mCompressionRatio;
    }

    public void setmCompressionRatio(int mCompressionRatio) {
        this.mCompressionRatio = mCompressionRatio;
    }

    public File getmDestinationPath() {
        return mDestinationPath;
    }

    public void setmDestinationPath(File mDestinationPath) {
        this.mDestinationPath = mDestinationPath;
    }
}
