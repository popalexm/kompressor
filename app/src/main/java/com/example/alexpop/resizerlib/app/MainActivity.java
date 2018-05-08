package com.example.alexpop.resizerlib.app;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.alexpop.resizerlib.R;
import com.example.alexpop.resizerlib.library.Kompressor;
import com.example.alexpop.resizerlib.library.callbacks.ImageListCopyCallback;
import com.example.alexpop.resizerlib.library.callbacks.ImageListResizeCallback;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageCopyCallback;
import com.example.alexpop.resizerlib.library.callbacks.SingleImageResizeCallback;
import com.example.alexpop.resizerlib.library.definitions.TaskType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
                                     implements View.OnClickListener,
                                                SettingsCallback,
                                                ImageListResizeCallback,
                                                ImageListCopyCallback,
                                                SingleImageCopyCallback,
                                                SingleImageResizeCallback{

    private String TAG = MainActivity.class.getSimpleName();

    private static final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 55;
    private static final int REQUEST_LOCAL_STORAGE = 3;

    @BindView(R.id.recycler_list_photos)
    RecyclerView mRecyclerViewPhotos;
    @BindView(R.id.fab_load_pictures)
    FloatingActionButton mLoadPictures;

    private PhotosRecyclerViewAdapter mPhotosToolsAdapter;
    private File mMediaStorageDir;
    private SharedPreferences mSharedPreferences;

    private int mCompressionRatio = 70;
    private int mMaxResizeHeight = 960;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mLoadPictures = findViewById(R.id.fab_load_pictures);
        mLoadPictures.setOnClickListener(this);
        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);

        initCopyToMediaDirectory();
        requestStoragePermissions();
        initRecyclerView();
    }

    private void openFileExplorer() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/jpeg");
        try {
            startActivityForResult(intent, REQUEST_LOCAL_STORAGE);
        } catch (ActivityNotFoundException e) {
            Log.d(TAG , "No activity found");
        }
    }

    private void initCopyToMediaDirectory(){
        mMediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getPackageName()
                + "/Files");
    }

    private void initRecyclerView() {
        List<File> photos = getAllImages(mMediaStorageDir);
        if (photos != null) {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
            mRecyclerViewPhotos.setLayoutManager(mLayoutManager);
            mPhotosToolsAdapter = new PhotosRecyclerViewAdapter(MainActivity.this , photos);
            mRecyclerViewPhotos.setAdapter(mPhotosToolsAdapter);
        } else {
            mPhotosToolsAdapter.notifyDataSetChanged();
            mRecyclerViewPhotos.invalidate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "Receiving permission results in MainActivity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_compress_pictures) {
            startCompression();
        }
        if (id == R.id.action_clear_pictures) {
            clearPictures(mMediaStorageDir);
            initRecyclerView();
        }
        if (id == R.id.action_refresh_pictures) {
            initRecyclerView();
        }
        if (id == R.id.action_set_attributes) {
            mMaxResizeHeight = mSharedPreferences.getInt( "mMaxResizeHeight", 0);
            mCompressionRatio = mSharedPreferences.getInt("mCompressionRatio", 0);
            new ResizeSettingsAlertDialog(MainActivity.this, this, mCompressionRatio , mMaxResizeHeight);
        }
        return super.onOptionsItemSelected(item);
    }

    private void startCompression() {
        ArrayList<File> assignedFiles = getAllImages(mMediaStorageDir);
            mMaxResizeHeight = mSharedPreferences.getInt( "mMaxResizeHeight", 0);
            mCompressionRatio = mSharedPreferences.getInt("mCompressionRatio", 0);
            if (assignedFiles != null && assignedFiles.size() != 0) {
                /**
                   Kompressor library initialisation with callbacks for single and multiple image resize results
                 */
                Kompressor kompressor = Kompressor.get();
                kompressor.loadResources(assignedFiles);
                kompressor.withResizeCallback(this);
                kompressor.withSingleImageResizeCallback(this);
                kompressor.withCompressionRatio(mCompressionRatio);
                kompressor.withMaxSize(mMaxResizeHeight);
                kompressor.startTask(TaskType.TASK_RESIZE_AND_COMPRESS_TO_RATIO);
            } else {
                Toast.makeText(MainActivity.this , "No pictures found to compress !" , Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCAL_STORAGE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        int currentItem = 0;
                        ArrayList<File> pictureUris = new ArrayList<>();
                        while (currentItem < count) {
                            Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                            String currentPath = getPathFromUri(imageUri);
                            String uri = "file://" + currentPath;
                            pictureUris.add(new File (Uri.parse(uri).getPath()));
                            currentItem = currentItem + 1;
                        }

                        Kompressor kompressor = Kompressor.get();
                        kompressor.loadResources(pictureUris);
                        kompressor.toDestinationPath(mMediaStorageDir);
                        kompressor.withCopyCallback(this);
                        kompressor.withSingleImageCopyCallback(this);
                        kompressor.startTask(TaskType.TASK_MOVE_TO_DIRECTORY);
                    }
                } else {
                Toast.makeText(MainActivity.this,   "Please select multiple images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.fab_load_pictures:
                openFileExplorer();
                break;
        }
    }

    public ArrayList<File> getAllImages(File directory){
        ArrayList<File> imgList = new ArrayList<>();
        File[] f = directory.listFiles();
        for (File file : f) {
            if (file != null)
                if (file.getName().toLowerCase().endsWith(".jpeg") || file.getName().toLowerCase().endsWith(".jpg")) {
                imgList.add(file);
            }
        }
        if (imgList.size() > 0) {
            Log.d(TAG, "getAllImages() returning " + imgList.size() + " files");
            return imgList;
        } else
            Log.d(TAG , "No files found, returning an empty array");
            return imgList;
    }

    private void clearPictures(File directory) {
        File[] f = directory.listFiles();
        for (File file : f) {
            if (file != null) {
                if (file.getName().toLowerCase().endsWith(".jpeg") || file.getName().toLowerCase().endsWith(".jpg")) {
                    file.delete();
                }
            }
        }
    }

    public  String getPathFromUri( Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = { MediaStore.Images.Media.DATA };
        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onImageListResizeStartedListener() {
        Toast.makeText(MainActivity.this,   " Started to compress the images images have been compressed ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onImageListResizeSuccessListener(@NonNull List<File> resizedFiles) {
        Toast.makeText(MainActivity.this ,  resizedFiles.size() + " images have been compressed ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onImageListResizeFailedListener(@NonNull List<File> failedToResizeFiles) {
        Toast.makeText(MainActivity.this ,  "Processing of " + failedToResizeFiles.size() + " images has failed ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onImageListCopyStartedListener() {
        Toast.makeText(MainActivity.this , "Started to copy images !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onImageListCopySuccessListener(@NonNull List<File> copiedSuccessfully) {
        Log.d(TAG , "Copied " + copiedSuccessfully.size() + " images successfully");
        if (copiedSuccessfully.size() > 0) {
            Toast.makeText(MainActivity.this , "Copied " + copiedSuccessfully.size() + " images to the destination directory !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onImageListFailedListener(@NonNull List<File> failedToCopy) {
        Log.d(TAG , "Failed to copy " + failedToCopy.size() + " images");
        if (failedToCopy.size() > 0) {
            Toast.makeText(MainActivity.this , "Failed to copy " + failedToCopy.size() + " images !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSingleImageCopySuccess(@NonNull File copiedFile) {
        mPhotosToolsAdapter.addPhotoToAdapter(copiedFile);
        mPhotosToolsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSingleImageCopyFailed(@NonNull File failedToCopyFile) {
        mPhotosToolsAdapter.notifyDataSetChanged();
        Log.d(TAG , "Returned single image copy failed for " + failedToCopyFile.getName() );
    }

    @Override
    public void onSingleImageResizeSuccess(@NonNull File resizedImage) {
        mPhotosToolsAdapter.notifyDataSetChanged();
        Log.d(TAG , " Returned resize success for " + resizedImage.getName());
    }

    @Override
    public void onSingleImageResizeFailed(@NonNull File failedToResizeImage) {
        mPhotosToolsAdapter.notifyDataSetChanged();
        Log.e(TAG , "Failed to resize image" + failedToResizeImage.getName() );
    }


    @Override
    public void onButtonOkSelected(int compressionRatio, int maxHeight) {
        mMaxResizeHeight = maxHeight;
        mCompressionRatio = compressionRatio;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("mMaxResizeHeight", mMaxResizeHeight);
        editor.putInt("mCompressionRatio", mCompressionRatio);
        editor.apply();
    }

    @Override
    public void onButtonNegativeSelected() { }
}
