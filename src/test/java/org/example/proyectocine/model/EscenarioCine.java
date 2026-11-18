package org.example.proyectocine.model;

import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

abstract class EscenarioCine {

    static final String ADMIN_CORREO = "admin@test.com";
    static final String ADMIN_CLAVE = "admin";
    static final String CEDULA_ANA = "1001";
    static final String CEDULA_LUIS = "2002";
    static final double PRECIO_ENTRADA = 10000;

    protected CineFacade facade;
    protected Cine cine;

    @BeforeEach
    void prepararEscenario() {
        Cine.reiniciar();
        cine = Cine.getInstancia();
        facade = new CineFacade((referencia, monto) -> true);

        facade.registrarAdmin("Admin", ADMIN_CORREO, ADMIN_CLAVE, "AD-1");
        facade.registrarCliente("Ana", "ana@test.com", "1234", CEDULA_ANA);
        facade.registrarCliente("Luis", "luis@test.com", "1234", CEDULA_LUIS);

        comoAdmin();
        facade.agregarPelicula("P1", "Dune", Genero.CIENCIA_FICCION, "+13", 150, "desc", null);
        facade.crearSala("S1", "Sala 1", 5, 8);
        facade.programarFuncion("F48H", "P1", "S1", LocalDateTime.now().plusHours(48), PRECIO_ENTRADA);
        facade.programarFuncion("F10H", "P1", "S1", LocalDateTime.now().plusHours(10), PRECIO_ENTRADA);
        facade.programarFuncion("F1H", "P1", "S1", LocalDateTime.now().plusHours(1), PRECIO_ENTRADA);
        facade.crearProducto("CRISP", "Crispetas", 12000, 100);
        facade.crearProducto("GAS", "Gaseosa", 6000, 50);
        facade.recargarTarjeta(CEDULA_ANA, 100000);
        facade.recargarTarjeta(CEDULA_LUIS, 5000);
        facade.cerrarSesion();
    }

    void comoAdmin() {
        facade.iniciarSesion(ADMIN_CORREO, ADMIN_CLAVE);
    }

    void comoCliente(String correo) {
        facade.iniciarSesion(correo, "1234");
    }

    static ArrayList<String> asientos(String... codigos) {
        return new ArrayList<>(Arrays.asList(codigos));
    }

    static Map<String, Integer> productos(Object... idYCantidad) {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        for (int i = 0; i < idYCantidad.length; i += 2) {
            mapa.put((String) idYCantidad[i], (Integer) idYCantidad[i + 1]);
        }
        return mapa;
    }
}
