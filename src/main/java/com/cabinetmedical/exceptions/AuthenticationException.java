package com.cabinetmedical.exceptions;

/**
 * Exception personnalisée pour les erreurs d'authentification.
 * Utilisée lors des échecs de connexion ou de vérification d'identité.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class AuthenticationException extends RuntimeException {
    
    /** Nombre de tentatives échouées */
    private int failedAttempts;
    
    /**
     * Constructeur avec message.
     * 
     * @param message Message d'erreur
     */
    public AuthenticationException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec message et nombre de tentatives.
     * 
     * @param message Message d'erreur
     * @param failedAttempts Nombre de tentatives échouées
     */
    public AuthenticationException(String message, int failedAttempts) {
        super(message);
        this.failedAttempts = failedAttempts;
    }
    
    /**
     * Constructeur avec message et cause.
     * 
     * @param message Message d'erreur
     * @param cause Exception originale
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Retourne le nombre de tentatives échouées.
     * 
     * @return Nombre de tentatives
     */
    public int getFailedAttempts() {
        return failedAttempts;
    }
}

