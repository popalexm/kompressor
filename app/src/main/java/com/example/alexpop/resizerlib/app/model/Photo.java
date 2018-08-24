package com.example.alexpop.resizerlib.app.model;

import android.support.annotation.NonNull;

import java.io.File;

public class Photo {

    @NonNull
    private final File photoFile;
    @NonNull
    private final String photoSize;

    public Photo(@NonNull File photoFile, @NonNull String photoSize) {
        this.photoFile = photoFile;
        this.photoSize = photoSize;
    }

    @NonNull
    public File getPhotoFile() {
        return photoFile;
    }

    @NonNull
    public String getPhotoSize() {
        return photoSize;
    }
}
