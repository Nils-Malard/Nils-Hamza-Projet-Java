package model;

public class Maison extends Batiment {
    private int nbPieces;
    private boolean aGarage;

    public Maison(String id, String nom, String adresse, double surface, int anneeConstruction, int nbPieces, boolean aGarage) {
        super(id, nom, adresse, surface, anneeConstruction);
        this.nbPieces = nbPieces;
        this.aGarage = aGarage;
    }

    @Override
    public String getType() {
        return "Maison";
    }

    @Override
    public Batiment cloner() {
        Maison copie = new Maison(getId() + "_copie", getNom() + " (Copie)", getAdresse(), getSurface(), getAnneeConstruction(), nbPieces, aGarage);
        for (ReleveEnergetique r : getListeReleves()) {
            copie.ajouterReleve(new ReleveEnergetique(r.getIdReleve(), r.getDate(), r.getHeure(), r.getTypeEnergie(), r.getQuantiteConsommee(), r.getValeurFinanciere()));
        }
        return copie;
    }

    // Getters et Setters
    public int getNbPieces() { return nbPieces; }
    public void setNbPieces(int nbPieces) { this.nbPieces = nbPieces; }
    public boolean isaGarage() { return aGarage; }
    public void setaGarage(boolean aGarage) { this.aGarage = aGarage; }
}