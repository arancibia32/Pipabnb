package com.example.pipabnb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class SeleccionarUbicacionActivity extends AppCompatActivity {
    private MapView map;
    private Marker marcador;
    private GeoPoint ubicacionSeleccionada;
    private Button btnConfirmar;

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

        setContentView(R.layout.activity_seleccionar_ubicacion);
        map = findViewById(R.id.mapSelector);
        btnConfirmar = findViewById(R.id.btnConfirmarUbicacion);

        map.setMultiTouchControls(true);
        map.setMinZoomLevel(4.0);
        map.setMaxZoomLevel(19.0);

        double latitud = getIntent().getDoubleExtra("latitud", -29.9027);
        double longitud = getIntent().getDoubleExtra("longitud", -71.2520);
        boolean tieneUbicacion = getIntent().hasExtra("latitud") && getIntent().hasExtra("longitud");
        GeoPoint centro = new GeoPoint(latitud, longitud);
        map.getController().setZoom(15.0);
        map.getController().setCenter(centro);

        if (tieneUbicacion) seleccionarPunto(centro);

        MapEventsReceiver receptor = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint punto) {
                seleccionarPunto(punto);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint punto) {
                seleccionarPunto(punto);
                return true;
            }
        };
        map.getOverlays().add(0, new MapEventsOverlay(receptor));

        btnConfirmar.setOnClickListener(v -> confirmarUbicacion());
        findViewById(R.id.btnCancelarUbicacion).setOnClickListener(v -> finish());
    }

    private void seleccionarPunto(GeoPoint punto) {
        ubicacionSeleccionada = punto;
        if (marcador == null) {
            marcador = new Marker(map);
            marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marcador.setTitle("Ubicación del alojamiento");
            map.getOverlays().add(marcador);
        }
        marcador.setPosition(punto);
        btnConfirmar.setEnabled(true);
        map.invalidate();
    }

    private void confirmarUbicacion() {
        if (ubicacionSeleccionada == null) return;
        Intent resultado = new Intent();
        resultado.putExtra("latitud", ubicacionSeleccionada.getLatitude());
        resultado.putExtra("longitud", ubicacionSeleccionada.getLongitude());
        setResult(RESULT_OK, resultado);
        finish();
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
