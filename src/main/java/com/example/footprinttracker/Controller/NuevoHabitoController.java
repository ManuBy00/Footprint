package com.example.footprinttracker.Controller;

import com.example.footprinttracker.DAO.ActividadDAO;
import com.example.footprinttracker.Model.Actividad;
import com.example.footprinttracker.Model.Habito;
import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Connection.ConnectionDB;
import com.example.footprinttracker.Services.HabitosService;
import com.example.footprinttracker.Utils.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;



public class NuevoHabitoController {

    @FXML private ComboBox<Actividad> comboActividad;
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField txtFrecuencia;
    HabitosService habitosService = new HabitosService();

    @FXML
    public void initialize() {
        cargarActividades();

        // Rellenar tipos predefinidos
        comboTipo.getItems().addAll("Diario", "Semanal", "Mensual");
        comboTipo.getSelectionModel().select("Diario");
    }

    /**
     * Carga las actividades desde la base de datos y las añade al desplegable (ComboBox)
     * para que el usuario pueda seleccionarlas al crear el hábito.
     */
    private void cargarActividades() {
        HabitosService habitosService = new HabitosService();
        List<Actividad> listaActividades = habitosService.listaActividades();
        comboActividad.getItems().addAll(listaActividades);

    }

    /**
     * Recoge los datos del formulario, valida que la frecuencia sea un número y que se haya
     * elegido una actividad. Si todo es correcto, crea el objeto Habito vinculado al usuario
     * actual y lo guarda en la base de datos.
     */
    @FXML
    private void guardarHabito() {
        // 1. Validaciones
        Actividad actividadSeleccionada = comboActividad.getValue();
        if (actividadSeleccionada == null) {
            mostrarAlerta("Error", "Debes seleccionar una actividad.");
            return;
        }

        String tipo = comboTipo.getValue();
        int frecuencia;
        try {
            frecuencia = Integer.parseInt(txtFrecuencia.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La frecuencia debe ser un número entero.");
            return;
        }

        // 2. Crear el objeto Habito
        Usuario usuarioLogueado = Sesion.getInstance().getUsuarioIniciado();

        Habito nuevoHabito = new Habito();
        nuevoHabito.setIdUsuario(usuarioLogueado); // Usando tu setter corregido
        nuevoHabito.setIdActividad(actividadSeleccionada);
        nuevoHabito.setTipo(tipo);
        nuevoHabito.setFrecuencia(frecuencia);
        nuevoHabito.setUltimaFecha(null); // Aún no realizado

        // 3. Guardar en Base de Datos (Hibernate)
        habitosService.addHabito(nuevoHabito);
    }


    /**
     * Cierra la ventana actual sin guardar cambios.
     */
    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    /**
     * Obtiene el escenario (Stage) actual a partir de un componente de la vista y lo cierra.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) txtFrecuencia.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}