package org.example.proyectocine.controller;

import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Promocion;

import java.time.LocalDate;
import java.util.ArrayList;

public class AdminPromocionesController {

    private final CineFacade facade;

    public AdminPromocionesController() {
        this.facade = new CineFacade();
    }

    public ArrayList<Promocion> obtenerPromociones() {
        return facade.obtenerPromociones();
    }

    public Promocion crearPromocion(String id, String nombre, double porcentaje,
                                    LocalDate fechaInicio, LocalDate fechaFin) {
        return facade.crearPromocion(id, nombre, porcentaje, fechaInicio, fechaFin);
    }

    public void activarPromocion(String idPromocion) {
        facade.activarPromocion(idPromocion);
    }

    public void desactivarPromocion(String idPromocion) {
        facade.desactivarPromocion(idPromocion);
    }
}