package com.max.notificacionesfcmbis.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.max.notificacionesfcmbis.R;
import com.max.notificacionesfcmbis.data.AppDatabase;
import com.max.notificacionesfcmbis.data.Checklist;
import com.max.notificacionesfcmbis.data.ChecklistItem;
import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private List<Checklist> checklists;
    private OnChecklistActionListener listener;

    public interface OnChecklistActionListener {
        void onAddItem(Checklist checklist);
        void onListCleared(); // Llamado cuando una lista se auto-elimina
    }

    public ChecklistAdapter(List<Checklist> checklists, OnChecklistActionListener listener) {
        this.checklists = checklists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checklist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Checklist checklist = checklists.get(position);
        holder.tvTitle.setText(checklist.title);

        AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
        List<ChecklistItem> items = db.checklistDao().getItemsForChecklist(checklist.id);

        ChecklistItemAdapter itemAdapter = new ChecklistItemAdapter(items, item -> {
            db.checklistDao().updateItemAndCheckCompletion(item);
            // Comprobar si la lista sigue existiendo
            List<Checklist> currentLists = db.checklistDao().getAllChecklists();
            boolean stillExists = false;
            for (Checklist c : currentLists) {
                if (c.id == checklist.id) {
                    stillExists = true;
                    break;
                }
            }
            if (!stillExists) {
                listener.onListCleared();
            }
        });

        holder.rvItems.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvItems.setAdapter(itemAdapter);

        holder.btnAddItem.setOnClickListener(v -> listener.onAddItem(checklist));
    }

    @Override
    public int getItemCount() {
        return checklists != null ? checklists.size() : 0;
    }

    public void setChecklists(List<Checklist> checklists) {
        this.checklists = checklists;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        RecyclerView rvItems;
        Button btnAddItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvChecklistTitle);
            rvItems = itemView.findViewById(R.id.rvItems);
            btnAddItem = itemView.findViewById(R.id.btnAddItem);
        }
    }
}
