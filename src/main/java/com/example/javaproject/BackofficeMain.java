package com.example.javaproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BackofficeMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/javaproject/ListeReclamationsBackoffice.fxml"));
        primaryStage.setTitle("Gestion des réclamations - Backoffice");
        primaryStage.setScene(new Scene(root, 1100, 700));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}