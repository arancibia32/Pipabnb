package com.example.pipabnb.models;

public class Comentario {
    private String id;
    private String autor;
    private String texto;
    private int calificacion;
    private long fecha;

    public Comentario() {
    }

    public Comentario(String id, String autor, String texto, int calificacion, long fecha) {
        this.id = id;
        this.autor = autor;
        this.texto = texto;
        this.calificacion = calificacion;
        this.fecha = fecha;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public int getCalificacion() { return calificacion; }
    public void setCalificacion(int calificacion) { this.calificacion = calificacion; }
    public long getFecha() { return fecha; }
    public void setFecha(long fecha) { this.fecha = fecha; }
}
