package com.example.alexpop.resizerlib.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.alexpop.resizerlib.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by alexpop on 4/2/18.
 */

public class PhotosRecyclerViewAdapter extends RecyclerView.Adapter<PhotosRecyclerViewAdapter.PhotosViewHolder>{

    private List<File> mCurrentPhotos;
    private Context mContext;

    PhotosRecyclerViewAdapter(@NonNull Context context , @NonNull List<File> currentPhotos){
        this.mContext = context;
        this.mCurrentPhotos = currentPhotos;
    }

    @NonNull
    @Override
    public PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_photo_details, parent, false);
        return new PhotosRecyclerViewAdapter.PhotosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosViewHolder holder, int position) {
          File photoFile = mCurrentPhotos.get(holder.getAdapterPosition());
          Glide.with(mContext).load(photoFile).into(holder.mImgViewPhoto);

          String sizeInMb = getStringSizeLengthFile(photoFile.length());
          holder.mTxtCurrentPhotoSize.setText(String.format("Current size : %s", sizeInMb));
    }

    @Override
    public int getItemCount() { return mCurrentPhotos == null ? 0 : mCurrentPhotos.size(); }

    class PhotosViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo)
        ImageView mImgViewPhoto;
        @BindView(R.id.current_photo_size)
        TextView mTxtCurrentPhotoSize;

        PhotosViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this , view);
        }
    }

    private String getStringSizeLengthFile(long size) {
        DecimalFormat df = new DecimalFormat("0.00");
        float sizeKb = 1024.0f;
        float sizeMo = sizeKb * sizeKb;
        float sizeGo = sizeMo * sizeKb;
        float sizeTerra = sizeGo * sizeKb;

        if(size < sizeMo)
            return df.format(size / sizeKb)+ " Kb";
        else if(size < sizeGo)
            return df.format(size / sizeMo) + " Mb";
        else if(size < sizeTerra)
            return df.format(size / sizeGo) + " Gb";

        return "";
    }

    public void addPhotoToAdapter(@NonNull File photo) {
        mCurrentPhotos.add(photo);
    }
}
