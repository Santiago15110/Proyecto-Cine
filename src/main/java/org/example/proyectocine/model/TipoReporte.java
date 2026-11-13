package org.example.proyectocine.model;

import java.time.LocalDate;

/**
 * Tipos de reporte que puede elegir el administrador (patrón Factory Method).
 * <p>
 * Cada constante sobrescribe el método fábrica {@link #crear(Cine, LocalDate, LocalDate)} y decide
 * qué {@link ReporteStrategy} concreto construir y con qué datos. Así, quien pide un reporte
 * (el Facade o la pantalla) no necesita conocer las clases ReporteVentas, ReporteRecargas, etc.,
 * y agregar un reporte nuevo es agregar una constante, sin modificar a quien los usa.
 * </p>
 * <ul>
 *     <li>Producto: {@link ReporteStrategy}</li>
 *     <li>Productos concretos: ReporteVentas, ReporteEntradasFuncion, ...</li>
 *     <li>Creador: {@link TipoReporte} con el método fábrica {@code crear}</li>
 *     <li>Creadores concretos: cada constante (VENTAS, RECARGAS, ...)</li>
 * </ul>
 */
public enum TipoReporte {

    VENTAS("Ventas por periodo", true) {
        @Override
        public ReporteStrategy crear(Cine cine, LocalDate inicio, LocalDate fin) {
            return new ReporteVentas(inicio, fin, cine.getCompras());
        }
    },
    ENTRADAS_POR_FUNCION("Entradas vendidas por función", true) {
        @Override
        public ReporteStrategy crear(Cine cine, LocalDate inicio, LocalDate fin) {
            return new ReporteEntradasFuncion(inicio, fin, cine.getFunciones(), cine.getCompras());
        }
    },
    OCUPACION_SALAS("Ocupación de las salas", false) {
        @Override
        public ReporteStrategy crear(Cine cine, LocalDate inicio, LocalDate fin) {
            return new ReporteOcupacionSalas(cine.getFunciones());
        }
    },
    CONFITERIA("Productos y combos vendidos", true) {
        @Override
        public ReporteStrategy crear(Cine cine, LocalDate inicio, LocalDate fin) {
            return new ReporteConfiteria(inicio, fin, cine.getCompras());
        }
    },
    RECARGAS("Recargas realizadas", true) {
        @Override
        public ReporteStrategy crear(Cine cine, LocalDate inicio, LocalDate fin) {
            return new ReporteRecargas(inicio, fin, tarjetas(cine));
        }
    },
    CANCELACIONES("Cancelaciones y reembolsos", true) {
        @Override
        public ReporteStrategy crear(Cine cine, LocalDate inicio, LocalDate fin) {
            return new ReporteCancelaciones(inicio, fin, cine.getCompras(), tarjetas(cine));
        }
    },
    PUNTOS("Puntos acumulados y redimidos", true) {
        @Override
        public ReporteStrategy crear(Cine cine, LocalDate inicio, LocalDate fin) {
            return new ReportePuntos(inicio, fin, cine.getClientes());
        }
    };

    private final String nombre;
    private final boolean usaRangoFechas;

    TipoReporte(String nombre, boolean usaRangoFechas) {
        this.nombre = nombre;
        this.usaRangoFechas = usaRangoFechas;
    }

    /**
     * Método fábrica: crea el reporte concreto de este tipo.
     *
     * @param cine   datos del cine
     * @param inicio inicio del periodo (se ignora si el reporte no usa fechas)
     * @param fin    fin del periodo (se ignora si el reporte no usa fechas)
     * @return el reporte listo para generar texto o gráfico
     */
    public abstract ReporteStrategy crear(Cine cine, LocalDate inicio, LocalDate fin);

    private static java.util.ArrayList<TarjetaVirtual> tarjetas(Cine cine) {
        java.util.ArrayList<TarjetaVirtual> tarjetas = new java.util.ArrayList<>();
        for (Cliente c : cine.getClientes()) {
            tarjetas.add(c.getTheTarjeta());
        }
        return tarjetas;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Indica si la pantalla debe pedir rango de fechas para este reporte.
     *
     * @return true si usa fecha de inicio y fin
     */
    public boolean usaRangoFechas() {
        return usaRangoFechas;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
