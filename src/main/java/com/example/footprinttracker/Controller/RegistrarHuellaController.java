package com.example.footprinttracker.Controller;

import com.example.footprinttracker.Model.Actividad;
import com.example.footprinttracker.Model.Categoria;
import com.example.footprinttracker.Model.Huella;
import com.example.footprinttracker.Services.HuellaService;
import com.example.footprinttracker.Utils.Utilidades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;

public class RegistrarHuellaController {


    HuellaService huellaService = new HuellaService();
    @FXML
    public ComboBox comboActividad;
    @FXML
    public ComboBox comboCategoria;
    @FXML
    public VBox listaHistorial;
    @FXML
    public TextField txtCantidad;

    public void initialize(){
        mostrarHuellasUsuario();
        configurarComboCategorias();

        //LISTENER AL COMBO CATEGORIA PARA ACTUALIZAR SUS ACTIVIDADES
        comboCategoria.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Si ha seleccionado una categoría, cargamos sus actividades
                comboActividad.getItems().clear();
                List<Actividad> actividades = huellaService.cargarActividadesPorCategoria((Categoria) newSelection);
                comboActividad.getItems().addAll(actividades);
                comboActividad.setDisable(false); // Habilitamos el combo
            } else {
                // Si no hay selección, limpiamos y deshabilitamos
                comboActividad.getItems().clear();
                comboActividad.setDisable(true);
            }
        });

    }

    public void registrarHuella(ActionEvent actionEvent) {
        try {
            if(comboActividad.isDisable() || comboCategoria.isDisable() || txtCantidad.getText().isEmpty()){
                Utilidades.mostrarAlerta("Error", "No pueden haber campos vacíos");
                return;
            }

            if (Integer.parseInt(txtCantidad.getText()) <= 0) {
                Utilidades.mostrarAlerta("Error", "Cantidad invalida");
            }

            Actividad actividad = (Actividad) comboActividad.getSelectionModel().getSelectedItem();
            Categoria categoria = (Categoria) comboCategoria.getSelectionModel().getSelectedItem();
            Integer cantidad = Integer.parseInt(txtCantidad.getText());

            huellaService.addHuella(categoria, actividad, cantidad, categoria.getUnidad());
            mostrarHuellasUsuario();
        }catch (NumberFormatException e){
            Utilidades.mostrarAlerta("Error", "Por favor, introduce un número válido (ej: 10.5).");
        }
    }

    private void mostrarHuellasUsuario() {
        listaHistorial.getChildren().clear();
       List<Huella> misHuellas = huellaService.cargarHuellasUsuario();

        try {
            for (Huella huella : misHuellas) {
                // Cargar el FXML de la fila
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/footprinttracker/view/item_huella.fxml"));

                // Obtener el nodo raíz (el HBox)
                HBox fila = loader.load();

                //  Obtener el controlador de esa PEQUEÑA fila y pasarle los datos
                HuellaItemController itemController = loader.getController();
                itemController.setDatos(huella);

                //  Añadir la fila visual a la lista del dashboard
                listaHistorial.getChildren().add(fila);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void configurarComboCategorias() {
        List<Categoria> categorias = huellaService.cargarCategorias();
        comboCategoria.getItems().addAll(categorias);

        // Configurar qué texto se muestra (para que no salga el hash raro)
        comboCategoria.setConverter(new javafx.util.StringConverter<Categoria>() {
            @Override
            public String toString(Categoria categoria) {
                return (categoria != null) ? categoria.getNombre() : "";
            }

            @Override
            public Categoria fromString(String string) {
                return null;
            }
        });

        // Convertidor para ACTIVIDAD
        comboActividad.setConverter(new StringConverter<Actividad>() {
            @Override
            public String toString(Actividad a) { return (a != null) ? a.getNombreActividad() : ""; }
            @Override
            public Actividad fromString(String s) { return null; }
        });
    }
}
