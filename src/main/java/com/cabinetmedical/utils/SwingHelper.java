package com.cabinetmedical.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Classe utilitaire pour les composants Swing.
 * Fournit des méthodes pour simplifier la manipulation des interfaces graphiques.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class SwingHelper {
    
    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private SwingHelper() {
        // Classe utilitaire
    }
    
    /**
     * Affiche un message d'erreur dans une boîte de dialogue.
     * 
     * @param parent Composant parent
     * @param message Message d'erreur
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent, 
            message, 
            "Erreur", 
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Affiche un message de succès dans une boîte de dialogue.
     * 
     * @param parent Composant parent
     * @param message Message de succès
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent, 
            message, 
            "Succès", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Affiche un message d'information dans une boîte de dialogue.
     * 
     * @param parent Composant parent
     * @param message Message d'information
     */
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent, 
            message, 
            "Information", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Affiche un message d'avertissement dans une boîte de dialogue.
     * 
     * @param parent Composant parent
     * @param message Message d'avertissement
     */
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent, 
            message, 
            "Attention", 
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Affiche une boîte de dialogue de confirmation.
     * 
     * @param parent Composant parent
     * @param message Message de confirmation
     * @return true si l'utilisateur confirme
     */
    public static boolean showConfirmation(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
            parent, 
            message, 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Affiche une boîte de dialogue de confirmation avec titre personnalisé.
     * 
     * @param parent Composant parent
     * @param message Message de confirmation
     * @param titre Titre de la boîte de dialogue
     * @return true si l'utilisateur confirme
     */
    public static boolean showConfirmation(Component parent, String message, String titre) {
        int result = JOptionPane.showConfirmDialog(
            parent, 
            message, 
            titre, 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Remplit une JTable avec des données et des colonnes.
     * La table sera non-éditable.
     * 
     * @param table JTable à remplir
     * @param data Données (tableau 2D)
     * @param columns Noms des colonnes
     */
    public static void populateTable(JTable table, Object[][] data, String[] columns) {
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre la table non-éditable
            }
        };
        table.setModel(model);
        
        // Ajuster la largeur des colonnes automatiquement
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }
    
    /**
     * Remplit une JComboBox avec des éléments.
     * 
     * @param comboBox JComboBox à remplir
     * @param items Éléments à ajouter
     */
    public static void populateComboBox(JComboBox<String> comboBox, String[] items) {
        comboBox.removeAllItems();
        for (String item : items) {
            comboBox.addItem(item);
        }
    }
    
    /**
     * Vide tous les champs texte d'un conteneur (récursivement).
     * 
     * @param container Conteneur à traiter
     */
    public static void clearTextFields(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JTextField) {
                ((JTextField) c).setText("");
            } else if (c instanceof JTextArea) {
                ((JTextArea) c).setText("");
            } else if (c instanceof JPasswordField) {
                ((JPasswordField) c).setText("");
            } else if (c instanceof Container) {
                clearTextFields((Container) c);
            }
        }
    }
    
    /**
     * Récupère l'ID de la ligne sélectionnée dans une JTable.
     * Suppose que l'ID est dans la première colonne.
     * 
     * @param table JTable
     * @return ID de la ligne sélectionnée, ou -1 si aucune sélection
     */
    public static int getSelectedId(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            return -1;
        }
        Object value = table.getValueAt(selectedRow, 0);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Vérifie si une ligne est sélectionnée dans une JTable.
     * 
     * @param table JTable à vérifier
     * @return true si une ligne est sélectionnée
     */
    public static boolean hasSelection(JTable table) {
        return table.getSelectedRow() != -1;
    }
    
    /**
     * Centre une fenêtre sur l'écran.
     * 
     * @param window Fenêtre à centrer
     */
    public static void centerWindow(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - window.getWidth()) / 2;
        int y = (screenSize.height - window.getHeight()) / 2;
        window.setLocation(x, y);
    }
    
    /**
     * Configure le look and feel système.
     */
    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Utiliser le look par défaut si échec
        }
    }
    
    /**
     * Active/désactive un groupe de composants.
     * 
     * @param enabled true pour activer
     * @param components Composants à modifier
     */
    public static void setEnabled(boolean enabled, Component... components) {
        for (Component c : components) {
            c.setEnabled(enabled);
        }
    }
}

