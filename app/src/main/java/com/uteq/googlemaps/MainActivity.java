package com.uteq.googlemaps;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mapa;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SupportMapFragment mapFragment = (SupportMapFragment)
            getSupportFragmentManager()
                .findFragmentById(R.id.mapquevedo);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;

        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.getUiSettings().setZoomControlsEnabled(true);
        
        
        CameraUpdate camUpd1 =
            CameraUpdateFactory
                    .newLatLngZoom(new LatLng(-1.022294, -79.460454), 13);

        mapa.moveCamera(camUpd1);

        PolylineOptions lineas = new
                PolylineOptions()
                .add(new LatLng(-1.0123347165530094, -79.46717825877806))
                .add(new LatLng(-1.011974417229305, -79.47159735591234))
                .add(new LatLng(-1.012911195382222, -79.47168080667463))
                .add(new LatLng(-1.0132118378103918, -79.4673172994985))
                .add(new LatLng(-1.0123347165530094, -79.46717825877806));

                lineas.width(8);
                lineas.color(Color.RED);

                 mapa.addPolyline(lineas);

        LatLng punto = new LatLng(-1.0125770674412826, -79.46722606548833);
        mapa.addMarker(new MarkerOptions().position(punto)
        .title("Universidad Técnica Estatal de Quevedo"));

    }
}