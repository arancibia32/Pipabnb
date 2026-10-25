
package com.example.pipabnb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Pantalla_principal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);






        ArrayList<Alojamiento> array_alojamiento = new ArrayList<>();

        Alojamiento alojamiento0 = new Alojamiento(
                "Cabaña del bosque",
                "Cabaña para dos personas",
                5,
                false,
                35000,
                "cabaña.jpg"
        );

        Alojamiento alojamiento1 = new Alojamiento(
                "Cabaña junto al río",
                "Cabaña rodeada de naturaleza",
                4,
                false,
                40000,
                "cabana_rio.jpg"
        );

        Alojamiento alojamiento2 = new Alojamiento(
                "Casa de playa",
                "Casa para cuatro personas",
                5,
                false,
                55000,
                "casa_playa.jpg"
        );

        array_alojamiento.add(alojamiento0);
        array_alojamiento.add(alojamiento1);
        array_alojamiento.add(alojamiento2);


        RecyclerView recyclerView = findViewById(R.id.recyclerAlojamientos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AlojamientoAdapter(array_alojamiento));

        BottomNavigationView menu_inferior = findViewById(R.id.menuInferior);
        menu_inferior.setOnItemSelectedListener(menuItem ->  {


            int id = menuItem.getItemId();

            if (id == R.id.nav_favoritos) {
                Intent intent_fav = new Intent(Pantalla_principal.this,FavoritoAdapter.class);
                startActivity(intent_fav);
                return true;
            }

            return false;
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
