package com.cabinetmedical.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe représentant le bilan mensuel des consultations.
 * Contient les statistiques et indicateurs de performance du cabinet.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class BilanMensuel {
    
    /** Mois du bilan (1-12) */
    private int mois;
    
    /** Année du bilan */
    private int annee;
    
    /** Nombre total de consultations */
    private int nombreConsultations;
    
    /** Chiffre d'affaires total en DH */
    private double chiffreAffaires;
    
    /** Nombre de consultations par catégorie */
    private Map<String, Integer> consultationsParCategorie;
    
    /** Évolution du nombre de consultations par semaine */
    private List<Integer> evolutionParSemaine;
    
    /** Nombre de consultations payées */
    private int consultationsPayees;
    
    /** Nombre de consultations impayées */
    private int consultationsImpayees;
    
    /** Montant total des impayés en DH */
    private double montantImpayes;
    
    /**
     * Constructeur par défaut.
     */
    public BilanMensuel() {
        this.consultationsParCategorie = new HashMap<>();
    }
    
    /**
     * Constructeur avec mois et année.
     * 
     * @param mois Mois du bilan (1-12)
     * @param annee Année du bilan
     */
    public BilanMensuel(int mois, int annee) {
        this.mois = mois;
        this.annee = annee;
        this.consultationsParCategorie = new HashMap<>();
    }
    
    // Getters et Setters
    
    public int getMois() {
        return mois;
    }
    
    public void setMois(int mois) {
        this.mois = mois;
    }
    
    public int getAnnee() {
        return annee;
    }
    
    public void setAnnee(int annee) {
        this.annee = annee;
    }
    
    public int getNombreConsultations() {
        return nombreConsultations;
    }
    
    public void setNombreConsultations(int nombreConsultations) {
        this.nombreConsultations = nombreConsultations;
    }
    
    public double getChiffreAffaires() {
        return chiffreAffaires;
    }
    
    public void setChiffreAffaires(double chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }
    
    public Map<String, Integer> getConsultationsParCategorie() {
        return consultationsParCategorie;
    }
    
    public void setConsultationsParCategorie(Map<String, Integer> consultationsParCategorie) {
        this.consultationsParCategorie = consultationsParCategorie;
    }
    
    public List<Integer> getEvolutionParSemaine() {
        return evolutionParSemaine;
    }
    
    public void setEvolutionParSemaine(List<Integer> evolutionParSemaine) {
        this.evolutionParSemaine = evolutionParSemaine;
    }
    
    public int getConsultationsPayees() {
        return consultationsPayees;
    }
    
    public void setConsultationsPayees(int consultationsPayees) {
        this.consultationsPayees = consultationsPayees;
    }
    
    public int getConsultationsImpayees() {
        return consultationsImpayees;
    }
    
    public void setConsultationsImpayees(int consultationsImpayees) {
        this.consultationsImpayees = consultationsImpayees;
    }
    
    public double getMontantImpayes() {
        return montantImpayes;
    }
    
    public void setMontantImpayes(double montantImpayes) {
        this.montantImpayes = montantImpayes;
    }
    
    /**
     * Ajoute une entrée aux consultations par catégorie.
     * 
     * @param categorie Nom de la catégorie
     * @param nombre Nombre de consultations
     */
    public void addConsultationCategorie(String categorie, int nombre) {
        this.consultationsParCategorie.put(categorie, nombre);
    }
    
    /**
     * Calcule le taux de paiement.
     * 
     * @return Pourcentage de consultations payées
     */
    public double getTauxPaiement() {
        if (nombreConsultations == 0) return 0;
        return (double) consultationsPayees / nombreConsultations * 100;
    }
    
    /**
     * Calcule le prix moyen par consultation.
     * 
     * @return Prix moyen en DH
     */
    public double getPrixMoyen() {
        if (nombreConsultations == 0) return 0;
        return chiffreAffaires / nombreConsultations;
    }
    
    /**
     * Retourne le nom du mois en français.
     * 
     * @return Nom du mois
     */
    public String getNomMois() {
        String[] moisNoms = {
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        };
        if (mois >= 1 && mois <= 12) {
            return moisNoms[mois - 1];
        }
        return "Inconnu";
    }
    
    @Override
    public String toString() {
        return "BilanMensuel{" +
                "mois=" + getNomMois() +
                ", annee=" + annee +
                ", nombreConsultations=" + nombreConsultations +
                ", chiffreAffaires=" + chiffreAffaires + " DH" +
                ", tauxPaiement=" + String.format("%.1f", getTauxPaiement()) + "%" +
                '}';
    }
}

