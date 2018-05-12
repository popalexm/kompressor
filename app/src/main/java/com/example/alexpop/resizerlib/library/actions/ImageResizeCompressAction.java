
package com.example.alexpop.resizerlib.library.actions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.alexpop.resizerlib.library.misc.Misc.formatTimeHHmmSS;

public class ImageResizeCompressAction {

    private String TAG = ImageResizeCompressAction.class.getSimpleName();

    public File resizeAndCompressAtPath(@NonNull String imagePath , int maxSize, int compressionRatio){
        Log.i(TAG , "Starting :imgCompress() on thread " + Thread.currentThread().getName() + " at time : " + formatTimeHHmmSS(System.currentTimeMillis()));
        File file = new File (imagePath);
        try {
            Bitmap bitmap;
            BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
            mBitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, mBitmapOptions);
            int originalWidth = mBitmapOptions.outWidth;
            int originalHeight = mBitmapOptions.outHeight;
            /* Calculating dimensions based on current image AR and maxWidth of the original image */
            int targetWidth;
            int targetHeight;
            if(originalWidth > originalHeight){
                targetWidth = maxSize;
                targetHeight = (originalHeight * maxSize) / originalWidth;
            } else {
                targetHeight = maxSize;
                targetWidth = (originalWidth * maxSize) / originalHeight;
            }
            mBitmapOptions.inJustDecodeBounds = false;
            mBitmapOptions.inScaled = true;
            mBitmapOptions.inSampleSize = calculateInSampleSize(originalHeight, originalWidth, targetWidth, targetHeight);

            // will load & resize the image to be 1/inSampleSize dimensions
            bitmap = BitmapFactory.decodeFile(imagePath, mBitmapOptions);
            Log.d(TAG , "Compressing bitmap to -> " + bitmap.getHeight() + " pixels height and " + bitmap.getWidth() + " pixels width");
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream(file));
        }
        catch (Throwable t) {
            Log.e(TAG, "Error while trying to resize the file." + t.toString ());
            t.printStackTrace ();
        }
        return file;
    }

    private int calculateInSampleSize(int originalHeight, int originalWidth, int targetWidth, int targetHeight) {
        int inSampleSize = 1;
        if (originalHeight > targetHeight || originalWidth > targetWidth) {
            // Calculate ratios of height and width to requested height and width
            int heightRatio = Math.round((float) originalHeight / (float) targetHeight);
            int widthRatio = Math.round((float) originalWidth / (float) targetWidth);
            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
