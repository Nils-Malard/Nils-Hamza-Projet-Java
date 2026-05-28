package model;

public class Bureau extends Batiment {
    private int nbBureaux;
    private String nomEntreprise;

    public Bureau(String id, String nom, String adresse, double surface, int anneeConstruction, int nbBureaux, String nomEntreprise) {
        super(id, nom, adresse, surface, anneeConstruction);
        this.nbBureaux = nbBureaux;
        this.nomEntreprise = nomEntreprise;
    }

    @Override
    public String getType() {
        return "Bureau";
    }

    @Override
    public Batiment cloner() {
        Bureau copie = new Bureau(getId() + "_copie", getNom() + " (Copie)", getAdresse(), getSurface(), getAnneeConstruction(), nbBureaux, nomEntreprise);
        for (ReleveEnergetique r : getListeReleves()) {
            copie.ajouterReleve(new ReleveEnergetique(r.getIdReleve(), r.getDate(), r.getHeure(), r.getTypeEnergie(), r.getQuantiteConsommee(), r.getValeurFinanciere()));
        }
        return copie;
    }

    // Getters et Setters
    public int getNbBureaux() { return nbBureaux; }
    public void setNbBureaux(int nbBureaux) { this.nbBureaux = nbBureaux; }
    public String getNomEntreprise() { return nomEntreprise; }
    public void setNomEntreprise(String nomEntreprise) { this.nomEntreprise = nomEntreprise; }
}