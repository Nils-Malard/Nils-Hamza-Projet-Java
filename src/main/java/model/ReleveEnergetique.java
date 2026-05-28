package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReleveEnergetique {
    private String idReleve;
    private LocalDate date;
    private LocalTime heure;
    private TypeEnergie typeEnergie;
    private double quantiteConsommee;
    private double valeurFinanciere;

    public ReleveEnergetique(String idReleve, LocalDate date, LocalTime heure,
                             TypeEnergie typeEnergie, double quantiteConsommee, double valeurFinanciere) {
        this.idReleve = idReleve;
        this.date = date;
        this.heure = heure;
        this.typeEnergie = typeEnergie;
        this.quantiteConsommee = quantiteConsommee;
        this.valeurFinanciere = valeurFinanciere;
    }


    public String getIdReleve() {
        return idReleve;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public TypeEnergie getTypeEnergie() {
        return typeEnergie;
    }

    public double getQuantiteConsommee() {
        return quantiteConsommee;
    }

    public double getValeurFinanciere() {
        return valeurFinanciere;
    }

    // --- SETTERS ---

    public void setIdReleve(String idReleve) {
        this.idReleve = idReleve;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public void setTypeEnergie(TypeEnergie typeEnergie) {
        this.typeEnergie = typeEnergie;
    }

    public void setQuantiteConsommee(double quantiteConsommee) {
        this.quantiteConsommee = quantiteConsommee;
    }

    public void setValeurFinanciere(double valeurFinanciere) {
        this.valeurFinanciere = valeurFinanciere;
    }
}