package org.example.proyectocine.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Confitería: productos y combos (Composite)")
class ComboTest {

    private Producto crispetas;
    private Producto gaseosa;
    private Producto perro;

    @BeforeEach
    void crearProductos() {
        crispetas = new Producto("CRISP", "Crispetas", 12000, 100);
        gaseosa = new Producto("GAS", "Gaseosa", 6000, 50);
        perro = new Producto("PERRO", "Perro caliente", 10000, 80);
    }

    @Test
    @DisplayName("El precio del combo es la suma de sus productos menos el descuento")
    void precioDelCombo() {
        Combo pareja = new Combo("PAREJA", "Combo pareja", 10);
        pareja.agregar(crispetas);
        pareja.agregar(crispetas);
        pareja.agregar(gaseosa);
        pareja.agregar(gaseosa);

        assertEquals(36000 * 0.9, pareja.getPrecio(), 0.01);
        assertEquals(Math.round(300 * 0.9), pareja.getCostoPuntos());
    }

    @Test
    @DisplayName("Un combo puede contener otro combo y el precio se calcula en cascada")
    void comboDentroDeCombo() {
        Combo pareja = new Combo("PAREJA", "Combo pareja", 10);
        pareja.agregar(crispetas);
        pareja.agregar(gaseosa);
        Combo familiar = new Combo("FAM", "Combo familiar", 5);
        familiar.agregar(pareja);
        familiar.agregar(perro);

        double precioPareja = 18000 * 0.9;
        assertEquals((precioPareja + 10000) * 0.95, familiar.getPrecio(), 0.01);
        assertTrue(familiar.contiene(crispetas));
    }

    @Test
    @DisplayName("Un combo no puede contenerse a sí mismo, ni directa ni indirectamente")
    void sinCiclos() {
        Combo a = new Combo("A", "A", 0);
        Combo b = new Combo("B", "B", 0);
        b.agregar(a);

        assertThrows(IllegalArgumentException.class, () -> a.agregar(a));
        assertThrows(IllegalArgumentException.class, () -> a.agregar(b));
    }

    @Test
    @DisplayName("Una línea de compra trata igual a un producto y a un combo")
    void productoYComboSeTratanIgual() {
        Combo combo = new Combo("C", "Combo", 0);
        combo.agregar(gaseosa);

        assertEquals(18000, new LineaProducto(gaseosa, 3).getSubtotal());
        assertEquals(12000, new LineaProducto(combo, 2).getSubtotal());
    }

    @Test
    @DisplayName("No se aceptan productos con precio o datos inválidos")
    void validaciones() {
        assertThrows(IllegalArgumentException.class, () -> new Producto("X", "X", 0, 10));
        assertThrows(IllegalArgumentException.class, () -> new Producto("X", " ", 1000, 10));
        assertThrows(IllegalArgumentException.class, () -> new Combo("X", "X", 100));
    }
}
