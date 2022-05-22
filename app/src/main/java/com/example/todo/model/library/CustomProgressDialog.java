package com.example.todo.model.library;


import android.content.Context;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;

public class CustomProgressDialog {
    private static ProgressDialog progressDialog;
    private boolean isSpinner;
    private Context context;

    public CustomProgressDialog(Context context, int style, String message) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(style);
        progressDialog.setMessage(message);
        if (style == ProgressDialog.STYLE_SPINNER)
            isSpinner = true;
        else
            isSpinner = false;
        progressDialog.setCancelable(true);
    }

    public void show()
    {
        if (progressDialog != null && !progressDialog.isShowing())
        {
            progressDialog.show();
        }
    }

    static public void dismiss()
    {
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
