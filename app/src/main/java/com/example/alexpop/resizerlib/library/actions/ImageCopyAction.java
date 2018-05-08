package com.example.alexpop.resizerlib.library.actions;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static com.example.alexpop.resizerlib.library.misc.Misc.formatTimeHHmmSS;

public class ImageCopyAction {

    private String TAG = ImageResizeCompressAction.class.getSimpleName();

    public File copyFileToDirectory(@NonNull File sourceFile, @NonNull File destFile) throws IOException {
        Log.d(TAG , "Starting to copy file - " + sourceFile.getName() + " to directory - "+ destFile.getName() + " , on thread " + Thread.currentThread().getName() + " at time : " + formatTimeHHmmSS(System.currentTimeMillis()));

        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        if (destFile.isDirectory()) {
            destFile = new File(destFile.getPath()+ File.separator + sourceFile.getName());
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
        return destFile;
    }
}
