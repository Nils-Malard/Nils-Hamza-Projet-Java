package model;

public class BatimentUniversitaire extends Batiment {

    public BatimentUniversitaire(String id, String nom, String adresse, double surface, int anneeConstruction) {
        super(id, nom, adresse, surface, anneeConstruction);
    }

    @Override
    public String getType() {
        return "Université";
    }

    @Override
    public Batiment cloner() {

        BatimentUniversitaire copie = new BatimentUniversitaire(getId(), getNom(), getAdresse(), getSurface(), getAnneeConstruction());
        for (ReleveEnergetique r : getListeReleves()) {
            copie.ajouterReleve(new ReleveEnergetique(r.getIdReleve(), r.getDate(), r.getHeure(), r.getTypeEnergie(), r.getQuantiteConsommee(), r.getValeurFinanciere()));
        }
        return copie;
    }
}