package org.example.proyectocine.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Convierte el texto de un reporte en un documento PDF sin librerías externas.
 * Escribe el PDF a mano (formato PDF 1.4) con letra Courier para que las columnas
 * del reporte queden alineadas, y crea varias páginas si el reporte es largo.
 */
public class AdaptadorPDF {

    private static final Charset WINDOWS_1252 = Charset.forName("windows-1252");
    private static final int LINEAS_POR_PAGINA = 60;
    private static final int TAMANO_LETRA = 9;
    private static final int INTERLINEADO = 12;
    private static final int MARGEN_IZQUIERDO = 40;
    private static final int ALTO_PAGINA = 842;
    private static final int ANCHO_PAGINA = 595;

    /**
     * Genera el PDF del reporte en la ruta indicada.
     *
     * @param reporte     reporte a exportar
     * @param rutaArchivo ruta del archivo .pdf
     * @throws RuntimeException si no se puede escribir el archivo
     */
    public static void exportarPDF(ReporteStrategy reporte, String rutaArchivo) {
        if (reporte == null) {
            throw new IllegalArgumentException("El reporte es obligatorio");
        }
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo es obligatoria");
        }
        try {
            byte[] pdf = construirPdf(reporte.generarContenidoTexto());
            Files.write(Path.of(rutaArchivo), pdf);
        } catch (IOException e) {
            throw new RuntimeException("Error al exportar el reporte a PDF: " + e.getMessage());
        }
    }

    private static byte[] construirPdf(String texto) throws IOException {
        List<List<String>> paginas = dividirEnPaginas(texto);
        int cantidadPaginas = paginas.size();

        // Objetos: 1 catálogo, 2 árbol de páginas, 3 fuente, luego (página, contenido) por cada página
        List<byte[]> objetos = new ArrayList<>();
        objetos.add(ascii("<< /Type /Catalog /Pages 2 0 R >>"));

        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < cantidadPaginas; i++) {
            kids.append(4 + i * 2).append(" 0 R ");
        }
        objetos.add(ascii("<< /Type /Pages /Kids [ " + kids + "] /Count " + cantidadPaginas + " >>"));
        objetos.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>"));

        for (int i = 0; i < cantidadPaginas; i++) {
            int numeroContenido = 5 + i * 2;
            objetos.add(ascii("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + ANCHO_PAGINA + " " + ALTO_PAGINA
                    + "] /Resources << /Font << /F1 3 0 R >> >> /Contents " + numeroContenido + " 0 R >>"));
            byte[] flujo = contenidoPagina(paginas.get(i));
            ByteArrayOutputStream contenido = new ByteArrayOutputStream();
            contenido.write(ascii("<< /Length " + flujo.length + " >>\nstream\n"));
            contenido.write(flujo);
            contenido.write(ascii("\nendstream"));
            objetos.add(contenido.toByteArray());
        }

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        salida.write(ascii("%PDF-1.4\n"));
        List<Integer> posiciones = new ArrayList<>();
        for (int i = 0; i < objetos.size(); i++) {
            posiciones.add(salida.size());
            salida.write(ascii((i + 1) + " 0 obj\n"));
            salida.write(objetos.get(i));
            salida.write(ascii("\nendobj\n"));
        }
        int inicioXref = salida.size();
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n0 ").append(objetos.size() + 1).append("\n");
        xref.append("0000000000 65535 f \n");
        for (int posicion : posiciones) {
            xref.append(String.format("%010d 00000 n \n", posicion));
        }
        xref.append("trailer\n<< /Size ").append(objetos.size() + 1).append(" /Root 1 0 R >>\n");
        xref.append("startxref\n").append(inicioXref).append("\n%%EOF\n");
        salida.write(ascii(xref.toString()));
        return salida.toByteArray();
    }

    private static List<List<String>> dividirEnPaginas(String texto) {
        String[] lineas = (texto == null ? "" : texto).replace("\r", "").split("\n", -1);
        List<List<String>> paginas = new ArrayList<>();
        List<String> actual = new ArrayList<>();
        for (String linea : lineas) {
            if (actual.size() == LINEAS_POR_PAGINA) {
                paginas.add(actual);
                actual = new ArrayList<>();
            }
            actual.add(linea);
        }
        paginas.add(actual);
        return paginas;
    }

    private static byte[] contenidoPagina(List<String> lineas) throws IOException {
        ByteArrayOutputStream flujo = new ByteArrayOutputStream();
        int y = ALTO_PAGINA - 50;
        flujo.write(ascii("BT\n/F1 " + TAMANO_LETRA + " Tf\n" + INTERLINEADO + " TL\n"
                + MARGEN_IZQUIERDO + " " + y + " Td\n"));
        for (String linea : lineas) {
            flujo.write('(');
            flujo.write(escapar(linea).getBytes(WINDOWS_1252));
            flujo.write(ascii(") Tj T*\n"));
        }
        flujo.write(ascii("ET"));
        return flujo.toByteArray();
    }

    private static String escapar(String linea) {
        return linea.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)").replace("\t", "    ");
    }

    private static byte[] ascii(String s) {
        return s.getBytes(WINDOWS_1252);
    }
}
