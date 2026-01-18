package com.example.footprinttracker.Controller;

import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Utils.Sesion;
import com.example.footprinttracker.Utils.Utilidades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController {
    @FXML
    public Button profileButton;
    @FXML
    public Label bienvenidoLabel;


    public void initialize(){
        Usuario usuario = Sesion.getInstance().getUsuarioIniciado();

        bienvenidoLabel.setText("Bienvenido "+usuario.getNombre());
    }

    public void abrirPerfil(ActionEvent actionEvent) {
        Utilidades.cambiarPantalla(profileButton, "/com/example/footprinttracker/view/profile_view.fxml", "Perfil");
    }

    public void handleLogout(ActionEvent actionEvent) {

    }
}
