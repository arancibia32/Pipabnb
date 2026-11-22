package com.example.pipabnb;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pipabnb.data.LocalDatabaseHelper;
import com.example.pipabnb.data.FotoUtils;
import com.example.pipabnb.models.AlojamientoDAO;
import com.example.pipabnb.models.Comentario;
import com.example.pipabnb.models.ComentarioDAO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class DetalleAlojamientoActivity extends AppCompatActivity {

    private boolean esFavorito = false;
    private String id;
    private LocalDatabaseHelper localDatabase;
    private ComentarioDAO comentarioDAO;
    private AlojamientoDAO alojamientoDAO;
    private LinearLayout contenedorComentarios;
    private TextView txtPromedioCalificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_alojamiento);

        localDatabase = new LocalDatabaseHelper(this);
        comentarioDAO = new ComentarioDAO();
        alojamientoDAO = new AlojamientoDAO();

        TextView btnVolver = findViewById(R.id.btnVolver);
        TextView btnFavorito = findViewById(R.id.btnFavorito);
        TextView txtNombreAlojamiento = findViewById(R.id.txtNombreAlojamiento);
        TextView txtPrecio = findViewById(R.id.txtPrecio);
        TextView txtDescripcion = findViewById(R.id.txtDescripcion);
        TextView txtServiciosDetalle = findViewById(R.id.txtServiciosDetalle);
        txtPromedioCalificacion = findViewById(R.id.txtPromedioCalificacion);
        contenedorComentarios = findViewById(R.id.contenedorComentarios);
        RatingBar ratingComentario = findViewById(R.id.ratingComentario);
        EditText inputComentario = findViewById(R.id.inputComentario);
        Button btnPublicarComentario = findViewById(R.id.btnPublicarComentario);
        ImageView imagenPrincipal = findViewById(R.id.imagenPrincipal);
        ImageView imagenSecundaria1 = findViewById(R.id.imagenSecundaria1);
        ImageView imagenSecundaria2 = findViewById(R.id.imagenSecundaria2);
        View galeriaSecundaria = findViewById(R.id.galeriaSecundaria);

        id = getIntent().getStringExtra("id");
        esFavorito = localDatabase.isFavorite(id);

        String nombre = getIntent().getStringExtra("nombre");
        String precio = getIntent().getStringExtra("precio");
        String descripcion = getIntent().getStringExtra("descripcion");
        ArrayList<String> servicios = getIntent().getStringArrayListExtra("servicios");
        cargarFotos(id, imagenPrincipal, imagenSecundaria1, imagenSecundaria2,
                galeriaSecundaria);

        if (nombre != null) {
            txtNombreAlojamiento.setText(nombre);
        }

        if (precio != null) {
            txtPrecio.setText(precio);
        }

        if (descripcion != null) {
            txtDescripcion.setText(descripcion);
        }

        if (servicios != null && !servicios.isEmpty()) {
            txtServiciosDetalle.setText(android.text.TextUtils.join("  •  ", servicios));
        } else {
            txtServiciosDetalle.setText("No hay servicios adicionales registrados");
        }
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
                esFavorito = !esFavorito;
                btnFavorito.setText(esFavorito ? "♥" : "♡");

                localDatabase.setFavorite(id, esFavorito);

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

        if (id == null || id.trim().isEmpty()) {
            btnPublicarComentario.setEnabled(false);
            Toast.makeText(this, "No se pudo identificar el alojamiento", Toast.LENGTH_SHORT).show();
        } else {
            escucharComentarios();
        }

        btnPublicarComentario.setOnClickListener(v -> {
            String texto = inputComentario.getText().toString().trim();
            int calificacion = Math.round(ratingComentario.getRating());
            if (texto.isEmpty()) {
                inputComentario.setError("Escribe un comentario");
                return;
            }
            if (calificacion < 1) {
                Toast.makeText(this, "Selecciona una calificación", Toast.LENGTH_SHORT).show();
                return;
            }

            String autor = getSharedPreferences("usuario", MODE_PRIVATE)
                    .getString("nombre", "Huesped");
            Comentario comentario = new Comentario(null, autor, texto,
                    calificacion, System.currentTimeMillis());
            btnPublicarComentario.setEnabled(false);
            comentarioDAO.guardar(id, comentario).addOnCompleteListener(task -> {
                btnPublicarComentario.setEnabled(true);
                if (task.isSuccessful()) {
                    inputComentario.setText("");
                    ratingComentario.setRating(0);
                    Toast.makeText(this, "Comentario publicado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No se pudo publicar el comentario", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void escucharComentarios() {
        comentarioDAO.escuchar(id, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                contenedorComentarios.removeAllViews();
                int cantidad = 0;
                int suma = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Comentario comentario = child.getValue(Comentario.class);
                    if (comentario != null) {
                        cantidad++;
                        suma += comentario.getCalificacion();
                        agregarComentario(comentario);
                    }
                }

                if (cantidad == 0) {
                    txtPromedioCalificacion.setText("Sin opiniones todavía");
                } else {
                    double promedio = (double) suma / cantidad;
                    txtPromedioCalificacion.setText(String.format(Locale.getDefault(),
                            "%.1f ★  (%d opiniones)", promedio, cantidad));
                    alojamientoDAO.actualizarCalificacion(id, promedio, cantidad);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(DetalleAlojamientoActivity.this,
                        "No se pudieron cargar los comentarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarComentario(Comentario comentario) {
        TextView vista = new TextView(this);
        String estrellas = new String(new char[comentario.getCalificacion()]).replace("\0", "★");
        vista.setText(comentario.getAutor() + "  " + estrellas + "\n"
                + comentario.getTexto());

        vista.setTextColor(Color.BLACK);
        vista.setTextSize(14);
        vista.setPadding(dp(12), dp(10), dp(12), dp(10));

        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.WHITE);
        fondo.setCornerRadius(dp(8));
        vista.setBackground(fondo);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8);
        vista.setLayoutParams(params);
        contenedorComentarios.addView(vista);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void mostrarFoto(ArrayList<String> fotos, int posicion, ImageView imageView) {
        if (fotos == null || posicion >= fotos.size()) {
            imageView.setVisibility(View.GONE);
            return;
        }
        imageView.setVisibility(View.VISIBLE);
        FotoUtils.mostrar(imageView, fotos.get(posicion));
    }

    private void cargarFotos(String alojamientoId, ImageView principal,
                             ImageView secundaria1, ImageView secundaria2,
                             View galeriaSecundaria) {
        if (alojamientoId == null || alojamientoId.trim().isEmpty()) {
            principal.setVisibility(View.GONE);
            secundaria1.setVisibility(View.GONE);
            secundaria2.setVisibility(View.GONE);
            galeriaSecundaria.setVisibility(View.GONE);
            return;
        }

        FirebaseDatabase.getInstance().getReference("alojamientos")
                .child(alojamientoId).child("fotos").get()
                .addOnSuccessListener(snapshot -> {
                    ArrayList<String> fotos = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String foto = child.getValue(String.class);
                        if (foto != null) fotos.add(foto);
                    }
                    mostrarFoto(fotos, 0, principal);
                    mostrarFoto(fotos, 1, secundaria1);
                    mostrarFoto(fotos, 2, secundaria2);
                    galeriaSecundaria.setVisibility(fotos.size() > 1
                            ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(error -> {
                    principal.setVisibility(View.GONE);
                    secundaria1.setVisibility(View.GONE);
                    secundaria2.setVisibility(View.GONE);
                    galeriaSecundaria.setVisibility(View.GONE);
                });
    }
}
