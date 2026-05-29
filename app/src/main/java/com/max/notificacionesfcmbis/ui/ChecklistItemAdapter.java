package com.max.notificacionesfcmbis.ui;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.max.notificacionesfcmbis.R;
import com.max.notificacionesfcmbis.data.ChecklistItem;
import java.util.List;

public class ChecklistItemAdapter extends RecyclerView.Adapter<ChecklistItemAdapter.ViewHolder> {

    private List<ChecklistItem> items;
    private OnItemChangeListener listener;

    public interface OnItemChangeListener {
        void onItemChanged(ChecklistItem item);
    }

    public ChecklistItemAdapter(List<ChecklistItem> items, OnItemChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checklist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChecklistItem item = items.get(position);
        holder.tvText.setText(item.text);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isCompleted);

        updateStrikeThrough(holder.tvText, item.isCompleted);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.isCompleted = isChecked;
            updateStrikeThrough(holder.tvText, isChecked);
            listener.onItemChanged(item);
        });
    }

    private void updateStrikeThrough(TextView tv, boolean completed) {
        if (completed) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cbItem);
            tvText = itemView.findViewById(R.id.tvItemText);
        }
    }
}
