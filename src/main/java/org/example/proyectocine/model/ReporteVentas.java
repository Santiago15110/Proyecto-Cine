package org.example.proyectocine.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteVentas extends ReporteStrategy{
    private final List<Compra> comprasTotales;

    public ReporteVentas(LocalDate fechaInicio, LocalDate fechaFin, List<Compra> comprasTotales) {
        super("Reporte de ventas", fechaInicio, fechaFin);
        this.comprasTotales = comprasTotales;
    }

    @Override
    public String generarContenidoTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append(construirEncabezado());
        sb.append(String.format("%-20s %-10s %s%n", "Película", "Entradas", "Total"));
        sb.append("\n");

        Map<String, DatosPeliculaReporte> resumenPeliculas = procesarVentasPorPelicula();
        int totalEntradasGeneral = 0;
        double totalVentasGeneral = 0.0;
        for (Map.Entry<String, DatosPeliculaReporte> entry : resumenPeliculas.entrySet()) {
            String tituloPelicula = entry.getKey();
            DatosPeliculaReporte datos = entry.getValue();

            sb.append(String.format("%-20s %-10d $%,12.2f%n",
                    tituloPelicula,
                    datos.entradas,
                    datos.totalDinero));

            totalEntradasGeneral += datos.entradas;
            totalVentasGeneral += datos.totalDinero;
        }
        sb.append("\n");
        sb.append(String.format("Total entradas: %d%n", totalEntradasGeneral));
        sb.append(String.format("Total ventas:   $%,12.2f%n", totalVentasGeneral));

        return sb.toString();
    }

    @Override
    public Map<String, Number> obtenerDatosGrafico() {
        Map<String, DatosPeliculaReporte> resumen = procesarVentasPorPelicula();
        Map<String, Number> datosGrafico = new LinkedHashMap<>();

        for (Map.Entry<String, DatosPeliculaReporte> entry : resumen.entrySet()) {
            datosGrafico.put(entry.getKey(), entry.getValue().totalDinero);
        }
        return datosGrafico;
    }

    /**
     * metodo interno para auxiliar el filtro de las fechas
     */
    private Map<String, DatosPeliculaReporte> procesarVentasPorPelicula() {
        Map<String, DatosPeliculaReporte> mapa = new HashMap<>();

        for (Compra compra : comprasTotales) {
            LocalDate fechaCompra = compra.getFecha().toLocalDate();
            boolean dentroDeRango = true;
            if (fechaInicio != null && fechaCompra.isBefore(fechaInicio)) dentroDeRango = false;
            if (fechaFin != null && fechaCompra.isAfter(fechaFin)) dentroDeRango = false;

            if (dentroDeRango && compra.getFuncion() != null) {
                String nombrePelicula = compra.getFuncion().getPelicula().getTitulo();
                int cantEntradas = compra.getEntradas().size();
                double valorCompra = compra.getTotal();

                mapa.putIfAbsent(nombrePelicula, new DatosPeliculaReporte());
                DatosPeliculaReporte datos = mapa.get(nombrePelicula);
                datos.entradas += cantEntradas;
                datos.totalDinero += valorCompra;
            }
        }
        return mapa;
    }

    /**
     * Clase auxiliar interna para acumular métricas por película.
     */
    private static class DatosPeliculaReporte {
        int entradas = 0;
        double totalDinero = 0.0;
    }
}
