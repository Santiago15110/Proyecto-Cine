package org.example.proyectocine.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Estrategia concreta para el reporte de puntos acumulados y redimidos por los clientes.
 */
public class ReportePuntos extends ReporteStrategy {

    private final List<Cliente> clientesTotales;

    public ReportePuntos(LocalDate fechaInicio, LocalDate fechaFin, List<Cliente> clientesTotales) {
        super("Reporte de Puntos CinemaUQ", fechaInicio, fechaFin);
        this.clientesTotales = clientesTotales;
    }

    @Override
    public String generarContenidoTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append(construirEncabezado());

        sb.append(String.format("%-15s %-22s %-15s %-15s%n", "Cédula", "Cliente", "Puntos Acum.", "Puntos Redim."));
        sb.append("\n");

        List<ResumenPuntosCliente> resumenClientes = procesarPuntosClientes();

        int totalAcumuladosGeneral = 0;
        int totalRedimidosGeneral = 0;

        for (ResumenPuntosCliente detalle : resumenClientes) {
            sb.append(String.format("%-15s %-22s %-15d %-15d%n",
                    detalle.cedula,
                    detalle.nombreCliente,
                    detalle.puntosAcumulados,
                    detalle.puntosRedidos));

            totalAcumuladosGeneral += detalle.puntosAcumulados;
            totalRedimidosGeneral += detalle.puntosRedidos;
        }

        sb.append("\n");
        sb.append(String.format("Total general puntos acumulados: %d%n", totalAcumuladosGeneral));
        sb.append(String.format("Total general puntos redimidos:  %d%n", totalRedimidosGeneral));

        return sb.toString();
    }

    @Override
    public Map<String, Number> obtenerDatosGrafico() {
        Map<String, Number> datosGrafico = new LinkedHashMap<>();
        List<ResumenPuntosCliente> resumenClientes = procesarPuntosClientes();

        for (ResumenPuntosCliente detalle : resumenClientes) {
            if (detalle.puntosAcumulados > 0) {
                datosGrafico.put(detalle.nombreCliente, detalle.puntosAcumulados);
            }
        }
        return datosGrafico;
    }

    private List<ResumenPuntosCliente> procesarPuntosClientes() {
        List<ResumenPuntosCliente> lista = new ArrayList<>();

        if (clientesTotales == null) return lista;

        for (Cliente cliente : clientesTotales) {
            int acumulados = 0;
            int redimidos = 0;
            AcumulacionPunto acumulacion = cliente.getTheAcumulacion();
            if (acumulacion != null) {

            }

            lista.add(new ResumenPuntosCliente(cliente.getCedula(), cliente.getNombre(), acumulados, redimidos));
        }

        return lista;
    }

    private static class ResumenPuntosCliente {
        String cedula;
        String nombreCliente;
        int puntosAcumulados;
        int puntosRedidos;

        public ResumenPuntosCliente(String cedula, String nombreCliente, int puntosAcumulados, int puntosRedidos) {
            this.cedula = cedula;
            this.nombreCliente = nombreCliente;
            this.puntosAcumulados = puntosAcumulados;
            this.puntosRedidos = puntosRedidos;
        }
    }
}