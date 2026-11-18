package org.example.proyectocine.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Funciones, asientos y notificaciones (Observer)")
class FuncionTest {

    private Sala sala;
    private Funcion funcion;

    @BeforeEach
    void crearFuncion() {
        Pelicula pelicula = new Pelicula("P1", "Dune", Genero.CIENCIA_FICCION, "+13", 120, "d", null);
        sala = new Sala("S1", "Sala 1", 2, 5);
        funcion = new Funcion("F1", pelicula, sala, LocalDateTime.now().plusDays(1), 10000);
    }

    @Test
    @DisplayName("La sala genera sus asientos y la capacidad excluye los deshabilitados")
    void capacidadDeLaSala() {
        assertEquals(10, sala.getCapacidad());
        sala.buscarAsiento('B', 5).setHabilitado(false);

        assertEquals(9, sala.getCapacidad());
        assertFalse(funcion.estaDisponible(sala.buscarAsiento('B', 5)));
    }

    @Test
    @DisplayName("La ocupación se calcula sobre los asientos habilitados")
    void ocupacion() {
        funcion.reservarAsiento(sala.buscarAsiento('A', 1));
        funcion.reservarAsiento(sala.buscarAsiento('A', 2));

        assertEquals(20.0, funcion.getOcupacion(), 0.01);
        assertThrows(AsientoNoDisponibleException.class, () -> funcion.reservarAsiento(sala.buscarAsiento('A', 1)));
    }

    @Test
    @DisplayName("Al cancelar la función se avisa a todos los observadores suscritos")
    void cancelarNotificaObservadores() {
        List<TipoEvento> recibidos = new ArrayList<>();
        Observador observador = (evento, dato) -> recibidos.add(evento);
        funcion.agregarObservador(observador);
        funcion.agregarObservador(observador);

        funcion.cancelar();

        assertEquals(List.of(TipoEvento.FUNCION_CANCELADA), recibidos);
        assertEquals(EstadoFuncion.CANCELADA, funcion.getEstado());
        assertThrows(CancelacionNoPermitidaException.class, funcion::cancelar);
    }

    @Test
    @DisplayName("Un observador eliminado ya no recibe avisos")
    void observadorEliminado() {
        List<TipoEvento> recibidos = new ArrayList<>();
        Observador observador = (evento, dato) -> recibidos.add(evento);
        funcion.agregarObservador(observador);
        funcion.eliminarObservador(observador);

        funcion.cancelar();

        assertTrue(recibidos.isEmpty());
    }

    @Test
    @DisplayName("El cliente guarda una notificación cuando su función se cancela")
    void clienteRecibeNotificacion() {
        Cliente cliente = new Cliente("Ana", "ana@test.com", "1234", "1001");
        funcion.agregarObservador(cliente);

        funcion.cancelar();

        assertEquals(1, cliente.getListNotificaciones().size());
        assertTrue(cliente.getListNotificaciones().get(0).contains("Dune"));
    }

    @Test
    @DisplayName("Una función que ya terminó queda FINALIZADA")
    void funcionFinalizada() {
        Pelicula corta = new Pelicula("P2", "Corto", Genero.DRAMA, "TP", 30, "d", null);
        Funcion pasada = new Funcion("F2", corta, sala, LocalDateTime.now().minusHours(2), 8000);

        assertEquals(EstadoFuncion.FINALIZADA, pasada.getEstado());
        assertEquals(EstadoFuncion.PROGRAMADA, funcion.getEstado());
    }
}
