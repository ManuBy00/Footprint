package com.example.footprinttracker.Controller;

import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Services.UsuariosService;
import com.example.footprinttracker.Utils.Sesion;
import com.example.footprinttracker.Utils.Utilidades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class DashboardController {
    @FXML
    public Button profileButton;
    @FXML
    public Label bienvenidoLabel;
    public BorderPane mainContainer;

    // Variable para guardar la vista de "Inicio" (las gráficas) y no tener que recargarlas
    private Node vistaInicio;


    public void initialize(){
        Usuario usuario = Sesion.getInstance().getUsuarioIniciado();

        bienvenidoLabel.setText("Bienvenido "+usuario.getNombre());

        vistaInicio = mainContainer.getCenter();
    }

    public void abrirPerfil(ActionEvent actionEvent) {
        //Utilidades.cambiarPantalla(profileButton, "/com/example/footprinttracker/view/profile_view.fxml", "Perfil");
        try {
            // Cargar el FXML del perfil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/footprinttracker/view/profile_view.fxml"));
            Parent perfilView = loader.load();

            // REEMPLAZAR EL CENTRO
            mainContainer.setCenter(perfilView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void mostrarInicio(ActionEvent event) {
        bienvenidoLabel.setText("Bienvenido, " +  Sesion.getInstance().getUsuarioIniciado().getNombre());
        mainContainer.setCenter(vistaInicio);
    }


    public void handleLogout(ActionEvent actionEvent) {
        UsuariosService usuariosService = new UsuariosService();
        usuariosService.logOut();
        Utilidades.cambiarPantalla(profileButton, "/com/example/footprinttracker/view/login_view.fxml", "Inicio de sesion");
    }

    public void abrirHuellasView(ActionEvent actionEvent) {
        try {
            // Cargar el FXML del perfil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/footprinttracker/view/huellas_view.fxml"));
            Parent huellasView = loader.load();

            // REEMPLAZAR EL CENTRO
            mainContainer.setCenter(huellasView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
