package com.max.notificacionesfcmbis;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.max.notificacionesfcmbis.data.AppDatabase;
import com.max.notificacionesfcmbis.databinding.ActivityMainBinding;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        // Inicializar el lanzador de permisos ANTES de usarlo
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No recibirás notificaciones", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Pedir permiso en Android 13+
        askNotificationPermission();

        // SUSCRIPCIÓN OBLIGATORIA A FCM PARA TODOS
        FirebaseMessaging.getInstance().subscribeToTopic("allUsers")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", "Suscrito al tema allUsers");
                    }
                });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_tasks, R.id.nav_checklists, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        updateNavHeader();
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Pedir el permiso
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void updateNavHeader() {
        View headerView = binding.navView.getHeaderView(0);
        if (headerView != null) {
            TextView tvUser = headerView.findViewById(R.id.tv_nav_user);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String userName = prefs.getString("user_name", "Usuario");
            tvUser.setText(userName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_send_global) {
            showSendGlobalDialog();
            return true;
        } else if (id == R.id.action_delete_all) {
            showDeleteAllConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSendGlobalDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_send_global, null);
        EditText etNombre = view.findViewById(R.id.etGlobalNombre);
        EditText etMensaje = view.findViewById(R.id.etGlobalMensaje);

        new AlertDialog.Builder(this)
                .setTitle("Enviar Aviso a Todos")
                .setView(view)
                .setPositiveButton("Enviar", (dialog, nullValue) -> {
                    enviarNotificacionFCM(etNombre.getText().toString(), etMensaje.getText().toString());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void enviarNotificacionFCM(String nombre, String mensaje) {
        new Thread(() -> {
            try {
                URL url = new URL("https://diccionario-piqueras.alwaysdata.net/notificaciones.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String data = "nombre=" + nombre + "&mensaje=" + mensaje;
                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                int code = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (code == 200) Toast.makeText(MainActivity.this, "Enviado a todos", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(MainActivity.this, "Error: " + code, Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error de red", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void showDeleteAllConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_delete_all)
                .setMessage(R.string.confirm_delete_all)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    AppDatabase.getInstance(MainActivity.this).taskDao().deleteAll();
                    recreate();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
