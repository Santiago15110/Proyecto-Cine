package org.example.proyectocine.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class PoliticaReembolso {

    private static final long MINUTOS_REEMBOLSO_TOTAL = 24 * 60;
    private static final long MINUTOS_MINIMOS_CANCELACION = 2 * 60;
    private static final double PORCENTAJE_PARCIAL = 80;

    /**
     * Calcula el porcentaje que se devuelve al cancelar una compra.
     *
     * @param funcion función de la compra (null si la compra solo tiene confitería)
     * @param ahora   momento de la cancelación
     * @return porcentaje a devolver (100 u 80)
     * @throws CancelacionNoPermitidaException si faltan menos de 2 horas para la función
     */
    public double porcentajeReembolso(Funcion funcion, LocalDateTime ahora) {
        if (funcion == null) {
            return 100;
        }
        long minutosRestantes = Duration.between(ahora, funcion.getFechaHora()).toMinutes();
        if (minutosRestantes > MINUTOS_REEMBOLSO_TOTAL) {
            return 100;
        }
        if (minutosRestantes >= MINUTOS_MINIMOS_CANCELACION) {
            return PORCENTAJE_PARCIAL;
        }
        throw new CancelacionNoPermitidaException(
                "No se puede cancelar: faltan menos de 2 horas para la función");
    }
}
