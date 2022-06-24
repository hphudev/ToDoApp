package com.example.todo.view.epdadapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.todo.R;
import com.example.todo.model.data.GroupObjectModel;
import com.example.todo.model.data.ItemTaskModel;
import com.example.todo.viewmodel.TaskItemViewModel;

import java.util.List;
import java.util.Map;

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

        LottieAnimationView lottieAnimationView = convertView.findViewById(R.id.animation_view);
        CheckBox checkBox = convertView.findViewById(R.id.checkbox_task);
        checkBox.setChecked(itemTaskModel.getTinhTrang());
//      Sự kiện cập nhật trạng thái hoàn thành, không hoàn thành
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
                Bitmap bitmap = BitmapFactory.decodeResource(parent.getContext().getResources(), R.mipmap.ic_app);
                Notification notification = new Notification.Builder(parent.getContext())
                        .setContentTitle("Title push notification")
                        .setContentText("This is a message")
                        .setSmallIcon(R.drawable.star_on)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager = (NotificationManager) parent.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
