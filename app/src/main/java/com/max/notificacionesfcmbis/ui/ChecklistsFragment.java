package com.max.notificacionesfcmbis.ui;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.max.notificacionesfcmbis.data.AppDatabase;
import com.max.notificacionesfcmbis.data.Checklist;
import com.max.notificacionesfcmbis.data.ChecklistItem;
import com.max.notificacionesfcmbis.databinding.FragmentChecklistsBinding;

import java.util.List;

public class ChecklistsFragment extends Fragment implements ChecklistAdapter.OnChecklistActionListener {

    private FragmentChecklistsBinding binding;
    private ChecklistAdapter adapter;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChecklistsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = AppDatabase.getInstance(requireContext());

        setupRecyclerView();
        loadChecklists();

        binding.fabAddChecklist.setOnClickListener(v -> showAddChecklistDialog());
    }

    private void setupRecyclerView() {
        adapter = new ChecklistAdapter(null, this);
        binding.rvChecklists.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChecklists.setAdapter(adapter);
    }

    private void loadChecklists() {
        List<Checklist> lists = db.checklistDao().getAllChecklists();
        adapter.setChecklists(lists);
    }

    private void showAddChecklistDialog() {
        EditText input = new EditText(requireContext());
        input.setHint("Nombre de la lista (ej: Compra)");
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(requireContext())
                .setTitle("Nueva Checklist")
                .setView(input)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String title = input.getText().toString().trim();
                    if (!title.isEmpty()) {
                        db.checklistDao().insertChecklist(new Checklist(title));
                        loadChecklists();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onAddItem(Checklist checklist) {
        EditText input = new EditText(requireContext());
        input.setHint("Elemento (ej: Leche)");

        new AlertDialog.Builder(requireContext())
                .setTitle("Añadir a " + checklist.title)
                .setView(input)
                .setPositiveButton("Añadir", (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    if (!text.isEmpty()) {
                        db.checklistDao().insertItem(new ChecklistItem(checklist.id, text, false));
                        loadChecklists();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onListCleared() {
        Toast.makeText(requireContext(), "¡Checklist completada y eliminada!", Toast.LENGTH_SHORT).show();
        loadChecklists();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
