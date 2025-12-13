package com.cabinetmedical.services;

import com.cabinetmedical.dao.CategorieDAO;
import com.cabinetmedical.exceptions.BusinessException;
import com.cabinetmedical.exceptions.NotFoundException;
import com.cabinetmedical.exceptions.ValidationException;
import com.cabinetmedical.models.Categorie;
import com.cabinetmedical.utils.ValidationHelper;

import java.util.List;

/**
 * Service pour la gestion des catégories de consultation.
 * Contient la logique métier et les validations.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class CategorieService {
    
    private final CategorieDAO categorieDAO;
    
    /**
     * Constructeur par défaut.
     */
    public CategorieService() {
        this.categorieDAO = new CategorieDAO();
    }
    
    /**
     * Constructeur avec injection du DAO.
     * 
     * @param categorieDAO DAO à utiliser
     */
    public CategorieService(CategorieDAO categorieDAO) {
        this.categorieDAO = categorieDAO;
    }
    
    /**
     * Crée une nouvelle catégorie.
     * 
     * @param categorie Catégorie à créer
     * @return Catégorie créée
     * @throws ValidationException si les données sont invalides
     * @throws BusinessException si la désignation existe déjà
     */
    public Categorie createCategorie(Categorie categorie) {
        validateCategorie(categorie);
        
        Categorie existing = categorieDAO.findByDesignation(categorie.getDesignation());
        if (existing != null) {
            throw new BusinessException("Une catégorie avec cette désignation existe déjà.");
        }
        
        return categorieDAO.create(categorie);
    }
    
    /**
     * Récupère une catégorie par son ID.
     * 
     * @param id ID de la catégorie
     * @return Catégorie trouvée
     * @throws NotFoundException si la catégorie n'existe pas
     */
    public Categorie getCategorieById(int id) {
        Categorie categorie = categorieDAO.findById(id);
        if (categorie == null) {
            throw new NotFoundException("Catégorie", id);
        }
        return categorie;
    }
    
    /**
     * Récupère une catégorie par sa désignation.
     * 
     * @param designation Désignation de la catégorie
     * @return Catégorie trouvée ou null
     */
    public Categorie getCategorieByDesignation(String designation) {
        return categorieDAO.findByDesignation(designation);
    }
    
    /**
     * Récupère toutes les catégories.
     * 
     * @return Liste de toutes les catégories
     */
    public List<Categorie> getAllCategories() {
        return categorieDAO.findAll();
    }
    
    /**
     * Met à jour une catégorie.
     * 
     * @param categorie Catégorie avec les nouvelles valeurs
     * @return Catégorie mise à jour
     * @throws NotFoundException si la catégorie n'existe pas
     * @throws BusinessException si une autre catégorie a la même désignation
     */
    public Categorie updateCategorie(Categorie categorie) {
        Categorie existing = categorieDAO.findById(categorie.getId());
        if (existing == null) {
            throw new NotFoundException("Catégorie", categorie.getId());
        }
        
        validateCategorie(categorie);
        
        // Vérifier l'unicité de la désignation
        Categorie byDesignation = categorieDAO.findByDesignation(categorie.getDesignation());
        if (byDesignation != null && byDesignation.getId() != categorie.getId()) {
            throw new BusinessException("Une autre catégorie a déjà cette désignation.");
        }
        
        return categorieDAO.update(categorie);
    }
    
    /**
     * Supprime une catégorie.
     * 
     * @param id ID de la catégorie
     * @return true si la suppression a réussi
     * @throws NotFoundException si la catégorie n'existe pas
     * @throws BusinessException si la catégorie est utilisée par des consultations
     */
    public boolean deleteCategorie(int id) {
        Categorie existing = categorieDAO.findById(id);
        if (existing == null) {
            throw new NotFoundException("Catégorie", id);
        }
        
        if (categorieDAO.isUsedByConsultations(id)) {
            throw new BusinessException("Cette catégorie est utilisée par des consultations et ne peut pas être supprimée.");
        }
        
        return categorieDAO.delete(id);
    }
    
    /**
     * Compte le nombre total de catégories.
     * 
     * @return Nombre de catégories
     */
    public int countCategories() {
        return categorieDAO.count();
    }
    
    /**
     * Valide les données d'une catégorie.
     * 
     * @param categorie Catégorie à valider
     * @throws ValidationException si les données sont invalides
     */
    private void validateCategorie(Categorie categorie) {
        if (!ValidationHelper.isNotEmpty(categorie.getDesignation())) {
            throw new ValidationException("designation", "La désignation est obligatoire.");
        }
        
        if (categorie.getDesignation().length() > 50) {
            throw new ValidationException("designation", "La désignation ne peut pas dépasser 50 caractères.");
        }
    }
}

