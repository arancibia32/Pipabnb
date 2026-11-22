package com.example.pipabnb;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pipabnb.models.Alojamiento;
import com.example.pipabnb.models.AlojamientoDAO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapaActivity extends AppCompatActivity {
    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(), getSharedPreferences("osmdroid", MODE_PRIVATE));
        String userAgent = "PipaBnB/1.0 (https://github.com/arancibia32/Pipabnb)";
        Configuration.getInstance().setUserAgentValue(userAgent);
        Configuration.getInstance().getAdditionalHttpRequestProperties()
                .put("User-Agent", userAgent);
        Configuration.getInstance().getAdditionalHttpRequestProperties()
                .put("X-Requested-With", getPackageName());

        setContentView(R.layout.mapa);
        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);
        map.setMinZoomLevel(4.0);
        map.setMaxZoomLevel(19.0);
        map.setTilesScaledToDpi(true);
        map.getController().setZoom(12.0);
        map.getController().setCenter(new GeoPoint(-29.9027, -71.2520));

        cargarAlojamientos();
    }

    private void cargarAlojamientos() {
        new AlojamientoDAO().obtenerAlojamientos(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                map.getOverlays().clear();
                GeoPoint primeraUbicacion = null;
                int cantidad = 0;

                for (DataSnapshot child : snapshot.getChildren()) {
                    Alojamiento alojamiento = child.getValue(Alojamiento.class);
                    if (alojamiento == null || !coordenadasValidas(alojamiento)) continue;

                    GeoPoint ubicacion = new GeoPoint(
                            alojamiento.getLatitud(), alojamiento.getLongitud());
                    Marker marcador = new Marker(map);
                    marcador.setPosition(ubicacion);
                    marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marcador.setTitle(alojamiento.getNombre());
                    String ciudad = alojamiento.getCiudad() == null ? "" : alojamiento.getCiudad();
                    marcador.setSnippet(ciudad + "  $" + (int) alojamiento.getPrecio() + " por noche");
                    map.getOverlays().add(marcador);

                    if (primeraUbicacion == null) primeraUbicacion = ubicacion;
                    cantidad++;
                }

                if (primeraUbicacion != null) {
                    map.getController().setCenter(primeraUbicacion);
                    map.getController().setZoom(cantidad == 1 ? 15.0 : 11.0);
                }
                map.invalidate();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MapaActivity.this,
                        "No se pudieron cargar las ubicaciones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean coordenadasValidas(Alojamiento alojamiento) {
        return alojamiento.getLatitud() >= -90 && alojamiento.getLatitud() <= 90
                && alojamiento.getLongitud() >= -180 && alojamiento.getLongitud() <= 180
                && !(alojamiento.getLatitud() == 0 && alojamiento.getLongitud() == 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }
}
