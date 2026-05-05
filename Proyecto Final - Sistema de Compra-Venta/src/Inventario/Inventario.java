package Inventario;

import java.util.List;

import DAO.JuegoDAO;
import Juegos.Juego;

public class Inventario {
    private JuegoDAO juegoDAO;

    public Inventario() {
        this.juegoDAO = new JuegoDAO();
    }

    public void agregarJuego(Juego juego) {
        juegoDAO.insertar(juego);
    }

    public Juego buscarPorId(String id) {
        return juegoDAO.buscarPorId(id);
    }

    public List<Juego> listarJuegos() {
        return juegoDAO.listar();
    }

    public void reducirStock(String id, int cantidad) {
        juegoDAO.reducirStock(id, cantidad);
    }

    public void aumentarStock(String id, int cantidad) {
        juegoDAO.aumentarStock(id, cantidad);
    }
  
}
