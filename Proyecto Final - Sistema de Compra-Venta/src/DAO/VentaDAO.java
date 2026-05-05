package DAO;

import Database.DBConnection;
import Venta.Venta;
import Venta.Item;

import java.sql.*;
import java.util.List;

public class VentaDAO {

    private JuegoDAO juegoDAO = new JuegoDAO();

    public int registrarVenta(Venta venta) {

        String sql = "INSERT INTO Ventas (fecha, idCliente, total) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1, Timestamp.valueOf(venta.getFecha()));
            ps.setString(2, venta.getCliente().getId());
            ps.setDouble(3, venta.getTotal());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                int idVenta = rs.getInt(1);

                guardarDetalle(connection, idVenta, venta.getItems());

                return idVenta;
            }

        } catch (SQLException e) {
            System.err.println("Error al registrar venta: " + e.getMessage());
        }

        return -1;
    }

    private void guardarDetalle(Connection connection, int idVenta, List<Item> items) {

        String sql = "INSERT INTO DetalleVenta (idVenta, idJuego, cantidad, precio) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            for (Item item : items) {

                ps.setInt(1, idVenta);
                ps.setString(2, item.getJuego().getID());
                ps.setInt(3, item.getCantidad());
                ps.setDouble(4, item.getSubtotal());

                ps.addBatch();

                juegoDAO.reducirStock(item.getJuego().getID(), item.getCantidad());
            }

            ps.executeBatch();

        } catch (SQLException e) {
            System.err.println("Error al guardar detalle de venta: " + e.getMessage());
        }
    }
}