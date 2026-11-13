package org.example.proyectocine.viewController;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.example.proyectocine.model.*;

import java.time.LocalDate;

public class ReportesViewController {

    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private ListView<String> lvTiposReporte;
    @FXML private RadioButton rbTexto;
    @FXML private RadioButton rbVisual;
    @FXML private ComboBox<String> cmbTipoGrafico;
    @FXML private TextArea txtResultadoTexto;
    @FXML private StackPane panelGrafico;
    @FXML private Label lblMensaje;

    private final CineFacade cineFacade = new CineFacade();
    private ReporteStrategy reporteActual;

    @FXML
    public void initialize() {
        lvTiposReporte.getItems().addAll(
                "Reporte de Ventas",
                "Reporte de Entradas por Función",
                "Reporte de Ocupación de Salas",
                "Reporte de Confitería",
                "Reporte de Recargas",
                "Reporte de Cancelaciones",
                "Reporte de Puntos"
        );

        cmbTipoGrafico.getItems().addAll("Diagrama de Barras", "Diagrama de Pastel", "Gráfico de Líneas");
        cmbTipoGrafico.getSelectionModel().selectFirst();
        rbTexto.setSelected(true);
        panelGrafico.setVisible(false);
    }

    @FXML
    public void onGenerarReporte() {
        String seleccion = lvTiposReporte.getSelectionModel().getSelectedItem();
        LocalDate inicio = dpFechaInicio.getValue();
        LocalDate fin = dpFechaFin.getValue();

        if (seleccion == null) {
            lblMensaje.setText("Por favor, seleccione un tipo de reporte.");
            return;
        }

        try {
            switch (seleccion) {
                case "Reporte de Ventas":
                    reporteActual = cineFacade.crearReporteVentas(inicio, fin);
                    break;
                case "Reporte de Entradas por Función":
                    reporteActual = cineFacade.crearReporteEntradasFuncion(inicio, fin);
                    break;
                case "Reporte de Ocupación de Salas":
                    reporteActual = cineFacade.crearReporteOcupacionSalas();
                    break;
                case "Reporte de Confitería":
                    reporteActual = cineFacade.crearReporteConfiteria(inicio, fin);
                    break;
                case "Reporte de Recargas":
                    reporteActual = cineFacade.crearReporteRecargas(inicio, fin);
                    break;
                case "Reporte de Cancelaciones":
                    reporteActual = cineFacade.crearReporteCancelaciones(inicio, fin);
                    break;
                case "Reporte de Puntos":
                    reporteActual = cineFacade.crearReportePuntos(inicio, fin);
                    break;
            }

            if (reporteActual != null) {
                txtResultadoTexto.setText(cineFacade.generarReporteTexto(reporteActual));
                actualizarGraficoVisual();
                lblMensaje.setText("Reporte generado exitosamente.");
            }

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
        if (reporteActual == null) {
            lblMensaje.setText("Primero debe generar un reporte.");
            return;
        }
        try {
            cineFacade.exportarReporteTXT(reporteActual, "reporte_cinema.txt");
            lblMensaje.setText("Reporte exportado a TXT exitosamente.");
        } catch (Exception e) {
            lblMensaje.setText("Error al exportar TXT: " + e.getMessage());
        }
    }

    @FXML
    public void onExportarPdf() {
        if (reporteActual == null) {
            lblMensaje.setText("Primero debe generar un reporte.");
            return;
        }
        try {
            cineFacade.exportarReportePDF(reporteActual, "reporte_cinema.pdf");
            lblMensaje.setText("Reporte exportado a PDF exitosamente.");
        } catch (Exception e) {
            lblMensaje.setText("Error al exportar PDF: " + e.getMessage());
        }
    }

    @FXML
    public void onVolver() {
        SceneManager.cambiarA(Vista.INICIO);
    }

}
