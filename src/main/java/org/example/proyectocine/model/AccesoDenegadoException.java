package org.example.proyectocine.model;

/**
 * Se lanza cuando un usuario intenta una operación que no le corresponde
 * (por ejemplo, un cliente intentando recargar una tarjeta).
 */
public class AccesoDenegadoException extends CinemaException {
    public AccesoDenegadoException(String message) {
        super(message);
    }
}
