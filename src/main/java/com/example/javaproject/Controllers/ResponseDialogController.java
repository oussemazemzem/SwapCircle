package com.example.javaproject.Controllers;

import com.example.javaproject.Entities.Reclamation;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;

public class ResponseDialogController {
    public ImageView userAvatar;
    @FXML private Label userNom;
    @FXML private Label userPrenom;
    @FXML private Label userEmail;
    @FXML private TextArea responseTextArea;
    @FXML private VBox reclamationDetailsContainer;

    private ListeReclamationsBackofficeController mainController;
    private Reclamation currentReclamation;

    public void setMainController(ListeReclamationsBackofficeController controller) {
        this.mainController = controller;
    }

    public void setCurrentReclamation(Reclamation reclamation) {
        this.currentReclamation = reclamation;
    }

    public void setUserDetails(String nom, String prenom, String email) {
        userNom.setText(nom);
        userPrenom.setText(prenom);
        userEmail.setText(email);
    }

    public void displayReclamationDetails(Reclamation reclamation) {
        reclamationDetailsContainer.getChildren().clear();

        addDetailLine("ID", String.valueOf(reclamation.getId()));
        addDetailLine("Titre", reclamation.getTitre());
        addDetailLine("Type", reclamation.getTypeReclamation());
        addDetailLine("Priorité", reclamation.getPriorite());
        addDetailLine("Catégorie", reclamation.getCategorie());
        addDetailLine("Statut", reclamation.getStatut());
        addDetailLine("Date", reclamation.getDateReclamation().toString());

        // Message avec style spécial
        Label messageTitle = new Label("Message:");
        messageTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #4c51bf; -fx-padding: 5px 0 0 0;");

        Label messageContent = new Label(reclamation.getMessage());
        messageContent.setWrapText(true);
        messageContent.setStyle("-fx-padding: 0 0 0 15px;");

        reclamationDetailsContainer.getChildren().addAll(
                messageTitle,
                messageContent
        );
    }

    private void addDetailLine(String label, String value) {
        HBox line = new HBox(10);
        Label titleLabel = new Label(label + ":");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 100px;");

        Label valueLabel = new Label(value);
        valueLabel.setWrapText(true);

        line.getChildren().addAll(titleLabel, valueLabel);
        reclamationDetailsContainer.getChildren().add(line);
    }

    private void addDetailItem(String title, String value) {
        HBox itemContainer = new HBox(5);
        Label titleLabel = new Label(title + " :");
        titleLabel.getStyleClass().add("reclamation-detail-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("reclamation-detail-value");

        itemContainer.getChildren().addAll(titleLabel, valueLabel);
        reclamationDetailsContainer.getChildren().add(itemContainer);
    }

    public String getResponse() {
        return responseTextArea.getText();
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String responseContent = getResponse();
        if (responseContent == null || responseContent.trim().isEmpty()) {
            mainController.showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez saisir une réponse valide");
            return;
        }
        mainController.handleSaveResponse(currentReclamation, responseContent);
        mainController.returnToListView();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        mainController.returnToListView();
    }
}