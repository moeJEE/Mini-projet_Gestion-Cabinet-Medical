package com.cabinetmedical.dao;

import com.cabinetmedical.exceptions.DatabaseException;
import com.cabinetmedical.models.Categorie;
import com.cabinetmedical.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des catégories de consultation.
 * Implémente les opérations CRUD sur les catégories.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class CategorieDAO implements GenericDAO<Categorie> {
    
    private final DatabaseConnection dbConnection;
    
    /**
     * Constructeur par défaut.
     */
    public CategorieDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Categorie create(Categorie categorie) {
        String sql = "INSERT INTO categorie (designation, description) VALUES (?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, categorie.getDesignation());
            stmt.setString(2, categorie.getDescription());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Création de la catégorie échouée.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categorie.setId(generatedKeys.getInt(1));
                }
            }
            
            return categorie;
            
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DatabaseException("Une catégorie avec cette désignation existe déjà.", e);
            }
            throw new DatabaseException("Erreur lors de la création de la catégorie: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Categorie findById(int id) {
        String sql = "SELECT * FROM categorie WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategorie(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche de la catégorie: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Categorie> findAll() {
        String sql = "SELECT * FROM categorie ORDER BY designation ASC";
        List<Categorie> categories = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategorie(rs));
            }
            
            return categories;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des catégories: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Categorie update(Categorie categorie) {
        String sql = "UPDATE categorie SET designation = ?, description = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categorie.getDesignation());
            stmt.setString(2, categorie.getDescription());
            stmt.setInt(3, categorie.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Mise à jour de la catégorie échouée.");
            }
            
            return categorie;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise à jour de la catégorie: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM categorie WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression de la catégorie: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recherche une catégorie par sa désignation.
     * 
     * @param designation Désignation de la catégorie
     * @return Catégorie trouvée ou null
     */
    public Categorie findByDesignation(String designation) {
        String sql = "SELECT * FROM categorie WHERE designation = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, designation);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategorie(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche par désignation: " + e.getMessage(), e);
        }
    }
    
    /**
     * Vérifie si une catégorie est utilisée par des consultations.
     * 
     * @param categorieId ID de la catégorie
     * @return true si la catégorie est utilisée
     */
    public boolean isUsedByConsultations(int categorieId) {
        String sql = "SELECT COUNT(*) FROM consultation WHERE categorie_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categorieId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la vérification d'utilisation: " + e.getMessage(), e);
        }
    }
    
    /**
     * Compte le nombre total de catégories.
     * 
     * @return Nombre de catégories
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM categorie";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du comptage des catégories: " + e.getMessage(), e);
        }
    }
    
    /**
     * Mappe un ResultSet vers un objet Categorie.
     * 
     * @param rs ResultSet positionné sur une ligne
     * @return Objet Categorie
     * @throws SQLException si erreur de lecture
     */
    private Categorie mapResultSetToCategorie(ResultSet rs) throws SQLException {
        return new Categorie(
            rs.getInt("id"),
            rs.getString("designation"),
            rs.getString("description")
        );
    }
}

