package com.example.footprinttracker.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class Utilidades {




    /**
     * valida el correo con una regex
     * @param correo
     * @return true si el formato es correcto
     */
    public static boolean validarCorreo(String correo){
        String regex = "[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";
        return correo.matches(regex);
    }

    /**
     * Muestra una ventana de alerta para mostrar información
     * @param titulo
     * @param mensaje
     */
    public static void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra una ventana de confirmación y espera la respuesta del usuario.
     *
     * @param titulo El texto del encabezado de la alerta (ej. "Confirmar Eliminación").
     * @param mensaje El mensaje que se muestra al usuario (ej. "¿Está seguro?").
     * @return true si el usuario hace clic en Aceptar (OK), false si hace clic en Cancelar.
     */
    public static boolean confirmarAccion(String titulo, String mensaje) {
        // 1. Crear el objeto Alert de tipo CONFIRMATION
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        // 2. Configurar el texto del diálogo
        alert.setTitle("Confirmación");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);

        // 3. Mostrar el diálogo y esperar la respuesta
        Optional<ButtonType> result = alert.showAndWait();

        // 4. Devolver true si la respuesta es OK
        return result.isPresent() && result.get() == ButtonType.OK;
    }


    /**
     * Método estático para navegar entre pantallas manteniendo la ventana maximizada.
     * @param node El evento del botón (para obtener la ventana actual).
     * @param fxmlPath La ruta absoluta del archivo FXML (ej: "/com/example/footprinttracker/view/register_view.fxml").
     * @param titulo El título que quieres ponerle a la nueva ventana.
     */
    public static void cambiarPantalla(Node node, String fxmlPath, String titulo) {
        try {
            // 1. Cargamos el nuevo diseño (El cuadro, los botones, etc.)
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Utilidades.class.getResource(fxmlPath)));
            Parent newRoot = loader.load();

            // 2. Obtenemos la ventana actual y la escena actual
            Stage stage = (Stage) node.getScene().getWindow();
            Scene currentScene = stage.getScene();

            // En lugar de crear una 'new Scene' (que resetea el tamaño),
            // simplemente le decimos a la escena actual: "Cambia tu contenido por este nuevo".
            // Al no cambiar la escena, la ventana NO recalcula su tamaño.
            currentScene.setRoot(newRoot);

            // 3. (Opcional) Cambiar título si hace falta
            if (titulo != null && !titulo.isEmpty()) {
                stage.setTitle(titulo);
            }

            // 4. Aseguramos visualización (por si acaso)
            stage.show();

        } catch (IOException e) {
            System.err.println("Error IO: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Ruta FXML incorrecta: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null); // Opcional: quita el encabezado para que quede más limpio
        alert.setContentText(mensaje);

        // Muestra la ventana y espera (bloquea el código hasta que el usuario responda)
        Optional<ButtonType> result = alert.showAndWait();

        // Devuelve true SOLO si el usuario pulsó OK
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}


