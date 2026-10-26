
package com.example.pipabnb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.activity.OnBackPressedCallback;

import java.util.ArrayList;
import java.text.Normalizer;
import java.util.Locale;

public class Pantalla_principal extends AppCompatActivity {

    private final ArrayList<Alojamiento> array_alojamiento = new ArrayList<>();
    private final ArrayList<Alojamiento> alojamientosVisibles = new ArrayList<>();
    private final ArrayList<String> busquedasRecientes = new ArrayList<>();
    private AlojamientoAdapter adapter;
    private EditText inputDestino;
    private TextView tituloResultados;
    private TextView txtSinResultados;
    private Button reciente1;
    private Button reciente2;
    private String ultimaBusqueda = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);






        Alojamiento alojamiento0 = new Alojamiento(
                "Cabaña del bosque",
                "La Serena",
                "Cabaña para dos personas",
                5,
                false,
                35000,
                "cabaña.jpg"
        );

        Alojamiento alojamiento1 = new Alojamiento(
                "Cabaña junto al río",
                "Coquimbo",
                "Cabaña rodeada de naturaleza",
                4,
                false,
                40000,
                "cabana_rio.jpg"
        );

        Alojamiento alojamiento2 = new Alojamiento(
                "Casa de playa",
                "La Serena",
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
        alojamientosVisibles.addAll(array_alojamiento);
        adapter = new AlojamientoAdapter(alojamientosVisibles);
        recyclerView.setAdapter(adapter);

        inputDestino = findViewById(R.id.input_destino);
        Button btnBuscar = findViewById(R.id.btnBuscar);
        tituloResultados = findViewById(R.id.tituloResultados);
        txtSinResultados = findViewById(R.id.txtSinResultados);
        reciente1 = findViewById(R.id.reciente1);
        reciente2 = findViewById(R.id.reciente2);

        btnBuscar.setOnClickListener(v -> buscarAlojamientos());
        inputDestino.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarAlojamientos();
                return true;
            }
            return false;
        });

        View.OnClickListener repetirBusqueda = v -> {
            inputDestino.setText(((Button) v).getText());
            buscarAlojamientos();
        };
        reciente1.setOnClickListener(repetirBusqueda);
        reciente2.setOnClickListener(repetirBusqueda);

        if (savedInstanceState != null) {
            ArrayList<String> recientes = savedInstanceState.getStringArrayList("busquedasRecientes");
            if (recientes != null) {
                busquedasRecientes.addAll(recientes);
            }
            ultimaBusqueda = savedInstanceState.getString("ultimaBusqueda", "");
            filtrarAlojamientos(ultimaBusqueda);
        }
        actualizarRecientes();

        BottomNavigationView menu_inferior = findViewById(R.id.menuInferior);
        menu_inferior.setOnItemSelectedListener(menuItem ->  {


            int id = menuItem.getItemId();

            if (id == R.id.nav_favoritos) {
                Intent intent_fav = new Intent(Pantalla_principal.this,FavoritoActivity.class);
                startActivity(intent_fav);
                return true;
            }

            if (id == R.id.nav_mapa) {
                Intent intent_map = new Intent(Pantalla_principal.this,MapaActivity.class);
                startActivity(intent_map);
                return true;
            }

            return false;
        });

        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void buscarAlojamientos() {
        ultimaBusqueda = inputDestino.getText().toString().trim().replaceAll("\\s+", " ");
        filtrarAlojamientos(ultimaBusqueda);

        if (!ultimaBusqueda.isEmpty()) {
            for (int i = busquedasRecientes.size() - 1; i >= 0; i--) {
                if (normalizar(busquedasRecientes.get(i)).equals(normalizar(ultimaBusqueda))) {
                    busquedasRecientes.remove(i);
                }
            }
            busquedasRecientes.add(0, ultimaBusqueda);
            if (busquedasRecientes.size() > 2) {
                busquedasRecientes.remove(2);
            }
            actualizarRecientes();
        }

        InputMethodManager teclado = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (teclado != null) {
            teclado.hideSoftInputFromWindow(inputDestino.getWindowToken(), 0);
        }
        inputDestino.clearFocus();
    }

    private void filtrarAlojamientos(String texto) {
        String consulta = normalizar(texto);
        // Se filtra una copia visible para conservar todos los alojamientos originales.
        alojamientosVisibles.clear();
        for (Alojamiento alojamiento : array_alojamiento) {
            if (consulta.isEmpty()
                    || normalizar(alojamiento.destino).contains(consulta)
                    || normalizar(alojamiento.nombre).contains(consulta)) {
                alojamientosVisibles.add(alojamiento);
            }
        }
        adapter.notifyDataSetChanged();
        tituloResultados.setText(consulta.isEmpty()
                ? "Recomendaciones"
                : "Resultados: " + alojamientosVisibles.size());
        txtSinResultados.setVisibility(alojamientosVisibles.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " ");
    }

    private void actualizarRecientes() {
        boolean hayRecientes = !busquedasRecientes.isEmpty();
        findViewById(R.id.tituloRecientes).setVisibility(hayRecientes ? View.VISIBLE : View.GONE);
        findViewById(R.id.contenedor_recientes).setVisibility(hayRecientes ? View.VISIBLE : View.GONE);
        reciente1.setVisibility(hayRecientes ? View.VISIBLE : View.GONE);
        reciente2.setVisibility(busquedasRecientes.size() > 1 ? View.VISIBLE : View.GONE);
        if (hayRecientes) {
            reciente1.setText(busquedasRecientes.get(0));
        }
        if (busquedasRecientes.size() > 1) {
            reciente2.setText(busquedasRecientes.get(1));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("busquedasRecientes", new ArrayList<>(busquedasRecientes));
        outState.putString("ultimaBusqueda", ultimaBusqueda);
    }



    protected void onResume() {
        super.onResume();

        BottomNavigationView menu_inferior = findViewById(R.id.menuInferior);
        menu_inferior.getMenu().setGroupCheckable(0, true, false);
        menu_inferior.getMenu().setGroupCheckable(0, false, false);
    }


}

