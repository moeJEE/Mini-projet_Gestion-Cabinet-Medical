package com.cabinetmedical.dao;

import com.cabinetmedical.exceptions.DatabaseException;
import com.cabinetmedical.models.*;
import com.cabinetmedical.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des utilisateurs (médecins et assistants).
 * Gère l'authentification et les opérations CRUD sur les utilisateurs.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class UtilisateurDAO implements GenericDAO<Utilisateur> {
    
    private final DatabaseConnection dbConnection;
    
    /**
     * Constructeur par défaut.
     */
    public UtilisateurDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Utilisateur create(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (login, password, type) VALUES (?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, utilisateur.getLogin());
            stmt.setString(2, utilisateur.getPassword());
            stmt.setString(3, utilisateur.getType().getValue());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Création de l'utilisateur échouée.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    utilisateur.setId(generatedKeys.getInt(1));
                }
            }
            
            return utilisateur;
            
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DatabaseException("Un utilisateur avec ce login existe déjà.", e);
            }
            throw new DatabaseException("Erreur lors de la création de l'utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Utilisateur findById(int id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtilisateur(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche de l'utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Utilisateur> findAll() {
        String sql = "SELECT * FROM utilisateur ORDER BY login ASC";
        List<Utilisateur> utilisateurs = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }
            
            return utilisateurs;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Utilisateur update(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET login = ?, password = ?, type = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, utilisateur.getLogin());
            stmt.setString(2, utilisateur.getPassword());
            stmt.setString(3, utilisateur.getType().getValue());
            stmt.setInt(4, utilisateur.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Mise à jour de l'utilisateur échouée.");
            }
            
            return utilisateur;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression de l'utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recherche un utilisateur par son login.
     * 
     * @param login Login de l'utilisateur
     * @return Utilisateur trouvé ou null
     */
    public Utilisateur findByLogin(String login) {
        String sql = "SELECT * FROM utilisateur WHERE login = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, login);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtilisateur(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche par login: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère tous les médecins.
     * 
     * @return Liste des médecins
     */
    public List<Medecin> findAllMedecins() {
        String sql = "SELECT * FROM utilisateur WHERE type = 'MEDECIN' ORDER BY login ASC";
        List<Medecin> medecins = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Medecin medecin = new Medecin(
                    rs.getInt("id"),
                    rs.getString("login"),
                    rs.getString("password")
                );
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    medecin.setCreatedAt(createdAt.toLocalDateTime());
                }
                medecins.add(medecin);
            }
            
            return medecins;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des médecins: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère tous les assistants.
     * 
     * @return Liste des assistants
     */
    public List<Assistant> findAllAssistants() {
        String sql = "SELECT * FROM utilisateur WHERE type = 'ASSISTANT' ORDER BY login ASC";
        List<Assistant> assistants = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Assistant assistant = new Assistant(
                    rs.getInt("id"),
                    rs.getString("login"),
                    rs.getString("password")
                );
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    assistant.setCreatedAt(createdAt.toLocalDateTime());
                }
                assistants.add(assistant);
            }
            
            return assistants;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des assistants: " + e.getMessage(), e);
        }
    }
    
    /**
     * Vérifie si un login existe déjà.
     * 
     * @param login Login à vérifier
     * @return true si le login existe
     */
    public boolean loginExists(String login) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE login = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, login);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la vérification du login: " + e.getMessage(), e);
        }
    }
    
    /**
     * Met à jour uniquement le mot de passe d'un utilisateur.
     * 
     * @param userId ID de l'utilisateur
     * @param newPasswordHash Nouveau mot de passe hashé
     * @return true si la mise à jour a réussi
     */
    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE utilisateur SET password = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise à jour du mot de passe: " + e.getMessage(), e);
        }
    }
    
    /**
     * Mappe un ResultSet vers un objet Utilisateur (Medecin ou Assistant).
     * 
     * @param rs ResultSet positionné sur une ligne
     * @return Objet Utilisateur approprié
     * @throws SQLException si erreur de lecture
     */
    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        TypeUtilisateur type = TypeUtilisateur.fromString(rs.getString("type"));
        Utilisateur utilisateur;
        
        if (type == TypeUtilisateur.MEDECIN) {
            utilisateur = new Medecin(
                rs.getInt("id"),
                rs.getString("login"),
                rs.getString("password")
            );
        } else {
            utilisateur = new Assistant(
                rs.getInt("id"),
                rs.getString("login"),
                rs.getString("password")
            );
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            utilisateur.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return utilisateur;
    }
}

