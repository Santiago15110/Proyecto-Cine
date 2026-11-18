package org.example.proyectocine.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Usuarios, sesión y permisos")
class UsuariosYPermisosTest extends EscenarioCine {

    @Test
    @DisplayName("Un cliente nuevo recibe su tarjeta virtual con saldo 0 y su cuenta de puntos")
    void clienteNuevoTieneTarjetaYPuntos() {
        Cliente nuevo = facade.registrarCliente("Sofía", "sofia@test.com", "1234", "3003");

        assertNotNull(nuevo.getTheTarjeta());
        assertEquals("3003", nuevo.getTheTarjeta().getNumero());
        assertEquals(0, nuevo.getTheTarjeta().getSaldo());
        assertTrue(nuevo.getTheTarjeta().isActiva());
        assertEquals(0, nuevo.getTheAcumulacion().getPuntos());
    }

    @Test
    @DisplayName("No se pueden registrar dos usuarios con el mismo correo o la misma cédula")
    void noPermiteCorreoNiCedulaRepetidos() {
        assertThrows(UsuarioExistenteException.class,
                () -> facade.registrarCliente("Otra", "ana@test.com", "1234", "9999"));
        assertThrows(UsuarioExistenteException.class,
                () -> facade.registrarCliente("Otra", "otra@test.com", "1234", CEDULA_ANA));
    }

    @Test
    @DisplayName("Iniciar sesión guarda al usuario y cerrar sesión lo quita")
    void iniciarYCerrarSesion() {
        Usuario usuario = facade.iniciarSesion("ana@test.com", "1234");

        assertInstanceOf(Cliente.class, usuario);
        assertEquals(usuario, facade.obtenerUsuarioActual());

        facade.cerrarSesion();
        assertNull(facade.obtenerUsuarioActual());
    }

    @Test
    @DisplayName("Con contraseña incorrecta se lanza CredencialesInvalidasException")
    void loginConClaveIncorrecta() {
        assertThrows(CredencialesInvalidasException.class, () -> facade.iniciarSesion("ana@test.com", "mala"));
        assertNull(facade.obtenerUsuarioActual());
    }

    @Test
    @DisplayName("Solo el administrador puede recargar tarjetas")
    void soloAdminRecarga() {
        comoCliente("ana@test.com");
        assertThrows(AccesoDenegadoException.class, () -> facade.recargarTarjeta(CEDULA_ANA, 1000));

        comoAdmin();
        facade.recargarTarjeta(CEDULA_ANA, 1000);
        assertEquals(101000, facade.consultarSaldo(CEDULA_ANA));
    }

    @Test
    @DisplayName("Sin sesión de administrador no se puede gestionar el cine")
    void operacionesDeAdminRequierenSesion() {
        assertThrows(AccesoDenegadoException.class,
                () -> facade.agregarPelicula("P9", "X", Genero.DRAMA, "+7", 90, "d", null));
        assertThrows(AccesoDenegadoException.class, () -> facade.crearSala("S9", "Sala 9", 3, 3));
        assertThrows(AccesoDenegadoException.class, () -> facade.cancelarFuncion("F48H"));
        assertThrows(AccesoDenegadoException.class, () -> facade.crearReporte(TipoReporte.VENTAS, null, null));
    }

    @Test
    @DisplayName("Un administrador solo se registra solo con el código de autorización correcto")
    void registroDeAdminConCodigo() {
        assertThrows(AccesoDenegadoException.class,
                () -> facade.registrarAdminConAutorizacion("Carlos", "carlos@test.com", "abcd", "AD-2", "MALO"));

        Admin admin = facade.registrarAdminConAutorizacion("Carlos", "carlos@test.com", "abcd", "AD-2",
                CineFacade.CODIGO_AUTORIZACION_ADMIN);
        assertEquals("AD-2", admin.getCodigo());
    }

    @Test
    @DisplayName("Después del primer administrador, solo otro administrador puede crear administradores")
    void segundoAdminRequiereSesionDeAdmin() {
        assertThrows(AccesoDenegadoException.class,
                () -> facade.registrarAdmin("Carlos", "carlos@test.com", "abcd", "AD-2"));

        comoAdmin();
        assertNotNull(facade.registrarAdmin("Carlos", "carlos@test.com", "abcd", "AD-2"));
    }
}
