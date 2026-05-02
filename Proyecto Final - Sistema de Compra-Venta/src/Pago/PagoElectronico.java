package Pago;

import java.util.Scanner;

import Banco.Banco;
import Banco.Tarjeta;

public class PagoElectronico extends Transaccion {

    //Atributos
    private Tarjeta tarjeta;
    private Banco banco;

    //Metodos
    public PagoElectronico (double monto, Banco banco){
        super(monto);
        this.banco = banco;

    }
    
    public void realizarPago() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Total a pagar: " + getMonto());

        System.out.println("\nIngrese el número de su tarjeta:");
        String numero = scanner.next().trim();

        tarjeta = banco.buscarTarjeta(numero);
        
        if (tarjeta != null) {
            System.out.println("Tarjeta encontrada. ");

              if(tarjeta.retirarDinero(monto)){        
                System.out.println("Pago exitoso.");
                this.estado = Estado.EXITOSO;

             } else {
                System.out.println("Saldo insuficiente.");
                this.estado = Estado.RECHAZADO;
             }

        } else {
            System.out.println("Tarjeta no encontrada. Pago rechazado.");
            this.estado = Estado.RECHAZADO;
        }
      
    }
    
}
