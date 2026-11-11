package org.example.proyectocine.model;

/**
 * Excepción base de todas las excepciones propias del dominio de CinemaUQ.
 */
public class CinemaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor de la excepción base del dominio
     * @param msg mensaje que describe el error
     */
    public CinemaException(String msg) {
        super(msg);
    }
}