module smart.energy.manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http; // <-- INDISPENSABLE pour les requêtes API sur Internet

    exports main;
    exports view;
    exports model;
    exports service;
}