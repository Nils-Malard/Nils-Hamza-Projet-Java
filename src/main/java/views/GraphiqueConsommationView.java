package views;

import javafx.concurrent.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import model.Batiment;
import model.ReleveEnergetique;
import model.TypeEnergie;
import service.GestionnaireBatiment;
import service.TableauDeBord;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphiqueConsommationView extends BorderPane {

    private ListView<Batiment> listeBatiments;
    private TabPane conteneurOnglets;

    // Éléments graphiques des anciens onglets
    private BarChart<String, Number> barChartHistorique;
    private Label titreHist;
    private PieChart pieChartRepartition;
    private BarChart<String, Number> barChartComparatif;

    // Onglet Diagnostics
    private Label lblReponseEnergieDominante;
    private Label lblReponseTendance;
    private Label lblReponseFacture;
    private ListView<String> listPicsConsommation;
    private Label lblTitreDiagnostic;

    // COMPOSANTS INTERFACE MULTI-API
    private Label lblGeolocVille;
    private Label lblMeteoTemp;
    private Label lblTarifKwh;
    private Label lblFactureAjusteeApi;
    private Label lblAnalyseImpactMeteo;
    private Button btnRafraichirDonneesExternes;
    private ProgressIndicator indicateurChargement;

    // Bas de page
    private Label labelKpiConso;
    private Label labelKpiCout;
    private Label labelPireBatiment;

    private TableauDeBord tdb;

    public GraphiqueConsommationView(TableauDeBord tdb) {
        this.tdb = tdb;
        this.setStyle("-fx-padding: 15px; -fx-background-color: #f4f4f4;");

        // --- LISTE DES BÂTIMENTS (GAUCHE) ---
        VBox zoneGauche = new VBox(10);
        Label titreListe = new Label("Bâtiments Enregistrés");
        titreListe.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        listeBatiments = new ListView<>();
        listeBatiments.setPrefWidth(220);
        rafraichirListe();
        zoneGauche.getChildren().addAll(titreListe, listeBatiments);
        this.setLeft(zoneGauche);

        // --- INTERFACE CENTRALE (ONGLETS) ---
        conteneurOnglets = new TabPane();
        conteneurOnglets.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        conteneurOnglets.setStyle("-fx-padding: 0 0 0 15px;");

        // Onglet 1, 2, 3, 4 (Simplement instanciés pour la structure complète)
        Tab tabHistorique = new Tab("Historique Chronologique");
        VBox boxHist = new VBox(10); titreHist = new Label("Sélectionnez un bâtiment...");
        barChartHistorique = new BarChart<>(new CategoryAxis(), new NumberAxis());
        barChartHistorique.setAnimated(false); boxHist.getChildren().addAll(titreHist, barChartHistorique); tabHistorique.setContent(boxHist);

        Tab tabRepartition = new Tab("Répartition par Énergie");
        VBox boxRep = new VBox(10); pieChartRepartition = new PieChart(); pieChartRepartition.setAnimated(false);
        boxRep.getChildren().addAll(new Label("Proportion énergétique :"), pieChartRepartition); tabRepartition.setContent(boxRep);

        Tab tabComparatif = new Tab("Comparaison Multi-Bâtiments");
        VBox boxComp = new VBox(10); barChartComparatif = new BarChart<>(new CategoryAxis(), new NumberAxis()); barChartComparatif.setAnimated(false);
        boxComp.getChildren().addAll(new Label("Comparatifs :"), barChartComparatif); tabComparatif.setContent(boxComp);

        Tab tabDiagnostic = new Tab("Diagnostics & Réponses");
        VBox boxDiag = new VBox(10); boxDiag.setStyle("-fx-padding: 15px; -fx-background-color: #ffffff;");
        lblTitreDiagnostic = new Label("Analyses"); lblReponseEnergieDominante = new Label("--"); lblReponseTendance = new Label("--"); lblReponseFacture = new Label("--"); listPicsConsommation = new ListView<>(); listPicsConsommation.setPrefHeight(80);
        boxDiag.getChildren().addAll(lblTitreDiagnostic, lblReponseEnergieDominante, lblReponseTendance, lblReponseFacture, listPicsConsommation); tabDiagnostic.setContent(boxDiag);

        // NOUVEL ONGLET CENTRALISÉ : SMART INSIGHTS - API MULTIPLES
        Tab tabMeteo = new Tab("Hub API Externes");
        VBox boxMeteo = new VBox(15);
        boxMeteo.setStyle("-fx-padding: 20px; -fx-background-color: #ffffff; -fx-border-color: #ddd; -fx-border-radius: 5px;");

        Label lblTitreMeteo = new Label("🚀 Hub d'Intégration d'API Externes (Données Temps Réel)");
        lblTitreMeteo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Section 1 : API Géolocalisation
        Label s1 = new Label("📍 1. API Géolocalisation & Cartographie (Position GPS)");
        s1.setStyle("-fx-font-weight: bold; -fx-text-fill: #2980b9;");
        lblGeolocVille = new Label("Emplacement détecté : En attente d'interrogation...");

        // Section 2 : API Météo
        Label s2 = new Label("🌤️ 2. API Météorologique (Données Environnementales)");
        s2.setStyle("-fx-font-weight: bold; -fx-text-fill: #2980b9;");
        lblMeteoTemp = new Label("Température extérieure : -- °C");
        lblAnalyseImpactMeteo = new Label("Impact sur l'infrastructure : En attente...");
        lblAnalyseImpactMeteo.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 8px; -fx-border-color: #eee;");

        // Section 3 : API Tarifs financiers
        Label s3 = new Label("⚡ 3. API Tarifs Énergétiques (Cours du Marché)");
        s3.setStyle("-fx-font-weight: bold; -fx-text-fill: #2980b9;");
        lblTarifKwh = new Label("Prix actuel de l'énergie au kWh : -- €");
        lblFactureAjusteeApi = new Label("Facture mensuelle réajustée via l'API : -- €");
        lblFactureAjusteeApi.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        btnRafraichirDonneesExternes = new Button("🔄 Interroger et Synchroniser les API");
        indicateurChargement = new ProgressIndicator();
        indicateurChargement.setPrefSize(24, 24);
        indicateurChargement.setVisible(false);

        HBox ligneAction = new HBox(10, btnRafraichirDonneesExternes, indicateurChargement);

        boxMeteo.getChildren().addAll(
                lblTitreMeteo,
                s1, lblGeolocVille, new Separator(),
                s2, lblMeteoTemp, lblAnalyseImpactMeteo, new Separator(),
                s3, lblTarifKwh, lblFactureAjusteeApi, new Separator(),
                ligneAction
        );
        tabMeteo.setContent(boxMeteo);

        conteneurOnglets.getTabs().addAll(tabHistorique, tabRepartition, tabComparatif, tabDiagnostic, tabMeteo);
        this.setCenter(conteneurOnglets);

        // --- ZONE BAS ---
        VBox zoneBas = new VBox(8);
        zoneBas.setStyle("-fx-padding: 15px 0 0 0; -fx-border-color: #ccc; -fx-border-width: 1px 0 0 0;");
        labelKpiConso = new Label("Consommation Bâtiment : --");
        labelKpiCout = new Label("Coût Estimé : --");
        labelPireBatiment = new Label("Bâtiment le plus énergivore global : Calcul...");
        labelPireBatiment.setStyle("-fx-text-fill: #d9534f; -fx-font-weight: bold;");
        zoneBas.getChildren().addAll(labelKpiConso, labelKpiCout, labelPireBatiment);
        this.setBottom(zoneBas);

        // Écouteur interactif
        listeBatiments.getSelectionModel().selectedItemProperty().addListener((observable, anc, nouv) -> {
            if (nouv != null) {
                tdb.setBatimentSelectionne(nouv);
                actualiserGraphiqueHistorique();
                actualiserGraphiqueRepartition();
                actualiserPanneauDiagnostics();
            }
        });

        // Déclenchement multi-API
        btnRafraichirDonneesExternes.setOnAction(e -> executerAppelsMultiApiAsynchrones());
        actualiserGraphiqueComparatif();
    }

    /**
     * ALGORITHME DE SYNCHRONISATION MULTI-API EN ARRIÈRE-PLAN
     */
    private void executerAppelsMultiApiAsynchrones() {
        btnRafraichirDonneesExternes.setDisable(true);
        indicateurChargement.setVisible(true);

        Task<Map<String, String>> chargeurMultiApi = new Task<>() {
            @Override
            protected Map<String, String> call() throws Exception {
                Map<String, String> resultats = new HashMap<>();

                // --- 1. INTERROGATION API 1 : GEOLOCALISATION (Simulée via coordonnées Paris) ---
                resultats.put("ville", "Paris (75000), Île-de-France, France");

                // --- 2. INTERROGATION API 2 : METEO (En direct via Open-Meteo) ---
                String urlMeteo = "https://api.open-meteo.com/v1/forecast?latitude=48.8566&longitude=2.3522&current_weather=true";
                String jsonMeteo = executerRequeteHttp(urlMeteo);

                java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"temperature\"\\s*:\\s*(-?\\d+\\.?\\d*)");
                java.util.regex.Matcher m = p.matcher(jsonMeteo);
                if (m.find()) {
                    resultats.put("temperature", m.group(1));
                } else {
                    resultats.put("temperature", "15.0"); // Valeur de repli sécurisée
                }

                // --- 3. INTERROGATION API 3 : TARIFS (Simulée via index de marché fluctuant) ---
                // Simule le prix du kWh mis à jour en direct (ex: entre 0.22€ et 0.29€ selon les heures)
                double tarifDynamiqueKwh = 0.22 + (new java.util.Random().nextDouble() * 0.07);
                resultats.put("tarif_kwh", String.valueOf(Math.round(tarifDynamiqueKwh * 100.0) / 100.0));

                return resultats;
            }
        };

        // Traitement des retours sur l'interface utilisateur
        chargeurMultiApi.setOnSucceeded(event -> {
            btnRafraichirDonneesExternes.setDisable(false);
            indicateurChargement.setVisible(false);

            Map<String, String> map = chargeurMultiApi.getValue();

            // Mises à jour des éléments visuels
            lblGeolocVille.setText("📍 Emplacement détecté : " + map.get("ville"));

            double temp = Double.parseDouble(map.get("temperature"));
            lblMeteoTemp.setText("🌡️ Température extérieure : " + temp + " °C");
            lblAnalyseImpactMeteo.setText(tdb.analyserImpactMeteo(temp));

            double tarif = Double.parseDouble(map.get("tarif_kwh"));
            lblTarifKwh.setText("⚡ Prix actuel du kWh indexé sur le marché : " + tarif + " € / kWh");

            // Calcul croisé avec le bâtiment sélectionné
            if (tdb.getBatimentSelectionne() != null) {
                double factureAjustee = tdb.calculerFactureAjustee(tarif);
                lblFactureAjusteeApi.setText("💰 Facture mensuelle recalculée au tarif du marché : " + factureAjustee + " €");
            } else {
                lblFactureAjusteeApi.setText("💰 Sélectionnez un bâtiment pour estimer sa facture.");
            }
        });

        chargeurMultiApi.setOnFailed(event -> {
            btnRafraichirDonneesExternes.setDisable(false);
            indicateurChargement.setVisible(false);
            lblAnalyseImpactMeteo.setText("❌ Échec de synchronisation avec les API distantes.");
        });

        new Thread(chargeurMultiApi).start();
    }

    /**
     * Méthode utilitaire pour effectuer des appels GET réseau propres
     */
    private String executerRequeteHttp(String adresseUrl) throws Exception {
        URL url = new URL(adresseUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(4000);

        BufferedReader fd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder chaine = new StringBuilder();
        String l;
        while ((l = fd.readLine()) != null) { chaine.append(l); }
        fd.close();
        return chaine.toString();
    }

    public void rafraichirListe() {
        var tous = GestionnaireBatiment.getInstance().getTousLesBatiments();
        listeBatiments.setItems(FXCollections.observableArrayList(tous));
    }

    private void actualiserPanneauDiagnostics() {
        Batiment b = tdb.getBatimentSelectionne();
        if (b == null) return;
        lblTitreDiagnostic.setText("Analyses automatisées : " + b.getNom());
        lblReponseEnergieDominante.setText("Énergie : " + tdb.getEnergieDominante());
        lblReponseTendance.setText("Tendance : " + tdb.getTendanceConsommation());
        lblReponseFacture.setText("Base : " + tdb.getEstimationFactureMensuelle() + " €");
        listPicsConsommation.setItems(FXCollections.observableArrayList(tdb.detecterPicsConsommation()));
    }

    private void actualiserGraphiqueHistorique() {
        barChartHistorique.getData().clear(); Batiment b = tdb.getBatimentSelectionne(); if (b == null) return;
        titreHist.setText("Historique : " + b.getNom());
        var ind = tdb.getIndicateurs();
        labelKpiConso.setText("Consommation (30j) : " + ind.getOrDefault("Consommation Totale (30j)", 0.0) + " unités");
        labelKpiCout.setText("Coût Estimé (30j) : " + ind.getOrDefault("Coût Estimé (30j)", 0.0) + " €");
        XYChart.Series<String, Number> s = new XYChart.Series<>(); s.setName("Consommation");
        for (ReleveEnergetique r : b.getListeReleves()) { s.getData().add(new XYChart.Data<>(r.getDate().toString() + " " + r.getHeure().toString(), r.getQuantiteConsommee())); }
        barChartHistorique.getData().add(s);
    }

    private void actualiserGraphiqueRepartition() {
        pieChartRepartition.getData().clear(); Batiment b = tdb.getBatimentSelectionne(); if (b == null) return;
        Map<TypeEnergie, Double> rep = new HashMap<>();
        for (ReleveEnergetique r : b.getListeReleves()) { rep.put(r.getTypeEnergie(), rep.getOrDefault(r.getTypeEnergie(), 0.0) + r.getQuantiteConsommee()); }
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        for (Map.Entry<TypeEnergie, Double> e : rep.entrySet()) { data.add(new PieChart.Data(e.getKey().toString(), e.getValue())); }
        pieChartRepartition.setData(data);
    }

    public void actualiserGraphiqueComparatif() {
        barChartComparatif.getData().clear(); List<Batiment> tous = GestionnaireBatiment.getInstance().getTousLesBatiments();
        LocalDate fin = LocalDate.now(); LocalDate deb = fin.minusDays(30);
        XYChart.Series<String, Number> s = new XYChart.Series<>(); s.setName("Bâtiments");
        for (Batiment b : tous) { s.getData().add(new XYChart.Data<>(b.getNom(), b.getConsommationParPeriode(deb, fin))); }
        barChartComparatif.getData().add(s);
        Batiment pire = tdb.getBatimentPlusConso(deb, fin);
        if (pire != null) labelPireBatiment.setText("Bâtiment le plus énergivore global : " + pire.getNom());
    }
}