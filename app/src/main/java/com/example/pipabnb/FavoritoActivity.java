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

        // 1. Inicializamos la lista de datos vacía y el DAO
        listaFavoritos = new ArrayList<>();
        alojamientoDAO = new AlojamientoDAO();
        localDatabase = new LocalDatabaseHelper(this);

        // 2. Reutilizamos el AlojamientoAdapter con la lista de favoritos vacía
        adapter = new AlojamientoAdapter(listaFavoritos);
        recycler.setAdapter(adapter);

        // 3. Leemos desde Firebase y filtramos en tiempo real [1.1.1]
        alojamientoDAO.obtenerAlojamientos(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listaFavoritos.clear(); // Limpiamos la lista para evitar duplicar

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Alojamiento alojamiento = postSnapshot.getValue(Alojamiento.class);

                    // FILTRADO: Si el alojamiento está marcado como favorito, lo agregamos [1.1.1]
                    if (alojamiento != null && localDatabase.isFavorite(alojamiento.getId())) {
                        listaFavoritos.add(alojamiento);
                    }
                }
                adapter.notifyDataSetChanged(); // Refresca tu RecyclerView en tiempo real
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
