package com.example.todo.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.model.data.ItemTaskListModel;
import com.example.todo.model.interfaces.ItemTaskInterface;
import com.google.firebase.database.annotations.NotNull;

import java.util.List;

public class ItemTaskListAdapter extends RecyclerView.Adapter<ItemTaskListAdapter.ItemTaskViewHolder> {
    private Context context;
    private String type;
    private List<ItemTaskListModel> mListTask;
    private final ItemTaskInterface itemTaskInterface;

    public ItemTaskListAdapter(Context context, ItemTaskInterface itemTaskInterface, String type) {
        this.context = context;
        this.itemTaskInterface = itemTaskInterface;
        this.type = type;
    }

    public void setData(List<ItemTaskListModel> itemTaskListModelList)
    {
        this.mListTask = itemTaskListModelList;
        notifyDataSetChanged();
    }

    public void addData(ItemTaskListModel itemTaskListModel)
    {
        this.mListTask.add(itemTaskListModel);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ItemTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_list, parent, false);
        return new ItemTaskViewHolder(view, this.itemTaskInterface, this.type);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemTaskViewHolder holder, int position) {
        ItemTaskListModel taskModel = mListTask.get(position);
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
        public ItemTaskViewHolder (@NotNull View itemView, ItemTaskInterface itemTaskInterface, String type)
        {
            super(itemView);
            icon = itemView.findViewById(R.id.img_item);
            title = itemView.findViewById(R.id.title_item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(itemView.getContext(), "start", Toast.LENGTH_LONG).show();
                    if (itemTaskInterface != null)
                    {
//                        Toast.makeText(itemView.getContext(), "clicked", Toast.LENGTH_LONG).show();
                        if (getBindingAdapterPosition() != RecyclerView.NO_POSITION)
                            itemTaskInterface.onItemClick(getBindingAdapterPosition(), type);
                    }
                }
            });
        }
    }
}
