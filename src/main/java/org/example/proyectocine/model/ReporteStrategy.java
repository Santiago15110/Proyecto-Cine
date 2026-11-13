package org.example.proyectocine.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public abstract class ReporteStrategy {
    protected final String titulo;
    protected final LocalDate fechaGeneracion;
    protected final LocalDate fechaInicio;
    protected final LocalDate fechaFin;

    protected static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * contructor de Reporte entre un rango de fechas
     * @param titulo de reporte
     * @param fechaInicio de reporte
     * @param fechaFin de reporte
     */
    public ReporteStrategy(String titulo, LocalDate fechaInicio, LocalDate fechaFin) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título del reporte es obligatorio.");
        }
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin.");
        }
        this.titulo = titulo;
        this.fechaGeneracion = LocalDate.now();
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    /**
     * contructor de reporte sin filtro
     * @param titulo de reporte
     */
    public ReporteStrategy(String titulo) {
        this(titulo, null, null);
    }

    /**
     * Genera el reporte en formato de texto plano tabular,
     */
    public abstract String generarContenidoTexto();

    /**
     * Devuelve los datos procesados en pares clave-valor
     */
    public abstract Map<String, Number> obtenerDatosGrafico();

    /**
     * Construye el encabezado.
     */
    protected String construirEncabezado() {
        StringBuilder sb = new StringBuilder();
        sb.append(titulo).append(" - CinemaUQ\n");
        if (fechaInicio != null && fechaFin != null) {
            sb.append("Periodo: ")
                    .append(fechaInicio.format(FORMATO_FECHA))
                    .append(" al ")
                    .append(fechaFin.format(FORMATO_FECHA))
                    .append("\n");
        }
        sb.append("Fecha de generación: ").append(fechaGeneracion.format(FORMATO_FECHA)).append("\n\n");
        return sb.toString();
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }
}
