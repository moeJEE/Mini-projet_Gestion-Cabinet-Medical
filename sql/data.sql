-- =============================================================================
-- CABINET MEDICAL - Données de test
-- =============================================================================
-- Ce script insère des données de test pour le développement et les tests.
-- Les mots de passe sont hashés avec BCrypt.
-- =============================================================================

USE cabinet_medical;

-- =============================================================================
-- UTILISATEURS
-- Mots de passe en clair pour les tests:
--   - Tous les utilisateurs: admin123
-- =============================================================================
INSERT INTO utilisateur (login, password, type) VALUES
-- Médecins (password: admin123 hashé avec BCrypt)
('dr.alami', '$2a$10$NLVmcrq8OKLZ97b5CT6Vk.d6s02/KSW8TQtsYx6PHxxGEgBSrF6Gy', 'MEDECIN'),
('dr.benkirane', '$2a$10$NLVmcrq8OKLZ97b5CT6Vk.d6s02/KSW8TQtsYx6PHxxGEgBSrF6Gy', 'MEDECIN'),
-- Assistants (password: admin123 hashé avec BCrypt)
('assistant1', '$2a$10$NLVmcrq8OKLZ97b5CT6Vk.d6s02/KSW8TQtsYx6PHxxGEgBSrF6Gy', 'ASSISTANT'),
('assistant2', '$2a$10$NLVmcrq8OKLZ97b5CT6Vk.d6s02/KSW8TQtsYx6PHxxGEgBSrF6Gy', 'ASSISTANT');

-- =============================================================================
-- CATÉGORIES DE CONSULTATION
-- =============================================================================
INSERT INTO categorie (designation, description) VALUES
('Consultation normale', 'Première consultation ou consultation standard'),
('Contrôle', 'Visite de suivi après traitement'),
('Urgence', 'Consultation urgente sans rendez-vous'),
('Visite à domicile', 'Consultation au domicile du patient'),
('Consultation spécialisée', 'Consultation nécessitant des examens spécifiques');

-- =============================================================================
-- PATIENTS
-- =============================================================================
INSERT INTO patient (nom, telephone, email) VALUES
('Ahmed Bennani', '0612345678', 'ahmed.bennani@email.com'),
('Fatima Idrissi', '0623456789', 'fatima.idrissi@email.com'),
('Mohammed Alaoui', '0634567890', 'mohammed.alaoui@email.com'),
('Sanae Tazi', '0645678901', 'sanae.tazi@email.com'),
('Youssef Amrani', '0656789012', 'youssef.amrani@email.com'),
('Khadija Fassi', '0667890123', 'khadija.fassi@email.com'),
('Omar Benjelloun', '0678901234', 'omar.benjelloun@email.com'),
('Aicha Lahlou', '0689012345', 'aicha.lahlou@email.com'),
('Rachid Moussaoui', '0690123456', 'rachid.moussaoui@email.com'),
('Nadia Chraibi', '0601234567', 'nadia.chraibi@email.com');

-- =============================================================================
-- CONSULTATIONS
-- Consultations pour le mois en cours et le mois précédent
-- =============================================================================

-- Consultations passées (mois dernier)
INSERT INTO consultation (date, description, prix_consultation, patient_id, categorie_id, medecin_id, est_payee) VALUES
-- Semaine 1 du mois dernier
(DATE_SUB(CURDATE(), INTERVAL 35 DAY) + INTERVAL 9 HOUR, 'Grippe saisonnière', 200.00, 1, 1, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 35 DAY) + INTERVAL 10 HOUR + INTERVAL 30 MINUTE, 'Contrôle tension', 150.00, 2, 2, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 34 DAY) + INTERVAL 14 HOUR, 'Douleurs lombaires', 200.00, 3, 1, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 33 DAY) + INTERVAL 11 HOUR, 'Angine', 200.00, 4, 1, 1, FALSE),

-- Semaine 2 du mois dernier
(DATE_SUB(CURDATE(), INTERVAL 28 DAY) + INTERVAL 9 HOUR, 'Consultation générale', 200.00, 5, 1, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 28 DAY) + INTERVAL 15 HOUR, 'Contrôle diabète', 150.00, 6, 2, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 27 DAY) + INTERVAL 10 HOUR, 'Migraine chronique', 250.00, 7, 5, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 26 DAY) + INTERVAL 16 HOUR, 'Visite domicile - mobilité réduite', 300.00, 8, 4, 1, FALSE),

-- Semaine 3 du mois dernier
(DATE_SUB(CURDATE(), INTERVAL 21 DAY) + INTERVAL 9 HOUR + INTERVAL 30 MINUTE, 'Rhume', 200.00, 9, 1, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 21 DAY) + INTERVAL 11 HOUR, 'Suivi grossesse', 200.00, 10, 2, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 20 DAY) + INTERVAL 14 HOUR + INTERVAL 30 MINUTE, 'Allergie cutanée', 200.00, 1, 1, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 19 DAY) + INTERVAL 10 HOUR, 'Contrôle post-opératoire', 150.00, 2, 2, 1, TRUE),

-- Semaine 4 du mois dernier
(DATE_SUB(CURDATE(), INTERVAL 14 DAY) + INTERVAL 9 HOUR, 'Gastro-entérite', 200.00, 3, 1, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 14 DAY) + INTERVAL 16 HOUR, 'Urgence - fièvre élevée', 250.00, 4, 3, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 13 DAY) + INTERVAL 11 HOUR + INTERVAL 30 MINUTE, 'Bilan annuel', 200.00, 5, 1, 1, FALSE),
(DATE_SUB(CURDATE(), INTERVAL 12 DAY) + INTERVAL 14 HOUR, 'Contrôle tension', 150.00, 6, 2, 1, TRUE);

-- Consultations récentes (cette semaine)
INSERT INTO consultation (date, description, prix_consultation, patient_id, categorie_id, medecin_id, est_payee) VALUES
(DATE_SUB(CURDATE(), INTERVAL 5 DAY) + INTERVAL 9 HOUR, 'Consultation générale', 200.00, 7, 1, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 5 DAY) + INTERVAL 10 HOUR + INTERVAL 30 MINUTE, 'Douleurs articulaires', 200.00, 8, 1, 1, FALSE),
(DATE_SUB(CURDATE(), INTERVAL 4 DAY) + INTERVAL 14 HOUR, 'Contrôle diabète', 150.00, 9, 2, 1, TRUE),
(DATE_SUB(CURDATE(), INTERVAL 3 DAY) + INTERVAL 11 HOUR, 'Fatigue chronique', 250.00, 10, 5, 1, FALSE),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY) + INTERVAL 9 HOUR + INTERVAL 30 MINUTE, 'Grippe', 200.00, 1, 1, 1, TRUE);

-- Consultations du jour
INSERT INTO consultation (date, description, prix_consultation, patient_id, categorie_id, medecin_id, est_payee) VALUES
(CURDATE() + INTERVAL 9 HOUR, 'Consultation matinale', 200.00, 2, 1, 1, FALSE),
(CURDATE() + INTERVAL 10 HOUR + INTERVAL 30 MINUTE, 'Contrôle tension', 150.00, 3, 2, 1, FALSE),
(CURDATE() + INTERVAL 14 HOUR, 'Consultation après-midi', 200.00, 4, 1, 1, FALSE),
(CURDATE() + INTERVAL 15 HOUR + INTERVAL 30 MINUTE, 'Suivi traitement', 150.00, 5, 2, 1, FALSE);

-- Consultations futures (J+1 et J+2 pour test relance)
INSERT INTO consultation (date, description, prix_consultation, patient_id, categorie_id, medecin_id, est_payee) VALUES
(DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 9 HOUR, 'RDV demain matin', 200.00, 6, 1, 1, FALSE),
(DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 14 HOUR, 'RDV demain après-midi', 200.00, 7, 1, 1, FALSE),
(DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 10 HOUR, 'RDV après-demain', 200.00, 8, 1, 1, FALSE),
(DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 15 HOUR, 'Contrôle dans 2 jours', 150.00, 9, 2, 1, FALSE);

-- Consultations futures (semaine prochaine)
INSERT INTO consultation (date, description, prix_consultation, patient_id, categorie_id, medecin_id, est_payee) VALUES
(DATE_ADD(CURDATE(), INTERVAL 7 DAY) + INTERVAL 9 HOUR, 'RDV semaine prochaine', 200.00, 10, 1, 1, FALSE),
(DATE_ADD(CURDATE(), INTERVAL 7 DAY) + INTERVAL 11 HOUR, 'Consultation spécialisée', 250.00, 1, 5, 1, FALSE),
(DATE_ADD(CURDATE(), INTERVAL 8 DAY) + INTERVAL 14 HOUR + INTERVAL 30 MINUTE, 'Visite domicile programmée', 300.00, 2, 4, 1, FALSE);

-- =============================================================================
-- FIN DU SCRIPT DE DONNÉES
-- =============================================================================

-- Afficher un résumé des données insérées
SELECT 'Utilisateurs' AS Entite, COUNT(*) AS Nombre FROM utilisateur
UNION ALL
SELECT 'Patients', COUNT(*) FROM patient
UNION ALL
SELECT 'Catégories', COUNT(*) FROM categorie
UNION ALL
SELECT 'Consultations', COUNT(*) FROM consultation;

