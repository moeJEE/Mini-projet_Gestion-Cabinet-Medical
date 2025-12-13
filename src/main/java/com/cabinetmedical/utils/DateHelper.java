package com.cabinetmedical.utils;

import com.cabinetmedical.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Classe utilitaire pour la manipulation et le formatage des dates.
 * Fournit des méthodes pour convertir et formater les dates pour l'interface Swing.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class DateHelper {
    
    /** Format de date standard (dd/MM/yyyy) */
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    
    /** Format d'heure standard (HH:mm) */
    public static final String TIME_FORMAT = "HH:mm";
    
    /** Format date et heure complet (dd/MM/yyyy HH:mm) */
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm";
    
    /** Formatter pour les dates */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    
    /** Formatter pour les heures */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    
    /** Formatter pour date et heure */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
    
    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private DateHelper() {
        // Classe utilitaire
    }
    
    /**
     * Formate un LocalDateTime pour affichage complet (date et heure).
     * 
     * @param dateTime Date et heure à formater
     * @return Chaîne formatée (dd/MM/yyyy HH:mm)
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * Formate un LocalDateTime pour afficher uniquement l'heure.
     * 
     * @param dateTime Date et heure
     * @return Chaîne formatée (HH:mm)
     */
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(TIME_FORMATTER);
    }
    
    /**
     * Formate un LocalDate pour affichage.
     * 
     * @param date Date à formater
     * @return Chaîne formatée (dd/MM/yyyy)
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Parse une date et heure depuis des chaînes séparées.
     * 
     * @param dateStr Chaîne de date (format: dd/MM/yyyy)
     * @param timeStr Chaîne d'heure (format: HH:mm)
     * @return LocalDateTime parsé
     * @throws ValidationException si le format est invalide
     */
    public static LocalDateTime parseDateTime(String dateStr, String timeStr) throws ValidationException {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) {
                throw new ValidationException("La date est obligatoire.");
            }
            if (timeStr == null || timeStr.trim().isEmpty()) {
                throw new ValidationException("L'heure est obligatoire.");
            }
            
            String combined = dateStr.trim() + " " + timeStr.trim();
            return LocalDateTime.parse(combined, DATETIME_FORMATTER);
            
        } catch (DateTimeParseException e) {
            throw new ValidationException("Format de date/heure invalide. Utilisez dd/MM/yyyy HH:mm");
        }
    }
    
    /**
     * Parse une date depuis une chaîne.
     * 
     * @param dateStr Chaîne de date (format: dd/MM/yyyy)
     * @return LocalDate parsé
     * @throws ValidationException si le format est invalide
     */
    public static LocalDate parseDate(String dateStr) throws ValidationException {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) {
                throw new ValidationException("La date est obligatoire.");
            }
            
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
            
        } catch (DateTimeParseException e) {
            throw new ValidationException("Format de date invalide. Utilisez dd/MM/yyyy");
        }
    }
    
    /**
     * Vérifie si une date est dans le futur.
     * 
     * @param dateTime Date à vérifier
     * @return true si la date est dans le futur
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }
    
    /**
     * Vérifie si une date est dans le passé.
     * 
     * @param dateTime Date à vérifier
     * @return true si la date est dans le passé
     */
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(LocalDateTime.now());
    }
    
    /**
     * Vérifie si une date est aujourd'hui.
     * 
     * @param dateTime Date à vérifier
     * @return true si la date est aujourd'hui
     */
    public static boolean isToday(LocalDateTime dateTime) {
        return dateTime != null && dateTime.toLocalDate().equals(LocalDate.now());
    }
    
    /**
     * Retourne le nom du mois en français.
     * 
     * @param mois Numéro du mois (1-12)
     * @return Nom du mois
     */
    public static String getNomMois(int mois) {
        String[] moisNoms = {
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        };
        if (mois >= 1 && mois <= 12) {
            return moisNoms[mois - 1];
        }
        return "Inconnu";
    }
    
    /**
     * Génère un tableau des mois pour JComboBox.
     * 
     * @return Tableau des noms de mois
     */
    public static String[] getMoisArray() {
        return new String[] {
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        };
    }
    
    /**
     * Génère un tableau des années pour JComboBox (5 dernières années + année courante).
     * 
     * @return Tableau des années
     */
    public static String[] getAnneesArray() {
        int currentYear = LocalDate.now().getYear();
        String[] annees = new String[6];
        for (int i = 0; i < 6; i++) {
            annees[i] = String.valueOf(currentYear - i);
        }
        return annees;
    }
}

