package com.cabinetmedical.controllers;

import com.cabinetmedical.exceptions.BusinessException;
import com.cabinetmedical.exceptions.NotFoundException;
import com.cabinetmedical.exceptions.ValidationException;
import com.cabinetmedical.models.*;
import com.cabinetmedical.services.UtilisateurService;

import java.util.List;

/**
 * Contrôleur pour la gestion des utilisateurs (médecins et assistants).
 * Fournit des méthodes formatées pour les composants Swing.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class UtilisateurController {
    
    private final UtilisateurService utilisateurService;
    
    /**
     * Constructeur par défaut.
     */
    public UtilisateurController() {
        this.utilisateurService = new UtilisateurService();
    }
    
    /**
     * Récupère tous les utilisateurs formatés pour JTable.
     * 
     * @return Tableau 2D [ID, Login, Type]
     */
    public Object[][] getUtilisateursForTable() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllUsers();
        Object[][] data = new Object[utilisateurs.size()][3];
        
        for (int i = 0; i < utilisateurs.size(); i++) {
            Utilisateur u = utilisateurs.get(i);
            data[i][0] = u.getId();
            data[i][1] = u.getLogin();
            data[i][2] = u.getType().getValue();
        }
        
        return data;
    }
    
    /**
     * Retourne les colonnes pour la table des utilisateurs.
     * 
     * @return Tableau des noms de colonnes
     */
    public String[] getUtilisateurTableColumns() {
        return new String[]{"ID", "Login", "Type"};
    }
    
    /**
     * Récupère les médecins formatés pour JComboBox.
     * 
     * @return Tableau "ID - Login"
     */
    public String[] getMedecinsForComboBox() {
        List<Medecin> medecins = utilisateurService.getAllMedecins();
        return medecins.stream()
                .map(m -> m.getId() + " - " + m.getLogin())
                .toArray(String[]::new);
    }
    
    /**
     * Récupère les types d'utilisateurs pour JComboBox.
     * 
     * @return Tableau des types
     */
    public String[] getTypesForComboBox() {
        return new String[]{"MEDECIN", "ASSISTANT"};
    }
    
    /**
     * Crée un nouvel utilisateur.
     * 
     * @param login Login
     * @param password Mot de passe
     * @param type Type (MEDECIN ou ASSISTANT)
     * @return Message de résultat
     */
    public String createUtilisateur(String login, String password, String type) {
        try {
            if (login == null || login.trim().isEmpty()) {
                return "Le login est requis";
            }
            if (password == null || password.isEmpty()) {
                return "Le mot de passe est requis";
            }
            if (password.length() < 6) {
                return "Le mot de passe doit contenir au moins 6 caractères";
            }
            if (type == null || type.isEmpty()) {
                return "Le type d'utilisateur est requis";
            }
            
            Utilisateur utilisateur;
            if ("MEDECIN".equalsIgnoreCase(type)) {
                utilisateur = new Medecin(login.trim(), password);
            } else if ("ASSISTANT".equalsIgnoreCase(type)) {
                utilisateur = new Assistant(login.trim(), password);
            } else {
                return "Type d'utilisateur invalide";
            }
            
            utilisateurService.createUser(utilisateur);
            return "Utilisateur créé avec succès";
            
        } catch (ValidationException | BusinessException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
    
    /**
     * Met à jour un utilisateur.
     * 
     * @param id ID de l'utilisateur
     * @param login Nouveau login
     * @param newPassword Nouveau mot de passe (null si inchangé)
     * @param type Type d'utilisateur
     * @return Message de résultat
     */
    public String updateUtilisateur(int id, String login, String newPassword, String type) {
        try {
            if (login == null || login.trim().isEmpty()) {
                return "Le login est requis";
            }
            if (newPassword != null && !newPassword.isEmpty() && newPassword.length() < 6) {
                return "Le mot de passe doit contenir au moins 6 caractères";
            }
            
            Utilisateur utilisateur;
            TypeUtilisateur typeUtil = TypeUtilisateur.fromString(type);
            
            if (typeUtil == TypeUtilisateur.MEDECIN) {
                utilisateur = new Medecin(id, login.trim(), "");
            } else {
                utilisateur = new Assistant(id, login.trim(), "");
            }
            
            String passwordToUpdate = (newPassword != null && !newPassword.isEmpty()) ? newPassword : null;
            utilisateurService.updateUser(utilisateur, passwordToUpdate);
            return "Utilisateur modifié avec succès";
            
        } catch (ValidationException | BusinessException | NotFoundException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
    
    /**
     * Supprime un utilisateur.
     * 
     * @param id ID de l'utilisateur
     * @return Message de résultat
     */
    public String deleteUtilisateur(int id) {
        try {
            utilisateurService.deleteUser(id);
            return "Utilisateur supprimé avec succès";
        } catch (NotFoundException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
    
    /**
     * Récupère les données d'un utilisateur pour un formulaire.
     * 
     * @param id ID de l'utilisateur
     * @return Tableau [login, type] ou null
     */
    public String[] getUtilisateurFormData(int id) {
        try {
            Utilisateur u = utilisateurService.getUserById(id);
            return new String[] {
                u.getLogin(),
                u.getType().getValue()
            };
        } catch (NotFoundException e) {
            return null;
        }
    }
}

