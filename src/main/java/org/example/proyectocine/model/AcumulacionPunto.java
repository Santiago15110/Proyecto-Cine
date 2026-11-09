package org.example.proyectocine.model;

import java.time.LocalDate;

/**
 * Puntos que acumula un cliente en un periodo de un año.
 * El periodo empieza al crearse y vence un año después; si los puntos
 * vencen, al sumar nuevos puntos se inicia un periodo nuevo desde cero.
 */
public class AcumulacionPunto {

    /**
     * Puntos acumulados en el periodo actual.
     */
    private int puntos;

    /**
     * Fecha en que empezó el periodo actual.
     */
    private LocalDate fechaInicio;

    /**
     * Fecha en que vence el periodo actual (un año después del inicio).
     */
    private LocalDate fechaVencimiento;

    /**
     * Cliente dueño de los puntos.
     */
    private Cliente theCliente;

    /**
     * Crea la acumulación de un cliente con 0 puntos y un periodo de un año desde hoy.
     *
     * @param theCliente cliente dueño de los puntos
     */
    public AcumulacionPunto(Cliente theCliente) {
        this.theCliente = theCliente;
        reiniciar();
    }

    /**
     * Indica si el periodo actual ya venció.
     *
     * @return {@code true} si la fecha de hoy es posterior a la fecha de vencimiento
     */
    public boolean estaVencida() {
        return LocalDate.now().isAfter(fechaVencimiento);
    }

    /**
     * Suma puntos al cliente. Si el periodo ya venció, primero lo reinicia
     * (puntos en 0 y un año nuevo desde hoy) y luego suma.
     *
     * @param cantidad puntos a sumar
     * @throws IllegalArgumentException si la cantidad es menor o igual a 0
     */
    public void sumarPuntos(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos debe ser mayor a 0");
        }
        if (estaVencida()) {
            reiniciar();
        }
        puntos += cantidad;
    }

    /**
     * Redime (gasta) puntos del cliente.
     *
     * @param cantidad puntos a redimir
     * @throws IllegalArgumentException si la cantidad es menor o igual a 0
     *                                  o si es mayor que los puntos disponibles
     * @throws IllegalStateException    si los puntos están vencidos
     */
    public void redimirPuntos(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos debe ser mayor a 0");
        }
        if (estaVencida()) {
            throw new IllegalStateException("Los puntos están vencidos");
        }
        if (cantidad > puntos) {
            throw new IllegalArgumentException("Puntos insuficientes");
        }
        puntos -= cantidad;
    }

    /**
     * Inicia un periodo nuevo: deja los puntos en 0 y pone el vencimiento un año después de hoy.
     */
    private void reiniciar() {
        this.puntos = 0;
        this.fechaInicio = LocalDate.now();
        this.fechaVencimiento = fechaInicio.plusYears(1);
    }

    /**
     * Obtiene los puntos disponibles.
     *
     * @return los puntos acumulados, o 0 si el periodo ya venció
     */
    public int getPuntos() {
        return estaVencida() ? 0 : puntos;
    }

    /**
     * Obtiene el cliente dueño de los puntos.
     *
     * @return el cliente
     */
    public Cliente getTheCliente() {
        return theCliente;
    }

    /**
     * Obtiene la fecha de vencimiento del periodo actual.
     *
     * @return la fecha de vencimiento
     */
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * Obtiene la fecha de inicio del periodo actual.
     *
     * @return la fecha de inicio
     */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
}
