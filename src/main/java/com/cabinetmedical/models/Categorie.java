package com.cabinetmedical.models;

import java.util.Objects;

/**
 * Entité représentant une catégorie de consultation.
 * Exemples: Consultation normale, Contrôle, Urgence, Visite à domicile.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class Categorie {
    
    /** Identifiant unique de la catégorie (auto-incrémenté) */
    private int id;
    
    /** Désignation de la catégorie (unique) */
    private String designation;
    
    /** Description détaillée de la catégorie */
    private String description;
    
    /**
     * Constructeur par défaut.
     */
    public Categorie() {
    }
    
    /**
     * Constructeur avec désignation et description.
     * 
     * @param designation Désignation de la catégorie
     * @param description Description de la catégorie
     */
    public Categorie(String designation, String description) {
        this.designation = designation;
        this.description = description;
    }
    
    /**
     * Constructeur complet avec id.
     * 
     * @param id Identifiant de la catégorie
     * @param designation Désignation de la catégorie
     * @param description Description de la catégorie
     */
    public Categorie(int id, String designation, String description) {
        this.id = id;
        this.designation = designation;
        this.description = description;
    }
    
    // Getters et Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getDesignation() {
        return designation;
    }
    
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categorie categorie = (Categorie) o;
        return id == categorie.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Categorie{" +
                "id=" + id +
                ", designation='" + designation + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

