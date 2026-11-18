package org.example.proyectocine.controller;

import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Sala;

import java.util.ArrayList;

public class AdminSalasController {

    private final CineFacade facade;

    public AdminSalasController() {
        this.facade = new CineFacade();
    }

    public ArrayList<Sala> obtenerSalas() {
        return facade.obtenerSalas();
    }

    public Sala crearSala(String id, String nombre, int filas, int columnas) {
        return facade.crearSala(id, nombre, filas, columnas);
    }

    public void deshabilitarAsiento(String idSala, String codigoAsiento) {
        facade.deshabilitarAsiento(idSala, codigoAsiento);
    }

    public void habilitarAsiento(String idSala, String codigoAsiento) {
        facade.habilitarAsiento(idSala, codigoAsiento);
    }
}