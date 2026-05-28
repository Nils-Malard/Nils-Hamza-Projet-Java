package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.*;
import service.GestionnaireBatiment;
import service.GenerateurDonneesTest;
import service.TableauDeBord;
import service.ImporteurCSV;
import view.GraphiqueConsommationView;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GestionnaireBatiment gestionnaire = GestionnaireBatiment.getInstance();

        Maison villa = new Maison("M01", "Villa Éco-Verte", "12 rue des Fleurs", 100.0, 2018, 1, true);
        Bureau qgTech = new Bureau("B01", "HQ Technopole", "45 avenue de l'Europe", 500.0, 2022, 15, "InnovCorp");
        Appartement appt = new Appartement("A01", "Appartement Centre-Ville", "2 Place du Marché", 65.0, 2015, 3, "302B");

        gestionnaire.ajouterBatiment(villa);
        gestionnaire.ajouterBatiment(qgTech);
        gestionnaire.ajouterBatiment(appt);

        GenerateurDonneesTest.genererDonnees(qgTech, 15);
        GenerateurDonneesTest.genererDonnees(appt, 8);

        villa.ajouterReleve(new ReleveEnergetique("R-V1", LocalDate.now(), LocalTime.of(8,0), TypeEnergie.ELECTRICITE, 120.0, 30.0));
        villa.ajouterReleve(new ReleveEnergetique("R-V2", LocalDate.now(), LocalTime.of(12,0), TypeEnergie.EAU, 40.0, 8.0));
        villa.ajouterReleve(new ReleveEnergetique("R-V3", LocalDate.now(), LocalTime.of(19,0), TypeEnergie.ELECTRICITE, 450.0, 112.0));


        ImporteurCSV importeur = new ImporteurCSV("donnees.csv", ';');
        importeur.importerEtDistribuer();

        TableauDeBord tdb = new TableauDeBord();

        GraphiqueConsommationView vuePrincipale = new GraphiqueConsommationView(tdb);

        Scene scene = new Scene(vuePrincipale, 1100, 700);

        primaryStage.setTitle("Smart Energy Manager - Module Connecté (API Météo)");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}