package org.example.proyectocine.model;

/**
 * Estado de una solicitud de recarga hecha por un cliente.
 */
public enum EstadoSolicitud {

    PENDIENTE("Pendiente"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada");

    private final String nombre;

    EstadoSolicitud(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
