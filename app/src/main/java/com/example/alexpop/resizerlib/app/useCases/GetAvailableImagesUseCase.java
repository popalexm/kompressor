package com.example.alexpop.resizerlib.app.useCases;

import com.example.alexpop.resizerlib.app.model.Photo;
import com.example.alexpop.resizerlib.app.useCases.base.BaseUseCaseMaybe;
import com.example.alexpop.resizerlib.app.utils.Utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GetAvailableImagesUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final File directory;

    public GetAvailableImagesUseCase(@NonNull File directory) {
        this.directory = directory;
    }

    @Override
    public Maybe<List<Photo>> perform() {
        return Maybe.create((MaybeOnSubscribe<List<Photo>>) emitter -> {
            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                List<Photo> copiedPhotos = new ArrayList<>();
                for (File photoFile : files) {
                    if (Utils.isFilePictureFormat(photoFile)) {
                        Photo photo = new Photo(photoFile, Utils.formatDiskSizeToValue(photoFile.length()));
                        copiedPhotos.add(photo);
                    }
                }
                if (!emitter.isDisposed()) {
                    emitter.onSuccess(copiedPhotos);
                }
            } else {
                if (!emitter.isDisposed()) {
                    emitter.onError(new FileNotFoundException());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
