package Venta;

import Juegos.Juego;

public class Item {
     Juego juego;
     int cantidad;

    Item(Juego juego) {
       this.juego = juego;
       this.cantidad = 1;
  }
}