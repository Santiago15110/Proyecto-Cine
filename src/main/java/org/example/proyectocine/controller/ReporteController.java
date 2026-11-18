package org.example.proyectocine.controller;

import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.ReporteStrategy;
import org.example.proyectocine.model.TipoReporte;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReporteController {

    private final CineFacade cineFacade;

    public ReporteController() {
        this.cineFacade = new CineFacade();
    }

    public List<TipoReporte> obtenerTiposReporte() {
        return List.of(TipoReporte.values());
    }

    public ReporteStrategy generarReporte(TipoReporte tipo, LocalDate inicio, LocalDate fin) {
        if (tipo == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de reporte.");
        }
        return cineFacade.crearReporte(tipo, inicio, fin);
    }

    public String obtenerTextoReporte(ReporteStrategy reporte) {
        return cineFacade.generarReporteTexto(reporte);
    }

    public void exportarTxt(ReporteStrategy reporte, String ruta) {
        cineFacade.exportarReporteTXT(reporte, ruta);
    }

    public void exportarPdf(ReporteStrategy reporte, String ruta) {
        cineFacade.exportarReportePDF(reporte, ruta);
    }

    /**
     * Nombre sugerido para guardar el archivo, por ejemplo "reporte_ventas_2026-11-17.pdf".
     *
     * @param tipo      tipo de reporte
     * @param extension "txt" o "pdf"
     * @return nombre de archivo sin espacios ni tildes
     */
    public String nombreSugerido(TipoReporte tipo, String extension) {
        String fecha = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return "reporte_" + tipo.name().toLowerCase() + "_" + fecha + "." + extension;
    }
}
