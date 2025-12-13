package com.cabinetmedical.dao;

import com.cabinetmedical.exceptions.DatabaseException;
import com.cabinetmedical.models.Patient;
import com.cabinetmedical.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des patients dans la base de données.
 * Implémente les opérations CRUD et les recherches spécifiques.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class PatientDAO implements GenericDAO<Patient> {
    
    private final DatabaseConnection dbConnection;
    
    /**
     * Constructeur par défaut.
     */
    public PatientDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Patient create(Patient patient) {
        String sql = "INSERT INTO patient (nom, telephone, email) VALUES (?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, patient.getNom());
            stmt.setString(2, patient.getTelephone());
            stmt.setString(3, patient.getEmail());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Création du patient échouée, aucune ligne affectée.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    patient.setId(generatedKeys.getInt(1));
                } else {
                    throw new DatabaseException("Création du patient échouée, aucun ID généré.");
                }
            }
            
            return patient;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la création du patient: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Patient findById(int id) {
        String sql = "SELECT * FROM patient WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche du patient: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Patient> findAll() {
        String sql = "SELECT * FROM patient ORDER BY nom ASC";
        List<Patient> patients = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
            
            return patients;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des patients: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Patient update(Patient patient) {
        String sql = "UPDATE patient SET nom = ?, telephone = ?, email = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patient.getNom());
            stmt.setString(2, patient.getTelephone());
            stmt.setString(3, patient.getEmail());
            stmt.setInt(4, patient.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Mise à jour du patient échouée, patient non trouvé.");
            }
            
            return patient;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise à jour du patient: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM patient WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression du patient: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recherche des patients par critères (nom, téléphone ou email).
     * 
     * @param criteria Critère de recherche
     * @return Liste des patients correspondants
     */
    public List<Patient> search(String criteria) {
        String sql = "SELECT * FROM patient WHERE nom LIKE ? OR telephone LIKE ? OR email LIKE ? ORDER BY nom ASC";
        List<Patient> patients = new ArrayList<>();
        String searchPattern = "%" + criteria + "%";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
            }
            
            return patients;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche des patients: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recherche un patient par son numéro de téléphone.
     * 
     * @param telephone Numéro de téléphone
     * @return Patient trouvé ou null
     */
    public Patient findByTelephone(String telephone) {
        String sql = "SELECT * FROM patient WHERE telephone = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, telephone);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche par téléphone: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recherche un patient par son email.
     * 
     * @param email Adresse email
     * @return Patient trouvé ou null
     */
    public Patient findByEmail(String email) {
        String sql = "SELECT * FROM patient WHERE email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche par email: " + e.getMessage(), e);
        }
    }
    
    /**
     * Compte le nombre total de patients.
     * 
     * @return Nombre de patients
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM patient";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du comptage des patients: " + e.getMessage(), e);
        }
    }
    
    /**
     * Mappe un ResultSet vers un objet Patient.
     * 
     * @param rs ResultSet positionné sur une ligne
     * @return Objet Patient
     * @throws SQLException si erreur de lecture
     */
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getInt("id"));
        patient.setNom(rs.getString("nom"));
        patient.setTelephone(rs.getString("telephone"));
        patient.setEmail(rs.getString("email"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            patient.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return patient;
    }
}

