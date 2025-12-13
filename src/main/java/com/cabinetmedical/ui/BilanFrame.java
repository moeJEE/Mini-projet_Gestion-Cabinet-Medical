/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.cabinetmedical.ui;

import com.cabinetmedical.controllers.BilanController;
import com.cabinetmedical.models.BilanMensuel;
import com.cabinetmedical.services.BilanService;
import com.cabinetmedical.utils.PDFExporter;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Moe_k
 */
public class BilanFrame extends javax.swing.JFrame {

    private BilanController bilanController;
    private BilanService bilanService;
    private DefaultTableModel tableModel;
    private BilanMensuel currentBilan;

    /**
     * Creates new form BilanFrame
     */
    public BilanFrame() {
        bilanController = new BilanController();
        bilanService = new BilanService();
        initComponents();
        setTitle("Bilan Mensuel");
        setLocationRelativeTo(null);
        
        setupComboBoxes();
        setupTable();
    }
    
    private void setupComboBoxes() {
        // Mois
        String[] mois = {"Janvier", "Fevrier", "Mars", "Avril", "Mai", "Juin",
                         "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Decembre"};
        for (String m : mois) {
            comboMois.addItem(m);
        }
        comboMois.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        
        // Annees (5 dernieres annees)
        int anneeActuelle = LocalDate.now().getYear();
        for (int i = anneeActuelle; i >= anneeActuelle - 4; i--) {
            comboAnnee.addItem(String.valueOf(i));
        }
    }
    
    private void setupTable() {
        // Le controleur retourne 2 colonnes: Semaine, Nombre de consultations
        String[] colonnes = {"Semaine", "Nombre de Consultations"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableEvolution.setModel(tableModel);
        tableEvolution.setRowHeight(25);
    }
    
    private void genererBilan() {
        int mois = comboMois.getSelectedIndex() + 1;
        int annee = Integer.parseInt((String) comboAnnee.getSelectedItem());
        
        // Charger le bilan
        currentBilan = bilanService.getBilanMensuel(mois, annee);
        
        // Afficher le bilan formaté
        String[] bilanData = bilanController.getBilanMensuelFormatted(mois, annee);
        
        StringBuilder sb = new StringBuilder();
        for (String line : bilanData) {
            sb.append(line).append("\n");
        }
        
        txtAreaBilan.setText(sb.toString());
        
        // Remplir la table d'évolution
        Object[][] evolutionData = bilanController.getEvolutionForTable(mois, annee);
        tableModel.setRowCount(0);
        for (Object[] row : evolutionData) {
            tableModel.addRow(row);
        }
        
        // Activer le bouton d'export
        btnExportPDF.setEnabled(true);
    }
    
    private void exporterPDF() {
        if (currentBilan == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez d'abord generer le bilan", 
                "Attention", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le bilan en PDF");
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
                    "Bilan exporte en PDF avec succes!\n" + filePath, 
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
        btnGenererBilan = new javax.swing.JButton();
        btnExportPDF = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaBilan = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableEvolution = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Bilan Mensuel");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Selection de la periode", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12)));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel2.setText("Mois :");

        comboMois.setFont(new java.awt.Font("Segoe UI", 0, 12));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel3.setText("Annee :");

        comboAnnee.setFont(new java.awt.Font("Segoe UI", 0, 12));

        btnGenererBilan.setBackground(new java.awt.Color(33, 150, 243));
        btnGenererBilan.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnGenererBilan.setText("Generer le Bilan");
        btnGenererBilan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenererBilanActionPerformed(evt);
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
                .addComponent(btnGenererBilan, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnExportPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(btnGenererBilan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExportPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Resume du Bilan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12)));

        txtAreaBilan.setEditable(false);
        txtAreaBilan.setColumns(20);
        txtAreaBilan.setFont(new java.awt.Font("Monospaced", 0, 14));
        txtAreaBilan.setRows(10);
        txtAreaBilan.setText("Selectionnez un mois et une annee,\npuis cliquez sur \"Generer le Bilan\"");
        jScrollPane1.setViewportView(txtAreaBilan);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Evolution par Semaine", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12)));

        tableEvolution.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Semaine", "Nombre de Consultations"}
        ) {
            boolean[] canEdit = new boolean [] {false, false};
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tableEvolution);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenererBilanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenererBilanActionPerformed
        genererBilan();
    }//GEN-LAST:event_btnGenererBilanActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BilanFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BilanFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BilanFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BilanFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BilanFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportPDF;
    private javax.swing.JButton btnGenererBilan;
    private javax.swing.JComboBox<String> comboAnnee;
    private javax.swing.JComboBox<String> comboMois;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tableEvolution;
    private javax.swing.JTextArea txtAreaBilan;
    // End of variables declaration//GEN-END:variables
}
