package org.example.proyectocine.controller;

import org.example.proyectocine.model.Asiento;
import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Funcion;

import java.util.ArrayList;

/**
 * Puente entre la pantalla de selección de asientos y el modelo.
 */
public class SalaAsientosController {

    /** Misma regla del Facade: 1 punto por cada $1.000 pagados con saldo. */
    private static final double PESOS_POR_PUNTO = 1000;

    private final CineFacade facade;

    public SalaAsientosController() {
        this.facade = new CineFacade();
    }

    public ArrayList<Funcion> obtenerFunciones() {
        return facade.obtenerFunciones();
    }

    /**
     * Indica si el asiento lo tiene alguien más en esta función.
     */
    public boolean estaOcupado(Funcion funcion, Asiento asiento) {
        return funcion.getAsientosOcupados().contains(asiento);
    }

    /**
     * Indica si el asiento se puede elegir (habilitado, libre y función no cancelada).
     */
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
