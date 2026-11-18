package org.example.proyectocine.controller;

import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Genero;
import org.example.proyectocine.model.Pelicula;

import java.util.ArrayList;

public class AdminPeliculasController {

    private final CineFacade facade;

    public AdminPeliculasController() {
        this.facade = new CineFacade();
    }

    public ArrayList<Pelicula> obtenerPeliculas() {
        return facade.obtenerTodasLasPeliculas();
    }

    public Pelicula agregarPelicula(String id, String titulo, Genero genero, String clasificacion,
                                    int duracionMin, String descripcion, String rutaPoster) {
        return facade.agregarPelicula(id, titulo, genero, clasificacion, duracionMin, descripcion, rutaPoster);
    }

    public void retirarDeCartelera(String idPelicula) {
        facade.retirarDeCartelera(idPelicula);
    }

    public void ponerEnCartelera(String idPelicula) {
        facade.ponerEnCartelera(idPelicula);
    }

    public void cambiarPoster(String idPelicula, String rutaPoster) {
        facade.cambiarPosterPelicula(idPelicula, rutaPoster);
    }
}