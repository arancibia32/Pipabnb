package com.example.pipabnb.models;

public class Alojamiento {
    private String id;
    private String nombre;
    private String descripcion;
    private int estrellas;
    private boolean favorito;
    private double precio;
    private String foto; // URL o nombre de la imagen principal

    //Constructor vacío obligatorio para Firebase
    public Alojamiento() {
    }


    public Alojamiento(String id, String nombre, String descripcion,
                       int estrellas, boolean favorito, double precio, String foto) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estrellas = estrellas;
        this.favorito = favorito;
        this.precio = precio;
        this.foto = foto;
    }

    //Métodos getset para firebase
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getEstrellas() { return estrellas; }
    public void setEstrellas(int estrellas) { this.estrellas = estrellas; }

    public boolean isFavorito() { return favorito; }
    public void setFavorito(boolean favorito) { this.favorito = favorito; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
