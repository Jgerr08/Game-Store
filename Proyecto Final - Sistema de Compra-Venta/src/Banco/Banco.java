package Banco;

import java.util.ArrayList;

public class Banco {
    private String nombre;
    private ArrayList<Tarjeta> tarjetas;

    public Banco(String nombre) {
        this.nombre = nombre;
        this.tarjetas = new ArrayList<>();
    }

    public void agregarTarjeta(Tarjeta tarjeta) {
        if(tarjeta == null){
            System.out.println("Tarjeta nula.");
            return;
        }

        if(buscarTarjeta(tarjeta.getNumero())!= null){
            System.out.println("La tarjeta ya existe.");
            return;
        }
        if(!tarjeta.validarTarjeta()){
            System.out.println("Tarjeta inválida.");
            return;
        }
        tarjetas.add(tarjeta);
    }

     public Tarjeta buscarTarjeta(String numero) {
        for (Tarjeta tarjeta : tarjetas) {
            if (tarjeta.getNumero().equals(numero)) {
                return tarjeta;
            }
        }
        return null;
    }


    public String getNombre() {
        return nombre;
    }
}
