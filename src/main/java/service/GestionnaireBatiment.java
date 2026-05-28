package service;

import model.Batiment;
import java.util.ArrayList;
import java.util.List;

public class GestionnaireBatiment {
    private static GestionnaireBatiment instance;

    private List<Batiment> batiments;

    private GestionnaireBatiment() {
        this.batiments = new ArrayList<>();
    }

    public static synchronized GestionnaireBatiment getInstance() {
        if (instance == null) {
            instance = new GestionnaireBatiment();
        }
        return instance;
    }

    public void ajouterBatiment(Batiment b) {
        if (b != null && !batiments.contains(b)) {
            batiments.add(b);
            System.out.println("Succès : Bâtiment ajouté -> " + b.getNom());
        }
    }

    // Opération : SUPPRIMER
    public boolean supprimerBatiment(String id) {
        return batiments.removeIf(b -> b.getId().equals(id));
    }

    public Batiment clonerBatiment(String id, String nouvelId) {
        for (Batiment b : batiments) {
            if (b.getId().equals(id)) {
                Batiment copie = b.cloner();
                copie.setId(nouvelId); // On lui donne un nouvel ID unique
                batiments.add(copie);
                return copie;
            }
        }
        return null;
    }

    public Batiment trouverBatimentParId(String id) {
        if (id == null) return null;

        return batiments.stream()
                .filter(b -> b.getId().equalsIgnoreCase(id.trim()))
                .findFirst()
                .orElse(null);
    }

    public Batiment getBatiment(String id) {
        return trouverBatimentParId(id);
    }

    public List<Batiment> getTousLesBatiments() {
        return new ArrayList<>(batiments);
    }
}