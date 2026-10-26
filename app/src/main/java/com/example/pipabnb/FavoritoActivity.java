package com.example.pipabnb;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoritoActivity extends AppCompatActivity {

    public static final ArrayList<Favorito> listaFavoritos =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fav);

        RecyclerView recycler = findViewById(R.id.recyclerFavoritos);

        recycler.setLayoutManager(new LinearLayoutManager(this));
/*
        listaFavoritos.add(new Favorito(
                "Lugar ejemplo",
                "Un hermoso templo histórico para visitar",
                R.drawable.templo1
        ));

        listaFavoritos.add(new Favorito(
                "Templo antiguo",
                "Arquitectura clásica bajo el cielo azul",
                R.drawable.templo2
        ));
*/

        FavoritoAdapter adapter = new FavoritoAdapter(listaFavoritos);

        recycler.setAdapter(adapter);


    }
    public static void agregarFavorito(Favorito nuevoFavorito) {
        for (Favorito favorito : listaFavoritos) {
            if (favorito.getNombre().equals(nuevoFavorito.getNombre())) {
                return;
            }
        }

        listaFavoritos.add(nuevoFavorito);
    }

    public static void quitarFavorito(String nombre) {
        for (int i = listaFavoritos.size() - 1; i >= 0; i--) {
            if (listaFavoritos.get(i).getNombre().equals(nombre)) {
                listaFavoritos.remove(i);
                return;
            }
        }
    }
}