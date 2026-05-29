package com.max.notificacionesfcmbis.utils;

import com.max.notificacionesfcmbis.data.Task;

public class TaskValidator {

    public static boolean isValidTask(Task task) {
        if (task == null) {
            return false;
        }

        // Título no puede ser nulo, vacío o muy corto
        if (task.title == null || task.title.trim().isEmpty() || task.title.trim().length() < 3) {
            return false;
        }

        // Descripción no puede ser nula
        if (task.description == null) {
            return false;
        }

        // Categoría debe ser válida (por ejemplo: "Trabajo", "Personal", "Estudios")
        if (task.category == null || task.category.trim().isEmpty()) {
            return false;
        }

        // Validar formato de fecha básico (dd/MM/yyyy)
        if (task.date == null || !task.date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        return true;
    }

    public static String getTaskPriority(Task task) {
        if (task == null || task.category == null) return "Baja";
        
        switch (task.category.toLowerCase()) {
            case "trabajo":
            case "urgente":
                return "Alta";
            case "estudios":
                return "Media";
            default:
                return "Baja";
        }
    }
}
