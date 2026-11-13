package org.example.proyectocine.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ReporteConfiteria extends ReporteStrategy {

    private final List<Compra> comprasTotales;

    /**
     * metodo constructor
     * @param fechaInicio de reporte de confiteria
     * @param fechaFin de reporte de confiteria
     * @param comprasTotales de reporte de confiteria
     */
    public ReporteConfiteria(LocalDate fechaInicio, LocalDate fechaFin, List<Compra> comprasTotales) {
        super("Reporte de Confitería", fechaInicio, fechaFin);
        this.comprasTotales = comprasTotales;
    }

    @Override
    public String generarContenidoTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append(construirEncabezado());

        sb.append(String.format("%-25s %-10s %s%n", "Producto / Combo", "Cantidad", "Subtotal"));
        sb.append("\n");

        Map<String, DatosConfiteriaReporte> resumenConfiteria = procesarVentasConfiteria();

        int totalUnidadesGeneral = 0;
        double totalDineroGeneral = 0.0;

        for (Map.Entry<String, DatosConfiteriaReporte> entry : resumenConfiteria.entrySet()) {
            String nombreItem = entry.getKey();
            DatosConfiteriaReporte datos = entry.getValue();

            sb.append(String.format("%-25s %-10d $%,12.2f%n",
                    nombreItem,
                    datos.cantidadTotal,
                    datos.dineroTotal));

            totalUnidadesGeneral += datos.cantidadTotal;
            totalDineroGeneral += datos.dineroTotal;
        }

        sb.append("\n");
        sb.append(String.format("Total unidades vendidas: %d%n", totalUnidadesGeneral));
        sb.append(String.format("Total recaudado:         $%,12.2f%n", totalDineroGeneral));

        return sb.toString();
    }

    @Override
    public Map<String, Number> obtenerDatosGrafico() {
        Map<String, DatosConfiteriaReporte> resumen = procesarVentasConfiteria();
        Map<String, Number> datosGrafico = new LinkedHashMap<>();

        for (Map.Entry<String, DatosConfiteriaReporte> entry : resumen.entrySet()) {
            datosGrafico.put(entry.getKey(), entry.getValue().cantidadTotal);
        }
        return datosGrafico;
    }

    private Map<String, DatosConfiteriaReporte> procesarVentasConfiteria() {
        Map<String, DatosConfiteriaReporte> mapa = new HashMap<>();

        for (Compra compra : comprasTotales) {
            LocalDate fechaCompra = compra.getFecha().toLocalDate();

            boolean dentroDeRango = true;
            if (fechaInicio != null && fechaCompra.isBefore(fechaInicio)) dentroDeRango = false;
            if (fechaFin != null && fechaCompra.isAfter(fechaFin)) dentroDeRango = false;

            // Solo cuentan las compras pagadas (no las canceladas)
            if (dentroDeRango && compra.getEstado() instanceof EstadoPagada && compra.getLineas() != null) {
                for (LineaProducto linea : compra.getLineas()) {
                    String nombreItem = linea.getItem().getNombre();
                    int cantidad = linea.getCantidad();
                    double subtotal = linea.getSubtotal();

                    mapa.putIfAbsent(nombreItem, new DatosConfiteriaReporte());
                    DatosConfiteriaReporte datos = mapa.get(nombreItem);
                    datos.cantidadTotal += cantidad;
                    datos.dineroTotal += subtotal;
                }
            }
        }
        return mapa;
    }

    private static class DatosConfiteriaReporte {
        int cantidadTotal = 0;
        double dineroTotal = 0.0;
    }
}