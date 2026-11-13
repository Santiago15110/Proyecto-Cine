package org.example.proyectocine.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExportadorTXT {

    public static void exportar(ReporteStrategy reporte, String rutaArchivo) throws IOException {
        String contenido = reporte.generarContenidoTexto();
        Path ruta = Path.of(rutaArchivo);
        Files.writeString(ruta, contenido);
    }
}