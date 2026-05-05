package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Database.DBConnection;
import Usuario.*;

public class UsuarioDAO {

    public void insertar(Usuario usuario, String rol) {

        String sql = "INSERT INTO Usuarios (id, contraseña, rol) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getId());
            ps.setString(2, usuario.getContraseña());
            ps.setString(3, rol);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
    }

    public Usuario login(String id, String contraseña) {

        String sql = "SELECT * FROM Usuarios WHERE id = ? AND contraseña = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, contraseña);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapUsuario(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error login: " + e.getMessage());
        }

        return null;
    }

    public Usuario buscarPorId(String id) {

        String sql = "SELECT * FROM Usuarios WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapUsuario(rs);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    public List<Usuario> listar() {

        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT * FROM Usuarios";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return lista;
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {

        String rol = rs.getString("rol");

        return switch (rol.toUpperCase()) {

            case "ADMIN" -> new Administrador(
                    rs.getString("id"),
                    rs.getString("contraseña")
            );

            case "TECNICO" -> new Tecnico(
                    rs.getString("id"),
                    rs.getString("contraseña")
            );

            case "CLIENTE" -> new Cliente(
                    rs.getString("id"),
                    rs.getString("contraseña")
            );

            default -> null;
        };
    }
}