package Menu;

import java.util.ArrayList;
import java.util.Scanner;

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

    public Menu() {

        this.listaUsuarios = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.inventario = new Inventario();
    }

    public void mostrarMenu() {

        int op;

        do {

            System.out.println(Colores.MORADO + "\n===== GAME STORE =====" + Colores.RESET);
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

    private void registrarUsuario() {

        System.out.println(Colores.MORADO + "\n===== REGISTRO =====" + Colores.RESET);

        System.out.print(Colores.CYAN + "Usuario: " + Colores.RESET);
        String id = scanner.nextLine();

        for (Usuario usuario : listaUsuarios) {
            if (usuario.getId().equals(id)) {
                System.out.println(Colores.ROSA + "Ese usuario ya existe" + Colores.RESET);
                return;
            }
        }

        System.out.print(Colores.CYAN + "Contraseña: " + Colores.RESET);
        String contraseña = scanner.nextLine();

        System.out.println(Colores.MORADO + "\nRol:" + Colores.RESET);
        System.out.println(Colores.VERDE + "1. Cliente" + Colores.RESET);
        System.out.println(Colores.CYAN + "2. Técnico" + Colores.RESET);

        System.out.print(Colores.CYAN + "Seleccione opción: " + Colores.RESET);
        int op = scanner.nextInt();
        scanner.nextLine();

        Usuario nuevoUsuario;

        switch (op) {

            case 1 -> nuevoUsuario = new Cliente(id, contraseña);

            case 2 -> nuevoUsuario = new Tecnico(id, contraseña);

            default -> {
                System.out.println(Colores.ROSA + "Rol inválido" + Colores.RESET);
                return;
            }
        }

        listaUsuarios.add(nuevoUsuario);

        System.out.println(Colores.VERDE + " Registrado como: " +
                nuevoUsuario.getClass().getSimpleName() + Colores.RESET);
    }

    private void iniciarSesion() {

        System.out.println(Colores.MORADO + "\n===== LOGIN =====" + Colores.RESET);

        System.out.print(Colores.CYAN + "Usuario: " + Colores.RESET);
        String id = scanner.nextLine();

        System.out.print(Colores.CYAN + "Contraseña: " + Colores.RESET);
        String contraseña = scanner.nextLine();

        for (Usuario usuario : listaUsuarios) {

            if (usuario.getId().equals(id) &&
                    usuario.getContraseña().equals(contraseña)) {

                usuarioActivo = usuario;

                System.out.println(Colores.VERDE + " Sesión iniciada" + Colores.RESET);

                usuario.iniciarSesion();
                return;
            }
        }

        System.out.println(Colores.ROSA + "Credenciales incorrectas" + Colores.RESET);
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

    private void menuAdministrador() {

        int op;

        do {

            System.out.println(Colores.MORADO + "\n===== PANEL ADMINISTRADOR =====" + Colores.RESET);
            System.out.println("1. Ver usuarios");
            System.out.println("2. Ver inventario completo");
            System.out.println("3. Registrar juego");
            System.out.println("4. Eliminar usuario");
            System.out.println("5. Actualizar stock de juego");
            System.out.println("6. Buscar usuario por ID");
            System.out.println("7. Buscar juego por ID");
            System.out.println("0. Cerrar sesión");

            System.out.print("Opción: ");
            op = scanner.nextInt();
            scanner.nextLine();

            switch (op) {

                case 1 -> {
                    System.out.println(Colores.CYAN + "\n--- USUARIOS ---" + Colores.RESET);

                    for (Usuario usuario : listaUsuarios) {
                        System.out.println(
                                usuario.getId() + " - " +
                                        usuario.getClass().getSimpleName());
                    }
                }
                  case 2 -> {
                    System.out.println(Colores.CYAN + "\n--- INVENTARIO ---" + Colores.RESET);

                    for (Juego juego : inventario.listarJuegos()) {
                        System.out.println(
                                juego.getTitulo() +
                                        " | Stock: " + juego.getStock() +
                                        " | Precio: " + juego.getPrecio());
                    }
                }

                  case 3 -> registrarJuego();

                case 4 -> {
                    System.out.print("ID usuario a eliminar: ");
                    String idUsuario = scanner.nextLine();

                    boolean eliminado = listaUsuarios.removeIf(
                            usuario -> usuario.getId().equals(idUsuario));

                    if (eliminado) {
                        System.out.println(Colores.VERDE + "Usuario eliminado" + Colores.RESET);
                    } else {
                        System.out.println(Colores.ROSA + "Usuario no encontrado" + Colores.RESET);
                    }
                }

                 case 5 -> {
                    System.out.print("Ingrese el ID del juego: ");
                    String idJuego = scanner.nextLine();

                    System.out.print("Nuevo stock: ");
                    int nuevoStock = scanner.nextInt();
                    scanner.nextLine();

                    inventario.aumentarStock(idJuego, nuevoStock);

                    System.out.println(Colores.VERDE + "Stock actualizado" + Colores.RESET);
                }

                case 6 -> {
                    System.out.print("Ingrese el ID del usuario: ");
                    String idUsuario = scanner.nextLine();

                    Usuario usuarioEncontrado = null;

                    for (Usuario usuario : listaUsuarios) {
                        if (usuario.getId().equals(idUsuario)) {
                            usuarioEncontrado = usuario;
                            break;
                        }
                    }

                    System.out.println(usuarioEncontrado != null
                            ? usuarioEncontrado.getId() + " - " + usuarioEncontrado.getClass().getSimpleName()
                            : "No encontrado");
                }

              

                case 7 -> {
                    System.out.print("Ingrese el ID del juego: ");
                    String idJuego = scanner.nextLine();

                    Juego juego = inventario.buscarPorId(idJuego);

                    System.out.println(juego != null
                            ? juego.getTitulo() + " | Stock: " + juego.getStock()
                            : "No encontrado");
                }

                
                case 0 -> {
                    usuarioActivo.cerrarSesion();
                    System.out.println(Colores.ROSA + "Sesión cerrada" + Colores.RESET);
                }

                default -> System.out.println(Colores.ROSA + "Opción inválida" + Colores.RESET);
            }

        } while (op != 0);
    }

    private void menuCliente() {

        Carrito carrito = new Carrito();
        int op;

        do {

            System.out.println(Colores.CYAN + "\n===== CLIENTE =====" + Colores.RESET);
            System.out.println("1. Agregar juego");
            System.out.println("2. Pagar");
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
                        System.out.println(Colores.VERDE + "Agregado" + Colores.RESET);
                    } else {
                        System.out.println(Colores.ROSA + "No disponible" + Colores.RESET);
                    }
                }

                case 2 -> {
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

        System.out.println(Colores.VERDE + "Pago realizado " + Colores.RESET);

        for (Item item : carrito.getItemsArray()) {

            inventario.reducirStock(
                    item.getJuego().getID(),
                    item.getCantidad());
        }

        carrito.limpiarCarrito();
    }
}