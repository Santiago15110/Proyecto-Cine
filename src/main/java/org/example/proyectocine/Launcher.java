package org.example.proyectocine;

import javafx.application.Application;
import org.example.proyectocine.model.Cine;
import org.example.proyectocine.model.Funcion;
import org.example.proyectocine.model.Genero;
import org.example.proyectocine.model.Pelicula;
import org.example.proyectocine.model.Sala;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Launcher {
    public static void main(String[] args) {
        cargarDatosDePrueba();
        Application.launch(HelloApplication.class, args);
    }

    /**
     * Datos quemados para probar las pantallas mientras no existan las pantallas
     * de administración (crear salas, películas y funciones).
     * Se cargan directo en el Cine porque el Facade exige un administrador con sesión iniciada.
     */
    private static void cargarDatosDePrueba() {
        Cine cine = Cine.getInstancia();

        // Sala genérica: la pantalla de asientos se dibuja con cualquier cantidad de filas y columnas
        Sala sala = new Sala("S2", "Sala 2", 8, 12);
        sala.buscarAsiento('G', 7).setHabilitado(false);
        sala.buscarAsiento('G', 8).setHabilitado(false);
        cine.agregarSala(sala);

        Pelicula pelicula = new Pelicula("P1", "Beyond Horizons", Genero.CIENCIA_FICCION, "+13",
                "Una tripulación viaja más allá del borde del universo conocido.", null);
        pelicula.setEnCartelera(true);
        cine.agregarPelicula(pelicula);

        LocalDateTime manana = LocalDate.now().plusDays(1).atTime(19, 15);
        Funcion funcion = new Funcion("F1", pelicula, sala, manana, 120, 16000);

        // Algunos asientos ocupados para que se vea el estado en la pantalla
        String[] ocupados = {"B4", "C7", "C8", "E4", "F2", "F3", "H5", "H6", "H7"};
        for (String codigo : ocupados) {
            funcion.reservarAsiento(sala.buscarAsiento(codigo.charAt(0), Integer.parseInt(codigo.substring(1))));
        }
        cine.agregarFuncion(funcion);
    }
}
