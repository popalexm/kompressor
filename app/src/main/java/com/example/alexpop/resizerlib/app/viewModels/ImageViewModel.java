package com.example.alexpop.resizerlib.app.viewModels;

import com.bumptech.glide.Glide;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.widget.ImageView;

public class ImageViewModel {

    public ObservableField<String> photoSize = new ObservableField<>("");
    public ObservableField<String> photoUrl = new ObservableField<>("");

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);
    }
}
