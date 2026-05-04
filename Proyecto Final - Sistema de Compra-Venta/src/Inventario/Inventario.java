package Inventario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Database.DBConnection;
import Juegos.Condicion;
import Juegos.Consola;
import Juegos.Juego;

public class Inventario {

    public void agregarJuego(Juego juego) {          
        DBConnection db = new DBConnection();
        db.insertarJuego(juego);
    }

    public Juego buscarPorId(String id) {

        String sql = "SELECT * FROM juegos WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapJuego(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return null;
    }
    public boolean reducirStock(String id, int cantidad) {

        String sql = "UPDATE juegos SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setString(2, id);
            ps.setInt(3, cantidad);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }
    public void aumentarStock(String id, int cantidad) {

        String sql = "UPDATE juegos SET stock = stock + ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setString(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    private Juego mapJuego(ResultSet rs) throws SQLException {

        return new Juego(
            rs.getString("id"),
            rs.getString("titulo"),
            Consola.valueOf(rs.getString("consola")),
            rs.getDouble("precio"),
            rs.getInt("stock"),
            Condicion.valueOf(rs.getString("condicion"))
        );
    }
}
