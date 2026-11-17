package org.example.proyectocine.viewController;

public enum Vista {

    BIENVENIDA("Bienvenida.fxml", "CinemaUQ - Bienvenido"),
    LOGIN("Login.fxml", "CinemaUQ - Iniciar sesión"),
    REGISTRO("RegistroUsuario.fxml", "CinemaUQ - Crear cuenta"),
    PANEL_CLIENTE("PanelCliente.fxml", "CinemaUQ"),
    PANEL_ADMIN("PanelAdmin.fxml", "CinemaUQ - Administración"),
    TARJETA("TarjetaVirtual.fxml", "Mi tarjeta virtual"),
    REPORTES("ReportesView.fxml", "Reporte del Cine"),
    CARTELERA("Cartelera.fxml", "Cartelera"),
    SALA("SalaAsientos.fxml", "Selección de asientos"),
    PAGO("ConfiteriaPago.fxml", "Confitería y pago"),
    ADMIN_CLIENTES("AdminClientes.fxml","Gestión de Clientes"),
    ADMIN_FUNCIONES("AdminFunciones.fxml", "CinemaUQ - Gestión de Funciones"),
    ADMIN_PELICULAS("AdminPeliculas.fxml", "CinemaUQ - Gestión de Películas"),
    ADMIN_SALAS("AdminSalas.fxml", "CinemaUQ - Gestión de Salas"),
    ADMIN_CONFITERIA("AdminConfiteria.fxml", "CinemaUQ - Gestión de Confitería"),
    ADMIN_PROMOCIONES("AdminPromociones.fxml", "CinemaUQ - Gestión de Promociones"),
    ADMIN_COMPRAS("AdminCompras.fxml", "CinemaUQ - Historial de Compras");

    private final String archivo;
    private final String titulo;

    Vista(String archivo, String titulo) {
        this.archivo = archivo;
        this.titulo = titulo;
    }

    public String getArchivo() {
        return archivo;
    }

    public String getTitulo() {
        return titulo;
    }
}
