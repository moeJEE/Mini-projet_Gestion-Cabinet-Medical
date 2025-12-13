package com.cabinetmedical.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité représentant une consultation/rendez-vous médical.
 * Contient les informations sur la consultation, le patient, 
 * la catégorie, le médecin et le statut de paiement.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class Consultation {
    
    /** Identifiant unique de la consultation (auto-incrémenté) */
    private int id;
    
    /** Date et heure de la consultation */
    private LocalDateTime date;
    
    /** Description/notes de la consultation */
    private String description;
    
    /** Prix de la consultation en DH */
    private double prixConsultation;
    
    /** ID du patient (clé étrangère) */
    private int patientId;
    
    /** ID de la catégorie (clé étrangère) */
    private int categorieId;
    
    /** ID du médecin (clé étrangère) */
    private int medecinId;
    
    /** Statut de paiement */
    private boolean estPayee;
    
    /** Date de création de l'enregistrement */
    private LocalDateTime createdAt;
    
    // Objets associés (pour les jointures)
    /** Objet Patient associé */
    private Patient patient;
    
    /** Objet Catégorie associé */
    private Categorie categorie;
    
    /** Objet Médecin associé */
    private Medecin medecin;
    
    /** Indicateur si le patient a été relancé */
    private boolean relance;
    
    /**
     * Constructeur par défaut.
     */
    public Consultation() {
        this.estPayee = false;
        this.relance = false;
    }
    
    /**
     * Constructeur avec paramètres essentiels.
     * 
     * @param date Date et heure de la consultation
     * @param description Description de la consultation
     * @param prixConsultation Prix en DH
     * @param patientId ID du patient
     * @param categorieId ID de la catégorie
     * @param medecinId ID du médecin
     */
    public Consultation(LocalDateTime date, String description, double prixConsultation,
                       int patientId, int categorieId, int medecinId) {
        this.date = date;
        this.description = description;
        this.prixConsultation = prixConsultation;
        this.patientId = patientId;
        this.categorieId = categorieId;
        this.medecinId = medecinId;
        this.estPayee = false;
        this.relance = false;
    }
    
    /**
     * Constructeur complet avec id.
     * 
     * @param id Identifiant de la consultation
     * @param date Date et heure
     * @param description Description
     * @param prixConsultation Prix en DH
     * @param patientId ID du patient
     * @param categorieId ID de la catégorie
     * @param medecinId ID du médecin
     * @param estPayee Statut de paiement
     */
    public Consultation(int id, LocalDateTime date, String description, double prixConsultation,
                       int patientId, int categorieId, int medecinId, boolean estPayee) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.prixConsultation = prixConsultation;
        this.patientId = patientId;
        this.categorieId = categorieId;
        this.medecinId = medecinId;
        this.estPayee = estPayee;
        this.relance = false;
    }
    
    // Getters et Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getPrixConsultation() {
        return prixConsultation;
    }
    
    public void setPrixConsultation(double prixConsultation) {
        this.prixConsultation = prixConsultation;
    }
    
    public int getPatientId() {
        return patientId;
    }
    
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    
    public int getCategorieId() {
        return categorieId;
    }
    
    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }
    
    public int getMedecinId() {
        return medecinId;
    }
    
    public void setMedecinId(int medecinId) {
        this.medecinId = medecinId;
    }
    
    public boolean isEstPayee() {
        return estPayee;
    }
    
    public void setEstPayee(boolean estPayee) {
        this.estPayee = estPayee;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public Categorie getCategorie() {
        return categorie;
    }
    
    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }
    
    public Medecin getMedecin() {
        return medecin;
    }
    
    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }
    
    public boolean isRelance() {
        return relance;
    }
    
    public void setRelance(boolean relance) {
        this.relance = relance;
    }
    
    /**
     * Vérifie si la consultation est passée.
     * 
     * @return true si la date est dans le passé
     */
    public boolean isPassee() {
        return date != null && date.isBefore(LocalDateTime.now());
    }
    
    /**
     * Vérifie si la consultation est à venir.
     * 
     * @return true si la date est dans le futur
     */
    public boolean isAVenir() {
        return date != null && date.isAfter(LocalDateTime.now());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consultation that = (Consultation) o;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", date=" + date +
                ", prixConsultation=" + prixConsultation +
                ", patientId=" + patientId +
                ", categorieId=" + categorieId +
                ", medecinId=" + medecinId +
                ", estPayee=" + estPayee +
                '}';
    }
}

