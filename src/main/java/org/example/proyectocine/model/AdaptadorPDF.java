package org.example.proyectocine.model;

import java.io.File;

public class AdaptadorPDF {

    public static void exportarPDF(ReporteStrategy reporte, String rutaArchivo) {
        String contenido = reporte.generarContenidoTexto();

        try {
            System.out.println("Generando PDF para: " + reporte.getTitulo());
            System.out.println("Guardando en: " + rutaArchivo);
            File archivo = new File(rutaArchivo);
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar el reporte a PDF: " + e.getMessage());
        }
    }
}