package com.example.pipabnb.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class AlojamientoDAO {
    private DatabaseReference dbRef;

    public AlojamientoDAO() {
        // Conexion al nodo alojamientos
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
            Map<String, Object> valores = new HashMap<>();
            valores.put("nombre", alojamiento.getNombre());
            valores.put("descripcion", alojamiento.getDescripcion());
            valores.put("estrellas", alojamiento.getEstrellas());
            valores.put("precio", alojamiento.getPrecio());
            valores.put("foto", alojamiento.getFoto());
            valores.put("servicios", alojamiento.getServicios());
            dbRef.child(alojamiento.getId()).updateChildren(valores);
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

    public void actualizarCalificacion(String id, double promedio, int cantidad) {
        if (id == null) return;
        Map<String, Object> valores = new HashMap<>();
        valores.put("promedioCalificacion", promedio);
        valores.put("cantidadOpiniones", cantidad);
        dbRef.child(id).updateChildren(valores);
    }
}
