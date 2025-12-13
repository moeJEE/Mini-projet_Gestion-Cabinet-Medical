package com.cabinetmedical.controllers;

import com.cabinetmedical.exceptions.BusinessException;
import com.cabinetmedical.exceptions.NotFoundException;
import com.cabinetmedical.exceptions.ValidationException;
import com.cabinetmedical.models.Patient;
import com.cabinetmedical.services.PatientService;

import java.util.List;

/**
 * Contrôleur pour la gestion des patients.
 * Fournit des méthodes formatées pour les composants Swing (JTable, JComboBox, etc.).
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class PatientController {
    
    private final PatientService patientService;
    
    /**
     * Constructeur par défaut.
     */
    public PatientController() {
        this.patientService = new PatientService();
    }
    
    /**
     * Récupère les données des patients formatées pour JTable.
     * 
     * @return Tableau 2D [ID, Nom, Téléphone, Email]
     */
    public Object[][] getPatientsForTable() {
        List<Patient> patients = patientService.getAllPatients();
        Object[][] data = new Object[patients.size()][4];
        
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            data[i][0] = p.getId();
            data[i][1] = p.getNom();
            data[i][2] = p.getTelephone();
            data[i][3] = p.getEmail() != null ? p.getEmail() : "";
        }
        
        return data;
    }
    
    /**
     * Retourne les noms des colonnes pour la table des patients.
     * 
     * @return Tableau des noms de colonnes
     */
    public String[] getPatientTableColumns() {
        return new String[]{"ID", "Nom", "Téléphone", "Email"};
    }
    
    /**
     * Recherche des patients et retourne les résultats formatés pour JTable.
     * 
     * @param criteria Critère de recherche
     * @return Tableau 2D des résultats
     */
    public Object[][] searchPatientsForTable(String criteria) {
        List<Patient> patients = patientService.searchPatients(criteria);
        Object[][] data = new Object[patients.size()][4];
        
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            data[i][0] = p.getId();
            data[i][1] = p.getNom();
            data[i][2] = p.getTelephone();
            data[i][3] = p.getEmail() != null ? p.getEmail() : "";
        }
        
        return data;
    }
    
    /**
     * Récupère les patients formatés pour JComboBox.
     * Format: "ID - Nom"
     * 
     * @return Tableau de chaînes pour ComboBox
     */
    public String[] getPatientsForComboBox() {
        List<Patient> patients = patientService.getAllPatients();
        return patients.stream()
                .map(p -> p.getId() + " - " + p.getNom())
                .toArray(String[]::new);
    }
    
    /**
     * Crée un nouveau patient avec validation.
     * 
     * @param nom Nom du patient
     * @param telephone Numéro de téléphone
     * @param email Adresse email (optionnel)
     * @return Message de résultat
     */
    public String createPatient(String nom, String telephone, String email) {
        try {
            // Validations de base
            if (nom == null || nom.trim().isEmpty()) {
                return "Le nom est requis";
            }
            if (telephone == null || !telephone.matches("\\d{10}")) {
                return "Téléphone invalide (10 chiffres requis)";
            }
            
            Patient patient = new Patient();
            patient.setNom(nom.trim());
            patient.setTelephone(telephone.trim());
            patient.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
            
            patientService.createPatient(patient);
            return "Patient ajouté avec succès";
            
        } catch (ValidationException | BusinessException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
    
    /**
     * Met à jour un patient existant.
     * 
     * @param id ID du patient
     * @param nom Nouveau nom
     * @param telephone Nouveau téléphone
     * @param email Nouvel email
     * @return Message de résultat
     */
    public String updatePatient(int id, String nom, String telephone, String email) {
        try {
            // Validations de base
            if (nom == null || nom.trim().isEmpty()) {
                return "Le nom est requis";
            }
            if (telephone == null || !telephone.matches("\\d{10}")) {
                return "Téléphone invalide (10 chiffres requis)";
            }
            
            Patient patient = new Patient();
            patient.setId(id);
            patient.setNom(nom.trim());
            patient.setTelephone(telephone.trim());
            patient.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
            
            patientService.updatePatient(patient);
            return "Patient modifié avec succès";
            
        } catch (ValidationException | BusinessException | NotFoundException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
    
    /**
     * Supprime un patient.
     * 
     * @param id ID du patient à supprimer
     * @return Message de résultat
     */
    public String deletePatient(int id) {
        try {
            patientService.deletePatient(id);
            return "Patient supprimé avec succès";
            
        } catch (NotFoundException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
    
    /**
     * Récupère un patient par son ID.
     * 
     * @param id ID du patient
     * @return Patient ou null
     */
    public Patient getPatientById(int id) {
        try {
            return patientService.getPatientById(id);
        } catch (NotFoundException e) {
            return null;
        }
    }
    
    /**
     * Récupère les données d'un patient pour pré-remplir un formulaire.
     * 
     * @param id ID du patient
     * @return Tableau [nom, telephone, email] ou null
     */
    public String[] getPatientFormData(int id) {
        try {
            Patient patient = patientService.getPatientById(id);
            return new String[] {
                patient.getNom(),
                patient.getTelephone(),
                patient.getEmail() != null ? patient.getEmail() : ""
            };
        } catch (NotFoundException e) {
            return null;
        }
    }
    
    /**
     * Extrait l'ID d'une chaîne ComboBox (format: "ID - Nom").
     * 
     * @param comboBoxItem Élément sélectionné
     * @return ID extrait ou -1
     */
    public int extractPatientId(String comboBoxItem) {
        if (comboBoxItem == null || comboBoxItem.isEmpty()) {
            return -1;
        }
        try {
            String idPart = comboBoxItem.split(" - ")[0];
            return Integer.parseInt(idPart.trim());
        } catch (Exception e) {
            return -1;
        }
    }
    
    /**
     * Compte le nombre total de patients.
     * 
     * @return Nombre de patients
     */
    public int countPatients() {
        return patientService.countPatients();
    }
}

