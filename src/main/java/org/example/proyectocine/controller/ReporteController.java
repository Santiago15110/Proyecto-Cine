package org.example.proyectocine.controller;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import org.example.proyectocine.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteController {

    private final CineFacade cineFacade;

    public ReporteController() {
        this.cineFacade = new CineFacade();
    }

    /**
     * Procesa la creación de la estrategia de reporte utilizando la fachada y el tipo seleccionado.
     */
    public ReporteStrategy generarReporte(String tipoReporteStr, LocalDate inicio, LocalDate fin) {
        TipoReporte tipo = convertirATipoReporte(tipoReporteStr);
        return cineFacade.crearReporte(tipo, inicio, fin);
    }

    /**
     * Obtiene el contenido en texto plano del reporte.
     */
    public String obtenerTextoReporte(ReporteStrategy reporte) {
        return cineFacade.generarReporteTexto(reporte);
    }

    /**
     * Exporta el reporte actual a un archivo TXT.
     */
    public void exportarTxt(ReporteStrategy reporte, String ruta) {
        cineFacade.exportarReporteTXT(reporte, ruta);
    }

    /**
     * Exporta el reporte actual a un archivo PDF.
     */
    public void exportarPdf(ReporteStrategy reporte, String ruta) {
        cineFacade.exportarReportePDF(reporte, ruta);
    }

    /**
     * Genera el gráfico visual de barras.
     */
    public BarChart<String, Number> construirBarChart(ReporteStrategy reporte) {
        return ReporteVisual.crearBarChart(reporte, "Categoría / Elemento", "Valor / Monto");
    }

    /**
     * Genera el gráfico visual de pastel.
     */
    public PieChart construirPieChart(ReporteStrategy reporte) {
        return ReporteVisual.crearPieChart(reporte);
    }

    /**
     * Genera el gráfico visual de líneas.
     */
    public LineChart<String, Number> construirLineChart(ReporteStrategy reporte) {
        return ReporteVisual.crearLineChart(reporte, "Periodo", "Valor");
    }

    private TipoReporte convertirATipoReporte(String seleccion) {
        if (seleccion == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de reporte.");
        }
        switch (seleccion) {
            case "Reporte de Ventas":
                return TipoReporte.VENTAS;
            case "Reporte de Entradas por Función":
                return TipoReporte.ENTRADAS_POR_FUNCION;
            case "Reporte de Ocupación de Salas":
                return TipoReporte.OCUPACION_SALAS;
            case "Reporte de Confitería":
                return TipoReporte.CONFITERIA;
            case "Reporte de Recargas":
                return TipoReporte.RECARGAS;
            case "Reporte de Cancelaciones":
                return TipoReporte.CANCELACIONES;
            case "Reporte de Puntos":
                return TipoReporte.PUNTOS;
            default:
                throw new IllegalArgumentException("Tipo de reporte desconocido: " + seleccion);
        }
    }
}