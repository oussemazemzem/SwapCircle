package com.example.javaproject.Controllers;

import com.example.javaproject.Entities.Reclamation;
import com.example.javaproject.Entities.Reponse;
import com.example.javaproject.Services.ReclamationService;
import com.example.javaproject.Services.ReponseService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListeReclamationsController implements Initializable {

    @FXML
    private FlowPane cardsContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private ComboBox<String> priorityFilter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            cardsContainer.applyCss();
            cardsContainer.layout();
            loadReclamations();
            Platform.runLater(() -> {
                cardsContainer.applyCss();
                cardsContainer.layout();
            });
        });
    }

    private void initializeFilters() {
        statusFilter.getItems().addAll("Tous", "Ouvert", "En cours", "Résolu", "Fermé");
        priorityFilter.getItems().addAll("Tous", "Haute", "Moyenne", "Basse");

        // Valeurs par défaut
        statusFilter.setValue("Tous");
        priorityFilter.setValue("Tous");

        // Écouteurs pour le filtrage
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterReclamations());
        priorityFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterReclamations());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterReclamations());
    }


    private void loadReclamations() {
        ReclamationService service = new ReclamationService();
        List<Reclamation> reclamations = service.getAllData();
        displayReclamations(reclamations);
    }

    private void displayReclamations(List<Reclamation> reclamations) {
        cardsContainer.getChildren().clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        reclamations.forEach(rec -> {
            VBox card = createReclamationCard(rec, formatter);
            cardsContainer.getChildren().add(card);
        });
    }

    private VBox createReclamationCard(Reclamation rec, DateTimeFormatter formatter) {
        VBox card = new VBox();
        card.getStyleClass().add("reclamation-card");
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setMaxWidth(400);

        // Titre
        Label title = new Label("📝 " + rec.getTitre());
        title.getStyleClass().add("card-header");

        // Contenu
        Label message = createCardField("💬 " + rec.getMessage());
        Label date = createCardField("📅 " + rec.getDateReclamation().format(formatter));
        Label type = createCardField("📂 Type : " + rec.getTypeReclamation());
        Label priority = createCardField("❗ Priorité : " + rec.getPriorite());
        Label status = createCardField("✅ Statut : " + rec.getStatut());
        Label category = createCardField("🏷 Catégorie : " + rec.getCategorie());

        // Boutons
        HBox buttonsContainer = new HBox(10);


        // Bouton Supprimer
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> handleDeleteReclamation(rec, card));

        // Bouton Voir Réponse (conditionnel)
        Button viewResponseButton = new Button("Voir réponse");
        viewResponseButton.getStyleClass().add("view-button");
        viewResponseButton.setVisible("Approuvée".equals(rec.getStatut()));
        viewResponseButton.setOnAction(e -> handleViewResponse(rec));

        buttonsContainer.getChildren().addAll(deleteButton, viewResponseButton);

        card.getChildren().addAll(title, message, date, type, priority, status, category, buttonsContainer);
        return card;
    }

    private void handleDeleteReclamation(Reclamation reclamation, VBox card) {
        try {
            // Confirmation avant suppression
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer la réclamation");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Suppression dans la base de données
                ReclamationService service = new ReclamationService();
                service.delete(reclamation);

                // Suppression de l'interface
                cardsContainer.getChildren().remove(card);

                // Message de succès
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation supprimée avec succès");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression : " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void handleViewResponse(Reclamation reclamation) {
        try {
            ReponseService reponseService = new ReponseService();
            Reponse reponse = reponseService.findByReclamationId(reclamation.getId());

            if (reponse != null) {
                // Création de la fenêtre popup
                Stage popup = new Stage();
                popup.initModality(Modality.APPLICATION_MODAL);
                popup.setTitle("Réponse à votre réclamation");

                // Conteneur principal avec style
                VBox root = new VBox(20);
                root.setPadding(new Insets(25));
                root.setStyle("-fx-background-color: #f8f9fa;");

                // Titre stylisé comme votre thème
                Label titleLabel = new Label("Réponse à votre réclamation");
                titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
                titleLabel.setPadding(new Insets(0, 0, 15, 0));

                // Date de réponse
                HBox dateBox = new HBox(5);
                Label dateIcon = new Label("📅");
                dateIcon.setStyle("-fx-font-size: 16px;");
                Label dateLabel = new Label("Réponse du " +
                        reponse.getDateReponse().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                dateBox.getChildren().addAll(dateIcon, dateLabel);

                // Contenu avec scroll et style de carte
                TextArea contentArea = new TextArea(reponse.getContenu());
                contentArea.setEditable(false);
                contentArea.setWrapText(true);
                contentArea.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 15px;");
                contentArea.setPrefHeight(200);

                // Bouton Fermer avec style cohérent
                Button closeButton = new Button("Fermer");
                closeButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8px 20px; " +
                        "-fx-background-radius: 8px; -fx-border-radius: 8px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);");
                closeButton.setOnAction(e -> popup.close());

                // Effet hover pour le bouton
                closeButton.setOnMouseEntered(e ->
                        closeButton.setStyle(closeButton.getStyle() + "-fx-background-color: #388E3C;"));
                closeButton.setOnMouseExited(e ->
                        closeButton.setStyle(closeButton.getStyle() + "-fx-background-color: #4CAF50;"));

                // Assemblage des composants
                root.getChildren().addAll(titleLabel, dateBox, contentArea, closeButton);
                root.setAlignment(Pos.TOP_CENTER);

                // Configuration de la scène
                Scene scene = new Scene(root, 500, 400);
                popup.setScene(scene);
                popup.setMinWidth(450);
                popup.setMinHeight(350);

                // Empêche la fermeture par la croix système
                popup.setOnCloseRequest(e -> {
                    e.consume();
                    popup.close();
                });

                // Affichage de la popup
                popup.showAndWait();
            } else {
                showAlert(Alert.AlertType.WARNING, "Aucune réponse", "Aucune réponse trouvée pour cette réclamation");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la réponse : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Label createCardField(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("card-field");
        label.setWrapText(true);
        label.setMaxWidth(380);
        return label;
    }

    private void filterReclamations() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = statusFilter.getValue();
        String selectedPriority = priorityFilter.getValue();

        ReclamationService service = new ReclamationService();
        List<Reclamation> filtered = service.getAllData().stream()
                .filter(rec -> searchText.isEmpty() ||
                        rec.getTitre().toLowerCase().contains(searchText) ||
                        rec.getMessage().toLowerCase().contains(searchText))
                .filter(rec -> selectedStatus.equals("Tous") ||
                        rec.getStatut().equals(selectedStatus))
                .filter(rec -> selectedPriority.equals("Tous") ||
                        rec.getPriorite().equals(selectedPriority))
                .toList();

        displayReclamations(filtered);
    }

    private void adjustCardSizes(double viewportWidth) {
        double availableWidth = viewportWidth - 40; // Marges
        int cardsPerRow = Math.max(1, (int) (availableWidth / 400));
        double cardWidth = (availableWidth / cardsPerRow) - 20;

        cardsContainer.getChildren().forEach(node -> {
            node.setStyle("-fx-pref-width: " + cardWidth + "px; -fx-max-width: " + cardWidth + "px;");
        });

        cardsContainer.requestLayout();
    }

    @FXML
    private void handleBackButton(ActionEvent event) {  // Ajoutez le paramètre ActionEvent
        try {
            // 1. Charger le FXML avec le même pattern
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javaproject/AjouterReclamation.fxml"));
            Parent root = loader.load();

            // 2. Obtenir la scène actuelle depuis l'événement
            Scene currentScene = ((Node)event.getSource()).getScene();

            // 3. Créer nouvelle scène avec mêmes dimensions
            Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

            // 4. Obtenir et mettre à jour le Stage
            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(newScene);
            stage.setTitle("Ajouter une Réclamation");
            stage.sizeToScene();

        } catch (IOException e) {
            System.err.println("Erreur de chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }
}