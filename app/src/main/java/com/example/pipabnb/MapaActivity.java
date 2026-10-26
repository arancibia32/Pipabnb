package com.example.pipabnb;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
                getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE)
        );

        Configuration.getInstance().setUserAgentValue(
                "Pipabnb/1.0 (com.example.pipabnb)"
        );
        Configuration.getInstance()
                .getAdditionalHttpRequestProperties()
                .put("User-Agent", "Pipabnb/1.0 (Android)");

        Configuration.getInstance()
                .getAdditionalHttpRequestProperties()
                .put("X-Requested-With", getPackageName());

        setContentView(R.layout.mapa);

        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);

        GeoPoint ubicacion = new GeoPoint(-29.9027, -71.2520);

        map.getController().setZoom(13.0);
        map.getController().setCenter(ubicacion);

        Marker marcador = new Marker(map);
        marcador.setPosition(ubicacion);
        marcador.setTitle("Alojamiento en La Serena");

        map.getOverlays().add(marcador);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause();
        }
    }
}
