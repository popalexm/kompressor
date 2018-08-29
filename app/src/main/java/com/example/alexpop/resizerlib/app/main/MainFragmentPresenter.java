package com.example.alexpop.resizerlib.app.main;

import com.example.alexpop.resizerlib.R;
import com.example.alexpop.resizerlib.app.injection.Injection;
import com.example.alexpop.resizerlib.app.model.Photo;
import com.example.alexpop.resizerlib.app.useCases.DeleteAllCopiedPicturesUseCase;
import com.example.alexpop.resizerlib.app.useCases.GetAvailableImagesUseCase;
import com.example.alexpop.resizerlib.app.utils.GlobalConstants;
import com.example.alexpop.resizerlib.app.utils.Utils;
import com.example.alexpop.resizerlib.kompressorLib.Kompressor;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchCopySuccessListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchResizeListener;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.StartingAssignedTaskListener;
import com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType;
import com.example.alexpop.resizerlib.kompressorLib.tasks.KompressorParameters;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;

public class MainFragmentPresenter
        implements MainFragmentContract.Presenter, EntireBatchCopySuccessListener, EntireBatchResizeListener, StartingAssignedTaskListener {

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
        this.kompressor.withStartingAssignedTaskListener(this);
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
        int compressRatio = Injection.provideSharedPreferences()
                .getInt(GlobalConstants.COMPRESSION_RATIO_SHARED_PREFS, 0);
        int maxHeight = Injection.provideSharedPreferences()
                .getInt(GlobalConstants.MAX_HEIGHT_RATIO_SHARED_PREFS, 0);
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
        new DeleteAllCopiedPicturesUseCase(mediaDirectory).perform()
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
    public void onBatchCopySuccess(@NonNull List<File> files) {
        List<Photo> copiedPhotos = new ArrayList<>();
        for (File file : files) {
            String fileSize = Utils.formatDiskSizeToValue(file.length());
            Photo photo = new Photo(file, fileSize);
            copiedPhotos.add(photo);
        }
        if (isViewAttached) {
            view.showAllPhotos(copiedPhotos);
        }
    }

    @Override
    public void onBatchCopyFailed(@NonNull List<File> files) {
        if (isViewAttached) {
            Context ctx = Injection.provideGlobalContext();
            view.showMessage(ctx.getString(R.string.format_success_message, ctx.getString(R.string.message_failed_to_copy_file), files.size(),
                    ctx.getString(R.string.message_files)));
        }
    }

    private void getAvailablePhotosInInternalDirectory() {
        new GetAvailableImagesUseCase(mediaDirectory).perform()
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
                        if (e instanceof FileNotFoundException) {
                            if (isViewAttached) {
                                view.clearAllPhotos();
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void startCompressionOnFiles() {
        new GetAvailableImagesUseCase(mediaDirectory).perform()
                .subscribe(new MaybeObserver<List<Photo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<Photo> availablePhotos) {
                        if (availablePhotos != null && availablePhotos.size() > 0) {
                            initKompressorLibParameters(availablePhotos);
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

    private void initKompressorLibParameters(@NonNull List<Photo> availablePhotos) {
        List<File> photoFiles = new ArrayList<>();
        for (Photo photo : availablePhotos) {
            photoFiles.add(photo.getPhotoFile());
        }
        int compressRatio = Injection.provideSharedPreferences()
                .getInt(GlobalConstants.COMPRESSION_RATIO_SHARED_PREFS, 0);
        int maxHeight = Injection.provideSharedPreferences()
                .getInt(GlobalConstants.MAX_HEIGHT_RATIO_SHARED_PREFS, 0);
        if (compressRatio > 0 && maxHeight > 0) {
            KompressorParameters parameters = new KompressorParameters.MainTaskParametersBuilder().setImageFiles(photoFiles)
                    .setTaskType(TaskType.TASK_RESIZE_AND_COMPRESS_TO_RATIO)
                    .setMaximumResizeWidth(maxHeight)
                    .setCompressionRatio(compressRatio)
                    .createMainTaskParameters();
            kompressor.startTask(parameters);
        }
    }

    @Override
    public void onBatchResizeSuccess(@NonNull List<File> files) {
        if (isViewAttached) {
            Context ctx = Injection.provideGlobalContext();
            view.showMessage(ctx.getString(R.string.format_success_message, ctx.getString(R.string.message_successfully_compressed), files.size(),
                    ctx.getString(R.string.message_files)));
        }
        getAvailablePhotosInInternalDirectory();
    }

    @Override
    public void onBatchResizeFailed(@NonNull List<File> files) {
        if (isViewAttached) {
            Context ctx = Injection.provideGlobalContext();
            view.showMessage(ctx.getString(R.string.format_success_message, ctx.getString(R.string.message_failed_to_resize), files.size(),
                    ctx.getString(R.string.message_files)));
        }
    }

    @Override
    public void onBatchCopyTaskStarted() {
        if (isViewAttached) {
            view.showMessage(Injection.provideGlobalContext()
                    .getString(R.string.message_started_to_copy_file));
        }
    }

    @Override
    public void onBatchResizeTaskStarted() {
        if (isViewAttached) {
            view.showMessage(Injection.provideGlobalContext()
                    .getString(R.string.message_starting_to_compress));
        }
    }
}
