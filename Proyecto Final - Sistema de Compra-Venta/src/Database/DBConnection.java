package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Juegos.Juego;


public class DBConnection {
        // Configuración de la conexión
    private static final String USUARIO = "usuario_java"; // El usuario que creaste
    private static final String PASSWORD = "1234"; // Su contraseña

    // localhost:1433 es la dirección estándar
    private static final String URL =
    "jdbc:sqlserver://localhost:1433;"
    + "databaseName=GameStore;"
    + "encrypt=true;"
    + "trustServerCertificate=true;";
    // Método para obtener la conexión
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

   // FUNCIÓN PARA INSERTAR
    public void insertarJuego(Juego juego) {
        String sql = "INSERT INTO Juegos (id, titulo, consola, precio, stock, condicion) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = getConnection(); 
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setString(1, juego.getID());
            preparedStatement.setString(2, juego.getTitulo());
            preparedStatement.setString(3, juego.getConsola().name());
            preparedStatement.setDouble(4, juego.getPrecio());
            preparedStatement.setInt(5, juego.getStock());
            preparedStatement.setString(6, juego.getCondicion().name());
            
            preparedStatement.executeUpdate();
            System.out.println(">>> ¡Éxito! Juego '" + juego.getTitulo() + "' insertado correctamente.");
            
        } catch (SQLException e) {
            System.err.println("!!! Error al insertar: " + e.getMessage());
        }
    }

    // FUNCIÓN PARA LEER Y MOSTRAR JUEGOS
    public void leerJuegos() {

        String sql = "SELECT * FROM juegos";

        try (Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            System.out.println("\n======= REPORTE DE JUEGOS =======");

            while (rs.next()) {

                String id = rs.getString("id");
                String titulo = rs.getString("titulo");
                String consola = rs.getString("consola");
                double precio = rs.getDouble("precio");
                int stock = rs.getInt("stock");
                String condicion = rs.getString("condicion");

                System.out.println(
                    "Código: " + id +
                    " | Título: " + titulo +
                    " | Consola: " + consola +
                    " | Precio: $" + precio +
                    " | Stock: " + stock +
                    " | Condición: " + condicion
                );
            }

            System.out.println("=================================\n");

        } catch (SQLException e) {
            System.err.println("Error al leer juegos: " + e.getMessage());
        }
    }
}
