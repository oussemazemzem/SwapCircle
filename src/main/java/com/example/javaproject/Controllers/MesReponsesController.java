package com.example.javaproject.Controllers;

import com.example.javaproject.Entities.Reponse;
import com.example.javaproject.Entities.Utilisateur;
import com.example.javaproject.Services.ReponseService;
import com.example.javaproject.Services.UtilisateurService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.geometry.HPos;
import javafx.scene.layout.Region;

import java.time.format.DateTimeFormatter;

public class MesReponsesController {

    @FXML
    private ListView<Reponse> reponsesList;
    private ListeReclamationsBackofficeController mainController;
    private final ReponseService reponseService = new ReponseService();
    private final UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void initialize() {
        configureListCells();

        // Configurer la taille de la ListView
        reponsesList.setPrefHeight(Region.USE_COMPUTED_SIZE);
        reponsesList.setPrefWidth(Region.USE_COMPUTED_SIZE);
    }

    public void setMainController(ListeReclamationsBackofficeController controller) {
        this.mainController = controller;
    }

    private void configureListCells() {
        reponsesList.setCellFactory(param -> new ListCell<Reponse>() {
            private final GridPane rowContainer = new GridPane();
            private final Label dateLabel = new Label();
            private final Label reclamationLabel = new Label();
            private final Label reponseLabel = new Label();
            private final Label utilisateurLabel = new Label(); // Changé de statutLabel à utilisateurLabel

            {
                // Configuration de la grille
                ColumnConstraints col1 = new ColumnConstraints();
                col1.setPercentWidth(15);
                col1.setHalignment(HPos.CENTER);

                ColumnConstraints col2 = new ColumnConstraints();
                col2.setPercentWidth(25);
                col2.setHalignment(HPos.LEFT);

                ColumnConstraints col3 = new ColumnConstraints();
                col3.setPercentWidth(40);
                col3.setHalignment(HPos.LEFT);

                ColumnConstraints col4 = new ColumnConstraints();
                col4.setPercentWidth(20);
                col4.setHalignment(HPos.CENTER);

                rowContainer.getColumnConstraints().addAll(col1, col2, col3, col4);
                rowContainer.setHgap(10);
                rowContainer.setPadding(new Insets(10, 5, 10, 5));

                // Style des composants
                dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2d3748;");
                reclamationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2d3748;");
                reponseLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2d3748;");
                utilisateurLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 2px 8px;");

                // Ajout des composants à la grille
                rowContainer.add(dateLabel, 0, 0);
                rowContainer.add(reclamationLabel, 1, 0);
                rowContainer.add(reponseLabel, 2, 0);
                rowContainer.add(utilisateurLabel, 3, 0);
            }

            @Override
            protected void updateItem(Reponse reponse, boolean empty) {
                super.updateItem(reponse, empty);

                if (empty || reponse == null) {
                    setGraphic(null);
                } else {
                    // Formatage de la date
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    dateLabel.setText(reponse.getDateReponse().format(formatter));

                    reclamationLabel.setText(reponse.getReclamationTitre());
                    reponseLabel.setText(reponse.getContenu());

                    // Récupération du nom de l'utilisateur
                    Utilisateur utilisateur = utilisateurService.getById(reponse.getIdUtilisateur());
                    if (utilisateur != null) {
                        utilisateurLabel.setText(utilisateur.getNom() + " " + utilisateur.getPrenom());
                    } else {
                        utilisateurLabel.setText("Inconnu");
                    }

                    // Style optionnel pour le label utilisateur
                    utilisateurLabel.setStyle(utilisateurLabel.getStyle() + "-fx-text-fill: #4c51bf;");

                    setGraphic(rowContainer);
                }
            }
        });
    }

    public void loadReponses() {
        // Charge les réponses de l'utilisateur connecté (remplacez 1 par l'ID utilisateur)
        reponsesList.setItems(FXCollections.observableArrayList(
                reponseService.getReponsesByUtilisateur(1)
        ));
    }

    @FXML
    private void handleRetourButton() {
        if (mainController != null) {
            mainController.returnToListView();
        }
    }
}