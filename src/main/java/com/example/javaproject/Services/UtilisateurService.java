package com.example.javaproject.Services;

import com.example.javaproject.Entities.Utilisateur;
import com.example.javaproject.Interfaces.IService;
import com.example.javaproject.Tools.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService implements IService<Utilisateur> {
    private final Connection cnx;

    public UtilisateurService() {
        cnx = Myconnection.getInstance().getCnx();
    }

    @Override
    public void addEntity(Utilisateur utilisateur) {
        String query = "INSERT INTO utilisateur(nom, prenom, email, mot_de_passe, role, date_inscription) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, utilisateur.getNom());
            pst.setString(2, utilisateur.getPrenom());
            pst.setString(3, utilisateur.getEmail());
            pst.setString(4, utilisateur.getMotDePasse());
            pst.setString(5, utilisateur.getRole());
            pst.setTimestamp(6, Timestamp.valueOf(utilisateur.getDateInscription()));

            pst.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    utilisateur.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur: " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(Utilisateur utilisateur) {
        String query = "UPDATE utilisateur SET nom=?, prenom=?, email=?, mot_de_passe=?, role=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, utilisateur.getNom());
            pst.setString(2, utilisateur.getPrenom());
            pst.setString(3, utilisateur.getEmail());
            pst.setString(4, utilisateur.getMotDePasse());
            pst.setString(5, utilisateur.getRole());
            pst.setInt(6, utilisateur.getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteEntity(Utilisateur utilisateur) {
        String query = "DELETE FROM utilisateur WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, utilisateur.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Utilisateur> getAllData() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT * FROM utilisateur";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setMotDePasse(rs.getString("mot_de_passe"));
                u.setRole(rs.getString("role"));
                u.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());

                utilisateurs.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        }
        return utilisateurs;
    }

    // Méthodes supplémentaires spécifiques
    public Utilisateur findByEmail(String email) {
        String query = "SELECT * FROM utilisateur WHERE email = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, email);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = new Utilisateur();
                    u.setId(rs.getInt("id"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setEmail(rs.getString("email"));
                    u.setMotDePasse(rs.getString("mot_de_passe"));
                    u.setRole(rs.getString("role"));
                    u.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par email: " + e.getMessage());
        }
        return null;
    }

    public Utilisateur authenticate(String email, String password) {
        String query = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, email);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = new Utilisateur();
                    u.setId(rs.getInt("id"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setEmail(rs.getString("email"));
                    u.setRole(rs.getString("role"));
                    u.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification: " + e.getMessage());
        }
        return null;
    }
    public Utilisateur getById(int id) {
        String query = "SELECT * FROM utilisateur WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = new Utilisateur();
                    u.setId(rs.getInt("id"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setEmail(rs.getString("email"));
                    u.setMotDePasse(rs.getString("mot_de_passe"));
                    u.setRole(rs.getString("role"));
                    u.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par ID: " + e.getMessage());
        }
        return null;
    }
}