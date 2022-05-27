package com.example.todo.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.model.data.ItemTaskModel;
import com.google.firebase.database.annotations.NotNull;

import org.w3c.dom.Text;

import java.util.List;

public class ItemTaskAdapter extends RecyclerView.Adapter<ItemTaskAdapter.ItemTaskViewHolder>{
    private Context context;
    private List<ItemTaskModel> mListTask;

    public ItemTaskAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<ItemTaskModel> itemTaskModelList)
    {
        this.mListTask = itemTaskModelList;
        notifyDataSetChanged();
    }

    public void addData(ItemTaskModel itemTaskModel)
    {
        this.mListTask.add(itemTaskModel);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ItemTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_list, parent, false);
        return new ItemTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemTaskViewHolder holder, int position) {
        ItemTaskModel taskModel = mListTask.get(position);
        if (taskModel == null)
            return;
        holder.icon.setImageResource(taskModel.getIcon());
        holder.title.setText(taskModel.getTitle());
    }

    @Override
    public int getItemCount() {
        if (mListTask != null)
            return mListTask.size();
        return 0;
    }

    public class ItemTaskViewHolder extends RecyclerView.ViewHolder{
        private ImageView icon;
        private TextView title;
        public ItemTaskViewHolder (@NotNull View itemView)
        {
            super(itemView);
            icon = itemView.findViewById(R.id.img_item);
            title = itemView.findViewById(R.id.title_item);
        }
    }
}
