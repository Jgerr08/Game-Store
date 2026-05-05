package Venta;

import Juegos.Juego;

public class Item {
     Juego juego;
     int cantidad;

    Item(Juego juego) {
       this.juego = juego;
       this.cantidad = 1;
  }
   public Juego getJuego() {
        return juego;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void incrementarCantidad() {
        cantidad++;
    }

    public void decrementarCantidad() {
        cantidad--;
    }

    public double getSubtotal() {
        return juego.getPrecio() * cantidad;
    }
}