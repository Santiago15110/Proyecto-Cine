package org.example.proyectocine.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record MovimientoSaldo(TipoMovimiento tipoMovimiento, double monto, LocalDate fecha, LocalTime hora, double saldoResultante, String descripcion) {


}
