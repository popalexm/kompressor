package com.example.alexpop.resizerlib.app.useCases;

import com.example.alexpop.resizerlib.app.useCases.base.BaseUseCaseCompletable;
import com.example.alexpop.resizerlib.app.utils.Utils;

import android.support.annotation.NonNull;

import java.io.File;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeleteAllCopiedPicturesUseCase implements BaseUseCaseCompletable {

    @NonNull
    private final File directory;

    public DeleteAllCopiedPicturesUseCase(@NonNull File directory) {
        this.directory = directory;
    }

    @Override
    public Completable perform() {
        return Completable.fromAction(() -> {
            File[] files = directory.listFiles();
            for (File photoFile : files) {
                if (photoFile != null) {
                    if (Utils.isFilePictureFormat(photoFile)) {
                        photoFile.delete();
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

