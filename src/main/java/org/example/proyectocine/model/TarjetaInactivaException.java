package org.example.proyectocine.model;

/**
 * Se lanza cuando se intenta pagar con una tarjeta virtual bloqueada.
 */
public class TarjetaInactivaException extends CinemaException {
    public TarjetaInactivaException(String message) {
        super(message);
    }
}
