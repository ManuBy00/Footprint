package com.example.footprinttracker.Controller;

import com.example.footprinttracker.Model.Recomendacion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RecomendacionItemController {

    @FXML
    private VBox boxIcono;
    @FXML private Text txtIcono;
    @FXML private Label lblTitulo;
    @FXML private Label lblDescripcion;
    @FXML private Label lblLink; // El botón de "Saber más"

    /**
     * Configura los datos y el formato con el que aparecerán en pantalla (en el item)
     * @param rec
     */
    public void setDatos(Recomendacion rec) {
        //  Título visual = Nombre de la Categoría (ya que no hay título en tabla)
        lblTitulo.setText("Consejo de " + rec.getIdCategoria().getNombre());

        //  Descripción + Impacto Estimado
        String texto = rec.getDescripcion();
        if (rec.getImpactoEstimado() != null && rec.getImpactoEstimado() > 0) {
            texto += "\n(Impacto estimado: -" + rec.getImpactoEstimado() + " kg CO₂)";
        }
        lblDescripcion.setText(texto);

        // 3. Ocultar el link "Saber más" porque la tabla no tiene URL
        if (lblLink != null) {
            lblLink.setVisible(false);
            lblLink.setManaged(false); // Para que no ocupe espacio
        }

        // 4. Estilos según categoría
        configurarEstilo(rec.getIdCategoria().getNombre());
    }

    private void configurarEstilo(String categoria) {
        // Limpiar estilos anteriores
        boxIcono.getStyleClass().removeAll("icon-box-yellow", "icon-box-green", "icon-box-blue");

        if (categoria == null) return;

        switch (categoria.toLowerCase()) {
            case "energía": case "electricidad": case "hogar":
                boxIcono.getStyleClass().add("icon-box-yellow");
                txtIcono.setText("⚡");
                break;
            case "agua":
                boxIcono.getStyleClass().add("icon-box-blue"); // Define este estilo en CSS si quieres
                txtIcono.setText("💧");
                break;
            case "alimentación":
                boxIcono.getStyleClass().add("icon-box-green");
                txtIcono.setText("🥗");
                break;
            case "transporte": case "movilidad":
                boxIcono.getStyleClass().add("icon-box-green");
                txtIcono.setText("🚲");
                break;
            default:
                boxIcono.getStyleClass().add("icon-box-green");
                txtIcono.setText("🌿");
        }
    }
}
