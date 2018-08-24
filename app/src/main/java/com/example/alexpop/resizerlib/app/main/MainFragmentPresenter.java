package com.example.alexpop.resizerlib.app.main;

import com.example.alexpop.resizerlib.R;
import com.example.alexpop.resizerlib.app.activity.Injection;
import com.example.alexpop.resizerlib.app.model.Photo;
import com.example.alexpop.resizerlib.app.useCases.GetAvailableImagesUseCaseMaybe;
import com.example.alexpop.resizerlib.app.utils.Utils;
import com.example.alexpop.resizerlib.kompressorLib.Kompressor;
import com.example.alexpop.resizerlib.kompressorLib.callbacks.EntireBatchCopyCallback;
import com.example.alexpop.resizerlib.kompressorLib.definitions.TaskType;
import com.example.alexpop.resizerlib.kompressorLib.tasks.KompressorParameters;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;

public class MainFragmentPresenter implements MainFragmentContract.Presenter, EntireBatchCopyCallback {

    private final MainFragmentContract.View view;
    private boolean isViewAttached;

    MainFragmentPresenter(MainFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public void onPicturesSelected(@NonNull List<File> photos) {
        Kompressor kompressor = Kompressor.get();
        File toCopyDestinationDirectory = Utils.getCopyToMediaDirectory(Injection.provideGlobalContext());
        KompressorParameters parameters = new KompressorParameters.MainTaskParametersBuilder().setImageFiles(photos)
                .setTaskType(TaskType.TASK_COPY_TO_DIRECTORY)
                .setToCopyDestinationDirectory(toCopyDestinationDirectory)
                .createMainTaskParameters();
        kompressor.withCopyCallback(this);
        kompressor.startTask(parameters);
    }

    @Override
    public void onOpenSelectPictures() {
        view.openFileExplorer();
    }

    @Override
    public void onSettingsClicked() {
    }

    @Override
    public void onCompressClicked() {
    }

    @Override
    public void onRefreshClicked() {
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
            view.showMessage(msg + files.size() + " files !");
        }
    }

    private void getAvailablePhotosInInternalDirectory() {
        File directory = Utils.getCopyToMediaDirectory(Injection.provideGlobalContext());
        new GetAvailableImagesUseCaseMaybe(directory).perform()
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

}
