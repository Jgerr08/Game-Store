package Venta;

import java.util.ArrayList;
import java.util.List;

import Inventario.Inventario;
import Juegos.Juego;

public class Carrito {
    List<Item> items;

    public Carrito() {
        items = new ArrayList<>();
    }

    public void agregarJuego(Juego juego) {
        for (Item item : items) {
            if (item.juego.getID().equals(juego.getID())) {
                item.cantidad++;
                return;
            }
        }
        items.add(new Item(juego));
    }

    public void borrarJuego(Juego juego) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            if (item.juego.getID().equals(juego.getID())) {

                item.cantidad--;

                if (item.cantidad <= 0) {
                    items.remove(i);
                }

                return;
            }
        }
    }

    public double calcularTotal() {
        double total = 0;
        for (Item item : items) {
            total += item.juego.getPrecio() * item.cantidad;
        }
        return total;
    }

    public void mostrarCarrito() {
        double total = 0;
        for (Item item : items) {
            double subtotal = item.juego.getPrecio() * item.cantidad;
            System.out.println(item.juego.getTitulo() + " x" + item.cantidad + " - $" + subtotal);
            total += subtotal;
        }
        System.out.println("----------------------");
        System.out.printf("%-9s $%.2f%n", "TOTAL:", total);
    }

    public void reducirStock(Inventario inventario) {
        for (Item item : items) {
            inventario.reducirStock(item.juego.getID(), item.cantidad);
        }
    }

    public void limpiarCarrito() {
        items.clear();
    }

    public Item[] getItemsArray() {
    return items.toArray(new Item[0]);
}

    public boolean estaVacio() {
        return items.isEmpty();
    }
}