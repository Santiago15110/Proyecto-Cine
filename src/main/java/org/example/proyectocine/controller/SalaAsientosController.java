package org.example.proyectocine.controller;

import org.example.proyectocine.model.Asiento;
import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Funcion;

import java.util.ArrayList;

public class SalaAsientosController {

    private static final double PESOS_POR_PUNTO = 1000;

    private final CineFacade facade;

    public SalaAsientosController() {
        this.facade = new CineFacade();
    }

    public ArrayList<Funcion> obtenerFunciones() {
        return facade.obtenerFunciones();
    }

    public boolean estaOcupado(Funcion funcion, Asiento asiento) {
        return funcion.getAsientosOcupados().contains(asiento);
    }

    public boolean estaDisponible(Funcion funcion, Asiento asiento) {
        return funcion.estaDisponible(asiento);
    }

    public double calcularSubtotal(Funcion funcion, int cantidadEntradas) {
        return funcion.getPrecioBase() * cantidadEntradas;
    }

    public int calcularPuntos(double total) {
        return (int) (total / PESOS_POR_PUNTO);
    }
}
