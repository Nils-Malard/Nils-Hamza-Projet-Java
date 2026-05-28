package model;

public class BatimentUniversitaire extends Batiment {
    // Ajoute ici les attributs spécifiques à un bâtiment universitaire si tu en as
    // Exemple : private String faculte;

    // Le constructeur doit accepter les 5 paramètres du parent + ses propres paramètres
    public BatimentUniversitaire(String id, String nom, String adresse, double surface, int anneeConstruction) {
        // L'appel à super(...) DOIT être la toute première ligne du constructeur
        super(id, nom, adresse, surface, anneeConstruction);
    }

    @Override
    public String getType() {
        return "Université";
    }

    @Override
    public Batiment cloner() {
        // Implémentation du clonage requise par ton architecture
        BatimentUniversitaire copie = new BatimentUniversitaire(getId(), getNom(), getAdresse(), getSurface(), getAnneeConstruction());
        for (ReleveEnergetique r : getListeReleves()) {
            copie.ajouterReleve(new ReleveEnergetique(r.getIdReleve(), r.getDate(), r.getHeure(), r.getTypeEnergie(), r.getQuantiteConsommee(), r.getValeurFinanciere()));
        }
        return copie;
    }
}