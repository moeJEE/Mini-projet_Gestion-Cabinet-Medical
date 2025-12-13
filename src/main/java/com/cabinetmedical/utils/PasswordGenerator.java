package com.cabinetmedical.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaire pour générer des mots de passe hashés.
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        String[] passwords = {"admin123", "medecin123", "assistant123"};
        
        System.out.println("=== Générateur de hash BCrypt ===\n");
        
        for (String password : passwords) {
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            System.out.println("Mot de passe: " + password);
            System.out.println("Hash BCrypt: " + hash);
            System.out.println("Vérification: " + BCrypt.checkpw(password, hash));
            System.out.println();
        }
        
        // Test avec le hash actuel dans la base
        String currentHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye.DLVS9VBXwCh7Y9wZU8gGH4mFR5yCDq";
        System.out.println("=== Test du hash actuel ===");
        System.out.println("Hash actuel: " + currentHash);
        System.out.println("admin123 match: " + BCrypt.checkpw("admin123", currentHash));
        System.out.println("password match: " + BCrypt.checkpw("password", currentHash));
    }
}

