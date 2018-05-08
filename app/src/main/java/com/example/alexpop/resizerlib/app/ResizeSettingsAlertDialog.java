package com.example.alexpop.resizerlib.app;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.alexpop.resizerlib.R;


public class ResizeSettingsAlertDialog extends AlertDialog implements DialogInterface.OnClickListener {

    private Context mContext;
    private AlertDialog mDialog;
    private SettingsCallback mSettingsCallback;

    private EditText mMaxHeightEditText;
    private EditText mCompressionRatioEditText;

    private int mOldCompressionRatio;
    private int mOldMaxHeight;

    ResizeSettingsAlertDialog(@NonNull Context context, @NonNull SettingsCallback settingsCallback, int oldCompressionRatio, int oldMaxHeight) {
        super(context);
        mContext = context;
        mSettingsCallback = settingsCallback;
        mOldMaxHeight = oldMaxHeight;
        mOldCompressionRatio = oldCompressionRatio;
        buildDialog();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case AlertDialog.BUTTON_POSITIVE :
                mSettingsCallback.onButtonOkSelected((Integer.valueOf(mCompressionRatioEditText.getText().toString())), Integer.valueOf(mMaxHeightEditText.getText().toString()));
                break;

            case AlertDialog.BUTTON_NEGATIVE :
                mDialog.dismiss();
                mSettingsCallback.onButtonNegativeSelected();
                break;
        }
    }

    private void buildDialog(){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.dialog_resize_settings, null);
        mDialog = new AlertDialog.Builder(mContext).setView(dialogView).create();
        mMaxHeightEditText = dialogView.findViewById(R.id.resize_max_height);
        mCompressionRatioEditText = dialogView.findViewById(R.id.resize_compress_ration);

        mDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Dismiss",
                this);
        mDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                this);

        mDialog.show();
        mMaxHeightEditText.setText(Integer.toString(mOldMaxHeight));
        mCompressionRatioEditText.setText(Integer.toString(mOldCompressionRatio));
    }
}
