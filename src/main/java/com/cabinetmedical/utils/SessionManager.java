package com.cabinetmedical.utils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Gestionnaire de session utilisateur.
 * Gère la connexion persistante, le timeout et l'audit des actions.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class SessionManager {
    
    private static SessionManager instance;
    
    // Configuration
    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.cabinetmedical";
    private static final String SESSION_FILE = CONFIG_DIR + "/session.properties";
    private static final String AUDIT_FILE = CONFIG_DIR + "/audit.log";
    private static final int SESSION_TIMEOUT_MINUTES = 30;
    
    // État de la session
    private int currentUserId = -1;
    private String currentUserType = null;
    private String currentUserLogin = null;
    private LocalDateTime lastActivity;
    private Timer timeoutTimer;
    private Runnable onTimeoutCallback;
    
    /**
     * Constructeur privé (Singleton).
     */
    private SessionManager() {
        createConfigDirectory();
    }
    
    /**
     * Retourne l'instance unique du gestionnaire de session.
     * 
     * @return Instance du SessionManager
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Crée le répertoire de configuration si nécessaire.
     */
    private void createConfigDirectory() {
        try {
            Files.createDirectories(Paths.get(CONFIG_DIR));
        } catch (IOException e) {
            System.err.println("Erreur creation repertoire config: " + e.getMessage());
        }
    }
    
    /**
     * Démarre une nouvelle session.
     * 
     * @param userId ID de l'utilisateur
     * @param userType Type d'utilisateur (MEDECIN, ASSISTANT)
     * @param login Login de l'utilisateur
     * @param rememberMe Sauvegarder la session
     */
    public void startSession(int userId, String userType, String login, boolean rememberMe) {
        this.currentUserId = userId;
        this.currentUserType = userType;
        this.currentUserLogin = login;
        this.lastActivity = LocalDateTime.now();
        
        if (rememberMe) {
            saveSession();
        }
        
        startTimeoutTimer();
        logAction("CONNEXION", "Utilisateur connecte: " + login + " (" + userType + ")");
    }
    
    /**
     * Termine la session actuelle.
     */
    public void endSession() {
        if (currentUserLogin != null) {
            logAction("DECONNEXION", "Utilisateur deconnecte: " + currentUserLogin);
        }
        
        clearSession();
        stopTimeoutTimer();
        deleteSessionFile();
    }
    
    /**
     * Vérifie si une session sauvegardée existe.
     * 
     * @return true si une session valide existe
     */
    public boolean hasSavedSession() {
        File sessionFile = new File(SESSION_FILE);
        if (!sessionFile.exists()) {
            return false;
        }
        
        try (FileInputStream fis = new FileInputStream(sessionFile)) {
            Properties props = new Properties();
            props.load(fis);
            
            String savedDate = props.getProperty("savedDate");
            if (savedDate != null) {
                LocalDateTime saved = LocalDateTime.parse(savedDate);
                // Session valide pendant 7 jours
                if (saved.plusDays(7).isAfter(LocalDateTime.now())) {
                    return true;
                }
            }
        } catch (Exception e) {
            // Session invalide
        }
        
        deleteSessionFile();
        return false;
    }
    
    /**
     * Restaure une session sauvegardée.
     * 
     * @return true si la session a été restaurée
     */
    public boolean restoreSession() {
        if (!hasSavedSession()) {
            return false;
        }
        
        try (FileInputStream fis = new FileInputStream(SESSION_FILE)) {
            Properties props = new Properties();
            props.load(fis);
            
            this.currentUserId = Integer.parseInt(props.getProperty("userId", "-1"));
            this.currentUserType = props.getProperty("userType");
            this.currentUserLogin = props.getProperty("login");
            this.lastActivity = LocalDateTime.now();
            
            if (currentUserId > 0 && currentUserType != null) {
                startTimeoutTimer();
                logAction("RESTAURATION_SESSION", "Session restauree pour: " + currentUserLogin);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Erreur restauration session: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Sauvegarde la session actuelle.
     */
    private void saveSession() {
        try (FileOutputStream fos = new FileOutputStream(SESSION_FILE)) {
            Properties props = new Properties();
            props.setProperty("userId", String.valueOf(currentUserId));
            props.setProperty("userType", currentUserType);
            props.setProperty("login", currentUserLogin);
            props.setProperty("savedDate", LocalDateTime.now().toString());
            props.store(fos, "Cabinet Medical - Session");
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde session: " + e.getMessage());
        }
    }
    
    /**
     * Supprime le fichier de session.
     */
    private void deleteSessionFile() {
        try {
            Files.deleteIfExists(Paths.get(SESSION_FILE));
        } catch (IOException e) {
            // Ignorer
        }
    }
    
    /**
     * Réinitialise l'état de la session.
     */
    private void clearSession() {
        this.currentUserId = -1;
        this.currentUserType = null;
        this.currentUserLogin = null;
        this.lastActivity = null;
    }
    
    /**
     * Démarre le timer de timeout.
     */
    private void startTimeoutTimer() {
        stopTimeoutTimer();
        
        timeoutTimer = new Timer(true);
        timeoutTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkTimeout();
            }
        }, 60000, 60000); // Vérifie toutes les minutes
    }
    
    /**
     * Arrête le timer de timeout.
     */
    private void stopTimeoutTimer() {
        if (timeoutTimer != null) {
            timeoutTimer.cancel();
            timeoutTimer = null;
        }
    }
    
    /**
     * Vérifie si la session a expiré.
     */
    private void checkTimeout() {
        if (lastActivity != null && currentUserId > 0) {
            LocalDateTime timeout = lastActivity.plusMinutes(SESSION_TIMEOUT_MINUTES);
            if (LocalDateTime.now().isAfter(timeout)) {
                logAction("TIMEOUT", "Session expiree pour: " + currentUserLogin);
                
                if (onTimeoutCallback != null) {
                    javax.swing.SwingUtilities.invokeLater(onTimeoutCallback);
                }
            }
        }
    }
    
    /**
     * Met à jour l'activité de l'utilisateur.
     */
    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
    }
    
    /**
     * Définit le callback de timeout.
     * 
     * @param callback Action à exécuter lors du timeout
     */
    public void setTimeoutCallback(Runnable callback) {
        this.onTimeoutCallback = callback;
    }
    
    /**
     * Enregistre une action dans le journal d'audit.
     * 
     * @param action Type d'action
     * @param details Détails de l'action
     */
    public void logAction(String action, String details) {
        try (FileWriter fw = new FileWriter(AUDIT_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            );
            
            String logEntry = String.format("[%s] [%s] [User:%s] %s%n",
                timestamp,
                action,
                currentUserLogin != null ? currentUserLogin : "SYSTEM",
                details
            );
            
            bw.write(logEntry);
            
        } catch (IOException e) {
            System.err.println("Erreur ecriture audit: " + e.getMessage());
        }
    }
    
    /**
     * Lit le journal d'audit.
     * 
     * @param maxLines Nombre maximum de lignes à lire
     * @return Contenu du journal
     */
    public String readAuditLog(int maxLines) {
        StringBuilder sb = new StringBuilder();
        
        try {
            Path path = Paths.get(AUDIT_FILE);
            if (Files.exists(path)) {
                java.util.List<String> lines = Files.readAllLines(path);
                int start = Math.max(0, lines.size() - maxLines);
                for (int i = start; i < lines.size(); i++) {
                    sb.append(lines.get(i)).append("\n");
                }
            }
        } catch (IOException e) {
            sb.append("Erreur lecture audit: ").append(e.getMessage());
        }
        
        return sb.toString();
    }
    
    // Getters
    
    public int getCurrentUserId() {
        return currentUserId;
    }
    
    public String getCurrentUserType() {
        return currentUserType;
    }
    
    public String getCurrentUserLogin() {
        return currentUserLogin;
    }
    
    public boolean isLoggedIn() {
        return currentUserId > 0 && currentUserType != null;
    }
    
    public int getTimeoutMinutes() {
        return SESSION_TIMEOUT_MINUTES;
    }
}

