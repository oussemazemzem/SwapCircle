package com.example.javaproject.Services;

import com.example.javaproject.Entities.Reponse;
import com.example.javaproject.Interfaces.IService;
import com.example.javaproject.Tools.Myconnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReponseService implements IService<Reponse> {
    private final Connection cnx;

    public ReponseService() {
        cnx = Myconnection.getInstance().getCnx();
    }

    @Override
    public void addEntity(Reponse reponse) {
        String query = "INSERT INTO reponse (id_reclamation, id_utilisateur, contenu, date_reponse) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reponse.getIdReclamation());
            pst.setInt(2, reponse.getIdUtilisateur());
            pst.setString(3, reponse.getContenu());
            pst.setTimestamp(4, Timestamp.valueOf(reponse.getDateReponse() != null ? reponse.getDateReponse() : LocalDateTime.now()));

            pst.executeUpdate();

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reponse.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la réponse : " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(Reponse reponse) {
        String query = "UPDATE reponse SET contenu = ?, date_reponse = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, reponse.getContenu());
            pst.setTimestamp(2, Timestamp.valueOf(reponse.getDateReponse()));
            pst.setInt(3, reponse.getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la réponse : " + e.getMessage());
        }
    }

    @Override
    public void deleteEntity(Reponse reponse) {
        String query = "DELETE FROM reponse WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reponse.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réponse : " + e.getMessage());
        }
    }

    @Override
    public List<Reponse> getAllData() {
        List<Reponse> list = new ArrayList<>();
        String query = "SELECT * FROM reponse";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Reponse r = new Reponse();
                r.setId(rs.getInt("id"));
                r.setIdReclamation(rs.getInt("id_reclamation"));
                r.setIdUtilisateur(rs.getInt("id_utilisateur"));
                r.setContenu(rs.getString("contenu"));
                r.setDateReponse(rs.getTimestamp("date_reponse").toLocalDateTime());

                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réponses : " + e.getMessage());
        }

        return list;
    }

    public Reponse findById(int id) {
        String query = "SELECT * FROM reponse WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Reponse r = new Reponse();
                    r.setId(rs.getInt("id"));
                    r.setIdReclamation(rs.getInt("id_reclamation"));
                    r.setIdUtilisateur(rs.getInt("id_utilisateur"));
                    r.setContenu(rs.getString("contenu"));
                    r.setDateReponse(rs.getTimestamp("date_reponse").toLocalDateTime());
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return null;
    }

    public List<Reponse> getReponsesByUtilisateur(int idUtilisateur) {
        List<Reponse> list = new ArrayList<>();
        String query = "SELECT r.*, rec.titre as reclamation_titre, rec.statut as reclamation_statut " +
                "FROM reponse r " +
                "JOIN reclamation rec ON r.id_reclamation = rec.id " +
                "WHERE r.id_utilisateur = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, idUtilisateur);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Reponse r = new Reponse();
                    r.setId(rs.getInt("id_reponse"));
                    r.setIdReclamation(rs.getInt("id_reclamation"));
                    r.setIdUtilisateur(rs.getInt("id_utilisateur"));
                    r.setContenu(rs.getString("contenu"));
                    r.setDateReponse(rs.getTimestamp("date_reponse").toLocalDateTime());
                    r.setReclamationTitre(rs.getString("reclamation_titre"));

                    list.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réponses: " + e.getMessage());
        }

        return list;
    }

    public Reponse findByReclamationId(int reclamationId) {
        String query = "SELECT * FROM reponse WHERE id_reclamation = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reclamationId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Reponse r = new Reponse();
                    r.setId(rs.getInt("id"));
                    r.setIdReclamation(rs.getInt("id_reclamation"));
                    r.setIdUtilisateur(rs.getInt("id_utilisateur"));
                    r.setContenu(rs.getString("contenu"));
                    r.setDateReponse(rs.getTimestamp("date_reponse").toLocalDateTime());
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par réclamation : " + e.getMessage());
        }
        return null;
    }
}