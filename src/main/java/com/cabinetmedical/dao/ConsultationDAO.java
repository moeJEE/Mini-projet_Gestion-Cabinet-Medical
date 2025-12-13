package com.cabinetmedical.dao;

import com.cabinetmedical.exceptions.DatabaseException;
import com.cabinetmedical.models.*;
import com.cabinetmedical.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO pour la gestion des consultations/rendez-vous.
 * Implémente les opérations CRUD et les requêtes complexes pour les consultations.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class ConsultationDAO implements GenericDAO<Consultation> {
    
    private final DatabaseConnection dbConnection;
    
    /**
     * Constructeur par défaut.
     */
    public ConsultationDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Consultation create(Consultation consultation) {
        String sql = "INSERT INTO consultation (date, description, prix_consultation, patient_id, categorie_id, medecin_id, est_payee) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(consultation.getDate()));
            stmt.setString(2, consultation.getDescription());
            stmt.setDouble(3, consultation.getPrixConsultation());
            stmt.setInt(4, consultation.getPatientId());
            stmt.setInt(5, consultation.getCategorieId());
            stmt.setInt(6, consultation.getMedecinId());
            stmt.setBoolean(7, consultation.isEstPayee());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Création de la consultation échouée.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    consultation.setId(generatedKeys.getInt(1));
                }
            }
            
            return consultation;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la création de la consultation: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Consultation findById(int id) {
        String sql = "SELECT c.*, p.nom as patient_nom, p.telephone as patient_telephone, p.email as patient_email, " +
                     "cat.designation as categorie_designation, cat.description as categorie_description, " +
                     "u.login as medecin_login " +
                     "FROM consultation c " +
                     "JOIN patient p ON c.patient_id = p.id " +
                     "JOIN categorie cat ON c.categorie_id = cat.id " +
                     "JOIN utilisateur u ON c.medecin_id = u.id " +
                     "WHERE c.id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConsultationWithDetails(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche de la consultation: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Consultation> findAll() {
        String sql = "SELECT c.*, p.nom as patient_nom, p.telephone as patient_telephone, p.email as patient_email, " +
                     "cat.designation as categorie_designation, cat.description as categorie_description, " +
                     "u.login as medecin_login " +
                     "FROM consultation c " +
                     "JOIN patient p ON c.patient_id = p.id " +
                     "JOIN categorie cat ON c.categorie_id = cat.id " +
                     "JOIN utilisateur u ON c.medecin_id = u.id " +
                     "ORDER BY c.date DESC";
        
        List<Consultation> consultations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                consultations.add(mapResultSetToConsultationWithDetails(rs));
            }
            
            return consultations;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des consultations: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Consultation update(Consultation consultation) {
        String sql = "UPDATE consultation SET date = ?, description = ?, prix_consultation = ?, " +
                     "patient_id = ?, categorie_id = ?, medecin_id = ?, est_payee = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(consultation.getDate()));
            stmt.setString(2, consultation.getDescription());
            stmt.setDouble(3, consultation.getPrixConsultation());
            stmt.setInt(4, consultation.getPatientId());
            stmt.setInt(5, consultation.getCategorieId());
            stmt.setInt(6, consultation.getMedecinId());
            stmt.setBoolean(7, consultation.isEstPayee());
            stmt.setInt(8, consultation.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Mise à jour de la consultation échouée.");
            }
            
            return consultation;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise à jour de la consultation: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM consultation WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression de la consultation: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les consultations d'une date spécifique.
     * 
     * @param date Date des consultations
     * @return Liste des consultations
     */
    public List<Consultation> findByDate(LocalDate date) {
        String sql = "SELECT c.*, p.nom as patient_nom, p.telephone as patient_telephone, p.email as patient_email, " +
                     "cat.designation as categorie_designation, cat.description as categorie_description, " +
                     "u.login as medecin_login " +
                     "FROM consultation c " +
                     "JOIN patient p ON c.patient_id = p.id " +
                     "JOIN categorie cat ON c.categorie_id = cat.id " +
                     "JOIN utilisateur u ON c.medecin_id = u.id " +
                     "WHERE DATE(c.date) = ? " +
                     "ORDER BY c.date ASC";
        
        List<Consultation> consultations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    consultations.add(mapResultSetToConsultationWithDetails(rs));
                }
            }
            
            return consultations;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche par date: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les consultations journalières d'un médecin.
     * 
     * @param medecinId ID du médecin
     * @param date Date des consultations
     * @return Liste des consultations
     */
    public List<Consultation> findByMedecinAndDate(int medecinId, LocalDate date) {
        String sql = "SELECT c.*, p.nom as patient_nom, p.telephone as patient_telephone, p.email as patient_email, " +
                     "cat.designation as categorie_designation, cat.description as categorie_description, " +
                     "u.login as medecin_login " +
                     "FROM consultation c " +
                     "JOIN patient p ON c.patient_id = p.id " +
                     "JOIN categorie cat ON c.categorie_id = cat.id " +
                     "JOIN utilisateur u ON c.medecin_id = u.id " +
                     "WHERE c.medecin_id = ? AND DATE(c.date) = ? " +
                     "ORDER BY c.date ASC";
        
        List<Consultation> consultations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, medecinId);
            stmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    consultations.add(mapResultSetToConsultationWithDetails(rs));
                }
            }
            
            return consultations;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche des consultations du médecin: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les consultations non payées.
     * 
     * @return Liste des consultations non payées
     */
    public List<Consultation> findNonPayees() {
        String sql = "SELECT c.*, p.nom as patient_nom, p.telephone as patient_telephone, p.email as patient_email, " +
                     "cat.designation as categorie_designation, cat.description as categorie_description, " +
                     "u.login as medecin_login " +
                     "FROM consultation c " +
                     "JOIN patient p ON c.patient_id = p.id " +
                     "JOIN categorie cat ON c.categorie_id = cat.id " +
                     "JOIN utilisateur u ON c.medecin_id = u.id " +
                     "WHERE c.est_payee = FALSE " +
                     "ORDER BY c.date DESC";
        
        List<Consultation> consultations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                consultations.add(mapResultSetToConsultationWithDetails(rs));
            }
            
            return consultations;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des consultations non payées: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valide le paiement d'une consultation.
     * 
     * @param consultationId ID de la consultation
     * @return true si le paiement a été validé
     */
    public boolean validerPaiement(int consultationId) {
        String sql = "UPDATE consultation SET est_payee = TRUE WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, consultationId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la validation du paiement: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les consultations pour la relance (J-1 ou J-2).
     * 
     * @param date Date de référence
     * @return Liste des consultations à relancer
     */
    public List<Consultation> findForRelance(LocalDate date) {
        String sql = "SELECT c.*, p.nom as patient_nom, p.telephone as patient_telephone, p.email as patient_email, " +
                     "cat.designation as categorie_designation, cat.description as categorie_description, " +
                     "u.login as medecin_login " +
                     "FROM consultation c " +
                     "JOIN patient p ON c.patient_id = p.id " +
                     "JOIN categorie cat ON c.categorie_id = cat.id " +
                     "JOIN utilisateur u ON c.medecin_id = u.id " +
                     "WHERE DATE(c.date) BETWEEN ? AND ? " +
                     "ORDER BY c.date ASC";
        
        List<Consultation> consultations = new ArrayList<>();
        LocalDate tomorrow = date.plusDays(1);
        LocalDate dayAfter = date.plusDays(2);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(tomorrow));
            stmt.setDate(2, Date.valueOf(dayAfter));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    consultations.add(mapResultSetToConsultationWithDetails(rs));
                }
            }
            
            return consultations;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des consultations à relancer: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les consultations d'un mois donné pour le bilan.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Liste des consultations
     */
    public List<Consultation> findByMois(int mois, int annee) {
        String sql = "SELECT c.*, p.nom as patient_nom, p.telephone as patient_telephone, p.email as patient_email, " +
                     "cat.designation as categorie_designation, cat.description as categorie_description, " +
                     "u.login as medecin_login " +
                     "FROM consultation c " +
                     "JOIN patient p ON c.patient_id = p.id " +
                     "JOIN categorie cat ON c.categorie_id = cat.id " +
                     "JOIN utilisateur u ON c.medecin_id = u.id " +
                     "WHERE MONTH(c.date) = ? AND YEAR(c.date) = ? " +
                     "ORDER BY c.date ASC";
        
        List<Consultation> consultations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, mois);
            stmt.setInt(2, annee);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    consultations.add(mapResultSetToConsultationWithDetails(rs));
                }
            }
            
            return consultations;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des consultations du mois: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les statistiques par catégorie pour un mois.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Map catégorie -> nombre de consultations
     */
    public Map<String, Integer> getStatsByCategorie(int mois, int annee) {
        String sql = "SELECT cat.designation, COUNT(*) as nombre " +
                     "FROM consultation c " +
                     "JOIN categorie cat ON c.categorie_id = cat.id " +
                     "WHERE MONTH(c.date) = ? AND YEAR(c.date) = ? " +
                     "GROUP BY cat.designation";
        
        Map<String, Integer> stats = new HashMap<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, mois);
            stmt.setInt(2, annee);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("designation"), rs.getInt("nombre"));
                }
            }
            
            return stats;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du calcul des statistiques: " + e.getMessage(), e);
        }
    }
    
    /**
     * Calcule le chiffre d'affaires d'un mois.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Chiffre d'affaires total
     */
    public double getChiffreAffaires(int mois, int annee) {
        String sql = "SELECT COALESCE(SUM(prix_consultation), 0) as total " +
                     "FROM consultation " +
                     "WHERE MONTH(date) = ? AND YEAR(date) = ? AND est_payee = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, mois);
            stmt.setInt(2, annee);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du calcul du chiffre d'affaires: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les consultations d'un patient.
     * 
     * @param patientId ID du patient
     * @return Liste des consultations
     */
    public List<Consultation> findByPatient(int patientId) {
        String sql = "SELECT c.*, p.nom as patient_nom, p.telephone as patient_telephone, p.email as patient_email, " +
                     "cat.designation as categorie_designation, cat.description as categorie_description, " +
                     "u.login as medecin_login " +
                     "FROM consultation c " +
                     "JOIN patient p ON c.patient_id = p.id " +
                     "JOIN categorie cat ON c.categorie_id = cat.id " +
                     "JOIN utilisateur u ON c.medecin_id = u.id " +
                     "WHERE c.patient_id = ? " +
                     "ORDER BY c.date DESC";
        
        List<Consultation> consultations = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, patientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    consultations.add(mapResultSetToConsultationWithDetails(rs));
                }
            }
            
            return consultations;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche des consultations du patient: " + e.getMessage(), e);
        }
    }
    
    /**
     * Vérifie si un créneau est disponible pour un patient.
     * 
     * @param patientId ID du patient
     * @param date Date et heure
     * @return true si le créneau est libre
     */
    public boolean isCreneauDisponible(int patientId, LocalDateTime date) {
        // Vérifie si le patient n'a pas déjà une consultation dans l'heure
        String sql = "SELECT COUNT(*) FROM consultation " +
                     "WHERE patient_id = ? AND date BETWEEN ? AND ?";
        
        LocalDateTime debut = date.minusMinutes(30);
        LocalDateTime fin = date.plusMinutes(30);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, patientId);
            stmt.setTimestamp(2, Timestamp.valueOf(debut));
            stmt.setTimestamp(3, Timestamp.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
            
            return true;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la vérification du créneau: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère l'évolution des consultations par semaine pour un mois.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Liste du nombre de consultations par semaine
     */
    public List<Integer> getEvolutionParSemaine(int mois, int annee) {
        String sql = "SELECT WEEK(date, 1) - WEEK(DATE_FORMAT(date, '%Y-%m-01'), 1) + 1 as semaine, " +
                     "COUNT(*) as nombre " +
                     "FROM consultation " +
                     "WHERE MONTH(date) = ? AND YEAR(date) = ? " +
                     "GROUP BY semaine " +
                     "ORDER BY semaine";
        
        List<Integer> evolution = new ArrayList<>();
        // Initialiser avec 5 semaines à 0
        for (int i = 0; i < 5; i++) {
            evolution.add(0);
        }
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, mois);
            stmt.setInt(2, annee);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int semaine = rs.getInt("semaine");
                    int nombre = rs.getInt("nombre");
                    if (semaine >= 1 && semaine <= 5) {
                        evolution.set(semaine - 1, nombre);
                    }
                }
            }
            
            return evolution;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du calcul de l'évolution: " + e.getMessage(), e);
        }
    }
    
    /**
     * Mappe un ResultSet vers un objet Consultation avec les détails des relations.
     * 
     * @param rs ResultSet positionné sur une ligne
     * @return Objet Consultation complet
     * @throws SQLException si erreur de lecture
     */
    private Consultation mapResultSetToConsultationWithDetails(ResultSet rs) throws SQLException {
        Consultation consultation = new Consultation();
        consultation.setId(rs.getInt("id"));
        consultation.setDate(rs.getTimestamp("date").toLocalDateTime());
        consultation.setDescription(rs.getString("description"));
        consultation.setPrixConsultation(rs.getDouble("prix_consultation"));
        consultation.setPatientId(rs.getInt("patient_id"));
        consultation.setCategorieId(rs.getInt("categorie_id"));
        consultation.setMedecinId(rs.getInt("medecin_id"));
        consultation.setEstPayee(rs.getBoolean("est_payee"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            consultation.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        // Mapper le patient
        Patient patient = new Patient();
        patient.setId(rs.getInt("patient_id"));
        patient.setNom(rs.getString("patient_nom"));
        patient.setTelephone(rs.getString("patient_telephone"));
        patient.setEmail(rs.getString("patient_email"));
        consultation.setPatient(patient);
        
        // Mapper la catégorie
        Categorie categorie = new Categorie();
        categorie.setId(rs.getInt("categorie_id"));
        categorie.setDesignation(rs.getString("categorie_designation"));
        categorie.setDescription(rs.getString("categorie_description"));
        consultation.setCategorie(categorie);
        
        // Mapper le médecin
        Medecin medecin = new Medecin();
        medecin.setId(rs.getInt("medecin_id"));
        medecin.setLogin(rs.getString("medecin_login"));
        consultation.setMedecin(medecin);
        
        return consultation;
    }
}

