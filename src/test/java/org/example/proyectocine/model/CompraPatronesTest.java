package org.example.proyectocine.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Compra: Builder, Decorator y State")
class CompraPatronesTest {

    private Cliente cliente;
    private Funcion funcion;
    private Sala sala;

    @BeforeEach
    void crearDatos() {
        cliente = new Cliente("Ana", "ana@test.com", "1234", "1001");
        Pelicula pelicula = new Pelicula("P1", "Dune", Genero.CIENCIA_FICCION, "+13", 150, "d", null);
        sala = new Sala("S1", "Sala 1", 3, 4);
        funcion = new Funcion("F1", pelicula, sala, LocalDateTime.now().plusDays(1), 10000);
    }

    private Entrada entrada(String id, char fila, int numero) {
        return new Entrada(id, funcion, sala.buscarAsiento(fila, numero));
    }

    @Test
    @DisplayName("Builder: arma la compra, calcula el total y la deja en estado Pendiente")
    void builderArmaLaCompra() {
        Compra compra = new CompraBuilder()
                .paraCliente(cliente)
                .agregarEntrada(entrada("E1", 'A', 1))
                .agregarEntrada(entrada("E2", 'A', 2))
                .agregarProducto(new Producto("GAS", "Gaseosa", 6000, 50), 1)
                .build();

        assertEquals(26000, compra.getTotal());
        assertInstanceOf(EstadoPendiente.class, compra.getEstado());
        assertEquals(funcion, compra.getFuncion());
    }

    @Test
    @DisplayName("Builder: no deja repetir un asiento en la misma compra ni crearla sin cliente")
    void builderValida() {
        CompraBuilder builder = new CompraBuilder().paraCliente(cliente).agregarEntrada(entrada("E1", 'A', 1));

        assertThrows(AsientoNoDisponibleException.class, () -> builder.agregarEntrada(entrada("E2", 'A', 1)));
        assertThrows(IllegalArgumentException.class, () -> new CompraBuilder().build());
    }

    @Test
    @DisplayName("Decorator: los descuentos y cargos se apilan sobre el precio base")
    void decoratorDePrecio() {
        Compra compra = new CompraBuilder().paraCliente(cliente).agregarEntrada(entrada("E1", 'A', 1)).build();
        ComponentePrecio precio = new CargoServicio(new DescuentoPorcentaje(new PrecioBase(compra), 20), 2000);

        assertEquals(10000 * 0.8 + 2000, precio.calcular(), 0.01);
        assertTrue(precio.getDescripcion().contains("Descuento"));
        assertTrue(precio.getDescripcion().contains("Cargo"));
    }

    @Test
    @DisplayName("Decorator: una promoción aplicada en el Builder envuelve el precio")
    void promocionComoDecorator() {
        Promocion promo = new Promocion("PR", "Martes", 25, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        Compra compra = new CompraBuilder().paraCliente(cliente)
                .agregarEntrada(entrada("E1", 'A', 1)).aplicarPromocion(promo).build();

        assertInstanceOf(DescuentoPorcentaje.class, compra.getPrecio());
        assertEquals(7500, compra.getTotal(), 0.01);
    }

    @Test
    @DisplayName("State: Pendiente → Pagada reserva los asientos; Pagada → Cancelada los libera")
    void transicionesDeEstado() {
        Compra compra = new CompraBuilder().paraCliente(cliente).agregarEntrada(entrada("E1", 'B', 2)).build();
        Asiento asiento = sala.buscarAsiento('B', 2);

        compra.confirmar();
        assertInstanceOf(EstadoPagada.class, compra.getEstado());
        assertFalse(funcion.estaDisponible(asiento));

        compra.cancelar();
        assertInstanceOf(EstadoCancelada.class, compra.getEstado());
        assertTrue(funcion.estaDisponible(asiento));
    }

    @Test
    @DisplayName("State: cada estado rechaza las acciones que no le corresponden")
    void accionesNoPermitidas() {
        Compra compra = new CompraBuilder().paraCliente(cliente).agregarEntrada(entrada("E1", 'C', 3)).build();
        compra.confirmar();

        assertThrows(CinemaException.class, compra::confirmar);
        compra.cancelar();
        assertThrows(CancelacionNoPermitidaException.class, compra::cancelar);
        assertThrows(CinemaException.class, compra::confirmar);
    }

    @Test
    @DisplayName("State: si un asiento se ocupó antes de confirmar, no se reserva ninguno")
    void confirmarConAsientoOcupado() {
        Compra compra = new CompraBuilder().paraCliente(cliente)
                .agregarEntrada(entrada("E1", 'A', 3))
                .agregarEntrada(entrada("E2", 'A', 4)).build();
        funcion.reservarAsiento(sala.buscarAsiento('A', 4));

        assertThrows(AsientoNoDisponibleException.class, compra::confirmar);
        assertTrue(funcion.estaDisponible(sala.buscarAsiento('A', 3)));
        assertInstanceOf(EstadoPendiente.class, compra.getEstado());
    }
}
