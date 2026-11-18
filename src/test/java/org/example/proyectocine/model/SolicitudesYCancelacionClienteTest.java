package org.example.proyectocine.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Solicitudes de recarga y cancelación desde Mis compras")
class SolicitudesYCancelacionClienteTest extends EscenarioCine {

    @Test
    @DisplayName("Solicitar una recarga no cambia el saldo hasta que el admin la apruebe")
    void solicitudQuedaPendiente() {
        SolicitudRecarga solicitud = facade.solicitarRecarga(CEDULA_LUIS, 30000);

        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
        assertEquals(5000, facade.consultarSaldo(CEDULA_LUIS));
        assertEquals(1, facade.obtenerSolicitudesCliente(CEDULA_LUIS).size());
    }

    @Test
    @DisplayName("El admin ve las pendientes y al aprobar se recarga el saldo con su movimiento")
    void aprobarRecarga() {
        SolicitudRecarga solicitud = facade.solicitarRecarga(CEDULA_LUIS, 30000);
        comoAdmin();

        assertEquals(1, facade.obtenerSolicitudesPendientes().size());
        facade.aprobarSolicitudRecarga(solicitud.getId());

        assertEquals(EstadoSolicitud.APROBADA, solicitud.getEstado());
        assertNotNull(solicitud.getFechaRespuesta());
        assertEquals(35000, facade.consultarSaldo(CEDULA_LUIS));
        assertTrue(facade.obtenerMovimientos(CEDULA_LUIS).get(0).descripcion().contains(solicitud.getId()));
        assertTrue(facade.obtenerSolicitudesPendientes().isEmpty());
    }

    @Test
    @DisplayName("Al rechazar, el saldo no cambia")
    void rechazarRecarga() {
        SolicitudRecarga solicitud = facade.solicitarRecarga(CEDULA_LUIS, 30000);
        comoAdmin();

        facade.rechazarSolicitudRecarga(solicitud.getId());

        assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado());
        assertEquals(5000, facade.consultarSaldo(CEDULA_LUIS));
    }

    @Test
    @DisplayName("Una solicitud ya respondida no se puede aprobar ni rechazar otra vez")
    void noSeRespondeDosVeces() {
        SolicitudRecarga solicitud = facade.solicitarRecarga(CEDULA_LUIS, 30000);
        comoAdmin();
        facade.aprobarSolicitudRecarga(solicitud.getId());

        assertThrows(CinemaException.class, () -> facade.aprobarSolicitudRecarga(solicitud.getId()));
        assertThrows(CinemaException.class, () -> facade.rechazarSolicitudRecarga(solicitud.getId()));
        assertEquals(35000, facade.consultarSaldo(CEDULA_LUIS));
    }

    @Test
    @DisplayName("Solo el administrador puede ver y responder solicitudes")
    void soloAdminResponde() {
        SolicitudRecarga solicitud = facade.solicitarRecarga(CEDULA_LUIS, 30000);
        comoCliente("luis@test.com");

        assertThrows(AccesoDenegadoException.class, () -> facade.obtenerSolicitudesPendientes());
        assertThrows(AccesoDenegadoException.class, () -> facade.aprobarSolicitudRecarga(solicitud.getId()));
    }

    @Test
    @DisplayName("Si la pasarela rechaza el pago, la solicitud sigue pendiente")
    void pasarelaRechazaAprobacion() {
        SolicitudRecarga solicitud = facade.solicitarRecarga(CEDULA_LUIS, 30000);
        CineFacade bancoCaido = new CineFacade((referencia, monto) -> false);
        comoAdmin();

        assertThrows(CinemaException.class, () -> bancoCaido.aprobarSolicitudRecarga(solicitud.getId()));
        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
        assertEquals(5000, facade.consultarSaldo(CEDULA_LUIS));
    }

    @Test
    @DisplayName("Mis compras muestra el porcentaje que se devolvería antes de cancelar")
    void porcentajeAntesDeCancelar() {
        Compra a48h = facade.comprarEntradas(CEDULA_ANA, "F48H", asientos("A1"));
        Compra a10h = facade.comprarEntradas(CEDULA_ANA, "F10H", asientos("A1"));
        Compra a1h = facade.comprarEntradas(CEDULA_ANA, "F1H", asientos("A1"));

        assertEquals(100, facade.consultarPorcentajeReembolso(a48h.getId()));
        assertEquals(80, facade.consultarPorcentajeReembolso(a10h.getId()));
        assertThrows(CancelacionNoPermitidaException.class, () -> facade.consultarPorcentajeReembolso(a1h.getId()));
        assertInstanceOf(EstadoPagada.class, a48h.getEstado());
    }

    @Test
    @DisplayName("Un cliente no puede cancelar la compra de otro cliente")
    void noCancelaCompraAjena() {
        Compra compraDeAna = facade.comprarEntradas(CEDULA_ANA, "F48H", asientos("A1"));

        assertThrows(AccesoDenegadoException.class, () -> facade.cancelarCompraCliente(CEDULA_LUIS, compraDeAna.getId()));
        facade.cancelarCompraCliente(CEDULA_ANA, compraDeAna.getId());
        assertInstanceOf(EstadoCancelada.class, compraDeAna.getEstado());
    }
}
