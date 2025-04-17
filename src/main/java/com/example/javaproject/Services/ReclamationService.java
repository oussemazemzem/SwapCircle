package com.example.javaproject.Services;

import com.example.javaproject.Entities.Reclamation;
import com.example.javaproject.Entities.Reponse;
import com.example.javaproject.Interfaces.IService;
import com.example.javaproject.Tools.Myconnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReclamationService implements IService<Reclamation> {
    private final Connection cnx = Myconnection.getInstance().getCnx();

    @Override
    public void addEntity(Reclamation reclamation) {
        String query = "INSERT INTO reclamation(id_utilisateur, message, statut, type_reclamation, date_reclamation, titre, priorite, categorie) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reclamation.getIdUtilisateur());
            pst.setString(2, reclamation.getMessage());
            pst.setString(3, reclamation.getStatut());
            pst.setString(4, reclamation.getTypeReclamation());
            pst.setTimestamp(5, Timestamp.valueOf(reclamation.getDateReclamation() != null ?
                    reclamation.getDateReclamation() : LocalDateTime.now()));
            pst.setString(6, reclamation.getTitre());
            pst.setString(7, reclamation.getPriorite());
            pst.setString(8, reclamation.getCategorie());

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    reclamation.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(Reclamation reclamation) {
        String query = "UPDATE reclamation SET id_utilisateur=?, message=?, statut=?, type_reclamation=?, date_reclamation=?, titre=?, priorite=?, categorie=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reclamation.getIdUtilisateur());
            pst.setString(2, reclamation.getMessage());
            pst.setString(3, reclamation.getStatut());
            pst.setString(4, reclamation.getTypeReclamation());
            pst.setTimestamp(5, Timestamp.valueOf(reclamation.getDateReclamation()));
            pst.setString(6, reclamation.getTitre());
            pst.setString(7, reclamation.getPriorite());
            pst.setString(8, reclamation.getCategorie());
            pst.setInt(9, reclamation.getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteEntity(Reclamation reclamation) {
        String query = "DELETE FROM reclamation WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reclamation.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Reclamation> getAllData() {
        List<Reclamation> list = new ArrayList<>();
        String query = "SELECT * FROM reclamation";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Reclamation r = new Reclamation();
                r.setId(rs.getInt("id"));
                r.setIdUtilisateur(rs.getInt("id_utilisateur"));
                r.setMessage(rs.getString("message"));
                r.setStatut(rs.getString("statut"));
                r.setTypeReclamation(rs.getString("type_reclamation"));
                r.setDateReclamation(rs.getTimestamp("date_reclamation").toLocalDateTime());
                r.setTitre(rs.getString("titre"));
                r.setPriorite(rs.getString("priorite"));
                r.setCategorie(rs.getString("categorie"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
        }
        return list;
    }

    public Reclamation findById(int id) {
        String query = "SELECT * FROM reclamation WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Reclamation r = new Reclamation();
                    r.setId(rs.getInt("id"));
                    r.setIdUtilisateur(rs.getInt("id_utilisateur"));
                    r.setMessage(rs.getString("message"));
                    r.setStatut(rs.getString("statut"));
                    r.setTypeReclamation(rs.getString("type_reclamation"));
                    r.setDateReclamation(rs.getTimestamp("date_reclamation").toLocalDateTime());
                    r.setTitre(rs.getString("titre"));
                    r.setPriorite(rs.getString("priorite"));
                    r.setCategorie(rs.getString("categorie"));
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return null;
    }

    public void delete(Reclamation reclamation) throws SQLException {
        String query = "DELETE FROM reclamation WHERE id = ?";
        try (Connection conn = Myconnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, reclamation.getId());
            pstmt.executeUpdate();
            System.out.println("Reclamation with ID " + reclamation.getId() + " deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error while deleting reclamation: " + e.getMessage());
            throw e;
        }
    }
    public List<Reclamation> getAllWithResponses() {
        List<Reclamation> reclamations = getAllData();
        ReponseService reponseService = new ReponseService();

        // Pour chaque réclamation, chercher la réponse associée
        return reclamations.stream().map(reclamation -> {
            Reponse reponse = reponseService.findByReclamationId(reclamation.getId());
            reclamation.setReponse(reponse);
            return reclamation;
        }).collect(Collectors.toList());
    }

    // Méthode pour vérifier si une réclamation a une réponse
    public boolean hasReponse(int reclamationId) {
        String query = "SELECT COUNT(*) FROM reponse WHERE id_reclamation = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reclamationId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de réponse : " + e.getMessage());
        }
        return false;
    }

    // Méthode pour mettre à jour le statut après réponse
    public void updateStatutAfterReponse(int reclamationId) {
        String query = "UPDATE reclamation SET statut = 'Approuvée' WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reclamationId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
        }
    }

    // Méthode pour obtenir les réclamations sans réponse
    public List<Reclamation> getReclamationsWithoutReponse() {
        return getAllWithResponses().stream()
                .filter(r -> r.getReponse() == null)
                .collect(Collectors.toList());
    }

    // Méthode pour obtenir les réclamations avec réponse
    public List<Reclamation> getReclamationsWithReponse() {
        return getAllWithResponses().stream()
                .filter(r -> r.getReponse() != null)
                .collect(Collectors.toList());
    }
}
