package org.example.proyectocine;

import javafx.application.Application;
import org.example.proyectocine.model.Cine;
import org.example.proyectocine.model.Funcion;
import org.example.proyectocine.model.Genero;
import org.example.proyectocine.model.Pelicula;
import org.example.proyectocine.model.Sala;

import java.time.LocalDate;

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

        // Salas genéricas: la pantalla de asientos se dibuja con cualquier cantidad de filas y columnas
        Sala sala1 = new Sala("S1", "Sala 1", 10, 14);
        Sala sala2 = new Sala("S2", "Sala 2", 8, 12);
        sala2.buscarAsiento('G', 7).setHabilitado(false);
        sala2.buscarAsiento('G', 8).setHabilitado(false);
        cine.agregarSala(sala1);
        cine.agregarSala(sala2);

        Pelicula beyond = new Pelicula("P1", "Beyond Horizons", Genero.CIENCIA_FICCION, "+13", 120,
                "Una tripulación viaja más allá del borde del universo conocido.", null);
        Pelicula risa = new Pelicula("P2", "La última risa", Genero.COMEDIA, "+7", 95,
                "Un comediante retirado vuelve a los escenarios para salvar su viejo teatro.", null);
        Pelicula sombras = new Pelicula("P3", "Sombras del pasado", Genero.DRAMA, "+16", 110,
                "Una familia debe enfrentar un secreto guardado durante veinte años.", null);
        cine.agregarPelicula(beyond);
        cine.agregarPelicula(risa);
        cine.agregarPelicula(sombras);

        LocalDate manana = LocalDate.now().plusDays(1);
        LocalDate pasado = LocalDate.now().plusDays(2);
        LocalDate tresDias = LocalDate.now().plusDays(3);

        // Función con asientos ocupados para que se vea el estado en la pantalla de asientos
        Funcion f1 = new Funcion("F1", beyond, sala2, manana.atTime(19, 15), 16000);
        String[] ocupados = {"B4", "C7", "C8", "E4", "F2", "F3", "H5", "H6", "H7"};
        for (String codigo : ocupados) {
            f1.reservarAsiento(sala2.buscarAsiento(codigo.charAt(0), Integer.parseInt(codigo.substring(1))));
        }
        cine.agregarFuncion(f1);

        cine.agregarFuncion(new Funcion("F2", beyond, sala1, manana.atTime(15, 0), 14000));
        cine.agregarFuncion(new Funcion("F3", beyond, sala1, pasado.atTime(20, 0), 16000));
        cine.agregarFuncion(new Funcion("F4", risa, sala2, manana.atTime(16, 30), 14000));
        cine.agregarFuncion(new Funcion("F5", risa, sala2, pasado.atTime(18, 0), 16000));
        cine.agregarFuncion(new Funcion("F6", sombras, sala1, manana.atTime(21, 0), 16000));
        cine.agregarFuncion(new Funcion("F7", sombras, sala2, tresDias.atTime(19, 0), 16000));
    }
}
