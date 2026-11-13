package org.example.proyectocine.model;

import javafx.scene.chart.*;
import java.util.Map;

public class ReporteVisual {

    /**
     * Construye un diagrama de barras.
     */
    public static BarChart<String, Number> crearBarChart(ReporteStrategy reporte, String etiquetaEjeX, String etiquetaEjeY) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(etiquetaEjeX);
        yAxis.setLabel(etiquetaEjeY);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(reporte.getTitulo());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Datos Operativos");

        Map<String, Number> datos = reporte.obtenerDatosGrafico();
        for (Map.Entry<String, Number> entry : datos.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
        return barChart;
    }

    /**
     * Construye un diagrama de pastel.
     */
    public static PieChart crearPieChart(ReporteStrategy reporte) {
        PieChart pieChart = new PieChart();
        pieChart.setTitle(reporte.getTitulo());

        Map<String, Number> datos = reporte.obtenerDatosGrafico();
        for (Map.Entry<String, Number> entry : datos.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue().doubleValue()));
        }

        return pieChart;
    }

    /**
     * Construye un gráfico de líneas.
     */
    public static LineChart<String, Number> crearLineChart(ReporteStrategy reporte, String ejeX, String ejeY) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(ejeX);
        yAxis.setLabel(ejeY);

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(reporte.getTitulo());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Comportamiento Temporal");

        Map<String, Number> datos = reporte.obtenerDatosGrafico();
        for (Map.Entry<String, Number> entry : datos.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        lineChart.getData().add(series);
        return lineChart;
    }
}