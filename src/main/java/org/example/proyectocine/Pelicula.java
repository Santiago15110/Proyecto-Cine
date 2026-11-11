package org.example.proyectocine;

import java.util.Objects;

public class Pelicula {

    private final String id;
    private String titulo;
    private Genero genero;
    private String clasificacion;
    private String descripcion;
    private String rutaPoster;
    private boolean enCartelera;

    /**
     * Crea una película que inicia en cartelera
     * @param id identificador de la película
     * @param titulo título de la película
     * @param genero género de la película
     * @param clasificacion clasificación por edades
     * @param descripcion descripción breve
     * @param rutaPoster ruta de la imagen del póster
     */
    public Pelicula(String id, String titulo, Genero genero, String clasificacion,
                    String descripcion, String rutaPoster) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id de la película es obligatorio");
        }
        validarTitulo(titulo);
        validarGenero(genero);
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.clasificacion = clasificacion;
        this.descripcion = descripcion;
        this.rutaPoster = rutaPoster;
        this.enCartelera = true;
    }

    private void validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de la película es obligatorio");
        }
    }

    private void validarGenero(Genero genero) {
        if (genero == null) {
            throw new IllegalArgumentException("El género de la película es obligatorio");
        }
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        validarTitulo(titulo);
        this.titulo = titulo;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        validarGenero(genero);
        this.genero = genero;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRutaPoster() {
        return rutaPoster;
    }

    public void setRutaPoster(String rutaPoster) {
        this.rutaPoster = rutaPoster;
    }

    public boolean isEnCartelera() {
        return enCartelera;
    }

    public void setEnCartelera(boolean enCartelera) {
        this.enCartelera = enCartelera;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pelicula)) {
            return false;
        }
        return id.equals(((Pelicula) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return titulo;
    }
}
