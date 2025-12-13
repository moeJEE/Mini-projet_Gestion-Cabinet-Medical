package com.cabinetmedical.exceptions;

/**
 * Exception personnalisée pour les erreurs de validation.
 * Utilisée quand les données d'entrée ne respectent pas les contraintes.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class ValidationException extends RuntimeException {
    
    /** Nom du champ en erreur */
    private String fieldName;
    
    /**
     * Constructeur avec message.
     * 
     * @param message Message d'erreur
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec nom de champ et message.
     * 
     * @param fieldName Nom du champ en erreur
     * @param message Message d'erreur
     */
    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }
    
    /**
     * Constructeur avec message et cause.
     * 
     * @param message Message d'erreur
     * @param cause Exception originale
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Retourne le nom du champ en erreur.
     * 
     * @return Nom du champ
     */
    public String getFieldName() {
        return fieldName;
    }
}

