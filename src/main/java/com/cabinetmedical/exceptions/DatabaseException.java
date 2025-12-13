package com.cabinetmedical.exceptions;

/**
 * Exception personnalisée pour les erreurs liées à la base de données.
 * Encapsule les exceptions JDBC pour une meilleure gestion des erreurs.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class DatabaseException extends RuntimeException {
    
    /**
     * Constructeur avec message.
     * 
     * @param message Message d'erreur
     */
    public DatabaseException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec message et cause.
     * 
     * @param message Message d'erreur
     * @param cause Exception originale
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructeur avec cause uniquement.
     * 
     * @param cause Exception originale
     */
    public DatabaseException(Throwable cause) {
        super(cause);
    }
}

