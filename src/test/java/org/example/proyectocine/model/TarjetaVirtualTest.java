package org.example.proyectocine.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tarjeta virtual")
class TarjetaVirtualTest {

    private TarjetaVirtual tarjeta;

    @BeforeEach
    void crearTarjeta() {
        tarjeta = new Cliente("Ana", "ana@test.com", "1234", "1001").getTheTarjeta();
    }

    @Test
    @DisplayName("Recargar, descontar y reembolsar actualizan el saldo y registran cada movimiento")
    void movimientos() {
        tarjeta.recargar(50000);
        tarjeta.descontar(20000, "Compra C-1");
        tarjeta.reembolsar(16000, "Reembolso C-1");

        assertEquals(46000, tarjeta.getSaldo());
        assertEquals(3, tarjeta.getListMovimientos().size());

        MovimientoSaldo pago = tarjeta.getListMovimientos().get(1);
        assertEquals(TipoMovimiento.PAGO, pago.tipoMovimiento());
        assertEquals(30000, pago.saldoResultante());
        assertEquals("Compra C-1", pago.descripcion());
        assertEquals(TipoMovimiento.REEMBOLSO, tarjeta.getListMovimientos().get(2).tipoMovimiento());
    }

    @Test
    @DisplayName("No se puede descontar más que el saldo")
    void saldoInsuficiente() {
        tarjeta.recargar(1000);

        assertThrows(SaldoInsuficienteException.class, () -> tarjeta.descontar(5000, "Compra"));
        assertEquals(1000, tarjeta.getSaldo());
        assertEquals(1, tarjeta.getListMovimientos().size());
    }

    @Test
    @DisplayName("Una tarjeta bloqueada no puede pagar, pero sí recibir recargas")
    void tarjetaBloqueada() {
        tarjeta.recargar(10000);
        tarjeta.bloquear();

        assertThrows(TarjetaInactivaException.class, () -> tarjeta.descontar(1000, "Compra"));
        tarjeta.recargar(5000);
        assertEquals(15000, tarjeta.getSaldo());

        tarjeta.activar();
        tarjeta.descontar(1000, "Compra");
        assertEquals(14000, tarjeta.getSaldo());
    }

    @Test
    @DisplayName("Los montos deben ser mayores que cero")
    void montosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> tarjeta.recargar(0));
        assertThrows(IllegalArgumentException.class, () -> tarjeta.descontar(-5, "x"));
        assertThrows(IllegalArgumentException.class, () -> tarjeta.reembolsar(0, "x"));
    }
}
