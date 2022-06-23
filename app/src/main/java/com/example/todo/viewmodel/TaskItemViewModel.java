package com.example.todo.viewmodel;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.todo.model.data.ItemTaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TaskItemViewModel {

    static public void update(Context context, ItemTaskModel itemTaskModel) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        map.put("MaDS", itemTaskModel.getMaDS());
        map.put("NgayDenHan", itemTaskModel.getNgayDenHan());
        map.put("NhacNho", itemTaskModel.getNhacNho());
        map.put("QuanTrong", itemTaskModel.getQuanTrong());
        map.put("TenNV", itemTaskModel.getTenNV());
        map.put("TinhTrang", itemTaskModel.getTinhTrang());
        firestore.collection("nhiemvu").document(itemTaskModel.getId())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
