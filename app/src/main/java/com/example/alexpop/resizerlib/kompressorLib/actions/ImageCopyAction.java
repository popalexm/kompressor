package com.example.alexpop.resizerlib.kompressorLib.actions;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public final class ImageCopyAction {

    private String TAG = ImageResizeCompressAction.class.getSimpleName();

    @NonNull
    public static File copyFileToDirectory(@NonNull File sourceFile, @NonNull File destinationFile) throws IOException {
        if (!destinationFile.getParentFile()
                .exists()) {
            destinationFile.getParentFile()
                    .mkdirs();
        }
        if (!destinationFile.exists()) {
            destinationFile.createNewFile();
        }
        if (destinationFile.isDirectory()) {
            destinationFile = new File(destinationFile.getPath() + File.separator + sourceFile.getName());
        }

        FileChannel sourceFileChannel = null;
        FileChannel destinationFileChannel = null;
        try {
            sourceFileChannel = new FileInputStream(sourceFile).getChannel();
            destinationFileChannel = new FileOutputStream(destinationFile).getChannel();
            destinationFileChannel.transferFrom(sourceFileChannel, 0, sourceFileChannel.size());
        } finally {
            if (sourceFileChannel != null) {
                sourceFileChannel.close();
            }
            if (destinationFileChannel != null) {
                destinationFileChannel.close();
            }
        }
        return destinationFile;
    }
}
