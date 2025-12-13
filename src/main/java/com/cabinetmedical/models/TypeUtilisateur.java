package com.cabinetmedical.models;

/**
 * Énumération des types d'utilisateurs du système.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public enum TypeUtilisateur {
    
    /** Médecin - peut gérer consultations et voir son planning */
    MEDECIN("MEDECIN"),
    
    /** Assistant - peut gérer patients et paiements */
    ASSISTANT("ASSISTANT");
    
    private final String value;
    
    TypeUtilisateur(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Convertit une chaîne en TypeUtilisateur.
     * 
     * @param value La valeur à convertir
     * @return Le TypeUtilisateur correspondant
     * @throws IllegalArgumentException si la valeur est invalide
     */
    public static TypeUtilisateur fromString(String value) {
        for (TypeUtilisateur type : TypeUtilisateur.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type d'utilisateur inconnu: " + value);
    }
}

