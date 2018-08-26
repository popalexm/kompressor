package com.example.alexpop.resizerlib.app.main;

import com.example.alexpop.resizerlib.R;
import com.example.alexpop.resizerlib.app.injection.Injection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestStoragePermissions();
        setupPhotoViewFragment();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            showSnackBarMessage(Injection.provideGlobalContext()
                    .getString(R.string.message_read_write_storage_permissions_denied));
        }
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

    private void setupPhotoViewFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new MainFragmentView(), MainFragmentView.TAG)
                .commit();
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.WRITE_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    private void showSnackBarMessage(@NonNull String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_coordinator_layout), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    /**
     * Delegate Toolbar button functionality to the the MainFragmentView and presenter
     */
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
