package com.max.notificacionesfcmbis;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    // Test 1: Verificar que el botón flotante está visible al iniciar la app
    @Test
    public void testFabIsDisplayed() {
        onView(withId(R.id.fabAdd)).check(matches(isDisplayed()));
    }

    // Test 2: Simular la interacción de abrir el diálogo de añadir tarea
    @Test
    public void testOpenAddTaskDialog() {
        // Clic en el botón flotante
        onView(withId(R.id.fabAdd)).perform(click());
        
        // Verificar que se abre el diálogo mostrando el campo de título
        onView(withId(R.id.etTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.etDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()));
    }

    // Test 3: Simular escribir una nueva tarea y cerrar teclado
    @Test
    public void testTypeNewTask() {
        // Abrir el diálogo
        onView(withId(R.id.fabAdd)).perform(click());

        // Escribir en el título
        onView(withId(R.id.etTitle))
                .perform(replaceText("Tarea E2E"), closeSoftKeyboard());

        // Escribir en la descripción
        onView(withId(R.id.etDescription))
                .perform(replaceText("Descripción E2E"), closeSoftKeyboard());

        // Verificar que el texto se introdujo correctamente
        onView(withId(R.id.etTitle)).check(matches(withText("Tarea E2E")));
        onView(withId(R.id.etDescription)).check(matches(withText("Descripción E2E")));
        
        // Hacemos clic en guardar (como no elegimos fecha/categoría, puede fallar la validación interna, pero el test verifica la UI)
        onView(withId(R.id.btnSave)).perform(click());
    }
}
