package com.max.notificacionesfcmbis.ui;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.max.notificacionesfcmbis.data.AppDatabase;
import com.max.notificacionesfcmbis.data.Task;
import com.max.notificacionesfcmbis.databinding.DialogAddTaskBinding;
import com.max.notificacionesfcmbis.notifications.AlarmReceiver;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskDialogFragment extends DialogFragment {

    private DialogAddTaskBinding binding;
    private Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPickDate.setOnClickListener(v -> showDateTimePicker());
        binding.btnSave.setOnClickListener(v -> saveTask());
    }

    private void showDateTimePicker() {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(requireContext(), (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                binding.tvSelectedDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveTask() {
        String title = binding.etTitle.getText().toString().trim();
        String desc = binding.etDescription.getText().toString().trim();
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String date = binding.tvSelectedDate.getText().toString();

        if (title.isEmpty() || desc.isEmpty() || date.equals("No seleccionada")) {
            Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task(title, desc, date, category, false);
        AppDatabase.getInstance(requireContext()).taskDao().insert(task);

        scheduleNotification(task);

        Toast.makeText(requireContext(), "Tarea guardada", Toast.LENGTH_SHORT).show();
        
        // Refrescar lista si es posible
        if (getParentFragment() instanceof TasksFragment) {
            // Esto es una simplificación; lo ideal es usar un ViewModel compartido
        }

        dismiss();
    }

    private void scheduleNotification(Task task) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("title", "Recordatorio: " + task.title);
        intent.putExtra("desc", task.description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 
                (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);

        if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
