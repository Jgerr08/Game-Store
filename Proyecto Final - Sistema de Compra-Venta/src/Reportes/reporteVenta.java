package Reportes;

import Database.DBConnection;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;

public class reporteVenta {

    public void generarReporteVenta() {

        String archivo = "reporte_ventas.txt";

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
                ORDER BY v.fecha DESC
                """;

        double totalGeneral = 0;
        int totalVentas = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
             BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {

            writer.write("===== REPORTE GENERAL DE VENTAS =====");
            writer.newLine();
            writer.write("-------------------------------------");
            writer.newLine();

            int ventaActual = -1;

            while (rs.next()) {

                int idVenta = rs.getInt("idVenta");

                if (idVenta != ventaActual) {

                    writer.newLine();
                    writer.write("🧾 Venta ID: " + idVenta);
                    writer.newLine();

                    writer.write("Cliente: " + rs.getString("idCliente"));
                    writer.newLine();

                    writer.write("Fecha: " + rs.getTimestamp("fecha"));
                    writer.newLine();

                    ventaActual = idVenta;
                    totalVentas++;
                }

                double subtotal = rs.getDouble("precio");

                totalGeneral += subtotal;

                writer.write(
                        "   - Juego: " + rs.getString("idJuego") +
                        " | Cantidad: " + rs.getInt("cantidad") +
                        " | Subtotal: $" + subtotal
                );

                writer.newLine();
            }

            writer.newLine();
            writer.write("-------------------------------------");
            writer.newLine();
            writer.write("Total de ventas: " + totalVentas);
            writer.newLine();
            writer.write("Total generado: $" + totalGeneral);
            writer.newLine();

            System.out.println("Reporte generado correctamente en: " + archivo);

        } catch (Exception e) {

            System.out.println("Error en reporte: " + e.getMessage());
        }
    }
}