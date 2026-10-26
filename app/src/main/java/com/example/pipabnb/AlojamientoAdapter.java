
package com.example.pipabnb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlojamientoAdapter extends RecyclerView.Adapter<AlojamientoAdapter.AlojamientoViewHolder> {

    private final ArrayList<Alojamiento> alojamientos;

    public AlojamientoAdapter(ArrayList<Alojamiento> alojamientos) {
        this.alojamientos = alojamientos;
    }

    @NonNull
    @Override

    public AlojamientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alojamiento, parent, false);

        return new AlojamientoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AlojamientoViewHolder holder, int position) {
        Alojamiento alojamiento = alojamientos.get(position);

        holder.txtNombre.setText(alojamiento.nombre);
        holder.txtDescripcion.setText(alojamiento.descripcion);
        holder.txtEstrellas.setText(alojamiento.estrellas + " estrellas");
        holder.txtPrecio.setText("$" + (int) alojamiento.precio + " por noche");

        holder.imgAlojamiento.setImageResource(
                R.drawable.ic_launcher_background
        );
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(
                    v.getContext(),
                    DetalleAlojamientoActivity.class
            );

            intent.putExtra("nombre", alojamiento.nombre);
            intent.putExtra(
                    "precio",
                    "$" + (int) alojamiento.precio + "\npor noche"
            );
            intent.putExtra("descripcion", alojamiento.descripcion);
            intent.putExtra("estrellas", alojamiento.estrellas);

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return alojamientos.size();
    }

    public static class AlojamientoViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAlojamiento;
        TextView txtNombre;
        TextView txtDescripcion;
        TextView txtEstrellas;
        TextView txtPrecio;

        public AlojamientoViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAlojamiento = itemView.findViewById(R.id.imgAlojamiento);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtEstrellas = itemView.findViewById(R.id.txtEstrellas);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
        }
    }
}
