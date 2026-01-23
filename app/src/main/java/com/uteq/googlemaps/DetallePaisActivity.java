package com.uteq.googlemaps;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uteq.googlemaps.WebServices.Asynchtask;
import com.uteq.googlemaps.WebServices.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DetallePaisActivity extends AppCompatActivity implements Asynchtask, OnMapReadyCallback {

    private GoogleMap mapa;
    private Pais paisDetallado; // 1. Variable para guardar el objeto Pais completo
    private TextView txtNombrePais, txtCapital, txtIso2, txtIsoNum, txtIso3, txtFips, txtPrefix, txtCenter, txtRectangle, txtInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pais);

        inicializarVistas();
        String codigoPais = getIntent().getStringExtra("codigo_pais");

        if (codigoPais == null || codigoPais.isEmpty()) {
            txtNombrePais.setText("Error: No se recibió código de país.");
            return;
        }

        // Llamar al WebService
        String urlInfo = "http://www.geognos.com/api/en/countries/info/" + codigoPais + ".json";
        WebService ws = new WebService(urlInfo, new HashMap<>(), this, this);
        ws.execute("GET");

// 2. Cambia la URL de la bandera
        ImageView imgBandera = findViewById(R.id.imgBandera);
        String urlBandera = "http://www.geognos.com/api/en/countries/flag/" + codigoPais + ".png";
        Glide.with(this).load(urlBandera).into(imgBandera);

        // Inicializar mapa (esto no cambia)
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
    }

    private void inicializarVistas() {
        txtNombrePais = findViewById(R.id.txtPais);
        txtCapital = findViewById(R.id.txtCapital);
        txtIso2 = findViewById(R.id.txtIso2);
        txtIsoNum = findViewById(R.id.txtIsoNum);
        txtIso3 = findViewById(R.id.txtIso3);
        txtFips = findViewById(R.id.txtFips);
        txtPrefix = findViewById(R.id.txtPrefix);
        txtCenter = findViewById(R.id.txtCenter);
        txtRectangle = findViewById(R.id.txtRectangle);
        txtInfo = findViewById(R.id.txtInfo);
    }

    @Override
    public void processFinish(String result) {
        try {
            Log.d("DetallePais_Debug", "Respuesta COMPLETA: " + result);
            JSONObject jsonResponse = new JSONObject(result);

            // ----- INICIO DE LA CORRECCIÓN CLAVE -----
            // La API de geognos.com/info/ devuelve un JSONObject, no un JSONArray.
            // Simplemente obtenemos el objeto directamente.
            if (jsonResponse.has("Results")) {
                JSONObject resultsObject = jsonResponse.getJSONObject("Results");

                // Creamos nuestro objeto Pais usando el constructor completo
                this.paisDetallado = new Pais(resultsObject);

                // Llamamos a actualizar la UI
                actualizarVistasConDatos();

            } else {
                // Si la respuesta no tiene la clave "Results", es un error.
                throw new JSONException("La respuesta JSON no contiene la clave 'Results'.");
            }
            // ----- FIN DE LA CORRECCIÓN CLAVE -----

        } catch (JSONException e) {
            // Este bloque se activa si el JSON es inválido o no tiene la estructura esperada
            runOnUiThread(() -> {
                txtNombrePais.setText("Error al procesar los datos.");
            });
            Log.e("DetallePais_Debug", "Error de JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.getUiSettings().setZoomControlsEnabled(true);
        if (paisDetallado != null) {
            actualizarVistasConDatos();
        }
    }

    private void actualizarVistasConDatos() {
        if (paisDetallado == null) return;

        // Poblar los TextViews (esto no cambia)
        txtNombrePais.setText(paisDetallado.getNombre());
        txtCapital.setText(paisDetallado.getCapital());
        txtIso2.setText(paisDetallado.getAlpha2Code());
        txtIsoNum.setText(paisDetallado.getIsoNum());
        txtIso3.setText(paisDetallado.getIso3());
        txtFips.setText(paisDetallado.getFips());
        txtPrefix.setText(paisDetallado.getTelPrefix());
        txtInfo.setText(paisDetallado.getCountryInfo());
        txtCenter.setText(paisDetallado.getCenterCoords());
        txtRectangle.setText(paisDetallado.getRectCoords());

        // Actualizar el mapa
        if (mapa != null && paisDetallado.getBounds() != null) {

            // Mover la cámara
            mapa.moveCamera(CameraUpdateFactory.newLatLngBounds(paisDetallado.getBounds(), 50));


            // Se obtiene las coordenadas del rectángulo desde el objeto Pais
            LatLngBounds bounds = paisDetallado.getBounds();
            LatLng northEast = bounds.northeast; // Esquina superior derecha
            LatLng southWest = bounds.southwest; // Esquina inferior izquierda

            // Se crea un PolylineOptions para dibujar el rectángulo
            PolylineOptions rectangulo = new PolylineOptions()
                    .add(new LatLng(northEast.latitude, southWest.longitude))
                    .add(northEast)
                    .add(new LatLng(southWest.latitude, northEast.longitude))
                    .add(southWest)
                    .add(new LatLng(northEast.latitude, southWest.longitude));

            rectangulo.width(8);
            rectangulo.color(Color.BLUE);

            mapa.addPolyline(rectangulo);

                // Parseamos las coordenadas del centro que ya tenemos como String
                String[] centerCoords = paisDetallado.getCenterCoords().split(",");
                double lat = Double.parseDouble(centerCoords[0].trim());
                double lng = Double.parseDouble(centerCoords[1].trim());
                LatLng centroDelPais = new LatLng(lat, lng);

                mapa.addMarker(new MarkerOptions()
                        .position(centroDelPais)
                        .title(paisDetallado.getNombre()));

        }
    }
}
