package com.cabinetmedical.services;

import com.cabinetmedical.dao.ConsultationDAO;
import com.cabinetmedical.dao.PatientDAO;
import com.cabinetmedical.dao.CategorieDAO;
import com.cabinetmedical.dao.UtilisateurDAO;
import com.cabinetmedical.exceptions.BusinessException;
import com.cabinetmedical.exceptions.NotFoundException;
import com.cabinetmedical.exceptions.ValidationException;
import com.cabinetmedical.models.Consultation;
import com.cabinetmedical.utils.ValidationHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service pour la gestion des consultations et rendez-vous.
 * Contient la logique métier et les validations.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class ConsultationService {
    
    private final ConsultationDAO consultationDAO;
    private final PatientDAO patientDAO;
    private final CategorieDAO categorieDAO;
    private final UtilisateurDAO utilisateurDAO;
    
    /**
     * Constructeur par défaut.
     */
    public ConsultationService() {
        this.consultationDAO = new ConsultationDAO();
        this.patientDAO = new PatientDAO();
        this.categorieDAO = new CategorieDAO();
        this.utilisateurDAO = new UtilisateurDAO();
    }
    
    /**
     * Constructeur avec injection des DAOs.
     * 
     * @param consultationDAO DAO des consultations
     * @param patientDAO DAO des patients
     * @param categorieDAO DAO des catégories
     * @param utilisateurDAO DAO des utilisateurs
     */
    public ConsultationService(ConsultationDAO consultationDAO, PatientDAO patientDAO,
                               CategorieDAO categorieDAO, UtilisateurDAO utilisateurDAO) {
        this.consultationDAO = consultationDAO;
        this.patientDAO = patientDAO;
        this.categorieDAO = categorieDAO;
        this.utilisateurDAO = utilisateurDAO;
    }
    
    /**
     * Crée une nouvelle consultation.
     * 
     * @param consultation Consultation à créer
     * @return Consultation créée
     * @throws ValidationException si les données sont invalides
     * @throws NotFoundException si patient/catégorie/médecin n'existent pas
     * @throws BusinessException si règle métier violée
     */
    public Consultation createConsultation(Consultation consultation) {
        validateConsultation(consultation);
        
        // Vérifier que les entités liées existent
        if (patientDAO.findById(consultation.getPatientId()) == null) {
            throw new NotFoundException("Patient", consultation.getPatientId());
        }
        if (categorieDAO.findById(consultation.getCategorieId()) == null) {
            throw new NotFoundException("Catégorie", consultation.getCategorieId());
        }
        if (utilisateurDAO.findById(consultation.getMedecinId()) == null) {
            throw new NotFoundException("Médecin", consultation.getMedecinId());
        }
        
        // Vérifier que le créneau est disponible pour le patient
        if (!consultationDAO.isCreneauDisponible(consultation.getPatientId(), consultation.getDate())) {
            throw new BusinessException("Le patient a déjà une consultation à cette heure.");
        }
        
        // Par défaut, la consultation n'est pas payée
        consultation.setEstPayee(false);
        
        return consultationDAO.create(consultation);
    }
    
    /**
     * Récupère une consultation par son ID.
     * 
     * @param id ID de la consultation
     * @return Consultation trouvée
     * @throws NotFoundException si la consultation n'existe pas
     */
    public Consultation getConsultationById(int id) {
        Consultation consultation = consultationDAO.findById(id);
        if (consultation == null) {
            throw new NotFoundException("Consultation", id);
        }
        return consultation;
    }
    
    /**
     * Récupère toutes les consultations.
     * 
     * @return Liste de toutes les consultations
     */
    public List<Consultation> getAllConsultations() {
        return consultationDAO.findAll();
    }
    
    /**
     * Récupère les consultations d'une date spécifique.
     * 
     * @param date Date des consultations
     * @return Liste des consultations
     */
    public List<Consultation> getConsultationsByDate(LocalDate date) {
        return consultationDAO.findByDate(date);
    }
    
    /**
     * Récupère les consultations journalières d'un médecin.
     * 
     * @param medecinId ID du médecin
     * @param date Date des consultations
     * @return Liste des consultations
     */
    public List<Consultation> getConsultationsJournalieres(int medecinId, LocalDate date) {
        return consultationDAO.findByMedecinAndDate(medecinId, date);
    }
    
    /**
     * Récupère toutes les consultations futures et d'aujourd'hui pour un médecin.
     * 
     * @param medecinId ID du médecin
     * @return Liste des consultations à venir
     */
    public List<Consultation> getConsultationsFuturesByMedecin(int medecinId) {
        return consultationDAO.findByMedecinId(medecinId).stream()
            .filter(c -> !c.getDate().toLocalDate().isBefore(LocalDate.now()))
            .sorted((c1, c2) -> c1.getDate().compareTo(c2.getDate()))
            .toList();
    }
    
    /**
     * Récupère les consultations d'un patient.
     * 
     * @param patientId ID du patient
     * @return Liste des consultations
     */
    public List<Consultation> getConsultationsByPatient(int patientId) {
        return consultationDAO.findByPatient(patientId);
    }
    
    /**
     * Compte le nombre de consultations pour une date donnée (tous médecins confondus).
     * 
     * @param date Date à vérifier
     * @return Nombre de consultations
     */
    public int countConsultationsByDate(LocalDate date) {
        return consultationDAO.findByDate(date).size();
    }
    
    /**
     * Met à jour une consultation.
     * 
     * @param consultation Consultation avec les nouvelles valeurs
     * @return Consultation mise à jour
     * @throws NotFoundException si la consultation n'existe pas
     * @throws BusinessException si la consultation est passée
     */
    public Consultation updateConsultation(Consultation consultation) {
        Consultation existing = consultationDAO.findById(consultation.getId());
        if (existing == null) {
            throw new NotFoundException("Consultation", consultation.getId());
        }
        
        // On ne peut pas modifier une consultation passée
        if (existing.isPassee()) {
            throw new BusinessException("Impossible de modifier une consultation passée.");
        }
        
        validateConsultation(consultation);
        
        return consultationDAO.update(consultation);
    }
    
    /**
     * Annule (supprime) une consultation.
     * 
     * @param id ID de la consultation
     * @return true si l'annulation a réussi
     * @throws NotFoundException si la consultation n'existe pas
     * @throws BusinessException si la consultation est passée
     */
    public boolean cancelConsultation(int id) {
        Consultation existing = consultationDAO.findById(id);
        if (existing == null) {
            throw new NotFoundException("Consultation", id);
        }
        
        // On ne peut pas annuler une consultation passée
        if (existing.isPassee()) {
            throw new BusinessException("Impossible d'annuler une consultation passée.");
        }
        
        return consultationDAO.delete(id);
    }
    
    /**
     * Valide le paiement d'une consultation.
     * 
     * @param consultationId ID de la consultation
     * @return true si la validation a réussi
     * @throws NotFoundException si la consultation n'existe pas
     * @throws BusinessException si déjà payée
     */
    public boolean validerPaiement(int consultationId) {
        Consultation consultation = consultationDAO.findById(consultationId);
        if (consultation == null) {
            throw new NotFoundException("Consultation", consultationId);
        }
        
        if (consultation.isEstPayee()) {
            throw new BusinessException("Cette consultation est déjà payée.");
        }
        
        return consultationDAO.validerPaiement(consultationId);
    }
    
    /**
     * Récupère les consultations non payées.
     * 
     * @return Liste des consultations non payées
     */
    public List<Consultation> getConsultationsNonPayees() {
        return consultationDAO.findNonPayees();
    }
    
    /**
     * Récupère les patients à relancer (RDV J-1 ou J-2).
     * 
     * @param date Date de référence (aujourd'hui)
     * @return Liste des consultations à relancer
     */
    public List<Consultation> getPatientsARelancer(LocalDate date) {
        return consultationDAO.findForRelance(date);
    }
    
    /**
     * Récupère les consultations d'un mois.
     * 
     * @param mois Mois (1-12)
     * @param annee Année
     * @return Liste des consultations
     */
    public List<Consultation> getConsultationsByMois(int mois, int annee) {
        return consultationDAO.findByMois(mois, annee);
    }
    
    /**
     * Valide les données d'une consultation.
     * 
     * @param consultation Consultation à valider
     * @throws ValidationException si les données sont invalides
     */
    private void validateConsultation(Consultation consultation) {
        if (consultation.getDate() == null) {
            throw new ValidationException("date", "La date est obligatoire.");
        }
        
        // La date ne peut pas être dans le passé (pour une nouvelle consultation)
        if (consultation.getId() == 0 && consultation.getDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("date", "La date ne peut pas être dans le passé.");
        }
        
        if (!ValidationHelper.isPositiveNumber(consultation.getPrixConsultation())) {
            throw new ValidationException("prix", "Le prix doit être supérieur à 0.");
        }
        
        if (consultation.getPatientId() <= 0) {
            throw new ValidationException("patient", "Le patient est obligatoire.");
        }
        
        if (consultation.getCategorieId() <= 0) {
            throw new ValidationException("categorie", "La catégorie est obligatoire.");
        }
        
        if (consultation.getMedecinId() <= 0) {
            throw new ValidationException("medecin", "Le médecin est obligatoire.");
        }
    }
}

