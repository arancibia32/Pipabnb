package com.example.pipabnb;

import android.content.Intent;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pipabnb.models.Alojamiento;
import com.example.pipabnb.models.AlojamientoDAO;
import com.example.pipabnb.data.FotoUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GestionAlojamientoActivity extends AppCompatActivity {
    private EditText inputId;
    private EditText inputNombre;
    private EditText inputDescripcion;
    private EditText inputPrecio;
    private EditText inputServicios;
    private EditText inputCiudad;
    private EditText inputLatitud;
    private EditText inputLongitud;
    private TextView txtFotosSeleccionadas;
    private TextView txtUbicacionSeleccionada;
    private Button btnAgregar;
    private Button btnModificar;
    private AlojamientoDAO alojamientoDAO;
    private AlojamientoAdapter adapter;
    private ArrayList<Alojamiento> listaAlojamientos;
    private final ArrayList<Uri> fotosLocales = new ArrayList<>();
    private ArrayList<String> fotosGuardadas = new ArrayList<>();
    private final ExecutorService procesadorFotos = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<Intent> selectorUbicacion = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), resultado -> {
                if (resultado.getResultCode() == Activity.RESULT_OK && resultado.getData() != null) {
                    double latitud = resultado.getData().getDoubleExtra("latitud", 0);
                    double longitud = resultado.getData().getDoubleExtra("longitud", 0);
                    inputLatitud.setText(String.valueOf(latitud));
                    inputLongitud.setText(String.valueOf(longitud));
                    actualizarTextoUbicacion();
                }
            });

    private final ActivityResultLauncher<String> selectorFotos = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(), uris -> {
                fotosLocales.clear();
                int limite = Math.min(uris.size(), 3);
                fotosLocales.addAll(uris.subList(0, limite));
                actualizarContadorFotos();
                if (uris.size() > 3) {
                    Toast.makeText(this, "Se usarán solamente las primeras 3 fotos",
                            Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_alojamiento);

        alojamientoDAO = new AlojamientoDAO();
        inputId = findViewById(R.id.inputId);
        inputNombre = findViewById(R.id.inputNombre);
        inputDescripcion = findViewById(R.id.inputDescripcion);
        inputPrecio = findViewById(R.id.inputPrecio);
        inputServicios = findViewById(R.id.inputServicios);
        inputCiudad = findViewById(R.id.inputCiudad);
        inputLatitud = findViewById(R.id.inputLatitud);
        inputLongitud = findViewById(R.id.inputLongitud);
        txtFotosSeleccionadas = findViewById(R.id.txtFotosSeleccionadas);
        txtUbicacionSeleccionada = findViewById(R.id.txtUbicacionSeleccionada);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnModificar = findViewById(R.id.btnModificar);

        RecyclerView recyclerGestion = findViewById(R.id.recyclerGestion);
        recyclerGestion.setLayoutManager(new LinearLayoutManager(this));
        listaAlojamientos = new ArrayList<>();
        adapter = new AlojamientoAdapter(listaAlojamientos);
        recyclerGestion.setAdapter(adapter);

        escucharAlojamientos();

        findViewById(R.id.btnSeleccionarFotos).setOnClickListener(v ->
                selectorFotos.launch("image/*"));
        findViewById(R.id.btnSeleccionarUbicacion).setOnClickListener(v -> abrirSelectorUbicacion());
        btnAgregar.setOnClickListener(v -> guardarAlojamiento(false));
        btnModificar.setOnClickListener(v -> guardarAlojamiento(true));
        findViewById(R.id.btnEliminar).setOnClickListener(v -> confirmarEliminacion());
        findViewById(R.id.btnLimpiar).setOnClickListener(v -> limpiarCampos());
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> cerrarSesion());
    }

    private void escucharAlojamientos() {
        alojamientoDAO.obtenerAlojamientos(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listaAlojamientos.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Alojamiento alojamiento = child.getValue(Alojamiento.class);
                    if (alojamiento != null) listaAlojamientos.add(alojamiento);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error al cargar alojamientos", error.toException());
            }
        });
    }

    private void guardarAlojamiento(boolean modificar) {
        String id = inputId.getText().toString().trim();
        String nombre = inputNombre.getText().toString().trim();
        String descripcion = inputDescripcion.getText().toString().trim();
        String precioTexto = inputPrecio.getText().toString().trim();
        String ciudad = inputCiudad.getText().toString().trim();
        String latitudTexto = inputLatitud.getText().toString().trim();
        String longitudTexto = inputLongitud.getText().toString().trim();

        if (modificar && id.isEmpty()) {
            Toast.makeText(this, "Seleccione un alojamiento de la lista", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validarFormulario(nombre, descripcion, precioTexto, ciudad, latitudTexto, longitudTexto)) return;

        Alojamiento alojamiento = new Alojamiento(
                modificar ? id : null,
                nombre,
                descripcion,
                0,
                false,
                Double.parseDouble(precioTexto),
                ""
        );
        alojamiento.setServicios(convertirTextoALista(inputServicios.getText().toString()));
        alojamiento.setLatitud(Double.parseDouble(latitudTexto));
        alojamiento.setLongitud(Double.parseDouble(longitudTexto));
        alojamiento.setCiudad(ciudad);

        bloquearAcciones(true);
        if (fotosLocales.isEmpty()) {
            alojamiento.setFotos(new ArrayList<>(fotosGuardadas));
            if (!fotosGuardadas.isEmpty()) alojamiento.setFoto(fotosGuardadas.get(0));
            guardarEnFirebase(alojamiento, modificar);
        } else {
            prepararFotos(fotos -> {
                alojamiento.setFotos(fotos);
                if (!fotos.isEmpty()) alojamiento.setFoto(fotos.get(0));
                guardarEnFirebase(alojamiento, modificar);
            });
        }
    }

    private void prepararFotos(FotosCallback callback) {
        procesadorFotos.execute(() -> {
            try {
                ArrayList<String> fotosBase64 = new ArrayList<>();
                for (Uri uri : fotosLocales) {
                    fotosBase64.add(FotoUtils.convertirABase64(getContentResolver(), uri));
                }
                runOnUiThread(() -> callback.onCompletado(fotosBase64));
            } catch (IOException | RuntimeException error) {
                runOnUiThread(() -> {
                    bloquearAcciones(false);
                    Log.e("Fotos", "No se pudieron preparar las fotos", error);
                    Toast.makeText(this, "No se pudo procesar una de las fotos",
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void guardarEnFirebase(Alojamiento alojamiento, boolean modificar) {
        if (modificar) {
            alojamientoDAO.actualizarAlojamiento(alojamiento);
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
        } else {
            alojamientoDAO.guardarAlojamiento(alojamiento);
            Toast.makeText(this, "Alojamiento publicado", Toast.LENGTH_SHORT).show();
        }
        bloquearAcciones(false);
        limpiarCampos();
    }

    private void confirmarEliminacion() {
        String id = inputId.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Seleccione un alojamiento de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar alojamiento")
                .setMessage("¿Estás seguro de que deseas eliminarlo?")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                    alojamientoDAO.eliminarAlojamiento(id);
                    Toast.makeText(this, "Alojamiento eliminado", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void cargarDatosAlojamiento(Alojamiento alojamiento) {
        inputId.setText(alojamiento.getId());
        inputNombre.setText(alojamiento.getNombre());
        inputDescripcion.setText(alojamiento.getDescripcion());
        inputPrecio.setText(String.valueOf(alojamiento.getPrecio()));
        inputCiudad.setText(alojamiento.getCiudad());
        inputLatitud.setText(String.valueOf(alojamiento.getLatitud()));
        inputLongitud.setText(String.valueOf(alojamiento.getLongitud()));
        inputServicios.setText(alojamiento.getServicios() == null
                ? "" : TextUtils.join(", ", alojamiento.getServicios()));
        fotosLocales.clear();
        fotosGuardadas = alojamiento.getFotos() == null
                ? new ArrayList<>() : new ArrayList<>(alojamiento.getFotos());
        actualizarContadorFotos();
        actualizarTextoUbicacion();
    }

    private boolean validarFormulario(String nombre, String descripcion, String precioTexto,
                                      String ciudad,
                                      String latitudTexto, String longitudTexto) {
        if (nombre.isEmpty() || descripcion.isEmpty() || precioTexto.isEmpty()
                || ciudad.isEmpty() || latitudTexto.isEmpty() || longitudTexto.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            double precio = Double.parseDouble(precioTexto);
            double latitud = Double.parseDouble(latitudTexto);
            double longitud = Double.parseDouble(longitudTexto);
            if (precio <= 0) {
                inputPrecio.setError("El precio debe ser mayor que cero");
                return false;
            }
            if (latitud < -90 || latitud > 90) {
                inputLatitud.setError("La latitud debe estar entre -90 y 90");
                return false;
            }
            if (longitud < -180 || longitud > 180) {
                inputLongitud.setError("La longitud debe estar entre -180 y 180");
                return false;
            }
        } catch (NumberFormatException error) {
            Toast.makeText(this, "Revise precio, latitud y longitud", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private ArrayList<String> convertirTextoALista(String texto) {
        ArrayList<String> servicios = new ArrayList<>();
        for (String servicio : texto.split(",")) {
            if (!servicio.trim().isEmpty()) servicios.add(servicio.trim());
        }
        return servicios;
    }

    private void actualizarContadorFotos() {
        int cantidad = fotosLocales.isEmpty() ? fotosGuardadas.size() : fotosLocales.size();
        txtFotosSeleccionadas.setText(cantidad == 0
                ? "Ninguna foto seleccionada" : cantidad + " foto(s) seleccionada(s)");
    }

    private void limpiarCampos() {
        inputId.setText("");
        inputNombre.setText("");
        inputDescripcion.setText("");
        inputPrecio.setText("");
        inputServicios.setText("");
        inputCiudad.setText("");
        inputLatitud.setText("");
        inputLongitud.setText("");
        fotosLocales.clear();
        fotosGuardadas.clear();
        actualizarContadorFotos();
        actualizarTextoUbicacion();
    }

    private void abrirSelectorUbicacion() {
        Intent intent = new Intent(this, SeleccionarUbicacionActivity.class);
        String latitud = inputLatitud.getText().toString().trim();
        String longitud = inputLongitud.getText().toString().trim();
        if (!latitud.isEmpty() && !longitud.isEmpty()) {
            intent.putExtra("latitud", Double.parseDouble(latitud));
            intent.putExtra("longitud", Double.parseDouble(longitud));
        }
        selectorUbicacion.launch(intent);
    }

    private void actualizarTextoUbicacion() {
        boolean seleccionada = !inputLatitud.getText().toString().trim().isEmpty()
                && !inputLongitud.getText().toString().trim().isEmpty();
        txtUbicacionSeleccionada.setText(seleccionada
                ? "Ubicación seleccionada correctamente"
                : "Todavía no has seleccionado una ubicación");
    }

    private void bloquearAcciones(boolean bloqueado) {
        btnAgregar.setEnabled(!bloqueado);
        btnModificar.setEnabled(!bloqueado);
    }

    private void cerrarSesion() {
        getSharedPreferences("usuario", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private interface FotosCallback {
        void onCompletado(ArrayList<String> fotos);
    }

    @Override
    protected void onDestroy() {
        procesadorFotos.shutdownNow();
        super.onDestroy();
    }
}
