package com.example.footprinttracker.Controller;

import com.example.footprinttracker.Model.Habito; // Tu entidad actualizada
import com.example.footprinttracker.Model.Recomendacion;
import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Services.HabitosService;
import com.example.footprinttracker.Services.RecomendacionService;
import com.example.footprinttracker.Utils.Sesion;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;

public class HabitosController implements Initializable {

    @FXML public VBox containerRecomendaciones;
    @FXML private VBox containerHabitos;
    @FXML private ProgressBar barraProgreso;
    @FXML private Label lblProgresoTexto;

    private List<Habito> listaHabitos;
    private List<Recomendacion> listaRecomendaciones;
    HabitosService habitosService =  new HabitosService();
    RecomendacionService recomendacionService =  new RecomendacionService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        renderizarListaHabitos();
        renderizarListaRedomendaciones();
        calcularProgreso();
    }

    /**
     * Obtiene el usuario conectado y descarga de la base de datos su lista de hábitos
     * y las recomendaciones personalizadas para tener los datos listos antes de pintar la pantalla.
     */
    private void cargarDatos() {
        Usuario u = Sesion.getInstance().getUsuarioIniciado();
        listaHabitos = habitosService.listaHabitosUsuario(u);
        listaRecomendaciones = recomendacionService.getRecomendacionesUsuario();
    }

    /**
     * Borra la lista visual actual y crea una nueva fila (cargando el FXML item_habito)
     * por cada hábito que tenga el usuario. También configura qué debe pasar cuando se
     * completa un hábito (recalcular barra) o se elimina (borrarlo).
     */
    private void renderizarListaHabitos() {
        containerHabitos.getChildren().clear();

        try {
            for (Habito h : listaHabitos) {
                // Cargamos la vista de fila (item_habito.fxml)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/footprinttracker/view/habito_item.fxml"));
                Parent nodo = loader.load();

                // Obtenemos el controlador y le pasamos ESTE hábito
                HabitoItemController itemController = loader.getController();

                // Le pasamos el objeto y una función lambda para recalcular el progreso al hacer click
                itemController.setDatos(h, () -> this.calcularProgreso(), (habitoABorrar) -> this.eliminarHabito(habitoABorrar));

                containerHabitos.getChildren().add(nodo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Limpia el contenedor de recomendaciones y añade una tarjeta visual por cada
     * consejo que el sistema haya generado para el usuario.
     */
    private void renderizarListaRedomendaciones(){
        containerRecomendaciones.getChildren().clear();
        try {
            for (Recomendacion r : listaRecomendaciones) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/footprinttracker/view/item_recomendacion.fxml"));
                Parent nodo = loader.load();
                RecomendacionItemController itemController = loader.getController();
                itemController.setDatos(r);
                containerRecomendaciones.getChildren().add(nodo);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recorre la lista de hábitos para ver cuántos tienen fecha de "hoy" (están completados).
     * Luego calcula el porcentaje (completados / total) y actualiza la barra de progreso y el texto.
     */
    private void calcularProgreso() {
        if (listaHabitos == null || listaHabitos.isEmpty()) {
            barraProgreso.setProgress(0);
            lblProgresoTexto.setText("0/0");
            return;
        }

        LocalDate hoy = LocalDate.now();
        int completados = 0;

        for (Habito h : listaHabitos) {
            if (h.getUltimaFecha() != null) {
                // Convertimos Instant a LocalDate para comparar solo el día
                LocalDate fechaHabito = h.getUltimaFecha().atZone(ZoneId.systemDefault()).toLocalDate();
                if (fechaHabito.isEqual(hoy)) {
                    completados++;
                }
            }
        }

        int total = listaHabitos.size();
        double porcentaje = (double) completados / total;

        barraProgreso.setProgress(porcentaje);
        lblProgresoTexto.setText(completados + "/" + total);
    }


    /**
     * Abre una ventana emergente (modal) para que el usuario rellene el formulario de un nuevo hábito.
     * Cuando esa ventana se cierra, se recargan los datos y la interfaz para mostrar el nuevo registro.
     */
    @FXML
    public void abrirNuevoHabito(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/footprinttracker/view/modal_nuevo_habito.fxml"));
        try {
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Habito");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            cargarDatos();
            renderizarListaHabitos();
            renderizarListaRedomendaciones();
            calcularProgreso();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra una alerta de confirmación. Si el usuario acepta, borra el hábito de la base de datos,
     * lo quita de la lista en memoria y actualiza toda la pantalla.
     */
    private void eliminarHabito(Habito habito) {
        //  Preguntar confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Hábito");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que quieres dejar de seguir el hábito: " +
                habito.getIdActividad().getNombreActividad() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            // Borrar de BD
            habitosService.deleteHabito(habito);

            // Borrar de la lista en memoria
            listaHabitos.remove(habito);

            // Actualizar la pantalla
            cargarDatos();
            renderizarListaHabitos();
            renderizarListaRedomendaciones();
            calcularProgreso();
        }
    }


}