package org.example.proyectocine.model;

import java.time.LocalDate;
import java.util.Objects;

public class Promocion {

    private final String id;
    private String nombre;
    private double porcentaje;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa;

    /**
     * Crea una promoción de descuento porcentual, activa por defecto
     * @param id identificador de la promoción
     * @param nombre nombre de la promoción
     * @param porcentaje porcentaje de descuento, mayor que 0 y hasta 100
     * @param fechaInicio primer día en que aplica
     * @param fechaFin último día en que aplica
     */
    public Promocion(String id, String nombre, double porcentaje, LocalDate fechaInicio, LocalDate fechaFin) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id de la promoción es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la promoción es obligatorio");
        }
        if (porcentaje <= 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe ser mayor que 0 y máximo 100");
        }
        if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("El rango de fechas de la promoción no es válido");
        }
        this.id = id;
        this.nombre = nombre;
        this.porcentaje = porcentaje;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activa = true;
    }

    /**
     * Verifica si la promoción se puede usar en una fecha
     * @param hoy fecha a consultar
     * @return true si está activa y la fecha está dentro de su rango
     */
    public boolean estaVigente(LocalDate hoy) {
        return activa && !hoy.isBefore(fechaInicio) && !hoy.isAfter(fechaFin);
    }

    /**
     * Envuelve un precio con el descuento de esta promoción (Decorator)
     * @param p precio al que se le aplica la promoción
     * @return nuevo precio con el descuento aplicado
     */
    public ComponentePrecio aplicar(ComponentePrecio p) {
        return new DescuentoPorcentaje(p, porcentaje);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la promoción es obligatorio");
        }
        this.nombre = nombre;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        if (porcentaje <= 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe ser mayor que 0 y máximo 100");
        }
        this.porcentaje = porcentaje;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("El rango de fechas de la promoción no es válido");
        }
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Promocion)) {
            return false;
        }
        return id.equals(((Promocion) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre + " (" + porcentaje + "%)";
    }
}
