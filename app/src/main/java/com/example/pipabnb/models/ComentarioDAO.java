package com.example.pipabnb.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ComentarioDAO {
    private final DatabaseReference comentariosRef;

    public ComentarioDAO() {
        comentariosRef = FirebaseDatabase.getInstance().getReference("comentarios");
    }

    public Task<Void> guardar(String alojamientoId, Comentario comentario) {
        String id = comentariosRef.child(alojamientoId).push().getKey();
        comentario.setId(id);
        return comentariosRef.child(alojamientoId).child(id).setValue(comentario);
    }

    public void escuchar(String alojamientoId, ValueEventListener listener) {
        comentariosRef.child(alojamientoId).addValueEventListener(listener);
    }
}
