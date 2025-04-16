package com.example.javaproject.Controllers;

import com.example.javaproject.Entities.Reclamation;
import com.example.javaproject.Entities.Reponse;
import com.example.javaproject.Entities.Utilisateur;
import com.example.javaproject.Services.ReclamationService;
import com.example.javaproject.Services.ReponseService;
import com.example.javaproject.Services.UtilisateurService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ListeReclamationsBackofficeController {

    @FXML
    private ListView<Reclamation> reclamationsList;

    private final ReclamationService service = new ReclamationService();
    @FXML
    private BorderPane rootPane;

    @FXML
    public void initialize() {
        configureListCells();
        loadReclamations();
    }

    private void configureListCells() {
        reclamationsList.setCellFactory(new Callback<ListView<Reclamation>, ListCell<Reclamation>>() {
            @Override
            public ListCell<Reclamation> call(ListView<Reclamation> param) {
                return new ListCell<Reclamation>() {
                    private final GridPane rowContainer = new GridPane();
                    private final Label dateLabel = new Label();
                    private final Label titleLabel = new Label();
                    private final Label typeLabel = new Label();
                    private final Label priorityLabel = new Label();
                    private final Label statusLabel = new Label();
                    private final Button actionBtn = new Button("Répondre");

                    {
                        // Configuration de la grille (inchangée)
                        ColumnConstraints col1 = new ColumnConstraints();
                        col1.setPercentWidth(10);
                        col1.setHalignment(HPos.CENTER);

                        ColumnConstraints col2 = new ColumnConstraints();
                        col2.setPercentWidth(30);
                        col2.setHalignment(HPos.LEFT);

                        ColumnConstraints col3 = new ColumnConstraints();
                        col3.setPercentWidth(20);
                        col3.setHalignment(HPos.LEFT);

                        ColumnConstraints col4 = new ColumnConstraints();
                        col4.setPercentWidth(15);
                        col4.setHalignment(HPos.CENTER);

                        ColumnConstraints col5 = new ColumnConstraints();
                        col5.setPercentWidth(15);
                        col5.setHalignment(HPos.CENTER);

                        ColumnConstraints col6 = new ColumnConstraints();
                        col6.setPercentWidth(10);
                        col6.setHalignment(HPos.CENTER);

                        rowContainer.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6);
                        rowContainer.setHgap(10);
                        rowContainer.setPadding(new Insets(10, 5, 10, 5)); // Augmentation du padding vertical

                        // Ajout des composants à la grille
                        rowContainer.add(dateLabel, 0, 0);
                        rowContainer.add(titleLabel, 1, 0);
                        rowContainer.add(typeLabel, 2, 0);
                        rowContainer.add(priorityLabel, 3, 0);
                        rowContainer.add(statusLabel, 4, 0);
                        rowContainer.add(actionBtn, 5, 0);

                        // Style des composants
                        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2d3748;");
                        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2d3748;");
                        typeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2d3748;");
                        priorityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2d3748;");
                        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 2px 8px; -fx-background-radius: 10px;");
                        actionBtn.setStyle("-fx-font-size: 13px; -fx-background-color: #4c51bf; -fx-text-fill: white; -fx-font-weight: bold;");

                        // Permet à la ligne de s'étendre
                        GridPane.setHgrow(titleLabel, Priority.ALWAYS);
                    }

                    @Override
                    protected void updateItem(Reclamation reclamation, boolean empty) {
                        super.updateItem(reclamation, empty);

                        if (empty || reclamation == null) {
                            setGraphic(null);
                        } else {
                            // Formatage de la date
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            dateLabel.setText(reclamation.getDateReclamation().format(formatter));

                            titleLabel.setText(reclamation.getTitre());
                            typeLabel.setText(reclamation.getTypeReclamation());
                            priorityLabel.setText(reclamation.getPriorite());
                            statusLabel.setText(reclamation.getStatut());

                            // Appliquer le style de statut
                            String statusStyle = "";
                            switch (reclamation.getStatut().toLowerCase()) {
                                case "en attente":
                                    statusStyle = "-fx-background-color: #fffaf0; -fx-text-fill: #dd6b20;";
                                    break;
                                case "traité":
                                case "traite":
                                    statusStyle = "-fx-background-color: #f0fff4; -fx-text-fill: #38a169;";
                                    break;
                                case "rejeté":
                                case "rejete":
                                    statusStyle = "-fx-background-color: #fff5f5; -fx-text-fill: #e53e3e;";
                                    break;
                                default:
                                    statusStyle = "-fx-background-color: #ebf8ff; -fx-text-fill: #2d3748;";
                            }
                            statusLabel.setStyle(statusLabel.getStyle() + statusStyle);

                            actionBtn.setOnAction(event -> handleResponse(reclamation));

                            setGraphic(rowContainer);
                        }
                    }
                };
            }
        });
    }

    private void loadReclamations() {
        reclamationsList.setItems(FXCollections.observableArrayList(
                service.getAllData()
        ));
    }

    private void handleResponse(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javaproject/ResponseDialog.fxml"));
            Parent responseForm = loader.load();
            ResponseDialogController controller = loader.getController();

            // Passer les références nécessaires
            controller.setMainController(this);
            controller.setCurrentReclamation(reclamation);

            // Récupérer l'utilisateur associé via UtilisateurService
            UtilisateurService utilisateurService = new UtilisateurService();
            Utilisateur utilisateur = utilisateurService.getById(reclamation.getIdUtilisateur());

            if (utilisateur != null) {
                // Afficher les informations réelles de l'utilisateur
                controller.setUserDetails(
                        utilisateur.getNom() ,
                        utilisateur.getPrenom(),
                        utilisateur.getEmail()
                );
            } else {
                // Fallback si utilisateur non trouvé
                controller.setUserDetails(
                        "Utilisateur #" + reclamation.getIdUtilisateur(),
                        "",
                        "non.disponible@example.com"
                );
                showAlert(Alert.AlertType.WARNING, "Avertissement",
                        "L'utilisateur associé à cette réclamation n'a pas été trouvé");
            }

            controller.displayReclamationDetails(reclamation);
            rootPane.setCenter(responseForm);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors du chargement du formulaire de réponse: " + e.getMessage());
        }
    }

    private void showReclamationList() {
        // Recharge la liste des réclamations
        loadReclamations();
        rootPane.setCenter(reclamationsList);
    }

    public void returnToListView() {
        try {
            // Reload the entire list view FXML instead of just the ListView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javaproject/ListeReclamationsBackoffice.fxml"));
            Parent listView = loader.load();
            rootPane.setCenter(listView);

            // Refresh the data
            ListeReclamationsBackofficeController controller = loader.getController();
            controller.loadReclamations();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur lors du chargement de la liste");
        }
    }

    public void handleSaveResponse(Reclamation reclamation, String responseContent) {
        if (responseContent == null || responseContent.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez saisir une réponse valide");
            return;
        }

        try {
            // Créer la nouvelle réponse
            Reponse response = new Reponse();
            response.setIdReclamation(reclamation.getId());
            response.setContenu(responseContent);
            response.setDateReponse(LocalDateTime.now());
            response.setIdUtilisateur(1); // ID de l'admin répondant (à adapter selon votre logique)

            // Sauvegarder via le service
            ReponseService reponseService = new ReponseService();
            reponseService.addEntity(response);

            // Mettre à jour la réclamation
            reclamation.setStatut("Traité");
            reclamation.setReponse(response); // Associer la réponse

            ReclamationService reclamationService = new ReclamationService();
            reclamationService.updateEntity(reclamation);

            // Rafraîchir l'affichage
            loadReclamations();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "La réponse a été enregistrée avec succès");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
    }

    public void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Méthode utilitaire pour afficher des alertes
    public static class AlertUtil {
        public static void showError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

    }
}