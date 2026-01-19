package com.example.footprinttracker.Controller;

import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Services.UsuariosService;
import com.example.footprinttracker.Utils.Sesion;
import com.example.footprinttracker.Utils.Utilidades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfileController {
    @FXML
    public TextField txtNombre;
    @FXML
    public TextField txtEmail;
    public PasswordField txtPassConfirm;
    public PasswordField txtPassNueva;
    public PasswordField txtPassActual;
    UsuariosService usuariosService =  new UsuariosService();

    public void initialize(){
        txtEmail.setText(Sesion.getInstance().getUsuarioIniciado().getEmail());
        txtNombre.setText(Sesion.getInstance().getUsuarioIniciado().getNombre());
    }

    public void actualizarPerfil(ActionEvent actionEvent) {
        String nombreNuevo = txtNombre.getText();
        if (nombreNuevo.isEmpty()) {
            Utilidades.mostrarAlerta("Error", "Campo vacío");
            return;
        }
        if (usuariosService.cambiarNombre(nombreNuevo)) {
            Utilidades.mostrarAlerta("Nombre actualizado", "Usuario actualizado correctamente");
        }else{
            Utilidades.mostrarAlerta("Error", "El nombre debe tener al menos 4 caracteres");
        }

    }

    public void cambiarPassword(ActionEvent actionEvent) {
        String passActual = txtPassActual.getText();
        String passConfirm = txtPassConfirm.getText();
        String passNuevo = txtPassNueva.getText();

        if (passActual.isEmpty() || passConfirm.isEmpty() || passNuevo.isEmpty()) {
            Utilidades.mostrarAlerta("Error", "Los campos están vacíos");
            return;
        }

        if (!(passNuevo.equals(passConfirm))) {
                Utilidades.mostrarAlerta("Error", "La nueva contraseña no coincide");
        }else {
            if (usuariosService.cambiarPassword(passActual, passNuevo)){
                   Utilidades.mostrarAlerta("Confirmación", "Se ha cambiado la contraseña correctamente");
            }else{
                   Utilidades.mostrarAlerta("Error", "La contraseña actual no es correcta");
            }
        }
    }

    public void eliminarCuenta(ActionEvent actionEvent) {
        if (Utilidades.mostrarConfirmacion("Confirmar", "Seguro que quiere eliminar la cuenta?")){
            usuariosService.eliminarUsuario();
            usuariosService.logOut();
            Utilidades.cambiarPantalla(txtNombre, "/com/example/footprinttracker/view/login_view.fxml", "Incio de sesion");
        }else{
            return;
        }
    }
}
