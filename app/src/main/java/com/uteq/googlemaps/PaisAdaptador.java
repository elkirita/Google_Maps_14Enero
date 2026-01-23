package com.uteq.googlemaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PaisAdaptador extends ArrayAdapter<Pais> {
    public PaisAdaptador(Context context, ArrayList<Pais> datos) {
    super(context, R.layout.lyitem, datos);
}
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.lyitem, null);

        Pais paisActual = getItem(position);

        TextView lblNombre = (TextView) item.findViewById(R.id.txtNombre);
        lblNombre.setText(paisActual.getNombre());

        ImageView imageView = (ImageView)item.findViewById(R.id.imgBandera);

        Glide.with(getContext())
                .load(paisActual.getUrlBandera())
                .into(imageView);
        return item;
    }
}