package com.example.alexpop.resizerlib.app.main;

import com.example.alexpop.resizerlib.app.base.BaseContract;
import com.example.alexpop.resizerlib.app.model.Photo;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

public class MainFragmentContract extends BaseContract {

    public interface View extends BaseContract.View {

        void openFileExplorer();

        void showAllPhotos(@NonNull List<Photo> photos);
    }

    public interface Presenter extends BaseContract.Presenter {

        void onPicturesSelected(@NonNull List<File> photos);

        void onOpenSelectPictures();

        void onSettingsClicked();

        void onCompressClicked();

        void onRefreshClicked();
    }
}
