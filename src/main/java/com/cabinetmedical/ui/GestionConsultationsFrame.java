/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.cabinetmedical.ui;

import com.cabinetmedical.controllers.ConsultationController;
import com.cabinetmedical.controllers.PatientController;
import com.cabinetmedical.controllers.CategorieController;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;

/**
 *
 * @author Moe_k
 */
public class GestionConsultationsFrame extends javax.swing.JFrame {

    private int medecinId;
    private ConsultationController consultationController;
    private PatientController patientController;
    private CategorieController categorieController;
    private DefaultTableModel tableModel;
    private int selectedConsultationId = -1;

    /**
     * Creates new form GestionConsultationsFrame
     */
    public GestionConsultationsFrame() {
        consultationController = new ConsultationController();
        patientController = new PatientController();
        categorieController = new CategorieController();
        initComponents();
        setTitle("Gestion des Consultations");
        setLocationRelativeTo(null);
        
        setupDateComboBoxes();
        setupHeureComboBox();
        setupTable();
        loadComboBoxes();
        refreshTable();
    }
    
    public GestionConsultationsFrame(int medecinId) {
        this();
        this.medecinId = medecinId;
        refreshTable(); // Rafraichir avec le bon medecinId
    }
    
    private void setupDateComboBoxes() {
        // Jours (1-31)
        comboJour.removeAllItems();
        for (int i = 1; i <= 31; i++) {
            comboJour.addItem(String.format("%02d", i));
        }
        
        // Mois (01-12)
        comboMois.removeAllItems();
        String[] mois = {"01 - Janvier", "02 - Fevrier", "03 - Mars", "04 - Avril", 
                         "05 - Mai", "06 - Juin", "07 - Juillet", "08 - Aout",
                         "09 - Septembre", "10 - Octobre", "11 - Novembre", "12 - Decembre"};
        for (String m : mois) {
            comboMois.addItem(m);
        }
        
        // Annees (annee actuelle + 1 an)
        comboAnnee.removeAllItems();
        int anneeActuelle = LocalDate.now().getYear();
        for (int i = anneeActuelle; i <= anneeActuelle + 1; i++) {
            comboAnnee.addItem(String.valueOf(i));
        }
        
        // Selectionner la date actuelle
        LocalDate today = LocalDate.now();
        comboJour.setSelectedIndex(today.getDayOfMonth() - 1);
        comboMois.setSelectedIndex(today.getMonthValue() - 1);
        comboAnnee.setSelectedIndex(0);
    }
    
    private void setupHeureComboBox() {
        comboHeure.removeAllItems();
        // Horaires de travail: 08:00 - 18:00 avec intervalles de 30 minutes
        String[] heures = {
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "12:00", "12:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
            "17:00", "17:30", "18:00"
        };
        for (String h : heures) {
            comboHeure.addItem(h);
        }
    }
    
    private void setupTable() {
        // Colonnes: ID, Heure, Patient, Categorie, Prix, Statut (6 colonnes)
        String[] colonnes = {"ID", "Heure", "Patient", "Categorie", "Prix (DH)", "Statut"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableConsultations.setModel(tableModel);
        tableConsultations.setRowHeight(25);
        tableConsultations.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Listener pour remplir le formulaire lors de la selection
        tableConsultations.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableConsultations.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromTable(selectedRow);
                }
            }
        });
    }
    
    private void populateFormFromTable(int row) {
        selectedConsultationId = (int) tableModel.getValueAt(row, 0);
        
        // Heure
        String heure = (String) tableModel.getValueAt(row, 1);
        for (int i = 0; i < comboHeure.getItemCount(); i++) {
            if (comboHeure.getItemAt(i).equals(heure)) {
                comboHeure.setSelectedIndex(i);
                break;
            }
        }
        
        // Patient
        String patientNom = (String) tableModel.getValueAt(row, 2);
        for (int i = 0; i < comboPatients.getItemCount(); i++) {
            if (comboPatients.getItemAt(i).contains(patientNom)) {
                comboPatients.setSelectedIndex(i);
                break;
            }
        }
        
        // Categorie
        String categorie = (String) tableModel.getValueAt(row, 3);
        for (int i = 0; i < comboCategories.getItemCount(); i++) {
            if (comboCategories.getItemAt(i).equals(categorie)) {
                comboCategories.setSelectedIndex(i);
                break;
            }
        }
        
        // Prix (enlever " DH")
        String prixStr = (String) tableModel.getValueAt(row, 4);
        txtPrix.setText(prixStr.replace(" DH", "").trim());
    }
    
    private void loadComboBoxes() {
        // Charger les patients
        comboPatients.removeAllItems();
        String[] patients = patientController.getPatientsForComboBox();
        for (String patient : patients) {
            comboPatients.addItem(patient);
        }
        
        // Charger les categories
        comboCategories.removeAllItems();
        String[] categories = categorieController.getCategoriesForComboBox();
        for (String categorie : categories) {
            comboCategories.addItem(categorie);
        }
    }
    
    private void refreshTable() {
        Object[][] data = consultationController.getConsultationsJourForTable(medecinId, LocalDate.now());
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }
    
    private String getSelectedDate() {
        String jour = (String) comboJour.getSelectedItem();
        String moisStr = (String) comboMois.getSelectedItem();
        String mois = moisStr.substring(0, 2); // Extraire "01", "02", etc.
        String annee = (String) comboAnnee.getSelectedItem();
        return jour + "/" + mois + "/" + annee;
    }
    
    private String getSelectedHeure() {
        return (String) comboHeure.getSelectedItem();
    }
    
    private void clearForm() {
        setupDateComboBoxes();
        comboHeure.setSelectedIndex(0);
        txtDescription.setText("");
        txtPrix.setText("");
        if (comboPatients.getItemCount() > 0) comboPatients.setSelectedIndex(0);
        if (comboCategories.getItemCount() > 0) comboCategories.setSelectedIndex(0);
        tableConsultations.clearSelection();
        selectedConsultationId = -1;
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
        comboPatients = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        comboCategories = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        comboJour = new javax.swing.JComboBox<>();
        comboMois = new javax.swing.JComboBox<>();
        comboAnnee = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        comboHeure = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txtPrix = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        btnAjouter = new javax.swing.JButton();
        btnModifier = new javax.swing.JButton();
        btnSupprimer = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableConsultations = new javax.swing.JTable();
        btnActualiser = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Gestion des Consultations");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nouvelle Consultation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12)));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel2.setText("Patient :");

        comboPatients.setFont(new java.awt.Font("Segoe UI", 0, 12));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel3.setText("Categorie :");

        comboCategories.setFont(new java.awt.Font("Segoe UI", 0, 12));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel4.setText("Date :");

        comboJour.setFont(new java.awt.Font("Segoe UI", 0, 12));

        comboMois.setFont(new java.awt.Font("Segoe UI", 0, 12));

        comboAnnee.setFont(new java.awt.Font("Segoe UI", 0, 12));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel5.setText("Heure :");

        comboHeure.setFont(new java.awt.Font("Segoe UI", 0, 12));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel6.setText("Prix (DH) :");

        txtPrix.setFont(new java.awt.Font("Segoe UI", 0, 12));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel7.setText("Description :");

        txtDescription.setColumns(20);
        txtDescription.setFont(new java.awt.Font("Segoe UI", 0, 12));
        txtDescription.setRows(3);
        jScrollPane2.setViewportView(txtDescription);

        btnAjouter.setBackground(new java.awt.Color(76, 175, 80));
        btnAjouter.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnAjouter.setText("+ Ajouter");
        btnAjouter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjouterActionPerformed(evt);
            }
        });

        btnModifier.setBackground(new java.awt.Color(33, 150, 243));
        btnModifier.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnModifier.setText("Modifier");
        btnModifier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModifierActionPerformed(evt);
            }
        });

        btnSupprimer.setBackground(new java.awt.Color(244, 67, 54));
        btnSupprimer.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnSupprimer.setText("Supprimer");
        btnSupprimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSupprimerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboPatients, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboJour, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboMois, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboAnnee, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboHeure, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPrix, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(btnAjouter, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(btnModifier, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(btnSupprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(comboPatients, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(comboCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(comboJour, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboMois, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboAnnee, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(comboHeure, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtPrix, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAjouter, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnModifier, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSupprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Consultations du jour", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12)));

        tableConsultations.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"ID", "Heure", "Patient", "Categorie", "Prix", "Statut"}
        ) {
            boolean[] canEdit = new boolean [] {false, false, false, false, false, false};
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tableConsultations);

        btnActualiser.setText("Actualiser");
        btnActualiser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualiserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnActualiser)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnActualiser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAjouterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAjouterActionPerformed
        try {
            String selectedPatient = (String) comboPatients.getSelectedItem();
            if (selectedPatient == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "Veuillez selectionner un patient", "Erreur", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            int patientId = Integer.parseInt(selectedPatient.split(" - ")[0]);
            
            String selectedCategorie = (String) comboCategories.getSelectedItem();
            int categorieId = categorieController.getCategorieIdByName(selectedCategorie);
            
            String result = consultationController.createConsultation(
                patientId,
                categorieId,
                getSelectedDate(),
                getSelectedHeure(),
                txtDescription.getText().trim(),
                txtPrix.getText().trim(),
                medecinId
            );
            
            // Verifier si le message indique un succes (avec ou sans accent)
            String resultLower = result.toLowerCase();
            if (resultLower.contains("succ") || resultLower.contains("cree") || resultLower.contains("créé")) {
                javax.swing.JOptionPane.showMessageDialog(this, result, "Succes", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshTable();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, result, "Erreur", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Prix invalide", "Erreur", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAjouterActionPerformed

    private void btnModifierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModifierActionPerformed
        int selectedRow = tableConsultations.getSelectedRow();
        if (selectedRow == -1 || selectedConsultationId == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Veuillez selectionner une consultation dans la liste", "Attention", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
            "Pour modifier cette consultation, elle sera annulee et une nouvelle sera creee avec les nouvelles informations.\nContinuer ?",
            "Confirmation de modification",
            javax.swing.JOptionPane.YES_NO_OPTION);
        
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            // Annuler l'ancienne
            String cancelResult = consultationController.cancelConsultation(selectedConsultationId);
            
            // Creer la nouvelle
            try {
                String selectedPatient = (String) comboPatients.getSelectedItem();
                int patientId = Integer.parseInt(selectedPatient.split(" - ")[0]);
                String selectedCategorie = (String) comboCategories.getSelectedItem();
                int categorieId = categorieController.getCategorieIdByName(selectedCategorie);
                
                String result = consultationController.createConsultation(
                    patientId,
                    categorieId,
                    getSelectedDate(),
                    getSelectedHeure(),
                    txtDescription.getText().trim(),
                    txtPrix.getText().trim(),
                    medecinId
                );
                
                String resultLower = result.toLowerCase();
                if (resultLower.contains("succ") || resultLower.contains("cree") || resultLower.contains("créé")) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Consultation modifiee avec succes", "Succes", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    refreshTable();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, result, "Erreur", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + e.getMessage(), "Erreur", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnModifierActionPerformed

    private void btnSupprimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSupprimerActionPerformed
        int selectedRow = tableConsultations.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Veuillez selectionner une consultation", "Attention", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
            "Etes-vous sur de vouloir annuler cette consultation ?",
            "Confirmation",
            javax.swing.JOptionPane.YES_NO_OPTION);
        
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            int consultationId = (int) tableModel.getValueAt(selectedRow, 0);
            String result = consultationController.cancelConsultation(consultationId);
            
            if (result.toLowerCase().contains("succes") || result.toLowerCase().contains("annul")) {
                javax.swing.JOptionPane.showMessageDialog(this, result, "Succes", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshTable();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, result, "Erreur", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnSupprimerActionPerformed

    private void btnActualiserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualiserActionPerformed
        loadComboBoxes();
        refreshTable();
    }//GEN-LAST:event_btnActualiserActionPerformed

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
            java.util.logging.Logger.getLogger(GestionConsultationsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GestionConsultationsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GestionConsultationsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GestionConsultationsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GestionConsultationsFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualiser;
    private javax.swing.JButton btnAjouter;
    private javax.swing.JButton btnModifier;
    private javax.swing.JButton btnSupprimer;
    private javax.swing.JComboBox<String> comboAnnee;
    private javax.swing.JComboBox<String> comboCategories;
    private javax.swing.JComboBox<String> comboHeure;
    private javax.swing.JComboBox<String> comboJour;
    private javax.swing.JComboBox<String> comboMois;
    private javax.swing.JComboBox<String> comboPatients;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tableConsultations;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtPrix;
    // End of variables declaration//GEN-END:variables
}
