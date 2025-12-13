package com.cabinetmedical.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité représentant un patient du cabinet médical.
 * Contient les informations personnelles et de contact du patient.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class Patient {
    
    /** Identifiant unique du patient (auto-incrémenté) */
    private int id;
    
    /** Nom complet du patient */
    private String nom;
    
    /** Numéro de téléphone (format marocain: 10 chiffres) */
    private String telephone;
    
    /** Adresse email du patient */
    private String email;
    
    /** Date de création de l'enregistrement */
    private LocalDateTime createdAt;
    
    /**
     * Constructeur par défaut.
     */
    public Patient() {
    }
    
    /**
     * Constructeur avec tous les champs (sans id).
     * 
     * @param nom Nom du patient
     * @param telephone Numéro de téléphone
     * @param email Adresse email
     */
    public Patient(String nom, String telephone, String email) {
        this.nom = nom;
        this.telephone = telephone;
        this.email = email;
    }
    
    /**
     * Constructeur complet avec id.
     * 
     * @param id Identifiant du patient
     * @param nom Nom du patient
     * @param telephone Numéro de téléphone
     * @param email Adresse email
     */
    public Patient(int id, String nom, String telephone, String email) {
        this.id = id;
        this.nom = nom;
        this.telephone = telephone;
        this.email = email;
    }
    
    // Getters et Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return id == patient.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

