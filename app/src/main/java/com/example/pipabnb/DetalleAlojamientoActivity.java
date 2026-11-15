package com.example.pipabnb;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pipabnb.models.AlojamientoDAO;

public class DetalleAlojamientoActivity extends AppCompatActivity {

    private boolean esFavorito = false;
    private String id;
    private AlojamientoDAO alojamientoDAO; // 1. Declaramos el DAO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_alojamiento);

        // 2. Inicializamos el DAO
        alojamientoDAO = new AlojamientoDAO();

        TextView btnVolver = findViewById(R.id.btnVolver);
        TextView btnFavorito = findViewById(R.id.btnFavorito);
        TextView txtNombreAlojamiento = findViewById(R.id.txtNombreAlojamiento);
        TextView txtPrecio = findViewById(R.id.txtPrecio);
        TextView txtDescripcion = findViewById(R.id.txtDescripcion);

        // 3. Recuperamos los datos del Intent (incluyendo el ID de Firebase y el estado) [1.1.1]
        id = getIntent().getStringExtra("id");
        esFavorito = getIntent().getBooleanExtra("favorito", false);

        String nombre = getIntent().getStringExtra("nombre");
        String precio = getIntent().getStringExtra("precio");
        String descripcion = getIntent().getStringExtra("descripcion");

        if (nombre != null) {
            txtNombreAlojamiento.setText(nombre);
        }

        if (precio != null) {
            txtPrecio.setText(precio);
        }

        if (descripcion != null) {
            txtDescripcion.setText(descripcion);
        }

        // 4. Pintamos el estado inicial del corazón según Firebase [1.1.1]
        btnFavorito.setText(esFavorito ? "♥" : "♡");

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Invertimos el estado de la variable
                esFavorito = !esFavorito;

                // Actualizamos visualmente el botón
                btnFavorito.setText(esFavorito ? "♥" : "♡");

                // 5. Guardamos la actualización de forma automática en Firebase [1.1.1]
                alojamientoDAO.cambiarFavorito(id, esFavorito);

                if (esFavorito) {
                    Toast.makeText(
                            DetalleAlojamientoActivity.this,
                            "Agregado a favoritos",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                            DetalleAlojamientoActivity.this,
                            "Eliminado de favoritos",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }
}
