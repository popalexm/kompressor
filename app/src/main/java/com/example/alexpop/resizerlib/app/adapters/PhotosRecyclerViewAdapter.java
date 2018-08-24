package com.example.alexpop.resizerlib.app.adapters;

import com.example.alexpop.resizerlib.R;
import com.example.alexpop.resizerlib.app.model.Photo;
import com.example.alexpop.resizerlib.app.viewModels.ImageViewModel;
import com.example.alexpop.resizerlib.databinding.ItemPhotoDetailsBinding;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.File;
import java.util.List;

public class PhotosRecyclerViewAdapter extends RecyclerView.Adapter<PhotosRecyclerViewAdapter.PhotosViewHolder> {

    @NonNull
    private final List<Photo> photoList;

    public PhotosRecyclerViewAdapter(@NonNull List<Photo> currentPhotos) {
        this.photoList = currentPhotos;
    }

    @NonNull
    @Override
    public PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPhotoDetailsBinding photoDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_photo_details, parent,
                false);
        return new PhotosViewHolder(photoDetailsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosViewHolder holder, int position) {
        Photo photo = photoList.get(holder.getAdapterPosition());
        File photoFile = photo.getPhotoFile();
        String photoSize = photo.getPhotoSize();

        ImageViewModel imageViewModel = new ImageViewModel();
        imageViewModel.photoSize.set(photoSize);
        imageViewModel.photoUrl.set(photoFile.getPath());

        ItemPhotoDetailsBinding photoDetailsBinding = holder.photoDetailsBinding;
        photoDetailsBinding.setViewModel(imageViewModel);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    static class PhotosViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final ItemPhotoDetailsBinding photoDetailsBinding;

        PhotosViewHolder(@NonNull ItemPhotoDetailsBinding photoDetailsBinding) {
            super(photoDetailsBinding.getRoot());
            this.photoDetailsBinding = photoDetailsBinding;
        }
    }
}
