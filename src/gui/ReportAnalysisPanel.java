package gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import services.InvoiceService;
import services.ItemService;
import utils.Forecast;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReportAnalysisPanel extends JPanel {
    private final InvoiceService invoiceService;
    private final ItemService itemService;

    public ReportAnalysisPanel(JFrame menuFrame, InvoiceService invoiceService, ItemService itemService) {
        this.invoiceService = invoiceService;
        this.itemService = itemService;

        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Reports and Analysis");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        JPanel leftPanel = new JPanel(new GridBagLayout());

        JLabel forecastLabel = new JLabel("Sales Forecast");
        forecastLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        leftPanel.add(forecastLabel, createGBC(0, 0));

        JPanel forecastPanel = createChartPanel();
        leftPanel.add(forecastPanel, createGBC(0, 1));

        GridBagConstraints gbcLeft = createGBC(0, 0);
        gbcLeft.weightx = 2.0;
        mainPanel.add(leftPanel, gbcLeft);

        JPanel rightPanel = new JPanel(new GridBagLayout());

        JLabel reportLabel = new JLabel("Stock Report");
        reportLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rightPanel.add(reportLabel, createGBC(0, 0));

        JPanel reportPanel = createReportPanel();
        rightPanel.add(reportPanel, createGBC(0, 1));

        JLabel analysisLabel = new JLabel("Income and Sales Analysis");
        analysisLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rightPanel.add(analysisLabel, createGBC(0, 2));

        JPanel analysisPanel = createAnalysisPanel();
        rightPanel.add(analysisPanel, createGBC(0, 3));

        GridBagConstraints gbcRight = createGBC(1, 0);
        gbcRight.weightx = 1.0;
        mainPanel.add(rightPanel, gbcRight);

        add(mainPanel, BorderLayout.CENTER);
    }

    private GridBagConstraints createGBC(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }

    private JPanel createChartPanel() {
        JFreeChart chart = createChart();
        return new ChartPanel(chart);
    }

    private JFreeChart createChart() {
        XYDataset dataset = createDataset();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "Month",
                "Sales",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(new Color(245, 245, 245));

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(34, 140, 240));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesToolTipGenerator(0, new StandardXYToolTipGenerator());

        renderer.setSeriesPaint(1, new Color(181, 43, 121));
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesToolTipGenerator(1, new StandardXYToolTipGenerator());

        plot.setRenderer(renderer);

        return chart;
    }

    private XYDataset createDataset() {
        DefaultXYDataset dataset = new DefaultXYDataset();

        try {
            List<Double> salesData = invoiceService.getMonthlySalesData();
            double[] salesArray = salesData.stream().mapToDouble(Double::doubleValue).toArray();
            double[] months = new double[salesArray.length];
            for (int i = 0; i < months.length; i++) {
                months[i] = i + 1;
            }

            double[][] data = new double[][]{months, salesArray};
            dataset.addSeries("Previous Sales", data);

            String forecast = Forecast.generateForecast(salesData);
            double forecastValue = Double.parseDouble(forecast);

            double[][] forecastData = new double[][]{{months.length, months.length + 1}, {salesArray[salesArray.length - 1], forecastValue}};
            dataset.addSeries("Forecast", forecastData);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }


    private JPanel createReportPanel() {
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        reportArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reportArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        try {
            String report = itemService.generateStockLevelReport();
            reportArea.setText(report);
        } catch (Exception e) {
            reportArea.setText(STR."Error generating report: \{e.getMessage()}");
        }

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAnalysisPanel() {
        JTextArea analysisArea = new JTextArea();
        analysisArea.setEditable(false);
        analysisArea.setLineWrap(true);
        analysisArea.setWrapStyleWord(true);
        analysisArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        analysisArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        try {
            String analysis = invoiceService.generateIncomeAndSalesAnalysis();
            analysisArea.setText(analysis);
        } catch (Exception e) {
            analysisArea.setText(STR."Error generating analysis: \{e.getMessage()}");
        }

        JScrollPane scrollPane = new JScrollPane(analysisArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}
