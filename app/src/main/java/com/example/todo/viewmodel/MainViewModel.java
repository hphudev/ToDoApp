package com.example.todo.viewmodel;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.todo.BR;
import com.example.todo.R;
import com.example.todo.model.data.ItemTaskModel;
import com.example.todo.view.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
    private int colorNewTaskList = R.color.purple_700;
    public MainViewModel(Activity activity){
        this.activity = activity;
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
        Button openColorPickerDialog = dialoOpenCreateTaskList.findViewById(R.id.btn_open_dialog_color_picker);
        Button createTaskList = dialoOpenCreateTaskList.findViewById(R.id.btn_confirm_create_task_list);
        EditText editNameList = dialoOpenCreateTaskList.findViewById(R.id.edt_name_task_list);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoOpenCreateTaskList.dismiss();
            }
        });

        openColorPickerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogColorPicker(false, openColorPickerDialog, getColorNewTaskList());
            }
        });

        createTaskList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int backgroundColor = v.getSolidColor();
                if (editNameList.getText().toString().equals("")){
                    editNameList.setError("Bạn chưa nhập tên danh sách");
                    return;
                }
                createTaskList(editNameList.getText().toString(), openColorPickerDialog.getCurrentTextColor(),backgroundColor);
                dialoOpenCreateTaskList.dismiss();
            }
        });
    }

    public void openDialogColorPicker(boolean supportsAlpha, Button openColorPickerDialog, int currentColor)
    {
        Toast.makeText(activity.getApplicationContext(), "Đã vào", Toast.LENGTH_LONG).show();
        final AmbilWarnaDialog dialog = new AmbilWarnaDialog(activity, currentColor, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                setColorNewTaskList(color);
                openColorPickerDialog.setBackgroundColor(color);
                Color colorTmp = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    colorTmp = Color.valueOf(color);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    float v = (colorTmp.red() + colorTmp.green() + colorTmp.blue()) / 3 > 0.5 ? 0 : 1;
                    Toast.makeText(activity, String.valueOf(v), Toast.LENGTH_LONG).show();
//                    openColorPickerDialog.setTextColor(Color.rgb(v, v, v));
                    openColorPickerDialog.setTextColor(Color.rgb(1 - colorTmp.red(), 1 - colorTmp.green(), 1 - colorTmp.blue()));
                }
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(activity.getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void createTaskList( String title, int backgroundColor, int textColor) {
        Toast.makeText(activity, title, Toast.LENGTH_LONG).show();


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
                                                        Toast.makeText(activity.getApplicationContext(), "Đã nhập", Toast.LENGTH_LONG).show();
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

    public void updatePositionTaskList(List<ItemTaskModel> modelList)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int stt = 0;
                for (ItemTaskModel model:
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
                                        Log.d("main", "Đã cập nhật chỉ số");
                                    }
                                }
                            });
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                thread.start();
            }
        }, 1000);
    }

}
