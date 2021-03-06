package com.example.todo.view.epdadapter;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.example.todo.R;
import com.example.todo.model.data.GroupObjectModel;
import com.example.todo.model.data.ItemTaskModel;
import com.example.todo.viewmodel.TaskItemViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ItemTaskExpandListViewAdapter extends BaseExpandableListAdapter {
    private static final int NOTIFICATION_ID = 1301;
    private List<GroupObjectModel> groupObjectModelList;
    private Map<GroupObjectModel, List<ItemTaskModel>> mListItems;

    public ItemTaskExpandListViewAdapter(List<GroupObjectModel> groupObjectModelList, Map<GroupObjectModel, List<ItemTaskModel>> mListItems) {
        this.groupObjectModelList = groupObjectModelList;
        this.mListItems = mListItems;
    }

    public void changeData(List<GroupObjectModel> groupObjectModelList, Map<GroupObjectModel, List<ItemTaskModel>> mListItems) {
        this.groupObjectModelList = groupObjectModelList;
        this.mListItems = mListItems;
        notifyDataSetChanged();
    }
    @Override
    public int getGroupCount() {
        if (groupObjectModelList != null)
            return groupObjectModelList.size();
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupObjectModelList != null)
            return mListItems.get(groupObjectModelList.get(groupPosition)).size();
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupObjectModelList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListItems.get(groupObjectModelList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        GroupObjectModel groupObjectModel = groupObjectModelList.get(groupPosition);
        return groupObjectModel.getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        ItemTaskModel itemTaskModel = mListItems.get(groupObjectModelList.get(groupPosition)).get(childPosition);
        return itemTaskModel.getIdInGroup();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_item, parent, false);
        }
        TextView textViewGroup = convertView.findViewById(R.id.tv_group);
        GroupObjectModel groupObjectModel = groupObjectModelList.get(groupPosition);
        textViewGroup.setText(groupObjectModel.getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        }
        ItemTaskModel itemTaskModel = mListItems.get(groupObjectModelList.get(groupPosition)).get(childPosition);
//
        TextView tv_check_Expired = convertView.findViewById(R.id.tv_check_expired);
        if (!itemTaskModel.getNgayDenHan().equals("null")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date date = simpleDateFormat.parse(itemTaskModel.getNgayDenHan());
                Date now = new Date();
                if (date.before(now) && !simpleDateFormat.format(date).toString().equals(simpleDateFormat.format(now).toString())) {
                    tv_check_Expired.setVisibility(View.VISIBLE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        CheckBox checkBox = convertView.findViewById(R.id.checkbox_task);
        checkBox.setChecked(itemTaskModel.getTinhTrang());
//      S??? ki???n c???p nh???t tr???ng th??i ho??n th??nh, kh??ng ho??n th??nh
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemTaskModel.setTinhTrang(checkBox.isChecked());
                TaskItemViewModel.update(parent.getContext(), itemTaskModel);
            }
        });

        TextView textView = convertView.findViewById(R.id.tv_name_task);
        textView.setText(itemTaskModel.getTenNV());

        ImageButton imageButton = convertView.findViewById(R.id.img_btn_favorite);
        if (itemTaskModel.getQuanTrong())
            imageButton.setImageResource(R.drawable.star_on);
        else
            imageButton.setImageResource(R.drawable.star_off);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemTaskModel.setQuanTrong(!itemTaskModel.getQuanTrong());
                if (itemTaskModel.getQuanTrong()) {
                    imageButton.setImageResource(R.drawable.star_on);
                    TaskItemViewModel.update(parent.getContext(), itemTaskModel);
                }
                else
                {
                    imageButton.setImageResource(R.drawable.star_off);
                    TaskItemViewModel.update(parent.getContext(), itemTaskModel);
                }
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialogDetailTask = new Dialog(parent.getContext());
                dialogDetailTask.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogDetailTask.setCancelable(true);
                dialogDetailTask.setCanceledOnTouchOutside(true);
                dialogDetailTask.setContentView(R.layout.layout_dialog_show_detail_info_task);
                dialogDetailTask.show();

                TextView name_task = dialogDetailTask.findViewById(R.id.tv_name_task);
                TextView date_expired = dialogDetailTask.findViewById(R.id.tv_date_expired);
                TextView remind = dialogDetailTask.findViewById(R.id.tv_remind);
                Button btn_delete_task = dialogDetailTask.findViewById(R.id.btn_delete_task);

                name_task.setText(itemTaskModel.getTenNV());
                date_expired.setText((itemTaskModel.getNgayDenHan().equals("null")) ? "Kh??ng c??" : itemTaskModel.getNgayDenHan());
                remind.setText((itemTaskModel.getNhacNho().equals("null")) ? "Kh??ng c??" : itemTaskModel.getNhacNho());
                btn_delete_task.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SweetAlertDialog(parent.getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("B???n ???? ch???c ch???n?")
                                .setContentText("Nhi???m v??? n??y s??? ???????c x??a v??nh vi???n!")
                                .setConfirmText("Ti???p t???c")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        FirebaseFirestore.getInstance().collection("nhiemvu").document(itemTaskModel.getId())
                                                .delete();
                                        dialogDetailTask.dismiss();
                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .setCancelButton("Tr??? l???i", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                    }
                });
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
