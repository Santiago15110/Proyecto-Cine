package org.example.proyectocine.model;

/**
 * Se lanza cuando se intenta cancelar una función o una compra
 * que no admite cancelación en su estado actual.
 */
public class CancelacionNoPermitidaException extends CinemaException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor de la excepción de cancelación no permitida
     * @param msg mensaje que describe el error
     */
    public CancelacionNoPermitidaException(String msg) {
        super(msg);
    }
}
