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
                System.out.println(
                    "Reduciendo stock -> Juego: " +
                    item.getJuego().getID() +
                    " | Cantidad: " + item.getCantidad()
                    );
            }

            ps.executeBatch();

        } catch (SQLException e) {
            System.err.println("Error al guardar detalle de venta: " + e.getMessage());
        }
    }

    public void mostrarVentas() {

        String sql = """
                SELECT
                    v.idVenta,
                    v.fecha,
                    v.idCliente,
                    dv.idJuego,
                    dv.cantidad,
                    dv.precio
                FROM Ventas v
                INNER JOIN DetalleVenta dv
                    ON v.idVenta = dv.idVenta
                ORDER BY v.fecha DESC
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== HISTORIAL DE VENTAS =====");

            boolean hayVentas = false;
            int ventaActual = -1;

            while (rs.next()) {

                hayVentas = true;

                int idVenta = rs.getInt("idVenta");

                if (idVenta != ventaActual) {

                    System.out.println("\n-----------------------------------");
                    System.out.println("VENTA ID: " + idVenta);
                    System.out.println("Cliente: " + rs.getString("idCliente"));
                    System.out.println("Fecha: " + rs.getTimestamp("fecha"));
                    System.out.println("-----------------------------------");

                    ventaActual = idVenta;
                }

                double subtotal = rs.getInt("cantidad") * rs.getDouble("precio");

                System.out.println(
                        "Juego: " + rs.getString("idJuego") +
                                " | Cantidad: " + rs.getInt("cantidad") +
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
