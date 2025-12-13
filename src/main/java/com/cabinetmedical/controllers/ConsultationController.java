package com.cabinetmedical.controllers;

import com.cabinetmedical.exceptions.BusinessException;
import com.cabinetmedical.exceptions.NotFoundException;
import com.cabinetmedical.exceptions.ValidationException;
import com.cabinetmedical.models.Consultation;
import com.cabinetmedical.services.ConsultationService;
import com.cabinetmedical.services.PatientService;
import com.cabinetmedical.services.CategorieService;
import com.cabinetmedical.utils.DateHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrôleur pour la gestion des consultations.
 * Fournit des méthodes formatées pour les composants Swing.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class ConsultationController {
    
    private final ConsultationService consultationService;
    private final PatientService patientService;
    private final CategorieService categorieService;
    
    /**
     * Constructeur par défaut.
     */
    public ConsultationController() {
        this.consultationService = new ConsultationService();
        this.patientService = new PatientService();
        this.categorieService = new CategorieService();
    }
    
    /**
     * Récupère les consultations du jour pour un médecin, formatées pour JTable.
     * 
     * @param medecinId ID du médecin
     * @param date Date des consultations
     * @return Tableau 2D [ID, Heure, Patient, Catégorie, Prix, Statut]
     */
    public Object[][] getConsultationsJourForTable(int medecinId, LocalDate date) {
        List<Consultation> consultations = consultationService.getConsultationsJournalieres(medecinId, date);
        Object[][] data = new Object[consultations.size()][6];
        
        for (int i = 0; i < consultations.size(); i++) {
            Consultation c = consultations.get(i);
            data[i][0] = c.getId();
            data[i][1] = DateHelper.formatTime(c.getDate());
            data[i][2] = c.getPatient() != null ? c.getPatient().getNom() : "Patient #" + c.getPatientId();
            data[i][3] = c.getCategorie() != null ? c.getCategorie().getDesignation() : "";
            data[i][4] = String.format("%.2f DH", c.getPrixConsultation());
            data[i][5] = c.isEstPayee() ? "Payée" : "Non payée";
        }
        
        return data;
    }
    
    /**
     * Retourne les colonnes pour la table des consultations du jour.
     * 
     * @return Tableau des noms de colonnes
     */
    public String[] getConsultationsJourTableColumns() {
        return new String[]{"ID", "Heure", "Patient", "Catégorie", "Prix", "Statut"};
    }
    
    /**
     * Récupère toutes les consultations formatées pour JTable.
     * 
     * @return Tableau 2D des consultations
     */
    public Object[][] getAllConsultationsForTable() {
        List<Consultation> consultations = consultationService.getAllConsultations();
        Object[][] data = new Object[consultations.size()][7];
        
        for (int i = 0; i < consultations.size(); i++) {
            Consultation c = consultations.get(i);
            data[i][0] = c.getId();
            data[i][1] = DateHelper.formatDateTime(c.getDate());
            data[i][2] = c.getPatient() != null ? c.getPatient().getNom() : "";
            data[i][3] = c.getCategorie() != null ? c.getCategorie().getDesignation() : "";
            data[i][4] = c.getDescription() != null ? c.getDescription() : "";
            data[i][5] = String.format("%.2f DH", c.getPrixConsultation());
            data[i][6] = c.isEstPayee() ? "Payée" : "Non payée";
        }
        
        return data;
    }
    
    /**
     * Retourne les colonnes pour la table de toutes les consultations.
     * 
     * @return Tableau des noms de colonnes
     */
    public String[] getAllConsultationsTableColumns() {
        return new String[]{"ID", "Date/Heure", "Patient", "Catégorie", "Description", "Prix", "Statut"};
    }
    
    /**
     * Récupère les consultations non payées formatées pour JTable.
     * 
     * @return Tableau 2D des consultations non payées
     */
    public Object[][] getConsultationsNonPayeesForTable() {
        List<Consultation> consultations = consultationService.getConsultationsNonPayees();
        Object[][] data = new Object[consultations.size()][6];
        
        for (int i = 0; i < consultations.size(); i++) {
            Consultation c = consultations.get(i);
            data[i][0] = c.getId();
            data[i][1] = DateHelper.formatDateTime(c.getDate());
            data[i][2] = c.getPatient() != null ? c.getPatient().getNom() : "";
            data[i][3] = c.getPatient() != null ? c.getPatient().getTelephone() : "";
            data[i][4] = c.getCategorie() != null ? c.getCategorie().getDesignation() : "";
            data[i][5] = String.format("%.2f DH", c.getPrixConsultation());
        }
        
        return data;
    }
    
    /**
     * Retourne les colonnes pour la table des consultations non payées.
     * 
     * @return Tableau des noms de colonnes
     */
    public String[] getConsultationsNonPayeesTableColumns() {
        return new String[]{"ID", "Date/Heure", "Patient", "Téléphone", "Catégorie", "Prix"};
    }
    
    /**
     * Récupère les patients à relancer formatés pour JTable.
     * 
     * @return Tableau 2D des patients à relancer
     */
    public Object[][] getPatientsARelancerForTable() {
        List<Consultation> consultations = consultationService.getPatientsARelancer(LocalDate.now());
        Object[][] data = new Object[consultations.size()][5];
        
        for (int i = 0; i < consultations.size(); i++) {
            Consultation c = consultations.get(i);
            data[i][0] = c.getId();
            data[i][1] = DateHelper.formatDateTime(c.getDate());
            data[i][2] = c.getPatient() != null ? c.getPatient().getNom() : "";
            data[i][3] = c.getPatient() != null ? c.getPatient().getTelephone() : "";
            data[i][4] = c.getPatient() != null ? c.getPatient().getEmail() : "";
        }
        
        return data;
    }
    
    /**
     * Retourne les colonnes pour la table des patients à relancer.
     * 
     * @return Tableau des noms de colonnes
     */
    public String[] getPatientsARelancerTableColumns() {
        return new String[]{"ID Consultation", "Date RDV", "Patient", "Téléphone", "Email"};
    }
    
    /**
     * Récupère les catégories formatées pour JComboBox.
     * 
     * @return Tableau des désignations
     */
    public String[] getCategoriesForComboBox() {
        return categorieService.getAllCategories().stream()
                .map(c -> c.getDesignation())
                .toArray(String[]::new);
    }
    
    /**
     * Récupère les patients formatés pour JComboBox.
     * 
     * @return Tableau "ID - Nom"
     */
    public String[] getPatientsForComboBox() {
        return patientService.getAllPatients().stream()
                .map(p -> p.getId() + " - " + p.getNom())
                .toArray(String[]::new);
    }
    
    /**
     * Récupère l'ID d'une catégorie par sa désignation.
     * 
     * @param designation Désignation de la catégorie
     * @return ID ou -1
     */
    public int getCategorieIdByName(String designation) {
        var categorie = categorieService.getCategorieByDesignation(designation);
        return categorie != null ? categorie.getId() : -1;
    }
    
    /**
     * Crée une nouvelle consultation.
     * 
     * @param patientId ID du patient
     * @param categorieId ID de la catégorie
     * @param date Date et heure
     * @param description Description
     * @param prix Prix de la consultation
     * @param medecinId ID du médecin
     * @return Message de résultat
     */
    public String createConsultation(int patientId, int categorieId, LocalDateTime date,
                                     String description, double prix, int medecinId) {
        try {
            // Validations
            if (date == null) {
                return "La date est requise";
            }
            if (date.isBefore(LocalDateTime.now())) {
                return "La date ne peut pas être dans le passé";
            }
            if (prix <= 0) {
                return "Le prix doit être supérieur à 0";
            }
            if (patientId <= 0) {
                return "Veuillez sélectionner un patient";
            }
            if (categorieId <= 0) {
                return "Veuillez sélectionner une catégorie";
            }
            
            Consultation consultation = new Consultation();
            consultation.setDate(date);
            consultation.setDescription(description);
            consultation.setPrixConsultation(prix);
            consultation.setPatientId(patientId);
            consultation.setCategorieId(categorieId);
            consultation.setMedecinId(medecinId);
            consultation.setEstPayee(false);
            
            consultationService.createConsultation(consultation);
            return "Consultation créée avec succès";
            
        } catch (ValidationException | BusinessException | NotFoundException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
    
    /**
     * Crée une consultation avec date/heure en chaînes.
     * 
     * @param patientId ID du patient
     * @param categorieId ID de la catégorie
     * @param dateStr Date (format: dd/MM/yyyy)
     * @param heureStr Heure (format: HH:mm)
     * @param description Description
     * @param prixStr Prix en chaîne
     * @param medecinId ID du médecin
     * @return Message de résultat
     */
    public String createConsultation(int patientId, int categorieId, String dateStr,
                                     String heureStr, String description, String prixStr, int medecinId) {
        try {
            LocalDateTime date = DateHelper.parseDateTime(dateStr, heureStr);
            double prix = Double.parseDouble(prixStr);
            return createConsultation(patientId, categorieId, date, description, prix, medecinId);
        } catch (NumberFormatException e) {
            return "Prix invalide";
        } catch (ValidationException e) {
            return e.getMessage();
        }
    }
    
    /**
     * Annule une consultation.
     * 
     * @param consultationId ID de la consultation
     * @return Message de résultat
     */
    public String cancelConsultation(int consultationId) {
        try {
            consultationService.cancelConsultation(consultationId);
            return "Consultation annulée avec succès";
        } catch (NotFoundException | BusinessException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
    
    /**
     * Valide le paiement d'une consultation.
     * 
     * @param consultationId ID de la consultation
     * @return Message de résultat
     */
    public String validerPaiement(int consultationId) {
        try {
            consultationService.validerPaiement(consultationId);
            return "Paiement validé avec succès";
        } catch (NotFoundException | BusinessException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
    
    /**
     * Extrait l'ID du patient d'un élément ComboBox.
     * 
     * @param comboBoxItem Élément "ID - Nom"
     * @return ID ou -1
     */
    public int extractPatientId(String comboBoxItem) {
        if (comboBoxItem == null || comboBoxItem.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(comboBoxItem.split(" - ")[0].trim());
        } catch (Exception e) {
            return -1;
        }
    }
}

