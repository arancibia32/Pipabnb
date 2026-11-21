package com.example.pipabnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity); //PANTALLA LOGIN



        EditText input_nombre = findViewById(R.id.input_nombre);
        EditText input_contrasena= findViewById(R.id.input_contrasena);
        Button btnIngresar = findViewById(R.id.btn_registrarse);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Evento del botón Ingresar
// Dentro del evento btnIngresar.setOnClickListener en tu MainActivity:
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = input_nombre.getText().toString().trim();
                String contrasena = input_contrasena.getText().toString();

                if (correo.isEmpty() || contrasena.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    String nombreUsuario = correo.contains("@")
                            ? correo.substring(0, correo.indexOf("@")) : correo;
                    getSharedPreferences("usuario", MODE_PRIVATE).edit()
                            .putString("nombre", nombreUsuario).apply();
                    // SIMULACIÓN DE ROLES:
                    if (correo.equals("admin@pipabnb.com") && contrasena.equals("admin123")) {
                        // Si inicia sesión como administrador, lo dirigimos a la pantalla de gestión del CRUD [1.1.1, 1.1.2]
                        Toast.makeText(MainActivity.this, "Sesión iniciada como Administrador", Toast.LENGTH_SHORT).show();
                        Intent adminIntent = new Intent(MainActivity.this, GestionAlojamientoActivity.class);
                        startActivity(adminIntent);
                    } else {
                        // Si inicia sesión cualquier otro usuario, va al flujo normal de huéspedes [1.1.1, 1.1.2]
                        Toast.makeText(MainActivity.this, "Sesión iniciada como Huésped", Toast.LENGTH_SHORT).show();
                        Intent principalIntent = new Intent(MainActivity.this, Pantalla_principal.class);
                        startActivity(principalIntent);
                    }
                }
            }
        });



        // Obtener la instancia de la base de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference connectionRef = database.getReference("conexion_prueba");

        // Intentar escribir un mensaje de prueba
        connectionRef.setValue("¡PipaBnB conectado con éxito!").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseTest", "¡Conexión establecida correctamente con Realtime Database!");
            } else {
                Log.e("FirebaseTest", "Error de conexión: ", task.getException());
            }
        });


    }
}
