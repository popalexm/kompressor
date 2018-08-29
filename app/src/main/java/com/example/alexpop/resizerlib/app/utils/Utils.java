package com.example.alexpop.resizerlib.app.utils;

import com.example.alexpop.resizerlib.R;
import com.example.alexpop.resizerlib.app.injection.Injection;

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
    private static final String ANDROID_DATA_FOLDER = "/Android/data/";
    private static final String FILES_SUBFOLDER = "/Files";

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
        return new File(Environment.getExternalStorageDirectory() + Utils.ANDROID_DATA_FOLDER + context.getPackageName() + Utils.FILES_SUBFOLDER);
    }

    @NonNull
    public static String formatDiskSizeToValue(long size) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        final float sizeKb = 1024.0f;
        final float sizeMo = sizeKb * sizeKb;
        final float sizeGo = sizeMo * sizeKb;
        final float sizeTerra = sizeGo * sizeKb;
        Context context = Injection.provideGlobalContext();
        String formattedSize = "";
        if (size < sizeMo) {
            formattedSize = decimalFormat.format(size / sizeKb) + " " + context.getString(R.string.prefix_kb);
        } else if (size < sizeGo) {
            formattedSize = decimalFormat.format(size / sizeMo) + " " + context.getString(R.string.prefix_mb);
        } else if (size < sizeTerra) {
            formattedSize = decimalFormat.format(size / sizeGo) + " " + context.getString(R.string.prefix_gb);
        }
        return formattedSize;
    }

    public static boolean isFilePictureFormat(@NonNull File file) {
        return file.getName()
                .toLowerCase()
                .endsWith(Utils.JPEG) || file.getName()
                .toLowerCase()
                .endsWith(Utils.JPG);
    }
}
