package com.cabinetmedical.exceptions;

/**
 * Exception personnalisée pour les violations des règles métier.
 * Utilisée quand une opération viole les contraintes métier du cabinet.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class BusinessException extends RuntimeException {
    
    /** Code d'erreur métier */
    private String errorCode;
    
    /**
     * Constructeur avec message.
     * 
     * @param message Message d'erreur
     */
    public BusinessException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec code et message.
     * 
     * @param errorCode Code d'erreur
     * @param message Message d'erreur
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Constructeur avec message et cause.
     * 
     * @param message Message d'erreur
     * @param cause Exception originale
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Retourne le code d'erreur métier.
     * 
     * @return Code d'erreur
     */
    public String getErrorCode() {
        return errorCode;
    }
}

