package org.example.proyectocine.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Puntos CinemaUQ")
class AcumulacionPuntoTest {

    private AcumulacionPunto puntos;
    private final LocalDate hoy = LocalDate.now();

    @BeforeEach
    void crearCuenta() {
        puntos = new Cliente("Ana", "ana@test.com", "1234", "1001").getTheAcumulacion();
    }

    @Test
    @DisplayName("Cada acumulación vence un año después de obtenida")
    void vigenciaDeUnAnio() {
        puntos.sumarPuntos(300, "compra", hoy.minusMonths(3));

        RegistroPuntos registro = puntos.getListRegistros().get(0);
        assertEquals(hoy.minusMonths(3).plusYears(1), registro.getFechaVencimiento());
    }

    @Test
    @DisplayName("Los puntos vencidos no cuentan como disponibles")
    void vencidosNoCuentan() {
        puntos.sumarPuntos(300, "vigente", hoy.minusMonths(6));
        puntos.sumarPuntos(999, "vencida", hoy.minusYears(2));

        assertEquals(300, puntos.getPuntos());
    }

    @Test
    @DisplayName("Al redimir se usan primero los puntos más próximos a vencer")
    void redimeLosMasProximosAVencer() {
        puntos.sumarPuntos(500, "reciente", hoy.minusMonths(1));
        puntos.sumarPuntos(300, "antigua", hoy.minusMonths(11));

        puntos.redimirPuntos(400);

        RegistroPuntos reciente = buscar("reciente");
        RegistroPuntos antigua = buscar("antigua");
        assertEquals(0, antigua.getPuntosDisponibles());
        assertEquals(400, reciente.getPuntosDisponibles());
        assertEquals(400, puntos.getPuntos());
        assertEquals(400, puntos.getTotalRedimidos());
    }

    @Test
    @DisplayName("No se puede redimir más de lo vigente")
    void noRedimeMasDeLoVigente() {
        puntos.sumarPuntos(100, "vigente", hoy);
        puntos.sumarPuntos(900, "vencida", hoy.minusYears(2));

        assertThrows(PuntosInsuficientesException.class, () -> puntos.redimirPuntos(200));
        assertEquals(100, puntos.getPuntos());
    }

    @Test
    @DisplayName("Anular los puntos de una compra solo quita los que aún no se han usado")
    void anularPuntosDeUnaCompra() {
        puntos.sumarPuntos(100, "C-1", hoy);
        puntos.redimirPuntos(30);

        int anulados = puntos.anularPuntosDe("C-1");

        assertEquals(70, anulados);
        assertEquals(0, puntos.getPuntos());
    }

    @Test
    @DisplayName("La fecha de vencimiento mostrada es la de los próximos puntos a vencer")
    void proximoVencimiento() {
        assertNull(puntos.getFechaVencimiento());

        puntos.sumarPuntos(100, "a", hoy.minusMonths(2));
        puntos.sumarPuntos(100, "b", hoy.minusMonths(8));

        assertEquals(hoy.minusMonths(8).plusYears(1), puntos.getFechaVencimiento());
    }

    private RegistroPuntos buscar(String referencia) {
        return puntos.getListRegistros().stream()
                .filter(r -> referencia.equals(r.getReferencia()))
                .findFirst().orElseThrow();
    }
}
