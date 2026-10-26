package com.example.pipabnb;

public class Favorito {

    private String nombre;
    private String descripcion;
    private int imagen;
    private boolean favorito;
    private String precio;
    private int estrellas;

    public Favorito(String nombre, String descripcion, int imagen, String precio, int estrellas) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.favorito = true;
        this.precio = precio;
        this.estrellas = estrellas;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getImagen() {
        return imagen;
    }
    public String getPrecio() {return precio;}

    public int getEstrellas() {return estrellas;}

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }
}
