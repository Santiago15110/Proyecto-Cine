package org.example.proyectocine.viewController;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.example.proyectocine.controller.ReporteController;
import org.example.proyectocine.model.ReporteStrategy;
import org.example.proyectocine.model.TipoReporte;

import java.io.File;
import java.time.LocalDate;

public class ReportesViewController {

    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private ListView<TipoReporte> lvTiposReporte;
    @FXML private RadioButton rbTexto;
    @FXML private RadioButton rbVisual;
    @FXML private ComboBox<String> cmbTipoGrafico;
    @FXML private TextArea txtResultadoTexto;
    @FXML private StackPane panelGrafico;
    @FXML private Label lblMensaje;

    private final ReporteController reporteController = new ReporteController();
    private ReporteStrategy reporteActual;
    private TipoReporte tipoActual;

    @FXML
    public void initialize() {
        lvTiposReporte.getItems().setAll(reporteController.obtenerTiposReporte());
        lvTiposReporte.getSelectionModel().selectedItemProperty().addListener((obs, anterior, tipo) -> {
            boolean usaFechas = tipo == null || tipo.usaRangoFechas();
            dpFechaInicio.setDisable(!usaFechas);
            dpFechaFin.setDisable(!usaFechas);
        });

        dpFechaInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dpFechaFin.setValue(LocalDate.now());

        cmbTipoGrafico.getItems().addAll("Diagrama de Barras", "Diagrama de Pastel", "Gráfico de Líneas");
        cmbTipoGrafico.getSelectionModel().selectFirst();
        rbTexto.setSelected(true);
        panelGrafico.setVisible(false);
    }

    @FXML
    public void onGenerarReporte() {
        TipoReporte tipo = lvTiposReporte.getSelectionModel().getSelectedItem();
        if (tipo == null) {
            lblMensaje.setText("Por favor, seleccione un tipo de reporte.");
            return;
        }
        LocalDate inicio = dpFechaInicio.getValue();
        LocalDate fin = dpFechaFin.getValue();
        if (tipo.usaRangoFechas() && inicio != null && fin != null && inicio.isAfter(fin)) {
            lblMensaje.setText("La fecha de inicio no puede ser posterior a la fecha fin.");
            return;
        }

        try {
            reporteActual = reporteController.generarReporte(tipo, inicio, fin);
            tipoActual = tipo;
            txtResultadoTexto.setText(reporteController.obtenerTextoReporte(reporteActual));
            actualizarGraficoVisual();
            lblMensaje.setText("Reporte generado exitosamente.");
        } catch (Exception e) {
            lblMensaje.setText("Error al generar el reporte: " + e.getMessage());
        }
    }

    @FXML
    public void onCambiarVista() {
        if (rbVisual.isSelected()) {
            cmbTipoGrafico.setDisable(false);
            txtResultadoTexto.setVisible(false);
            panelGrafico.setVisible(true);
            actualizarGraficoVisual();
        } else {
            cmbTipoGrafico.setDisable(true);
            panelGrafico.setVisible(false);
            txtResultadoTexto.setVisible(true);
        }
    }

    @FXML
    public void onCambiarTipoGrafico() {
        if (rbVisual.isSelected()) {
            actualizarGraficoVisual();
        }
    }

    private void actualizarGraficoVisual() {
        if (reporteActual == null) return;

        String tipoGrafico = cmbTipoGrafico.getValue();
        panelGrafico.getChildren().clear();

        if (tipoGrafico == null) return;

        switch (tipoGrafico) {
            case "Diagrama de Barras":
                BarChart<String, Number> barChart = ReporteVisual.crearBarChart(reporteActual, "Categoría", "Valor");
                panelGrafico.getChildren().add(barChart);
                break;
            case "Diagrama de Pastel":
                PieChart pieChart = ReporteVisual.crearPieChart(reporteActual);
                panelGrafico.getChildren().add(pieChart);
                break;
            case "Gráfico de Líneas":
                LineChart<String, Number> lineChart = ReporteVisual.crearLineChart(reporteActual, "Periodo", "Valor");
                panelGrafico.getChildren().add(lineChart);
                break;
        }
    }

    @FXML
    public void onExportarTxt() {
        exportar("txt", "Archivo de texto (*.txt)");
    }

    @FXML
    public void onExportarPdf() {
        exportar("pdf", "Documento PDF (*.pdf)");
    }

    private void exportar(String extension, String descripcion) {
        if (reporteActual == null) {
            lblMensaje.setText("Primero debe generar un reporte.");
            return;
        }
        File archivo = elegirArchivo(extension, descripcion);
        if (archivo == null) {
            lblMensaje.setText("Exportación cancelada.");
            return;
        }
        try {
            if (extension.equals("pdf")) {
                reporteController.exportarPdf(reporteActual, archivo.getAbsolutePath());
            } else {
                reporteController.exportarTxt(reporteActual, archivo.getAbsolutePath());
            }
            lblMensaje.setText("Reporte guardado en " + archivo.getAbsolutePath());
        } catch (Exception e) {
            lblMensaje.setText("Error al exportar " + extension.toUpperCase() + ": " + e.getMessage());
        }
    }

    private File elegirArchivo(String extension, String descripcion) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar reporte");
        selector.setInitialFileName(reporteController.nombreSugerido(tipoActual, extension));
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter(descripcion, "*." + extension));
        File documentos = new File(System.getProperty("user.home"), "Documents");
        selector.setInitialDirectory(documentos.isDirectory() ? documentos : new File(System.getProperty("user.home")));

        File archivo = selector.showSaveDialog(lblMensaje.getScene().getWindow());
        if (archivo != null && !archivo.getName().toLowerCase().endsWith("." + extension)) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + "." + extension);
        }
        return archivo;
    }

    @FXML
    public void onVolver() {
        Navegacion.irAlPanel();
    }
}
