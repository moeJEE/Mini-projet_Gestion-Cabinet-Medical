package com.cabinetmedical.dao;

import java.util.List;

/**
 * Interface générique pour les opérations CRUD de base.
 * Définit le contrat standard pour tous les DAO du projet.
 * 
 * @param <T> Type de l'entité
 * @author Cabinet Medical
 * @version 1.0
 */
public interface GenericDAO<T> {
    
    /**
     * Crée une nouvelle entité dans la base de données.
     * 
     * @param entity Entité à créer
     * @return Entité créée avec son ID généré
     */
    T create(T entity);
    
    /**
     * Recherche une entité par son identifiant.
     * 
     * @param id Identifiant de l'entité
     * @return Entité trouvée ou null si non trouvée
     */
    T findById(int id);
    
    /**
     * Récupère toutes les entités.
     * 
     * @return Liste de toutes les entités
     */
    List<T> findAll();
    
    /**
     * Met à jour une entité existante.
     * 
     * @param entity Entité avec les nouvelles valeurs
     * @return Entité mise à jour
     */
    T update(T entity);
    
    /**
     * Supprime une entité par son identifiant.
     * 
     * @param id Identifiant de l'entité à supprimer
     * @return true si la suppression a réussi
     */
    boolean delete(int id);
}

