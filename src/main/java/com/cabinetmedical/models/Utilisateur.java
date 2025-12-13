package com.cabinetmedical.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Classe abstraite représentant un utilisateur du système.
 * Sert de base pour les classes Medecin et Assistant.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public abstract class Utilisateur {
    
    /** Identifiant unique de l'utilisateur (auto-incrémenté) */
    protected int id;
    
    /** Login de connexion (unique) */
    protected String login;
    
    /** Mot de passe hashé (BCrypt) */
    protected String password;
    
    /** Type d'utilisateur (MEDECIN ou ASSISTANT) */
    protected TypeUtilisateur type;
    
    /** Date de création du compte */
    protected LocalDateTime createdAt;
    
    /**
     * Constructeur par défaut.
     */
    public Utilisateur() {
    }
    
    /**
     * Constructeur avec paramètres.
     * 
     * @param login Login de connexion
     * @param password Mot de passe (sera hashé)
     * @param type Type d'utilisateur
     */
    public Utilisateur(String login, String password, TypeUtilisateur type) {
        this.login = login;
        this.password = password;
        this.type = type;
    }
    
    /**
     * Constructeur complet avec id.
     * 
     * @param id Identifiant
     * @param login Login de connexion
     * @param password Mot de passe hashé
     * @param type Type d'utilisateur
     */
    public Utilisateur(int id, String login, String password, TypeUtilisateur type) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.type = type;
    }
    
    // Getters et Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getLogin() {
        return login;
    }
    
    public void setLogin(String login) {
        this.login = login;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public TypeUtilisateur getType() {
        return type;
    }
    
    public void setType(TypeUtilisateur type) {
        this.type = type;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Vérifie si l'utilisateur est un médecin.
     * 
     * @return true si médecin, false sinon
     */
    public boolean isMedecin() {
        return type == TypeUtilisateur.MEDECIN;
    }
    
    /**
     * Vérifie si l'utilisateur est un assistant.
     * 
     * @return true si assistant, false sinon
     */
    public boolean isAssistant() {
        return type == TypeUtilisateur.ASSISTANT;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", type=" + type +
                '}';
    }
}

