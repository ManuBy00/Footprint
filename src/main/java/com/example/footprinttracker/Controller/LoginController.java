package com.example.footprinttracker.Controller;

import com.example.footprinttracker.DAO.UsuarioDAO;
import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Services.UsuariosService;
import com.example.footprinttracker.Utils.Sesion;
import com.example.footprinttracker.Utils.Utilidades;
import jakarta.transaction.Transactional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    public TextField txtEmail;
    @FXML
    public PasswordField txtPassword;
    @FXML
    public Button btnLogin;
    @FXML
    public Label lblError;
    @FXML
    public Hyperlink linkRegistro;

    public void handleLogin(ActionEvent actionEvent) {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        //  Validación de formato
        if (!Utilidades.validarCorreo(email)) {
            Utilidades.mostrarAlerta("Formato Incorrecto", "Por favor, introduce un email válido.");
            return;
        }

        if (email.isEmpty() || password.isEmpty()) {
            Utilidades.mostrarAlerta("Campos vacíos", "No puede haber campos vacíos");
            return;
        }

        UsuariosService usuariosService = new UsuariosService();
        // llama a usuarios service para la lógica de inicio de sesión
        if (usuariosService.login(email, password)) {
            // --- LOGIN EXITOSO ---
            Utilidades.cambiarPantalla(btnLogin, "/com/example/footprinttracker/view/main_view.fxml", "Dashboard");
        } else {
            // --- LOGIN FALLIDO ---
            Utilidades.mostrarAlerta("Error de Acceso", "El email o la contraseña son incorrectos.");
        }
    }

    public void abrirRegistro(ActionEvent actionEvent) {
            Utilidades.cambiarPantalla(btnLogin, "/com/example/footprinttracker/view/register_view.fxml", "Registro");

    }
}

