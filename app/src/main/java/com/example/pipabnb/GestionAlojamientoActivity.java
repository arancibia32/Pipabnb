package com.example.pipabnb;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pipabnb.models.Alojamiento;
import com.example.pipabnb.models.AlojamientoDAO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GestionAlojamientoActivity extends AppCompatActivity {

    private EditText inputId, inputNombre, inputDescripcion, inputPrecio, inputEstrellas, inputFoto, inputServicios;
    private Button btnAgregar, btnModificar, btnEliminar, btnVolver;
    private AlojamientoDAO alojamientoDAO;

    // Variables para listar los alojamientos abajo del formulario [1.1.1]
    private RecyclerView recyclerGestion;
    private ArrayList<Alojamiento> listaAlojamientos;
    private AlojamientoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_alojamiento);

        alojamientoDAO = new AlojamientoDAO();

        // Vinculamos vistas [1.1.1]
        inputId = findViewById(R.id.inputId);
        inputNombre = findViewById(R.id.inputNombre);
        inputDescripcion = findViewById(R.id.inputDescripcion);
        inputPrecio = findViewById(R.id.inputPrecio);
        inputEstrellas = findViewById(R.id.inputEstrellas);
        inputFoto = findViewById(R.id.inputFoto);
        inputServicios = findViewById(R.id.inputServicios);

        btnAgregar = findViewById(R.id.btnAgregar);
        btnModificar = findViewById(R.id.btnModificar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnVolver = findViewById(R.id.btnVolver);

        // 1. Inicializamos el RecyclerView de abajo en el onCreate [1.1.1]
        recyclerGestion = findViewById(R.id.recyclerGestion);
        recyclerGestion.setLayoutManager(new LinearLayoutManager(this));

        listaAlojamientos = new ArrayList<>();
        adapter = new AlojamientoAdapter(listaAlojamientos);
        recyclerGestion.setAdapter(adapter);

        // 2. Escuchamos de Firebase en tiempo real para poblar la lista [1.1.1]
        alojamientoDAO.obtenerAlojamientos(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listaAlojamientos.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Alojamiento alojamiento = postSnapshot.getValue(Alojamiento.class);
                    if (alojamiento != null) {
                        listaAlojamientos.add(alojamiento);
                    }
                }
                adapter.notifyDataSetChanged(); // Actualiza la lista automáticamente [1.1.1]
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseTest", "Error al cargar alojamientos", error.toException());
            }
        });

        btnVolver.setOnClickListener(v -> finish());

        // 1. CREATE (Agregar)
        btnAgregar.setOnClickListener(v -> {
            String nombre = inputNombre.getText().toString().trim();
            String desc = inputDescripcion.getText().toString().trim();
            String precioStr = inputPrecio.getText().toString().trim();
            String estrellasStr = inputEstrellas.getText().toString().trim();
            String foto = inputFoto.getText().toString().trim();

            if (!validarFormulario(nombre, desc, precioStr, estrellasStr)) return;

            Alojamiento nuevo = new Alojamiento(null, nombre, desc, Integer.parseInt(estrellasStr), false, Double.parseDouble(precioStr), foto);
            nuevo.setServicios(convertirTextoALista(inputServicios.getText().toString()));

            alojamientoDAO.guardarAlojamiento(nuevo);
            Toast.makeText(this, "Alojamiento creado", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        });

        // 2. UPDATE (Modificar)
        btnModificar.setOnClickListener(v -> {
            String id = inputId.getText().toString().trim();
            String nombre = inputNombre.getText().toString().trim();
            String desc = inputDescripcion.getText().toString().trim();
            String precioStr = inputPrecio.getText().toString().trim();
            String estrellasStr = inputEstrellas.getText().toString().trim();
            String foto = inputFoto.getText().toString().trim();

            if (id.isEmpty() || !validarFormulario(nombre, desc, precioStr, estrellasStr)) {
                if (id.isEmpty()) Toast.makeText(this,
                        "Seleccione un alojamiento de la lista", Toast.LENGTH_SHORT).show();
                return;
            }

            Alojamiento editado = new Alojamiento(id, nombre, desc, Integer.parseInt(estrellasStr), false, Double.parseDouble(precioStr), foto);
            editado.setServicios(convertirTextoALista(inputServicios.getText().toString()));

            alojamientoDAO.actualizarAlojamiento(editado);
            Toast.makeText(this, "Alojamiento modificado", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        });

        // 3. DELETE (Eliminar)
        btnEliminar.setOnClickListener(v -> {
            String id = inputId.getText().toString().trim();

            if (id.isEmpty()) {
                Toast.makeText(this, "Ingrese el ID para eliminar", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Eliminar Alojamiento")
                    .setMessage("¿Estás seguro de que deseas eliminar este alojamiento?")
                    .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                        alojamientoDAO.eliminarAlojamiento(id);
                        Toast.makeText(this, "Eliminado con éxito", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    // Método que llama el adaptador para rellenar el formulario de arriba [1.1.1]
    public void cargarDatosAlojamiento(Alojamiento alojamiento) {
        inputId.setText(alojamiento.getId());
        inputNombre.setText(alojamiento.getNombre());
        inputDescripcion.setText(alojamiento.getDescripcion());
        inputPrecio.setText(String.valueOf(alojamiento.getPrecio()));
        inputEstrellas.setText(String.valueOf(alojamiento.getEstrellas()));
        inputFoto.setText(alojamiento.getFoto());

        if (alojamiento.getServicios() != null) {
            inputServicios.setText(TextUtils.join(", ", alojamiento.getServicios()));
        } else {
            inputServicios.setText("");
        }
    }

    private ArrayList<String> convertirTextoALista(String texto) {
        ArrayList<String> lista = new ArrayList<>();
        if (!texto.trim().isEmpty()) {
            for (String s : texto.split(",")) {
                if (!s.trim().isEmpty()) {
                    lista.add(s.trim());
                }
            }
        }
        return lista;
    }

    private void limpiarCampos() {
        inputId.setText("");
        inputNombre.setText("");
        inputDescripcion.setText("");
        inputPrecio.setText("");
        inputEstrellas.setText("");
        inputFoto.setText("");
        inputServicios.setText("");
    }

    private boolean validarFormulario(String nombre, String descripcion,
                                      String precioTexto, String estrellasTexto) {
        if (nombre.isEmpty() || descripcion.isEmpty() ||
                precioTexto.isEmpty() || estrellasTexto.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            double precio = Double.parseDouble(precioTexto);
            int estrellas = Integer.parseInt(estrellasTexto);
            if (precio <= 0) {
                inputPrecio.setError("El precio debe ser mayor que cero");
                return false;
            }
            if (estrellas < 1 || estrellas > 5) {
                inputEstrellas.setError("Ingrese un valor entre 1 y 5");
                return false;
            }
        } catch (NumberFormatException error) {
            Toast.makeText(this, "Revise el precio y las estrellas", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
