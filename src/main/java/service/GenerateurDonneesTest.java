package service;

import model.Batiment;
import model.ReleveEnergetique;
import model.TypeEnergie;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

public class GenerateurDonneesTest {
    private static final Random random = new Random();

    public static void genererDonnees(Batiment batiment, int nbReleves) {
        LocalDate aujourdhui = LocalDate.now();

        for (int i = 0; i < nbReleves; i++) {
            String id = "R-" + batiment.getId() + "-" + (i + 1);
            LocalDate dateReleve = aujourdhui.minusDays(random.nextInt(30));
            LocalTime heureReleve = LocalTime.of(random.nextInt(24), 0);

            TypeEnergie type = TypeEnergie.values()[random.nextInt(TypeEnergie.values().length)];

            double quantite = 10 + (random.nextDouble() * 90);
            double cout = quantite * 0.25;

            ReleveEnergetique r = new ReleveEnergetique(id, dateReleve, heureReleve, type, Math.round(quantite*100.0)/100.0, Math.round(cout*100.0)/100.0);
            batiment.ajouterReleve(r);
        }
        System.out.println("Génération de " + nbReleves + " relevés réussie pour : " + batiment.getNom());
    }
}