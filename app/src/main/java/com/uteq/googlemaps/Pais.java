package com.uteq.googlemaps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Pais {

    private String nombre;
    private String alpha2Code;
    private String urlBandera;

    private String capital, isoNum, iso3, fips, telPrefix, countryInfo, centerCoords, rectCoords;
    private LatLngBounds bounds;


    public Pais(JSONObject a, boolean isListConstructor) throws JSONException {
        this.nombre = a.getString("name");
        this.alpha2Code = a.getString("alpha2Code");
        this.urlBandera = "http://www.geognos.com/api/en/countries/flag/" + this.alpha2Code + ".png";
    }

    public Pais(JSONObject resultsFromGeognos) throws JSONException {
        // Usamos optString y has() para máxima seguridad contra campos faltantes
        this.nombre = resultsFromGeognos.optString("Name", "N/A");

        if (resultsFromGeognos.has("Capital")) {
            this.capital = resultsFromGeognos.getJSONObject("Capital").optString("Name", "N/A");
        } else {
            this.capital = "N/A";
        }

        if (resultsFromGeognos.has("CountryCodes")) {
            JSONObject countryCodes = resultsFromGeognos.getJSONObject("CountryCodes");
            this.alpha2Code = countryCodes.optString("iso2", "N/A");
            this.isoNum = countryCodes.optString("isoN", "N/A");
            this.iso3 = countryCodes.optString("iso3", "N/A");
            this.fips = countryCodes.optString("fips", "N/A");
        }

        this.telPrefix = resultsFromGeognos.optString("TelPref", "N/A");
        this.countryInfo = resultsFromGeognos.optString("CountryInfo", "N/A");

        if (resultsFromGeognos.has("GeoPt")) {
            JSONArray geoPt = resultsFromGeognos.getJSONArray("GeoPt");
            this.centerCoords = String.format("%.2f, %.2f", geoPt.getDouble(0), geoPt.getDouble(1));
        }

        if (resultsFromGeognos.has("GeoRectangle")) {
            JSONObject rect = resultsFromGeognos.getJSONObject("GeoRectangle");
            this.rectCoords = String.format("W:%.2f, E:%.2f, N:%.2f, S:%.2f",
                    rect.optDouble("West"), rect.optDouble("East"),
                    rect.optDouble("North"), rect.optDouble("South"));

            this.bounds = new LatLngBounds(
                    new LatLng(rect.getDouble("South"), rect.getDouble("West")),
                    new LatLng(rect.getDouble("North"), rect.getDouble("East"))
            );
        }
    }

    public static ArrayList<Pais> JsonObjectsBuild(JSONArray datos) throws JSONException {
        ArrayList<Pais> paises = new ArrayList<>();
        for (int i = 0; i < datos.length(); i++) {
            // Llama al constructor simple (con el booleano)
            paises.add(new Pais(datos.getJSONObject(i), true));
        }
        return paises;
    }

    public String getNombre() { return nombre; }
    public String getAlpha2Code() { return alpha2Code; }
    public String getUrlBandera() { return urlBandera; }
    public String getCapital() { return capital; }
    public String getIsoNum() { return isoNum; }
    public String getIso3() { return iso3; }
    public String getFips() { return fips; }
    public String getTelPrefix() { return telPrefix; }
    public String getCountryInfo() { return countryInfo; }
    public String getCenterCoords() { return centerCoords; }
    public String getRectCoords() { return rectCoords; }
    public LatLngBounds getBounds() { return bounds; }
}
