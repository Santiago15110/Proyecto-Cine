package org.example.proyectocine.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Solicitud de recarga que hace un cliente. El saldo solo se acredita
 * cuando un administrador la aprueba.
 */
public class SolicitudRecarga {

    private final String id;
    private final Cliente cliente;
    private final double monto;
    private final LocalDateTime fechaSolicitud;
    private EstadoSolicitud estado;
    private LocalDateTime fechaRespuesta;

    /**
     * Crea una solicitud en estado pendiente.
     *
     * @param id             identificador de la solicitud
     * @param cliente        cliente que pide la recarga
     * @param monto          monto a recargar, mayor que cero
     * @param fechaSolicitud momento en que se pidió
     */
    public SolicitudRecarga(String id, Cliente cliente, double monto, LocalDateTime fechaSolicitud) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id de la solicitud es obligatorio");
        }
        if (cliente == null || fechaSolicitud == null) {
            throw new IllegalArgumentException("El cliente y la fecha son obligatorios");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a recargar debe ser mayor a 0");
        }
        this.id = id;
        this.cliente = cliente;
        this.monto = monto;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    /**
     * Marca la solicitud como aprobada. La recarga del saldo la hace el Facade.
     *
     * @throws CinemaException si la solicitud ya fue respondida
     */
    public void aprobar() {
        validarPendiente();
        estado = EstadoSolicitud.APROBADA;
        fechaRespuesta = LocalDateTime.now();
    }

    /**
     * Marca la solicitud como rechazada.
     *
     * @throws CinemaException si la solicitud ya fue respondida
     */
    public void rechazar() {
        validarPendiente();
        estado = EstadoSolicitud.RECHAZADA;
        fechaRespuesta = LocalDateTime.now();
    }

    public boolean estaPendiente() {
        return estado == EstadoSolicitud.PENDIENTE;
    }

    private void validarPendiente() {
        if (!estaPendiente()) {
            throw new CinemaException("La solicitud " + id + " ya fue " + estado.getNombre().toLowerCase());
        }
    }

    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    /**
     * @return momento en que el administrador respondió, o null si sigue pendiente
     */
    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolicitudRecarga)) {
            return false;
        }
        return id.equals(((SolicitudRecarga) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Solicitud " + id + " - " + cliente.getNombre() + " - " + monto + " (" + estado + ")";
    }
}
