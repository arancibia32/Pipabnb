
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
import com.example.pipabnb.data.FotoUtils;

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
        String descripcion = alojamiento.getDescripcion();
        if (alojamiento.getCiudad() != null && !alojamiento.getCiudad().trim().isEmpty()) {
            descripcion = alojamiento.getCiudad() + "  •  " + descripcion;
        }
        holder.txtDescripcion.setText(descripcion);
        if (alojamiento.getCantidadOpiniones() > 0) {
            holder.txtEstrellas.setText(String.format(java.util.Locale.getDefault(),
                    "%.1f ★ (%d opiniones)", alojamiento.getPromedioCalificacion(),
                    alojamiento.getCantidadOpiniones()));
        } else {
            holder.txtEstrellas.setText("Sin opiniones");
        }
        holder.txtPrecio.setText("$" + (int) alojamiento.getPrecio() + " por noche");

        String imagen = alojamiento.getFoto();
        if (alojamiento.getFotos() != null && !alojamiento.getFotos().isEmpty()) {
            imagen = alojamiento.getFotos().get(0);
        }
        FotoUtils.mostrar(holder.imgAlojamiento, imagen);

        holder.itemView.setOnClickListener(v -> {
            if (v.getContext() instanceof GestionAlojamientoActivity) {
                ((GestionAlojamientoActivity) v.getContext()).cargarDatosAlojamiento(alojamiento);
            } else {
                Intent intent = new Intent(v.getContext(), DetalleAlojamientoActivity.class);
                intent.putExtra("id", alojamiento.getId());
                intent.putExtra("favorito", alojamiento.isFavorito());
                intent.putExtra("nombre", alojamiento.getNombre());
                intent.putExtra("precio", "$" + (int) alojamiento.getPrecio() + "\npor noche");
                intent.putExtra("descripcion", alojamiento.getDescripcion());
                intent.putExtra("estrellas", alojamiento.getEstrellas());
                intent.putStringArrayListExtra("servicios", alojamiento.getServicios());
                intent.putExtra("latitud", alojamiento.getLatitud());
                intent.putExtra("longitud", alojamiento.getLongitud());
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
