package com.example.pipabnb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pipabnb.models.Usuario;
import com.example.pipabnb.models.UsuarioDAO;

public class MainActivity extends AppCompatActivity {
    private EditText inputNombre;
    private EditText inputContrasena;
    private Button btnIngresar;
    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        usuarioDAO = new UsuarioDAO();
        usuarioDAO.asegurarAnfitrionDemo();

        inputNombre = findViewById(R.id.input_nombre);
        inputContrasena = findViewById(R.id.input_contrasena);
        btnIngresar = findViewById(R.id.btn_registrarse);

        btnIngresar.setOnClickListener(v -> iniciarSesion());
        findViewById(R.id.btn_crear_cuenta).setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class)));
    }

    private void iniciarSesion() {
        String nombre = inputNombre.getText().toString().trim();
        String contrasena = inputContrasena.getText().toString();

        if (nombre.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Ingrese nombre y contraseña");
            return;
        }

        btnIngresar.setEnabled(false);
        usuarioDAO.iniciarSesion(nombre, contrasena, (usuario, mensaje) -> {
            btnIngresar.setEnabled(true);
            if (usuario == null) {
                mostrarAlerta("No se pudo ingresar", mensaje);
                return;
            }
            guardarSesion(usuario);
            abrirPantallaSegunRol(usuario);
        });
    }

    private void guardarSesion(Usuario usuario) {
        getSharedPreferences("usuario", MODE_PRIVATE).edit()
                .putString("id", usuario.getId())
                .putString("nombre", usuario.getNombre())
                .putString("rol", usuario.getRol())
                .apply();
    }

    private void abrirPantallaSegunRol(Usuario usuario) {
        Intent intent;
        if ("anfitrion".equals(usuario.getRol())) {
            intent = new Intent(this, GestionAlojamientoActivity.class);
        } else {
            intent = new Intent(this, Pantalla_principal.class);
        }
        startActivity(intent);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }
}
