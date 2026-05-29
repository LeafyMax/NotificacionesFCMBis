package com.max.notificacionesfcmbis.utils;

import org.junit.Test;
import static org.junit.Assert.*;

import com.max.notificacionesfcmbis.data.Task;

public class TaskValidatorTest {

    @Test
    public void testValidTask() {
        Task task = new Task("Comprar leche", "Ir al supermercado", "10/10/2026", "Personal", false);
        assertTrue("La tarea debería ser válida", TaskValidator.isValidTask(task));
    }

    @Test
    public void testNullTask() {
        assertFalse("Debería retornar false para tarea nula", TaskValidator.isValidTask(null));
    }

    @Test
    public void testInvalidTitle() {
        Task taskShortTitle = new Task("a", "Descripción", "10/10/2026", "Personal", false);
        assertFalse("El título es muy corto", TaskValidator.isValidTask(taskShortTitle));

        Task taskNullTitle = new Task(null, "Descripción", "10/10/2026", "Personal", false);
        assertFalse("El título es nulo", TaskValidator.isValidTask(taskNullTitle));
    }

    @Test
    public void testInvalidDescription() {
        Task task = new Task("Título válido", null, "10/10/2026", "Personal", false);
        assertFalse("La descripción es nula", TaskValidator.isValidTask(task));
    }

    @Test
    public void testInvalidCategory() {
        Task task = new Task("Título válido", "Descripción", "10/10/2026", "", false);
        assertFalse("La categoría está vacía", TaskValidator.isValidTask(task));
    }

    @Test
    public void testInvalidDate() {
        Task task = new Task("Título válido", "Descripción", "10-10-2026", "Personal", false);
        assertFalse("El formato de fecha es inválido", TaskValidator.isValidTask(task));
    }

    @Test
    public void testGetTaskPriority() {
        Task highPriority1 = new Task("T1", "D1", "10/10/2026", "Trabajo", false);
        assertEquals("Alta", TaskValidator.getTaskPriority(highPriority1));

        Task highPriority2 = new Task("T2", "D2", "10/10/2026", "Urgente", false);
        assertEquals("Alta", TaskValidator.getTaskPriority(highPriority2));

        Task mediumPriority = new Task("T3", "D3", "10/10/2026", "Estudios", false);
        assertEquals("Media", TaskValidator.getTaskPriority(mediumPriority));

        Task lowPriority = new Task("T4", "D4", "10/10/2026", "Personal", false);
        assertEquals("Baja", TaskValidator.getTaskPriority(lowPriority));
        
        assertEquals("Baja", TaskValidator.getTaskPriority(null));
    }
}
