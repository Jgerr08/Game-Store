package Reportes;

import Database.DBConnection;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class reporteDia {

    public void generarReporteDia() {

        String archivo = "reporte_dia.pdf";
        String imageFile = "top_game_store.png";
        LocalDate hoy = LocalDate.now();

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
                WHERE CAST(v.fecha AS DATE) = ?
                ORDER BY v.id
                """;

        double totalDia = 0;
        Set<Integer> ventasUnicas = new HashSet<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(hoy));
            ResultSet rs = ps.executeQuery();

            PdfWriter writer = new PdfWriter(archivo);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            ImageData data = ImageDataFactory.create(imageFile);
            Image image = new Image(data);
            image.scaleAbsolute(150, 100f);
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            document.add(image);
            document.add(new Paragraph("REPORTE DEL DÍA")
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(hoy.toString())
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(" "));

            int ventaActual = -1;
            Table tabla = null;

            while (rs.next()) {

                int idVenta = rs.getInt("id");
                ventasUnicas.add(idVenta);

                if (idVenta != ventaActual) {

                    if (tabla != null) {
                        document.add(tabla);
                    }

                    document.add(new Paragraph("\nVENTA #" + idVenta)
                            .setFont(bold));

                    document.add(new Paragraph("Cliente: " + rs.getString("idCliente")));
                    document.add(new Paragraph("Fecha: " + rs.getTimestamp("fecha")));

                    tabla = new Table(new float[]{3, 2, 2, 2});
                    tabla.setWidth(UnitValue.createPercentValue(100));

                    tabla.addHeaderCell("Juego");
                    tabla.addHeaderCell("Cantidad");
                    tabla.addHeaderCell("Precio");
                    tabla.addHeaderCell("Subtotal");

                    ventaActual = idVenta;
                }

                String juego = rs.getString("nombreJuego");
                int cantidad = rs.getInt("cantidad");
                double precio = rs.getDouble("precio");
                double subtotal = cantidad * precio;

                totalDia += subtotal;

                tabla.addCell(juego);
                tabla.addCell(String.valueOf(cantidad));
                tabla.addCell("$" + precio);
                tabla.addCell("$" + subtotal);
            }

            if (tabla != null) {
                document.add(tabla);
            }

            document.add(new Paragraph("\n=============================="));
            document.add(new Paragraph("Ventas totales: " + ventasUnicas.size()).setFont(bold));
            document.add(new Paragraph("Total del día: $" + totalDia).setFont(bold));

            document.close();

            System.out.println("PDF generado correctamente: " + archivo);

        } catch (Exception e) {
            System.out.println("Error reporte: " + e.getMessage());
        }
    }
}