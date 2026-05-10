package Reportes;

import Database.DBConnection;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

import java.sql.*;

public class reporteVenta {

    public void generarReporteVenta() {

        String archivo = "reporte_ventas.pdf";
        String imageFile = "top_game_store.png";

        String sql = """
                SELECT 
                    v.id,
                    v.fecha,
                    v.idCliente,
                    dv.cantidad,
                    dv.precio,
                    j.titulo AS nombreJuego
                FROM Venta v
                INNER JOIN DetalleVenta dv
                    ON v.id = dv.idVenta
                INNER JOIN Juegos j
                    ON dv.idJuego = j.id
                ORDER BY v.fecha DESC, v.id DESC
                """;

        double totalGeneral = 0;
        int ventasContadas = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            PdfWriter writer = new PdfWriter(archivo);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            ImageData data = ImageDataFactory.create(imageFile);
            Image image = new Image(data);
            image.scaleAbsolute(150, 100f);
            
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            document.add(image);
            document.add(new Paragraph("TOP GAME STORE").setFont(bold));
            document.add(new Paragraph("REPORTE GENERAL DE VENTAS").setFont(bold));

            int ventaActual = -1;
            Table tabla = null;

            while (rs.next()) {

                int idVenta = rs.getInt("id");

                if (idVenta != ventaActual) {

                    if (tabla != null) {
                        document.add(tabla);
                    }

                    document.add(new Paragraph("\nVENTA ID: " + idVenta).setFont(bold));
                    document.add(new Paragraph("Cliente: " + rs.getString("idCliente")).setFont(normal));
                    document.add(new Paragraph("Fecha: " + rs.getTimestamp("fecha")).setFont(normal));

                    tabla = new Table(new float[]{4, 2, 2, 2});
                    tabla.setWidth(UnitValue.createPercentValue(100));

                    tabla.addHeaderCell(new Cell().add(new Paragraph("Juego").setFont(bold)));
                    tabla.addHeaderCell(new Cell().add(new Paragraph("Cantidad").setFont(bold)));
                    tabla.addHeaderCell(new Cell().add(new Paragraph("Precio").setFont(bold)));
                    tabla.addHeaderCell(new Cell().add(new Paragraph("Subtotal").setFont(bold)));

                    ventaActual = idVenta;
                    ventasContadas++;
                }

                String juego = rs.getString("nombreJuego");
                int cantidad = rs.getInt("cantidad");
                double precio = rs.getDouble("precio");

                double subtotal = cantidad * precio;
                totalGeneral += subtotal;

                tabla.addCell(new Cell().add(new Paragraph(juego).setFont(normal)));
                tabla.addCell(new Cell().add(new Paragraph(String.valueOf(cantidad)).setFont(normal)));
                tabla.addCell(new Cell().add(new Paragraph("$" + precio).setFont(normal)));
                tabla.addCell(new Cell().add(new Paragraph("$" + subtotal).setFont(normal)));
            }

            // última tabla
            if (tabla != null) {
                document.add(tabla);
            }

            document.add(new Paragraph("\n-------------------------------------"));
            document.add(new Paragraph("Total de ventas: " + ventasContadas).setFont(bold));
            document.add(new Paragraph("Total generado: $" + totalGeneral).setFont(bold));

            document.close();

            System.out.println("PDF generado correctamente: " + archivo);

        } catch (Exception e) {
            System.out.println("Error en reporte: " + e.getMessage());
        }
    }
}