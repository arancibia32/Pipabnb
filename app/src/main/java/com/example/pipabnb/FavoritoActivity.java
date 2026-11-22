package com.example.pipabnb;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pipabnb.models.Alojamiento;
import com.example.pipabnb.models.AlojamientoDAO;
import com.example.pipabnb.data.LocalDatabaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoritoActivity extends AppCompatActivity {

    private AlojamientoDAO alojamientoDAO;
    private ArrayList<Alojamiento> listaFavoritos;
    private AlojamientoAdapter adapter;
    private LocalDatabaseHelper localDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fav);

        RecyclerView recycler = findViewById(R.id.recyclerFavoritos);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        listaFavoritos = new ArrayList<>();
        alojamientoDAO = new AlojamientoDAO();
        localDatabase = new LocalDatabaseHelper(this);

        adapter = new AlojamientoAdapter(listaFavoritos);
        recycler.setAdapter(adapter);

        alojamientoDAO.obtenerAlojamientos(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listaFavoritos.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Alojamiento alojamiento = postSnapshot.getValue(Alojamiento.class);

                    if (alojamiento != null && localDatabase.isFavorite(alojamiento.getId())) {
                        listaFavoritos.add(alojamiento);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseTest", "Error al cargar favoritos", error.toException());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (localDatabase != null && listaFavoritos != null) {
            listaFavoritos.removeIf(alojamiento ->
                    !localDatabase.isFavorite(alojamiento.getId()));
            adapter.notifyDataSetChanged();
        }
    }
}
