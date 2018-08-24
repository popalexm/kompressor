package com.example.alexpop.resizerlib.app.useCases;

import android.support.annotation.NonNull;

import java.io.File;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeleteAllCopiedPictures implements BaseUseCaseCompletable {

    @NonNull
    private final File directory;

    public DeleteAllCopiedPictures(@NonNull File directory) {
        this.directory = directory;
    }

    @Override
    public Completable perform() {
        return Completable.fromAction(() -> {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file != null) {
                    if (file.getName()
                            .toLowerCase()
                            .endsWith(".jpeg") || file.getName()
                            .toLowerCase()
                            .endsWith(".jpg")) {
                        file.delete();
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

