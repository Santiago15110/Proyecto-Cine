package org.example.proyectocine.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DatosIniciales {

    public static final String CORREO_ADMIN = "admin@cinemauq.com";
    public static final String CLAVE_ADMIN = "admin123";

    private DatosIniciales() {
    }

    public static void cargar() {
        if (!Cine.getInstancia().getUsuarios().isEmpty()) {
            return;
        }
        CineFacade f = new CineFacade();

        f.registrarAdmin("Administrador CinemaUQ", CORREO_ADMIN, CLAVE_ADMIN, "AD-001");
        f.registrarCliente("Ana María López", "ana@mail.com", "1234", "1094000001");
        f.registrarCliente("Luis Gómez", "luis@mail.com", "1234", "1094000002");
        f.registrarCliente("Sofía Ramírez", "sofia@mail.com", "1234", "1094000003");

        f.iniciarSesion(CORREO_ADMIN, CLAVE_ADMIN);

        f.agregarPelicula("P01", "Dune: Parte Dos", Genero.CIENCIA_FICCION, "+13", 166,
                "Paul Atreides se une a los Fremen para vengar a su familia.", poster("P01"));
        f.agregarPelicula("P02", "Intensamente 2", Genero.COMEDIA, "TP", 96,
                "Nuevas emociones llegan a la mente de Riley en su adolescencia.", poster("P02"));
        f.agregarPelicula("P03", "Interestelar", Genero.CIENCIA_FICCION, "+13", 169,
                "Un grupo de exploradores viaja por un agujero de gusano para salvar a la humanidad.", poster("P03"));
        f.agregarPelicula("P04", "El Conjuro", Genero.TERROR, "+15", 112,
                "Dos investigadores de lo paranormal ayudan a una familia aterrorizada.", poster("P04"));
        f.agregarPelicula("P05", "Gladiador II", Genero.ACCION, "+15", 148,
                "Lucio debe entrar al Coliseo para devolverle la gloria a Roma.", poster("P05"));
        f.agregarPelicula("P06", "Wicked", Genero.FANTASIA, "TP", 160,
                "La historia de las brujas de Oz antes de la llegada de Dorothy.", poster("P06"));

        f.crearSala("S1", "Sala 1", 8, 12);
        f.crearSala("S2", "Sala 2 VIP", 6, 10);
        f.crearSala("S3", "Sala 3", 8, 12);
        f.deshabilitarAsiento("S1", "H1");
        f.deshabilitarAsiento("S1", "H12");

        LocalDate hoy = LocalDate.now();
        f.programarFuncion("F01", "P01", "S1", hoy.plusDays(1).atTime(18, 0), 15000);
        f.programarFuncion("F02", "P01", "S2", hoy.plusDays(2).atTime(20, 30), 22000);
        f.programarFuncion("F03", "P02", "S3", hoy.plusDays(1).atTime(15, 0), 12000);
        f.programarFuncion("F04", "P02", "S3", hoy.plusDays(3).atTime(17, 30), 12000);
        f.programarFuncion("F05", "P03", "S1", hoy.plusDays(2).atTime(19, 0), 15000);
        f.programarFuncion("F06", "P04", "S3", hoy.plusDays(1).atTime(21, 45), 15000);
        f.programarFuncion("F07", "P05", "S2", hoy.plusDays(4).atTime(20, 0), 22000);
        f.programarFuncion("F08", "P06", "S1", hoy.plusDays(3).atTime(16, 0), 15000);
        f.programarFuncion("F09", "P03", "S3", redondear(LocalDateTime.now().plusHours(10)), 15000);
        f.programarFuncion("F10", "P05", "S1", redondear(LocalDateTime.now().plusMinutes(70)), 15000);

        f.crearProducto("CRISP_M", "Crispetas medianas", 12000, 100);
        f.crearProducto("CRISP_G", "Crispetas grandes", 16000, 130);
        f.crearProducto("GASEOSA", "Gaseosa", 6000, 50);
        f.crearProducto("AGUA", "Agua", 4000, 30);
        f.crearProducto("PERRO", "Perro caliente", 10000, 80);
        f.crearProducto("NACHOS", "Nachos con queso", 14000, 110);
        f.crearCombo("COMBO_PERSONAL", "Combo personal", 10, lista("CRISP_M", "GASEOSA"));
        f.crearCombo("COMBO_PAREJA", "Combo pareja", 15, lista("CRISP_G", "CRISP_G", "GASEOSA", "GASEOSA"));
        f.crearCombo("COMBO_FAMILIAR", "Combo familiar", 10, lista("COMBO_PAREJA", "PERRO", "PERRO"));

        f.crearPromocion("PR01", "Martes de cine", 20, hoy.minusDays(7), hoy.plusDays(30));
        f.crearPromocion("PR02", "Estreno de octubre", 10, hoy.minusDays(40), hoy.minusDays(10));
        f.crearPromocion("PR03", "Black Friday", 30, hoy.plusDays(10), hoy.plusDays(12));

        f.recargarTarjeta("1094000001", 150000);
        f.recargarTarjeta("1094000002", 60000);
        f.recargarTarjeta("1094000003", 40000);

        Cliente ana = f.buscarCliente("1094000001");
        ana.getTheAcumulacion().sumarPuntos(300, "Bienvenida", hoy.minusMonths(11));
        ana.getTheAcumulacion().sumarPuntos(500, "Aniversario CinemaUQ", hoy.minusMonths(2));
        ana.getTheAcumulacion().sumarPuntos(200, "Promoción 2024", hoy.minusYears(2));
        Cliente luis = f.buscarCliente("1094000002");
        luis.getTheAcumulacion().sumarPuntos(150, "Bienvenida", hoy.minusMonths(5));

        f.realizarCompra("1094000001", "F01", lista("D6", "D7"), productos("COMBO_PAREJA", 1), new ArrayList<>());
        f.comprarConfiteria("1094000001", productos("NACHOS", 1));
        f.realizarCompra("1094000001", "F08", lista("E5"), new LinkedHashMap<>(), new ArrayList<>());
        Compra cancelada = f.comprarEntradas("1094000001", "F05", lista("C3", "C4"));
        f.cancelarCompra(cancelada.getId());

        f.comprarEntradas("1094000002", "F05", lista("F5", "F6"), f.obtenerPromocionesVigentes());
        f.realizarCompraConPuntos("1094000002", "F03", lista("B4"), new LinkedHashMap<>());
        f.comprarEntradas("1094000002", "F09", lista("A1"));

        f.realizarCompra("1094000003", "F06", lista("C1"), productos("COMBO_PERSONAL", 1), new ArrayList<>());

        f.bloquearTarjeta("1094000003");
        f.cancelarFuncion("F08");

        f.solicitarRecarga("1094000002", 30000);
        f.solicitarRecarga("1094000003", 20000);

        f.cerrarSesion();
    }

    private static String poster(String idPelicula) {
        for (String extension : new String[]{".jpg", ".jpeg", ".png"}) {
            String ruta = "/posters/" + idPelicula + extension;
            if (DatosIniciales.class.getResource(ruta) != null) {
                return ruta;
            }
        }
        return null;
    }

    private static LocalDateTime redondear(LocalDateTime fecha) {
        int minutos = (fecha.getMinute() / 15) * 15;
        return fecha.toLocalDate().atTime(LocalTime.of(fecha.getHour(), minutos));
    }

    private static ArrayList<String> lista(String... valores) {
        return new ArrayList<>(Arrays.asList(valores));
    }

    private static Map<String, Integer> productos(String id, int cantidad) {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        mapa.put(id, cantidad);
        return mapa;
    }
}
