package org.example.proyectocine.viewController;

import javafx.fxml.FXML;

public class BienvenidaViewController {

    @FXML
    private void onCliente() {
        abrirLogin(Perfil.CLIENTE);
    }

    @FXML
    private void onAdministrador() {
        abrirLogin(Perfil.ADMINISTRADOR);
    }

    private void abrirLogin(Perfil perfil) {
        LoginViewController login = SceneManager.cambiarA(Vista.LOGIN);
        login.configurar(perfil);
    }
}
