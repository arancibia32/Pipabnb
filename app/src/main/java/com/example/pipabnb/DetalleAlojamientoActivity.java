package com.example.pipabnb;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetalleAlojamientoActivity extends AppCompatActivity {

    private boolean esFavorito = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_alojamiento);

        TextView btnVolver = findViewById(R.id.btnVolver);
        TextView btnFavorito = findViewById(R.id.btnFavorito);
        TextView txtNombreAlojamiento =
                findViewById(R.id.txtNombreAlojamiento);
        TextView txtPrecio = findViewById(R.id.txtPrecio);
        TextView txtDescripcion = findViewById(R.id.txtDescripcion);

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

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esFavorito = !esFavorito;

                if (esFavorito) {
                    btnFavorito.setText("♥");
                    Toast.makeText(
                            DetalleAlojamientoActivity.this,
                            "Agregado a favoritos",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    btnFavorito.setText("♡");
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