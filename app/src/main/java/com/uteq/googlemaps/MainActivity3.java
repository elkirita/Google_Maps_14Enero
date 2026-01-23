package com.uteq.googlemaps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView; // Importar GridView
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.uteq.googlemaps.WebServices.Asynchtask;
import com.uteq.googlemaps.WebServices.WebService;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity3 extends AppCompatActivity implements Asynchtask {

    private GridView gridPaises;
    private ArrayList<Pais> todosLosPaises = new ArrayList<>();
    private ArrayList<Pais> paisesMostrados = new ArrayList<>();
    private PaisAdaptador adaptadorPaises;
    private boolean estaCargando = false;
    private int paginaActual = 0;
    private final int TAMANO_PAGINA = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gridPaises = findViewById(R.id.gridPaises);
        adaptadorPaises = new PaisAdaptador(this, paisesMostrados);
        gridPaises.setAdapter(adaptadorPaises);

        String urlCompleta = "https://api.countrylayer.com/v2/all?access_key=b6a0e4a557b6f097c6b2a4f8b8777cf3";
        WebService ws = new WebService(urlCompleta, new HashMap<>(), this, this);
        ws.execute("GET");

        gridPaises.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) { }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!estaCargando && (firstVisibleItem + visibleItemCount) >= totalItemCount && totalItemCount > 0 && totalItemCount < todosLosPaises.size()) {
                    cargarSiguientePagina();
                }
            }
        });

        gridPaises.setOnItemClickListener((parent, view, position, id) -> {
            Pais paisSeleccionado = paisesMostrados.get(position);
            Intent intent = new Intent(MainActivity3.this, DetallePaisActivity.class);

            String codigoAEnviar = paisSeleccionado.getAlpha2Code();
            Log.d("MainActivity3", "Enviando código de país: " + codigoAEnviar);
            intent.putExtra("codigo_pais", codigoAEnviar);
            startActivity(intent);
        });
    }

    @Override
    public void processFinish(String result) throws JSONException {
        try {
            JSONArray JSONlista = new JSONArray(result);
            todosLosPaises = Pais.JsonObjectsBuild(JSONlista);
            cargarSiguientePagina();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void cargarSiguientePagina() {
        if (estaCargando) return;
        estaCargando = true;

        new android.os.Handler().postDelayed(() -> {
            int inicio = paginaActual * TAMANO_PAGINA;
            int fin = Math.min(inicio + TAMANO_PAGINA, todosLosPaises.size());

            for (int i = inicio; i < fin; i++) {
                paisesMostrados.add(todosLosPaises.get(i));
            }

            adaptadorPaises.notifyDataSetChanged();
            paginaActual++;
            estaCargando = false;
        }, 300);
    }
}
