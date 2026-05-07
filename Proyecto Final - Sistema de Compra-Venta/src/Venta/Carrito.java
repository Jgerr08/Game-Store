package Venta;

import java.util.ArrayList;
import java.util.List;

import Juegos.Juego;

public class Carrito {

    private List<Item> items;

    public Carrito() {
        items = new ArrayList<>();
    }

    public void agregarJuego(Juego juego) {

        for (Item item : items) {
            if (item.getJuego().getID().equals(juego.getID())) {
                item.incrementarCantidad();
                return;
            }
        }

        items.add(new Item(juego));
    }

    public void borrarJuego(Juego juego) {

        for (int i = 0; i < items.size(); i++) {

            Item item = items.get(i);

            if (item.getJuego().getID().equals(juego.getID())) {

                item.decrementarCantidad();

                if (item.getCantidad() <= 0) {
                    items.remove(i);
                }

                return;
            }
        }
    }

    public double calcularTotal() {

        double total = 0;

        for (Item item : items) {
            total += item.getJuego().getPrecio() * item.getCantidad();
        }

        return total;
    }

    public void mostrarCarrito() {

        for (Item item : items) {

            System.out.println(
                item.getJuego().getTitulo() +
                " x" + item.getCantidad() +
                " - $" + (item.getJuego().getPrecio() * item.getCantidad())
            );
        }

        System.out.println("----------------------");
        System.out.printf("TOTAL: $%.2f%n", calcularTotal());
    }

    public void limpiarCarrito() {
        items.clear();
    }

    public List<Item> getItemsArray() {
        return items;
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }
}