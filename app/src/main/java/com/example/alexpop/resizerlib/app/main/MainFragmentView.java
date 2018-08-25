package com.example.alexpop.resizerlib.app.main;

import com.example.alexpop.resizerlib.R;
import com.example.alexpop.resizerlib.app.adapters.PhotosRecyclerViewAdapter;
import com.example.alexpop.resizerlib.app.dialogs.ResizeSettingsDialogFragment;
import com.example.alexpop.resizerlib.app.model.Photo;
import com.example.alexpop.resizerlib.app.utils.Utils;
import com.example.alexpop.resizerlib.databinding.FragmentPhotoListBinding;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MainFragmentView extends Fragment implements MainFragmentContract.View {

    public final static String TAG = MainFragmentView.class.getSimpleName();
    @NonNull
    private static final String IMAGE_TYPE = "image/jpeg";
    private static final int OPEN_LOCAL_STORAGE_CODE = 3;
    @NonNull
    private FragmentPhotoListBinding binding;
    @NonNull
    private MainFragmentPresenter presenter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo_list, container, false);
        presenter = new MainFragmentPresenter(this);
        binding.setPresenter(presenter);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onAttach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDetach();
    }

    @Override
    public void showAllPhotos(@NonNull List<Photo> photos) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewListPhotos.setLayoutManager(linearLayoutManager);
        binding.recyclerViewListPhotos.setAdapter(new PhotosRecyclerViewAdapter(photos));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainFragmentView.OPEN_LOCAL_STORAGE_CODE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData()
                        .getItemCount();
                int currentItem = 0;
                List<File> files = new ArrayList<>();
                while (currentItem < count) {
                    Uri imageUri = data.getClipData()
                            .getItemAt(currentItem)
                            .getUri();
                    String currentPath = Utils.getRealPathFromUri(imageUri);
                    String uri = "file://" + currentPath;
                    files.add(new File(Uri.parse(uri)
                            .getPath()));
                    currentItem = currentItem + 1;
                }
                if (files.size() > 0) {
                    presenter.onPicturesSelected(files);
                }
            } else {
                showMessage(getString(R.string.message_please_select_multiple_images));
            }
        }
    }

    @Override
    public void openFileExplorer() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType(MainFragmentView.IMAGE_TYPE);
        try {
            startActivityForResult(intent, MainFragmentView.OPEN_LOCAL_STORAGE_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showMessage(@NonNull String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showCompressionSettingsDialog(int oldCompressionRatio, int oldMaxHeight) {
        ResizeSettingsDialogFragment fragment = new ResizeSettingsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ResizeSettingsDialogFragment.BUNDLE_COMPRESSION_VALUE, oldCompressionRatio);
        bundle.putInt(ResizeSettingsDialogFragment.BUNDLE_RESIZE_VALUE, oldMaxHeight);
        fragment.setArguments(bundle);
        Activity activity = getActivity();
        if (activity != null) {
            fragment.show(activity.getFragmentManager(), ResizeSettingsDialogFragment.TAG);
        }
    }

    public void startPhotoCompression() {
        presenter.onCompressClicked();
    }

    public void openCompressionSettings() {
        presenter.onSettingsClicked();
    }

    public void deleteImportedPhotos() {
        presenter.onDeleteImportedPhotosClicked();
    }

    public void refreshImportedPhotos() {
        presenter.onRefreshClicked();
    }
}
