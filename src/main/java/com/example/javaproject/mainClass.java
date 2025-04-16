package com.example.javaproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class mainClass extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javaproject/AjouterReclamation.fxml"));

        AnchorPane root = loader.load();

        // Créer la scène
        Scene scene = new Scene(root);

        // Ajouter le fichier CSS
        scene.getStylesheets().add(getClass().getResource("/com/example/javaproject/styles.CSS").toExternalForm());

        // Configurer la fenêtre
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ajouter une Réclamation");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(800);

        // Afficher
        primaryStage.show();
    }



    public static void main(String[] args) {
        // Lancer l'application JavaFX
        launch(args);
    }
}
