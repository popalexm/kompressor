package com.example.alexpop.resizerlib.app.useCases;

import com.example.alexpop.resizerlib.app.model.Photo;
import com.example.alexpop.resizerlib.app.utils.Utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GetAvailableImagesUseCaseMaybe implements BaseUseCaseMaybe {

    @NonNull
    private final File directory;

    public GetAvailableImagesUseCaseMaybe(@NonNull File directory) {
        this.directory = directory;
    }

    @Override
    public Maybe<List<Photo>> perform() {
        return Maybe.create(new MaybeOnSubscribe<List<Photo>>() {
            @Override
            public void subscribe(MaybeEmitter<List<Photo>> emitter) {
                List<Photo> copiedPhotos = new ArrayList<>();
                File[] files = directory.listFiles();
                for (File file : files) {
                    if (file != null && file.getName()
                            .toLowerCase()
                            .endsWith(".jpeg") || file.getName()
                            .toLowerCase()
                            .endsWith(".jpg")) {
                        String fileSize = Utils.convertToMbKbGb(file.length());
                        Photo photo = new Photo(file, fileSize);
                        copiedPhotos.add(photo);
                    }
                    if (!emitter.isDisposed()) {
                        emitter.onSuccess(copiedPhotos);
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
