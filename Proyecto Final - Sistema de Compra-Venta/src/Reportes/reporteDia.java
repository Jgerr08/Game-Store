package Reportes;

import Database.DBConnection;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDate;

public class reporteDia {

    public void generarReporteDia() {

        String archivo = "reporte_dia.txt";

        LocalDate hoy = LocalDate.now();

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
                WHERE CAST(v.fecha AS DATE) = ?
                ORDER BY v.id
                """;

        double totalDia = 0;
        int ventas = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {

            ps.setDate(1, Date.valueOf(hoy));

            ResultSet rs = ps.executeQuery();
            
            writer.write("========================================");
            writer.newLine();
            writer.write("           REPORTE DEL DÍA");
            writer.newLine();
            writer.write("           " + hoy);
            writer.newLine();
            writer.write("========================================");
            writer.newLine();

            int ventaActual = -1;

            while (rs.next()) {

                int idVenta = rs.getInt("idVenta");

                if (idVenta != ventaActual) {

                    writer.newLine();
                    writer.write(String.format("VENTA ID: %-10d CLIENTE: %-10s FECHA: %s",
                            idVenta,
                            rs.getString("idCliente"),
                            rs.getTimestamp("fecha")
                    ));

                    writer.newLine();

                    ventaActual = idVenta;
                    ventas++;
                }

                String juego = rs.getString("idJuego");
                int cantidad = rs.getInt("cantidad");
                double precio = rs.getDouble("precio");
                double subtotal = cantidad * precio;

                totalDia += subtotal;

                writer.write(String.format("    %-10s x%-3d  SUBTOTAL: $%-10.2f",
                        juego,
                        cantidad,
                        subtotal
                ));

                writer.newLine();
            }

            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.write(String.format("VENTAS TOTALES: %d", ventas));
            writer.newLine();
            writer.write(String.format("Total del día: $%.2f", totalDia));
            writer.newLine();
            writer.write("========================================");

            System.out.println("Ticket generado correctamente: " + archivo);

        } catch (Exception e) {

            System.out.println("Error reporte: " + e.getMessage());
        }
    }
}