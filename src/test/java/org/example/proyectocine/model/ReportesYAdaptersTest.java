package org.example.proyectocine.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Reportes (Factory Method + Strategy), exportación y pasarela (Adapter)")
class ReportesYAdaptersTest extends EscenarioCine {

    private final LocalDate ayer = LocalDate.now().minusDays(1);
    private final LocalDate manana = LocalDate.now().plusDays(1);

    @Test
    @DisplayName("Factory Method: cada tipo de reporte crea su estrategia concreta")
    void cadaTipoCreaSuReporte() {
        comoAdmin();

        assertInstanceOf(ReporteVentas.class, facade.crearReporte(TipoReporte.VENTAS, ayer, manana));
        assertInstanceOf(ReporteEntradasFuncion.class, facade.crearReporte(TipoReporte.ENTRADAS_POR_FUNCION, ayer, manana));
        assertInstanceOf(ReporteOcupacionSalas.class, facade.crearReporte(TipoReporte.OCUPACION_SALAS, null, null));
        assertInstanceOf(ReporteConfiteria.class, facade.crearReporte(TipoReporte.CONFITERIA, ayer, manana));
        assertInstanceOf(ReporteRecargas.class, facade.crearReporte(TipoReporte.RECARGAS, ayer, manana));
        assertInstanceOf(ReporteCancelaciones.class, facade.crearReporte(TipoReporte.CANCELACIONES, ayer, manana));
        assertInstanceOf(ReportePuntos.class, facade.crearReporte(TipoReporte.PUNTOS, ayer, manana));
    }

    @Test
    @DisplayName("El reporte de ventas no cuenta las compras canceladas")
    void ventasSinCanceladas() {
        facade.comprarEntradas(CEDULA_ANA, "F48H", asientos("A1", "A2"));
        Compra cancelada = facade.comprarEntradas(CEDULA_ANA, "F48H", asientos("B1"));
        facade.cancelarCompra(cancelada.getId());

        comoAdmin();
        String texto = facade.generarReporteTexto(facade.crearReporte(TipoReporte.VENTAS, ayer, manana));

        assertTrue(texto.contains("Total entradas: 2"));
        assertTrue(texto.contains("Dune"));
    }

    @Test
    @DisplayName("Los reportes incluyen título, periodo y fecha de generación")
    void encabezadoDelReporte() {
        comoAdmin();
        String texto = facade.generarReporteTexto(facade.crearReporte(TipoReporte.RECARGAS, ayer, manana));

        assertTrue(texto.contains("CinemaUQ"));
        assertTrue(texto.contains("Periodo:"));
        assertTrue(texto.contains("Fecha de generación:"));
    }

    @Test
    @DisplayName("Exportar a TXT y a PDF crea los archivos")
    void exportarArchivos(@TempDir Path carpeta) throws Exception {
        facade.comprarEntradas(CEDULA_ANA, "F48H", asientos("A1"));
        comoAdmin();
        ReporteStrategy reporte = facade.crearReporte(TipoReporte.VENTAS, ayer, manana);

        Path txt = carpeta.resolve("ventas.txt");
        Path pdf = carpeta.resolve("ventas.pdf");
        facade.exportarReporteTXT(reporte, txt.toString());
        facade.exportarReportePDF(reporte, pdf.toString());

        assertTrue(Files.readString(txt).contains("Dune"));
        byte[] bytes = Files.readAllBytes(pdf);
        assertTrue(new String(bytes, 0, 8).startsWith("%PDF-1.4"));
        assertTrue(new String(bytes).trim().endsWith("%%EOF"));
    }

    @Test
    @DisplayName("Adapter: la pasarela adapta el banco externo a la interfaz IPasarelaPago")
    void adapterDePasarela() {
        IPasarelaPago pasarela = new PasarelaPagoAdapter();

        assertTrue(pasarela.procesarPago("1001", 5000));
    }

    @Test
    @DisplayName("Si la pasarela rechaza el pago, la recarga no se aplica")
    void pasarelaRechaza() {
        CineFacade facadeConBancoCaido = new CineFacade((referencia, monto) -> false);
        comoAdmin();

        assertThrows(CinemaException.class, () -> facadeConBancoCaido.recargarTarjeta(CEDULA_ANA, 5000));
        assertEquals(100000, facade.consultarSaldo(CEDULA_ANA));
    }

    @Test
    @DisplayName("Singleton: todas las partes del sistema usan el mismo Cine")
    void singleton() {
        CineFacade otroFacade = new CineFacade();
        otroFacade.registrarCliente("Sofía", "sofia@test.com", "1234", "3003");

        assertSame(Cine.getInstancia(), cine);
        assertEquals("Sofía", facade.buscarCliente("3003").getNombre());
    }

    @Test
    @DisplayName("Los datos iniciales se cargan sin errores y dejan los reportes con información")
    void datosIniciales() {
        Cine.reiniciar();
        DatosIniciales.cargar();
        CineFacade nuevo = new CineFacade();
        nuevo.iniciarSesion(DatosIniciales.CORREO_ADMIN, DatosIniciales.CLAVE_ADMIN);

        assertFalse(nuevo.obtenerCompras().isEmpty());
        assertTrue(nuevo.generarReporteTexto(nuevo.crearReporte(TipoReporte.VENTAS, ayer, manana)).contains("Total ventas"));
    }
}
