package org.example.proyectocine.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cancelaciones y reembolsos")
class CancelacionesTest extends EscenarioCine {

    @Test
    @DisplayName("Con más de 24 horas se devuelve el 100 % y se liberan los asientos")
    void masDe24HorasDevuelveTodo() {
        Compra compra = facade.comprarEntradas(CEDULA_ANA, "F48H", asientos("A1"));

        facade.cancelarCompra(compra.getId());

        assertEquals(100000, facade.consultarSaldo(CEDULA_ANA));
        assertInstanceOf(EstadoCancelada.class, compra.getEstado());
        assertEquals(40, facade.consultarAsientosDisponibles("F48H").size());
    }

    @Test
    @DisplayName("Entre 2 y 24 horas se devuelve el 80 %")
    void entre2y24HorasDevuelve80() {
        Compra compra = facade.comprarEntradas(CEDULA_ANA, "F10H", asientos("A1"));

        facade.cancelarCompra(compra.getId());

        assertEquals(100000 - 10000 + 8000, facade.consultarSaldo(CEDULA_ANA));
    }

    @Test
    @DisplayName("Con menos de 2 horas no se permite cancelar")
    void menosDe2HorasNoSePermite() {
        Compra compra = facade.comprarEntradas(CEDULA_ANA, "F1H", asientos("A1"));

        assertThrows(CancelacionNoPermitidaException.class, () -> facade.cancelarCompra(compra.getId()));
        assertInstanceOf(EstadoPagada.class, compra.getEstado());
    }

    @Test
    @DisplayName("Un reembolso no se puede ejecutar dos veces sobre la misma compra")
    void noHayDobleReembolso() {
        Compra compra = facade.comprarEntradas(CEDULA_ANA, "F48H", asientos("A1"));
        facade.cancelarCompra(compra.getId());

        assertThrows(CancelacionNoPermitidaException.class, () -> facade.cancelarCompra(compra.getId()));
        assertEquals(100000, facade.consultarSaldo(CEDULA_ANA));
    }

    @Test
    @DisplayName("Al cancelar se anulan los puntos que había dado la compra")
    void cancelarAnulaPuntos() {
        Compra compra = facade.comprarEntradas(CEDULA_ANA, "F48H", asientos("A1", "A2"));
        assertEquals(20, facade.consultarPuntos(CEDULA_ANA));

        facade.cancelarCompra(compra.getId());

        assertEquals(0, facade.consultarPuntos(CEDULA_ANA));
    }

    @Test
    @DisplayName("Cancelar una compra pagada con puntos devuelve los puntos")
    void cancelarCompraConPuntosDevuelvePuntos() {
        facade.buscarCliente(CEDULA_ANA).getTheAcumulacion().sumarPuntos(200, "inicial");
        Compra compra = facade.realizarCompraConPuntos(CEDULA_ANA, "F48H", asientos("A1"), productos());
        assertEquals(50, facade.consultarPuntos(CEDULA_ANA));

        facade.cancelarCompra(compra.getId());

        assertEquals(200, facade.consultarPuntos(CEDULA_ANA));
        assertEquals(100000, facade.consultarSaldo(CEDULA_ANA));
    }

    @Test
    @DisplayName("Si el cine cancela la función devuelve el 100 % y avisa al cliente (Observer)")
    void cineCancelaFuncion() {
        facade.comprarEntradas(CEDULA_ANA, "F10H", asientos("A1"));

        comoAdmin();
        facade.cancelarFuncion("F10H");

        assertEquals(100000, facade.consultarSaldo(CEDULA_ANA));
        assertEquals(1, facade.obtenerNotificaciones(CEDULA_ANA).size());
        assertTrue(facade.obtenerNotificaciones(CEDULA_ANA).get(0).contains("Dune"));
        assertEquals(EstadoFuncion.CANCELADA, cine.buscarFuncion("F10H").getEstado());
        assertThrows(CinemaException.class, () -> facade.comprarEntradas(CEDULA_LUIS, "F10H", asientos("B1")));
    }

    @Test
    @DisplayName("Un cliente que canceló todas sus entradas ya no recibe avisos de esa función")
    void clienteSinEntradasNoEsNotificado() {
        Compra compra = facade.comprarEntradas(CEDULA_ANA, "F48H", asientos("A1"));
        facade.cancelarCompra(compra.getId());

        comoAdmin();
        facade.cancelarFuncion("F48H");

        assertTrue(facade.obtenerNotificaciones(CEDULA_ANA).isEmpty());
    }
}
