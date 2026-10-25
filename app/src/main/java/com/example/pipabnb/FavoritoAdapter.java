package com.example.pipabnb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoritoAdapter extends RecyclerView.Adapter<FavoritoAdapter.FavoritoViewHolder> {

    private ArrayList<Favorito> lista;

    public FavoritoAdapter(ArrayList<Favorito> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public FavoritoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorito, parent, false);

        return new FavoritoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull FavoritoViewHolder holder, int position) {

        Favorito lugar = lista.get(position);

        holder.txtNombre.setText(lugar.getNombre());
        holder.txtDescripcion.setText(lugar.getDescripcion());
        holder.imgLugar.setImageResource(lugar.getImagen());

        holder.btnFavorito.setText(
                lugar.isFavorito() ? "♥" : "♡");

        holder.btnFavorito.setOnClickListener(v -> {
            lugar.setFavorito(!lugar.isFavorito());

            holder.btnFavorito.setText(
                    lugar.isFavorito() ? "♥" : "♡");
        });

        holder.btnDetalle.setOnClickListener(v ->
                Toast.makeText(
                        v.getContext(),
                        lugar.getNombre(),
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class FavoritoViewHolder extends RecyclerView.ViewHolder {

        ImageView imgLugar;
        TextView txtNombre;
        TextView txtDescripcion;
        TextView btnFavorito;
        TextView btnDetalle;

        public FavoritoViewHolder(@NonNull View itemView) {
            super(itemView);

            imgLugar = itemView.findViewById(R.id.imgLugar);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            btnFavorito = itemView.findViewById(R.id.btnFavorito);
            btnDetalle = itemView.findViewById(R.id.btnDetalle);
        }
    }
}
