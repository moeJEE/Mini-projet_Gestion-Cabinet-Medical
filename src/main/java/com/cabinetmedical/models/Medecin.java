package com.cabinetmedical.models;

/**
 * Classe représentant un médecin du cabinet.
 * Hérite de la classe abstraite Utilisateur.
 * Un médecin peut gérer ses consultations et voir son planning.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class Medecin extends Utilisateur {
    
    /**
     * Constructeur par défaut.
     * Initialise automatiquement le type à MEDECIN.
     */
    public Medecin() {
        super();
        this.type = TypeUtilisateur.MEDECIN;
    }
    
    /**
     * Constructeur avec login et password.
     * 
     * @param login Login de connexion
     * @param password Mot de passe
     */
    public Medecin(String login, String password) {
        super(login, password, TypeUtilisateur.MEDECIN);
    }
    
    /**
     * Constructeur complet avec id.
     * 
     * @param id Identifiant du médecin
     * @param login Login de connexion
     * @param password Mot de passe hashé
     */
    public Medecin(int id, String login, String password) {
        super(id, login, password, TypeUtilisateur.MEDECIN);
    }
    
    @Override
    public String toString() {
        return "Medecin{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }
}

