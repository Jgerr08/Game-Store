package Pago;
import java.util.Scanner;
public class PagoEfectivo extends Transaccion{
    
//Atributos
    private double montoPagado;
    private double cambio;

//Metodos
public PagoEfectivo (double monto) {
    super(monto);

}

public void realizarPago(){
    Scanner scanner = new Scanner(System.in);

    System.out.println("Pago en Efectivo");
    System.out.println("\nTotal a pagar:" + monto);

    double pagado = 0;
    if(pagado < monto){
        System.out.println("Ingresar el monto entregado:");
        pagado = scanner.nextDouble();

    if (pagado < monto){
        System.out.println("El monto es insuficiente. Hace falta una cantidad de: " + (monto -= pagado));
        this.estado = Estado.RECHAZADO;
        return;
        
    }
    }
    
    this.montoPagado = pagado;
    this.cambio = montoPagado - monto;
    this.estado = Estado.EXITOSO;

    System.out.println("Pago realizado de manera exitosa");
    System.out.println("Cambio: " + cambio);

    }

    public double getMontoPagado(){
        return montoPagado;
    }
    public double getCambio(){
        return cambio;
    }


}
