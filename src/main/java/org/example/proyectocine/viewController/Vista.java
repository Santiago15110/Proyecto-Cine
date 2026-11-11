package org.example.proyectocine.viewController;

/**
 * Pantallas de la aplicación, con su archivo FXML y el título de la ventana.
 * Para agregar una pantalla nueva, solo se agrega una constante aquí.
 */
public enum Vista {

    INICIO("PaginaInicio.fxml", "Cine"),
    REGISTRO("RegistroUsuario.fxml", "Registro de usuarios"),
    TARJETA("TarjetaVirtual.fxml", "Mi tarjeta virtual");

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
