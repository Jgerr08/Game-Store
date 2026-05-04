package Venta;

import Usuario.Cliente;
import java.util.Date;

public class Venta {

    private Cliente cliente;
    private Item[] items;
    private double total;
    private Date fecha;

    public Venta(Cliente cliente, Item[] items, double total) {
        this.cliente = cliente;
        this.items = items;
        this.total = total;
        this.fecha = new Date();
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Item[] getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public Date getFecha() {
        return fecha;
    }

    public void mostrarVenta() {
        System.out.println("================================");
        System.out.println("         VENTA FINAL           ");
        System.out.println("================================");
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