package org.example.proyectocine.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ReporteCancelaciones extends ReporteStrategy {

    private final List<Compra> comprasTotales;
    private final List<TarjetaVirtual> tarjetasTotales;

    /**
     * metodo contructor
     * @param fechaInicio de reporte de cancelacion
     * @param fechaFin de reporte de cancelacion
     * @param comprasTotales de reporte de cancelacion
     * @param tarjetasTotales de reporte de cancelacion
     */
    public ReporteCancelaciones(LocalDate fechaInicio, LocalDate fechaFin,
                                List<Compra> comprasTotales, List<TarjetaVirtual> tarjetasTotales) {
        super("Reporte de Cancelaciones y Reembolsos", fechaInicio, fechaFin);
        this.comprasTotales = comprasTotales;
        this.tarjetasTotales = tarjetasTotales;
    }

    @Override
    public String generarContenidoTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append(construirEncabezado());

        sb.append(String.format("%-12s %-12s %-20s %s%n", "ID Compra", "Fecha", "Cliente", "Monto Reembolsado"));
        sb.append("\n");

        List<DetalleReembolso> reembolsosFiltrados = procesarReembolsos();

        int totalCancelacionesCount = 0;
        double totalMontoReembolsado = 0.0;

        for (DetalleReembolso detalle : reembolsosFiltrados) {
            sb.append(String.format("%-12s %-12s %-20s $%,12.2f%n",
                    detalle.idCompra,
                    detalle.fecha.toString(),
                    detalle.nombreCliente,
                    detalle.monto));

            totalCancelacionesCount++;
            totalMontoReembolsado += detalle.monto;
        }

        sb.append("\n");
        sb.append(String.format("Total cancelaciones/reembolsos: %d%n", totalCancelacionesCount));
        sb.append(String.format("Total dinero reembolsado:       $%,12.2f%n", totalMontoReembolsado));

        return sb.toString();
    }

    @Override
    public Map<String, Number> obtenerDatosGrafico() {

        Map<String, Number> datosGrafico = new LinkedHashMap<>();
        List<DetalleReembolso> reembolsos = procesarReembolsos();

        Map<LocalDate, Double> montoPorFecha = new LinkedHashMap<>();
        for (DetalleReembolso d : reembolsos) {
            montoPorFecha.put(d.fecha, montoPorFecha.getOrDefault(d.fecha, 0.0) + d.monto);
        }

        montoPorFecha.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> datosGrafico.put(entry.getKey().toString(), entry.getValue()));

        return datosGrafico;
    }

    private List<DetalleReembolso> procesarReembolsos() {
        List<DetalleReembolso> lista = new ArrayList<>();

        if (tarjetasTotales == null) return lista;
        for (TarjetaVirtual tarjeta : tarjetasTotales) {
            if (tarjeta.getListMovimientos() != null) {
                for (MovimientoSaldo mov : tarjeta.getListMovimientos()) {
                    if (mov.tipoMovimiento() == TipoMovimiento.REEMBOLSO) {
                        LocalDate fechaMov = mov.fecha();

                        boolean dentroDeRango = true;
                        if (fechaInicio != null && fechaMov.isBefore(fechaInicio)) dentroDeRango = false;
                        if (fechaFin != null && fechaMov.isAfter(fechaFin)) dentroDeRango = false;

                        if (dentroDeRango) {
                            String clienteNombre = (tarjeta.getTheCliente() != null) ? tarjeta.getTheCliente().getNombre() : "N/A";
                            String descripcion = mov.descripcion();
                            String idCompra = (descripcion != null && !descripcion.isBlank()) ? descripcion : "N/A";

                            lista.add(new DetalleReembolso(idCompra, fechaMov, clienteNombre, mov.monto()));
                        }
                    }
                }
            }
        }
        return lista;
    }

    private static class DetalleReembolso {
        String idCompra;
        LocalDate fecha;
        String nombreCliente;
        double monto;

        public DetalleReembolso(String idCompra, LocalDate fecha, String nombreCliente, double monto) {
            this.idCompra = idCompra;
            this.fecha = fecha;
            this.nombreCliente = nombreCliente;
            this.monto = monto;
        }
    }
}