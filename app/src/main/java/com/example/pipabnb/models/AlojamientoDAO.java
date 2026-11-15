package com.example.pipabnb.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AlojamientoDAO {
    private DatabaseReference dbRef;

    public AlojamientoDAO() {
        // Conexión al nodo "alojamientos" en tu Realtime Database [1.1.1]
        dbRef = FirebaseDatabase.getInstance().getReference("alojamientos");
    }

    // 1. CREATE (Crear nuevo alojamiento)
    public void guardarAlojamiento(Alojamiento alojamiento) {
        String idUnico = dbRef.push().getKey();
        alojamiento.setId(idUnico);
        if (idUnico != null) {
            dbRef.child(idUnico).setValue(alojamiento);
        }
    }

    // 2. READ (Leer todos los alojamientos en tiempo real)
    public void obtenerAlojamientos(ValueEventListener listener) {
        dbRef.addValueEventListener(listener);
    }

    // 3. UPDATE COMPLETO (Actualizar todos los campos de un alojamiento)
    public void actualizarAlojamiento(Alojamiento alojamiento) {
        if (alojamiento.getId() != null) {
            dbRef.child(alojamiento.getId()).setValue(alojamiento);
        }
    }

    // 4. UPDATE PARCIAL (Cambiar únicamente el estado de favorito de un ID) [1.1.1]
    public void cambiarFavorito(String id, boolean esFavorito) {
        if (id != null) {
            dbRef.child(id).child("favorito").setValue(esFavorito);
        }
    }

    // 5. DELETE (Eliminar un alojamiento de Firebase)
    public void eliminarAlojamiento(String id) {
        if (id != null) {
            dbRef.child(id).removeValue();
        }
    }
}
