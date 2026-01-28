package com.example.footprinttracker.Controller;

import com.example.footprinttracker.Model.Actividad;
import com.example.footprinttracker.Model.Categoria;
import com.example.footprinttracker.Model.Huella;
import com.example.footprinttracker.Services.HuellaService;
import com.example.footprinttracker.Services.ReporteService;
import com.example.footprinttracker.Utils.Utilidades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.example.footprinttracker.Utils.Utilidades.mostrarAlerta;

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
    @FXML public Button btnExportar;

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

    /**
     * Gestiona el evento de registro de una nueva huella.
     * Valida que los campos no estén vacíos y que la cantidad sea numérica y positiva.
     * Si los datos son correctos, invoca al servicio para guardar el registro y actualiza la lista visual.
     *
     * @param actionEvent Evento del botón
     */
    public void registrarHuella(ActionEvent actionEvent) {
        try {
            if(comboActividad.isDisable() || comboCategoria.isDisable() || txtCantidad.getText().isEmpty()){
                mostrarAlerta("Error", "No pueden haber campos vacíos");
                return;
            }

            if (Integer.parseInt(txtCantidad.getText()) <= 0) {
                mostrarAlerta("Error", "Cantidad invalida");
            }

            Actividad actividad = (Actividad) comboActividad.getSelectionModel().getSelectedItem();
            Categoria categoria = (Categoria) comboCategoria.getSelectionModel().getSelectedItem();
            Integer cantidad = Integer.parseInt(txtCantidad.getText());

            huellaService.addHuella(categoria, actividad, cantidad, categoria.getUnidad());
            mostrarHuellasUsuario();
        }catch (NumberFormatException e){
            mostrarAlerta("Error", "Por favor, introduce un número válido (ej: 10.5).");
        }
    }

    /**
     * Refresca la lista visual del historial de registros.
     * Limpia el contenedor actual y carga dinámicamente un componente FXML  item_huella.fxml
     * por cada registro recuperado de la base de datos, asignándole su controlador y lógica de eliminación.
     */
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
                itemController.setDatos(huella, (huella1 -> {
                    eliminarHuella(huella1);
                }));

                //  Añadir la fila visual a la lista del dashboard
                listaHistorial.getChildren().add(fila);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicializa y configura los selectores (ComboBox) de la interfaz.
     * Carga las categorías desde la base de datos y define los code StringConverter para
     * mostrar los nombres legibles de las Categorías y Actividades en lugar de sus referencias de objeto.
     */
    public void configurarComboCategorias() {
        List<Categoria> categorias = huellaService.cargarCategorias();
        comboCategoria.getItems().addAll(categorias);

        // Configurar qué texto se muestra (para que no salga el hash)
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

    /**
     * Proceso de eliminación de un registro
     * Solicita confirmación al usuario mediante un diálogo. Si se acepta, elimina el registro
     * a través del servicio y refresca la lista de la interfaz.
     * @param huella El objeto huella a eliminar.
     */
    private void eliminarHuella(Huella huella) {
        // Preguntar confirmación
        boolean confirmar = Utilidades.mostrarConfirmacion("Eliminar", "¿Seguro que quieres borrar este registro?");

        if (confirmar) {
            // 2. Borrar de la Base de Datos
            if (huellaService.deleteHuella(huella)) {

                // Refrescar la lista visualmente
                mostrarHuellasUsuario();

                mostrarAlerta("Eliminado", "Huella eliminada correctamente.");
            } else {
                mostrarAlerta("Error", "No se pudo eliminar la huella.");
            }
        }
    }

    /**
     * Gestiona la exportación del historial completo a un archivo CSV.
     * Abre un diálogo nativo de selección de archivo FileChooser para que el usuario
     * elija la ubicación y nombre del archivo antes de generar el reporte.
     */
    @FXML
    private void onDescargarReporteClick() {

        // Elegir dónde guardar
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar CSV");
        fileChooser.setInitialFileName("historial_huella.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                ReporteService reporteService = new ReporteService();
                // Llamada al servicio
                reporteService.generarCSV(file.getAbsolutePath());
                mostrarAlerta("Éxito", "Archivo guardado correctamente.");

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
            }
        }
    }

}
