package com.example.footprinttracker.Controller;

import com.example.footprinttracker.Model.Huella;
import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Services.HuellaService;
import com.example.footprinttracker.Services.UsuariosService;
import com.example.footprinttracker.Utils.Sesion;
import com.example.footprinttracker.Utils.Utilidades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    public Button profileButton;
    @FXML
    public Label bienvenidoLabel;
    @FXML
    public BorderPane mainContainer;
    @FXML
    public Label huellaMensualTxt;
    @FXML
    public Label registroMensual;

    // Variable para guardar la vista de "Inicio" (las gráficas) y no tener que recargarlas
    private Node vistaInicio;
    Usuario usuario = Sesion.getInstance().getUsuarioIniciado();


    @Override
    public void initialize(URL location, ResourceBundle resources){
        setViewData();
        vistaInicio = mainContainer.getCenter();
    }

    /**
     * Carga la vista del perfil de usuario desde el archivo FXML y la muestra
     * en la zona central de la ventana principal, reemplazando el contenido anterior.
     */
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

    /**
     * Vuelve a mostrar la pantalla de inicio (Dashboard).
     * Actualiza el saludo con el nombre del usuario y refresca los datos numéricos
     * (huella mensual y registros) llamando a setViewData() antes de mostrar la vista.
     */
    @FXML
    public void mostrarInicio(ActionEvent event) {
        bienvenidoLabel.setText("Bienvenido, " +  Sesion.getInstance().getUsuarioIniciado().getNombre());
        setViewData();
        mainContainer.setCenter(vistaInicio);

    }

    /**
     * Cierra la sesión del usuario actual llamando al servicio correspondiente
     * y redirige la aplicación completa a la pantalla de inicio de sesión (Login).
     */
    public void handleLogout(ActionEvent actionEvent) {
        UsuariosService usuariosService = new UsuariosService();
        usuariosService.logOut();
        Utilidades.cambiarPantalla(profileButton, "/com/example/footprinttracker/view/login_view.fxml", "Inicio de sesion");
    }

    /**
     * Navega a la sección de Huellas. Carga la vista de registro e historial
     * y la coloca en el panel central de la interfaz.
     */
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

    /**
     * Calcula y muestra los datos resumen del dashboard principal.
     * Obtiene la fecha actual, consulta el impacto total de CO2 y el número de registros
     * realizados en el mes actual, y actualiza las etiquetas de texto con los resultados formateados.
     */
    public void setViewData(){
        HuellaService  huellaService = new HuellaService();
        bienvenidoLabel.setText("Bienvenido "+usuario.getNombre());

        LocalDate fecha = LocalDate.now();
        int mes = fecha.getMonthValue();
        int year = fecha.getYear();
        double impactoMensual = huellaService.calcularHuellaMensual(mes, year);
        int numRegistros = huellaService.registroMensual();

        huellaMensualTxt.setText(String.format("%.2f", impactoMensual));
        registroMensual.setText(String.format("%.2s", numRegistros));
    }

    /**
     * Carga y muestra la vista de Gestión de Hábitos en el contenedor central de la aplicación.
     */
    @FXML
    private void abrirHabitos(ActionEvent event) {
        try {
            FXMLLoader vista = new FXMLLoader(getClass().getResource("/com/example/footprinttracker/view/habitos_view.fxml"));
            Parent habitosView = vista.load();
            mainContainer.setCenter(habitosView);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Navega a la pantalla de Análisis y Gráficas, cargando su vista FXML en el área principal.
     */
    @FXML
    private void abrirAnalisis(ActionEvent actionEvent) {
        try {
            FXMLLoader vista = new FXMLLoader(getClass().getResource("/com/example/footprinttracker/view/analisis_view.fxml"));
            Parent analisisView = vista.load();
            mainContainer.setCenter(analisisView);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
