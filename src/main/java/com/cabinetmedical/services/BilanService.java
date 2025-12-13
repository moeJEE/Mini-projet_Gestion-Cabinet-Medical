package com.cabinetmedical.services;

import com.cabinetmedical.dao.ConsultationDAO;
import com.cabinetmedical.models.BilanMensuel;
import com.cabinetmedical.models.Consultation;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service pour la génération des bilans et statistiques.
 * Fournit les rapports mensuels et l'évolution des consultations.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class BilanService {
    
    private final ConsultationDAO consultationDAO;
    
    /**
     * Constructeur par défaut.
     */
    public BilanService() {
        this.consultationDAO = new ConsultationDAO();
    }
    
    /**
     * Constructeur avec injection du DAO.
     * 
     * @param consultationDAO DAO des consultations
     */
    public BilanService(ConsultationDAO consultationDAO) {
        this.consultationDAO = consultationDAO;
    }
    
    /**
     * Génère le bilan mensuel.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Bilan mensuel complet
     */
    public BilanMensuel getBilanMensuel(int mois, int annee) {
        BilanMensuel bilan = new BilanMensuel(mois, annee);
        
        // Récupérer toutes les consultations du mois
        List<Consultation> consultations = consultationDAO.findByMois(mois, annee);
        
        // Nombre total de consultations
        bilan.setNombreConsultations(consultations.size());
        
        // Calculer le chiffre d'affaires (consultations payées uniquement)
        double chiffreAffaires = consultationDAO.getChiffreAffaires(mois, annee);
        bilan.setChiffreAffaires(chiffreAffaires);
        
        // Statistiques par catégorie
        Map<String, Integer> statsCategorie = consultationDAO.getStatsByCategorie(mois, annee);
        bilan.setConsultationsParCategorie(statsCategorie);
        
        // Évolution par semaine
        List<Integer> evolution = consultationDAO.getEvolutionParSemaine(mois, annee);
        bilan.setEvolutionParSemaine(evolution);
        
        // Statistiques de paiement
        int payees = 0;
        int impayees = 0;
        double montantImpayes = 0;
        
        for (Consultation c : consultations) {
            if (c.isEstPayee()) {
                payees++;
            } else {
                impayees++;
                montantImpayes += c.getPrixConsultation();
            }
        }
        
        bilan.setConsultationsPayees(payees);
        bilan.setConsultationsImpayees(impayees);
        bilan.setMontantImpayes(montantImpayes);
        
        return bilan;
    }
    
    /**
     * Récupère l'évolution des consultations sur une période.
     * 
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Liste des consultations sur la période
     */
    public List<Consultation> getEvolutionConsultations(LocalDate dateDebut, LocalDate dateFin) {
        // On peut utiliser findByMois pour chaque mois de la période
        // ou créer une nouvelle méthode dans le DAO
        return consultationDAO.findAll().stream()
                .filter(c -> {
                    LocalDate consultationDate = c.getDate().toLocalDate();
                    return !consultationDate.isBefore(dateDebut) && !consultationDate.isAfter(dateFin);
                })
                .toList();
    }
    
    /**
     * Calcule le chiffre d'affaires d'un mois.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Chiffre d'affaires
     */
    public double getChiffreAffairesMensuel(int mois, int annee) {
        return consultationDAO.getChiffreAffaires(mois, annee);
    }
    
    /**
     * Récupère les statistiques par catégorie.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Map catégorie -> nombre
     */
    public Map<String, Integer> getStatistiquesCategorie(int mois, int annee) {
        return consultationDAO.getStatsByCategorie(mois, annee);
    }
    
    /**
     * Récupère l'évolution hebdomadaire des consultations.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Liste du nombre de consultations par semaine
     */
    public List<Integer> getEvolutionHebdomadaire(int mois, int annee) {
        return consultationDAO.getEvolutionParSemaine(mois, annee);
    }
    
    /**
     * Calcule le taux de paiement d'un mois.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Taux de paiement en pourcentage
     */
    public double getTauxPaiement(int mois, int annee) {
        List<Consultation> consultations = consultationDAO.findByMois(mois, annee);
        if (consultations.isEmpty()) {
            return 0;
        }
        
        long payees = consultations.stream().filter(Consultation::isEstPayee).count();
        return (double) payees / consultations.size() * 100;
    }
}

