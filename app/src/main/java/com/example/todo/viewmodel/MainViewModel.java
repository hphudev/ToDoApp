package com.example.todo.viewmodel;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.todo.BR;
import com.example.todo.R;
import com.example.todo.model.data.ItemTaskListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainViewModel extends BaseObservable {
    private String title;
    private Activity activity;
    private String nameNewTaskList;
    private int colorNewTaskList;
    public MainViewModel(Activity activity){
        this.activity = activity;
        colorNewTaskList = activity.getResources().getColor(R.color.blue);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getNameNewTaskList() {
        return nameNewTaskList;
    }

    public void setNameNewTaskList(String nameNewTaskList) {
        this.nameNewTaskList = nameNewTaskList;
        notifyPropertyChanged(BR.nameNewTaskList);
    }

    @Bindable
    public int getColorNewTaskList() {
        return colorNewTaskList;
    }

    public void setColorNewTaskList(int colorNewTaskList) {
        this.colorNewTaskList = colorNewTaskList;
        notifyPropertyChanged(BR.colorNewTaskList);
    }

    public void openDialogCreateTaskList()
    {
        final Dialog dialoOpenCreateTaskList = new Dialog(activity);
        dialoOpenCreateTaskList.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialoOpenCreateTaskList.setCancelable(true);
        dialoOpenCreateTaskList.setCanceledOnTouchOutside(false);
        dialoOpenCreateTaskList.setContentView(R.layout.layout_dialog_create_list_task);
        dialoOpenCreateTaskList.show();

        Button cancelButton = dialoOpenCreateTaskList.findViewById(R.id.btn_cancel_dialog_create_task_list);
        Button btnOpenColorPickerDialog = dialoOpenCreateTaskList.findViewById(R.id.btn_open_dialog_color_picker);
        Button btnCreateTaskList = dialoOpenCreateTaskList.findViewById(R.id.btn_confirm_create_task_list);
        EditText editNameList = dialoOpenCreateTaskList.findViewById(R.id.edt_name_task_list);

        btnOpenColorPickerDialog.setBackgroundColor(colorNewTaskList);
        btnOpenColorPickerDialog.setTextColor(Color.WHITE);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoOpenCreateTaskList.dismiss();
            }
        });

        btnOpenColorPickerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogColorPicker(false, btnOpenColorPickerDialog, getColorNewTaskList());
            }
        });

        btnCreateTaskList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editNameList.getText().toString().equals("")){
                    editNameList.setError("Bạn chưa nhập tên danh sách");
                    return;
                }
//                Toast.makeText(activity.getApplicationContext(), String.valueOf(backgroundColor), Toast.LENGTH_LONG).show();
                createTaskListOnFireStore(editNameList.getText().toString(),colorNewTaskList, btnOpenColorPickerDialog.getTextColors().getDefaultColor());
                dialoOpenCreateTaskList.dismiss();
            }
        });
    }

    public void openDialogColorPicker(boolean supportsAlpha, Button openColorPickerDialog, int currentColor)
    {
//        Toast.makeText(activity.getApplicationContext(), "Đã vào", Toast.LENGTH_LONG).show();
        final AmbilWarnaDialog dialog = new AmbilWarnaDialog(activity, currentColor, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                setColorNewTaskList(color);
                openColorPickerDialog.setBackgroundColor(color);
//                int v = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3 > 128 ? 0 : 255;
                int v = 255;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    openColorPickerDialog.setTextColor(Color.rgb(v, v, v));
                }
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
//                Toast.makeText(activity.getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void createTaskListOnFireStore( String title, int backgroundColor, int textColor) {
//        Toast.makeText(activity, title, Toast.LENGTH_LONG).show();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("danhsach")
                .whereEqualTo("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    map.put("MauNen", backgroundColor);
                                    map.put("MauChu", textColor);
                                    map.put("TenDS", title);
                                    QuerySnapshot querySnapshot = task.getResult();
                                    map.put("STT", querySnapshot.size() + 1);
                                    map.put("Created_at", Calendar.getInstance().getTime());
                                    firestore.collection("danhsach")
                                            .add(map)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful())
                                                    {
//                                                        Toast.makeText(activity.getApplicationContext(), "Đã nhập", Toast.LENGTH_LONG).show();
                                                    }
                                                    else
                                                    {

                                                    }
                                                }
                                            });
                                }
                            }
                        });
    }

    public void updatePositionTaskList(List<ItemTaskListModel> modelList)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int stt = 0;
                for (ItemTaskListModel model:
                        modelList) {
                    stt++;
                    Map<String, Object> hasMap = new HashMap<>();
                    hasMap.put("STT", stt);
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("danhsach")
                            .document(model.getId())
                            .update(hasMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
//                                        Log.d("main", "Đã cập nhật chỉ số");
                                    }
                                }
                            });
                }
            }
        });
        thread.start();
    }

}
