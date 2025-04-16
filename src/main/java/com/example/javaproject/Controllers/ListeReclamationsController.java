package com.example.javaproject.Controllers;

import com.example.javaproject.Entities.Reclamation;
import com.example.javaproject.Services.ReclamationService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
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
            // Forcer le calcul des dimensions
            cardsContainer.applyCss();
            cardsContainer.layout();

            // Charger les données
            loadReclamations();

            // Ajuster à nouveau après le chargement
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

    private void setupContainer() {
        // Configuration responsive
        scrollPane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            adjustCardSizes(newVal.getWidth());
        });

        // Style initial forcé
        cardsContainer.setStyle("-fx-pref-width: 800px;");
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
        Label type = createCardField("📂 " + rec.getTypeReclamation());

        Label priority = createCardField("⚠️ Priorité : " + rec.getPriorite());
        priority.getStyleClass().add("priorite-" + rec.getPriorite().toLowerCase().replace("é", "e"));

        Label status = createCardField("✅ Statut : " + rec.getStatut());
        status.getStyleClass().add("statut-" + rec.getStatut().toLowerCase().replace("é", "e").replace(" ", "-"));

        Label category = createCardField("🏷 " + rec.getCategorie());

        // Bouton Supprimer
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> handleDeleteReclamation(rec, card));

        card.getChildren().addAll(title, message, date, type, priority, status, category, deleteButton);
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

    // Ajoutez cette méthode utilitaire si elle n'existe pas déjà
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String createEmojiLabel(String emoji) {
        return String.format("%s ", emoji); // Ajoute un espace après l'emoji
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

    private void forceLayoutUpdate() {
        adjustCardSizes(scrollPane.getViewportBounds().getWidth());
        cardsContainer.requestLayout();
        Platform.runLater(() -> {
            cardsContainer.layout();
            scrollPane.requestLayout();
        });
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