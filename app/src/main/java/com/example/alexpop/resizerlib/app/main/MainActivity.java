package com.example.alexpop.resizerlib.app.main;

import com.example.alexpop.resizerlib.R;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 55;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestStoragePermissions();
        setupPhotoViewFragment();
    }

    private void setupPhotoViewFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new MainFragmentView(), MainFragmentView.TAG)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_compress_pictures:
                sendCompressPicturesRequest();
                break;

            case R.id.action_clear_pictures:
                deleteAllPhotos();
                break;

            case R.id.action_refresh_pictures:
                refreshImportedPhotos();
                break;

            case R.id.action_set_attributes:
                openCompressionSettingsDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.WRITE_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    private void sendCompressPicturesRequest() {
        MainFragmentView fragment = (MainFragmentView) getSupportFragmentManager().findFragmentByTag(MainFragmentView.TAG);
        if (fragment != null) {
            fragment.startPhotoCompression();
        }
    }

    private void openCompressionSettingsDialog() {
        MainFragmentView fragment = (MainFragmentView) getSupportFragmentManager().findFragmentByTag(MainFragmentView.TAG);
        if (fragment != null) {
            fragment.openCompressionSettings();
        }
    }

    private void deleteAllPhotos() {
        MainFragmentView fragment = (MainFragmentView) getSupportFragmentManager().findFragmentByTag(MainFragmentView.TAG);
        if (fragment != null) {
            fragment.deleteImportedPhotos();
        }
    }

    private void refreshImportedPhotos() {
        MainFragmentView fragment = (MainFragmentView) getSupportFragmentManager().findFragmentByTag(MainFragmentView.TAG);
        if (fragment != null) {
            fragment.refreshImportedPhotos();
        }
    }
}
