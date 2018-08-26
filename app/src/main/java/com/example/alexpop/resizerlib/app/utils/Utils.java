package com.example.alexpop.resizerlib.app.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;

import java.io.File;
import java.text.DecimalFormat;

public final class Utils {

    private static final String JPG = ".jpg";
    private static final String JPEG = ".jpeg";

    private Utils() {
    }

    @NonNull
    public static String getRealPathFromUri(@NonNull Uri uri) {
        String documentId = DocumentsContract.getDocumentId(uri);
        return documentId.split(":")[1];
    }

    /**
     * Provides the directory where the photos will be copied , and later on compressed
     */
    @NonNull
    public static File getCopyToMediaDirectory(@NonNull Context context) {
        return new File(Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/Files");
    }

    @NonNull
    public static String convertToMbKbGb(long size) {
        DecimalFormat df = new DecimalFormat("0.00");
        float sizeKb = 1024.0f;
        float sizeMo = sizeKb * sizeKb;
        float sizeGo = sizeMo * sizeKb;
        float sizeTerra = sizeGo * sizeKb;

        if (size < sizeMo) {
            return df.format(size / sizeKb) + " Kb";
        } else if (size < sizeGo) {
            return df.format(size / sizeMo) + " Mb";
        } else if (size < sizeTerra) {
            return df.format(size / sizeGo) + " Gb";
        }

        return "";
    }

    public static boolean isFilePictureFormat(@NonNull File file) {
        return file.getName()
                .toLowerCase()
                .endsWith(Utils.JPEG) || file.getName()
                .toLowerCase()
                .endsWith(Utils.JPG);
    }
}
