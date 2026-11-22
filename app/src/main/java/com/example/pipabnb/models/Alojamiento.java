package com.example.pipabnb.models;

import java.util.ArrayList;

public class Alojamiento {
    private String id;
    private String nombre;
    private String descripcion;
    private int estrellas;
    private boolean favorito;
    private double precio;
    private String foto;
    private ArrayList<String> fotos;
    private ArrayList<String> servicios;
    private double promedioCalificacion;
    private int cantidadOpiniones;
    private double latitud;
    private double longitud;
    private String ciudad;

    public Alojamiento() {
        this.fotos = new ArrayList<>();
        this.servicios = new ArrayList<>();
    }

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
    public double getPromedioCalificacion() { return promedioCalificacion; }
    public void setPromedioCalificacion(double promedioCalificacion) { this.promedioCalificacion = promedioCalificacion; }
    public int getCantidadOpiniones() { return cantidadOpiniones; }
    public void setCantidadOpiniones(int cantidadOpiniones) { this.cantidadOpiniones = cantidadOpiniones; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
}
