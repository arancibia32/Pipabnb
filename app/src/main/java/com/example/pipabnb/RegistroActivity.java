package com.example.pipabnb;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pipabnb.models.UsuarioDAO;

public class RegistroActivity extends AppCompatActivity {
    private EditText inputNombre;
    private EditText inputContrasena;
    private EditText inputConfirmarContrasena;
    private Button btnCrearCuenta;
    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        usuarioDAO = new UsuarioDAO();
        inputNombre = findViewById(R.id.inputNombreRegistro);
        inputContrasena = findViewById(R.id.inputContrasenaRegistro);
        inputConfirmarContrasena = findViewById(R.id.inputConfirmarContrasena);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        findViewById(R.id.btnVolverLogin).setOnClickListener(v -> finish());
        btnCrearCuenta.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombre = inputNombre.getText().toString().trim();
        String contrasena = inputContrasena.getText().toString();
        String confirmacion = inputConfirmarContrasena.getText().toString();

        if (nombre.isEmpty() || contrasena.isEmpty() || confirmacion.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Complete todos los campos");
            return;
        }
        if (!contrasena.equals(confirmacion)) {
            mostrarAlerta("Contraseñas diferentes", "Las contraseñas deben coincidir");
            return;
        }

        btnCrearCuenta.setEnabled(false);
        usuarioDAO.registrar(nombre, contrasena, (creado, mensaje) -> {
            btnCrearCuenta.setEnabled(true);
            if (creado) {
                new AlertDialog.Builder(this)
                        .setTitle("Registro exitoso")
                        .setMessage(mensaje)
                        .setPositiveButton("Ir al inicio", (dialog, which) -> finish())
                        .show();
            } else {
                mostrarAlerta("No se pudo registrar", mensaje);
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }
}
