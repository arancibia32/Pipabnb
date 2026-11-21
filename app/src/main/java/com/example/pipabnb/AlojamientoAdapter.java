
package com.example.pipabnb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pipabnb.models.Alojamiento;

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

        holder.txtNombre.setText(alojamiento.getNombre());
        holder.txtDescripcion.setText(alojamiento.getDescripcion());
        if (alojamiento.getCantidadOpiniones() > 0) {
            holder.txtEstrellas.setText(String.format(java.util.Locale.getDefault(),
                    "%.1f ★ (%d opiniones)", alojamiento.getPromedioCalificacion(),
                    alojamiento.getCantidadOpiniones()));
        } else {
            holder.txtEstrellas.setText("Sin opiniones");
        }
        holder.txtPrecio.setText("$" + (int) alojamiento.getPrecio() + " por noche");

        holder.imgAlojamiento.setImageResource(
                R.drawable.ic_launcher_background
        );

        holder.itemView.setOnClickListener(v -> {
            if (v.getContext() instanceof GestionAlojamientoActivity) {
                // 1. Si estamos en la gestión, auto-rellenamos el formulario de esa pantalla [1.1.1]
                ((GestionAlojamientoActivity) v.getContext()).cargarDatosAlojamiento(alojamiento);
            } else {
                // 2. Si estamos en otra pantalla, abrimos el detalle normal [1.1.1]
                Intent intent = new Intent(v.getContext(), DetalleAlojamientoActivity.class);
                intent.putExtra("id", alojamiento.getId());
                intent.putExtra("favorito", alojamiento.isFavorito());
                intent.putExtra("nombre", alojamiento.getNombre());
                intent.putExtra("precio", "$" + (int) alojamiento.getPrecio() + "\npor noche");
                intent.putExtra("descripcion", alojamiento.getDescripcion());
                intent.putExtra("estrellas", alojamiento.getEstrellas());
                intent.putStringArrayListExtra("servicios", alojamiento.getServicios());
                v.getContext().startActivity(intent);
            }
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
