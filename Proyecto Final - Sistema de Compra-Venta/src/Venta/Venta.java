package Venta;

import Usuario.Cliente;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Venta {
    private int idVenta; 
    private Cliente cliente;
    private List<Item> items;
    private double total;
    private LocalDateTime fecha;

    public Venta(Cliente cliente, List<Item> items, double total) {
        this.cliente = cliente;
        this.items = items;
        this.total = total;
        this.fecha = LocalDateTime.now();
    }

    public int getIDVenta(){
        return idVenta;
    }
    public Cliente getCliente() {
        return cliente;
    }

    public List<Item> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void mostrarVenta() {
        System.out.println("================================");
        System.out.println("         VENTA FINAL           ");
        System.out.println("================================");
        System.out.println("ID de Venta:"+getIDVenta());
        System.out.println("Cliente: " + cliente.getId());
        System.out.println("Fecha: " + fecha);
        System.out.println("--------------------------------");

        for (Item item : items) {
            if (item != null) {
                System.out.println(
                    item.juego.getTitulo() +
                    " x" + item.cantidad +
                    " - $" + (item.juego.getPrecio() * item.cantidad)
                );
            }
        }

        System.out.println("--------------------------------");
        System.out.printf("TOTAL: $%.2f%n", total);
        System.out.println("================================");
    }
}