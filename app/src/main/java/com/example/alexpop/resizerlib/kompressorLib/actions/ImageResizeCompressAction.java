package com.example.alexpop.resizerlib.kompressorLib.actions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class ImageResizeCompressAction {

    private final String TAG = ImageResizeCompressAction.class.getSimpleName();

    public File resizeAndCompressAtPath(@NonNull String imagePath , int maxSize, int compressionRatio){
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
