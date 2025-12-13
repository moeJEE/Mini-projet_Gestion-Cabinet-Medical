package com.cabinetmedical.services;

import com.cabinetmedical.dao.PatientDAO;
import com.cabinetmedical.exceptions.BusinessException;
import com.cabinetmedical.exceptions.NotFoundException;
import com.cabinetmedical.exceptions.ValidationException;
import com.cabinetmedical.models.Patient;
import com.cabinetmedical.utils.ValidationHelper;

import java.util.List;

/**
 * Service pour la gestion des patients.
 * Contient la logique métier et les validations.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class PatientService {
    
    private final PatientDAO patientDAO;
    
    /**
     * Constructeur par défaut.
     */
    public PatientService() {
        this.patientDAO = new PatientDAO();
    }
    
    /**
     * Constructeur avec injection du DAO.
     * 
     * @param patientDAO DAO à utiliser
     */
    public PatientService(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }
    
    /**
     * Crée un nouveau patient.
     * 
     * @param patient Patient à créer
     * @return Patient créé avec son ID
     * @throws ValidationException si les données sont invalides
     * @throws BusinessException si un patient avec le même téléphone existe
     */
    public Patient createPatient(Patient patient) {
        // Validations
        validatePatient(patient);
        
        // Vérifier unicité du téléphone
        Patient existing = patientDAO.findByTelephone(patient.getTelephone());
        if (existing != null) {
            throw new BusinessException("Un patient avec ce numéro de téléphone existe déjà.");
        }
        
        // Vérifier unicité de l'email si fourni
        if (ValidationHelper.isNotEmpty(patient.getEmail())) {
            existing = patientDAO.findByEmail(patient.getEmail());
            if (existing != null) {
                throw new BusinessException("Un patient avec cet email existe déjà.");
            }
        }
        
        return patientDAO.create(patient);
    }
    
    /**
     * Récupère un patient par son ID.
     * 
     * @param id ID du patient
     * @return Patient trouvé
     * @throws NotFoundException si le patient n'existe pas
     */
    public Patient getPatientById(int id) {
        Patient patient = patientDAO.findById(id);
        if (patient == null) {
            throw new NotFoundException("Patient", id);
        }
        return patient;
    }
    
    /**
     * Récupère tous les patients.
     * 
     * @return Liste de tous les patients
     */
    public List<Patient> getAllPatients() {
        return patientDAO.findAll();
    }
    
    /**
     * Recherche des patients par critères.
     * 
     * @param criteria Critère de recherche
     * @return Liste des patients correspondants
     */
    public List<Patient> searchPatients(String criteria) {
        if (!ValidationHelper.isNotEmpty(criteria)) {
            return getAllPatients();
        }
        return patientDAO.search(criteria.trim());
    }
    
    /**
     * Met à jour un patient existant.
     * 
     * @param patient Patient avec les nouvelles valeurs
     * @return Patient mis à jour
     * @throws ValidationException si les données sont invalides
     * @throws NotFoundException si le patient n'existe pas
     * @throws BusinessException si un autre patient a le même téléphone/email
     */
    public Patient updatePatient(Patient patient) {
        // Vérifier que le patient existe
        Patient existing = patientDAO.findById(patient.getId());
        if (existing == null) {
            throw new NotFoundException("Patient", patient.getId());
        }
        
        // Validations
        validatePatient(patient);
        
        // Vérifier unicité du téléphone (sauf pour le patient lui-même)
        Patient byPhone = patientDAO.findByTelephone(patient.getTelephone());
        if (byPhone != null && byPhone.getId() != patient.getId()) {
            throw new BusinessException("Un autre patient a déjà ce numéro de téléphone.");
        }
        
        // Vérifier unicité de l'email si fourni (sauf pour le patient lui-même)
        if (ValidationHelper.isNotEmpty(patient.getEmail())) {
            Patient byEmail = patientDAO.findByEmail(patient.getEmail());
            if (byEmail != null && byEmail.getId() != patient.getId()) {
                throw new BusinessException("Un autre patient a déjà cet email.");
            }
        }
        
        return patientDAO.update(patient);
    }
    
    /**
     * Supprime un patient.
     * 
     * @param id ID du patient à supprimer
     * @return true si la suppression a réussi
     * @throws NotFoundException si le patient n'existe pas
     */
    public boolean deletePatient(int id) {
        Patient existing = patientDAO.findById(id);
        if (existing == null) {
            throw new NotFoundException("Patient", id);
        }
        
        return patientDAO.delete(id);
    }
    
    /**
     * Compte le nombre total de patients.
     * 
     * @return Nombre de patients
     */
    public int countPatients() {
        return patientDAO.count();
    }
    
    /**
     * Valide les données d'un patient.
     * 
     * @param patient Patient à valider
     * @throws ValidationException si les données sont invalides
     */
    private void validatePatient(Patient patient) {
        if (!ValidationHelper.isNotEmpty(patient.getNom())) {
            throw new ValidationException("nom", "Le nom est obligatoire.");
        }
        
        if (patient.getNom().length() > 100) {
            throw new ValidationException("nom", "Le nom ne peut pas dépasser 100 caractères.");
        }
        
        if (!ValidationHelper.isValidPhone(patient.getTelephone())) {
            throw new ValidationException("telephone", "Le numéro de téléphone doit contenir 10 chiffres.");
        }
        
        if (ValidationHelper.isNotEmpty(patient.getEmail()) && !ValidationHelper.isValidEmail(patient.getEmail())) {
            throw new ValidationException("email", "L'adresse email n'est pas valide.");
        }
    }
}

