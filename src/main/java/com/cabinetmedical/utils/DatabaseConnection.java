package com.cabinetmedical.utils;

import com.cabinetmedical.exceptions.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classe singleton pour la gestion de la connexion à la base de données MySQL.
 * Utilise le pattern Singleton pour garantir une seule instance de connexion.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class DatabaseConnection {
    
    /** Instance unique (Singleton) */
    private static DatabaseConnection instance;
    
    /** Connexion à la base de données */
    private Connection connection;
    
    /** Propriétés de configuration */
    private Properties properties;
    
    /** URL de connexion */
    private String url;
    
    /** Nom d'utilisateur */
    private String username;
    
    /** Mot de passe */
    private String password;
    
    /**
     * Constructeur privé (Singleton).
     * Charge la configuration depuis le fichier database.properties.
     */
    private DatabaseConnection() {
        loadProperties();
    }
    
    /**
     * Charge les propriétés de connexion depuis le fichier de configuration.
     */
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            
            if (input == null) {
                // Configuration par défaut si le fichier n'existe pas
                url = "jdbc:mysql://localhost:3306/cabinet_medical";
                username = "root";
                password = "";
                System.out.println("Fichier database.properties non trouvé, utilisation des valeurs par défaut");
                return;
            }
            
            properties.load(input);
            url = properties.getProperty("db.url", "jdbc:mysql://localhost:3306/cabinet_medical");
            username = properties.getProperty("db.username", "root");
            password = properties.getProperty("db.password", "");
            
            // Charger le driver MySQL
            String driver = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            Class.forName(driver);
            
        } catch (IOException e) {
            throw new DatabaseException("Erreur de chargement de la configuration", e);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Driver MySQL non trouvé", e);
        }
    }
    
    /**
     * Retourne l'instance unique de DatabaseConnection (Singleton).
     * 
     * @return Instance de DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Retourne une connexion à la base de données.
     * Crée une nouvelle connexion si nécessaire.
     * 
     * @return Connection active
     * @throws DatabaseException si la connexion échoue
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, username, password);
                connection.setAutoCommit(true);
            }
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException("Impossible de se connecter à la base de données: " + e.getMessage(), e);
        }
    }
    
    /**
     * Ferme la connexion à la base de données.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la fermeture de la connexion", e);
        }
    }
    
    /**
     * Teste la connexion à la base de données.
     * 
     * @return true si la connexion est valide
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed() && conn.isValid(5);
        } catch (SQLException | DatabaseException e) {
            return false;
        }
    }
    
    /**
     * Démarre une transaction (désactive l'auto-commit).
     * 
     * @throws DatabaseException si erreur
     */
    public void beginTransaction() {
        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            throw new DatabaseException("Erreur au démarrage de la transaction", e);
        }
    }
    
    /**
     * Valide la transaction en cours.
     * 
     * @throws DatabaseException si erreur
     */
    public void commit() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.commit();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du commit", e);
        }
    }
    
    /**
     * Annule la transaction en cours.
     * 
     * @throws DatabaseException si erreur
     */
    public void rollback() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du rollback", e);
        }
    }
}

