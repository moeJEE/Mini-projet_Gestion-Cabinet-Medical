package com.cabinetmedical.controllers;

import com.cabinetmedical.models.BilanMensuel;
import com.cabinetmedical.services.BilanService;
import com.cabinetmedical.utils.DateHelper;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour la génération des bilans et statistiques.
 * Fournit des méthodes formatées pour les composants Swing.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class BilanController {
    
    private final BilanService bilanService;
    
    /**
     * Constructeur par défaut.
     */
    public BilanController() {
        this.bilanService = new BilanService();
    }
    
    /**
     * Récupère le bilan mensuel formaté pour affichage.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Tableau de lignes formatées
     */
    public String[] getBilanMensuelFormatted(int mois, int annee) {
        BilanMensuel bilan = bilanService.getBilanMensuel(mois, annee);
        
        return new String[] {
            "═══════════════════════════════════════════════════",
            "        BILAN MENSUEL - " + bilan.getNomMois() + " " + annee,
            "═══════════════════════════════════════════════════",
            "",
            "📊 STATISTIQUES GÉNÉRALES",
            "───────────────────────────────────────────────────",
            "   Nombre total de consultations : " + bilan.getNombreConsultations(),
            "   Chiffre d'affaires           : " + String.format("%.2f", bilan.getChiffreAffaires()) + " DH",
            "   Prix moyen par consultation  : " + String.format("%.2f", bilan.getPrixMoyen()) + " DH",
            "",
            "💰 STATISTIQUES DE PAIEMENT",
            "───────────────────────────────────────────────────",
            "   Consultations payées         : " + bilan.getConsultationsPayees(),
            "   Consultations impayées       : " + bilan.getConsultationsImpayees(),
            "   Montant des impayés          : " + String.format("%.2f", bilan.getMontantImpayes()) + " DH",
            "   Taux de paiement            : " + String.format("%.1f", bilan.getTauxPaiement()) + "%",
            "",
            "📋 CONSULTATIONS PAR CATÉGORIE",
            "───────────────────────────────────────────────────",
            formatConsultationsParCategorie(bilan.getConsultationsParCategorie())
        };
    }
    
    /**
     * Formate les consultations par catégorie.
     * 
     * @param stats Map catégorie -> nombre
     * @return Chaîne formatée
     */
    private String formatConsultationsParCategorie(Map<String, Integer> stats) {
        if (stats == null || stats.isEmpty()) {
            return "   Aucune donnée";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            sb.append("   • ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString().trim();
    }
    
    /**
     * Récupère l'évolution hebdomadaire formatée pour JTable.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Tableau 2D [Semaine, Nombre de consultations]
     */
    public Object[][] getEvolutionForTable(int mois, int annee) {
        List<Integer> evolution = bilanService.getEvolutionHebdomadaire(mois, annee);
        Object[][] data = new Object[evolution.size()][2];
        
        for (int i = 0; i < evolution.size(); i++) {
            data[i][0] = "Semaine " + (i + 1);
            data[i][1] = evolution.get(i);
        }
        
        return data;
    }
    
    /**
     * Retourne les colonnes pour la table d'évolution.
     * 
     * @return Tableau des noms de colonnes
     */
    public String[] getEvolutionTableColumns() {
        return new String[]{"Semaine", "Nombre de consultations"};
    }
    
    /**
     * Récupère les statistiques par catégorie formatées pour JTable.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Tableau 2D [Catégorie, Nombre]
     */
    public Object[][] getStatsCategorieForTable(int mois, int annee) {
        Map<String, Integer> stats = bilanService.getStatistiquesCategorie(mois, annee);
        Object[][] data = new Object[stats.size()][2];
        
        int i = 0;
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            data[i][0] = entry.getKey();
            data[i][1] = entry.getValue();
            i++;
        }
        
        return data;
    }
    
    /**
     * Retourne les colonnes pour la table des statistiques par catégorie.
     * 
     * @return Tableau des noms de colonnes
     */
    public String[] getStatsCategorieTableColumns() {
        return new String[]{"Catégorie", "Nombre de consultations"};
    }
    
    /**
     * Récupère les mois pour JComboBox.
     * 
     * @return Tableau des noms de mois
     */
    public String[] getMoisForComboBox() {
        return DateHelper.getMoisArray();
    }
    
    /**
     * Récupère les années pour JComboBox.
     * 
     * @return Tableau des années
     */
    public String[] getAnneesForComboBox() {
        return DateHelper.getAnneesArray();
    }
    
    /**
     * Calcule le résumé du bilan en une ligne.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Résumé formaté
     */
    public String getBilanResume(int mois, int annee) {
        BilanMensuel bilan = bilanService.getBilanMensuel(mois, annee);
        return String.format("%s %d : %d consultations, %.2f DH (%.1f%% payées)",
            bilan.getNomMois(),
            annee,
            bilan.getNombreConsultations(),
            bilan.getChiffreAffaires(),
            bilan.getTauxPaiement()
        );
    }
    
    /**
     * Récupère le chiffre d'affaires d'un mois.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Chiffre d'affaires formaté
     */
    public String getChiffreAffairesFormatted(int mois, int annee) {
        double ca = bilanService.getChiffreAffairesMensuel(mois, annee);
        return String.format("%.2f DH", ca);
    }
    
    /**
     * Récupère le taux de paiement d'un mois.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Taux formaté en pourcentage
     */
    public String getTauxPaiementFormatted(int mois, int annee) {
        double taux = bilanService.getTauxPaiement(mois, annee);
        return String.format("%.1f%%", taux);
    }
}

