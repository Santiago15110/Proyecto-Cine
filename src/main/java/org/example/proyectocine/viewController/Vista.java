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
    PAGO("ConfiteriaPago.fxml", "Confitería y pago");

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
