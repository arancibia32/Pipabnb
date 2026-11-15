package com.example.pipabnb.models; // Definido en la carpeta models [1.1.1]

import java.util.ArrayList;

public class Alojamiento {
    private String id; // Requerido para actualizar/eliminar en Firebase [1.1.1]
    private String nombre;
    private String descripcion;
    private int estrellas;
    private boolean favorito;
    private double precio;
    private String foto;
    private ArrayList<String> fotos;
    private ArrayList<String> servicios;

    // 1. Constructor vacío obligatorio para la lectura en Firebase [1.2.4]
    public Alojamiento() {
        this.fotos = new ArrayList<>();
        this.servicios = new ArrayList<>();
    }

    // 2. Constructor de 7 parámetros usado en tu GestionAlojamientoActivity [1.1.1]
    public Alojamiento(String id, String nombre, String descripcion, int estrellas, boolean favorito, double precio, String foto) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estrellas = estrellas;
        this.favorito = favorito;
        this.precio = precio;
        this.foto = foto;
        this.fotos = new ArrayList<>();
        this.servicios = new ArrayList<>();
    }

    // Getters y Setters públicos para el mapeo automático de Firebase [1.2.4]
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

    public ArrayList<String> getFotos() { return fotos; }
    public void setFotos(ArrayList<String> fotos) { this.fotos = fotos; }

    public ArrayList<String> getServicios() { return servicios; }
    public void setServicios(ArrayList<String> servicios) { this.servicios = servicios; }
}
