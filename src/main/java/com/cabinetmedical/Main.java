package com.cabinetmedical;

import com.cabinetmedical.ui.LoginFrame;
import com.cabinetmedical.utils.SwingHelper;

import javax.swing.*;

/**
 * Classe principale de l'application de gestion de cabinet médical.
 * Point d'entrée de l'application.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class Main {
    
    /**
     * Point d'entrée de l'application.
     * Lance l'écran de connexion.
     * 
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        // Configurer le Look and Feel système
        SwingHelper.setSystemLookAndFeel();
        
        System.out.println("==============================================");
        System.out.println("  CABINET MEDICAL - Application de Gestion");
        System.out.println("==============================================");
        System.out.println();
        System.out.println("Démarrage de l'application...");
        
        // Lancer l'interface de connexion
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}

