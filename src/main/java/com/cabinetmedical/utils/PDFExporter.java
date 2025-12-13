package com.cabinetmedical.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.cabinetmedical.models.BilanMensuel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Utilitaire pour l'export de documents PDF.
 * Permet de générer des rapports professionnels.
 * 
 * @author Cabinet Medical
 * @version 1.0
 */
public class PDFExporter {
    
    // Couleurs du thème
    private static final BaseColor HEADER_COLOR = new BaseColor(33, 150, 243); // Bleu
    private static final BaseColor ACCENT_COLOR = new BaseColor(76, 175, 80);  // Vert
    private static final BaseColor DANGER_COLOR = new BaseColor(244, 67, 54);  // Rouge
    
    // Polices
    private static Font TITLE_FONT;
    private static Font SUBTITLE_FONT;
    private static Font HEADER_FONT;
    private static Font NORMAL_FONT;
    private static Font BOLD_FONT;
    private static Font SMALL_FONT;
    
    static {
        try {
            TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, HEADER_COLOR);
            SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
            HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
            BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
            SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Exporte un bilan mensuel en PDF.
     * 
     * @param bilan Le bilan à exporter
     * @param filePath Chemin du fichier PDF
     * @throws DocumentException Si erreur lors de la création du PDF
     * @throws IOException Si erreur d'écriture
     */
    public static void exportBilanMensuel(BilanMensuel bilan, String filePath) 
            throws DocumentException, IOException {
        
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        // En-tête avec logo et titre
        addHeader(document, "BILAN MENSUEL");
        
        // Sous-titre avec la période
        Paragraph periode = new Paragraph(
            bilan.getNomMois() + " " + bilan.getAnnee(),
            SUBTITLE_FONT
        );
        periode.setAlignment(Element.ALIGN_CENTER);
        periode.setSpacingAfter(20);
        document.add(periode);
        
        // Date de génération
        Paragraph dateGen = new Paragraph(
            "Genere le " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy a HH:mm")),
            SMALL_FONT
        );
        dateGen.setAlignment(Element.ALIGN_RIGHT);
        dateGen.setSpacingAfter(20);
        document.add(dateGen);
        
        // Section Statistiques Générales
        addSectionTitle(document, "STATISTIQUES GENERALES");
        
        PdfPTable statsTable = new PdfPTable(2);
        statsTable.setWidthPercentage(100);
        statsTable.setSpacingBefore(10);
        statsTable.setSpacingAfter(20);
        
        addTableRow(statsTable, "Nombre total de consultations", String.valueOf(bilan.getNombreConsultations()));
        addTableRow(statsTable, "Chiffre d'affaires", String.format("%.2f DH", bilan.getChiffreAffaires()));
        addTableRow(statsTable, "Prix moyen par consultation", String.format("%.2f DH", bilan.getPrixMoyen()));
        
        document.add(statsTable);
        
        // Section Statistiques de Paiement
        addSectionTitle(document, "STATISTIQUES DE PAIEMENT");
        
        PdfPTable paiementTable = new PdfPTable(2);
        paiementTable.setWidthPercentage(100);
        paiementTable.setSpacingBefore(10);
        paiementTable.setSpacingAfter(20);
        
        addTableRow(paiementTable, "Consultations payees", String.valueOf(bilan.getConsultationsPayees()));
        addTableRow(paiementTable, "Consultations impayees", String.valueOf(bilan.getConsultationsImpayees()));
        addTableRow(paiementTable, "Montant des impayes", String.format("%.2f DH", bilan.getMontantImpayes()));
        addTableRow(paiementTable, "Taux de paiement", String.format("%.1f%%", bilan.getTauxPaiement()));
        
        document.add(paiementTable);
        
        // Section Consultations par Catégorie
        if (bilan.getConsultationsParCategorie() != null && !bilan.getConsultationsParCategorie().isEmpty()) {
            addSectionTitle(document, "CONSULTATIONS PAR CATEGORIE");
            
            PdfPTable categorieTable = new PdfPTable(2);
            categorieTable.setWidthPercentage(100);
            categorieTable.setSpacingBefore(10);
            categorieTable.setSpacingAfter(20);
            
            // En-tête du tableau
            PdfPCell headerCat = new PdfPCell(new Phrase("Categorie", HEADER_FONT));
            headerCat.setBackgroundColor(HEADER_COLOR);
            headerCat.setPadding(8);
            categorieTable.addCell(headerCat);
            
            PdfPCell headerNb = new PdfPCell(new Phrase("Nombre", HEADER_FONT));
            headerNb.setBackgroundColor(HEADER_COLOR);
            headerNb.setPadding(8);
            headerNb.setHorizontalAlignment(Element.ALIGN_CENTER);
            categorieTable.addCell(headerNb);
            
            for (Map.Entry<String, Integer> entry : bilan.getConsultationsParCategorie().entrySet()) {
                PdfPCell catCell = new PdfPCell(new Phrase(entry.getKey(), NORMAL_FONT));
                catCell.setPadding(6);
                categorieTable.addCell(catCell);
                
                PdfPCell nbCell = new PdfPCell(new Phrase(String.valueOf(entry.getValue()), NORMAL_FONT));
                nbCell.setPadding(6);
                nbCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                categorieTable.addCell(nbCell);
            }
            
            document.add(categorieTable);
        }
        
        // Pied de page
        addFooter(document);
        
        document.close();
    }
    
    /**
     * Exporte une liste de consultations en PDF.
     * 
     * @param data Données du tableau [ID, Date, Patient, Catégorie, Prix, Statut]
     * @param colonnes Noms des colonnes
     * @param titre Titre du rapport
     * @param filePath Chemin du fichier PDF
     * @throws DocumentException Si erreur lors de la création du PDF
     * @throws IOException Si erreur d'écriture
     */
    public static void exportTableToPDF(Object[][] data, String[] colonnes, String titre, String filePath)
            throws DocumentException, IOException {
        
        Document document = new Document(PageSize.A4.rotate()); // Mode paysage
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        // En-tête
        addHeader(document, titre);
        
        // Date de génération
        Paragraph dateGen = new Paragraph(
            "Genere le " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy a HH:mm")),
            SMALL_FONT
        );
        dateGen.setAlignment(Element.ALIGN_RIGHT);
        dateGen.setSpacingAfter(20);
        document.add(dateGen);
        
        // Tableau
        PdfPTable table = new PdfPTable(colonnes.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // En-têtes du tableau
        for (String col : colonnes) {
            PdfPCell headerCell = new PdfPCell(new Phrase(col, HEADER_FONT));
            headerCell.setBackgroundColor(HEADER_COLOR);
            headerCell.setPadding(8);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell);
        }
        
        // Données
        boolean alternate = false;
        for (Object[] row : data) {
            for (Object cell : row) {
                PdfPCell dataCell = new PdfPCell(new Phrase(
                    cell != null ? cell.toString() : "", 
                    NORMAL_FONT
                ));
                dataCell.setPadding(6);
                if (alternate) {
                    dataCell.setBackgroundColor(new BaseColor(245, 245, 245));
                }
                table.addCell(dataCell);
            }
            alternate = !alternate;
        }
        
        document.add(table);
        
        // Résumé
        Paragraph resume = new Paragraph(
            "Total: " + data.length + " enregistrement(s)",
            BOLD_FONT
        );
        resume.setSpacingBefore(15);
        document.add(resume);
        
        // Pied de page
        addFooter(document);
        
        document.close();
    }
    
    /**
     * Ajoute un en-tête au document.
     */
    private static void addHeader(Document document, String title) throws DocumentException {
        // Ligne de séparation
        LineSeparator line = new LineSeparator();
        line.setLineColor(HEADER_COLOR);
        line.setLineWidth(2);
        
        // Titre
        Paragraph titlePara = new Paragraph(title, TITLE_FONT);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(5);
        document.add(titlePara);
        
        // Sous-titre
        Paragraph subtitle = new Paragraph("Cabinet Medical - Systeme de Gestion", SMALL_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);
        
        document.add(line);
        document.add(Chunk.NEWLINE);
    }
    
    /**
     * Ajoute un titre de section.
     */
    private static void addSectionTitle(Document document, String title) throws DocumentException {
        Paragraph section = new Paragraph(title, SUBTITLE_FONT);
        section.setSpacingBefore(15);
        section.setSpacingAfter(5);
        document.add(section);
        
        LineSeparator line = new LineSeparator();
        line.setLineColor(BaseColor.LIGHT_GRAY);
        line.setLineWidth(1);
        document.add(line);
    }
    
    /**
     * Ajoute une ligne à un tableau de statistiques.
     */
    private static void addTableRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, NORMAL_FONT));
        labelCell.setPadding(8);
        labelCell.setBorderColor(BaseColor.LIGHT_GRAY);
        table.addCell(labelCell);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, BOLD_FONT));
        valueCell.setPadding(8);
        valueCell.setBorderColor(BaseColor.LIGHT_GRAY);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }
    
    /**
     * Ajoute un pied de page au document.
     */
    private static void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        
        LineSeparator line = new LineSeparator();
        line.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(line);
        
        Paragraph footer = new Paragraph(
            "Cabinet Medical - Document genere automatiquement - Confidentiel",
            SMALL_FONT
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        document.add(footer);
    }
}

