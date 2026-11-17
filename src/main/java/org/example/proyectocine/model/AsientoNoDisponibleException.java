package org.example.proyectocine.model;

public class AsientoNoDisponibleException extends CinemaException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor de la excepción de asiento no disponible
     * @param msg mensaje que describe el error
     */
    public AsientoNoDisponibleException(String msg) {
        super(msg);
    }
}

