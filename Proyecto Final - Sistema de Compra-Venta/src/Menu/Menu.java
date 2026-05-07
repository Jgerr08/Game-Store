package Menu;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import DAO.UsuarioDAO;
import DAO.VentaDAO;
import Database.DBConnection;
import Inventario.Inventario;
import Juegos.Condicion;
import Juegos.Consola;
import Juegos.Juego;
import Usuario.Administrador;
import Usuario.Cliente;
import Usuario.Tecnico;
import Usuario.Usuario;
import Util.Colores;
import Venta.Carrito;
import Venta.Item;

public class Menu {

    private ArrayList<Usuario> listaUsuarios;
    private Usuario usuarioActivo;
    private Scanner scanner;
    private Inventario inventario;
    private UsuarioDAO usuarioDAO;
    private VentaDAO ventaDAO;
    private Connection conexion;

    public Menu() {

        this.listaUsuarios = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.inventario = new Inventario();
        this.usuarioDAO = new UsuarioDAO();
        this.ventaDAO = new VentaDAO();
        System.out.println("Conexión exitosa a la BD");
        try {
            conexion = DBConnection.getConnection();

        } catch (SQLException e) {
            System.out.println("Error al conectar con la BD");
            e.printStackTrace();
        }
    }

    public void mostrarMenu() {

        int op;

        do {

            System.out.println(
                    Colores.MORADO
                            + "___________               ________                          _________ __                        \n"
                            +
                            "\\__    ___/___ ______    /  _____/_____    _____   ____    /   _____//  |_  ___________   ____  \n"
                            +
                            "  |    | /  _ \\\\____ \\  /   \\  ___\\__  \\  /     \\_/ __ \\   \\_____  \\\\   __\\/  _ \\_  __ \\_/ __ \\ \n"
                            +
                            "  |    |(  <_> )  |_> > \\    \\_\\  \\/ __ \\|  Y Y  \\  ___/   /        \\|  | (  <_> )  | \\\\/\\  ___/ \n"
                            +
                            "  |____| \\____/|   __/   \\______  (____  /__|_|  /\\___  > /_______  /|__|  \\____/|__|    \\___  >\n"
                            +
                            "               |__|             \\/     \\/      \\/     \\/          \\/                         \\/"
                            +
                            Colores.RESET);
            System.out.println(Colores.CYAN + "1. Registrar usuario" + Colores.RESET);
            System.out.println(Colores.CYAN + "2. Iniciar sesión" + Colores.RESET);
            System.out.println(Colores.ROSA + "0. Salir" + Colores.RESET);

            System.out.print(Colores.CYAN + "Opción: " + Colores.RESET);
            op = scanner.nextInt();
            scanner.nextLine();

            switch (op) {

                case 1 -> registrarUsuario();

                case 2 -> {
                    iniciarSesion();
                    if (usuarioActivo != null) {
                        mostrarMenuRol();
                    }
                }

                case 0 -> System.out.println(Colores.ROSA + "Gracias por visitarnos ¡Vuelva pronto!" + Colores.RESET);

                default -> System.out.println(Colores.ROSA + "Opción inválida" + Colores.RESET);
            }

        } while (op != 0);
    }

    private void mostrarMenuRol() {

        if (usuarioActivo instanceof Administrador) {

            menuAdministrador();

        } else if (usuarioActivo instanceof Cliente) {

            menuCliente();

        } else if (usuarioActivo instanceof Tecnico) {

            menuTecnico();
        }

        usuarioActivo = null;
    }

    private void registrarUsuario() {

        System.out.println(Colores.MORADO + "\n===== REGISTRO =====" + Colores.RESET);

        try {

            System.out.print(Colores.CYAN + "Usuario: " + Colores.RESET);
            String id = scanner.nextLine();

            System.out.print(Colores.CYAN + "Contraseña: " + Colores.RESET);
            String password = scanner.nextLine();

            System.out.println(Colores.MORADO + "\nRol:" + Colores.RESET);
            System.out.println(Colores.VERDE + "1. Cliente" + Colores.RESET);
            System.out.println(Colores.CYAN + "2. Técnico" + Colores.RESET);

            System.out.print(Colores.CYAN + "Seleccione opción: " + Colores.RESET);

            int op = Integer.parseInt(scanner.nextLine());

            String rol;

            switch (op) {

                case 1 -> rol = "CLIENTE";

                case 2 -> rol = "TECNICO";

                default -> {
                    System.out.println(Colores.ROSA + "Rol inválido" + Colores.RESET);
                    return;
                }
            }

            String sql = """
                    INSERT INTO usuarios(id, contraseña, rol)
                    VALUES (?, ?, ?)
                    """;

            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setString(1, id);
            ps.setString(2, password);
            ps.setString(3, rol);

            ps.executeUpdate();

            System.out.println(Colores.VERDE +
                    " Usuario registrado correctamente" +
                    Colores.RESET);

        } catch (SQLServerException e) {

            if (e.getMessage().contains("PRIMARY KEY") ||
                    e.getMessage().contains("UNIQUE")) {

                System.out.println(Colores.ROSA +
                        "Ese usuario ya existe" +
                        Colores.RESET);

            } else {

                System.out.println(Colores.ROSA +
                        "Error SQL" +
                        Colores.RESET);

                e.printStackTrace();
            }

        } catch (Exception e) {

            System.out.println(Colores.ROSA +
                    "Error al registrar usuario" +
                    Colores.RESET);

            e.printStackTrace();
        }
    }

    private void iniciarSesion() {

        System.out.println(Colores.MORADO + "\n===== LOGIN =====" + Colores.RESET);

        System.out.print(Colores.CYAN + "Usuario: " + Colores.RESET);
        String id = scanner.nextLine();

        System.out.print(Colores.CYAN + "Contraseña: " + Colores.RESET);
        String contraseña = scanner.nextLine();


        Usuario usuario = usuarioDAO.login(id, contraseña);

        if (usuario != null) {

            usuarioActivo = usuario;

            System.out.println(Colores.VERDE +
                    " Sesión iniciada" +
                    Colores.RESET);

            mostrarMenuRol();

        } else {

            System.out.println(Colores.ROSA +
                    "Credenciales incorrectas" +
                    Colores.RESET);
        }
    }

    private void menuCliente() {

        Carrito carrito = new Carrito();
        int op;

        do {

            System.out.println(Colores.CYAN + "\n===== CLIENTE =====" + Colores.RESET);
            System.out.println("1. Agregar juego del carrito");
            System.out.println("2. Borrar juego del carrito");
            System.out.println("3. Mostrar carrito");
            System.out.println("4. Pagar");
            System.out.println("0. Salir");

            System.out.print("Opción: ");
            op = scanner.nextInt();
            scanner.nextLine();

            switch (op) {

                case 1 -> {

                    System.out.print("Ingrese el ID del juego: ");
                    String id = scanner.nextLine();

                    Juego juego = inventario.buscarPorId(id);

                    if (juego != null && juego.getStock() > 0) {
                        carrito.agregarJuego(juego);
                        System.out.println(Colores.VERDE + "Agregado al carrito" + Colores.RESET);
                    } else {
                        System.out.println(Colores.ROSA + "No disponible" + Colores.RESET);
                    }
                }
                case 2 -> {
                    System.out.print("Ingrese el ID del juego: ");
                    String id = scanner.nextLine();

                    Juego juego = inventario.buscarPorId(id);
                    if (juego != null) {
                        carrito.borrarJuego(juego);
                        System.out.println(Colores.VERDE + "Juego borrado del carrito" + Colores.RESET);
                    } else {
                        System.out.println(Colores.ROSA + "Juego no encontrado" + Colores.RESET);
                    }
                }
                case 3 -> {
                    carrito.mostrarCarrito();
                    pagar(carrito);
                }

                case 0 -> usuarioActivo.cerrarSesion();
            }

        } while (op != 0);
    }

    private void menuTecnico() {

        int op;

        do {

            System.out.println(Colores.CYAN + "\n===== TECNICO =====" + Colores.RESET);
            System.out.println("1. Buscar juego");
            System.out.println("2. Registrar juego");
            System.out.println("3. Ver inventario");
            System.out.println("0. Salir");

            System.out.print("Opción: ");
            op = scanner.nextInt();
            scanner.nextLine();

            switch (op) {

                case 1 -> {

                    System.out.print("ID: ");
                    String id = scanner.nextLine();

                    Juego juego = inventario.buscarPorId(id);

                    System.out.println(juego != null ? juego.getTitulo() : "No encontrado");
                }

                case 2 -> registrarJuego();

                case 3 -> {
                    for (Juego juego : inventario.listarJuegos()) {
                        System.out.println(juego.getTitulo());
                    }
                }

                case 0 -> usuarioActivo.cerrarSesion();
            }

        } while (op != 0);
    }

    private void menuAdministrador() {

        int op;

        do {

            System.out.println(Colores.MORADO +
                    "\n========== ADMINISTRADOR ==========" +
                    Colores.RESET);

            System.out.println(Colores.VERDE + "1. Registrar juego" + Colores.RESET);
            System.out.println(Colores.VERDE + "2. Ver inventario" + Colores.RESET);
            System.out.println(Colores.VERDE + "3. Buscar juego" + Colores.RESET);
            System.out.println(Colores.VERDE + "4. Eliminar juego" + Colores.RESET);
            System.out.println(Colores.VERDE + "5. Aumentar stock" + Colores.RESET);
            System.out.println(Colores.VERDE + "6. Reducir stock" + Colores.RESET);
            System.out.println(Colores.VERDE + "7. Modificar precio" + Colores.RESET);
            System.out.println(Colores.VERDE + "8. Ver usuarios" + Colores.RESET);
            System.out.println(Colores.VERDE + "9. Eliminar usuario" + Colores.RESET);
            System.out.println(Colores.VERDE + "10. Ver ventas" + Colores.RESET);
            System.out.println(Colores.ROSA + "0. Cerrar sesión" + Colores.RESET);

            System.out.print(Colores.CYAN +
                    "Seleccione opción: " +
                    Colores.RESET);

            try {

                op = Integer.parseInt(scanner.nextLine());

                switch (op) {

                    case 1 -> {

                        System.out.println(
                                Colores.VERDE +
                                        "\n=== REGISTRAR JUEGO ===" +
                                        Colores.RESET);

                        registrarJuego();
                    }

                    case 2 -> {

                        System.out.println(
                                Colores.CYAN +
                                        "\n=== LISTA DE JUEGOS ===" +
                                        Colores.RESET);

                        inventario.listarJuegos();
                    }

                    case 3 -> {

                        System.out.println(
                                Colores.MORADO +
                                        "\n=== INVENTARIO COMPLETO ===" +
                                        Colores.RESET);

                        inventario.mostrarInventario();
                    }

                    case 4 -> {

                        System.out.println(
                                Colores.ROSA +
                                        "\n=== ELIMINAR JUEGO ===" +
                                        Colores.RESET);

                        System.out.print(
                                Colores.CYAN +
                                        "ID del juego: " +
                                        Colores.RESET);

                        String id = scanner.nextLine();

                        Juego juego = inventario.buscarPorId(id);

                        if (juego != null) {

                            inventario.eliminarJuego(juego);

                            System.out.println(
                                    Colores.VERDE +
                                            "Juego eliminado correctamente" +
                                            Colores.RESET);

                        } else {

                            System.out.println(
                                    Colores.ROSA +
                                            "Juego no encontrado" +
                                            Colores.RESET);
                        }
                    }

                    case 5 -> {

                        System.out.println(
                                Colores.VERDE +
                                        "\n=== AUMENTAR STOCK ===" +
                                        Colores.RESET);

                        System.out.print(
                                Colores.CYAN +
                                        "ID del juego: " +
                                        Colores.RESET);

                        String id = scanner.nextLine();

                        System.out.print(
                                Colores.CYAN +
                                        "Cantidad a agregar: " +
                                        Colores.RESET);

                        int cantidad = Integer.parseInt(scanner.nextLine());

                        inventario.aumentarStock(id,cantidad);

                            System.out.println(
                                    Colores.VERDE +
                                            "Stock actualizado correctamente" +
                                            Colores.RESET);

                       
                    }

                    case 6 -> {

                        System.out.println(
                                Colores.ROSA +
                                        "\n=== REDUCIR STOCK ===" +
                                        Colores.RESET);

                        System.out.print(
                                Colores.CYAN +
                                        "ID del juego: " +
                                        Colores.RESET);

                        String id = scanner.nextLine();

                        System.out.print(
                                Colores.CYAN +
                                        "Cantidad a reducir: " +
                                        Colores.RESET);

                        int cantidad = Integer.parseInt(scanner.nextLine());

                        inventario.modificarPrecio(id, cantidad);

                        System.out.println(
                                Colores.VERDE +
                                        "Precio actualizado correctamente" +
                                        Colores.RESET);

                    }

                    case 7 -> {

                        System.out.println(
                                Colores.CYAN +
                                        "\n=== MODIFICAR PRECIO ===" +
                                        Colores.RESET);

                        System.out.print(
                                Colores.CYAN +
                                        "ID del juego: " +
                                        Colores.RESET);

                        String id = scanner.nextLine();

                        System.out.print(
                                Colores.CYAN +
                                        "Nuevo precio: " +
                                        Colores.RESET);

                        double nuevoPrecio = Double.parseDouble(scanner.nextLine());

                        inventario.modificarPrecio(id, nuevoPrecio);

                        System.out.println(
                                Colores.VERDE +
                                        "Precio actualizado correctamente" +
                                        Colores.RESET);

                    }

                    case 8 -> {

                        System.out.println(
                                Colores.MORADO +
                                        "\n=== LISTA DE USUARIOS ===" +
                                        Colores.RESET);

                        inventario.listarJuegos();
                    }

                    case 9 -> {

                        System.out.println(
                                Colores.ROSA +
                                        "\n=== ELIMINAR USUARIO ===" +
                                        Colores.RESET);

                        System.out.print(
                                Colores.CYAN +
                                        "ID del usuario: " +
                                        Colores.RESET);

                        String id = scanner.nextLine();

                        Usuario usuario = usuarioDAO.buscarPorId(id);

                        if (usuario != null) {

                            usuarioDAO.eliminar(usuario.getId());

                            System.out.println(
                                    Colores.VERDE +
                                            "Usuario eliminado correctamente" +
                                            Colores.RESET);

                        } else {

                            System.out.println(
                                    Colores.ROSA +
                                            "Usuario no encontrado" +
                                            Colores.RESET);
                        }
                    }

                    case 10 -> {

                        System.out.println(
                                Colores.VERDE +
                                        "\n=== HISTORIAL DE VENTAS ===" +
                                        Colores.RESET);
                        mostrarVentas();
                    }

                    case 0 -> {System.out.println(
                            Colores.ROSA +
                                    "Cerrando sesión..." +
                                    Colores.RESET);
                    usuarioActivo.cerrarSesion();
                    }

                    default -> System.out.println(
                            Colores.ROSA +
                                    "Opción inválida" +
                                    Colores.RESET);
                }

            } catch (NumberFormatException e) {

                System.out.println(
                        Colores.ROSA +
                                "Ingrese un número válido" +
                                Colores.RESET);

                op = -1;
            }

        } while (op != 0);
    }

    private void registrarJuego() {

        System.out.print("ID: ");
        String id = scanner.nextLine();

        System.out.print("Título: ");
        String titulo = scanner.nextLine();

        System.out.print("Precio: ");
        double precio = scanner.nextDouble();

        System.out.print("Stock: ");
        int stock = scanner.nextInt();
        scanner.nextLine();

        Juego juego = new Juego(id, titulo, Consola.XBOX, precio, stock, Condicion.NUEVO);

        inventario.agregarJuego(juego);

        System.out.println(Colores.VERDE + "Juego registrado" + Colores.RESET);
    }

    private void pagar(Carrito carrito) {

        String archivo = "reporte_ventas.txt";

        String idVenta = java.util.UUID.randomUUID().toString().substring(0, 8);

        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss");

        String fecha = ahora.format(formato);

        try (java.io.BufferedWriter escritor = new java.io.BufferedWriter(new java.io.FileWriter(archivo, true))) {

            escritor.write("===== REPORTE DE VENTA =====");
            escritor.newLine();

            escritor.write("ID Venta: " + idVenta);
            escritor.newLine();

            escritor.write("Fecha: " + fecha);
            escritor.newLine();

            escritor.newLine();

            double total = 0;

            for (Item item : carrito.getItemsArray()) {

                double subtotal = item.getCantidad() * item.getJuego().getPrecio();
                total += subtotal;

                escritor.write(
                        "Juego: " + item.getJuego().getTitulo() +
                                " | Cantidad: " + item.getCantidad() +
                                " | Subtotal: " + subtotal);

                escritor.newLine();

                inventario.reducirStock(
                        item.getJuego().getID(),
                        item.getCantidad());
            }

            escritor.newLine();
            escritor.write("TOTAL DE VENTA: " + total);
            escritor.newLine();
            escritor.write("----------------------------");
            escritor.newLine();
            escritor.newLine();

        } catch (Exception e) {
            System.out.println("Error al guardar venta: " + e.getMessage());
        }

        carrito.limpiarCarrito();
    }

    private void generarReporteDelDia() {

        String archivo = "reporte_ventas.txt";

        java.time.LocalDate hoy = java.time.LocalDate.now();

        System.out.println(Colores.MORADO + "\n===== REPORTE DE VENTAS DEL DÍA =====" + Colores.RESET);
        System.out.println("Fecha: " + hoy);
        System.out.println("----------------------------");

        try (java.io.BufferedReader lector = new java.io.BufferedReader(new java.io.FileReader(archivo))) {

            String linea;
            boolean incluirVenta = false;

            while ((linea = lector.readLine()) != null) {

                if (linea.startsWith("Fecha:")) {

                    String fechaTexto = linea.replace("Fecha:", "").trim();

                    java.time.LocalDate fechaVenta = java.time.LocalDate.parse(fechaTexto.substring(0, 10));

                    incluirVenta = fechaVenta.equals(hoy);
                }

                if (incluirVenta) {
                    System.out.println(linea);
                }

                if (linea.startsWith("----------------------------")) {
                    incluirVenta = false;
                }
            }

        } catch (Exception e) {
            System.out.println("Error al generar reporte: " + e.getMessage());
        }
    }
    private void mostrarVentas() {

    System.out.println(
            Colores.MORADO +
            "\n========== HISTORIAL DE VENTAS ==========" +
            Colores.RESET
    );

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

        boolean hayVentas = false;

        while (rs.next()) {

            hayVentas = true;

            System.out.println(
                    Colores.CYAN +
                    "\nID Venta: " + rs.getInt("idVenta") +
                    "\nCliente: " + rs.getString("idCliente") +
                    "\nJuego: " + rs.getString("idJuego") +
                    "\nCantidad: " + rs.getInt("cantidad") +
                    "\nSubtotal: $" + rs.getDouble("precio") +
                    "\nFecha: " + rs.getTimestamp("fecha") +
                    "\n-----------------------------------" +
                    Colores.RESET
            );
        }

        if (!hayVentas) {

            System.out.println(
                    Colores.ROSA +
                    "No hay ventas registradas" +
                    Colores.RESET
            );
        }

    } catch (SQLException e) {

        System.out.println(
                Colores.ROSA +
                "Error al mostrar ventas: " +
                e.getMessage() +
                Colores.RESET
        );
    }
}
}