package org.example.proyectocine.model;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ReporteOcupacionSalas extends ReporteStrategy {

    private final List<Funcion> funcionesTotales;

    /**
     * metodo constructor
     * @param funcionesTotales de reporte de ocupacion de las salas
     */
    public ReporteOcupacionSalas(List<Funcion> funcionesTotales) {
        super("Reporte de Ocupación de Salas");
        this.funcionesTotales = funcionesTotales;
    }

    @Override
    public String generarContenidoTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append(construirEncabezado());

        sb.append(String.format("%-20s %-15s %-20s %-12s%n", "Sala", "Película", "Fecha/Hora", "Ocupación"));
        sb.append("\n");

        double sumaOcupacion = 0.0;
        int contador = 0;

        for (Funcion funcion : funcionesTotales) {
            double ocupacion = funcion.getOcupacion();
            String fechaStr = funcion.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            sb.append(String.format("%-20s %-15s %-20s %-10.2f%%%n",
                    funcion.getSala().getNombre(),
                    funcion.getPelicula().getTitulo(),
                    fechaStr,
                    ocupacion));

            sumaOcupacion += ocupacion;
            contador++;
        }

        double promedioOcupacion = (contador > 0) ? (sumaOcupacion / contador) : 0.0;

        sb.append("\n");
        sb.append(String.format("Promedio general de ocupación de salas: %.2f%%%n", promedioOcupacion));

        return sb.toString();
    }

    @Override
    public Map<String, Number> obtenerDatosGrafico() {
        Map<String, Number> datosGrafico = new LinkedHashMap<>();

        for (Funcion funcion : funcionesTotales) {
            String etiqueta = funcion.getSala().getNombre() + " - " + funcion.getPelicula().getTitulo();
            datosGrafico.put(etiqueta, funcion.getOcupacion());
        }
        return datosGrafico;
    }
}