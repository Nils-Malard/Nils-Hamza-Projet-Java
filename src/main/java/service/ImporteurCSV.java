package service;

import model.Batiment;
import model.ReleveEnergetique;
import model.TypeEnergie;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ImporteurCSV {
    private String cheminFichier;
    private char separateur;

    public ImporteurCSV(String cheminFichier, char separateur) {
        this.cheminFichier = cheminFichier;
        this.separateur = separateur;
    }

    public void importerEtDistribuer() {
        GestionnaireBatiment gestionnaire = GestionnaireBatiment.getInstance();
        String ligne;
        boolean estPremiereLigne = true;

        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            while ((ligne = br.readLine()) != null) {
                if (estPremiereLigne) {
                    estPremiereLigne = false;
                    String[] verifHeader = ligne.split(String.valueOf(separateur));
                    if (verifHeader[0].equalsIgnoreCase("BâtimentID") || verifHeader[0].equalsIgnoreCase("ID")) {
                        continue;
                    }
                }

                if (ligne.trim().isEmpty()) continue;

                String[] jetons = ligne.split(String.valueOf(separateur));

                if (jetons.length >= 7) {
                    String idBatiment = jetons[0].trim();
                    String idReleve = jetons[1].trim();
                    LocalDate date = LocalDate.parse(jetons[2].trim());
                    LocalTime heure = LocalTime.parse(jetons[3].trim());
                    TypeEnergie typeEnergie = TypeEnergie.valueOf(jetons[4].trim().toUpperCase());
                    double quantite = Double.parseDouble(jetons[5].trim());
                    double valeurFinanciere = Double.parseDouble(jetons[6].trim());

                    ReleveEnergetique releve = new ReleveEnergetique(
                            idReleve, date, heure, typeEnergie, quantite, valeurFinanciere
                    );

                    Batiment batiment = gestionnaire.trouverBatimentParId(idBatiment);
                    if (batiment != null) {
                        batiment.ajouterReleve(releve);
                    } else {
                        System.out.println(" Impossible d'importer le relevé " + idReleve + " : Bâtiment '" + idBatiment + "' inconnu.");
                    }
                }
            }
            System.out.println("Importation du fichier CSV et distribution des données réussie !");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Erreur lors de l'importation du CSV : " + e.getMessage());
        }
    }
}