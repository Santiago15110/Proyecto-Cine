package org.example.proyectocine.viewController;


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
