package com.max.notificacionesfcmbis.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.snackbar.Snackbar;
import com.max.notificacionesfcmbis.data.AppDatabase;
import com.max.notificacionesfcmbis.data.Task;
import com.max.notificacionesfcmbis.databinding.FragmentTaskListBinding;
import java.util.List;

public class TaskListFragment extends Fragment implements TaskAdapter.OnTaskListener {

    private static final String ARG_COMPLETED = "arg_completed";
    private FragmentTaskListBinding binding;
    private TaskAdapter adapter;
    private boolean isCompletedList;
    private AppDatabase db;

    public static TaskListFragment newInstance(boolean completed) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_COMPLETED, completed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isCompletedList = getArguments().getBoolean(ARG_COMPLETED);
        }
        db = AppDatabase.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        loadTasks();
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter(null, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    public void loadTasks() {
        List<Task> tasks = db.taskDao().getTasksByStatus(isCompletedList);
        adapter.setTasks(tasks);
        binding.tvEmpty.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDoneClick(Task task) {
        task.isCompleted = !task.isCompleted;
        db.taskDao().update(task);
        loadTasks();
        // Notificar al fragmento padre o usar un ViewModel para refrescar ambos tabs
        if (getParentFragment() instanceof TasksFragment) {
            // Refrescaría los otros fragmentos si fuera necesario
        }
    }

    @Override
    public void onDeleteClick(Task task, int position) {
        db.taskDao().delete(task);
        loadTasks();
        Snackbar.make(binding.getRoot(), "Tarea eliminada", Snackbar.LENGTH_LONG)
                .setAction("DESHACER", v -> {
                    db.taskDao().insert(task);
                    loadTasks();
                }).show();
    }

    @Override
    public void onEditClick(Task task) {
        // Implementar edición si es necesario
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
