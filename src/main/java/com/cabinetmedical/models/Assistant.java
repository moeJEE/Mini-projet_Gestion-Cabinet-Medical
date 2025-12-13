package com.cabinetmedical.models;

/**
 * Classe représentant un assistant du cabinet médical.
 * Hérite de la classe abstraite Utilisateur.
 * Un assistant peut gérer les patients, les rendez-vous et les paiements.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class Assistant extends Utilisateur {
    
    /**
     * Constructeur par défaut.
     * Initialise automatiquement le type à ASSISTANT.
     */
    public Assistant() {
        super();
        this.type = TypeUtilisateur.ASSISTANT;
    }
    
    /**
     * Constructeur avec login et password.
     * 
     * @param login Login de connexion
     * @param password Mot de passe
     */
    public Assistant(String login, String password) {
        super(login, password, TypeUtilisateur.ASSISTANT);
    }
    
    /**
     * Constructeur complet avec id.
     * 
     * @param id Identifiant de l'assistant
     * @param login Login de connexion
     * @param password Mot de passe hashé
     */
    public Assistant(int id, String login, String password) {
        super(id, login, password, TypeUtilisateur.ASSISTANT);
    }
    
    @Override
    public String toString() {
        return "Assistant{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }
}

