package com.example.todo.view.adapter;

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
import com.example.todo.model.data.PartnerListModel;
import com.example.todo.model.interfaces.ItemTaskInterface;
import com.example.todo.model.interfaces.PartnerItemInterface;

import java.util.List;

public class PartnerListAdapter extends RecyclerView.Adapter<PartnerListAdapter.PartnerViewHolder> {
    private final PartnerItemInterface partnerItemInterface;
    private List<PartnerListModel> partnerListModels;

    public PartnerListAdapter(PartnerItemInterface partnerItemInterface, List<PartnerListModel> partnerListModels) {
        this.partnerItemInterface = partnerItemInterface;
        this.partnerListModels = partnerListModels;
    }

    public void setData(List<PartnerListModel> partnerListModels) {
        this.partnerListModels = partnerListModels;
        notifyDataSetChanged();
    }

    public void addData(PartnerListModel partnerListModel) {
        this.partnerListModels.add(partnerListModel);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PartnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_partner, parent, false);
        return new PartnerViewHolder(view, this.partnerItemInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull PartnerViewHolder holder, int position) {
        PartnerListModel partnerListModel = partnerListModels.get(position);
        if (partnerListModel == null)
            return;
        holder.textView.setText(partnerListModel.getEmail());
    }

    @Override
    public int getItemCount() {
        return partnerListModels.size();
    }

    public class PartnerViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        public PartnerViewHolder(@NonNull View itemView, PartnerItemInterface partnerItemInterface) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_email);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    partnerItemInterface.onItemClick(getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
