package org.example.proyectocine.model;

public interface Sujeto {

    void agregarObservador(Observador o);
    void eliminarObservador(Observador o);
    void notificarObservador(TipoEvento tipoEvento, Object dato);


}
