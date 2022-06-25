package com.example.todo.model.library;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CustomAlertDialog extends DialogFragment {
    public interface alertDialogInterface{
        public void onPositiveEvent(DialogFragment dialogFragment, int requestCode);
        public void onNegativeEvent(DialogFragment dialogFragment, int requestCode);
//        public void onNeutralEvent(DialogFragment dialogFragment);
    }
    private String title;
    private String message;
    private int requestCode;
    public alertDialogInterface listener;

    public CustomAlertDialog(String title, String message, int requestCode){
        this.title = title;
        this.message = message;
        this.requestCode = requestCode;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(this.title)
                .setMessage(this.message)
                .setCancelable(false);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onPositiveEvent(CustomAlertDialog.this, CustomAlertDialog.this.requestCode);
            }
        });
        builder.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.onNegativeEvent(CustomAlertDialog.this, CustomAlertDialog.this.requestCode);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (alertDialogInterface) context;
    }
}
