package com.example.pipabnb;
//hola
import java.util.ArrayList;

class Alojamiento {
    String nombre;
    String descripcion;

    int estrellas;
    boolean favorito;
    double precio;
    ArrayList<String> fotos;
    String foto;


    public Alojamiento(String nombre, String descripcion, int estrellas, boolean favorito, double precio, String foto) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estrellas = estrellas;
        this.favorito = favorito;
        this.precio = precio;
        this.foto = foto;
        this.fotos = new ArrayList<>();
    }



}