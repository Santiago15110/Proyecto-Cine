package org.example.proyectocine.controller;

import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Funcion;
import org.example.proyectocine.model.Pelicula;
import org.example.proyectocine.model.Sala;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AdminFuncionesController {

    private final CineFacade facade;

    public AdminFuncionesController() {
        this.facade = new CineFacade();
    }

    public ArrayList<Funcion> obtenerFunciones() {
        return facade.obtenerFunciones();
    }

    public ArrayList<Pelicula> obtenerPeliculas() {
        return facade.obtenerPeliculasEnCartelera();
    }

    public ArrayList<Sala> obtenerSalas() {
        return facade.obtenerSalas();
    }

    public Funcion programarFuncion(String id, String idPelicula, String idSala,
                                    LocalDateTime fechaHora, double precio) {
        return facade.programarFuncion(id, idPelicula, idSala, fechaHora, precio);
    }

    public void cancelarFuncion(String idFuncion) {
        facade.cancelarFuncion(idFuncion);
    }
}