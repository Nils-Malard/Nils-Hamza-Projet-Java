package model;

public class Appartement extends Batiment {
    private int etage;
    private String numeroAppt;

    public Appartement(String id, String nom, String adresse, double surface, int anneeConstruction, int etage, String numeroAppt) {
        super(id, nom, adresse, surface, anneeConstruction);
        this.etage = etage;
        this.numeroAppt = numeroAppt;
    }

    @Override
    public String getType() {
        return "Appartement";
    }

    @Override
    public Batiment cloner() {
        Appartement copie = new Appartement(getId() + "_copie", getNom() + " (Copie)", getAdresse(), getSurface(), getAnneeConstruction(), etage, numeroAppt);
        for (ReleveEnergetique r : getListeReleves()) {
            copie.ajouterReleve(new ReleveEnergetique(r.getIdReleve(), r.getDate(), r.getHeure(), r.getTypeEnergie(), r.getQuantiteConsommee(), r.getValeurFinanciere()));
        }
        return copie;
    }

    // Getters et Setters
    public int getEtage() { return etage; }
    public void setEtage(int etage) { this.etage = etage; }
    public String getNumeroAppt() { return numeroAppt; }
    public void setNumeroAppt(String numeroAppt) { this.numeroAppt = numeroAppt; }
}