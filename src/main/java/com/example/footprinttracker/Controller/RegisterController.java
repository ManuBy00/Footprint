package com.example.footprinttracker.Controller;

import com.example.footprinttracker.DAO.UsuarioDAO;
import com.example.footprinttracker.Model.Habito;
import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Services.UsuariosService;
import com.example.footprinttracker.Utils.Utilidades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;

public class RegisterController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblError;


    private UsuariosService usuariosService = new UsuariosService();

    /**
     * Gestiona la acción del botón de registro.
     * * Recoge los datos introducidos en los campos de texto (nombre, email, contraseña)
     * y delega la validación y creación al servicio. Si el registro tiene éxito,
     * redirige al usuario a la pantalla de inicio de sesión; si falla, muestra un error.
     */
    @FXML
    public void handleRegister() {
        String nombre = txtNombre.getText();
        String email = txtEmail.getText();
        String pass = txtPassword.getText();
        String confirmPass = txtConfirmPassword.getText();

        boolean guardado = usuariosService.registrarUsuario(nombre, email, pass, confirmPass);

        if (guardado) {
            System.out.println("Usuario registrado con éxito");
            volverLogin();

        } else {
            mostrarError("Error al guardar el usuario.");
        }
    }

    @FXML
    public void volverLogin() {
        Utilidades.cambiarPantalla(txtNombre, "/com/example/footprinttracker/view/login_view.fxml", "Login");
    }


    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}