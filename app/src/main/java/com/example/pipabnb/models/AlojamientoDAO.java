package com.example.pipabnb.models;

import com.example.pipabnb.models.Alojamiento;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AlojamientoDAO {
    private DatabaseReference dbRef;

    public AlojamientoDAO() {
        dbRef = FirebaseDatabase.getInstance().getReference("alojamientos");
    }

    public void obtenerAlojamientos(ValueEventListener listener) {
        dbRef.addValueEventListener(listener);
    }

    // NUEVO MÉTODO: Guarda un alojamiento en Firebase
    public void guardarAlojamiento(Alojamiento alojamiento) {
        // ID UNICO
        String idUnico = dbRef.push().getKey();
        alojamiento.setId(idUnico); // Le asigna el ID autogenerado al modelo

        if (idUnico != null) {
            dbRef.child(idUnico).setValue(alojamiento);
        }
    }



    //UPDATE
    public void actualizarAlojamiento(Alojamiento alojamiento) {
        if (alojamiento.getId() != null) {
            // Al pasar un modelo con un ID que ya existe, Firebase sobrescribe la información anterior [1.1.1]
            dbRef.child(alojamiento.getId()).setValue(alojamiento);
        }
    }

    //DELETE
    public void eliminarAlojamiento(String id) {
        if (id != null) {
            // removeValue() remueve por completo el nodo en la base de datos [1.1.1]
            dbRef.child(id).removeValue();
        }
    }

}
