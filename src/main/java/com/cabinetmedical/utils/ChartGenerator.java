package com.cabinetmedical.utils;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.*;
import org.jfree.data.general.*;
import org.jfree.data.time.*;
import org.jfree.chart.labels.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Générateur de graphiques avec JFreeChart.
 * Crée des visualisations professionnelles pour les statistiques.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class ChartGenerator {
    
    // Couleurs du thème
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color DANGER_COLOR = new Color(244, 67, 54);
    private static final Color INFO_COLOR = new Color(0, 188, 212);
    private static final Color PURPLE_COLOR = new Color(156, 39, 176);
    
    private static final Color[] CHART_COLORS = {
        PRIMARY_COLOR, SUCCESS_COLOR, WARNING_COLOR, 
        DANGER_COLOR, INFO_COLOR, PURPLE_COLOR,
        new Color(255, 152, 0), new Color(121, 85, 72)
    };
    
    /**
     * Crée un diagramme circulaire (camembert) des consultations par catégorie.
     * 
     * @param data Map catégorie -> nombre de consultations
     * @param title Titre du graphique
     * @return JPanel contenant le graphique
     */
    public static JPanel createPieChart(Map<String, Integer> data, String title) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        
        JFreeChart chart = ChartFactory.createPieChart3D(
            title,
            dataset,
            true,  // Légende
            true,  // Tooltips
            false  // URLs
        );
        
        // Style
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setDepthFactor(0.1);
        plot.setStartAngle(290);
        plot.setForegroundAlpha(0.8f);
        
        // Couleurs personnalisées
        int i = 0;
        for (String key : data.keySet()) {
            plot.setSectionPaint(key, CHART_COLORS[i % CHART_COLORS.length]);
            i++;
        }
        
        // Labels
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", 
            java.text.NumberFormat.getInstance(), 
            java.text.NumberFormat.getPercentInstance()
        ));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        
        return chartPanel;
    }
    
    /**
     * Crée un histogramme des revenus.
     * 
     * @param categories Labels des barres (ex: mois)
     * @param values Valeurs correspondantes
     * @param title Titre du graphique
     * @param xLabel Label de l'axe X
     * @param yLabel Label de l'axe Y
     * @return JPanel contenant le graphique
     */
    public static JPanel createBarChart(String[] categories, double[] values, 
            String title, String xLabel, String yLabel) {
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (int i = 0; i < categories.length; i++) {
            dataset.addValue(values[i], "Revenus", categories[i]);
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            title,
            xLabel,
            yLabel,
            dataset,
            PlotOrientation.VERTICAL,
            false, // Légende
            true,  // Tooltips
            false  // URLs
        );
        
        // Style
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Couleur des barres
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, PRIMARY_COLOR);
        renderer.setDrawBarOutline(false);
        
        // Labels des valeurs sur les barres
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
            "{2} DH", java.text.NumberFormat.getInstance()
        ));
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 10));
        renderer.setDefaultPositiveItemLabelPosition(
            new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER)
        );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 350));
        
        return chartPanel;
    }
    
    /**
     * Crée une courbe d'évolution des consultations.
     * 
     * @param labels Labels de l'axe X (ex: semaines)
     * @param values Valeurs correspondantes
     * @param title Titre du graphique
     * @param xLabel Label de l'axe X
     * @param yLabel Label de l'axe Y
     * @return JPanel contenant le graphique
     */
    public static JPanel createLineChart(String[] labels, int[] values, 
            String title, String xLabel, String yLabel) {
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (int i = 0; i < labels.length; i++) {
            dataset.addValue(values[i], "Consultations", labels[i]);
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            title,
            xLabel,
            yLabel,
            dataset,
            PlotOrientation.VERTICAL,
            false, // Légende
            true,  // Tooltips
            false  // URLs
        );
        
        // Style
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(false);
        
        // Style de la ligne
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, PRIMARY_COLOR);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-5, -5, 10, 10));
        renderer.setSeriesShapesFilled(0, true);
        
        // Labels des points
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        
        return chartPanel;
    }
    
    /**
     * Crée un graphique comparatif (payé vs impayé).
     * 
     * @param paye Montant payé
     * @param impaye Montant impayé
     * @param title Titre du graphique
     * @return JPanel contenant le graphique
     */
    public static JPanel createComparisonChart(double paye, double impaye, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(paye, "Paye", "Statut");
        dataset.addValue(impaye, "Impaye", "Statut");
        
        JFreeChart chart = ChartFactory.createBarChart(
            title,
            "",
            "Montant (DH)",
            dataset,
            PlotOrientation.HORIZONTAL,
            true,  // Légende
            true,  // Tooltips
            false  // URLs
        );
        
        // Style
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, SUCCESS_COLOR);
        renderer.setSeriesPaint(1, DANGER_COLOR);
        renderer.setDrawBarOutline(false);
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
            "{2} DH", java.text.NumberFormat.getInstance()
        ));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 200));
        
        return chartPanel;
    }
    
    /**
     * Crée un panneau de KPIs (indicateurs clés).
     * 
     * @param totalConsultations Nombre total de consultations
     * @param chiffreAffaires Chiffre d'affaires
     * @param tauxPaiement Taux de paiement
     * @param consultationsJour Consultations du jour
     * @return JPanel contenant les KPIs
     */
    public static JPanel createKPIPanel(int totalConsultations, double chiffreAffaires, 
            double tauxPaiement, int consultationsJour) {
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        panel.add(createKPICard("Consultations Totales", 
            String.valueOf(totalConsultations), PRIMARY_COLOR));
        panel.add(createKPICard("Chiffre d'Affaires", 
            String.format("%.2f DH", chiffreAffaires), SUCCESS_COLOR));
        panel.add(createKPICard("Taux de Paiement", 
            String.format("%.1f%%", tauxPaiement), 
            tauxPaiement >= 80 ? SUCCESS_COLOR : (tauxPaiement >= 50 ? WARNING_COLOR : DANGER_COLOR)));
        panel.add(createKPICard("Consultations du Jour", 
            String.valueOf(consultationsJour), INFO_COLOR));
        
        return panel;
    }
    
    /**
     * Crée une carte KPI individuelle.
     */
    private static JPanel createKPICard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
}

