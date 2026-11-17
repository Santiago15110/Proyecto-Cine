package org.example.proyectocine.viewController;

public enum Perfil {

    CLIENTE("Clientes", "Compra entradas, elige tus asientos y acumula puntos."),
    ADMINISTRADOR("Administradores", "Gestiona la cartelera, las funciones y los reportes del cine.");

    private final String titulo;
    private final String descripcion;

    Perfil(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
