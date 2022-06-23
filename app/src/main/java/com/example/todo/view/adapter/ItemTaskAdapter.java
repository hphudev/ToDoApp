package com.example.todo.view.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.model.data.ItemTaskModel;
import com.example.todo.model.interfaces.ItemTaskInterface;

import java.util.List;

public class ItemTaskAdapter extends RecyclerView.Adapter<ItemTaskAdapter.ItemTaskViewHolder> {
    private final ItemTaskInterface itemTaskInterface;
    private List<ItemTaskModel> itemTaskModels;


    public ItemTaskAdapter(ItemTaskInterface itemTaskInterface, List<ItemTaskModel> itemTaskModels) {
        this.itemTaskInterface = itemTaskInterface;
        this.itemTaskModels = itemTaskModels;
    }

    public void setData(List<ItemTaskModel> itemTaskModels) {
        this.itemTaskModels = itemTaskModels;
    }

    public void addData(ItemTaskModel itemTaskModel) {
        this.itemTaskModels.add(itemTaskModel);
    }

    @NonNull
    @Override
    public ItemTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ItemTaskViewHolder(view, this.itemTaskInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemTaskViewHolder holder, int position) {
        ItemTaskModel itemTaskModel = itemTaskModels.get(position);
        if (itemTaskModel == null)
            return;
        holder.checkBox.setChecked(itemTaskModel.getTinhTrang());
        holder.textView.setText(itemTaskModel.getTenNV());
        holder.imageButton.setImageResource((itemTaskModel.getQuanTrong()) ? (R.drawable.star_on) : (R.drawable.star));
    }

    @Override
    public int getItemCount() {
        return itemTaskModels.size();
    }

    public class ItemTaskViewHolder extends RecyclerView.ViewHolder{
        private CheckBox checkBox;
        private TextView textView;
        private ImageButton imageButton;
        public ItemTaskViewHolder(@NonNull View itemView, ItemTaskInterface itemTaskInterface) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_task);
            textView = itemView.findViewById(R.id.tv_name_task);
            imageButton = itemView.findViewById(R.id.img_btn_favorite);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemTaskModels.get(getBindingAdapterPosition()).getQuanTrong() == true)
                        imageButton.setImageResource(R.drawable.star_on);
                    else
                        imageButton.setImageResource(R.drawable.star);
                }
            });
        }
    }
}
