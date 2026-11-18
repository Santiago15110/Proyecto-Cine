package org.example.proyectocine.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Compras con saldo y con puntos")
class ComprasTest extends EscenarioCine {

    @Test
    @DisplayName("Comprar entradas descuenta el saldo, ocupa los asientos, suma puntos y registra el movimiento")
    void compraConSaldo() {
        Compra compra = facade.comprarEntradas(CEDULA_ANA, "F48H", asientos("A1", "A2"));

        assertEquals(20000, compra.getTotal());
        assertInstanceOf(EstadoPagada.class, compra.getEstado());
        assertEquals(80000, facade.consultarSaldo(CEDULA_ANA));
        assertEquals(20, facade.consultarPuntos(CEDULA_ANA));
        assertEquals(38, facade.consultarAsientosDisponibles("F48H").size());

        MovimientoSaldo ultimo = facade.obtenerMovimientos(CEDULA_ANA).get(0);
        assertEquals(TipoMovimiento.PAGO, ultimo.tipoMovimiento());
        assertEquals(20000, ultimo.monto());
        assertTrue(ultimo.descripcion().contains(compra.getId()));
    }

    @Test
    @DisplayName("Un mismo asiento no se puede vender dos veces para la misma función")
    void asientoNoSeVendeDosVeces() {
        facade.comprarEntradas(CEDULA_ANA, "F48H", asientos("B3"));

        assertThrows(AsientoNoDisponibleException.class,
                () -> facade.comprarEntradas(CEDULA_LUIS, "F48H", asientos("B3")));
    }

    @Test
    @DisplayName("Si el saldo no alcanza, no se cobra y los asientos quedan libres")
    void saldoInsuficienteLiberaAsientos() {
        assertThrows(SaldoInsuficienteException.class,
                () -> facade.comprarEntradas(CEDULA_LUIS, "F48H", asientos("C1")));

        assertEquals(5000, facade.consultarSaldo(CEDULA_LUIS));
        assertEquals(40, facade.consultarAsientosDisponibles("F48H").size());
    }

    @Test
    @DisplayName("Con la tarjeta bloqueada no se puede pagar")
    void tarjetaBloqueadaNoPaga() {
        comoAdmin();
        facade.bloquearTarjeta(CEDULA_ANA);

        assertThrows(TarjetaInactivaException.class,
                () -> facade.comprarEntradas(CEDULA_ANA, "F48H", asientos("D1")));
        assertEquals(40, facade.consultarAsientosDisponibles("F48H").size());
    }

    @Test
    @DisplayName("Compra mixta: entradas + confitería con una promoción vigente (Decorator)")
    void compraConConfiteriaYPromocion() {
        comoAdmin();
        Promocion martes = facade.crearPromocion("PR1", "Martes", 20, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        Compra compra = facade.realizarCompra(CEDULA_ANA, "F48H", asientos("A5"),
                productos("CRISP", 1, "GAS", 2), new ArrayList<>(List.of(martes)));

        assertEquals((10000 + 12000 + 12000) * 0.8, compra.getTotal(), 0.01);
        assertEquals(2, compra.getLineas().size());
    }

    @Test
    @DisplayName("Una promoción vencida no se puede aplicar")
    void promocionVencidaNoAplica() {
        comoAdmin();
        Promocion vieja = facade.crearPromocion("PR2", "Vieja", 50, LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));

        assertThrows(CinemaException.class, () -> facade.comprarEntradas(CEDULA_ANA, "F48H",
                asientos("A6"), new ArrayList<>(List.of(vieja))));
    }

    @Test
    @DisplayName("No se venden entradas de una función que ya empezó")
    void funcionIniciadaNoSeVende() {
        comoAdmin();
        facade.programarFuncion("PASADA", "P1", "S1", java.time.LocalDateTime.now().minusMinutes(5), PRECIO_ENTRADA);

        assertThrows(CinemaException.class, () -> facade.comprarEntradas(CEDULA_ANA, "PASADA", asientos("A1")));
    }

    @Test
    @DisplayName("Pagar con puntos no toca el saldo, gasta los puntos y no genera puntos nuevos")
    void compraConPuntos() {
        facade.buscarCliente(CEDULA_ANA).getTheAcumulacion().sumarPuntos(300, "inicial");

        Compra compra = facade.realizarCompraConPuntos(CEDULA_ANA, "F48H", asientos("E1"), productos("GAS", 1));

        assertEquals(Entrada.COSTO_PUNTOS + 50, compra.getPuntosRedimidos());
        assertTrue(compra.isPagadaConPuntos());
        assertEquals(100000, facade.consultarSaldo(CEDULA_ANA));
        assertEquals(300 - 200, facade.consultarPuntos(CEDULA_ANA));
    }

    @Test
    @DisplayName("Si los puntos no alcanzan se lanza PuntosInsuficientesException y no se reservan asientos")
    void puntosInsuficientes() {
        assertThrows(PuntosInsuficientesException.class,
                () -> facade.realizarCompraConPuntos(CEDULA_ANA, "F48H", asientos("E2"), productos()));
        assertEquals(40, facade.consultarAsientosDisponibles("F48H").size());
    }

    @Test
    @DisplayName("Cotizar calcula el total sin cobrar ni reservar")
    void cotizarNoTieneEfectos() {
        double total = facade.cotizarCompra(CEDULA_ANA, "F48H", asientos("A1"), productos("CRISP", 1), new ArrayList<>());

        assertEquals(22000, total);
        assertEquals(100000, facade.consultarSaldo(CEDULA_ANA));
        assertEquals(40, facade.consultarAsientosDisponibles("F48H").size());
    }
}
