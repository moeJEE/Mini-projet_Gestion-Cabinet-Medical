/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.cabinetmedical.ui;

import com.cabinetmedical.controllers.BilanController;
import com.cabinetmedical.models.BilanMensuel;
import com.cabinetmedical.services.BilanService;
import com.cabinetmedical.services.ConsultationService;
import com.cabinetmedical.utils.ChartGenerator;
import com.cabinetmedical.utils.PDFExporter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.Map;

/**
 * Fenêtre de statistiques avec graphiques et export PDF.
 *
 * @author Moe_k
 */
public class StatistiquesFrame extends javax.swing.JFrame {

    private BilanController bilanController;
    private BilanService bilanService;
    private ConsultationService consultationService;
    private BilanMensuel currentBilan;

    /**
     * Creates new form StatistiquesFrame
     */
    public StatistiquesFrame() {
        bilanController = new BilanController();
        bilanService = new BilanService();
        consultationService = new ConsultationService();
        initComponents();
        setTitle("Statistiques et Rapports");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        setupComboBoxes();
    }
    
    private void setupComboBoxes() {
        // Mois
        comboMois.removeAllItems();
        String[] mois = {"Janvier", "Fevrier", "Mars", "Avril", "Mai", "Juin",
                         "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Decembre"};
        for (String m : mois) {
            comboMois.addItem(m);
        }
        comboMois.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        
        // Annees
        comboAnnee.removeAllItems();
        int anneeActuelle = LocalDate.now().getYear();
        for (int i = anneeActuelle; i >= anneeActuelle - 4; i--) {
            comboAnnee.addItem(String.valueOf(i));
        }
    }
    
    private void genererStatistiques() {
        int mois = comboMois.getSelectedIndex() + 1;
        int annee = Integer.parseInt((String) comboAnnee.getSelectedItem());
        
        // Charger le bilan
        currentBilan = bilanService.getBilanMensuel(mois, annee);
        
        // Calculer le nombre de consultations du jour
        int consultationsDuJour = consultationService.countConsultationsByDate(LocalDate.now());
        
        // Effacer le panneau des graphiques
        panelGraphiques.removeAll();
        panelGraphiques.setLayout(new GridLayout(2, 2, 10, 10));
        
        // 1. KPIs
        JPanel kpiPanel = ChartGenerator.createKPIPanel(
            currentBilan.getNombreConsultations(),
            currentBilan.getChiffreAffaires(),
            currentBilan.getTauxPaiement(),
            consultationsDuJour
        );
        kpiPanel.setBorder(BorderFactory.createTitledBorder("Indicateurs Cles"));
        panelGraphiques.add(kpiPanel);
        
        // 2. Camembert des catégories
        Map<String, Integer> categories = currentBilan.getConsultationsParCategorie();
        if (categories != null && !categories.isEmpty()) {
            JPanel piePanel = ChartGenerator.createPieChart(categories, "Consultations par Categorie");
            panelGraphiques.add(piePanel);
        } else {
            JPanel emptyPanel = new JPanel();
            emptyPanel.add(new JLabel("Pas de donnees par categorie"));
            panelGraphiques.add(emptyPanel);
        }
        
        // 3. Comparaison Payé/Impayé
        JPanel compPanel = ChartGenerator.createComparisonChart(
            currentBilan.getChiffreAffaires() - currentBilan.getMontantImpayes(),
            currentBilan.getMontantImpayes(),
            "Paiements"
        );
        panelGraphiques.add(compPanel);
        
        // 4. Evolution hebdomadaire
        Object[][] evolution = bilanController.getEvolutionForTable(mois, annee);
        if (evolution.length > 0) {
            String[] labels = new String[evolution.length];
            int[] values = new int[evolution.length];
            for (int i = 0; i < evolution.length; i++) {
                labels[i] = (String) evolution[i][0];
                values[i] = (int) evolution[i][1];
            }
            JPanel linePanel = ChartGenerator.createLineChart(
                labels, values, 
                "Evolution Hebdomadaire", 
                "Semaine", "Consultations"
            );
            panelGraphiques.add(linePanel);
        } else {
            JPanel emptyPanel = new JPanel();
            emptyPanel.add(new JLabel("Pas de donnees d'evolution"));
            panelGraphiques.add(emptyPanel);
        }
        
        panelGraphiques.revalidate();
        panelGraphiques.repaint();
        
        // Activer le bouton d'export
        btnExportPDF.setEnabled(true);
    }
    
    private void exporterPDF() {
        if (currentBilan == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez d'abord generer les statistiques", 
                "Attention", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le rapport PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers PDF", "pdf"));
        fileChooser.setSelectedFile(new File("Bilan_" + currentBilan.getNomMois() + "_" + currentBilan.getAnnee() + ".pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            
            try {
                PDFExporter.exportBilanMensuel(currentBilan, filePath);
                JOptionPane.showMessageDialog(this, 
                    "Rapport PDF exporte avec succes!\n" + filePath, 
                    "Succes", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Ouvrir le fichier
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(new File(filePath));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'export: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        comboMois = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        comboAnnee = new javax.swing.JComboBox<>();
        btnGenerer = new javax.swing.JButton();
        btnExportPDF = new javax.swing.JButton();
        panelGraphiques = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24));
        jLabel1.setForeground(new java.awt.Color(33, 150, 243));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Statistiques et Rapports");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Periode", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12)));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel2.setText("Mois :");

        comboMois.setFont(new java.awt.Font("Segoe UI", 0, 12));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel3.setText("Annee :");

        comboAnnee.setFont(new java.awt.Font("Segoe UI", 0, 12));

        btnGenerer.setBackground(new java.awt.Color(33, 150, 243));
        btnGenerer.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnGenerer.setText("Generer Statistiques");
        btnGenerer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenererActionPerformed(evt);
            }
        });

        btnExportPDF.setBackground(new java.awt.Color(76, 175, 80));
        btnExportPDF.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnExportPDF.setText("Exporter PDF");
        btnExportPDF.setEnabled(false);
        btnExportPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportPDFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboMois, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboAnnee, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnGenerer)
                .addGap(18, 18, 18)
                .addComponent(btnExportPDF)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(comboMois, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(comboAnnee, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerer, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExportPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelGraphiques.setBackground(new java.awt.Color(255, 255, 255));
        panelGraphiques.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Graphiques", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12)));
        panelGraphiques.setLayout(new java.awt.GridLayout(2, 2, 10, 10));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelGraphiques, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelGraphiques, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenererActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenererActionPerformed
        genererStatistiques();
    }//GEN-LAST:event_btnGenererActionPerformed

    private void btnExportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportPDFActionPerformed
        exporterPDF();
    }//GEN-LAST:event_btnExportPDFActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StatistiquesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new StatistiquesFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportPDF;
    private javax.swing.JButton btnGenerer;
    private javax.swing.JComboBox<String> comboAnnee;
    private javax.swing.JComboBox<String> comboMois;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panelGraphiques;
    // End of variables declaration//GEN-END:variables
}

