package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Batiment {
    private String id;
    private String nom;
    private String adresse;
    private double surface;
    private int anneeConstruction;
    private List<ReleveEnergetique> listeReleves;

    public Batiment(String id, String nom, String adresse, double surface, int anneeConstruction) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.surface = surface;
        this.anneeConstruction = anneeConstruction;
        this.listeReleves = new ArrayList<>();
    }

    public void ajouterReleve(ReleveEnergetique r) {
        this.listeReleves.add(r);
    }

    public double getConsommationParPeriode(LocalDate debut, LocalDate fin) {
        return listerRelevesParPeriode(debut, fin).stream()
                .mapToDouble(ReleveEnergetique::getQuantiteConsommee)
                .sum();
    }

    public double calculerCoutTotalParPeriode(LocalDate debut, LocalDate fin) {
        return listerRelevesParPeriode(debut, fin).stream()
                .mapToDouble(ReleveEnergetique::getValeurFinanciere)
                .sum();
    }

    private List<ReleveEnergetique> listerRelevesParPeriode(LocalDate debut, LocalDate fin) {
        List<ReleveEnergetique> res = new ArrayList<>();
        for (ReleveEnergetique r : listeReleves) {
            if (!r.getDate().isBefore(debut) && !r.getDate().isAfter(fin)) {
                res.add(r);
            }
        }
        return res;
    }

    public abstract Batiment cloner();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public double getSurface() { return surface; }
    public void setSurface(double surface) { this.surface = surface; }

    public int getAnneeConstruction() { return anneeConstruction; }
    public void setAnneeConstruction(int anneeConstruction) { this.anneeConstruction = anneeConstruction; }

    public List<ReleveEnergetique> getListeReleves() { return listeReleves; }
    public abstract String getType();

    @Override
    public String toString() {
        return nom + " [" + getType() + "] - " + adresse;
    }
}