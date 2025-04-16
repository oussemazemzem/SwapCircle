package com.example.javaproject.Controllers;

import com.example.javaproject.Entities.Reclamation;
import com.example.javaproject.Tools.Myconnection;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AjouterReclamation {

    @FXML private TextField titreField;
    @FXML private TextArea messageField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> prioriteComboBox;
    @FXML private ComboBox<String> categorieComboBox;

    @FXML private Label titreErrorLabel;
    @FXML private Label messageErrorLabel;
    @FXML private Label typeErrorLabel;
    @FXML private Label prioriteErrorLabel;
    @FXML private Label categorieErrorLabel;
    @FXML private Pane backgroundPane;

    @FXML
    public void initialize() {
        // Initialisation des ComboBox
        typeComboBox.getItems().addAll("Problème technique", "Service client", "Autre");
        prioriteComboBox.getItems().addAll("Haute", "Moyenne", "Basse");
        categorieComboBox.getItems().addAll("Général", "Urgent", "Non urgent");

        // Masquer les labels d'erreur au démarrage
        clearErrorLabels();

        // Initialiser l'arrière-plan animé
        initAnimatedBackground();
    }

    private void initAnimatedBackground() {
        // Vérifier que le backgroundPane est initialisé
        if (backgroundPane == null) return;

        final int circleCount = 10;  // Augmenté légèrement pour un meilleur effet
        final double minRadius = 40; // Taille minimale augmentée
        final double maxRadius = 120; // Taille maximale augmentée

        // Obtenir les dimensions du pane
        double paneWidth = backgroundPane.getWidth() > 0 ? backgroundPane.getWidth() : 1200;
        double paneHeight = backgroundPane.getHeight() > 0 ? backgroundPane.getHeight() : 700;

        for (int i = 0; i < circleCount; i++) {
            Circle circle = new Circle();
            circle.getStyleClass().add("animated-circle");

            // Position et taille aléatoires
            double radius = minRadius + Math.random() * (maxRadius - minRadius);
            double centerX = Math.random() * paneWidth;
            double centerY = Math.random() * paneHeight;

            circle.setRadius(radius);
            circle.setCenterX(centerX);
            circle.setCenterY(centerY);

            // Animation de pulsation plus visible
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2 + Math.random() * 2), circle);
            scaleTransition.setFromX(1);
            scaleTransition.setFromY(1);
            scaleTransition.setToX(1.5);  // Zoom plus important (50%)
            scaleTransition.setToY(1.5);
            scaleTransition.setAutoReverse(true);
            scaleTransition.setCycleCount(Animation.INDEFINITE);

            // Animation de déplacement améliorée
            TranslateTransition translateTransition = new TranslateTransition(
                    Duration.seconds(12 + Math.random() * 10), circle);

            // Déplacement plus ample
            double moveX = (Math.random() - 0.5) * paneWidth * 0.4;
            double moveY = (Math.random() - 0.5) * paneHeight * 0.4;

            translateTransition.setByX(moveX);
            translateTransition.setByY(moveY);
            translateTransition.setAutoReverse(true);
            translateTransition.setCycleCount(Animation.INDEFINITE);
            translateTransition.setInterpolator(Interpolator.EASE_BOTH);

            // Démarrer les animations
            new ParallelTransition(circle, scaleTransition, translateTransition).play();
            backgroundPane.getChildren().add(circle);
        }
    }

    private void clearErrorLabels() {
        titreErrorLabel.setText("");
        messageErrorLabel.setText("");
        typeErrorLabel.setText("");
        prioriteErrorLabel.setText("");
        categorieErrorLabel.setText("");
    }

    @FXML
    private void handleAjouterButton(ActionEvent event) {
        // Valider tous les champs avant soumission
        boolean isFormValid = validateForm();

        if (!isFormValid) {
            return;
        }

        String titre = titreField.getText();
        String message = messageField.getText();
        String typeReclamation = typeComboBox.getValue();
        String priorite = prioriteComboBox.getValue();
        String categorie = categorieComboBox.getValue();

        // Création et enregistrement de la réclamation
        Reclamation reclamation = new Reclamation();
        reclamation.setIdUtilisateur(1);
        reclamation.setTitre(titre);
        reclamation.setMessage(message);
        reclamation.setTypeReclamation(typeReclamation);
        reclamation.setPriorite(priorite);
        reclamation.setCategorie(categorie);
        reclamation.setStatut("En attente");
        reclamation.setDateReclamation(LocalDateTime.now());

        try (Connection connection = Myconnection.getInstance().getCnx()) {
            String sql = "INSERT INTO reclamation (id_utilisateur, titre, message, type_reclamation, statut, date_reclamation, priorite, categorie) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, reclamation.getIdUtilisateur());
                stmt.setString(2, reclamation.getTitre());
                stmt.setString(3, reclamation.getMessage());
                stmt.setString(4, reclamation.getTypeReclamation());
                stmt.setString(5, reclamation.getStatut());
                stmt.setObject(6, reclamation.getDateReclamation());
                stmt.setString(7, reclamation.getPriorite());
                stmt.setString(8, reclamation.getCategorie());

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    Alert successAlert = createSuccessAlert("Succès", "Réclamation ajoutée avec succès !");
                    successAlert.showAndWait();
                    clearForm();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Échec de l'enregistrement: " + e.getMessage());
        }
    }

    private Alert createSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Création de l'icône verte ✓
        Text checkIcon = new Text("✓");
        checkIcon.setFont(Font.font("System", FontWeight.BOLD, 24));
        checkIcon.setFill(Color.GREEN);

        // Appliquer l'icône
        alert.setGraphic(checkIcon);

        // Style optionnel pour renforcer le visuel de validation
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #f8fff8;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #006400;");

        return alert;
    }

    private void clearForm() {
        titreField.clear();
        messageField.clear();
        typeComboBox.setValue(null);
        prioriteComboBox.setValue(null);
        categorieComboBox.setValue(null);
        clearErrorLabels();
    }

    @FXML
    private void handleListeButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javaproject/liste_reclamations.fxml"));
            Parent root = loader.load();

            Scene currentScene = ((Node)event.getSource()).getScene();
            Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(newScene);
            stage.setTitle("Liste des Réclamations");
            stage.sizeToScene();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page des réclamations.");
        }
    }

    // Validation des champs texte (en temps réel)
    @FXML
    private void validateTitre() {
        String titre = titreField.getText();
        if (titre.isEmpty()) {
            setErrorStyle(titreField, titreErrorLabel, "Le titre est obligatoire");
        } else if (titre.length() < 5) {
            setWarningStyle(titreField, titreErrorLabel, "Minimum 5 caractères");
        } else {
            setValidStyle(titreField, titreErrorLabel);
        }
    }

    @FXML
    private void validateMessage() {
        String message = messageField.getText();
        if (message.isEmpty()) {
            setErrorStyle(messageField, messageErrorLabel, "Le message est obligatoire");
        } else if (message.length() < 10) {
            setWarningStyle(messageField, messageErrorLabel, "Minimum 10 caractères");
        } else {
            setValidStyle(messageField, messageErrorLabel);
        }
    }

    // Validation des ComboBox (seulement à la soumission)
    private void validateType() {
        if (typeComboBox.getValue() == null) {
            setErrorStyle(typeComboBox, typeErrorLabel, "Veuillez sélectionner un type");
        } else {
            setValidStyle(typeComboBox, typeErrorLabel);
        }
    }

    private void validatePriorite() {
        if (prioriteComboBox.getValue() == null) {
            setErrorStyle(prioriteComboBox, prioriteErrorLabel, "Veuillez sélectionner une priorité");
        } else {
            setValidStyle(prioriteComboBox, prioriteErrorLabel);
        }
    }

    private void validateCategorie() {
        if (categorieComboBox.getValue() == null) {
            setErrorStyle(categorieComboBox, categorieErrorLabel, "Veuillez sélectionner une catégorie");
        } else {
            setValidStyle(categorieComboBox, categorieErrorLabel);
        }
    }

    // Méthodes utilitaires pour le style
    private void setErrorStyle(Control control, Label errorLabel, String message) {
        control.setStyle("-fx-border-color: #f44336; -fx-border-width: 2px;");
        errorLabel.setText(message);
    }

    private void setWarningStyle(Control control, Label errorLabel, String message) {
        control.setStyle("-fx-border-color: #FF9800; -fx-border-width: 2px;");
        errorLabel.setText(message);
    }

    private void setValidStyle(Control control, Label errorLabel) {
        control.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px;");
        errorLabel.setText("");
    }

    private boolean validateForm() {
        // Valider les champs texte
        validateTitre();
        validateMessage();

        // Valider les ComboBox seulement à la soumission
        validateType();
        validatePriorite();
        validateCategorie();

        // Vérifier si tous les champs sont valides
        boolean isTitreValid = !titreField.getText().isEmpty() && titreField.getText().length() >= 5;
        boolean isMessageValid = !messageField.getText().isEmpty() && messageField.getText().length() >= 10;
        boolean isTypeValid = typeComboBox.getValue() != null;
        boolean isPrioriteValid = prioriteComboBox.getValue() != null;
        boolean isCategorieValid = categorieComboBox.getValue() != null;

        return isTitreValid && isMessageValid && isTypeValid && isPrioriteValid && isCategorieValid;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}