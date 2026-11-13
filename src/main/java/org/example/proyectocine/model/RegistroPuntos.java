package org.example.proyectocine.model;

import java.time.LocalDate;

/**
 * Una acumulación de puntos con su propia fecha de obtención y de vencimiento (un año después).
 * Ejemplo: 300 puntos obtenidos el 10/09/2026 que vencen el 10/09/2027.
 */
public class RegistroPuntos {

    private final int puntosObtenidos;
    private int puntosDisponibles;
    private final LocalDate fechaObtencion;
    private final LocalDate fechaVencimiento;
    private final String referencia;

    /**
     * Crea una acumulación de puntos.
     *
     * @param puntos         puntos obtenidos
     * @param fechaObtencion fecha en que se obtuvieron
     * @param referencia     de dónde salieron (por ejemplo el id de la compra)
     */
    public RegistroPuntos(int puntos, LocalDate fechaObtencion, String referencia) {
        if (puntos <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos debe ser mayor a 0");
        }
        if (fechaObtencion == null) {
            throw new IllegalArgumentException("La fecha de obtención es obligatoria");
        }
        this.puntosObtenidos = puntos;
        this.puntosDisponibles = puntos;
        this.fechaObtencion = fechaObtencion;
        this.fechaVencimiento = fechaObtencion.plusYears(1);
        this.referencia = referencia;
    }

    public boolean estaVencido(LocalDate hoy) {
        return hoy.isAfter(fechaVencimiento);
    }

    /**
     * Gasta puntos de esta acumulación.
     *
     * @param cantidad puntos a gastar (máximo los disponibles)
     */
    void usar(int cantidad) {
        if (cantidad < 0 || cantidad > puntosDisponibles) {
            throw new IllegalArgumentException("Cantidad de puntos no válida");
        }
        puntosDisponibles -= cantidad;
    }

    /**
     * Anula los puntos que quedan sin usar (por ejemplo, si se cancela la compra que los dio).
     *
     * @return los puntos que se anularon
     */
    int anular() {
        int anulados = puntosDisponibles;
        puntosDisponibles = 0;
        return anulados;
    }

    public int getPuntosObtenidos() {
        return puntosObtenidos;
    }

    public int getPuntosDisponibles() {
        return puntosDisponibles;
    }

    public LocalDate getFechaObtencion() {
        return fechaObtencion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getReferencia() {
        return referencia;
    }
}
