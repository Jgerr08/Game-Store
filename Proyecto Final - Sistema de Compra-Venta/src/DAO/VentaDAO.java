package DAO;

import Database.DBConnection;
import Venta.Venta;
import Venta.Item;

import java.sql.*;
import java.util.List;

public class VentaDAO {

    private JuegoDAO juegoDAO = new JuegoDAO();

    public int registrarVenta(Venta venta) {

        String sql = """
                    INSERT INTO Venta (fecha, idCliente, total)
                    OUTPUT INSERTED.id
                    VALUES (?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(venta.getFecha()));
            ps.setString(2, venta.getCliente().getId());
            ps.setDouble(3, venta.getTotal());

            ResultSet rs = ps.executeQuery();

            int idVenta = -1;

            if (rs.next()) {
                idVenta = rs.getInt(1);
            }

            System.out.println("ID REAL: " + idVenta); // 🔥 DEBUG

            if (idVenta <= 0) {
                throw new SQLException("No se generó ID válido");
            }

            guardarDetalle(connection, idVenta, venta.getItems());

            return idVenta;

        } catch (SQLException e) {
            System.err.println("Error al registrar venta: " + e.getMessage());
        }

        return -1;
    }

    private void guardarDetalle(Connection connection, int idVenta, List<Item> items) {

        if (idVenta <= 0) {
            throw new RuntimeException("ID de venta inválido: " + idVenta);
        }

        String sql = """
                    INSERT INTO DetalleVenta (idVenta, idJuego, cantidad, precio)
                    VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            for (Item item : items) {

                System.out.println("Insertando -> Venta: " + idVenta +
                        " Juego: " + item.getJuego().getID());

                ps.setInt(1, idVenta);
                ps.setString(2, item.getJuego().getID());
                ps.setInt(3, item.getCantidad());
                ps.setDouble(4, item.getSubtotal());

                ps.addBatch();
            }

            ps.executeBatch();

            for (Item item : items) {
                juegoDAO.reducirStock(
                        item.getJuego().getID(),
                        item.getCantidad());
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar detalle de venta: " + e.getMessage());
        }
    }

    public void mostrarVentas() {

        String sql = """
                    SELECT
                        v.id,
                        v.fecha,
                        v.idCliente,
                        dv.idJuego,
                        dv.cantidad,
                        dv.precio
                    FROM Venta v
                    INNER JOIN DetalleVenta dv
                        ON v.id = dv.idVenta
                    ORDER BY v.id DESC
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== HISTORIAL DE VENTAS =====");

            boolean hayVentas = false;
            int ventaActual = -1;

            while (rs.next()) {

                hayVentas = true;

                int idVenta = rs.getInt("id");

                if (idVenta != ventaActual) {

                    System.out.println("\n-----------------------------------");
                    System.out.println("VENTA ID: " + idVenta);
                    System.out.println("Cliente: " + rs.getString("idCliente"));
                    System.out.println("Fecha: " + rs.getTimestamp("fecha"));
                    System.out.println("-----------------------------------");

                    ventaActual = idVenta;
                }

                int cantidad = rs.getInt("cantidad");
                double precio = rs.getDouble("precio");

                double subtotal = cantidad * precio;

                System.out.println(
                        "Juego: " + rs.getString("idJuego") +
                                " | Cantidad: " + cantidad +
                                " | Subtotal: $" + subtotal);
            }

            if (!hayVentas) {
                System.out.println("No hay ventas registradas");
            }

        } catch (SQLException e) {
            System.out.println("Error al mostrar ventas: " + e.getMessage());
        }
    }

}
