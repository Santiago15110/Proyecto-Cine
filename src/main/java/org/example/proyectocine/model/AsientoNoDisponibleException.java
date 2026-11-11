package org.example.proyectocine.model;

/**
 * Se lanza cuando se intenta reservar un asiento que no está disponible
 * para una función (ya vendido, deshabilitado o función no programada).
 */
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

