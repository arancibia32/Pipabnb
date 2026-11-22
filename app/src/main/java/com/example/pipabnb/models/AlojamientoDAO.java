package com.example.pipabnb.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class AlojamientoDAO {
    private DatabaseReference dbRef;

    public AlojamientoDAO() {
        dbRef = FirebaseDatabase.getInstance().getReference("alojamientos");
    }

    public void guardarAlojamiento(Alojamiento alojamiento) {
        String idUnico = dbRef.push().getKey();
        alojamiento.setId(idUnico);
        if (idUnico != null) {
            dbRef.child(idUnico).setValue(alojamiento);
        }
    }

    public void obtenerAlojamientos(ValueEventListener listener) {
        dbRef.addValueEventListener(listener);
    }

    public void actualizarAlojamiento(Alojamiento alojamiento) {
        if (alojamiento.getId() != null) {
            Map<String, Object> valores = new HashMap<>();
            valores.put("nombre", alojamiento.getNombre());
            valores.put("descripcion", alojamiento.getDescripcion());
            valores.put("precio", alojamiento.getPrecio());
            valores.put("foto", alojamiento.getFoto());
            valores.put("fotos", alojamiento.getFotos());
            valores.put("servicios", alojamiento.getServicios());
            valores.put("latitud", alojamiento.getLatitud());
            valores.put("longitud", alojamiento.getLongitud());
            valores.put("ciudad", alojamiento.getCiudad());
            dbRef.child(alojamiento.getId()).updateChildren(valores);
        }
    }

    public void cambiarFavorito(String id, boolean esFavorito) {
        if (id != null) {
            dbRef.child(id).child("favorito").setValue(esFavorito);
        }
    }

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
