package com.example.alexpop.resizerlib.app.dialogs;

import com.example.alexpop.resizerlib.R;
import com.example.alexpop.resizerlib.app.injection.Injection;
import com.example.alexpop.resizerlib.app.utils.GlobalConstants;
import com.example.alexpop.resizerlib.app.viewModels.ResizeSettingsDialogViewModel;
import com.example.alexpop.resizerlib.databinding.FragmentResizeSettingsBinding;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ResizeSettingsDialogFragment extends DialogFragment implements DialogButtonCallbacks {

    public static final String TAG = ResizeSettingsDialogFragment.class.getSimpleName();
    @NonNull
    public static final String BUNDLE_COMPRESSION_VALUE = "COMPRESSION_VALUE";
    @NonNull
    public static final String BUNDLE_RESIZE_VALUE = "RESIZE_VALUE";
    @NonNull
    private final ResizeSettingsDialogViewModel viewModel = new ResizeSettingsDialogViewModel();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            int compressionRatio = arguments.getInt(ResizeSettingsDialogFragment.BUNDLE_COMPRESSION_VALUE);
            int maxResizeHeight = arguments.getInt(ResizeSettingsDialogFragment.BUNDLE_RESIZE_VALUE);
            viewModel.compressionRatio.set(String.valueOf(compressionRatio));
            viewModel.maxHeight.set(String.valueOf(maxResizeHeight));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentResizeSettingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_resize_settings, container, false);
        binding.setViewModel(viewModel);
        binding.setClickHandlers(this);
        return binding.getRoot();
    }

    @Override
    public void onButtonSaveClicked() {
        SharedPreferences preferences = Injection.provideSharedPreferences();
        SharedPreferences.Editor edit = preferences.edit();
        edit.clear();
        edit.putInt(GlobalConstants.COMPRESSION_RATIO_SHARED_PREFS, Integer.parseInt(viewModel.compressionRatio.get()));
        edit.putInt(GlobalConstants.MAX_HEIGHT_RATIO_SHARED_PREFS, Integer.parseInt(viewModel.maxHeight.get()));
        edit.apply();
        dismiss();
    }

    @Override
    public void onButtonCancelClicked() {
        dismiss();
    }
}
