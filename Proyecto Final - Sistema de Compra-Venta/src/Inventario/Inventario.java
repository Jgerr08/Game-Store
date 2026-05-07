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
    public void eliminarJuego(Juego juego){
        juegoDAO.eliminar(juego.getID());
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
    public void modificarPrecio(String id, double nuevoPrecio) {
        juegoDAO.modificarPrecio(id,nuevoPrecio);
    }

    public void mostrarInventario() {

        List<Juego> juegos = listarJuegos();

        if (juegos.isEmpty()) {

            System.out.println("No hay juegos registrados");
            return;
        }

        for (Juego juego : juegos) {

            System.out.println(
                "ID: " + juego.getID() +
                " | " + juego.getTitulo() +
                " | $" + juego.getPrecio() +
                " | Stock: " + juego.getStock()
            );        }
    }
}
