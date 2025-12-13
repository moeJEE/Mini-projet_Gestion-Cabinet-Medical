package com.cabinetmedical.controllers;

import com.cabinetmedical.exceptions.AuthenticationException;
import com.cabinetmedical.models.Utilisateur;
import com.cabinetmedical.services.UtilisateurService;

/**
 * Contrôleur pour l'authentification.
 * Fournit des méthodes prêtes à l'emploi pour les interfaces Swing.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class AuthController {
    
    private final UtilisateurService utilisateurService;
    
    /**
     * Constructeur par défaut.
     */
    public AuthController() {
        this.utilisateurService = new UtilisateurService();
    }
    
    /**
     * Authentifie un utilisateur.
     * 
     * Format de retour:
     * - En cas de succès: "SUCCESS:TYPE:ID" (ex: "SUCCESS:MEDECIN:1")
     * - En cas d'erreur: Message d'erreur
     * 
     * @param login Login de l'utilisateur
     * @param password Mot de passe
     * @return Résultat formaté pour l'interface
     */
    public String authenticate(String login, String password) {
        try {
            if (login == null || login.trim().isEmpty()) {
                return "Le login est requis";
            }
            if (password == null || password.isEmpty()) {
                return "Le mot de passe est requis";
            }
            
            Utilisateur utilisateur = utilisateurService.authenticate(login.trim(), password);
            
            // Retourner le résultat formaté: SUCCESS:TYPE:ID
            return "SUCCESS:" + utilisateur.getType().getValue() + ":" + utilisateur.getId();
            
        } catch (AuthenticationException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur de connexion: " + e.getMessage();
        }
    }
    
    /**
     * Vérifie si le résultat d'authentification est un succès.
     * 
     * @param result Résultat de authenticate()
     * @return true si succès
     */
    public boolean isAuthenticationSuccess(String result) {
        return result != null && result.startsWith("SUCCESS:");
    }
    
    /**
     * Extrait le type d'utilisateur du résultat d'authentification.
     * 
     * @param result Résultat de authenticate()
     * @return Type d'utilisateur (MEDECIN ou ASSISTANT)
     */
    public String getUserType(String result) {
        if (isAuthenticationSuccess(result)) {
            String[] parts = result.split(":");
            if (parts.length >= 2) {
                return parts[1];
            }
        }
        return null;
    }
    
    /**
     * Extrait l'ID utilisateur du résultat d'authentification.
     * 
     * @param result Résultat de authenticate()
     * @return ID de l'utilisateur ou -1
     */
    public int getUserId(String result) {
        if (isAuthenticationSuccess(result)) {
            String[] parts = result.split(":");
            if (parts.length >= 3) {
                try {
                    return Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }
    
    /**
     * Change le mot de passe d'un utilisateur.
     * 
     * @param userId ID de l'utilisateur
     * @param oldPassword Ancien mot de passe
     * @param newPassword Nouveau mot de passe
     * @param confirmPassword Confirmation du nouveau mot de passe
     * @return Message de résultat
     */
    public String changePassword(int userId, String oldPassword, String newPassword, String confirmPassword) {
        try {
            if (oldPassword == null || oldPassword.isEmpty()) {
                return "L'ancien mot de passe est requis";
            }
            if (newPassword == null || newPassword.isEmpty()) {
                return "Le nouveau mot de passe est requis";
            }
            if (newPassword.length() < 6) {
                return "Le nouveau mot de passe doit contenir au moins 6 caractères";
            }
            if (!newPassword.equals(confirmPassword)) {
                return "Les mots de passe ne correspondent pas";
            }
            
            boolean success = utilisateurService.changePassword(userId, oldPassword, newPassword);
            
            if (success) {
                return "Mot de passe modifié avec succès";
            } else {
                return "Erreur lors du changement de mot de passe";
            }
            
        } catch (AuthenticationException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
}

