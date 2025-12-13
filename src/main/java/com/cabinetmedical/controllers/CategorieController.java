package com.cabinetmedical.controllers;

import com.cabinetmedical.exceptions.BusinessException;
import com.cabinetmedical.exceptions.NotFoundException;
import com.cabinetmedical.exceptions.ValidationException;
import com.cabinetmedical.models.Categorie;
import com.cabinetmedical.services.CategorieService;

import java.util.List;

/**
 * Contrôleur pour la gestion des catégories de consultation.
 * Fournit des méthodes formatées pour les composants Swing.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class CategorieController {
    
    private final CategorieService categorieService;
    
    /**
     * Constructeur par défaut.
     */
    public CategorieController() {
        this.categorieService = new CategorieService();
    }
    
    /**
     * Récupère les catégories formatées pour JTable.
     * 
     * @return Tableau 2D [ID, Désignation, Description]
     */
    public Object[][] getCategoriesForTable() {
        List<Categorie> categories = categorieService.getAllCategories();
        Object[][] data = new Object[categories.size()][3];
        
        for (int i = 0; i < categories.size(); i++) {
            Categorie c = categories.get(i);
            data[i][0] = c.getId();
            data[i][1] = c.getDesignation();
            data[i][2] = c.getDescription() != null ? c.getDescription() : "";
        }
        
        return data;
    }
    
    /**
     * Retourne les noms des colonnes pour la table des catégories.
     * 
     * @return Tableau des noms de colonnes
     */
    public String[] getCategorieTableColumns() {
        return new String[]{"ID", "Désignation", "Description"};
    }
    
    /**
     * Récupère les catégories formatées pour JComboBox.
     * 
     * @return Tableau des désignations
     */
    public String[] getCategoriesForComboBox() {
        List<Categorie> categories = categorieService.getAllCategories();
        return categories.stream()
                .map(Categorie::getDesignation)
                .toArray(String[]::new);
    }
    
    /**
     * Récupère l'ID d'une catégorie par sa désignation.
     * 
     * @param designation Désignation de la catégorie
     * @return ID de la catégorie ou -1
     */
    public int getCategorieIdByName(String designation) {
        Categorie categorie = categorieService.getCategorieByDesignation(designation);
        return categorie != null ? categorie.getId() : -1;
    }
    
    /**
     * Crée une nouvelle catégorie.
     * 
     * @param designation Désignation de la catégorie
     * @param description Description de la catégorie
     * @return Message de résultat
     */
    public String createCategorie(String designation, String description) {
        try {
            if (designation == null || designation.trim().isEmpty()) {
                return "La désignation est requise";
            }
            
            Categorie categorie = new Categorie();
            categorie.setDesignation(designation.trim());
            categorie.setDescription(description != null ? description.trim() : null);
            
            categorieService.createCategorie(categorie);
            return "Catégorie ajoutée avec succès";
            
        } catch (ValidationException | BusinessException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
    
    /**
     * Met à jour une catégorie existante.
     * 
     * @param id ID de la catégorie
     * @param designation Nouvelle désignation
     * @param description Nouvelle description
     * @return Message de résultat
     */
    public String updateCategorie(int id, String designation, String description) {
        try {
            if (designation == null || designation.trim().isEmpty()) {
                return "La désignation est requise";
            }
            
            Categorie categorie = new Categorie();
            categorie.setId(id);
            categorie.setDesignation(designation.trim());
            categorie.setDescription(description != null ? description.trim() : null);
            
            categorieService.updateCategorie(categorie);
            return "Catégorie modifiée avec succès";
            
        } catch (ValidationException | BusinessException | NotFoundException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
    
    /**
     * Supprime une catégorie.
     * 
     * @param id ID de la catégorie
     * @return Message de résultat
     */
    public String deleteCategorie(int id) {
        try {
            categorieService.deleteCategorie(id);
            return "Catégorie supprimée avec succès";
            
        } catch (NotFoundException | BusinessException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
    
    /**
     * Récupère les données d'une catégorie pour un formulaire.
     * 
     * @param id ID de la catégorie
     * @return Tableau [désignation, description] ou null
     */
    public String[] getCategorieFormData(int id) {
        try {
            Categorie categorie = categorieService.getCategorieById(id);
            return new String[] {
                categorie.getDesignation(),
                categorie.getDescription() != null ? categorie.getDescription() : ""
            };
        } catch (NotFoundException e) {
            return null;
        }
    }
}

