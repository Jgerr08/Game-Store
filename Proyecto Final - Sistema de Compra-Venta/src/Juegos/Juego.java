package Juegos;

public class Juego {
    private String id;
    private String titulo;
    private Consola consola;
    private double precio;
    private int stock;
    private Condicion condicion;

    public Juego(String id, String titulo, Consola consola, double precio, int stock, Condicion condicion) {
        this.id = id;
        this.titulo = titulo;
        this.consola = consola;
        this.precio = precio;
        this.stock = stock;
        this.condicion = condicion;
    }

    public String getID(){
        return id;
    }
    public String getTitulo(){
        return titulo;
    }
    public Consola getConsola(){
        return consola;
    }
    public double getPrecio(){
        return precio;
    }
    public int getStock(){
        return stock;
    }
    public Condicion getCondicion(){
        return condicion;
    }
    
    
}
