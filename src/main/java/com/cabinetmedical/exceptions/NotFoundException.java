package com.cabinetmedical.exceptions;

/**
 * Exception personnalisée quand une entité n'est pas trouvée.
 * Utilisée lors des recherches par ID ou critères sans résultat.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class NotFoundException extends RuntimeException {
    
    /** Type d'entité non trouvée */
    private String entityType;
    
    /** Identifiant recherché */
    private Object entityId;
    
    /**
     * Constructeur avec message.
     * 
     * @param message Message d'erreur
     */
    public NotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec type d'entité et id.
     * 
     * @param entityType Type d'entité (ex: "Patient", "Consultation")
     * @param entityId Identifiant recherché
     */
    public NotFoundException(String entityType, Object entityId) {
        super(entityType + " avec l'id " + entityId + " non trouvé(e)");
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    /**
     * Constructeur avec message et cause.
     * 
     * @param message Message d'erreur
     * @param cause Exception originale
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Retourne le type d'entité non trouvée.
     * 
     * @return Type d'entité
     */
    public String getEntityType() {
        return entityType;
    }
    
    /**
     * Retourne l'identifiant recherché.
     * 
     * @return Identifiant
     */
    public Object getEntityId() {
        return entityId;
    }
}

