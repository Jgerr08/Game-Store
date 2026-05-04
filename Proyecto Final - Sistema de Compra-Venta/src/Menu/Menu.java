package Menu;

import Usuario.Usuario;
import Usuario.Administrador;
import Usuario.Cliente;
import Usuario.Tecnico;
import Venta.Carrito;
import Juegos.Juego;
import Juegos.Consola;
import Juegos.Condicion;
import Inventario.Inventario;
import Pago.Transaccion;
import Pago.PagoEfectivo;
import Pago.PagoElectronico;
import Pago.Estado;
import Banco.Banco;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    private ArrayList<Usuario> usuarios;
    private Usuario sesionActiva;
    private Scanner scanner;

    public Menu(){
        this.usuarios = new ArrayList<>();
        this.sesionActiva = null;
        this.scanner = new Scanner(System.in);
    }

    public void mostrar(){
        int opcion = 0;

        while(opcion != 3){
            System.out.println("Game Store");
            System.out.println("1. Registrarse");
            System.out.println("2. Iniciar sesion");
            System.out.println("3. Salir");
            System.out.println("Opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion){
                case 1: registrar();
                break;
                case 2: iniciarSesion();
                if (sesionActiva != null) {
                    mostrarMenuRol();
                }
                break;
                case 3: System.out.println("Gracias por visitarnos");
                break;
                default: System.out.println("Opcion invalida");
            }
        }
    }

    private void registrar(){
        System.out.println("Registro");
        System.out.println("Ingrese un nombre de usuario: ");
        String id = scanner.nextLine();

        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                System.out.println("Ese nombre de usuario ya existe");
                return;
            }
        }

        System.out.println("Ingrese una contrasena: ");
        String contraseña = scanner.nextLine();

        System.out.println("Seleccione su rol:");
        System.out.println("1. Administrador");
        System.out.println("2. Cliente");
        System.out.println("3. Tecnico");
        System.out.println("Opcion: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        Usuario nuevo = null;
        switch (opcion) {
            case 1: nuevo = new Administrador(id, contraseña); break;
            case 2: nuevo = new Cliente(id, contraseña); break;
            case 3: nuevo = new Tecnico(id, contraseña); break;
            default: System.out.println("Opcion invalida"); return;
        }

        usuarios.add(nuevo);
        System.out.println("Ha sido registrado como: " + nuevo.getClass().getSimpleName());
    }

    private void iniciarSesion(){
        System.out.println("Inicio de Sesion");
        System.out.println("Nombre de usuario: ");
        String id = scanner.nextLine();
        System.out.println("Contraseña: ");
        String contraseña = scanner.nextLine();

        for (Usuario u : usuarios) {
            if (u.getId().equals(id) && u.getContraseña().equals(contraseña)) {
                sesionActiva = u;
                sesionActiva.iniciarSesion();
                return;
            }
        }
        System.out.println("El nombre de usuario o la contraseña es incorrecta");
    }

    private void mostrarMenuRol(){
        if (sesionActiva instanceof Administrador) {
            mostrarMenuAdministrador();
        } else if (sesionActiva instanceof Cliente) {
            mostrarMenuCliente();
        } else if (sesionActiva instanceof Tecnico) {
            mostrarMenuTecnico();
        }
        sesionActiva = null;
    }

    private void mostrarMenuAdministrador(){
        int opcion = 0;
        Inventario inventario = new Inventario();

        while(opcion != 3){
            System.out.println("Menu de Administrador");
            System.out.println("1. Consultar usuarios registrados");
            System.out.println("2. Registrar juego");
            System.out.println("3. Salir");
            System.out.println("Opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion){
                case 1:
                    for (Usuario u : usuarios) {
                        System.out.println(u.getId() + " - " + u.getClass().getSimpleName());
                    }
                    break;
                case 2:
                    registrarJuego(inventario);
                    break;
                case 3:
                    sesionActiva.cerrarSesion();
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }
    }

    private void mostrarMenuCliente(){
        int opcion = 0;
        Carrito carrito = new Carrito();
        Inventario inventario = new Inventario();
        String id;
        Juego juego;

        while(opcion != 3){
            System.out.println("Menu de Cliente");
            System.out.println("1. Agregar juego al carrito");
            System.out.println("2. Ver carrito y pagar");
            System.out.println("3. Salir");
            System.out.println("Opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion){
                case 1:
                    System.out.println("Ingrese el ID del juego: ");
                    id = scanner.nextLine();
                    juego = inventario.buscarPorId(id);

                    if (juego == null) {
                        System.out.println("Juego no encontrado");
                    } else if (juego.getStock() <= 0) {
                        System.out.println("El juego no tiene stock disponible");
                    } else {
                        carrito.agregarJuego(juego);
                        System.out.println(juego.getTitulo() + " agregado al carrito");
                    }
                    break;

                case 2:
                    if (carrito.estaVacio()) {
                        System.out.println("El carrito esta vacio");
                    } else {
                        carrito.mostrarCarrito();
                        pagar(carrito, inventario);
                    }
                    break;

                case 3:
                    sesionActiva.cerrarSesion();
                    break;

                default:
                    System.out.println("Opcion invalida");
            }
        }
    }

    private void pagar(Carrito carrito, Inventario inventario){
        System.out.println("Metodo de Pago");
        System.out.println("1. Pago en efectivo");
        System.out.println("2. Pago electronico");
        System.out.println("Opcion: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        Transaccion transaccion = null;
        Banco banco = null;

        switch (opcion){
            case 1:
                transaccion = new PagoEfectivo(carrito.calcularTotal());
                break;
            case 2:
                banco = new Banco("Banco");
                transaccion = new PagoElectronico(carrito.calcularTotal(), banco);
                break;
            default:
                System.out.println("Opcion invalida");
                return;
        }

        transaccion.realizarPago();

        if (transaccion.getEstado() == Estado.EXITOSO) {
            carrito.reducirStock(inventario);
            carrito.limpiarCarrito();
            System.out.println("Compra finalizada. Gracias por su compra");
        } else {
            System.out.println("El pago no se completo. El carrito sigue activo");
        }
    }

    private void mostrarMenuTecnico(){
        int opcion = 0;
        Inventario inventario = new Inventario();
        String id;
        Juego juego;

        while(opcion != 3){
            System.out.println("Menu de Tecnico");
            System.out.println("1. Buscar juego por ID");
            System.out.println("2. Registrar juego");
            System.out.println("3. Salir");
            System.out.println("Opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion){
                case 1:
                    System.out.println("Ingrese el ID del juego: ");
                    id = scanner.nextLine();
                    juego = inventario.buscarPorId(id);

                    if (juego == null) {
                        System.out.println("Juego no encontrado");
                    } else {
                        System.out.println("Titulo: " + juego.getTitulo());
                        System.out.println("Consola: " + juego.getConsola());
                        System.out.println("Precio: $" + juego.getPrecio());
                        System.out.println("Stock: " + juego.getStock());
                        System.out.println("Condicion: " + juego.getCondicion());
                    }
                    break;

                case 2:
                    registrarJuego(inventario);
                    break;

                case 3:
                    sesionActiva.cerrarSesion();
                    break;

                default:
                    System.out.println("Opcion invalida");
            }
        }
    }

    private void registrarJuego(Inventario inventario){
        System.out.println("Registrar Juego");
        System.out.println("Ingrese el ID del juego: ");
        String id = scanner.nextLine();

        System.out.println("Ingrese el titulo del juego: ");
        String titulo = scanner.nextLine();

        System.out.println("Seleccione la consola:");
        Consola[] consolas = Consola.values();
        for (int i = 0; i < consolas.length; i++) {
            System.out.println((i + 1) + ". " + consolas[i]);
        }
        System.out.println("Opcion: ");
        int opcionConsola = scanner.nextInt();
        scanner.nextLine();
        Consola consola = consolas[opcionConsola - 1];

        System.out.println("Ingrese el precio: ");
        double precio = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("Ingrese el stock: ");
        int stock = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Seleccione la condicion:");
        Condicion[] condiciones = Condicion.values();
        for (int i = 0; i < condiciones.length; i++) {
            System.out.println((i + 1) + ". " + condiciones[i]);
        }
        System.out.println("Opcion: ");
        int opcionCondicion = scanner.nextInt();
        scanner.nextLine();
        Condicion condicion = condiciones[opcionCondicion - 1];

        Juego juego = new Juego(id, titulo, consola, precio, stock, condicion);
        inventario.agregarJuego(juego);
    }
}