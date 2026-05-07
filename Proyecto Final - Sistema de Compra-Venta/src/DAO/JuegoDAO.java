package DAO;

import Database.DBConnection;
import Juegos.Juego;
import Juegos.Consola;
import Juegos.Condicion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JuegoDAO {

    public void insertar(Juego juego) {

        String sql = "INSERT INTO Juegos (id, titulo, consola, precio, stock, condicion) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, juego.getID());
            ps.setString(2, juego.getTitulo());
            ps.setString(3, juego.getConsola().name());
            ps.setDouble(4, juego.getPrecio());
            ps.setInt(5, juego.getStock());
            ps.setString(6, juego.getCondicion().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar juego: " + e.getMessage());
        }
    }

    public Juego buscarPorId(String id) {

        String sql = "SELECT * FROM Juegos WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapJuego(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar juego: " + e.getMessage());
        }

        return null;
    }

    public List<Juego> listar() {

        List<Juego> lista = new ArrayList<>();

        String sql = "SELECT id, titulo, consola, precio, stock, condicion FROM Juegos";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Juego juego = new Juego(
                        rs.getString("id"),
                        rs.getString("titulo"),
                        Consola.valueOf(rs.getString("consola").toUpperCase()),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        Condicion.valueOf(rs.getString("condicion").toUpperCase()));
                lista.add(juego);
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return lista;
    }

    public boolean reducirStock(String id, int cantidad) {

        String sql = "UPDATE Juegos SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setString(2, id);
            ps.setInt(3, cantidad);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al reducir stock: " + e.getMessage());
        }

        return false;
    }

    public boolean aumentarStock(String id, int cantidad) {

        String sql = "UPDATE Juegos SET stock = stock + ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setString(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al aumentar stock: " + e.getMessage());
        }

        return false;
    }

    public boolean eliminar(String id) {

        String sql = "DELETE FROM Juegos WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar juego: " + e.getMessage());
        }

        return false;
    }

    public boolean modificarPrecio(String id, double nuevoPrecio) {

        String sql = "UPDATE Juegos SET precio = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, nuevoPrecio);
            ps.setString(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println("Error al modificar precio: " + e.getMessage());
        }

        return false;
    }

    private Juego mapJuego(ResultSet rs) throws SQLException {

        return new Juego(
                rs.getString("id"),
                rs.getString("titulo"),
                Consola.valueOf(rs.getString("consola")),
                rs.getDouble("precio"),
                rs.getInt("stock"),
                Condicion.valueOf(rs.getString("condicion")));
    }
}