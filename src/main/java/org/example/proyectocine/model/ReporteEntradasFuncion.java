package org.example.proyectocine.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ReporteEntradasFuncion extends ReporteStrategy {

    private final List<Funcion> funcionesTotales;
    private final List<Compra> comprasTotales;

    public ReporteEntradasFuncion(LocalDate fechaInicio, LocalDate fechaFin, List<Funcion> funcionesTotales, List<Compra> comprasTotales) {
        super("Reporte de Entradas por Función", fechaInicio, fechaFin);
        this.funcionesTotales = funcionesTotales;
        this.comprasTotales = comprasTotales;
    }

    @Override
    public String generarContenidoTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append(construirEncabezado());

        sb.append(String.format("%-25s %-20s %-10s%n", "Película", "Fecha/Hora", "Vendidas"));
        sb.append("\n");

        Map<String, Integer> ventasPorFuncion = procesarEntradasPorFuncion();
        int totalEntradas = 0;

        for (Funcion funcion : funcionesTotales) {
            String claveFuncion = funcion.getPelicula().getTitulo() + " (" + funcion.getId() + ")";
            int vendidas = ventasPorFuncion.getOrDefault(funcion.getId(), 0);
            String fechaStr = funcion.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            sb.append(String.format("%-25s %-20s %-10d%n",
                    funcion.getPelicula().getTitulo(),
                    fechaStr,
                    vendidas));

            totalEntradas += vendidas;
        }

        sb.append("\n");
        sb.append(String.format("Total de entradas vendidas: %d%n", totalEntradas));

        return sb.toString();
    }

    @Override
    public Map<String, Number> obtenerDatosGrafico() {
        Map<String, Number> datosGrafico = new LinkedHashMap<>();
        Map<String, Integer> ventasPorFuncion = procesarEntradasPorFuncion();

        for (Funcion funcion : funcionesTotales) {
            String etiqueta = funcion.getPelicula().getTitulo() + " [" + funcion.getFechaHora().toLocalDate() + "]";
            int vendidas = ventasPorFuncion.getOrDefault(funcion.getId(), 0);
            datosGrafico.put(etiqueta, vendidas);
        }
        return datosGrafico;
    }

    /**
     * metodo auxiliar para el filtro de las fechas
     * @return
     */
    private Map<String, Integer> procesarEntradasPorFuncion() {
        Map<String, Integer> mapa = new HashMap<>();

        for (Compra compra : comprasTotales) {
            LocalDate fechaCompra = compra.getFecha().toLocalDate();
            boolean dentroDeRango = (fechaInicio == null || !fechaCompra.isBefore(fechaInicio))
                    && (fechaFin == null || !fechaCompra.isAfter(fechaFin));
            // Solo cuentan las compras pagadas (no las canceladas) dentro del periodo
            if (dentroDeRango && compra.getEstado() instanceof EstadoPagada
                    && compra.getFuncion() != null && !compra.getEntradas().isEmpty()) {
                String idFuncion = compra.getFuncion().getId();
                int cantidadEntradas = compra.getEntradas().size();

                mapa.put(idFuncion, mapa.getOrDefault(idFuncion, 0) + cantidadEntradas);
            }
        }
        return mapa;
    }
}
