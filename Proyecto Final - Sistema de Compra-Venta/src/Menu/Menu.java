package Menu;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import Banco.Tarjeta;
import DAO.UsuarioDAO;
import DAO.VentaDAO;
import Database.DBConnection;
import Inventario.Inventario;
import Juegos.Condicion;
import Juegos.Consola;
import Juegos.Juego;
import Reportes.reporteDia;
import Reportes.reporteVenta;
import Usuario.Administrador;
import Usuario.Cliente;
import Usuario.Tecnico;
import Usuario.Usuario;
import Util.Colores;
import Venta.Carrito;
import Venta.Item;
import Venta.Venta;

public class Menu {

    private ArrayList<Usuario> listaUsuarios;
    private Usuario usuarioActivo;
    private Scanner scanner;
    private Inventario inventario;
    private UsuarioDAO usuarioDAO;
    private VentaDAO ventaDAO;
    private Connection conexion;
    reporteDia reporteDia;
    reporteVenta reporteVenta;

    public Menu() {

        this.listaUsuarios = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.inventario = new Inventario();
        this.usuarioDAO = new UsuarioDAO();
        this.ventaDAO = new VentaDAO();
        this.reporteDia = new reporteDia();
        this.reporteVenta = new reporteVenta();
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
            System.out.println("1. Agregar juego al carrito");
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
                }
                case 4 -> {
                    pagar(carrito);
                }

                case 0 -> usuarioActivo.cerrarSesion();

                default -> System.out.println(Colores.ROSA + "Opción inválida" + Colores.RESET);
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

                default -> System.out.println(Colores.ROSA + "Opción inválida" + Colores.RESET);
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
                                        "\n=== INVENTARIO COMPLETO ===" +
                                        Colores.RESET);

                        inventario.mostrarInventario();
                    }

                    case 3 -> {

                        System.out.print("ID: ");
                        String id = scanner.nextLine();

                        Juego juego = inventario.buscarPorId(id);

                        System.out.println(juego != null ? juego.getTitulo() : "No encontrado");

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

                        inventario.aumentarStock(id, cantidad);

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

                        System.out.print("ID del juego: ");
                        String id = scanner.nextLine();

                        System.out.print("Cantidad a reducir: ");
                        int cantidad = Integer.parseInt(scanner.nextLine());

                        inventario.reducirStock(id, cantidad);

                        System.out.println("Stock reducido correctamente");

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

                        List<Usuario> usuarios = usuarioDAO.listar();

                        if (usuarios.isEmpty()) {
                            System.out.println("No hay usuarios");
                            return;
                        }

                        for (Usuario u : usuarios) {
                            System.out.println("ID: " + u.getId());
                        }
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
                        ventaDAO.mostrarVentas();
                    }

                    case 0 -> {
                        System.out.println(
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

        if (carrito.getItemsArray().isEmpty()) {

            System.out.println("Carrito vacío");
            return;
        }

        System.out.println(Colores.CYAN + "\n===== PAGO =====" + Colores.RESET);

        System.out.print("Número de tarjeta: ");
        String numero = scanner.nextLine();

        System.out.print("Titular: ");
        String titular = scanner.nextLine();

        System.out.print("CVV: ");
        String cvv = scanner.nextLine();

        System.out.print("Mes expiración: ");
        int mes = Integer.parseInt(scanner.nextLine());

        System.out.print("Año expiración: ");
        int año = Integer.parseInt(scanner.nextLine());

        Tarjeta tarjeta;

        try {

            tarjeta = new Tarjeta(numero, titular, cvv, mes, año, 10000);

        } catch (Exception e) {

            System.out.println("Error en tarjeta: " + e.getMessage());
            return;
        }

        if (!tarjeta.validarTarjeta()) {

            System.out.println("Tarjeta inválida");
            return;
        }

        double total = carrito.calcularTotal();

        if (!tarjeta.retirarDinero(total)) {

            System.out.println("Fondos insuficientes");
            return;
        }
        Cliente cliente = (Cliente) usuarioActivo;

        Venta venta = new Venta(
                cliente,
                carrito.getItemsArray(),
                total

        );

        int idVenta = ventaDAO.registrarVenta(venta);

        if (idVenta == -1) {

            System.out.println("Error al registrar venta");
            return;
        }

        for (Item item : carrito.getItemsArray()) {

            inventario.reducirStock(
                    item.getJuego().getID(),
                    item.getCantidad());
        }

        reporteVenta.generarReporteVenta();

        reporteDia.generarReporteDia();

        carrito.limpiarCarrito();

        System.out.println(Colores.VERDE + "Pago exitoso" + Colores.RESET);
        System.out.println("ID Venta: " + idVenta);
        System.out.println("Total: $" + total);
        System.out.println("Saldo restante: $" + tarjeta.getSaldo());
    }
}