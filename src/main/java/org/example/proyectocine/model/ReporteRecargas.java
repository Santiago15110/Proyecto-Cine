package org.example.proyectocine.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ReporteRecargas extends ReporteStrategy {

    private final List<TarjetaVirtual> tarjetasTotales;

    /**
     *   Estrategia concreta para el reporte de recargas realizadas a las tarjetas virtuales
     * @param fechaInicio de recarga
     * @param fechaFin de recarga
     * @param tarjetasTotales de recarga
     */
    public ReporteRecargas(LocalDate fechaInicio, LocalDate fechaFin, List<TarjetaVirtual> tarjetasTotales) {
        super("Reporte de Recargas", fechaInicio, fechaFin);
        this.tarjetasTotales = tarjetasTotales;
    }

    @Override
    public String generarContenidoTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append(construirEncabezado());

        sb.append(String.format("%-15s %-20s %-12s %s%n", "Nro. Tarjeta", "Fecha", "Cliente", "Monto Recargado"));
        sb.append("\n");

        List<DetalleRecarga> recargasFiltradas = procesarRecargas();

        int totalRecargasCount = 0;
        double totalMontoRecargado = 0.0;

        for (DetalleRecarga detalle : recargasFiltradas) {
            sb.append(String.format("%-15s %-20s %-12s $%,12.2f%n",
                    detalle.numeroTarjeta,
                    detalle.fecha.toString(),
                    detalle.nombreCliente,
                    detalle.monto));

            totalRecargasCount++;
            totalMontoRecargado += detalle.monto;
        }

        sb.append("\n");
        sb.append(String.format("Total de recargas realizadas: %d%n", totalRecargasCount));
        sb.append(String.format("Total dinero recargado:       $%,12.2f%n", totalMontoRecargado));

        return sb.toString();
    }

    @Override
    public Map<String, Number> obtenerDatosGrafico() {
        Map<String, Number> datosGrafico = new LinkedHashMap<>();
        List<DetalleRecarga> recargas = procesarRecargas();

        Map<LocalDate, Double> acumuladoPorFecha = new HashMap<>();
        for (DetalleRecarga d : recargas) {
            acumuladoPorFecha.put(d.fecha, acumuladoPorFecha.getOrDefault(d.fecha, 0.0) + d.monto);
        }

        acumuladoPorFecha.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> datosGrafico.put(entry.getKey().toString(), entry.getValue()));

        return datosGrafico;
    }

    private List<DetalleRecarga> procesarRecargas() {
        List<DetalleRecarga> lista = new ArrayList<>();

        if (tarjetasTotales == null) return lista;

        for (TarjetaVirtual tarjeta : tarjetasTotales) {
            if (tarjeta.getListMovimientos() != null) {
                for (MovimientoSaldo mov : tarjeta.getListMovimientos()) {
                    if (mov.tipoMovimiento() == TipoMovimiento.RECARGA) {
                        LocalDate fechaMov = mov.fecha();

                        boolean dentroDeRango = true;
                        if (fechaInicio != null && fechaMov.isBefore(fechaInicio)) dentroDeRango = false;
                        if (fechaFin != null && fechaMov.isAfter(fechaFin)) dentroDeRango = false;

                        if (dentroDeRango) {
                            String clienteNombre = (tarjeta.getTheCliente() != null) ? tarjeta.getTheCliente().getNombre() : "N/A";
                            lista.add(new DetalleRecarga(tarjeta.getNumero(), fechaMov, clienteNombre, mov.monto()));
                        }
                    }
                }
            }
        }
        return lista;
    }

    private static class DetalleRecarga {
        String numeroTarjeta;
        LocalDate fecha;
        String nombreCliente;
        double monto;

        public DetalleRecarga(String numeroTarjeta, LocalDate fecha, String nombreCliente, double monto) {
            this.numeroTarjeta = numeroTarjeta;
            this.fecha = fecha;
            this.nombreCliente = nombreCliente;
            this.monto = monto;
        }
    }
}