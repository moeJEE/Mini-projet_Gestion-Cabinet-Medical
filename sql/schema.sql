-- =============================================================================
-- CABINET MEDICAL - Script de création de la base de données
-- =============================================================================
-- Ce script crée la structure de la base de données pour l'application
-- de gestion de cabinet médical.
-- =============================================================================

-- Créer la base de données
CREATE DATABASE IF NOT EXISTS cabinet_medical 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Utiliser la base de données
USE cabinet_medical;

-- =============================================================================
-- TABLE: utilisateur
-- Stocke les médecins et assistants du cabinet
-- =============================================================================
CREATE TABLE IF NOT EXISTS utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL COMMENT 'Mot de passe hashé avec BCrypt',
    type ENUM('MEDECIN', 'ASSISTANT') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_login (login),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- TABLE: patient
-- Stocke les informations des patients
-- =============================================================================
CREATE TABLE IF NOT EXISTS patient (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    telephone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_nom (nom),
    INDEX idx_telephone (telephone),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- TABLE: categorie
-- Catégories de consultations (Normale, Contrôle, Urgence, etc.)
-- =============================================================================
CREATE TABLE IF NOT EXISTS categorie (
    id INT AUTO_INCREMENT PRIMARY KEY,
    designation VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    
    INDEX idx_designation (designation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- TABLE: consultation
-- Rendez-vous et consultations médicales
-- =============================================================================
CREATE TABLE IF NOT EXISTS consultation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL COMMENT 'Date et heure de la consultation',
    description TEXT COMMENT 'Notes et observations',
    prix_consultation DECIMAL(10,2) NOT NULL COMMENT 'Prix en DH',
    patient_id INT NOT NULL,
    categorie_id INT NOT NULL,
    medecin_id INT NOT NULL,
    est_payee BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Clés étrangères
    FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE,
    FOREIGN KEY (categorie_id) REFERENCES categorie(id) ON DELETE RESTRICT,
    FOREIGN KEY (medecin_id) REFERENCES utilisateur(id) ON DELETE RESTRICT,
    
    -- Index pour les requêtes fréquentes
    INDEX idx_date (date),
    INDEX idx_patient (patient_id),
    INDEX idx_medecin (medecin_id),
    INDEX idx_categorie (categorie_id),
    INDEX idx_est_payee (est_payee),
    INDEX idx_medecin_date (medecin_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- VUES UTILES
-- =============================================================================

-- Vue pour les consultations avec détails
CREATE OR REPLACE VIEW v_consultations_details AS
SELECT 
    c.id,
    c.date,
    c.description,
    c.prix_consultation,
    c.est_payee,
    c.created_at,
    p.id AS patient_id,
    p.nom AS patient_nom,
    p.telephone AS patient_telephone,
    p.email AS patient_email,
    cat.id AS categorie_id,
    cat.designation AS categorie_designation,
    u.id AS medecin_id,
    u.login AS medecin_login
FROM consultation c
JOIN patient p ON c.patient_id = p.id
JOIN categorie cat ON c.categorie_id = cat.id
JOIN utilisateur u ON c.medecin_id = u.id;

-- Vue pour les consultations du jour
CREATE OR REPLACE VIEW v_consultations_jour AS
SELECT * FROM v_consultations_details
WHERE DATE(date) = CURDATE()
ORDER BY date ASC;

-- Vue pour les consultations non payées
CREATE OR REPLACE VIEW v_consultations_impayees AS
SELECT * FROM v_consultations_details
WHERE est_payee = FALSE
ORDER BY date DESC;

-- =============================================================================
-- PROCEDURES STOCKEES
-- =============================================================================

-- Procédure pour obtenir les statistiques mensuelles
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_bilan_mensuel(IN p_mois INT, IN p_annee INT)
BEGIN
    SELECT 
        COUNT(*) AS nombre_consultations,
        COALESCE(SUM(CASE WHEN est_payee = TRUE THEN prix_consultation ELSE 0 END), 0) AS chiffre_affaires,
        SUM(CASE WHEN est_payee = TRUE THEN 1 ELSE 0 END) AS consultations_payees,
        SUM(CASE WHEN est_payee = FALSE THEN 1 ELSE 0 END) AS consultations_impayees,
        COALESCE(SUM(CASE WHEN est_payee = FALSE THEN prix_consultation ELSE 0 END), 0) AS montant_impayes
    FROM consultation
    WHERE MONTH(date) = p_mois AND YEAR(date) = p_annee;
END //
DELIMITER ;

-- =============================================================================
-- FIN DU SCRIPT
-- =============================================================================

