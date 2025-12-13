package com.cabinetmedical.utils;

/**
 * Classe utilitaire pour les validations de données.
 * Fournit des méthodes statiques pour valider les entrées utilisateur.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class ValidationHelper {
    
    /** Expression régulière pour validation email */
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    /** Expression régulière pour numéro de téléphone marocain (10 chiffres) */
    private static final String PHONE_REGEX = "\\d{10}";
    
    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private ValidationHelper() {
        // Classe utilitaire
    }
    
    /**
     * Vérifie si une adresse email est valide.
     * 
     * @param email Adresse email à valider
     * @return true si l'email est valide
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }
    
    /**
     * Vérifie si un numéro de téléphone est valide (format marocain: 10 chiffres).
     * 
     * @param phone Numéro de téléphone à valider
     * @return true si le numéro est valide
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches(PHONE_REGEX);
    }
    
    /**
     * Vérifie si une chaîne n'est pas vide (non null et non whitespace).
     * 
     * @param str Chaîne à vérifier
     * @return true si la chaîne contient du texte
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Vérifie si un nombre est positif (> 0).
     * 
     * @param number Nombre à vérifier
     * @return true si le nombre est positif
     */
    public static boolean isPositiveNumber(double number) {
        return number > 0;
    }
    
    /**
     * Vérifie si un nombre est positif ou zéro (>= 0).
     * 
     * @param number Nombre à vérifier
     * @return true si le nombre est >= 0
     */
    public static boolean isNonNegative(double number) {
        return number >= 0;
    }
    
    /**
     * Vérifie si un entier est dans une plage donnée.
     * 
     * @param value Valeur à vérifier
     * @param min Minimum (inclus)
     * @param max Maximum (inclus)
     * @return true si la valeur est dans la plage
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * Vérifie si une chaîne respecte une longueur maximale.
     * 
     * @param str Chaîne à vérifier
     * @param maxLength Longueur maximale
     * @return true si la longueur est respectée
     */
    public static boolean hasMaxLength(String str, int maxLength) {
        return str == null || str.length() <= maxLength;
    }
    
    /**
     * Vérifie si une chaîne respecte une longueur minimale.
     * 
     * @param str Chaîne à vérifier
     * @param minLength Longueur minimale
     * @return true si la longueur est respectée
     */
    public static boolean hasMinLength(String str, int minLength) {
        return str != null && str.length() >= minLength;
    }
    
    /**
     * Nettoie une chaîne (trim et suppression des espaces multiples).
     * 
     * @param str Chaîne à nettoyer
     * @return Chaîne nettoyée ou null
     */
    public static String sanitize(String str) {
        if (str == null) {
            return null;
        }
        return str.trim().replaceAll("\\s+", " ");
    }
}

