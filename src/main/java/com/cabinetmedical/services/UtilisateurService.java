package com.cabinetmedical.services;

import com.cabinetmedical.dao.UtilisateurDAO;
import com.cabinetmedical.exceptions.AuthenticationException;
import com.cabinetmedical.exceptions.BusinessException;
import com.cabinetmedical.exceptions.NotFoundException;
import com.cabinetmedical.exceptions.ValidationException;
import com.cabinetmedical.models.*;
import com.cabinetmedical.utils.ValidationHelper;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

/**
 * Service pour la gestion des utilisateurs et l'authentification.
 * Gère les médecins et les assistants avec hashage des mots de passe.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class UtilisateurService {
    
    private final UtilisateurDAO utilisateurDAO;
    
    /** Nombre de rounds pour BCrypt */
    private static final int BCRYPT_ROUNDS = 10;
    
    /**
     * Constructeur par défaut.
     */
    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }
    
    /**
     * Constructeur avec injection du DAO.
     * 
     * @param utilisateurDAO DAO à utiliser
     */
    public UtilisateurService(UtilisateurDAO utilisateurDAO) {
        this.utilisateurDAO = utilisateurDAO;
    }
    
    /**
     * Authentifie un utilisateur.
     * 
     * @param login Login de l'utilisateur
     * @param password Mot de passe en clair
     * @return Utilisateur authentifié
     * @throws AuthenticationException si l'authentification échoue
     */
    public Utilisateur authenticate(String login, String password) {
        if (!ValidationHelper.isNotEmpty(login) || !ValidationHelper.isNotEmpty(password)) {
            throw new AuthenticationException("Login et mot de passe requis.");
        }
        
        Utilisateur utilisateur = utilisateurDAO.findByLogin(login);
        
        if (utilisateur == null) {
            throw new AuthenticationException("Login ou mot de passe incorrect.");
        }
        
        if (!BCrypt.checkpw(password, utilisateur.getPassword())) {
            throw new AuthenticationException("Login ou mot de passe incorrect.");
        }
        
        return utilisateur;
    }
    
    /**
     * Hashe un mot de passe avec BCrypt.
     * 
     * @param password Mot de passe en clair
     * @return Mot de passe hashé
     */
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
    
    /**
     * Crée un nouvel utilisateur.
     * 
     * @param utilisateur Utilisateur à créer (mot de passe en clair)
     * @return Utilisateur créé
     * @throws ValidationException si les données sont invalides
     * @throws BusinessException si le login existe déjà
     */
    public Utilisateur createUser(Utilisateur utilisateur) {
        validateUtilisateur(utilisateur);
        
        if (utilisateurDAO.loginExists(utilisateur.getLogin())) {
            throw new BusinessException("Ce login est déjà utilisé.");
        }
        
        // Hasher le mot de passe
        utilisateur.setPassword(hashPassword(utilisateur.getPassword()));
        
        return utilisateurDAO.create(utilisateur);
    }
    
    /**
     * Crée un nouveau médecin.
     * 
     * @param login Login
     * @param password Mot de passe en clair
     * @return Médecin créé
     */
    public Medecin createMedecin(String login, String password) {
        Medecin medecin = new Medecin(login, password);
        return (Medecin) createUser(medecin);
    }
    
    /**
     * Crée un nouvel assistant.
     * 
     * @param login Login
     * @param password Mot de passe en clair
     * @return Assistant créé
     */
    public Assistant createAssistant(String login, String password) {
        Assistant assistant = new Assistant(login, password);
        return (Assistant) createUser(assistant);
    }
    
    /**
     * Récupère un utilisateur par son ID.
     * 
     * @param id ID de l'utilisateur
     * @return Utilisateur trouvé
     * @throws NotFoundException si l'utilisateur n'existe pas
     */
    public Utilisateur getUserById(int id) {
        Utilisateur utilisateur = utilisateurDAO.findById(id);
        if (utilisateur == null) {
            throw new NotFoundException("Utilisateur", id);
        }
        return utilisateur;
    }
    
    /**
     * Récupère tous les utilisateurs.
     * 
     * @return Liste de tous les utilisateurs
     */
    public List<Utilisateur> getAllUsers() {
        return utilisateurDAO.findAll();
    }
    
    /**
     * Récupère tous les médecins.
     * 
     * @return Liste des médecins
     */
    public List<Medecin> getAllMedecins() {
        return utilisateurDAO.findAllMedecins();
    }
    
    /**
     * Récupère tous les assistants.
     * 
     * @return Liste des assistants
     */
    public List<Assistant> getAllAssistants() {
        return utilisateurDAO.findAllAssistants();
    }
    
    /**
     * Met à jour un utilisateur.
     * 
     * @param utilisateur Utilisateur avec les nouvelles valeurs
     * @param newPassword Nouveau mot de passe (null si inchangé)
     * @return Utilisateur mis à jour
     * @throws NotFoundException si l'utilisateur n'existe pas
     * @throws BusinessException si le nouveau login existe déjà
     */
    public Utilisateur updateUser(Utilisateur utilisateur, String newPassword) {
        Utilisateur existing = utilisateurDAO.findById(utilisateur.getId());
        if (existing == null) {
            throw new NotFoundException("Utilisateur", utilisateur.getId());
        }
        
        // Vérifier le login si modifié
        if (!existing.getLogin().equals(utilisateur.getLogin())) {
            if (utilisateurDAO.loginExists(utilisateur.getLogin())) {
                throw new BusinessException("Ce login est déjà utilisé.");
            }
        }
        
        // Mettre à jour le mot de passe si fourni
        if (ValidationHelper.isNotEmpty(newPassword)) {
            validatePassword(newPassword);
            utilisateur.setPassword(hashPassword(newPassword));
        } else {
            // Garder l'ancien mot de passe
            utilisateur.setPassword(existing.getPassword());
        }
        
        return utilisateurDAO.update(utilisateur);
    }
    
    /**
     * Change le mot de passe d'un utilisateur.
     * 
     * @param userId ID de l'utilisateur
     * @param oldPassword Ancien mot de passe
     * @param newPassword Nouveau mot de passe
     * @return true si le changement a réussi
     * @throws AuthenticationException si l'ancien mot de passe est incorrect
     * @throws ValidationException si le nouveau mot de passe est invalide
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        Utilisateur utilisateur = getUserById(userId);
        
        // Vérifier l'ancien mot de passe
        if (!BCrypt.checkpw(oldPassword, utilisateur.getPassword())) {
            throw new AuthenticationException("Ancien mot de passe incorrect.");
        }
        
        validatePassword(newPassword);
        
        String hashedPassword = hashPassword(newPassword);
        return utilisateurDAO.updatePassword(userId, hashedPassword);
    }
    
    /**
     * Supprime un utilisateur.
     * 
     * @param id ID de l'utilisateur
     * @return true si la suppression a réussi
     * @throws NotFoundException si l'utilisateur n'existe pas
     */
    public boolean deleteUser(int id) {
        Utilisateur existing = utilisateurDAO.findById(id);
        if (existing == null) {
            throw new NotFoundException("Utilisateur", id);
        }
        
        return utilisateurDAO.delete(id);
    }
    
    /**
     * Valide les données d'un utilisateur.
     * 
     * @param utilisateur Utilisateur à valider
     * @throws ValidationException si les données sont invalides
     */
    private void validateUtilisateur(Utilisateur utilisateur) {
        if (!ValidationHelper.isNotEmpty(utilisateur.getLogin())) {
            throw new ValidationException("login", "Le login est obligatoire.");
        }
        
        if (utilisateur.getLogin().length() < 3 || utilisateur.getLogin().length() > 50) {
            throw new ValidationException("login", "Le login doit contenir entre 3 et 50 caractères.");
        }
        
        validatePassword(utilisateur.getPassword());
        
        if (utilisateur.getType() == null) {
            throw new ValidationException("type", "Le type d'utilisateur est obligatoire.");
        }
    }
    
    /**
     * Valide un mot de passe.
     * 
     * @param password Mot de passe à valider
     * @throws ValidationException si le mot de passe est invalide
     */
    private void validatePassword(String password) {
        if (!ValidationHelper.isNotEmpty(password)) {
            throw new ValidationException("password", "Le mot de passe est obligatoire.");
        }
        
        if (password.length() < 6) {
            throw new ValidationException("password", "Le mot de passe doit contenir au moins 6 caractères.");
        }
    }
}

