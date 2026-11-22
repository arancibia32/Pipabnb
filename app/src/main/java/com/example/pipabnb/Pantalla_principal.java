package com.example.pipabnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pipabnb.data.LocalDatabaseHelper;
import com.example.pipabnb.models.Alojamiento;
import com.example.pipabnb.models.AlojamientoDAO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Pantalla_principal extends AppCompatActivity {
    private final ArrayList<Alojamiento> todosLosAlojamientos = new ArrayList<>();
    private final ArrayList<Alojamiento> alojamientosVisibles = new ArrayList<>();
    private AlojamientoAdapter adapter;
    private LocalDatabaseHelper localDatabase;
    private EditText inputDestino;
    private TextView tituloResultados;
    private TextView txtSinResultados;
    private Button reciente1;
    private Button reciente2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        localDatabase = new LocalDatabaseHelper(this);
        inputDestino = findViewById(R.id.input_destino);
        tituloResultados = findViewById(R.id.tituloResultados);
        txtSinResultados = findViewById(R.id.txtSinResultados);
        reciente1 = findViewById(R.id.reciente1);
        reciente2 = findViewById(R.id.reciente2);

        RecyclerView recyclerView = findViewById(R.id.recyclerAlojamientos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlojamientoAdapter(alojamientosVisibles);
        recyclerView.setAdapter(adapter);

        configurarBusqueda();
        cargarBusquedasRecientes();
        escucharAlojamientos();
        configurarNavegacion();
        findViewById(R.id.btnCerrarSesionUsuario).setOnClickListener(v -> cerrarSesion());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
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

    private void configurarBusqueda() {
        findViewById(R.id.btnBuscar).setOnClickListener(v -> buscar(inputDestino.getText().toString()));
        inputDestino.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscar(inputDestino.getText().toString());
                return true;
            }
            return false;
        });

        View.OnClickListener buscarReciente = v -> {
            String texto = ((Button) v).getText().toString();
            inputDestino.setText(texto);
            inputDestino.setSelection(texto.length());
            buscar(texto);
        };
        reciente1.setOnClickListener(buscarReciente);
        reciente2.setOnClickListener(buscarReciente);
    }

    private void escucharAlojamientos() {
        new AlojamientoDAO().obtenerAlojamientos(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                todosLosAlojamientos.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Alojamiento alojamiento = child.getValue(Alojamiento.class);
                    if (alojamiento != null) todosLosAlojamientos.add(alojamiento);
                }
                aplicarFiltro(inputDestino.getText().toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error al obtener alojamientos", error.toException());
                Toast.makeText(Pantalla_principal.this,
                        "No se pudieron cargar los alojamientos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscar(String texto) {
        String busqueda = texto == null ? "" : texto.trim();
        if (!busqueda.isEmpty()) {
            localDatabase.saveRecentSearch(busqueda);
            cargarBusquedasRecientes();
        }
        aplicarFiltro(busqueda);
    }

    private void aplicarFiltro(String texto) {
        String filtro = normalizar(texto);
        alojamientosVisibles.clear();
        for (Alojamiento alojamiento : todosLosAlojamientos) {
            if (filtro.isEmpty() || coincide(alojamiento, filtro)) {
                alojamientosVisibles.add(alojamiento);
            }
        }

        adapter.notifyDataSetChanged();
        txtSinResultados.setVisibility(alojamientosVisibles.isEmpty() ? View.VISIBLE : View.GONE);
        tituloResultados.setText(filtro.isEmpty() ? "Alojamientos disponibles" :
                "Resultados (" + alojamientosVisibles.size() + ")");
    }

    private boolean coincide(Alojamiento alojamiento, String filtro) {
        if (normalizar(alojamiento.getNombre()).contains(filtro) ||
                normalizar(alojamiento.getDescripcion()).contains(filtro) ||
                normalizar(alojamiento.getCiudad()).contains(filtro)) return true;
        if (alojamiento.getServicios() != null) {
            for (String servicio : alojamiento.getServicios()) {
                if (normalizar(servicio).contains(filtro)) return true;
            }
        }
        return false;
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(Locale.ROOT).trim();
    }

    private void cargarBusquedasRecientes() {
        List<String> recientes = localDatabase.getRecentSearches(2);
        configurarBotonReciente(reciente1, recientes, 0);
        configurarBotonReciente(reciente2, recientes, 1);
    }

    private void configurarBotonReciente(Button boton, List<String> recientes, int posicion) {
        if (posicion < recientes.size()) {
            boton.setText(recientes.get(posicion));
            boton.setVisibility(View.VISIBLE);
        } else {
            boton.setVisibility(View.GONE);
        }
    }

    private void configurarNavegacion() {
        BottomNavigationView menuInferior = findViewById(R.id.menuInferior);
        menuInferior.setSelectedItemId(R.id.nav_buscar);
        menuInferior.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_buscar) return true;
            if (id == R.id.nav_favoritos) {
                startActivity(new Intent(this, FavoritoActivity.class));
                return true;
            }
            if (id == R.id.nav_mapa) {
                startActivity(new Intent(this, MapaActivity.class));
                return true;
            }
            return false;
        });
    }

    private void cerrarSesion() {
        getSharedPreferences("usuario", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BottomNavigationView) findViewById(R.id.menuInferior))
                .setSelectedItemId(R.id.nav_buscar);
    }
}
