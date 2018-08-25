package com.example.alexpop.resizerlib.app.main;

import com.example.alexpop.resizerlib.R;
import com.example.alexpop.resizerlib.app.injection.Injection;
import com.example.alexpop.resizerlib.app.model.Photo;
import com.example.alexpop.resizerlib.app.useCases.DeleteAllCopiedPictures;
import com.example.alexpop.resizerlib.app.useCases.GetAvailableImagesUseCaseMaybe;
import com.example.alexpop.resizerlib.app.utils.GlobalConstants;
import com.example.alexpop.resizerlib.app.utils.Utils;
import com.example.alexpop.resizerlib.kompressorLib.Kompressor;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchResizeCallback;
import com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType;
import com.example.alexpop.resizerlib.kompressorLib.tasks.KompressorParameters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;

public class MainFragmentPresenter implements MainFragmentContract.Presenter, EntireBatchCopyCallback, EntireBatchResizeCallback {

    @NonNull
    private final MainFragmentContract.View view;
    private boolean isViewAttached;
    @NonNull
    private final Kompressor kompressor;
    @NonNull
    private final File mediaDirectory = Utils.getCopyToMediaDirectory(Injection.provideGlobalContext());

    MainFragmentPresenter(@NonNull MainFragmentContract.View view) {
        this.view = view;
        this.kompressor = Kompressor.get();
        this.kompressor.withBatchCopyCallbacks(this);
        this.kompressor.withBatchResizeCallbacks(this);
    }

    @Override
    public void onPicturesSelected(@NonNull List<File> photos) {
        File toCopyDestinationDirectory = Utils.getCopyToMediaDirectory(Injection.provideGlobalContext());
        KompressorParameters parameters = new KompressorParameters.MainTaskParametersBuilder().setImageFiles(photos)
                .setTaskType(TaskType.TASK_COPY_TO_DIRECTORY)
                .setToCopyDestinationDirectory(toCopyDestinationDirectory)
                .createMainTaskParameters();
        kompressor.startTask(parameters);
    }

    @Override
    public void onOpenSelectPictures() {
        if (isViewAttached) {
            view.openFileExplorer();
        }
    }

    @Override
    public void onSettingsClicked() {
        SharedPreferences preferences = Injection.provideGlobalContext()
                .getSharedPreferences(GlobalConstants.KOMPRESSOR_LIB_PREFERENCES, Context.MODE_PRIVATE);
        int compressRatio = preferences.getInt(GlobalConstants.COMPRESSION_RATIO_SHARED_PREFS, 0);
        int maxHeight = preferences.getInt(GlobalConstants.MAX_HEIGHT_RATIO_SHARED_PREFS, 0);
        if (isViewAttached) {
            view.showCompressionSettingsDialog(compressRatio, maxHeight);
        }
    }

    @Override
    public void onCompressClicked() {
        startCompressionOnFiles();
    }

    @Override
    public void onRefreshClicked() {
        getAvailablePhotosInInternalDirectory();
    }

    @Override
    public void onDeleteImportedPhotosClicked() {
        File mediaDirectory = Utils.getCopyToMediaDirectory(Injection.provideGlobalContext());
        new DeleteAllCopiedPictures(mediaDirectory).perform()
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        if (isViewAttached) {
                            view.showMessage(Injection.provideGlobalContext()
                                    .getString(R.string.message_deleted_all_files));
                        }
                        getAvailablePhotosInInternalDirectory();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onAttach() {
        isViewAttached = true;
        getAvailablePhotosInInternalDirectory();
    }

    @Override
    public void onDetach() {
        isViewAttached = false;
    }

    @Override
    public void onBatchCopyStartedListener() {
        if (isViewAttached) {
            String msg = Injection.provideGlobalContext()
                    .getString(R.string.message_started_to_copy_file);
            view.showMessage(msg);
        }
    }

    @Override
    public void onBatchCopySuccessListener(@NonNull List<File> files) {
        List<Photo> copiedPhotos = new ArrayList<>();
        for (File file : files) {
            String fileSize = Utils.convertToMbKbGb(file.length());
            Photo photo = new Photo(file, fileSize);
            copiedPhotos.add(photo);
        }
        if (isViewAttached) {
            view.showAllPhotos(copiedPhotos);
        }
    }

    @Override
    public void onBatchFailedListener(@NonNull List<File> files) {
        if (isViewAttached) {
            String msg = Injection.provideGlobalContext()
                    .getString(R.string.message_failed_to_copy_file);
            String endMsg = Injection.provideGlobalContext()
                    .getString(R.string.message_files);
            view.showMessage(msg + files.size() + endMsg);
        }
    }

    private void getAvailablePhotosInInternalDirectory() {
        new GetAvailableImagesUseCaseMaybe(mediaDirectory).perform()
                .subscribe(new MaybeObserver<List<Photo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<Photo> photos) {
                        if (photos != null) {
                            view.showAllPhotos(photos);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void startCompressionOnFiles() {
        new GetAvailableImagesUseCaseMaybe(mediaDirectory).perform()
                .subscribe(new MaybeObserver<List<Photo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<Photo> photos) {
                        if (photos != null && photos.size() > 0) {
                            List<File> photoFiles = new ArrayList<>();
                            for (Photo photo : photos) {
                                photoFiles.add(photo.getPhotoFile());
                            }
                            KompressorParameters parameters = new KompressorParameters.MainTaskParametersBuilder().setImageFiles(photoFiles)
                                    .setTaskType(TaskType.TASK_RESIZE_AND_COMPRESS_TO_RATIO)
                                    .setMaximumResizeWidth(750)
                                    .setCompressionRatio(80)
                                    .createMainTaskParameters();
                            kompressor.startTask(parameters);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void onBatchResizeStartedListener() {
        if (isViewAttached) {
            view.showMessage(Injection.provideGlobalContext()
                    .getString(R.string.message_starting_to_compress));
        }
    }

    @Override
    public void onBatchResizeSuccessListener(@NonNull List<File> files) {
        if (isViewAttached) {
            Context context = Injection.provideGlobalContext();
            String starMsg = context.getString(R.string.message_starting_to_compress);
            String endMsg = context.getString(R.string.message_files);
            view.showMessage(starMsg + files.size() + endMsg);
        }
        getAvailablePhotosInInternalDirectory();
    }

    @Override
    public void onBatchResizeFailedListener(@NonNull List<File> files) {
        if (isViewAttached) {
            view.showMessage("Failed to resize " + files.size() + " files!");
        }
    }
}
