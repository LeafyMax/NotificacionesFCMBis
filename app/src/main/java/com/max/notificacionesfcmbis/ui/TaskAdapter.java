package com.max.notificacionesfcmbis.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.max.notificacionesfcmbis.R;
import com.max.notificacionesfcmbis.data.Task;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private OnTaskListener listener;

    public interface OnTaskListener {
        void onDoneClick(Task task);
        void onDeleteClick(Task task, int position);
        void onEditClick(Task task);
    }

    public TaskAdapter(List<Task> tasks, OnTaskListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.tvTitle.setText(task.title);
        holder.tvDescription.setText(task.description);
        holder.tvDate.setText(task.date);
        holder.tvCategory.setText(task.category);
        
        if (task.isCompleted) {
            holder.btnDone.setImageResource(android.R.drawable.checkbox_on_background);
        } else {
            holder.btnDone.setImageResource(android.R.drawable.checkbox_off_background);
        }

        holder.btnDone.setOnClickListener(v -> listener.onDoneClick(task));

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.menu_contextual_task);
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    listener.onEditClick(task);
                    return true;
                } else if (id == R.id.action_delete) {
                    listener.onDeleteClick(task, holder.getAdapterPosition());
                    return true;
                }
                return false;
            });
            popup.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDate, tvCategory;
        ImageButton btnDone;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnDone = itemView.findViewById(R.id.btnDone);
        }
    }
}
