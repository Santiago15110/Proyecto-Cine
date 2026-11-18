package org.example.proyectocine.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Política de reembolso (100 % / 80 % / no permitido)")
class PoliticaReembolsoTest {

    private final PoliticaReembolso politica = new PoliticaReembolso();
    private final LocalDateTime inicioFuncion = LocalDateTime.of(2026, 12, 10, 20, 0);
    private Funcion funcion;

    @BeforeEach
    void crearFuncion() {
        Pelicula pelicula = new Pelicula("P1", "Dune", Genero.CIENCIA_FICCION, "+13", 150, "d", null);
        funcion = new Funcion("F1", pelicula, new Sala("S1", "Sala 1", 3, 3), inicioFuncion, 10000);
    }

    @Test
    @DisplayName("Más de 24 horas antes: 100 %")
    void masDe24Horas() {
        assertEquals(100, politica.porcentajeReembolso(funcion, inicioFuncion.minusHours(25)));
    }

    @Test
    @DisplayName("Exactamente 24 horas antes: 80 %")
    void exactamente24Horas() {
        assertEquals(80, politica.porcentajeReembolso(funcion, inicioFuncion.minusHours(24)));
    }

    @Test
    @DisplayName("Entre 2 y 24 horas: 80 %")
    void entre2y24Horas() {
        assertEquals(80, politica.porcentajeReembolso(funcion, inicioFuncion.minusHours(10)));
        assertEquals(80, politica.porcentajeReembolso(funcion, inicioFuncion.minusHours(2)));
    }

    @Test
    @DisplayName("Menos de 2 horas: no se permite cancelar")
    void menosDe2Horas() {
        assertThrows(CancelacionNoPermitidaException.class,
                () -> politica.porcentajeReembolso(funcion, inicioFuncion.minusMinutes(119)));
        assertThrows(CancelacionNoPermitidaException.class,
                () -> politica.porcentajeReembolso(funcion, inicioFuncion.plusMinutes(10)));
    }

    @Test
    @DisplayName("Una compra solo de confitería se reembolsa al 100 %")
    void sinFuncion() {
        assertEquals(100, politica.porcentajeReembolso(null, inicioFuncion));
    }
}
