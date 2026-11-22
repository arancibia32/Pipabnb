package com.example.pipabnb.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsuarioDAO {
    private final DatabaseReference usuariosRef;

    public interface RegistroCallback {
        void onResultado(boolean creado, String mensaje);
    }

    public interface LoginCallback {
        void onResultado(Usuario usuario, String mensaje);
    }

    public UsuarioDAO() {
        usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");
    }

    public void registrar(String nombre, String contrasena, RegistroCallback callback) {
        usuariosRef.orderByChild("nombre").equalTo(nombre)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            callback.onResultado(false, "El nombre de usuario ya existe");
                            return;
                        }

                        String id = usuariosRef.push().getKey();
                        if (id == null) {
                            callback.onResultado(false, "No se pudo crear el usuario");
                            return;
                        }

                        Usuario usuario = new Usuario(id, nombre, contrasena, "usuario");
                        usuariosRef.child(id).setValue(usuario).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                callback.onResultado(true, "Cuenta creada correctamente");
                            } else {
                                callback.onResultado(false, "No se pudo guardar la cuenta");
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onResultado(false, "Error al consultar Firebase");
                    }
                });
    }

    public void iniciarSesion(String nombre, String contrasena, LoginCallback callback) {
        usuariosRef.orderByChild("nombre").equalTo(nombre)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            callback.onResultado(null, "Usuario no encontrado");
                            return;
                        }

                        for (DataSnapshot child : snapshot.getChildren()) {
                            Usuario usuario = child.getValue(Usuario.class);
                            if (usuario != null && contrasena.equals(usuario.getContrasena())) {
                                callback.onResultado(usuario, "Ingreso correcto");
                                return;
                            }
                        }
                        callback.onResultado(null, "Contraseña incorrecta");
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onResultado(null, "Error al consultar Firebase");
                    }
                });
    }

    public void asegurarAnfitrionDemo() {
        usuariosRef.child("anfitrion_demo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Usuario anfitrion = new Usuario(
                            "anfitrion_demo", "admin", "admin123", "anfitrion");
                    usuariosRef.child("anfitrion_demo").setValue(anfitrion);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
