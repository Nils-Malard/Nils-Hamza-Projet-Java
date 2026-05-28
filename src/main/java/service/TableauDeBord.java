package service;

import model.Batiment;
import model.ReleveEnergetique;
import model.TypeEnergie;
import java.time.LocalDate;
import java.util.*;

public class TableauDeBord {
    private Batiment batimentSelectionne;
    private Map<String, Double> indicateurs;

    public TableauDeBord() {
        this.indicateurs = new HashMap<>();
    }

    public void setBatimentSelectionne(Batiment b) {
        this.batimentSelectionne = b;
        this.actualiserIndicateurs();
    }

    public void actualiserIndicateurs() {
        if (batimentSelectionne == null) {
            indicateurs.clear();
            return;
        }
        LocalDate fin = LocalDate.now();
        LocalDate debut = fin.minusDays(30);

        double consoTotale = batimentSelectionne.getConsommationParPeriode(debut, fin);
        double coutTotal = batimentSelectionne.calculerCoutTotalParPeriode(debut, fin);
        double consoParMetreCarre = batimentSelectionne.getSurface() > 0 ? (consoTotale / batimentSelectionne.getSurface()) : 0;

        indicateurs.put("Consommation Totale (30j)", Math.round(consoTotale * 100.0) / 100.0);
        indicateurs.put("Coût Estimé (30j)", Math.round(coutTotal * 100.0) / 100.0);
        indicateurs.put("Performance (kWh/m²)", Math.round(consoParMetreCarre * 100.0) / 100.0);
    }

    public double getEstimationFactureMensuelle() {
        if (batimentSelectionne == null) return 0.0;
        LocalDate fin = LocalDate.now();
        LocalDate debut = fin.minusDays(30);
        return Math.round(batimentSelectionne.calculerCoutTotalParPeriode(debut, fin) * 100.0) / 100.0;
    }

    public double calculerFactureAjustee(double tarifKwhApi) {
        if (batimentSelectionne == null) return 0.0;
        LocalDate fin = LocalDate.now();
        LocalDate debut = fin.minusDays(30);
        double consoTotale = batimentSelectionne.getConsommationParPeriode(debut, fin);
        return Math.round((consoTotale * tarifKwhApi) * 100.0) / 100.0;
    }
    public String analyserImpactMeteo(double temperatureExterieure) {
        if (batimentSelectionne == null) return "Aucun bâtiment sélectionné.";
        if (temperatureExterieure < 10.0) {
            return "Température basse (" + temperatureExterieure + "°C). Attention à la surconsommation de Chauffage.";
        } else if (temperatureExterieure > 28.0) {
            return "Température élevée (" + temperatureExterieure + "°C). Risque de pic lié à la climatisation.";
        } else {
            return "Température idéale (" + temperatureExterieure + "°C). Consommation passive optimisée.";
        }
    }

    public String getEnergieDominante() {
        if (batimentSelectionne == null || batimentSelectionne.getListeReleves().isEmpty()) return "Aucune";

        Map<TypeEnergie, Double> totaux = new HashMap<>();
        for (ReleveEnergetique r : batimentSelectionne.getListeReleves()) {
            totaux.put(r.getTypeEnergie(), totaux.getOrDefault(r.getTypeEnergie(), 0.0) + r.getQuantiteConsommee());
        }

        return totaux.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey().toString())
                .orElse("Inconnu");
    }

    public String getTendanceConsommation() {
        if (batimentSelectionne == null || batimentSelectionne.getListeReleves().isEmpty()) return "Stable";

        LocalDate ajd = LocalDate.now();
        double recent = batimentSelectionne.getConsommationParPeriode(ajd.minusDays(15), ajd);
        double ancien = batimentSelectionne.getConsommationParPeriode(ajd.minusDays(30), ajd.minusDays(16));

        if (ancien == 0) return "Stable";
        double evo = ((recent - ancien) / ancien) * 100;

        if (evo > 5) return "En hausse (" + Math.round(evo) + "%)";
        if (evo < -5) return "En baisse (" + Math.round(Math.abs(evo)) + "%)";
        return "➡Stable";
    }

    public List<String> detecterPicsConsommation() {
        List<String> pics = new ArrayList<>();
        if (batimentSelectionne == null || batimentSelectionne.getListeReleves().isEmpty()) return pics;

        double moy = batimentSelectionne.getListeReleves().stream()
                .mapToDouble(ReleveEnergetique::getQuantiteConsommee)
                .average()
                .orElse(0.0);

        for (ReleveEnergetique r : batimentSelectionne.getListeReleves()) {
            if (r.getQuantiteConsommee() > (moy * 1.5)) {
                pics.add("🚨 [" + r.getTypeEnergie() + "] Le " + r.getDate() + " à " + r.getHeure() + " : " + r.getQuantiteConsommee() + " unités");
            }
        }
        return pics;
    }

    public Batiment getBatimentPlusConso(LocalDate d, LocalDate f) {
        List<Batiment> tous = GestionnaireBatiment.getInstance().getTousLesBatiments();

        if (tous == null || tous.isEmpty()) return null;

        Batiment pire = null;
        double max = -1;

        for (Batiment b : tous) {
            double c = b.getConsommationParPeriode(d, f);
            if (c > max) {
                max = c;
                pire = b;
            }
        }
        return pire;
    }

    public Batiment getBatimentSelectionne() {
        return batimentSelectionne;
    }

    public Map<String, Double> getIndicateurs() {
        return new HashMap<>(indicateurs);
    }
}